
package self.micromagic.util;

import java.io.UnsupportedEncodingException;
import java.io.PrintStream;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * <code>StringTool</code>类是实现一些常用的字符串处理. <p>
 *
 * @author  micromagic
 * @version 1.0, 2002-10-18
 */
public class StringTool
{
   /**
    * 是否需要使用QuickStringAppend.
    * 如果需要则可在配置文件中设置:
    * eterna.use.quickStringAppend=true
    */
   public static final String USE_QUICK_STRING_APPEND = "eterna.use.quickStringAppend";

   public static final int MAX_INTERN_SIZE = 1024 * 8;

   /**
    * 对字符串进行intern处理. <p>
    * 如果输入的字符串为null, 则直接返回null。如果输入的字符串长
    * 度超出了限制, 则不进行intern处理。
    *
    * @param str           要intern处理的字符串
    * @param newOnBeyond   超出限制是是否要重新构造字符串
    * @return              处理完的字符串
    */
   public static String intern(String str, boolean newOnBeyond)
   {
      /*
      对一些长期保存的字符串将其放入字符串池, 这样可节省30%以上的内存空间
      另外对于超过8k的字符串, 当设置了newOnBeyond时, 将会重新生成新的字符串
      因为如StringBuffer生成的字符串，会有浪费的空间, 如果直接长期保存的话
      这些空间就不会被释放, 将会长期占用内存
      */
      if (str == null)
      {
         return null;
      }
      return str.length() > MAX_INTERN_SIZE ?
            (newOnBeyond ? new String(str) : str) : str.intern();
   }

   /**
    * 对字符串进行intern处理. <p>
    * 如果输入的字符串为null, 则直接返回null。如果输入的字符串长
    * 度超出了限制, 则不进行intern处理。
    *
    * @param str    要intern处理的字符串
    * @return       处理完的字符串
    */
   public static String intern(String str)
   {
      return intern(str, false);
   }

   /**
    * 判断字符串是否为空. <p>
    * 空串<code>""</code>和空对象<code>null</code>都返回<code>true</code>, 其他
    * 情况则返回<code>false</code>.
    *
    * @param str    被判断的字符串
    * @return       是否为空
    */
   public static boolean isEmpty(String str)
   {
      return str == null || str.length() == 0;
   }

   /**
    * 根据指定的分隔符<code>delimiter</code>分割一个字符串
    * <code>str</code>. <p>
    * 如果给出的字符串<code>str</code>为<code>null</code>、空串
    * <code>""</code>，则返回<code>null</code>，只包含分隔符
    * <code>delimiter</code>，则返回长度为0的字符串数组。如果给出的
    * 字符串<code>str</code>可以分割，即不在上述情况之列，则返回一个
    * 字符串数组，每个单元包含一个被分隔出来的字符串。
    * <p>
    * 例如：
    * <blockquote><pre>
    * StringProcessor.separateString("一,二,三", ",")
    *         返回 {"一", "二", "三"}
    * StringProcessor.separateString("你好，大家好！我很好。", "，！")
    *         返回 {"你好", "大家好", "我很好。"}
    * StringProcessor.separateString(null, ", \t\r\n")
    *         返回 null
    * StringProcessor.separateString(", , , , ", ", \n")
    *         返回 {}
    * </pre></blockquote>
    *
    * @param str         要进行分割的字符串
    * @param delimiter   分隔符集合
    * @return  分割后的字符串所组成的数组
    */
   public static String[] separateString(String str, String delimiter)
   {
      return separateString(str, delimiter, false);
   }


   /**
    * 根据指定的分隔符<code>delimiter</code>分割一个字符串
    * <code>str</code>. <p>
    *
    * @param str         要进行分割的字符串
    * @param delimiter   分隔符集合
    * @param trim        是否要对分割出来的每个字符串进行去除空格处理
    * @return  分割后的字符串所组成的数组
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
         // 仅有一个字符时用字符比较来判断
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
         // 有多个字符时用字符串的包含字符来判断
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
   原来旧的实现
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
    * 将<code>src</code>字符串中的所有<code>oldStr</code>替换为
    * <code>newStr</code>. <p>
    * 如果<code>oldStr</code>没有在原字符串中出现, 则返回原字符串.
    *
    * @param src           原字符串
    * @param oldStr        要替换的子串
    * @return              替换后的字符串
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
    * 将字符串数组<code>arr</code>用链接符<code>link</code>链接成一个字符串. <p>
    *
    * @param arr    要链接的字符串数组
    * @param link   链接符
    * @return       用链接符链接后的字符串
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
    * 将字符串按照一定的格式转换成Map. <p>
    *
    * @param str              要转换成Map的字符串
    * @param itemDelimiter    Map元素的分隔符集合
    * @param kvDelimiter      key和value的分隔符
    * @return          转换后的Map对象
    */
   public static Map string2Map(String str, String itemDelimiter, char kvDelimiter)
   {
      return string2Map(str, itemDelimiter, kvDelimiter, true, true, null, null);
   }

   /**
    * 将字符串按照一定的格式转换成Map. <p>
    *
    * @param str              要转换成Map的字符串
    * @param itemDelimiter    Map元素的分隔符集合
    * @param kvDelimiter      key和value的分隔符
    * @param trimItem         是否要对每个元素进行trim
    * @param needResolve      是否要处理文本中"${...}"的动态属性
    * @param resolveRes       处理动态属性是绑定的资源
    * @param result           将转换的结果放入此Map中
    * @return          转换后的Map对象
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
    * 创建一个字符串连接器.
    *
    * @return    字符串连接器
    */
   public static StringAppender createStringAppender()
   {
      return createStringAppender(null, 16, false);
   }

   /**
    * 创建一个字符串连接器.
    *
    * @param size         初始字符缓存的容量
    * @return    字符串连接器
    */
   public static StringAppender createStringAppender(int size)
   {
      return createStringAppender(null, size, false);
   }

   /**
    * 创建一个字符串连接器.
    *
    * @param str              需要初始化进去的字符串
    * @param plusSize         还需要扩展的容量
    * @param needSynchronize  是否需要保证线程安全
    * @return    字符串连接器
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
      通过javassist来动态构造实现类
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
    * 将ISO8859-1格式的字符串<code>str</code>转换为Unicode编码格式
    * 的字符串. <p>
    * 由于Java的字符串是Unicode编码格式，对于其它格式的字符串需要对
    * 其进行格式转换，否则就会在存储或显示的时候产生乱码。
    *
    * @param   str    要进行编码格式转换的字符串
    * @return  转换成Unicode编码格式的字符串
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
         //进行编码格式的转换
         byte[] temp = str.getBytes("8859_1");
         decodeStr = new String(temp);
         return decodeStr;
      }
      catch (UnsupportedEncodingException uee)
      {
         //不可能抛出这个异常，应为我们使用的是正确的编码们称
         throw new InternalError();
      }
   }

   /**
    * 将一组ISO8859-1格式的字符串<code>str</code>转换为Unicode编码格式
    * 的字符串. <p>
    * 由于Java的字符串是Unicode编码格式，对于其它格式的字符串需要对
    * 其进行格式转换，否则就会在存储或显示的时候产生乱码。
    *
    * @param   astr    要进行编码格式转换的字符串数组
    * @return  转换成Unicode编码格式的字符串数组
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
    * 将Unicode格式的字符串<code>str</code>转换为ISO8859-1编码格式
    * 的字符串. <p>
    * 由于Java的字符串是Unicode编码格式，如果输出设备需要ISO8859-1
    * 编码格式的话，则需对其进行格式转换，否则就会在显示中文的时候
    * 产生乱码。
    *
    * @param   str    要进行编码格式转换的字符串
    * @return  转换成ISO8859-1编码格式的字符串
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
         //进行编码格式的转换
         byte[] temp = str.getBytes();
         encodeStr = new String(temp, "8859_1");
         return encodeStr;
      }
      catch (UnsupportedEncodingException uee)
      {
         //不可能抛出这个异常，应为我们使用的是正确的编码们称
         throw new InternalError();
      }
   }

   /**
    * 将一组Unicode格式的字符串<code>str</code>转换为ISO8859-1编码格式
    * 的字符串. <p>
    * 由于Java的字符串是Unicode编码格式，如果输出设备需要ISO8859-1
    * 编码格式的话，则需对其进行格式转换，否则就会在显示中文的时候
    * 产生乱码。
    *
    * @param   astr    要进行编码格式转换的字符串数组
    * @return  转换成ISO8859-1编码格式的字符串数组
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
