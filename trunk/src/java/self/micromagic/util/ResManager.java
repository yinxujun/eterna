
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
 * 资源数据管理者.
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
         // 获取下一行
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
               // 一个“#”代表注释, 无需处理
            }
            else if (firstChar == SPECIAL_FLAG && secondChar == SPECIAL_FLAG
                  && thirdChar != SPECIAL_FLAG)
            {
               // 两个“#”开始代表资源的开始, 后面的文字去除控制字符后为资源的名称
               nowKey = line.substring(2).trim();
               List resList = new ArrayList();
               if (initMap.put(nowKey, resList) != null)
               {
                  throw new IOException("Duplicate res name:" + nowKey + ".");
               }
            }
            else
            {
               // 其它情况为资源的文本
               if (nowKey == null)
               {
                  throw new IOException("Hasn't res name at line:" + lineNum + ".");
               }
               if (firstChar == SPECIAL_FLAG && secondChar == SPECIAL_FLAG
                     && thirdChar == SPECIAL_FLAG)
               {
                  // 连续3个“#”开始代表一个“#”
                  line = line.substring(2);
               }
               List resList = (List) initMap.get(nowKey);
               resList.add(line);
            }
         }
         else if (!this.skipEmptyLine && nowKey != null)
         {
            // 如果不忽略空行, 且开始了资源文本, 则将空行作为资源文本的一部分
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
    * 输出资源的值.
    *
    * @param resName      需要输出的资源的名称
    * @param paramBind    输出的资源需要绑定的参数
    * @param indentCount  输出的资源每行需要缩进的值
    * @param buf          用于输出资源的缓存
    * @return    如果给出了buf参数, 则返回buf, 如果未给出则返回
    *            新生成的<code>StringAppender</code>
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
    * 处理每行起始部分的缩进
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
    * 将List类型的资源文本转换成字符串数组.
    */
   private String[] transToArray(List resList)
   {
      String[] arr = new String[resList.size()];
      return (String[]) resList.toArray(arr);
   }

   /**
    * 获取读取资源数据时使用的字符集.
    */
   public String getCharset()
   {
      return this.charset;
   }

   /**
    * 设置读取资源数据时使用的字符集.
    */
   public void setCharset(String charset)
   {
      this.charset = charset;
   }

}
