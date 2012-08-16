
package self.micromagic.eterna.view.impl;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

import self.micromagic.cg.BeanMethodInfo;
import self.micromagic.cg.BeanTool;
import self.micromagic.cg.ClassGenerator;
import self.micromagic.cg.ClassKeyCache;
import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.AbstractGenerator;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.share.Tool;
import self.micromagic.eterna.sql.ResultIterator;
import self.micromagic.eterna.sql.ResultMetaData;
import self.micromagic.eterna.sql.ResultRow;
import self.micromagic.eterna.view.DataPrinter;
import self.micromagic.eterna.view.DataPrinterGenerator;
import self.micromagic.eterna.view.StringCoder;
import self.micromagic.util.FormatTool;

public class DataPrinterImpl extends AbstractGenerator
      implements DataPrinter, DataPrinterGenerator
{
   protected StringCoder stringCoder;
	protected DateFormat dateFormat = FormatTool.dateFullFormat;

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
         Object value = entry.getValue();
         if (value != null)
         {
				Object key = entry.getKey();
         	String keyStr = key == null ? null : key.toString();
            if (hasPreData || !first)
            {
               // 输出data数据集前面会有其它数据或不是第一个数据时，需要先输出","
               out.write(',');
            }
				else
				{
            	first = false;
				}
            out.write('"');
            this.stringCoder.toJsonString(out, keyStr);
            out.write("\":");
            this.print(out, value);
         }
      }
   }

   public void print(Writer out, Object value)
         throws IOException, ConfigurationException
   {
		if (value == null)
		{
			out.write("null");
		}
		else if (value instanceof String)
		{
			out.write('"');
			this.stringCoder.toJsonStringWithoutCheck(out, (String) value);
			out.write('"');
		}
		else if (value instanceof Number || value instanceof Boolean)
		{
			out.write(value.toString());
		}
		else if (value instanceof Map)
		{
			this.printMap(out, (Map) value);
		}
		else if (value instanceof Collection)
		{
			this.printCollection(out, (Collection) value);
		}
		else if (value instanceof ResultRow)
		{
			try
			{
				this.printResultRow(out, (ResultRow) value);
			}
			catch (SQLException ex)
			{
				throw new ConfigurationException(ex);
			}
		}
		else if (value instanceof ResultIterator)
		{
			out.write('{');
			try
			{
				this.printResultIterator(out, (ResultIterator) value);
			}
			catch (SQLException ex)
			{
				throw new ConfigurationException(ex);
			}
			out.write('}');
		}
		else if (value instanceof Iterator)
		{
			this.printIterator(out, (Iterator) value);
		}
		else if (value instanceof BeanPrinter)
		{
			((BeanPrinter) value).print(this, out, value);
		}
		else if (value instanceof Enumeration)
		{
			this.printEnumeration(out, (Enumeration) value);
		}
		else if (value instanceof Object[])
		{
			this.print(out, (Object[]) value);
		}
		else if (value instanceof int[])
		{
			this.print(out, (int[]) value);
		}
		else if (value instanceof double[])
		{
			this.print(out, (double[]) value);
		}
		else if (value instanceof Date)
		{
			this.print(out, this.dateFormat.format((Date) value));
		}
		else if (value instanceof Calendar)
		{
			Date d = ((Calendar) value).getTime();
			this.print(out, this.dateFormat.format(d));
		}
		else if (Tool.isBean(value.getClass()))
		{
			BeanPrinter bp = this.getBeanPrinter(value.getClass());
			out.write('{');
			bp.print(this, out, value);
			out.write('}');
		}
		else if (value instanceof boolean[])
		{
			this.print(out, (boolean[]) value);
		}
		else if (value instanceof long[])
		{
			this.print(out, (long[]) value);
		}
		else if (value instanceof char[])
		{
			this.print(out, (char[]) value);
		}
		else if (value instanceof float[])
		{
			this.print(out, (float[]) value);
		}
		else if (value instanceof byte[])
		{
			this.print(out, (byte[]) value);
		}
		else if (value instanceof short[])
		{
			this.print(out, (short[]) value);
		}
		else
		{
			out.write('"');
			this.stringCoder.toJsonStringWithoutCheck(out, String.valueOf(value));
			out.write('"');
		}
   }

   public void print(Writer out, Object[] values)
         throws IOException, ConfigurationException
   {
		out.write('[');
		if (values.length > 0)
		{
			this.print(out, values[0]);
		}
		for (int i = 1; i < values.length; i++)
		{
			out.write(',');
			this.print(out, values[i]);
		}
		out.write(']');
   }

   public void printMap(Writer out, Map map)
         throws IOException, ConfigurationException
   {
      out.write('{');
      this.printData(out, map, false);
      out.write('}');
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
      out.write('{');
      ResultMetaData rmd = row.getResultIterator().getMetaData();
      int count = rmd.getColumnCount();
      boolean firstSetted = false;
      for (int i = 1; i <= count; i++)
      {
         if (rmd.getColumnReader(i).isValid())
         {
            if (firstSetted)
            {
               out.write(',');
            }
            firstSetted = true;
            out.write('"');
            this.stringCoder.toJsonString(out, rmd.getColumnName(i));
            out.write("\":\"");
            this.stringCoder.toJsonString(out, row.getFormated(i));
            out.write('"');
         }
      }
      out.write('}');
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
               out.write(',');
            }
				else
				{
            	firstSetted = true;
				}
            out.write('"');
            this.stringCoder.toJsonString(out, rmd.getColumnName(i));
            out.write("\":");
            out.write(String.valueOf(i));
         }
      }
      out.write('}');
      out.write(",rowCount:");
      out.write(String.valueOf(ritr.getRecordCount()));
      out.write(",rows:[");
		boolean nextRow = false;
      while (ritr.hasNext())
      {
			if (nextRow)
			{
            out.write(',');
			}
			else
			{
				nextRow = true;
			}
         ResultRow row = (ResultRow) ritr.next();
         out.write('[');
         for (int i = 1; i <= count; i++)
         {
            if (i > 1)
            {
               out.write(',');
            }
            out.write('"');
            this.stringCoder.toJsonString(out, row.getFormated(i));
            out.write('"');
         }
         out.write(']');
      }
      out.write(']');
   }

	public void printEnumeration(Writer out, Enumeration e)
         throws IOException, ConfigurationException
	{
      out.write('[');
		if (e.hasMoreElements())
		{
			this.print(out, e.nextElement());
		}
      while (e.hasMoreElements())
      {
			out.write(',');
			this.print(out, e.nextElement());
      }
      out.write(']');
	}

   public void printIterator(Writer out, Iterator itr)
         throws IOException, ConfigurationException
   {
      out.write('[');
		if (itr.hasNext())
		{
			this.print(out, itr.next());
		}
      while (itr.hasNext())
      {
			out.write(',');
			this.print(out, itr.next());
      }
      out.write(']');
   }

   public void print(Writer out, boolean b)
         throws IOException, ConfigurationException
   {
      out.write(b ? "true" : "false");
   }

   public void print(Writer out, boolean[] values)
         throws IOException, ConfigurationException
   {
		out.write('[');
		if (values.length > 0)
		{
      	out.write(values[0] ? "true" : "false");
		}
		for (int i = 1; i < values.length; i++)
		{
			out.write(',');
      	out.write(values[i] ? "true" : "false");
		}
		out.write(']');
   }

   public void print(Writer out, char c)
         throws IOException, ConfigurationException
   {
      out.write('"');
		this.stringCoder.toJsonString(out, c);
      out.write('"');
   }

   public void print(Writer out, char[] values)
         throws IOException, ConfigurationException
   {
		out.write('[');
		if (values.length > 0)
		{
			out.write('"');
			this.stringCoder.toJsonString(out, values[0]);
			out.write('"');
		}
		for (int i = 1; i < values.length; i++)
		{
			out.write(",\"");
			this.stringCoder.toJsonString(out, values[i]);
			out.write('"');
		}
		out.write(']');
   }

   public void print(Writer out, int i)
         throws IOException, ConfigurationException
   {
      out.write(Integer.toString(i, 10));
   }

   public void print(Writer out, int[] values)
         throws IOException, ConfigurationException
   {
		out.write('[');
		if (values.length > 0)
		{
      	out.write(Integer.toString(values[0], 10));
		}
		for (int i = 1; i < values.length; i++)
		{
			out.write(',');
      	out.write(Integer.toString(values[i], 10));
		}
		out.write(']');
   }

   public void print(Writer out, byte[] values)
         throws IOException, ConfigurationException
   {
		out.write('[');
		if (values.length > 0)
		{
      	out.write(Integer.toString(values[0], 10));
		}
		for (int i = 1; i < values.length; i++)
		{
			out.write(',');
      	out.write(Integer.toString(values[i], 10));
		}
		out.write(']');
   }

   public void print(Writer out, short[] values)
         throws IOException, ConfigurationException
   {
		out.write('[');
		if (values.length > 0)
		{
      	out.write(Integer.toString(values[0], 10));
		}
		for (int i = 1; i < values.length; i++)
		{
			out.write(',');
      	out.write(Integer.toString(values[i], 10));
		}
		out.write(']');
   }

   public void print(Writer out, long l)
         throws IOException, ConfigurationException
   {
      out.write(Long.toString(l, 10));
   }

   public void print(Writer out, long[] values)
         throws IOException, ConfigurationException
   {
		out.write('[');
		if (values.length > 0)
		{
			out.write(Long.toString(values[0], 10));
		}
		for (int i = 1; i < values.length; i++)
		{
			out.write(',');
			out.write(Long.toString(values[i], 10));
		}
		out.write(']');
   }

   public void print(Writer out, float f)
         throws IOException, ConfigurationException
   {
      out.write(Float.toString(f));
   }

   public void print(Writer out, float[] values)
         throws IOException, ConfigurationException
   {
		out.write('[');
		if (values.length > 0)
		{
			out.write(Float.toString(values[0]));
		}
		for (int i = 1; i < values.length; i++)
		{
			out.write(',');
			out.write(Float.toString(values[i]));
		}
		out.write(']');
   }

   public void print(Writer out, double d)
         throws IOException, ConfigurationException
   {
      out.write(Double.toString(d));
   }

   public void print(Writer out, double[] values)
         throws IOException, ConfigurationException
   {
		out.write('[');
		if (values.length > 0)
		{
			out.write(Double.toString(values[0]));
		}
		for (int i = 1; i < values.length; i++)
		{
			out.write(',');
			out.write(Double.toString(values[i]));
		}
		out.write(']');
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
         out.write('"');
         this.stringCoder.toJsonStringWithoutCheck(out, s);
         out.write('"');
      }
   }

	public void printObjectBegin(Writer out)
			throws IOException
	{
		out.write('{');
	}

	public void printObjectEnd(Writer out)
			throws IOException
	{
		out.write('}');
	}

	public void printPair(Writer out, String key, boolean value, boolean first)
			throws IOException, ConfigurationException
	{
		if (!first)
		{
			out.write(',');
		}
		this.print(out, key);
		out.write(':');
		this.print(out, value);
	}

	public void printPair(Writer out, String key, char value, boolean first)
			throws IOException, ConfigurationException
	{
		if (!first)
		{
			out.write(',');
		}
		this.print(out, key);
		out.write(':');
		this.print(out, value);
	}

	public void printPair(Writer out, String key, int value, boolean first)
			throws IOException, ConfigurationException
	{
		if (!first)
		{
			out.write(',');
		}
		this.print(out, key);
		out.write(':');
		this.print(out, value);
	}

	public void printPair(Writer out, String key, long value, boolean first)
			throws IOException, ConfigurationException
	{
		if (!first)
		{
			out.write(',');
		}
		this.print(out, key);
		out.write(':');
		this.print(out, value);
	}

	public void printPair(Writer out, String key, float value, boolean first)
			throws IOException, ConfigurationException
	{
		if (!first)
		{
			out.write(',');
		}
		this.print(out, key);
		out.write(':');
		this.print(out, value);
	}

	public void printPair(Writer out, String key, double value, boolean first)
			throws IOException, ConfigurationException
	{
		if (!first)
		{
			out.write(',');
		}
		this.print(out, key);
		out.write(':');
		this.print(out, value);
	}

	public void printPair(Writer out, String key, String value, boolean first)
			throws IOException, ConfigurationException
	{
		if (!first)
		{
			out.write(',');
		}
		this.print(out, key);
		out.write(':');
		this.print(out, value);
	}

	public void printPair(Writer out, String key, Object value, boolean first)
			throws IOException, ConfigurationException
	{
		if (!first)
		{
			out.write(',');
		}
		this.print(out, key);
		out.write(':');
		this.print(out, value);
	}

	public void setDateFormat(DateFormat format)
	{
		if (format == null)
		{
			throw new NullPointerException("The param format is null.");
		}
		this.dateFormat = format;
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
            String ut = "p.printPair(out, \"${name}\", ${value}, ${first});";
            String pt = "p.printPair(out, \"${name}\", ${o_value}, ${first});";
            String lt = "";
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
                  out.write(',');
               }
               first = false;
               Field f = this.fields[i];
               out.write('"');
               out.write(f.getName());
               out.write("\":");
               p.print(out, f.get(bean));
            }
            for (int i = 0; i < this.methods.length; i++)
            {
               BeanMethodInfo m = this.methods[i];
               if (m.method != null)
               {
                  if (!first)
                  {
                     out.write(',');
                  }
                  first = false;
                  out.write('"');
                  out.write(m.name);
                  out.write("\":");
                  p.print(out, m.method.invoke(bean, new Object[0]));
               }
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
