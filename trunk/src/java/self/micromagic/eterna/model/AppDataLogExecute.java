
package self.micromagic.eterna.model;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.ReferenceMap;
import org.dom4j.Element;
import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.model.impl.AbstractExecute;
import self.micromagic.eterna.search.SearchAdapter;
import self.micromagic.eterna.search.SearchManager;
import self.micromagic.eterna.share.Generator;
import self.micromagic.eterna.share.SessionCache;
import self.micromagic.eterna.share.Tool;
import self.micromagic.eterna.share.TypeManager;
import self.micromagic.eterna.sql.ResultIterator;
import self.micromagic.eterna.sql.ResultMetaData;
import self.micromagic.eterna.sql.ResultRow;

public class AppDataLogExecute extends AbstractExecute
      implements Execute, Generator
{
   public String getExecuteType() throws ConfigurationException
   {
      return "appDataLog";
   }

   public ModelExport execute(AppData data, Connection conn)
         throws ConfigurationException, SQLException, IOException
   {
      if (AppData.getAppLogType() != 0)
      {
         printAppData(data);
      }
      return null;
   }

   public static void printAppData(AppData data)
   {
      if (AppData.getAppLogType() == 0)
      {
         return;
      }
      try
      {
         new Printer().printAppData(data);
      }
      catch (Throwable ex)
      {
         log.error("Error in print app data.", ex);
      }
   }

   public static void printObject(Element parent, Object value)
   {
      try
      {
         new Printer().printObject(parent, value);
      }
      catch (Throwable ex)
      {
         log.error("Error in print object:" + value, ex);
         parent.addAttribute("error", ex.getMessage());
      }
   }

   /**
    * 设置app运行日志记录方式
    */
   public static void setAppLogType(String type)
   {
      try
      {
         AppData.setAppLogType(Integer.parseInt(type));
      }
      catch (Exception ex)
      {
         log.error("Error in set app log type.", ex);
      }
   }


   /**
    * BeanPrinter生成
    */
   private static Map beanPrinterMap = new ReferenceMap(ReferenceMap.WEAK, ReferenceMap.HARD);
   private static BeanPrinter getBeanPrinter(Class beanClass)
   {
      BeanPrinter bp = (BeanPrinter) beanPrinterMap.get(beanClass);
      if (bp == null)
      {
         synchronized (beanPrinterMap)
         {
            // 再获取一次, 如果其他线程已处理了, 这里就不用做了
            bp = (BeanPrinter) beanPrinterMap.get(beanClass);
            if (bp == null)
            {
               try
               {
                  String mh = "public void print(AppDataLogExecute$Printer p,"
                        + " Element parent, Object value) throws Exception";
                  String ut = "Element nowNode = parent.addElement(\"${type}\");"
                        + "nowNode.addAttribute(\"name\", \"${name}\");"
                        + "p.printObject(nowNode, ${value});";
                  String pt = "Element nowNode = parent.addElement(\"${type}\");"
                        + "nowNode.addAttribute(\"name\", \"${name}\");"
                        + "nowNode.addAttribute(\"type\", \"${primitive}\");"
                        + "nowNode.addAttribute(\"value\", ${value});";
                  String[] imports = new String[]{
                     Tool.getPackageString(AppDataLogExecute.class),
                     Tool.getPackageString(Element.class),
                     Tool.getPackageString(beanClass)
                  };
                  bp = (BeanPrinter) Tool.createBeanProcesser(beanClass, BeanPrinter.class, mh,
                        "value", ut, pt, "", imports, Tool.BEAN_PROCESSER_TYPE_R);
                  if (bp == null)
                  {
                     bp = new BeanPrinterImpl(beanClass);
                  }
               }
               catch (Throwable ex)
               {
                  bp = new BeanPrinterImpl(beanClass);
               }
            }
            beanPrinterMap.put(beanClass, bp);
         }
      }
      return bp;
   }

   public static interface BeanPrinter
   {
      public void print(Printer p, Element parent, Object value) throws Exception;
   }

   private static class BeanPrinterImpl
         implements BeanPrinter
   {
      private Field[] fields;
      private Tool.BeanMethodInfo[] methods;

      public BeanPrinterImpl(Class c)
      {
         this.fields = Tool.getBeanFields(c);
         this.methods = Tool.getBeanReadMethods(c);
      }

      public void print(Printer p, Element parent, Object value)
            throws Exception
      {
         for (int i = 0; i < this.fields.length; i++)
         {
            Field f = this.fields[i];
            Element fNode = parent.addElement("field");
            fNode.addAttribute("name", f.getName());
            p.printObject(fNode, f.get(value));
         }
         for (int i = 0; i < this.methods.length; i++)
         {
            Tool.BeanMethodInfo m = this.methods[i];
            Element mNode = parent.addElement("method");
            mNode.addAttribute("name", m.name);
            p.printObject(mNode, m.method.invoke(value, new Object[0]));
         }
      }

   }

   public static class Printer
   {
      private ArrayList cStack = new ArrayList();
      private int idIndex = 1;

      public void printAppData(AppData data)
            throws Exception
      {
         Element node = data.getCurrentNode();
         if (node == null)
         {
            return;
         }
         Element appData = node.addElement("appData");
         for (int i = 0; i < data.maps.length; i++)
         {
            Element mapNode = appData.addElement(AppData.MAP_NAMES[i]);
            this.printObject(mapNode, data.maps[i]);
         }
         Element tmpNode = appData.addElement("cache");
         this.printObject(tmpNode, data.caches);
         tmpNode = appData.addElement("stack");
         this.printObject(tmpNode, data.stack);
      }

      private void printMap(Element parent, Map map)
            throws Exception
      {
         parent.addAttribute("count", String.valueOf(map.entrySet().size()));
         Iterator entrys = map.entrySet().iterator();
         while (entrys.hasNext())
         {
            Map.Entry entry = (Map.Entry) entrys.next();
            String key = (String) entry.getKey();
            Object value = entry.getValue();
            Element vNode = parent.addElement("value");
            vNode.addAttribute("key", key);
            this.printObject(vNode, value);
         }
      }

      private void printCollection(Element parent, Collection collection)
            throws Exception
      {
         parent.addAttribute("count", String.valueOf(collection.size()));
         if (collection.size() > 0)
         {
            int index = 0;
            Iterator itr = collection.iterator();
            while (itr.hasNext())
            {
               Element vNode = parent.addElement("value");
               vNode.addAttribute("index", String.valueOf(index));
               this.printObject(vNode, itr.next());
               index++;
            }
         }
      }

      private void printResultRow(Element parent, ResultRow row)
            throws Exception
      {
         parent.addAttribute("type", "ResultRow");
         ResultMetaData rmd = row.getResultIterator().getMetaData();
         int count = rmd.getColumnCount();
         parent.addAttribute("columnCount", String.valueOf(count));
         for (int i = 1; i <= count; i++)
         {
            if (rmd.getColumnReader(i).isValid())
            {
               Element vNode = parent.addElement("value");
               vNode.addAttribute("columnName", rmd.getColumnName(i));
               vNode.addAttribute("type", TypeManager.getTypeName(rmd.getColumnReader(i).getType()));
               vNode.addAttribute("value", row.getFormated(i));
            }
         }
      }

      private void printResultIterator(Element parent, ResultIterator ritr)
            throws Exception
      {
         parent.addAttribute("type", "ResultIterator");
         int rowCount = ritr.getRecordCount();
         parent.addAttribute("rowCount", String.valueOf(rowCount));
         int printCount = 5;
         if (rowCount < printCount)
         {
            printCount = rowCount;
         }
         for (int i = 0; i < printCount; i++)
         {
            ResultRow row = ritr.preFetch(i + 1);
            if (row != null)
            {
               Element vNode = parent.addElement("value");
               vNode.addAttribute("index", String.valueOf(i + 1));
               printResultRow(vNode, row);
            }
         }
         if (rowCount > printCount)
         {
            Element vNode = parent.addElement("value");
            vNode.addAttribute("type", "more");
            vNode.addAttribute("value", "...");
         }
      }

      public void printObject(Element parent, Object value)
            throws Exception
      {
         if (value == null)
         {
            parent.addAttribute("type", "null");
         }
         else if (value instanceof ResultRow)
         {
            printResultRow(parent, (ResultRow) value);
         }
         else if (value instanceof SearchManager)
         {
            parent.addAttribute("type", "SearchManager");
            SearchManager sm = (SearchManager) value;
            Iterator itr = sm.getConditions().iterator();
            while (itr.hasNext())
            {
               SearchManager.Condition con = (SearchManager.Condition) itr.next();
               Element vNode = parent.addElement("value");
               vNode.addAttribute("conditionName", con.name);
               if (con.value == null)
               {
                  vNode.addAttribute("type", "null");
               }
               else
               {
                  vNode.addAttribute("type", "String");
                  vNode.addAttribute("value", con.value);
               }
            }
         }
         else if (value instanceof ResultIterator)
         {
            this.printResultIterator(parent, (ResultIterator) value);
         }
         else if (value instanceof Collection)
         {
            if (value instanceof List)
            {
               parent.addAttribute("type", "List");
            }
            else if (value instanceof Set)
            {
               parent.addAttribute("type", "Set");
            }
            else
            {
               parent.addAttribute("type", "Collection");
            }
            if (this.checkAndPush(parent, value))
            {
               this.printCollection(parent, (Collection) value);
               this.pop();
            }
         }
         else if (value instanceof SearchAdapter.Result)
         {
            parent.addAttribute("type", "SearchAdapter.Result");
            SearchAdapter.Result result = (SearchAdapter.Result) value;
            parent.addAttribute("pageNum", String.valueOf(result.pageNum));
            parent.addAttribute("pageSize", String.valueOf(result.pageSize));
            parent.addAttribute("searchName", result.searchName);
            if (result.queryResult.isRealRecordCountAvailable())
            {
               parent.addAttribute("totalCount", String.valueOf(result.queryResult.getRealRecordCount()));
            }
            if (result.singleOrderName != null)
            {
               parent.addAttribute("orderName", result.singleOrderName);
               parent.addAttribute("orderDesc", String.valueOf(result.singleOrderDesc));
            }
            parent.addAttribute("hasNextPage", String.valueOf(result.queryResult.isHasMoreRecord()));
            Element vNode = parent.addElement("value");
            printResultIterator(vNode, result.queryResult);
         }
         else if (value instanceof SearchManager.Attributes)
         {
            parent.addAttribute("type", "SearchManager.Attributes");
            SearchManager.Attributes sma = (SearchManager.Attributes) value;
            parent.addAttribute("pageNumTag", sma.pageNumTag);
            parent.addAttribute("pageSizeTag", sma.pageSizeTag);
            parent.addAttribute("querySettingTag", sma.querySettingTag);
            parent.addAttribute("queryTypeClear", sma.queryTypeClear);
            parent.addAttribute("queryTypeReset", sma.queryTypeReset);
            parent.addAttribute("queryTypeTag", sma.queryTypeTag);
         }
         else if (value instanceof String)
         {
            parent.addAttribute("type", "String");
            parent.addAttribute("value", (String) value);
         }
         else if (value instanceof Number)
         {
            parent.addAttribute("type", "Number");
            parent.addAttribute("value", String.valueOf(value));
         }
         else if (value instanceof Map)
         {
            parent.addAttribute("type", "Map");
            if (this.checkAndPush(parent, value))
            {
               this.printMap(parent, (Map) value);
               this.pop();
            }
         }
         else if (value instanceof SessionCache.Property)
         {
            printObject(parent, ((SessionCache.Property) value).getValue());
         }
         else if (value.getClass().isArray())
         {
            parent.addAttribute("type", "Array");
            if (this.checkAndPush(parent, value))
            {
               try
               {
                  this.printCollection(parent, Arrays.asList((Object[]) value));
               }
               catch (ClassCastException cce)
               {
                  int length = Array.getLength(value);
                  ArrayList arr = new ArrayList(length);
                  for (int i = 0; i < length; i++)
                  {
                     arr.add(Array.get(value, i));
                  }
                  this.printCollection(parent, arr);
               }
               this.pop();
            }
         }
         else
         {
            if (Tool.isBean(value.getClass()))
            {
               parent.addAttribute("type", "bean:" + value.getClass().getName());
               if (this.checkAndPush(parent, value))
               {
                  this.printBean(parent, value);
                  this.pop();
               }
            }
            else
            {
               parent.addAttribute("type", "class:" + value.getClass().getName());
               parent.addAttribute("value", value.toString());
            }
         }
      }

      private void printBean(Element parent, Object value)
            throws Exception
      {
         Class c = value.getClass();
         BeanPrinter bp = getBeanPrinter(c);
         bp.print(this, parent, value);
      }

      private boolean checkAndPush(Element parent, Object value)
      {
         for (int i = this.cStack.size() - 1; i >= 0; i--)
         {
            Object[] tmp = (Object[]) this.cStack.get(i);
            if (tmp[0] == value)
            {
               Element sameE = (Element) tmp[1];
               String id = sameE.attributeValue("containerId");
               if (id == null)
               {
                  id = "c_" + this.idIndex;
                  this.idIndex++;
                  sameE.addAttribute("containerId", id);
               }
               parent.addAttribute("recursion", "true");
               parent.addAttribute("refId", id);
               return false;
            }
         }
         this.cStack.add(new Object[]{value, parent});
         return true;
      }

      private void pop()
      {
         this.cStack.remove(this.cStack.size() - 1);
      }

   }

}
