
package self.micromagic.util;

import java.lang.reflect.Constructor;

/**
 * һ�����ٵ��ַ���ƴ�ӹ���.
 */
class QuickStringAppender
      implements StringAppender, StringTool.StringAppenderCreater
{
   /**
    * ���ڴ洢�ַ�.
    */
   private char value[];

   /**
    * �ַ��ĸ���.
    */
   private int count;

   QuickStringAppender()
   {
   }

   public QuickStringAppender(int length)
   {
	   this.value = new char[length];
   }

   public StringAppender create(int initSize)
   {
      return new QuickStringAppender(initSize);
   }

   /**
    * ���ַ��Ĵ洢�ռ������չ.
    */
   private void expandCapacity(int minimumCapacity)
   {
	   int newCapacity = (this.value.length + 1) * 2;
      if (newCapacity < 0)
      {
         newCapacity = Integer.MAX_VALUE;
      }
      else if (minimumCapacity > newCapacity)
      {
	      newCapacity = minimumCapacity;
      }
      char[] newValue = new char[newCapacity];
      System.arraycopy(this.value, 0, newValue, 0, this.count);
      this.value = newValue;
	}

   public StringAppender append(Object obj)
   {
      return this.append(String.valueOf(obj));
   }

   public StringAppender append(String str)
   {
      if (str == null)
      {
	      str = String.valueOf(str);
	   }
      int len = str.length();
      int newcount = this.count + len;
      if (newcount > this.value.length)
      {
         this.expandCapacity(newcount);
      }
      str.getChars(0, len, this.value, this.count);
      this.count = newcount;
      return this;
   }

   public StringAppender append(String str, int startIndex, int length)
   {
      if (str == null)
      {
         return this.append(String.valueOf(str));
      }
      if (length > 0)
      {
	      int newcount = this.count + length;
         if (newcount > this.value.length)
         {
            this.expandCapacity(newcount);
         }
         str.getChars(startIndex, startIndex + length, this.value, this.count);
         this.count = newcount;
      }
      return this;
   }

   public StringAppender append(char[] chars)
   {
	   int len = chars.length;
	   int newcount = this.count + len;
	   if (newcount > this.value.length)
      {
	      this.expandCapacity(newcount);
      }
	   System.arraycopy(chars, 0, this.value, this.count, len);
	   this.count = newcount;
	   return this;
   }

   public StringAppender append(char[] chars, int startIndex, int length)
   {
      int newcount = this.count + length;
	   if (newcount > this.value.length)
      {
	      this.expandCapacity(newcount);
      }
	   System.arraycopy(chars, startIndex, this.value, this.count, length);
	   this.count = newcount;
	   return this;
   }

   public StringAppender append(boolean value)
   {
	   return this.append(value ? "true" : "false");
   }

   public StringAppender append(char ch)
   {
      int newcount = this.count + 1;
	   if (newcount > this.value.length)
      {
	      this.expandCapacity(newcount);
      }
	   this.value[this.count++] = ch;
	   return this;
   }

   public StringAppender append(CharSequence s)
   {
   	if (s == null)
   	{
        s = "null";
		}
		if (s instanceof String)
		{
			return this.append((String) s);
		}
		return this.append(s, 0, s.length());
   }

   public StringAppender append(CharSequence s, int start, int end)
   {
   	if (s == null)
   	{
        s = "null";
		}
		if (s instanceof String)
		{
			return this.append(((String) s).substring(start, end));
		}
		if ((start < 0) || (end < 0) || (start > end) || (end > s.length()))
		{
			throw new IndexOutOfBoundsException(
					"start " + start + ", end " + end + ", s.length() " + s.length());
		}
		int len = end - start;
		if (len == 0)
		{
			return this;
		}
		int newCount = this.count + len;
		if (newCount > this.value.length)
		{
			this.expandCapacity(newCount);
		}
		for (int i= start; i < end; i++)
		{
      	this.value[this.count++] = s.charAt(i);
      }
      this.count = newCount;
		return this;
   }

   public StringAppender append(int value)
   {
	   return this.append(Integer.toString(value, 10));
   }

   public StringAppender append(long value)
   {
	   return this.append(Long.toString(value, 10));
   }

   public StringAppender append(float value)
   {
	   return this.append(Float.toString(value));
   }

   public StringAppender append(double value)
   {
	   return this.append(Double.toString(value));
   }

   public StringAppender appendln()
   {
	   return this.append(Utility.LINE_SEPARATOR);
   }

   public String substring(int beginIndex, int endIndex)
   {
	   if (beginIndex < 0)
      {
	      throw new StringIndexOutOfBoundsException("beginIndex:" + beginIndex);
      }
	   if (endIndex > this.count)
      {
	      throw new StringIndexOutOfBoundsException("endIndex:" + endIndex);
      }
	   if (beginIndex > endIndex)
      {
	      throw new StringIndexOutOfBoundsException("beginIndex - endIndex:" + (endIndex - beginIndex));
      }
      return this.createString(beginIndex, endIndex - beginIndex, this.value);
   }

   public String toString()
   {
      if (this.value.length - this.count > MAX_WASTE_COUNT)
      {
         // ����˷ѵĿռ����, ����ø����ַ����ķ�ʽ
         return new String(this.value, 0, this.count);
      }
      return this.createString(0, this.count, this.value);
   }

   public int length()
   {
      return this.count;
   }

   public char charAt(int index)
   {
	   if ((index < 0) || (index >= this.count))
      {
	      throw new StringIndexOutOfBoundsException(index);
	   }
	   return this.value[index];
   }

   public CharSequence subSequence(int start, int end)
   {
      return this.substring(start, end);
   }


   /**
    * ���ַ���200����ʱ, ʹ�÷�����ò������ַ����Ĺ��캯����ȸ����ַ�������.
    */
   private static final int REFLECT_CREATE_GAP = 200;

   /**
    * ����˷ѿռ���ֽ���, ������������, ��ʹ�ø����ַ����ķ�ʽ.
    */
   private static final int MAX_WASTE_COUNT = 512;

   private static Constructor strConstructor;
   static
   {
      try
      {
         strConstructor = String.class.getDeclaredConstructor(
               new Class[]{int.class, int.class, char[].class});
         strConstructor.setAccessible(true);
      }
      catch (Throwable ex)
      {
         ex.printStackTrace();
      }
   }

   private String createString(int offset, int count, char[] chars)
   {
      if (strConstructor == null || count < REFLECT_CREATE_GAP)
      {
         return new String(chars, offset, count);
      }
      else
      {
         try
         {
            return (String) strConstructor.newInstance(
                  new Object[]{new Integer(offset), new Integer(count), chars});
         }
         catch (Throwable ex)
         {
            // �������Ͳ�ʹ�÷����������ַ���
            strConstructor = null;
            return new String(chars, offset, count);
         }
      }
   }

}
