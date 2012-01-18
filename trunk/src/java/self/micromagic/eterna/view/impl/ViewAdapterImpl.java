
package self.micromagic.eterna.view.impl;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.search.SearchAdapter;
import self.micromagic.eterna.search.SearchManager;
import self.micromagic.eterna.share.AbstractGenerator;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.sql.ResultIterator;
import self.micromagic.eterna.sql.ResultMetaData;
import self.micromagic.eterna.sql.ResultRow;
import self.micromagic.eterna.view.BaseManager;
import self.micromagic.eterna.view.Component;
import self.micromagic.eterna.view.Function;
import self.micromagic.eterna.view.StringCoder;
import self.micromagic.eterna.view.ViewAdapter;
import self.micromagic.eterna.view.ViewAdapterGenerator;
import self.micromagic.eterna.view.Resource;
import self.micromagic.util.MemoryChars;
import self.micromagic.util.StringTool;
import self.micromagic.util.Utility;
import self.micromagic.util.container.UnmodifiableIterator;

public class ViewAdapterImpl extends AbstractGenerator
      implements ViewAdapter, ViewAdapterGenerator
{
   private List componentList = new LinkedList();
   private StringCoder stringCoder;
   private String viewGlobalSetting = "";
   private int debug = 0;
   private String width;
   private String height;
   protected String beforeInit;
   protected String initScript;
   protected String dynamicViewRes;

   private ViewAdapterGenerator.ModifiableViewRes viewRes = new ModifiableViewResImpl();

   protected boolean initialized = false;

   private MemoryChars mc = null;

   public void initialize(EternaFactory factory)
         throws ConfigurationException
   {
      if (!this.initialized)
      {
         this.initialized = true;

         this.dynamicViewRes = BaseManager.dealScriptPart(
               this.viewRes, this.dynamicViewRes, BaseManager.GRAMMER_TYPE_NONE, factory);

         Iterator componentItr = this.getComponents();
         while (componentItr.hasNext())
         {
            Component com = (Component) componentItr.next();
            com.initialize(factory, null);
            this.viewRes.addAll(com.getViewRes());
         }
         this.beforeInit = BaseManager.dealScriptPart(
               this.viewRes, this.beforeInit, BaseManager.GRAMMER_TYPE_EXPRESSION, factory);
         this.initScript = BaseManager.dealScriptPart(
               this.viewRes, this.initScript, BaseManager.GRAMMER_TYPE_EXPRESSION, factory);

         this.stringCoder = factory.getStringCoder();
         this.viewGlobalSetting = this.getFactory().getViewGlobalSetting();
         this.viewGlobalSetting = BaseManager.dealScriptPart(
               this.viewRes, this.viewGlobalSetting, BaseManager.GRAMMER_TYPE_JSON, factory);

         // 这里需要新建一个set, 因为执行过程中会改变原来的数据集
         Set typicalSet = new HashSet(this.viewRes.getTypicalComponentNames());
         Set dealedSet = new HashSet();
         int setSize = 0;
         while (typicalSet.size() > setSize)
         {
            setSize = typicalSet.size();
            Iterator typicals = typicalSet.iterator();
            while (typicals.hasNext())
            {
               String name = (String) typicals.next();
               if (dealedSet.contains(name))
               {
                  continue;
               }
               dealedSet.add(name);
               Component com = this.getFactory().getTypicalComponent(name);
               if (com != null)
               {
                  this.viewRes.addAll(com.getViewRes());
               }
               else
               {
                  log.error("Not found typical component [" + name + "] in view ["
                     + this.getName() + "]");
               }
            }
            typicalSet = new HashSet(this.viewRes.getTypicalComponentNames());
         }
      }
   }

   public void setDynamicViewRes(String res)
   {
      this.dynamicViewRes = res;
   }

   public ViewAdapter.ViewRes getViewRes()
   {
      return this.viewRes;
   }

   public int getDebug()
   {
      return this.debug;
   }

   public void setDebug(int debug)
   {
      this.debug = debug;
   }

   public String getWidth()
   {
      return this.width;
   }

   public void setWidth(String width)
   {
      this.width = width;
   }

   public String getHeight()
   {
      return this.height;
   }

   public void setHeight(String height)
   {
      this.height = height;
   }

   public String getBeforeInit()
         throws ConfigurationException
   {
      return this.beforeInit;
   }

   public void setBeforeInit(String condition)
         throws ConfigurationException
   {
      this.beforeInit = condition;
   }

   public String getInitScript()
         throws ConfigurationException
   {
      return this.initScript;
   }

   public void setInitScript(String body)
         throws ConfigurationException
   {
      this.initScript = body;
   }

   public Iterator getComponents()
   {
      return new UnmodifiableIterator(this.componentList.iterator());
   }

   public void addComponent(Component com)
   {
      this.componentList.add(com);
   }

   public void deleteComponent(Component com)
   {
      this.componentList.remove(com);
   }

   public void clearComponents()
   {
      this.componentList.clear();
   }

   public EternaFactory getFactory()
   {
      return (EternaFactory) this.factory;
   }

   public void printEvent(Writer out, AppData data, Component.Event event)
         throws IOException, ConfigurationException
   {
      out.write("{type:");
      out.write("\"");
      out.write(stringCoder.toJsonString(event.getName()));
      out.write("\"");

      if (event.getScriptParam() != null)
      {
         out.write(",param:");
         out.write("\"");
         out.write(stringCoder.toJsonString(event.getScriptParam()));
         out.write("\"");
      }

      String eventBegin = "var tempBak=_eterna.egTemp();try{_eterna.egTemp(event.data.egTemp);var webObj=event.data.webObj;var objConfig=event.data.objConfig;";
      String eventEnd = "}finally{_eterna.egTemp(tempBak);}";
      if (this.getDebug() >= ETERNA_VIEW_DEBUG_BASE)
      {
         out.write(",fn:function(event){");
         out.write("try{");
         out.write("return event.data.eventConfig._fn(event);");
         out.write("}catch (ex){");
         out.write("if (eterna_debug >= ED_FN_CALLED){");
         out.write("eterna_fn_stack.push(new Array(event.data.eventConfig.type, event.data.eventConfig._fn,");
         out.write(" \"event.data\", event.data));");
         out.write("_eterna.printException(ex);");
         out.write("eterna_fn_stack.pop();throw ex;}}");
         out.write("}");
         out.write(",_fn:function(event){");
         out.write(eventBegin);
         out.write(event.getScriptBody());
         out.write(eventEnd);
         out.write("}");
      }
      else
      {
         out.write(",fn:function(event){");
         out.write(eventBegin);
         out.write(event.getScriptBody());
         out.write(eventEnd);
         out.write("}");
      }
      out.write("}");
   }

   public void printFunction(Writer out, AppData data, String key, Function fn)
         throws IOException, ConfigurationException
   {
      if (this.getDebug() >= ETERNA_VIEW_DEBUG_BASE)
      {
         out.write("\"_ef_");
         out.write(this.stringCoder.toJsonString(key));
         out.write("\":");
         out.write("function(");
         out.write(fn.getParam());
         out.write("){");
         out.write(fn.getBody());
         out.write("}");

         out.write(",\"");
         out.write(this.stringCoder.toJsonString(key));
         out.write("\":");
         out.write("function(");
         out.write(fn.getParam());
         out.write("){try{");
         out.write("return eternaData.eFns[\"_ef_");
         out.write(this.stringCoder.toJsonString(key));
         out.write("\"](");
         out.write(fn.getParam());
         out.write(");}catch (ex){");
         out.write("if (eterna_debug >= ED_FN_CALLED){");
         out.write("eterna_fn_stack.push(new Array(\"");
         out.write(this.stringCoder.toJsonString(key));
         out.write("\", eternaData.eFns[\"_ef_");
         out.write(this.stringCoder.toJsonString(key));
         out.write("\"]");
         String[] params = StringTool.separateString(fn.getParam(), ",", true);
         for (int i = 0; i < params.length; i++)
         {
            out.write(", ");
            out.write("\"" + params[i] + "\", " + params[i]);
         }
         out.write("));");
         out.write("_eterna.printException(ex);");
         out.write("eterna_fn_stack.pop();throw ex;}}");
         out.write("}");
      }
      else
      {
         out.write("\"");
         out.write(this.stringCoder.toJsonString(key));
         out.write("\":");
         out.write("function(");
         out.write(fn.getParam());
         out.write("){");
         out.write(fn.getBody());
         out.write("}");
      }
   }

   public void printResource(Writer out, String name, Resource resource)
         throws IOException, ConfigurationException
   {
      out.write("\"");
      out.write(this.stringCoder.toJsonString(name));
      out.write("\":");
      out.write("function(){");
      out.write("var resArray = ");
      this.printIterator(out, resource.getParsedRessource());
      out.write(";return eterna_getResourceValue(resArray, arguments);");
      out.write("}");
   }

   public void printView(Writer out, AppData data)
         throws IOException, ConfigurationException
   {
      String dataType = data.getRequestParameter(DATA_TYPE);
      boolean webData = !DATA_TYPE_ONLYRECORD.equalsIgnoreCase(dataType);

      out.write("{");

      if (webData)
      {
         out.write("global:{");
         out.write(this.getFactory().getViewGlobalSetting());
         out.write("},\n");
      }

      out.write("records:{");
      out.write("root:");
      out.write("\"");
      out.write(this.stringCoder.toJsonString(data.contextRoot));
      out.write("\"");
      out.write(",modelNameTag:");
      out.write("\"");
      out.write(this.stringCoder.toJsonString(this.getFactory().getModelNameTag()));
      out.write("\"");
      if (data.modelName != null)
      {
         out.write(",modelName:");
         out.write("\"");
         out.write(this.stringCoder.toJsonString(data.modelName));
         out.write("\"");
      }
      this.printDataMap(out, data.dataMap);
      out.write("}");

      if (webData)
      {
         data.addSpcialData(VIEW_CACHE, DYNAMIC_VIEW, null);

         if (this.mc == null || this.getDebug() >= ETERNA_VIEW_DEBUG_BASE)
         {
            synchronized (this)
            {
               if (this.mc == null || this.getDebug() >= ETERNA_VIEW_DEBUG_BASE)
               {
                  Writer oldOut = out;
                  if (this.getDebug() < ETERNA_VIEW_DEBUG_BASE)
                  {
                     this.mc = new MemoryChars(1, 128);
                     out = mc.getWriter();
                  }

                  Iterator tmpTypicals = this.viewRes.getTypicalComponentNames().iterator();
                  while (tmpTypicals.hasNext())
                  {
                     String name = (String) tmpTypicals.next();
                     Component com = this.getFactory().getTypicalComponent(name);
                     if (com != null)
                     {
                        data.addSpcialData(ViewAdapter.TYPICAL_COMPONENTS_MAP, name, com);
                     }
                  }

                  out.write(",\nview:[");
                  Iterator itr = this.getComponents();
                  while (itr.hasNext())
                  {
                     Component com = (Component) itr.next();
                     com.print(out, data, this);
                     if (itr.hasNext())
                     {
                        out.write(",");
                     }
                  }
                  out.write("]");

                  if (this.initScript != null)
                  {
                     out.write(",\ninit:");
                     out.write("\"");
                     stringCoder.toJsonString(out, this.initScript);
                     out.write("\"");
                  }

                  if (this.beforeInit != null)
                  {
                     out.write(",beforeInit:");
                     out.write("\"");
                     stringCoder.toJsonString(out, this.beforeInit);
                     out.write("\"");
                  }

                  Map typical = data.getSpcialDataMap(TYPICAL_COMPONENTS_MAP, true);
                  data.setSpcialDataMap(USED_TYPICAL_COMPONENTS, typical);
                  if (typical != null)
                  {
                     out.write(",\ntypical:{");
                     this.printTypical(out, data, typical, null);
                     out.write("}");
                  }

                  Map fnMap = (Map) data.getSpcialData(VIEW_CACHE, DYNAMIC_FUNCTIONS);
                  if (fnMap != null)
                  {
                     BaseManager.putAllFunction(fnMap, this.viewRes.getFunctionMap());
                  }
                  else
                  {
                     fnMap = this.viewRes.getFunctionMap();
                  }
                  Iterator entrys = fnMap.entrySet().iterator();
                  if (fnMap.size() > 0)
                  {
                     out.write(",\neFns:{");
                     boolean hasFunction = false;
                     while (entrys.hasNext())
                     {
                        Map.Entry entry = (Map.Entry) entrys.next();
                        String key = (String) entry.getKey();
                        Function fn = (Function) entry.getValue();
                        if (fn != null)
                        {
                           if (hasFunction)
                           {
                              out.write(",");
                           }
                           else
                           {
                              hasFunction = true;
                           }
                           this.printFunction(out, data, key, fn);
                        }
                     }
                     out.write("}");
                  }

                  Set resourceSet = (Set) data.getSpcialData(VIEW_CACHE, DYNAMIC_RESOURCE_NAMES);
                  if (resourceSet != null)
                  {
                     resourceSet.addAll(this.viewRes.getResourceNames());
                  }
                  else
                  {
                     resourceSet = this.viewRes.getResourceNames();
                  }
                  if (resourceSet.size() > 0)
                  {
                     Iterator resources = resourceSet.iterator();
                     out.write(",\nres:{");
                     boolean hasResource = false;
                     while (resources.hasNext())
                     {
                        String name = (String) resources.next();
                        Resource resource = this.getFactory().getResource(name);
                        if (resource != null)
                        {
                           if (hasResource)
                           {
                              out.write(",");
                           }
                           else
                           {
                              hasResource = true;
                           }
                           this.printResource(out, name, resource);
                        }
                        else
                        {
                           log.error("Not found the resource:[" + name + "].");
                        }
                     }
                     out.write("}");
                  }

                  out = oldOut;
               }
            }
         }

         if (this.getDebug() < ETERNA_VIEW_DEBUG_BASE)
         {
            Utility.copyChars(this.mc.getReader(), out);
         }

         // 如果是动态视图, 则不能缓存
         if ("1".equals(data.getSpcialData(VIEW_CACHE, DYNAMIC_VIEW)))
         {
            this.mc = null;
         }
      }

      out.write("}");
   }

   /**
    * 生成typical控件.
    *
    * @param typicalMap   当前要生成的typical控件
    * @param allMap       所有生成的typical控件
    */
   private void printTypical(Writer out, AppData data, Map typicalMap, Map allMap)
         throws IOException, ConfigurationException
   {
      Map typical = typicalMap;
      if (allMap == null)
      {
         allMap = typical;
      }
      else
      {
         // allMap 不为空，表示是递归进来的，所以要加个","
         out.write(",");
      }

      Iterator itr = typical.entrySet().iterator();
      while (itr.hasNext())
      {
         Map.Entry entry = (Map.Entry) itr.next();
         String key = (String) entry.getKey();
         Component com = (Component) entry.getValue();
         out.write("\"");
         out.write(this.stringCoder.toJsonString(key));
         out.write("\"");
         out.write(":");
         com.print(out, data, this);

         if (itr.hasNext())
         {
            out.write(",");
         }
      }

      Map newTypical = data.getSpcialDataMap(TYPICAL_COMPONENTS_MAP, true);
      data.setSpcialDataMap(USED_TYPICAL_COMPONENTS, allMap);
      if (newTypical != null)
      {
         itr = newTypical.entrySet().iterator();
         while (itr.hasNext())
         {
            Map.Entry entry = (Map.Entry) itr.next();
            if (allMap.containsKey(entry.getKey()))
            {
               itr.remove();
            }
            else
            {
               allMap.put(entry.getKey(), entry.getValue());
            }
         }
         if (newTypical.size() > 0)
         {
            this.printTypical(out, data, newTypical, allMap);
         }
      }
   }

   private void printDataMap(Writer out, Map data)
         throws IOException, ConfigurationException
   {
      Iterator entrys = data.entrySet().iterator();
      while (entrys.hasNext())
      {
         Map.Entry entry = (Map.Entry) entrys.next();
         String key = (String) entry.getKey();
         Object value = entry.getValue();
         if (value != null)
         {
            // 输出dataMap时，需要先输出","，因为前面会有其它数据
            out.write(",\"");
            out.write(this.stringCoder.toJsonString(key));
            out.write("\":");
            this.printObject(out, value);
         }
      }
   }

   private void printMap(Writer out, Map data)
         throws IOException, ConfigurationException
   {
      Iterator entrys = data.entrySet().iterator();
      while (entrys.hasNext())
      {
         Map.Entry entry = (Map.Entry) entrys.next();
         String key = (String) entry.getKey();
         Object value = entry.getValue();
         if (value != null)
         {
            out.write("\"");
            out.write(this.stringCoder.toJsonString(key));
            out.write("\":");
            this.printObject(out, value);
         }
         if (value != null && entrys.hasNext())
         {
            out.write(",");
         }
      }
   }

   private void printObject(Writer out, Object value)
         throws IOException, ConfigurationException
   {
      try
      {
         if (value instanceof ResultRow)
         {
            out.write("{");
            ResultRow row = (ResultRow) value;
            ResultMetaData rmd = row.getResultIterator().getMetaData();
            int count = rmd.getColumnCount();
            boolean firstSetted = false;
            for (int i = 1; i <= count; i++)
            {
               if (rmd.getColumnReader(i).isValid())
               {
                  if (firstSetted)
                  {
                     out.write(",");
                  }
                  firstSetted = true;
                  out.write("\"");
                  out.write(this.stringCoder.toJsonString(rmd.getColumnName(i)));
                  out.write("\":\"");
                  out.write(this.stringCoder.toJsonString(row.getFormated(i)));
                  out.write("\"");
               }
            }
            out.write("}");
         }
         else if (value instanceof SearchManager)
         {
            SearchManager sm = (SearchManager) value;
            Iterator itr = sm.getConditions().iterator();
            boolean hasValue = false;
            out.write("{");
            while (itr.hasNext())
            {
               SearchManager.Condition con = (SearchManager.Condition) itr.next();
               if (con.value != null)
               {
                  if (hasValue)
                  {
                     out.write(",");
                  }
                  hasValue = true;
                  out.write("\"");
                  out.write(this.stringCoder.toJsonString(con.name));
                  out.write("\":\"");
                  out.write(this.stringCoder.toJsonString(con.value));
                  out.write("\"");
               }
            }
            out.write("}");
         }
         else if (value instanceof ResultIterator)
         {
            out.write("{");
            this.printResultIterator(out, (ResultIterator) value);
            out.write("}");
         }
         else if (value instanceof Collection)
         {
            this.printCollection(out, (Collection) value);
         }
         else if (value instanceof SearchAdapter.Result)
         {
            out.write("{");
            SearchAdapter.Result result = (SearchAdapter.Result) value;
            this.printResultIterator(out, result.queryResult);
            out.write(",pageNum:");
            out.write(String.valueOf(result.pageNum));
            out.write(",pageSize:");
            out.write(String.valueOf(result.pageSize));
            out.write(",searchName:");
            out.write("\"");
            out.write(this.stringCoder.toJsonString(result.searchName));
            out.write("\"");
            if (result.queryResult.isRealRecordCountAvailable())
            {
               out.write(",totalCount:");
               out.write(String.valueOf(result.queryResult.getRealRecordCount()));
            }
            if (result.singleOrderName != null)
            {
               out.write(",orderName:");
               out.write("\"");
               out.write(this.stringCoder.toJsonString(result.singleOrderName));
               out.write("\"");
               out.write(",orderDesc:");
               out.write(result.singleOrderDesc ? "1" : "0");
            }
            out.write(",hasNextPage:");
            out.write(result.queryResult.isHasMoreRecord() ? "1" : "0");
            out.write("}");
         }
         else if (value instanceof SearchManager.Attributes)
         {
            out.write("{");
            this.printSearchAttributes(out, (SearchManager.Attributes) value);
            out.write("}");
         }
         else if (value instanceof String)
         {
            out.write("\"");
            out.write(this.stringCoder.toJsonString((String) value));
            out.write("\"");
         }
         else if (value instanceof Number || value instanceof Boolean)
         {
            out.write(value.toString());
         }
         else if (value instanceof Map)
         {
            out.write("{");
            this.printMap(out, (Map) value);
            out.write("}");
         }
         else if (value.getClass().isArray())
         {
            try
            {
               this.printCollection(out, Arrays.asList((Object[]) value));
            }
            catch (ClassCastException cce)
            {
               int length = Array.getLength(value);
               ArrayList arr = new ArrayList(length);
               for (int i = 0; i < length; i++)
               {
                  arr.add(Array.get(value, i));
               }
               this.printIterator(out, arr.iterator());
            }
         }
         else
         {
            out.write("\"");
            out.write(this.stringCoder.toJsonString(value.toString()));
            out.write("\"");
         }
      }
      catch (SQLException ex)
      {
         log.error("SQL error in printObject.", ex);
         throw new ConfigurationException(ex);
      }
   }

   private void printSearchAttributes(Writer out, SearchManager.Attributes sma)
         throws IOException
   {
      out.write("pageNumTag:");
      out.write("\"");
      out.write(this.stringCoder.toJsonString(sma.pageNumTag));
      out.write("\"");
      out.write(",pageSizeTag:");
      out.write("\"");
      out.write(this.stringCoder.toJsonString(sma.pageSizeTag));
      out.write("\"");
      out.write(",querySettingTag:");
      out.write("\"");
      out.write(this.stringCoder.toJsonString(sma.querySettingTag));
      out.write("\"");
      out.write(",queryTypeClear:");
      out.write("\"");
      out.write(this.stringCoder.toJsonString(sma.queryTypeClear));
      out.write("\"");
      out.write(",queryTypeReset:");
      out.write("\"");
      out.write(this.stringCoder.toJsonString(sma.queryTypeReset));
      out.write("\"");
      out.write(",queryTypeTag:");
      out.write("\"");
      out.write(this.stringCoder.toJsonString(sma.queryTypeTag));
      out.write("\"");
   }

   private void printCollection(Writer out, Collection collection)
         throws IOException, ConfigurationException
   {
      if (collection.size() > 0)
      {
         /*
         这里去掉的对第一个元素是否是ResultRow的判断, 因为ResultIterator中
         增加了copy方法, 可以生成副本. 这样就不需要复制到List中了.
         Object obj = collection.iterator().next();
         if (obj instanceof ResultRow)
         {
            ResultRow row = (ResultRow) obj;
            ResultMetaData rmd = row.getResultIterator().getMetaData();
            // 由于结果集对象是在外部加"{}"对，所以对于Collection的结果集
            // 需要在这里加"{}"对。
            out.write("{");
            this.printIterator(out, collection.iterator(), rmd, collection.size());
            out.write("}");
         }
         else
         */
         {
            this.printIterator(out, collection.iterator());
         }
      }
      else
      {
         out.write("[]");
      }
   }

   private void printResultIterator(Writer out, ResultIterator ritr)
         throws IOException, ConfigurationException, SQLException
   {
      this.printIterator(out ,ritr, ritr.getMetaData(), ritr.getRecordCount());
   }

   private void printIterator(Writer out, Iterator itr)
         throws IOException, ConfigurationException
   {
      out.write("[");
      while (itr.hasNext())
      {
         Object value = itr.next();
         if (value != null)
         {
            this.printObject(out, value);
         }
         else
         {
            out.write("null");
         }
         if (itr.hasNext())
         {
            out.write(",");
         }
      }
      out.write("]");
   }

   private void printIterator(Writer out, Iterator itr, ResultMetaData rmd, int recordCount)
         throws IOException, ConfigurationException, SQLException
   {
      int count = rmd.getColumnCount();
      out.write("names:{");
      boolean firstSetted = false;
      for (int i = 1; i <= count; i++)
      {
         if (rmd.getColumnReader(i).isValid())
         {
            if (firstSetted)
            {
               out.write(",");
            }
            firstSetted = true;
            out.write("\"");
            out.write(this.stringCoder.toJsonString(rmd.getColumnName(i)));
            out.write("\":");
            out.write(String.valueOf(i));
         }
      }
      out.write("}");
      out.write(",rowCount:");
      out.write(String.valueOf(recordCount));
      out.write(",rows:[");
      while (itr.hasNext())
      {
         ResultRow row = (ResultRow) itr.next();
         out.write("[");
         for (int i = 1; i <= count; i++)
         {
            if (i > 1)
            {
               out.write(",");
            }
            out.write("\"");
            out.write(this.stringCoder.toJsonString(row.getFormated(i)));
            out.write("\"");
         }
         out.write("]");
         if (itr.hasNext())
         {
            out.write(",");
         }
      }
      out.write("]");
   }

   public ViewAdapter createViewAdapter()
   {
      return this;
   }

   public Object create() throws ConfigurationException
   {
      return this.createViewAdapter();
   }

}
