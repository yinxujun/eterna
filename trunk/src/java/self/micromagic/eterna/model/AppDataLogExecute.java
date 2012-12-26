
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
import java.util.Enumeration;
import java.util.Date;
import java.util.Calendar;

import org.dom4j.Element;
import self.micromagic.cg.BeanMethodInfo;
import self.micromagic.cg.BeanTool;
import self.micromagic.cg.ClassGenerator;
import self.micromagic.cg.ClassKeyCache;
import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.model.impl.AbstractExecute;
import self.micromagic.eterna.search.SearchAdapter;
import self.micromagic.eterna.search.SearchManager;
import self.micromagic.eterna.share.Generator;
import self.micromagic.eterna.share.Tool;
import self.micromagic.eterna.share.TypeManager;
import self.micromagic.eterna.sql.ResultIterator;
import self.micromagic.eterna.sql.ResultMetaData;
import self.micromagic.eterna.sql.ResultRow;
import self.micromagic.util.container.SessionCache;
import self.micromagic.util.container.PreFetchIterator;
import self.micromagic.util.container.ThreadCache;
import self.micromagic.util.FormatTool;
import self.micromagic.util.BooleanRef;

/**
 * @author micromagic@sina.com
 */
public class AppDataLogExecute extends AbstractExecute
      implements Execute, Generator
{
   public String getExecuteType()
			throws ConfigurationException
   {
      return "appDataLog";
   }

   public ModelExport execute(AppData data, Connection conn)
         throws ConfigurationException, SQLException, IOException
   {
		printAppData(data);
      return null;
   }

	/**
	 * ���ڼ�¼��Ϣ��ӡ�ߵĹ�����.
	 */
	private static PrinterCreater creater = new PrinterCreater();

	/**
	 * �������ڼ�¼��Ϣ��ӡ�ߵĹ�����.
	 */
	public static void setPrinterCreater(PrinterCreater c)
	{
		creater = c;
	}

	/**
	 * ���AppData�����е���Ϣ.
	 */
   public static void printAppData(AppData data)
   {
      if (data.getLogType() == 0)
      {
         return;
      }
      try
      {
         creater.createPrinter().printAppData(data);
      }
      catch (Throwable ex)
      {
         log.error("Error in print app data.", ex);
      }
   }

	/**
	 * �������е���Ϣ��ӵ����ڵ���.
	 *
	 * @param parent   �������Ϣ�ĸ��ڵ�
	 * @param value    ������Ҫ�����Ϣ�Ķ���
	 */
   public static void printObject(Element parent, Object value)
   {
      try
      {
         creater.createPrinter().printObject(parent, value);
      }
      catch (Throwable ex)
      {
         log.error("Error in print object:" + value, ex);
         parent.addAttribute("error", ex.getMessage());
      }
   }

   /**
    * ����app������־��¼��ʽ
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
    * BeanPrinter����
    */
   private static ClassKeyCache beanPrinterMap = ClassKeyCache.getInstance();
   private static BeanPrinter getBeanPrinter(Class beanClass)
   {
      BeanPrinter bp = (BeanPrinter) beanPrinterMap.getProperty(beanClass);
      if (bp == null)
      {
         bp = getBeanPrinter0(beanClass);
      }
      return bp;
   }
   private static synchronized BeanPrinter getBeanPrinter0(Class beanClass)
   {
      BeanPrinter bp = (BeanPrinter) beanPrinterMap.getProperty(beanClass);
      if (bp == null)
      {
         try
         {
            String mh = "public void print(" + ClassGenerator.getClassName(Printer.class)
                  + " p," + " Element parent, Object value) throws Exception";
            String ut = "p.printObject(parent.addElement(\"${type}\")"
                  + ".addAttribute(\"name\", \"${name}\")"
                  + ", ${value});";
            String pt = "parent.addElement(\"${type}\")"
                  + ".addAttribute(\"name\", \"${name}\")"
                  + ".addAttribute(\"type\", \"${primitive}\")"
                  + ".addAttribute(\"value\", ${value});";
            String[] imports = new String[]{
               ClassGenerator.getPackageString(AppDataLogExecute.class),
               ClassGenerator.getPackageString(Element.class),
               ClassGenerator.getPackageString(beanClass)
            };
            bp = (BeanPrinter) Tool.createBeanPrinter(beanClass, BeanPrinter.class, mh,
                  "value", ut, pt, "", imports);
            if (bp == null)
            {
               bp = new BeanPrinterImpl(beanClass);
            }
         }
         catch (Throwable ex)
         {
            bp = new BeanPrinterImpl(beanClass);
         }
         beanPrinterMap.setProperty(beanClass, bp);
      }
      return bp;
   }

   public interface BeanPrinter
   {
      public void print(Printer p, Element parent, Object value) throws Exception;

   }

   private static class BeanPrinterImpl
         implements BeanPrinter
   {
      private Field[] fields;
      private BeanMethodInfo[] methods;

      public BeanPrinterImpl(Class c)
      {
         this.fields = BeanTool.getBeanFields(c);
         this.methods = BeanTool.getBeanReadMethods(c);
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
            BeanMethodInfo m = this.methods[i];
            if (m.method != null)
            {
               Element mNode = parent.addElement("method");
               mNode.addAttribute("name", m.name);
               p.printObject(mNode, m.method.invoke(value, new Object[0]));
            }
         }
      }

   }

	/**
	 * ��־��Ϣ��ӡ�߷������̻߳����еı�ǩ��.
	 */
	private static final String PRINTER_CACHE_FLAG = "app.data.log.printer";

   /**
	 * ��¼��־��Ϣ��ӡ�ߵĴ�����.
	 */
	public static class PrinterCreater
	{
      public Printer createPrinter()
		{
			ThreadCache cache = ThreadCache.getInstance();
			Printer p = (Printer) cache.getProperty(PRINTER_CACHE_FLAG);
			if (p == null)
			{
				p = new Printer();
				cache.setProperty(PRINTER_CACHE_FLAG, p);
			}
			return p;
		}

	}

   /**
	 * ��¼��־��Ϣ�Ĵ�ӡ��.
	 */
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
            String key = String.valueOf(entry.getKey());
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

		private void printIterator(Element parent, PreFetchIterator itr)
				throws Exception
		{
			int index = 1;
			BooleanRef hasNext = new BooleanRef();
			Object obj = itr.prefetch(index, hasNext);
			while (hasNext.value)
			{
				Element vNode = parent.addElement("value");
				vNode.addAttribute("index", String.valueOf(index - 1));
				this.printObject(vNode, obj);
				obj = itr.prefetch(++index, hasNext);
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
         else if (value instanceof Boolean)
         {
            parent.addAttribute("type", "Boolean");
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
			else if (value instanceof Iterator)
			{
				parent.addAttribute("type", "Iterator");
				// ֻ������ΪPreFetchIteratorʱ������Ԥȡֵ�ķ�ʽ��¼������Ὣ�α��Ƶ����
            if (value instanceof PreFetchIterator)
            {
					if (this.checkAndPush(parent, value))
					{
						this.printIterator(parent, (PreFetchIterator) value);
						this.pop();
					}
            }
				else
				{
					parent.addAttribute("msg", "Can not read!");
				}
			}
			else if (value instanceof Enumeration)
			{
				// Enumeration������ʾ�Ļ��Ὣ�α��Ƶ����, ��������ֻ�ܼ�¼����
				parent.addAttribute("type", "Enumeration").addAttribute("msg", "Can not read!");
			}
			else if (value instanceof Date)
			{
            parent.addAttribute("type", "Date");
            parent.addAttribute("value", FormatTool.dateFullFormat.format((Date) value));
			}
			else if (value instanceof Calendar)
			{
            parent.addAttribute("type", "Calendar");
				Date d = ((Calendar) value).getTime();
            parent.addAttribute("value", FormatTool.dateFullFormat.format(d));
			}
         else if (value instanceof BeanPrinter)
         {
            ((BeanPrinter) value).print(this, parent, value);
         }
         else if (Tool.isBean(value.getClass()))
         {
            parent.addAttribute("type", "bean:" + ClassGenerator.getClassName(value.getClass()));
            if (this.checkAndPush(parent, value))
            {
               this.printBean(parent, value);
               this.pop();
            }
         }
			else if (value instanceof Map.Entry)
			{
				Map.Entry entry = (Map.Entry) value;
            String tKey = String.valueOf(entry.getKey());
            Object tValue = entry.getValue();
            parent.addAttribute("type", "Entry");
            Element vNode = parent.addElement("value");
            vNode.addAttribute("key", tKey);
            this.printObject(vNode, tValue);
			}
         else if (value.getClass().isArray())
         {
            parent.addAttribute("type", "Array");
            if (this.checkAndPush(parent, value))
            {
               if (value.getClass().getComponentType().isPrimitive())
               {
                  // ����ǻ�������, ��Ҫ�Է���ķ�ʽ��ȡÿ��Ԫ��
                  int length = Array.getLength(value);
                  ArrayList arr = new ArrayList(length);
                  for (int i = 0; i < length; i++)
                  {
                     arr.add(Array.get(value, i));
                  }
                  this.printCollection(parent, arr);
               }
               else
               {
                  this.printCollection(parent, Arrays.asList((Object[]) value));
               }
               this.pop();
            }
         }
         else
         {
            parent.addAttribute("type", "class:" + ClassGenerator.getClassName(value.getClass()));
            parent.addAttribute("value", value.toString());
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
