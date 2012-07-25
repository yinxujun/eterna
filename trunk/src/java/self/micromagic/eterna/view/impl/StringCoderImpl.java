
package self.micromagic.eterna.view.impl;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.view.StringCoder;
import self.micromagic.util.StringAppender;
import self.micromagic.util.StringTool;

public class StringCoderImpl
      implements StringCoder
{
   /**
    * �������Ƶ���󳤶�.
    */
   protected final static int MAX_PARSE_LENGTH = 32;

	/**
	 * �ַ��ֵ��ĳߴ�.
	 */
	protected final static int CHAR_TABLE_SIZE = 128;

	/**
	 * JSON�������ַ�.
	 */
	protected final static String[] ESCAPES_JSON = new String[CHAR_TABLE_SIZE];

	/**
	 * HTML�������ַ�.
	 */
	protected final static String[] ESCAPES_HTML = new String[CHAR_TABLE_SIZE];

	/**
	 * ��Ч�������ַ�.
	 */
	protected final static boolean[] VALID_NAME_CHARS = new boolean[CHAR_TABLE_SIZE];

	/**
	 * ��Ч����ʼ�����ַ�.
	 */
	protected final static boolean[] VALID_FIRST_NAME_CHARS = new boolean[CHAR_TABLE_SIZE];

	static
	{
		for (int i = 0; i < ' '; i++)
		{
			ESCAPES_JSON[i] = " ";
		}
		ESCAPES_JSON['\r'] = "\\r";
		ESCAPES_JSON['\n'] = "\\n";
		ESCAPES_JSON['\t'] = "\\t";
		ESCAPES_JSON['\b'] = "\\b";
		ESCAPES_JSON['\f'] = "\\f";
		ESCAPES_JSON['"'] = "\\\"";
		ESCAPES_JSON['\''] = "\\'";
		ESCAPES_JSON['\\'] = "\\\\";
		ESCAPES_JSON['/'] = "\\/";
		ESCAPES_JSON['<'] = "\\u003C";  // 074 = 0x3C = '<';

		ESCAPES_HTML['<'] = "&lt;";
		ESCAPES_HTML['>'] = "&gt;";
		ESCAPES_HTML['&'] = "&amp;";
		ESCAPES_HTML['"'] = "&quot;";
		ESCAPES_HTML['\''] = "&#39;";

		for (int i = 'a'; i <= 'z'; i++)
		{
			VALID_NAME_CHARS[i] = true;
		}
		for (int i = 'A'; i <= 'Z'; i++)
		{
			VALID_NAME_CHARS[i] = true;
		}
		for (int i = '0'; i <= '9'; i++)
		{
			VALID_NAME_CHARS[i] = true;
		}
		VALID_NAME_CHARS['_'] = true;
		VALID_NAME_CHARS['$'] = true;

		System.arraycopy(VALID_NAME_CHARS, 0, VALID_FIRST_NAME_CHARS, 0, CHAR_TABLE_SIZE);
		for (int i = '0'; i <= '9'; i++)
		{
			VALID_FIRST_NAME_CHARS[i] = false;
		}
	}

   public void initStringCoder(EternaFactory factory)
         throws ConfigurationException
   {
   }

   public String parseJsonRefName(String str)
   {
      if (str == null)
      {
         return "";
      }
      if (str.length() > MAX_PARSE_LENGTH)
      {
         return "[\"" + this.toJsonString(str) + "\"]";
      }
      for (int i = 0; i < str.length(); i++)
      {
         char c = str.charAt(i);
         if (!this.isValidNameChar(c, i == 0))
         {
            return "[\"" + this.toJsonString(str) + "\"]";
         }
      }
      return "." + this.toJsonString(str);
   }

   protected boolean isValidNameChar(char c, boolean first)
   {
		if (first)
		{
			return c < CHAR_TABLE_SIZE ? VALID_FIRST_NAME_CHARS[c]: false;
		}
		else
		{
			return c < CHAR_TABLE_SIZE ? VALID_NAME_CHARS[c]: false;
		}
   }

   public String toHTML(String str)
   {
      if (str == null)
      {
         return "";
      }
      StringAppender temp = null;
      int modifyCount = 0;
		int len = str.length();
      for (int i = 0; i < len; i++)
      {
         char c = str.charAt(i);
         String appendStr = null;
			if (c < CHAR_TABLE_SIZE && (appendStr = ESCAPES_HTML[c]) != null)
			{
				modifyCount++;
			}
         if (modifyCount == 1)
         {
            temp = StringTool.createStringAppender(str.length() + 16);
            temp.append(str.substring(0, i));
            //���ｫmodifyCount�ĸ�������, ��ֹ��һ�ε���ʹ���������������ʼ��
            modifyCount++;
         }
         if (modifyCount > 0)
         {
            if (appendStr == null)
            {
               temp.append(c);
            }
            else
            {
               temp.append(appendStr);
            }
         }
      }
      return temp == null ? str : temp.toString();
   }

   public void toHTML(Writer out, String str)
         throws IOException
   {
      if (str == null)
      {
         return;
      }
		int len = str.length();
      for (int i = 0; i < len; i++)
      {
         char c = str.charAt(i);
			if (c < CHAR_TABLE_SIZE && ESCAPES_HTML[c] != null)
			{
				out.write(ESCAPES_HTML[c]);
			}
			else
			{
				out.write(c);
			}
      }
   }

   public String toJsonString(String str)
   {
      if (str == null)
      {
         return "";
      }
      StringAppender temp = null;
      int modifyCount = 0;
		int len = str.length();
      for (int i = 0; i < len; i++)
      {
         char c = str.charAt(i);
         String appendStr = null;
			if (c < CHAR_TABLE_SIZE && (appendStr = ESCAPES_JSON[c]) != null)
			{
				modifyCount++;
			}
         if (modifyCount == 1)
         {
            temp = StringTool.createStringAppender(str.length() + 16);
            temp.append(str.substring(0, i));
            //���ｫmodifyCount�ĸ�������, ��ֹ��һ�ε���ʹ���������������ʼ��
            modifyCount++;
         }
         if (modifyCount > 0)
         {
            if (appendStr == null)
            {
               temp.append(c);
            }
            else
            {
               temp.append(appendStr);
            }
         }
      }
      return temp == null ? str : temp.toString();
   }

   public void toJsonString(Writer out, String str)
         throws IOException
   {
      if (str == null)
      {
         return;
      }
		this.toJsonStringWithoutCheck(out, str);
   }

	public void toJsonStringWithoutCheck(Writer out, String str)
			throws IOException
	{
		int len = str.length();
		if (len > REFLECT_GET_GAP && StringInfo.valid)
		{
			StringInfo info;
			try
			{
				info = new StringInfo(str);
				int endIndex = info.count + info.offset;
				int startOutPos = info.offset;
				char[] buf = info.buf;
				for (int i = info.offset; i < endIndex; i++)
				{
					char c = buf[i];
					if (c < CHAR_TABLE_SIZE && ESCAPES_JSON[c] != null)
					{
						if (startOutPos < i)
						{
							out.write(buf, startOutPos, i - startOutPos);
						}
						out.write(ESCAPES_JSON[c]);
						startOutPos = i + 1;
					}
				}
				if (startOutPos < endIndex)
				{
					out.write(buf, startOutPos, endIndex - startOutPos);
				}
				return;
			}
			catch (IllegalAccessException ex)
			{
				StringInfo.valid = false;
			}
		}
      for (int i = 0; i < len; i++)
      {
         char c = str.charAt(i);
			if (c < CHAR_TABLE_SIZE && ESCAPES_JSON[c] != null)
			{
				out.write(ESCAPES_JSON[c]);
			}
			else
			{
				out.write(c);
			}
      }
	}

	public void toJsonString(Writer out, int c)
			throws IOException
	{
		if (c < CHAR_TABLE_SIZE && ESCAPES_JSON[c] != null)
		{
			out.write(ESCAPES_JSON[c]);
		}
		else
		{
			out.write(c);
		}
	}


   /**
    * ���ַ���200����ʱ, ʹ�÷������ȡchar�����ʹ��charAt����.
    */
   private static final int REFLECT_GET_GAP = 200;

	private static class StringInfo
	{
		static volatile boolean valid = true;

		public final int offset;
		public final int count;
		public final char[] buf;

		public StringInfo(String str)
				throws IllegalAccessException
		{
			this.offset = strOffsetField.getInt(str);
			this.count = strCountField.getInt(str);
			this.buf = (char[]) strValueField.get(str);
		}

		private static Field strValueField;
		private static Field strCountField;
		private static Field strOffsetField;

		static
		{
			Class c = String.class;
			try
			{
				strValueField = c.getDeclaredField("value");
				strValueField.setAccessible(true);
				strCountField = c.getDeclaredField("count");
				strCountField.setAccessible(true);
				strOffsetField = c.getDeclaredField("offset");
				strOffsetField.setAccessible(true);
			}
			catch (Throwable ex)
			{
				valid = false;
			}
		}
	}

}
