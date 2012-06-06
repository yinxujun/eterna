
package self.micromagic.util;

import java.io.InputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
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
   private final char SPECIAL_FLAG = '#';
   private final int INDENT_SIZE = 3;
   private final char[] INDENT_BUF = {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};

   private String charset = "UTF-8";
   private boolean skipEmptyLine = true;
   private Map resCache = new HashMap();

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
    * @param resName      ��Ҫ�������Դ������
    * @param paramBind    �������Դ��Ҫ�󶨵Ĳ���
    * @param indentCount  �������Դÿ����Ҫ������ֵ
    * @param buf          ���������Դ�Ļ���
    * @return    ���������buf����, �򷵻�buf, ���δ�����򷵻�
    *            �����ɵ�<code>StringAppender</code>
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
               resArr[i] : Utility.resolveDynamicPropnames(resArr[i], paramBind);
         if (s.length() > 0)
         {
            this.dealIndent(indentCount, buf);
            buf.append(s);
         }
      }
      return buf;
   }

   /**
    * ����ÿ����ʼ���ֵ�����
    */
   private void dealIndent(int indentCount, StringAppender buf)
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
