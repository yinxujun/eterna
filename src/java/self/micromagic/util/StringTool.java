
package self.micromagic.util;

import java.io.UnsupportedEncodingException;
import java.io.PrintStream;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * <code>StringTool</code>����ʵ��һЩ���õ��ַ�������. <p>
 *
 * @author  micromagic
 * @version 1.0, 2002-10-18
 */
public class StringTool
{
   /**
    * �Ƿ���Ҫʹ��QuickStringAppend.
    * �����Ҫ����������ļ�������:
    * eterna.use.quickStringAppend=true
    */
   public static final String USE_QUICK_STRING_APPEND = "eterna.use.quickStringAppend";

   public static final int MAX_INTERN_SIZE = 1024 * 8;

   /**
    * ���ַ�������intern����. <p>
    * ���������ַ���Ϊnull, ��ֱ�ӷ���null�����������ַ�����
    * �ȳ���������, �򲻽���intern����
    *
    * @param str           Ҫintern������ַ���
    * @param newOnBeyond   �����������Ƿ�Ҫ���¹����ַ���
    * @return              ��������ַ���
    */
   public static String intern(String str, boolean newOnBeyond)
   {
      /*
      ��һЩ���ڱ�����ַ�����������ַ�����, �����ɽ�ʡ30%���ϵ��ڴ�ռ�
      ������ڳ���8k���ַ���, ��������newOnBeyondʱ, �������������µ��ַ���
      ��Ϊ��StringBuffer���ɵ��ַ����������˷ѵĿռ�, ���ֱ�ӳ��ڱ���Ļ�
      ��Щ�ռ�Ͳ��ᱻ�ͷ�, ���᳤��ռ���ڴ�
      */
      if (str == null)
      {
         return null;
      }
      return str.length() > MAX_INTERN_SIZE ?
            (newOnBeyond ? new String(str) : str) : str.intern();
   }

   /**
    * ���ַ�������intern����. <p>
    * ���������ַ���Ϊnull, ��ֱ�ӷ���null�����������ַ�����
    * �ȳ���������, �򲻽���intern����
    *
    * @param str    Ҫintern������ַ���
    * @return       ��������ַ���
    */
   public static String intern(String str)
   {
      return intern(str, false);
   }

   /**
    * �ж��ַ����Ƿ�Ϊ��. <p>
    * �մ�<code>""</code>�Ϳն���<code>null</code>������<code>true</code>, ����
    * ����򷵻�<code>false</code>.
    *
    * @param str    ���жϵ��ַ���
    * @return       �Ƿ�Ϊ��
    */
   public static boolean isEmpty(String str)
   {
      return str == null || str.length() == 0;
   }

   /**
    * ����ָ���ķָ���<code>delimiter</code>�ָ�һ���ַ���
    * <code>str</code>. <p>
    * ����������ַ���<code>str</code>Ϊ<code>null</code>���մ�
    * <code>""</code>���򷵻�<code>null</code>��ֻ�����ָ���
    * <code>delimiter</code>���򷵻س���Ϊ0���ַ������顣���������
    * �ַ���<code>str</code>���Էָ�������������֮�У��򷵻�һ��
    * �ַ������飬ÿ����Ԫ����һ�����ָ��������ַ�����
    * <p>
    * ���磺
    * <blockquote><pre>
    * StringProcessor.separateString("һ,��,��", ",")
    *         ���� {"һ", "��", "��"}
    * StringProcessor.separateString("��ã���Һã��Һܺá�", "����")
    *         ���� {"���", "��Һ�", "�Һܺá�"}
    * StringProcessor.separateString(null, ", \t\r\n")
    *         ���� null
    * StringProcessor.separateString(", , , , ", ", \n")
    *         ���� {}
    * </pre></blockquote>
    *
    * @param str         Ҫ���зָ���ַ���
    * @param delimiter   �ָ�������
    * @return  �ָ����ַ�������ɵ�����
    */
   public static String[] separateString(String str, String delimiter)
   {
      return separateString(str, delimiter, false);
   }


   /**
    * ����ָ���ķָ���<code>delimiter</code>�ָ�һ���ַ���
    * <code>str</code>. <p>
    *
    * @param str         Ҫ���зָ���ַ���
    * @param delimiter   �ָ�������
    * @param trim        �Ƿ�Ҫ�Էָ������ÿ���ַ�������ȥ���ո���
    * @return  �ָ����ַ�������ɵ�����
    */
   public static String[] separateString(String str, String delimiter, boolean trim)
   {
      if (str == null)
      {
         return null;
      }
      int count = str.length();
      if (count == 0)
      {
         return new String[0];
      }
      if (isEmpty(delimiter))
      {
         delimiter = " \t\n\r\f";
      }

      List list = new ArrayList();
      int i = 0;
      int begin = 0;
      boolean notMatch = false;
      if (delimiter.length() == 1)
      {
         // ����һ���ַ�ʱ���ַ��Ƚ����ж�
         char c = delimiter.charAt(0);
         while (i < count)
         {
            if (str.charAt(i) == c)
            {
               if (notMatch)
               {
                  list.add(trim ? str.substring(begin, i).trim() : str.substring(begin, i));
                  notMatch = false;
               }
               begin = ++i;
               continue;
            }
            notMatch = true;
            i++;
         }
      }
      else
      {
         // �ж���ַ�ʱ���ַ����İ����ַ����ж�
         while (i < count)
         {
            if (delimiter.indexOf(str.charAt(i)) >= 0)
            {
               if (notMatch)
               {
                  list.add(trim ? str.substring(begin, i).trim() : str.substring(begin, i));
                  notMatch = false;
               }
               begin = ++i;
               continue;
            }
            notMatch = true;
            i++;
         }
      }
      if (notMatch)
      {
         list.add(trim ? str.substring(begin, i).trim() : str.substring(begin, i));
      }

      return (String[]) list.toArray(new String[list.size()]);
   }

   /*
   ԭ���ɵ�ʵ��
   public static String[] separateString(String str, String delimiter, boolean trim)
   {
      if (str == null)
      {
         return null;
      }

      StringTokenizer st = new StringTokenizer(str, delimiter);
      int count = st.countTokens();
      if (count == 0)
      {
         return new String[0];
      }

      String[] bolck = new String[count];
      for (int i = 0; i < count; i++)
      {
         bolck[i] = st.nextToken();
      }
      if (trim)
      {
         for (int i = 0; i < count; i++)
         {
            bolck[i] = bolck[i].trim();
         }
      }
      return bolck;
   }
   */

   /**
    * ��<code>src</code>�ַ����е�����<code>oldStr</code>�滻Ϊ
    * <code>newStr</code>. <p>
    * ���<code>oldStr</code>û����ԭ�ַ����г���, �򷵻�ԭ�ַ���.
    *
    * @param src           ԭ�ַ���
    * @param oldStr        Ҫ�滻���Ӵ�
    * @return              �滻����ַ���
    */
   public static String replaceAll(String src, String oldStr, String newStr)
   {
      if (isEmpty(src) || isEmpty(oldStr))
      {
         return src;
      }

      if (newStr == null)
      {
         newStr = "";
      }
      int begin = 0;
      int end = src.indexOf(oldStr);
      if (end == -1)
      {
         return src;
      }

      int oldStrLength = oldStr.length();
      int plusSize = newStr.length() - oldStrLength;
      plusSize = plusSize <= 0 ? 0 : plusSize * 8 + 16;
      StringAppender result = createStringAppender(src.length() + plusSize);
      do
      {
         result.append(src.substring(begin, end)).append(newStr);
         begin = end + oldStrLength;
         end = src.indexOf(oldStr, begin);
      } while (end != -1);
      result.append(src.substring(begin));

      return result.toString();
   }

   /**
    * ���ַ�������<code>arr</code>�����ӷ�<code>link</code>���ӳ�һ���ַ���. <p>
    *
    * @param arr    Ҫ���ӵ��ַ�������
    * @param link   ���ӷ�
    * @return       �����ӷ����Ӻ���ַ���
    */
   public static String linkStringArr(String[] arr, String link)
   {
      if (arr == null || arr.length == 0)
      {
         return "";
      }
      if (arr.length == 1)
      {
         return arr[0];
      }
      link = link == null ? "" : link;
      StringAppender buf = StringTool.createStringAppender(arr.length * (link.length() + 16));
      for (int i = 0; i < arr.length; i++)
      {
         if (i > 0)
         {
            buf.append(link);
         }
         buf.append(arr[i]);
      }
      return buf.toString();
   }

   /**
    * ���ַ�������һ���ĸ�ʽת����Map. <p>
    *
    * @param str              Ҫת����Map���ַ���
    * @param itemDelimiter    MapԪ�صķָ�������
    * @param kvDelimiter      key��value�ķָ���
    * @return          ת�����Map����
    */
   public static Map string2Map(String str, String itemDelimiter, char kvDelimiter)
   {
      return string2Map(str, itemDelimiter, kvDelimiter, true, true, null, null);
   }

   /**
    * ���ַ�������һ���ĸ�ʽת����Map. <p>
    *
    * @param str              Ҫת����Map���ַ���
    * @param itemDelimiter    MapԪ�صķָ�������
    * @param kvDelimiter      key��value�ķָ���
    * @param trimItem         �Ƿ�Ҫ��ÿ��Ԫ�ؽ���trim
    * @param needResolve      �Ƿ�Ҫ�����ı���"${...}"�Ķ�̬����
    * @param resolveRes       ����̬�����ǰ󶨵���Դ
    * @param result           ��ת���Ľ�������Map��
    * @return          ת�����Map����
    */
   public static Map string2Map(String str, String itemDelimiter, char kvDelimiter,
         boolean trimItem, boolean needResolve, Map resolveRes, Map result)
   {
      if (str == null)
      {
         return null;
      }
      if (result == null)
      {
         result = new HashMap();
      }
      if (needResolve)
      {
         str = Utility.resolveDynamicPropnames(str, resolveRes);
      }
      String[] arr = StringTool.separateString(str, itemDelimiter, trimItem);
      for (int i = 0; i < arr.length; i++)
      {
         int index = arr[i].indexOf(kvDelimiter);
         if (index != -1)
         {
            String k = arr[i].substring(0, index);
            String v = arr[i].substring(index + 1);
            result.put(trimItem ? k.trim() : k, trimItem ? v.trim() : v);
         }
         else if (arr[i].length() > 0)
         {
            if (trimItem)
            {
               String trimStr = arr[i].trim();
               if (trimStr.length() > 0)
               {
                  result.put(trimStr, "");
               }
            }
            else
            {
               result.put(arr[i], "");
            }
         }
      }
      return result;
   }

   /**
    * ����һ���ַ���������.
    *
    * @return    �ַ���������
    */
   public static StringAppender createStringAppender()
   {
      return createStringAppender(null, 16, false);
   }

   /**
    * ����һ���ַ���������.
    *
    * @param size         ��ʼ�ַ����������
    * @return    �ַ���������
    */
   public static StringAppender createStringAppender(int size)
   {
      return createStringAppender(null, size, false);
   }

   /**
    * ����һ���ַ���������.
    *
    * @param str              ��Ҫ��ʼ����ȥ���ַ���
    * @param plusSize         ����Ҫ��չ������
    * @param needSynchronize  �Ƿ���Ҫ��֤�̰߳�ȫ
    * @return    �ַ���������
    */
   public static StringAppender createStringAppender(String str, int plusSize, boolean needSynchronize)
   {
      int initSize = isEmpty(str) ? plusSize : str.length() + plusSize;
      StringAppender sa;
      if (needSynchronize)
      {
         sa = stringBufferCreater.create(initSize);
      }
      else
      {
         if (stringAppendCreater == null)
         {
            if ("true".equalsIgnoreCase(Utility.getProperty(USE_QUICK_STRING_APPEND)))
            {
               stringAppendCreater = new QuickStringAppender();
            }
            else
            {
               ClassLoader cl = StringTool.class.getClassLoader();
               if (cl.getResource("java/lang/StringBuilder.class") != null)
               {
                  stringAppendCreater = createStringBuilderCreater();
               }
               else
               {
                  stringAppendCreater = stringBufferCreater;
               }
            }
         }
         sa = stringAppendCreater.create(initSize);
      }
      if (!isEmpty(str))
      {
         sa.append(str);
      }
      return sa;
   }

   private static StringAppenderCreater stringBufferCreater = new StrBuffer();
   private static StringAppenderCreater stringAppendCreater = null;

   private static StringAppenderCreater createStringBuilderCreater()
   {
      try
      {
         Class c = Class.forName("self.micromagic.util.StringTool$StringBuilderCreater");
         return (StringAppenderCreater) c.newInstance();
      }
      catch (Throwable ex)
      {
         return stringBufferCreater;
      }
   }
   /*
      ͨ��javassist����̬����ʵ����
      try
      {
         javassist.ClassPool pool = new javassist.ClassPool();
         pool.appendSystemPath();
         pool.appendClassPath(new javassist.ClassClassPath(this.getClass()));
         pool.importPackage("self.micromagic.util");
         String thisName = "StringTool$StringBuilderAppendCreaterFactory$StringBuilder";
         javassist.CtClass cc = pool.makeClass(this.getClass().getName() + "$StringBuilder");
         cc.addInterface(pool.get(StringAppender.class.getName()));
         cc.addInterface(pool.get(StringAppenderCreater.class.getName()));
         cc.addField(javassist.CtField.make("private java.lang.StringBuilder buf;", cc));
         String funBody;

         funBody = "public " + thisName + "() {}";
         cc.addConstructor(javassist.CtNewConstructor.make(funBody, cc));
         funBody = "private " + thisName + "(int initSize)"
               + "{this.buf = new java.lang.StringBuilder(initSize);}";
         cc.addConstructor(javassist.CtNewConstructor.make(funBody, cc));

         funBody = "public StringAppender create(int initSize)"
               + "{return new " + thisName + "(initSize);}";
         cc.addMethod(javassist.CtNewMethod.make(funBody, cc));
         funBody = "public StringAppender append(Object obj)"
               + "{this.buf.append(obj);return this;}";
         cc.addMethod(javassist.CtNewMethod.make(funBody, cc));
         funBody = "public StringAppender append(String str)"
               + "{this.buf.append(str);return this;}";
         cc.addMethod(javassist.CtNewMethod.make(funBody, cc));
         funBody = "public StringAppender append(String str, int startIndex, int length)"
               + "{this.buf.append(str.substring(startIndex, length));return this;}";
         cc.addMethod(javassist.CtNewMethod.make(funBody, cc));
         funBody = "public StringAppender append(char[] chars)"
               + "{this.buf.append(chars);return this;}";
         cc.addMethod(javassist.CtNewMethod.make(funBody, cc));
         funBody = "public StringAppender append(char[] chars, int startIndex, int length)"
               + "{this.buf.append(chars, startIndex, length);return this;}";
         cc.addMethod(javassist.CtNewMethod.make(funBody, cc));
         funBody = "public StringAppender append(boolean value)"
               + "{this.buf.append(value);return this;}";
         cc.addMethod(javassist.CtNewMethod.make(funBody, cc));
         funBody = "public StringAppender append(char ch)"
               + "{this.buf.append(ch);return this;}";
         cc.addMethod(javassist.CtNewMethod.make(funBody, cc));
         funBody = "public StringAppender append(int value)"
               + "{this.buf.append(value);return this;}";
         cc.addMethod(javassist.CtNewMethod.make(funBody, cc));
         funBody = "public StringAppender append(long value)"
               + "{this.buf.append(value);return this;}";
         cc.addMethod(javassist.CtNewMethod.make(funBody, cc));
         funBody = "public StringAppender append(float value)"
               + "{this.buf.append(value);return this;}";
         cc.addMethod(javassist.CtNewMethod.make(funBody, cc));
         funBody = "public StringAppender append(double value)"
               + "{this.buf.append(value);return this;}";
         cc.addMethod(javassist.CtNewMethod.make(funBody, cc));
         funBody = "public String toString() {return this.buf.toString();}";
         cc.addMethod(javassist.CtNewMethod.make(funBody, cc));
         funBody = "public int length() {return this.buf.length();}";
         cc.addMethod(javassist.CtNewMethod.make(funBody, cc));
         funBody = "public char charAt(int index) {return this.buf.charAt(index);}";
         cc.addMethod(javassist.CtNewMethod.make(funBody, cc));
         funBody = "public CharSequence subSequence(int start, int end)"
               + "{return this.buf.subSequence(start, end);}";
         cc.addMethod(javassist.CtNewMethod.make(funBody, cc));

         Class c = cc.toClass(this.getClass().getClassLoader());
         return (StringAppenderCreater) c.newInstance();
      }
      catch (Throwable ex)
      {
         return null;
      }
   */

   interface StringAppenderCreater
   {
      StringAppender create(int initSize);
   }

   private static class StrBuffer
         implements StringAppender, StringAppenderCreater
   {
      private StringBuffer buf;

      StrBuffer()
      {
      }

      private StrBuffer(int initSize)
      {
         this.buf = new StringBuffer(initSize);
      }

      public StringAppender create(int initSize)
      {
         return new StrBuffer(initSize);
      }

      public StringAppender append(Object obj)
      {
         this.buf.append(obj);
         return this;
      }

      public StringAppender append(String str)
      {
         this.buf.append(str);
         return this;
      }

      public StringAppender append(String str, int startIndex, int length)
      {
         this.buf.append(str.substring(startIndex, length));
         return this;
      }

      public StringAppender append(char[] chars)
      {
         this.buf.append(chars);
         return this;
      }

      public StringAppender append(char[] chars, int startIndex, int length)
      {
         this.buf.append(chars, startIndex, length);
         return this;
      }

      public StringAppender append(boolean value)
      {
         this.buf.append(value);
         return this;
      }

      public StringAppender append(char ch)
      {
         this.buf.append(ch);
         return this;
      }

      public StringAppender append(int value)
      {
         this.buf.append(value);
         return this;
      }

      public StringAppender append(long value)
      {
         this.buf.append(value);
         return this;
      }

      public StringAppender append(float value)
      {
         this.buf.append(value);
         return this;
      }

      public StringAppender append(double value)
      {
         this.buf.append(value);
         return this;
      }

      public StringAppender appendln()
      {
         this.buf.append(Utility.LINE_SEPARATOR);
         return this;
      }

      public String substring(int beginIndex, int endIndex)
      {
         return this.buf.substring(beginIndex, endIndex);
      }

      public String toString()
      {
         return this.buf.toString();
      }

      public int length()
      {
         return this.buf.length();
      }

      public char charAt(int index)
      {
         return this.buf.charAt(index);
      }

      public CharSequence subSequence(int start, int end)
      {
         return this.buf.subSequence(start, end);
      }

   }


   public static void printStringHaxcode(PrintStream out, String str, boolean endline)
   {
      if (str == null)
      {
         return;
      }
      int count = str.length();
      for (int i = 0; i < count; i++)
      {
         out.print(Integer.toHexString(str.charAt(i)));
      }
      if (endline)
      {
         out.println();
      }
   }


   /**
    * ��ISO8859-1��ʽ���ַ���<code>str</code>ת��ΪUnicode�����ʽ
    * ���ַ���. <p>
    * ����Java���ַ�����Unicode�����ʽ������������ʽ���ַ�����Ҫ��
    * ����и�ʽת��������ͻ��ڴ洢����ʾ��ʱ��������롣
    *
    * @param   str    Ҫ���б����ʽת�����ַ���
    * @return  ת����Unicode�����ʽ���ַ���
    */
   public static String decodeFrom8859_1(String str)
   {
      if (str == null)
      {
         return null;
      }

      try
      {
         String decodeStr;
         //���б����ʽ��ת��
         byte[] temp = str.getBytes("8859_1");
         decodeStr = new String(temp);
         return decodeStr;
      }
      catch (UnsupportedEncodingException uee)
      {
         //�������׳�����쳣��ӦΪ����ʹ�õ�����ȷ�ı����ǳ�
         throw new InternalError();
      }
   }

   /**
    * ��һ��ISO8859-1��ʽ���ַ���<code>str</code>ת��ΪUnicode�����ʽ
    * ���ַ���. <p>
    * ����Java���ַ�����Unicode�����ʽ������������ʽ���ַ�����Ҫ��
    * ����и�ʽת��������ͻ��ڴ洢����ʾ��ʱ��������롣
    *
    * @param   astr    Ҫ���б����ʽת�����ַ�������
    * @return  ת����Unicode�����ʽ���ַ�������
    */
   public static String[] decodeFrom8859_1(String[] astr)
   {
      String[] decodeValues = new String[astr.length];

      for (int i = 0; i < decodeValues.length; i++)
      {
         decodeValues[i] = decodeFrom8859_1(astr[i]);
      }

      return decodeValues;
   }

   /**
    * ��Unicode��ʽ���ַ���<code>str</code>ת��ΪISO8859-1�����ʽ
    * ���ַ���. <p>
    * ����Java���ַ�����Unicode�����ʽ���������豸��ҪISO8859-1
    * �����ʽ�Ļ������������и�ʽת��������ͻ�����ʾ���ĵ�ʱ��
    * �������롣
    *
    * @param   str    Ҫ���б����ʽת�����ַ���
    * @return  ת����ISO8859-1�����ʽ���ַ���
    */
   public static String encodeTo8859_1(String str)
   {
      if (str == null)
      {
         return null;
      }

      try
      {
         String encodeStr;
         //���б����ʽ��ת��
         byte[] temp = str.getBytes();
         encodeStr = new String(temp, "8859_1");
         return encodeStr;
      }
      catch (UnsupportedEncodingException uee)
      {
         //�������׳�����쳣��ӦΪ����ʹ�õ�����ȷ�ı����ǳ�
         throw new InternalError();
      }
   }

   /**
    * ��һ��Unicode��ʽ���ַ���<code>str</code>ת��ΪISO8859-1�����ʽ
    * ���ַ���. <p>
    * ����Java���ַ�����Unicode�����ʽ���������豸��ҪISO8859-1
    * �����ʽ�Ļ������������и�ʽת��������ͻ�����ʾ���ĵ�ʱ��
    * �������롣
    *
    * @param   astr    Ҫ���б����ʽת�����ַ�������
    * @return  ת����ISO8859-1�����ʽ���ַ�������
    */
   public static String[] encodeTo8859_1(String[] astr)
   {
      String[] encodeValues = new String[astr.length];

      for (int i = 0; i < encodeValues.length; i++)
      {
         encodeValues[i] = encodeTo8859_1(astr[i]);
      }

      return encodeValues;
   }

}
