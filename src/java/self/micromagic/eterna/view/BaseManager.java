
package self.micromagic.eterna.view;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.digester.FactoryManager;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.search.ConditionProperty;
import self.micromagic.eterna.search.SearchAdapter;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.sql.ResultReader;
import self.micromagic.eterna.sql.ResultReaderManager;
import self.micromagic.grammer.GrammerElement;
import self.micromagic.grammer.GrammerManager;
import self.micromagic.grammer.ParserData;
import self.micromagic.util.StringTool;
import self.micromagic.util.Utility;

public class BaseManager
{
   public final static Log log = Utility.createLog("eterna");

   public static final int GRAMMER_TYPE_NONE = 0;
   public static final int GRAMMER_TYPE_JSON = 1;
   public static final int GRAMMER_TYPE_EXPRESSION = 2;

   private static final String[] PLUS_NAMES = {
      "ef", "eternaData.eFns", "eternaFunction", "eternaData.eFns",
      "typical", "eternaData.typical", "res", "eternaData.res",
      "data", "eternaData.records", "dataV", "eternaData.records", "tmpData", "records", "tmpDataV", "records",
      "efV", "eternaData.eFns", "typicalV", "eternaData.typical", "resV", "eternaData.res",
      "global", "eternaData.global", "globalV", "eternaData.global"
   };
   private static final int PLUS_GCELL_COUNT = 5;

   public static final String TYPICAL_NAME = "$typical";
   public static final String TYPICAL_SAME_AS_NAME = "$sameAsName";

   public static final String DATA_SRC = "dataSrc";
   public static final String INIT_PARAM = "initParam";
   public static final String NEW_ROW = "newRow";
   public static final String REQUIRED = "required";
   public static final String CELL_SIZE = "cellSize";
   public static final String CONTAINER_PARAM = "containerParam";
   public static final String TITLE_PARAM = "titleParam";
   public static final String BEFORE_INIT = "beforeInit";
   public static final String INIT_SCRIPT = "initScript";

   public static final String ETERNA_INITIALIZED_FLAG = "eterna_initialized";
   private static Set definedNameSet = new HashSet();
   private static int eternaId = 1;
   private static GrammerManager grammerManager;

   static
   {
      definedNameSet.add("value");
      definedNameSet.add(DATA_SRC);
      definedNameSet.add(INIT_PARAM);
      definedNameSet.add(NEW_ROW);
      definedNameSet.add(REQUIRED);
      definedNameSet.add(CELL_SIZE);
      definedNameSet.add(CONTAINER_PARAM);
      definedNameSet.add(TITLE_PARAM);
      definedNameSet.add(BEFORE_INIT);
      definedNameSet.add(INIT_SCRIPT);
      definedNameSet.add(ResultReader.INPUT_TYPE_FLG);
      try
      {
         grammerManager = new GrammerManager();
         grammerManager.init(BaseManager.class.getClassLoader().getResource(
               "self/micromagic/eterna/view/grammer.xml").openStream());
      }
      catch (Exception ex)
      {
         log.error("Error in create grammerManager.", ex);
      }
   }

   public static int createEternaId()
   {
      return 0xffffff & eternaId++;
   }

   public static String addParentScript(String script, String parentScript)
   {
      if (script == null)
      {
         return parentScript;
      }
      if (parentScript == null)
      {
         parentScript = "";
      }
      int index = script.indexOf(Replacement.PARENT_SCRIPT);
      if (index == -1)
      {
         return script;
      }
      StringBuffer buf = new StringBuffer(script.length() + 64);
      while (index != -1)
      {
         buf.append(script.substring(0, index));
         buf.append(parentScript);
         script = script.substring(index + Replacement.PARENT_SCRIPT.length());
         index = script.indexOf(Replacement.PARENT_SCRIPT);
      }
      buf.append(script);
      // 这里的返回内容不用做任何处理, 因为在后面的dealScriptPart方法中还会做处理
      return buf.toString();
   }

   public static String createTypicalComponentName(AppData data, Component com)
         throws ConfigurationException
   {
      boolean hasOld = false;
      // 先从typical列表和used列表中查找对应名称的控件
      Component oldCom = queryTypicalComponent(data, com.getName());
      if (oldCom == com)
      {
         return com.getName();
      }
      else if (oldCom != null)
      {
         hasOld = true;
      }
      // 如果存在同名的控件, 则放入列表的名称要加上后缀
      String idName;
      if (hasOld)
      {
         idName = com.getName() + "_T" + Integer.toString(System.identityHashCode(com), 32);
         Component tmpCom = queryTypicalComponent(data, idName);
         int idIndex = 0;
         String tmpName = idName;
         while (tmpCom != com)
         {
            if (tmpCom == null)
            {
               data.addSpcialData(ViewAdapter.TYPICAL_COMPONENTS_MAP, idName, com);
               break;
            }
            // 某些存储地址比较大的情况下, identityHashCode可能会重复, 所以再添加顺序编号
            idIndex++;
            idName = tmpName + "_" + idIndex;
            tmpCom = queryTypicalComponent(data, idName);
         }
      }
      else
      {
         idName = com.getName();
         data.addSpcialData(ViewAdapter.TYPICAL_COMPONENTS_MAP, idName, com);
      }
      return idName;
   }

   private static Component queryTypicalComponent(AppData data, String name)
   {
      Component tmp = (Component) data.getSpcialData(ViewAdapter.TYPICAL_COMPONENTS_MAP, name);
      if (tmp == null)
      {
         tmp = (Component) data.getSpcialData(ViewAdapter.USED_TYPICAL_COMPONENTS, name);
      }
      return tmp;
   }

   /**
    * 添加一个动态资源的名称
    */
   public static void addDynamicResourceName(String name)
   {
      if (name != null)
      {
         AppData data = AppData.getCurrentData();
         Set resourceNames = (Set) data.getSpcialData(ViewAdapter.VIEW_CACHE, ViewAdapter.DYNAMIC_RESOURCE_NAMES);
         if (resourceNames == null)
         {
            resourceNames = new HashSet();
            data.addSpcialData(ViewAdapter.VIEW_CACHE, ViewAdapter.DYNAMIC_RESOURCE_NAMES, resourceNames);
         }
      }
   }

   /**
    * 添加一组动态方法.
    */
   public static void addDynamicFunction(Map fnMap)
   {
      if (fnMap == null)
      {
         return;
      }
      if (fnMap.size() > 0)
      {
         AppData data = AppData.getCurrentData();
         Map functions = (Map) data.getSpcialData(ViewAdapter.VIEW_CACHE, ViewAdapter.DYNAMIC_FUNCTIONS);
         if (functions == null)
         {
            functions = new HashMap();
            data.addSpcialData(ViewAdapter.VIEW_CACHE, ViewAdapter.DYNAMIC_FUNCTIONS, functions);
         }
         putAllFunction(functions, fnMap);
      }
   }

   /**
    * 向方法的map中添加一组方法.
    */
   public static void putAllFunction(Map functionMap, Map putMap)
   {
      if (putMap != null)
      {
         Iterator entrys = putMap.entrySet().iterator();
         while (entrys.hasNext())
         {
            Map.Entry entry = (Map.Entry) entrys.next();
            Function oldFn = (Function) functionMap.get(entry.getKey());
            if (oldFn != null && oldFn != entry.getValue())
            {
               log.error("Duplicate function name:[" + entry.getKey() + "] when add it.");
            }
            if (oldFn == null)
            {
               functionMap.put(entry.getKey(), entry.getValue());
            }
         }
      }
   }

   /**
    * 解析资源文本.
    */
   public static List parseResourceText(String text)
         throws ConfigurationException
   {
      GrammerElement ge = grammerManager.getGrammerElement("resource_parser");
      ParserData pd = new ParserData(text);
      try
      {
         if (!ge.verify(pd))
         {
            throw new ConfigurationException("Parse resource error:" + text
                  + "\n[maxBuf:" + pd.getMaxErrorBuffer() + "].");
         }
         return pd.getGrammerCellLst();
      }
      catch (Exception ex)
      {
         log.error("Error in parse resource.", ex);
         throw new ConfigurationException("Parse resource error:" + text + "\n[msg:"
               + ex.getMessage() + "].");
      }
   }

   /**
    * 处理代码中的注释, 替换代码中的扩展标签等.
    */
   public static String dealScriptPart(ViewAdapterGenerator.ModifiableViewRes viewRes, String script,
         int grammerType, EternaFactory factory)
         throws ConfigurationException
   {
      if (script == null)
      {
         return null;
      }
      return StringTool.intern(checkGrammmer(viewRes, script, grammerType, factory), true);
   }

   private static String checkGrammmer(ViewAdapterGenerator.ModifiableViewRes viewRes, String script,
         int grammerType, EternaFactory factory)
         throws ConfigurationException
   {
      GrammerElement ge;
      if (!FactoryManager.isCheckGrammer() || grammerType == GRAMMER_TYPE_NONE)
      {
         ge = grammerManager.getGrammerElement("expression_checker_onlyPlus");
      }
      else
      {
         ge = grammerManager.getGrammerElement(
               grammerType == GRAMMER_TYPE_JSON ? "json_part" : "expression_checker");
      }
      ParserData pd = new ParserData(script);
      try
      {
         if (!ge.verify(pd))
         {
            throw new ConfigurationException("Grammer error:" + script
                  + "\n[maxBuf:" + pd.getMaxErrorBuffer() + "].");
         }
         StringBuffer buf = new StringBuffer(script.length());
         parseGrammerCell(viewRes, pd.getGrammerCellLst(), buf, factory);
         if (log.isDebugEnabled())
         {
            if (buf.length() < script.length())
            {
               log.debug("buf:\n" + buf + "\n-----------------------\nscript:\n" + script);
            }
         }
         return buf.toString();
      }
      catch (Exception ex)
      {
         log.error("Error in check grammer.", ex);
         throw new ConfigurationException("Grammer error:" + script + "\n[msg:" + ex.getMessage() + "].");
      }
   }

   private static void parseGrammerCell(ViewAdapterGenerator.ModifiableViewRes viewRes, List gclist,
         StringBuffer buf, EternaFactory factory)
         throws ConfigurationException
   {
      if (gclist == null)
      {
         return;
      }
      Iterator itr = gclist.iterator();
      while (itr.hasNext())
      {
         ParserData.GrammerCell cell = (ParserData.GrammerCell) itr.next();
         int type = cell.grammerElement.getType();
         if ("plus".equals(cell.grammerElement.getName()))
         {
            boolean validPlusName = false;
            ParserData.GrammerCell[] plusCells = new ParserData.GrammerCell[PLUS_GCELL_COUNT];
            cell.subCells.toArray(plusCells);
            for (int i = 0; i < PLUS_NAMES.length; i += 2)
            {
               String plusName = PLUS_NAMES[i];
               if (plusName.equals(plusCells[1].textBuf))
               {
                  String tmpName = plusCells[3].textBuf;
                  if (tmpName.length() > 0)
                  {
                     if (i < 4)
                     {
                        // 前两个表示使用的是静态方法调用, 需要注册此方法
                        tmpName = viewRes.addFunction(factory.getFunction(tmpName));
                     }
                     else if (i < 6)
                     {
                        // 第三个表示typical控件, 要添加此名称
                        viewRes.addTypicalComponentNames(tmpName);
                     }
                     else if (i < 8)
                     {
                        // 第四个表示resource, 要添加此名称
                        viewRes.addResourceNames(tmpName);
                     }
                  }
                  buf.append(PLUS_NAMES[i + 1]);
                  if (tmpName.length() > 0)
                  {
                     if (plusName.charAt(plusName.length() - 1) == 'V')
                     {
                        buf.append("[").append(tmpName).append("]");
                     }
                     else
                     {
                        buf.append(factory.getStringCoder().parseJsonRefName(tmpName));
                     }
                  }
                  validPlusName = true;
                  break;
               }
            }
            if (validPlusName)
            {
               continue;
            }
            else
            {
               // 这段现在执行不到, 因为非法的名称不会解析为plus
               log.error("Invalid plus name:" + cell.textBuf);
            }
         }
         if (type == GrammerElement.TYPE_NOTE)
         {
            if (buf.length() > 0 && buf.charAt(buf.length() - 1) > ' ')
            {
               buf.append(' ');
            }
            continue;
         }
         if (cell.subCells != null)
         {
            parseGrammerCell(viewRes, cell.subCells, buf, factory);
         }
         else
         {
            buf.append(cell.textBuf);
         }
      }
   }


   private StringCoder stringCoder;
   List items = new LinkedList();

   public BaseManager(EternaFactory factory)
         throws ConfigurationException
   {
      this.stringCoder = factory.getStringCoder();
   }

   public int getCount()
   {
      return this.items.size();
   }

   public List getItems()
   {
      return this.items;
   }

   public void setItems(ResultReaderManager readerManager)
         throws ConfigurationException
   {
      Iterator itr = readerManager.getReaderList().iterator();
      while (itr.hasNext())
      {
         ResultReader temp = (ResultReader) itr.next();
         if (temp.isVisible())
         {
            this.items.add(new ReaderItem(temp));
         }
      }
   }

   public void setItems(SearchAdapter search)
         throws ConfigurationException
   {
      int count = search.getConditionPropertyCount();
      for (int i = 0; i < count; i++)
      {
         ConditionProperty temp = search.getConditionProperty(i);
         if (temp.isVisible())
         {
            this.items.add(new PropertyItem(temp));
         }
      }
   }

   private static int[] parseCellSize(String cellSizeStr)
   {
      if (cellSizeStr != null)
      {
         int index = cellSizeStr.indexOf(',');
         try
         {
            return new int[]{
               Integer.parseInt(cellSizeStr.substring(0, index)),
               Integer.parseInt(cellSizeStr.substring(index + 1))
            };
         }
         catch (Exception ex)
         {
            log.warn("Error cell size string:[" + cellSizeStr + "].", ex);
            return null;
         }
      }
      return null;
   }

   public interface Item
   {
      public String getName() throws ConfigurationException;

      public String getDataSrc() throws ConfigurationException;

      String getCaption() throws ConfigurationException;

      int getWidth() throws ConfigurationException;

      int[] getCellSize() throws ConfigurationException;

      int getType() throws ConfigurationException;

      boolean isNewRow() throws ConfigurationException;

      boolean isRequired() throws ConfigurationException;

      String getInputType() throws ConfigurationException;

      String getInitParam() throws ConfigurationException;

      String getContainerParam() throws ConfigurationException;

      String getTitleParam() throws ConfigurationException;

      String getBeforeInit() throws ConfigurationException;

      String getInitScript() throws ConfigurationException;

      boolean isVisible() throws ConfigurationException;

   }

   abstract class AbstractItem
   {
      private String dataSrc;
      private boolean newRow;
      private boolean required;
      private String containerParam;
      private String titleParam;
      private String beforeInit;
      private String initScript;
      private int[] cellSize;
      protected String initParam;

      protected void initItem()
            throws ConfigurationException
      {
         this.dataSrc = this.getAttribute(DATA_SRC);
         this.beforeInit = this.getAttribute(BEFORE_INIT);
         this.initScript = this.getAttribute(INIT_SCRIPT);
         this.containerParam = this.getAttribute(CONTAINER_PARAM);
         this.titleParam = this.getAttribute(TITLE_PARAM);
         this.cellSize = parseCellSize(this.getAttribute(CELL_SIZE));
         this.newRow = "true".equals(this.getAttribute(NEW_ROW));
         this.required = "true".equals(this.getAttribute(REQUIRED));
      }

      protected abstract String getAttribute(String name) throws ConfigurationException;

      public String getDataSrc()
            throws ConfigurationException
      {
         return this.dataSrc;
      }

      public boolean isNewRow()
            throws ConfigurationException
      {
         return this.newRow;
      }

      public boolean isRequired()
            throws ConfigurationException
      {
         return this.required;
      }

      public int[] getCellSize()
            throws ConfigurationException
      {
         return this.cellSize;
      }

      public String getInitParam()
      {
         return this.initParam;
      }

      public String getContainerParam()
      {
         return this.containerParam;
      }

      public String getTitleParam()
      {
         return this.titleParam;
      }

      public String getBeforeInit()
      {
         return this.beforeInit;
      }

      public String getInitScript()
      {
         return this.initScript;
      }

   }

   class ReaderItem extends AbstractItem
         implements Item
   {
      private ResultReader reader;
      private String inputType;

      public ReaderItem(ResultReader reader)
            throws ConfigurationException
      {
         this.reader = reader;

         this.initItem();
         this.inputType = (String) this.reader.getAttribute(ResultReader.INPUT_TYPE_FLG);
         String tmp = (String) this.reader.getAttribute(INIT_PARAM);
         if (tmp != null)
         {
            this.initParam = tmp;
         }
         else
         {
            String[] names = reader.getAttributeNames();
            if (names != null && names.length > 0)
            {
               StringBuffer buf = new StringBuffer();
               for (int i = 0; i < names.length; i++)
               {
                  String name = names[i];
                  if (name != null && name.startsWith("print."))
                  {
                     continue;
                  }
                  if (!definedNameSet.contains(name))
                  {
                     if (buf.length() > 0)
                     {
                        buf.append(",");
                     }
                     buf.append(name).append(":").append("\"");
                     buf.append(stringCoder.toJsonString((String) reader.getAttribute(name)));
                     buf.append("\"");
                  }
               }
               if (buf.length() > 0)
               {
                  this.initParam = StringTool.intern(buf.toString(), true);
               }
            }
         }
      }

      protected String getAttribute(String name)
            throws ConfigurationException
      {
         return (String) this.reader.getAttribute(name);
      }

      public String getName()
            throws ConfigurationException
      {
         return this.reader.getName();
      }

      public String getCaption()
            throws ConfigurationException
      {
         return this.reader.getCaption();
      }

      public int getWidth()
            throws ConfigurationException
      {
         return this.reader.getWidth();
      }

      public int getType()
            throws ConfigurationException
      {
         return this.reader.getType();
      }

      public String getInputType()
            throws ConfigurationException
      {
         return this.inputType;
      }

      public boolean isVisible()
            throws ConfigurationException
      {
         return this.reader.isVisible();
      }

   }

   class PropertyItem extends AbstractItem
         implements Item
   {
      private ConditionProperty property;

      public PropertyItem(ConditionProperty property)
            throws ConfigurationException
      {
         this.property = property;

         this.initItem();
         String tmp = this.property.getAttribute(INIT_PARAM);
         if (tmp != null)
         {
            this.initParam = tmp;
         }
         else
         {
            String[] names = property.getAttributeNames();
            if (names != null && names.length > 0)
            {
               StringBuffer buf = new StringBuffer();
               for (int i = 0; i < names.length; i++)
               {
                  String name = names[i];
                  if (!definedNameSet.contains(name))
                  {
                     if (buf.length() > 0)
                     {
                        buf.append(",");
                     }
                     buf.append(name).append(":").append("\"");
                     buf.append(stringCoder.toJsonString(property.getAttribute(name)));
                     buf.append("\"");
                  }
               }
               if (buf.length() > 0)
               {
                  this.initParam = StringTool.intern(buf.toString(), true);
               }
            }
         }
      }

      protected String getAttribute(String name)
            throws ConfigurationException
      {
         return this.property.getAttribute(name);
      }

      public String getName()
            throws ConfigurationException
      {
         return this.property.getName();
      }

      public String getCaption()
            throws ConfigurationException
      {
         return this.property.getColumnCaption();
      }

      public int getWidth()
      {
         return -1;
      }

      public int getType()
            throws ConfigurationException
      {
         return this.property.getColumnType();
      }

      public String getInputType()
            throws ConfigurationException
      {
         return this.property.getConditionInputType();
      }

      public boolean isVisible()
            throws ConfigurationException
      {
         return this.property.isVisible();
      }

   }

}
