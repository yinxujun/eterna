
package self.micromagic.util;

import java.io.InputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * ��Դ���ݹ�����.
 */
public class ResManager
{
	private static final char SPECIAL_FLAG = '#';
	private static final int INDENT_SIZE = 3;
	private static final char[] INDENT_BUF = {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};

	private String charset = "UTF-8";
	private boolean skipEmptyLine = true;
	private Map resCache = new HashMap();

	/**
	 * �Դ��������������.
	 */
	public static String indentCode(String code, int indent)
	{
		if (StringTool.isEmpty(code))
		{
			return "";
		}
		BufferedReader r = new BufferedReader(new StringReader(code));
		try
		{
			StringAppender buf = StringTool.createStringAppender(code.length() + 128);
			String line = r.readLine();
			int preIndent = indent, nowIndent = indent;
			int preBeginSpace = -1;
			boolean afterFirst = false;
			while (line != null)
			{
				if (afterFirst)
				{
					buf.appendln();
				}
				afterFirst = true;
				int[] arr = doIndentLine(nowIndent, line, buf, preIndent, preBeginSpace);
				preIndent = nowIndent;
				nowIndent = arr[0];
				if (arr[1] >= 0)
				{
					preBeginSpace = arr[1];
				}
				if (nowIndent < indent)
				{
					nowIndent = indent;
				}
				line = r.readLine();
		  }
			return buf.toString();
		}
		catch (IOException ex)
		{
			throw new RuntimeException(ex);
		}
	}

	/**
	 * ����1�е�����.
	 * ����������, ��Ҫ����������� �� ��ǰ�еĿո���
	 */
	private static int[] doIndentLine(int indent, String line, StringAppender buf,
			int preIndent, int preBeginSpace)
	{
		int index = -1;
		int count = line.length();
		int plusCount = 0;
		for (int i = 0; i < count; i++)
		{
			char c = line.charAt(i);
			if (c > ' ')
			{
				index = i;
				break;
			}
			else if (c == '\t')
			{
				// tab����һ����������-1, ��Ϊ�����Ѿ���һ���ո���
				plusCount += INDENT_SIZE - 1;
			}
		}
		// û���ҵ��ǿո����ʵ�ַ�, ��Ϊ���д���
		if (index == -1)
		{
			return new int[]{indent, -1};
		}
		int beginSpace = index + plusCount;
		if (line.charAt(index) == '}' && indent > 0)
		{
			indent--;
		}
		if (preBeginSpace == -1 || preIndent != indent)
		{
			dealIndent(indent, buf);
			buf.append(line.substring(index));
		}
		else
		{
			int tmpI = (beginSpace - preBeginSpace) / INDENT_SIZE;
			tmpI = tmpI < 0 ? 0 : tmpI > 2 ? 2 : tmpI;
			dealIndent(indent + tmpI, buf);
			buf.append(line.substring(index));
		}
		if (getLastValidChar(line) == '{')
		{
			indent++;
		}
		return new int[]{indent, beginSpace};
	}

	/**
	 * ������һ���ǿո��ַ�, ������ǿո��򷵻�0
	 */
	private static char getLastValidChar(String line)
	{
		char c;
		for (int i = line.length() - 1; i >= 0; i--)
		{
			c = line.charAt(i);
			if (c > ' ')
			{
				return c;
			}
		}
		return (char) 0;
	}

	public synchronized void load(InputStream inStream)
			throws IOException
	{
		BufferedReader in = new BufferedReader(new InputStreamReader(inStream, this.charset));
		Map initMap = new HashMap();
		String nowKey = null;
		int lineNum = 0;
		while (true)
		{
			lineNum++;
			// ��ȡ��һ��
			String line = in.readLine();
			if (line == null)
			{
				break;
			}

			if (line.length() > 0)
			{
				char firstChar = line.charAt(0);
				char secondChar = 0;
				if (line.length() > 1)
				{
					secondChar = line.charAt(1);
				}
				char thirdChar = 0;
				if (line.length() > 2)
				{
					thirdChar = line.charAt(2);
				}

				if (firstChar == SPECIAL_FLAG && secondChar != SPECIAL_FLAG)
				{
					// һ����#������ע��, ���账��
				}
				else if (firstChar == SPECIAL_FLAG && secondChar == SPECIAL_FLAG
						&& thirdChar != SPECIAL_FLAG)
				{
					// ������#����ʼ������Դ�Ŀ�ʼ, ���������ȥ�������ַ���Ϊ��Դ������
					nowKey = line.substring(2).trim();
					List resList = new ArrayList();
					if (initMap.put(nowKey, resList) != null)
					{
						throw new IOException("Duplicate res name:" + nowKey + ".");
					}
				}
				else
				{
					// �������Ϊ��Դ���ı�
					if (nowKey == null)
					{
						throw new IOException("Hasn't res name at line:" + lineNum + ".");
					}
					if (firstChar == SPECIAL_FLAG && secondChar == SPECIAL_FLAG
							&& thirdChar == SPECIAL_FLAG)
					{
						// ����3����#����ʼ����һ����#��
						line = line.substring(2);
					}
					List resList = (List) initMap.get(nowKey);
					resList.add(line);
				}
			}
			else if (!this.skipEmptyLine && nowKey != null)
			{
				// ��������Կ���, �ҿ�ʼ����Դ�ı�, �򽫿�����Ϊ��Դ�ı���һ����
				List resList = (List) initMap.get(nowKey);
				resList.add(line);
			}
		}

		Iterator itr = initMap.entrySet().iterator();
		while (itr.hasNext())
		{
			Map.Entry entry = (Map.Entry) itr.next();
			this.resCache.put(entry.getKey(), this.transToArray((List) entry.getValue()));
		}
	}

	/**
	 * �����Դ��ֵ.
	 *
	 * @param resName		��Ҫ�������Դ������
	 * @param paramBind	 �������Դ��Ҫ�󶨵Ĳ���
	 * @param indentCount  �������Դÿ����Ҫ������ֵ
	 * @param buf			 ���������Դ�Ļ���
	 * @return	 ���������buf����, �򷵻�buf, ���δ�����򷵻�
	 *				�����ɵ�<code>StringAppender</code>
	 */
	public StringAppender printRes(String resName, Map paramBind, int indentCount, StringAppender buf)
	{
		if (buf == null)
		{
			buf = StringTool.createStringAppender();
		}
		String[] resArr = (String[]) this.resCache.get(resName);
		for (int i = 0; i < resArr.length; i++)
		{
			if (i > 0)
			{
				buf.appendln();
			}
			String s = paramBind == null ?
					resArr[i] : Utility.resolveDynamicPropnames(resArr[i], paramBind, true);
			if (s.length() > 0)
			{
				dealIndent(indentCount, buf);
				buf.append(s);
			}
		}
		return buf;
	}

	/**
	 * ����ÿ����ʼ���ֵ�����
	 */
	private static void dealIndent(int indentCount, StringAppender buf)
	{
		if (indentCount <= 0)
		{
			return;
		}
		int count = indentCount * INDENT_SIZE;
		while (count > INDENT_BUF.length)
		{
			buf.append(INDENT_BUF);
			count -= INDENT_BUF.length;
		}
		if (count > 0)
		{
			buf.append(INDENT_BUF, 0, count);
		}
	}

	/**
	 * ��List���͵���Դ�ı�ת�����ַ�������.
	 */
	private String[] transToArray(List resList)
	{
		String[] arr = new String[resList.size()];
		return (String[]) resList.toArray(arr);
	}

	/**
	 * ��ȡ��ȡ��Դ����ʱʹ�õ��ַ���.
	 */
	public String getCharset()
	{
		return this.charset;
	}

	/**
	 * ���ö�ȡ��Դ����ʱʹ�õ��ַ���.
	 */
	public void setCharset(String charset)
	{
		this.charset = charset;
	}

}
