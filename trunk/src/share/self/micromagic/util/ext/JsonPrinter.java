
package self.micromagic.util.ext;

import java.io.Writer;
import java.io.IOException;

import self.micromagic.eterna.view.impl.DataPrinterImpl;
import self.micromagic.eterna.view.impl.StringCoderImpl;
import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.util.Utility;

/**
 * ��һ�����������������json�ĸ�ʽ���.
 *
 * @see DataPrinterImpl
 */
public class JsonPrinter
{
   /**
    * ʹ��<code>DataPrinterImpl</code>���ж���->Json�ַ�����ת��.
    */
   private static DataPrinterImpl printer = new DataPrinterImpl(new StringCoderImpl());

   /**
    * json�ַ����������.
    */
   private Writer out;

   /**
    * ����һ��<code>JsonPrinter</code>����.
    *
    * @param out   ת�����json�ַ����������.
    */
   public JsonPrinter(Writer out)
   {
      this.out = out;
   }

   /**
    * �����������ֵ.
    *
    * @param b           ����ֵ
    */
   public JsonPrinter print(boolean b)
         throws IOException
   {
      try
      {
         printer.print(this.out, b);
      }
      catch (ConfigurationException ex)
      {
         throw new RuntimeException(ex);
      }
      return this;
   }

   /**
    * ����ַ�����ֵ.
    *
    * @param c           �ַ�ֵ
    */
   public JsonPrinter print(char c)
         throws IOException
   {
      try
      {
         printer.print(this.out, c);
      }
      catch (ConfigurationException ex)
      {
         throw new RuntimeException(ex);
      }
      return this;
   }

   /**
    * �������ֵ.
    *
    * @param i           ����ֵ
    */
   public JsonPrinter print(int i)
         throws IOException
   {
      try
      {
         printer.print(this.out, i);
      }
      catch (ConfigurationException ex)
      {
         throw new RuntimeException(ex);
      }
      return this;
   }

   /**
    * ���������ֵ.
    *
    * @param l           ������ֵ
    */
   public JsonPrinter print(long l)
         throws IOException
   {
      try
      {
         printer.print(this.out, l);
      }
      catch (ConfigurationException ex)
      {
         throw new RuntimeException(ex);
      }
      return this;
   }

   /**
    * ���������ֵ.
    *
    * @param f           ������ֵ
    */
   public JsonPrinter print(float f)
         throws IOException
   {
      try
      {
         printer.print(this.out, f);
      }
      catch (ConfigurationException ex)
      {
         throw new RuntimeException(ex);
      }
      return this;
   }

   /**
    * ���˫���ȸ�����ֵ.
    *
    * @param d           ˫���ȸ�����ֵ
    */
   public JsonPrinter print(double d)
         throws IOException
   {
      try
      {
         printer.print(this.out, d);
      }
      catch (ConfigurationException ex)
      {
         throw new RuntimeException(ex);
      }
      return this;
   }


   /**
    * ����ַ�������ֵ.
    *
    * @param s           �ַ�������ֵ
    */
   public JsonPrinter print(String s)
         throws IOException
   {
      try
      {
         printer.print(this.out, s);
      }
      catch (ConfigurationException ex)
      {
         throw new RuntimeException(ex);
      }
      return this;
   }

   /**
    * ���һ��Object����.
    *
    * @param value        Ҫ�����Object����
    */
   public JsonPrinter print(Object value)
         throws IOException
   {
      try
      {
         printer.print(this.out, value);
      }
      catch (ConfigurationException ex)
      {
         throw new RuntimeException(ex);
      }
      return this;
   }

   /**
    * ���һ��Object��������.
    *
    * @param values       Ҫ�����Object��������
    */
   public JsonPrinter print(Object[] values)
         throws IOException
   {
      try
      {
         printer.print(this.out, values);
      }
      catch (ConfigurationException ex)
      {
         throw new RuntimeException(ex);
      }
      return this;
   }

   /**
    * ���һ�����з�.
    */
   public JsonPrinter println()
         throws IOException
   {
      this.out.write(Utility.LINE_SEPARATOR);
      return this;
   }

}
