
package self.micromagic.eterna.view.impl;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import self.micromagic.cg.BeanMethodInfo;
import self.micromagic.cg.BeanTool;
import self.micromagic.cg.ClassGenerator;
import self.micromagic.cg.ClassKeyCache;
import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.search.SearchAdapter;
import self.micromagic.eterna.search.SearchManager;
import self.micromagic.eterna.share.AbstractGenerator;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.share.Tool;
import self.micromagic.eterna.sql.ResultIterator;
import self.micromagic.eterna.sql.ResultMetaData;
import self.micromagic.eterna.sql.ResultRow;
import self.micromagic.eterna.view.DataPrinter;
import self.micromagic.eterna.view.DataPrinterGenerator;
import self.micromagic.eterna.view.StringCoder;

public class DataPrinterImpl extends AbstractGenerator
      implements DataPrinter, DataPrinterGenerator
{
   protected StringCoder stringCoder;

   public DataPrinterImpl()
   {
   }

   public DataPrinterImpl(StringCoder stringCoder)
   {
      this.stringCoder = stringCoder;
   }

   public void initialize(EternaFactory factory)
         throws ConfigurationException
   {
      this.stringCoder = factory.getStringCoder();
   }

   public void printData(Writer out, Map data, boolean hasPreData)
         throws IOException, ConfigurationException
   {
      boolean first = true;
      Iterator entrys = data.entrySet().iterator();
      while (entrys.hasNext())
      {
         Map.Entry entry = (Map.Entry) entrys.next();
         String key = (String) entry.getKey();
         Object value = entry.getValue();
         if (value != null)
         {
            if (hasPreData || !first)
            {
               // 输出data数据集前面会有其它数据或不是第一个数据时，需要先输出","
               out.write(",");
            }
            first = false;
            out.write("\"");
            out.write(this.stringCoder.toJsonString(key));
            out.write("\":");
            this.print(out, value);
         }
      }
   }

   public void print(Writer out, Object value)
         throws IOException, ConfigurationException
   {
      try
      {
         if (value == null)
         {
            out.write("null");
         }
         else if (value instanceof String)
         {
            out.write("\"");
            out.write(this.stringCoder.toJsonString((String) value));
            out.write("\"");
         }
         else if (value instanceof ResultRow)
         {
            this.printResultRow(out, (ResultRow) value);
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
         else if (value instanceof Number || value instanceof Boolean)
         {
            out.write(value.toString());
         }
         else if (value instanceof Map)
         {
            this.printMap(out, (Map) value);
         }
         else if (value.getClass().isArray())
         {
            if (value.getClass().getComponentType().isPrimitive())
            {
               // 如果是基本类型, 需要以反射的方式获取每个元素
               int length = Array.getLength(value);
               out.write("[");
               for (int i = 0; i < length; i++)
               {
                  if (i > 0)
                  {
                     out.write(",");
                  }
                  Object tmpObj = Array.get(value, i);
                  if (tmpObj != null)
                  {
                     this.print(out, tmpObj);
                  }
                  else
                  {
                     out.write("null");
                  }
               }
               out.write("]");
            }
            else
            {
               this.print(out, (Object[]) value);
            }
         }
         else if (value instanceof BeanPrinter)
         {
            ((BeanPrinter) value).print(this, out, value);
         }
         else if (Tool.isBean(value.getClass()))
         {
            BeanPrinter bp = this.getBeanPrinter(value.getClass());
            out.write("{");
            bp.print(this, out, value);
            out.write("}");
         }
         else
         {
            out.write("\"");
            out.write(this.stringCoder.toJsonString(String.valueOf(value)));
            out.write("\"");
         }
      }
      catch (SQLException ex)
      {
         log.error("SQL error in printObject.", ex);
         throw new ConfigurationException(ex);
      }
   }

   public void print(Writer out, Object[] values)
         throws IOException, ConfigurationException
   {
      if (values == null)
      {
         out.write("null");
      }
      else
      {
         out.write("[");
         for (int i = 0; i < values.length; i++)
         {
            if (i > 0)
            {
               out.write(",");
            }
            Object value = values[i];
            if (value != null)
            {
               this.print(out, value);
            }
            else
            {
               out.write("null");
            }
         }
         out.write("]");
      }
   }

   public void printMap(Writer out, Map map)
         throws IOException, ConfigurationException
   {
      out.write("{");
      this.printData(out, map, false);
      out.write("}");
   }

   protected void printSearchAttributes(Writer out, SearchManager.Attributes sma)
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

   protected void printCollection(Writer out, Collection collection)
         throws IOException, ConfigurationException
   {
      if (collection.size() > 0)
      {
         this.printIterator(out, collection.iterator());
      }
      else
      {
         out.write("[]");
      }
   }

   public void printResultRow(Writer out, ResultRow row)
         throws IOException, ConfigurationException, SQLException
   {
      out.write("{");
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

   public void printResultIterator(Writer out, ResultIterator ritr)
         throws IOException, ConfigurationException, SQLException
   {
      ResultMetaData rmd = ritr.getMetaData();
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
      out.write(String.valueOf(ritr.getRecordCount()));
      out.write(",rows:[");
      while (ritr.hasNext())
      {
         ResultRow row = (ResultRow) ritr.next();
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
         if (ritr.hasNext())
         {
            out.write(",");
         }
      }
      out.write("]");
   }

   public void printIterator(Writer out, Iterator itr)
         throws IOException, ConfigurationException
   {
      out.write("[");
      while (itr.hasNext())
      {
         Object value = itr.next();
         if (value != null)
         {
            this.print(out, value);
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


   public void print(Writer out, boolean b)
         throws IOException, ConfigurationException
   {
      out.write(b ? "true" : "false");
   }

   public void print(Writer out, char c)
         throws IOException, ConfigurationException
   {
      out.write("\"");
      out.write(this.stringCoder.toJsonString(String.valueOf(c)));
      out.write("\"");
   }

   public void print(Writer out, int i)
         throws IOException, ConfigurationException
   {
      out.write(String.valueOf(i));
   }

   public void print(Writer out, long l)
         throws IOException, ConfigurationException
   {
      out.write(String.valueOf(l));
   }

   public void print(Writer out, float f)
         throws IOException, ConfigurationException
   {
      out.write(String.valueOf(f));
   }

   public void print(Writer out, double d)
         throws IOException, ConfigurationException
   {
      out.write(String.valueOf(d));
   }

   public void print(Writer out, String s)
         throws IOException, ConfigurationException
   {
      if (s == null)
      {
         out.write("null");
      }
      else
      {
         out.write("\"");
         out.write(this.stringCoder.toJsonString(s));
         out.write("\"");
      }
   }

   public DataPrinter createDataPrinter()
   {
      return this;
   }

   public Object create()
   {
      return this.createDataPrinter();
   }

   /**
    * 存放BeanPrinter的缓存
    */
   private static ClassKeyCache beanPrinterCache = ClassKeyCache.getInstance();

   public BeanPrinter getBeanPrinter(Class beanClass)
   {
      BeanPrinter bp = (BeanPrinter) beanPrinterCache.getProperty(beanClass);
      if (bp == null)
      {
         bp = getBeanPrinter0(beanClass);
      }
      return bp;
   }

   private static synchronized BeanPrinter getBeanPrinter0(Class beanClass)
   {
      BeanPrinter bp = (BeanPrinter) beanPrinterCache.getProperty(beanClass);
      if (bp == null)
      {
         try
         {
            String mh = "public void print(DataPrinter p, Writer out, Object bean)"
                  + " throws IOException, ConfigurationException";
            String ut = "out.write(\"\\\"${name}\\\":\");"
                  + "p.print(out, ${value});";
            String pt = "out.write(\"\\\"${name}\\\":\");"
                  + "p.print(out, ${o_value});";
            String lt = "out.write(\",\");";
            String[] imports = new String[]{
               ClassGenerator.getPackageString(DataPrinter.class),
               ClassGenerator.getPackageString(Writer.class),
               ClassGenerator.getPackageString(ConfigurationException.class),
               ClassGenerator.getPackageString(beanClass)
            };
            bp = (BeanPrinter) Tool.createBeanPrinter(beanClass, BeanPrinter.class, mh,
                  "bean", ut, pt, lt, imports);
            if (bp == null)
            {
               bp = new BeanPrinterImpl(beanClass);
            }
         }
         catch (Throwable ex)
         {
            bp = new BeanPrinterImpl(beanClass);
         }
         beanPrinterCache.setProperty(beanClass, bp);
      }
      return bp;
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

      public void print(DataPrinter p, Writer out, Object bean)
            throws IOException, ConfigurationException
      {
         try
         {
            boolean first = true;
            for (int i = 0; i < this.fields.length; i++)
            {
               if (!first)
               {
                  out.write(",");
               }
               first = false;
               Field f = this.fields[i];
               out.write("\"");
               out.write(f.getName());
               out.write("\":");
               p.print(out, f.get(bean));
            }
            for (int i = 0; i < this.methods.length; i++)
            {
               if (!first)
               {
                  out.write(",");
               }
               first = false;
               Method m = this.methods[i].method;
               out.write("\"");
               out.write(this.methods[i].name);
               out.write("\":");
               p.print(out, m.invoke(bean, new Object[0]));
            }
         }
         catch (Exception ex)
         {
            if (ex instanceof IOException)
            {
               throw (IOException) ex;
            }
            if (ex instanceof ConfigurationException)
            {
               throw (ConfigurationException) ex;
            }
            throw new ConfigurationException(ex);
         }
      }

   }

}
