
package self.micromagic.eterna.view.impl;

import java.io.IOException;
import java.io.Writer;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.view.StringCoder;
import self.micromagic.util.StringAppender;
import self.micromagic.util.StringTool;

public class StringCoderImpl
      implements StringCoder
{
   /**
    * 解析名称的最大长度.
    */
   protected final static int MAX_PARSE_LENGTH = 32;


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
      if (c == '_' || c == '$' || (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))
      {
         return true;
      }
      if (!first && (c >= '0' && c <= '9'))
      {
         return true;
      }
      return false;
   }

   public String toHTML(String str)
   {
      if (str == null)
      {
         return "";
      }
      StringAppender temp = null;
      int modifyCount = 0;
      for (int i = 0; i < str.length(); i++)
      {
         char c = str.charAt(i);
         String appendStr = null;
         switch (c)
         {
            case '<':
               appendStr = "&lt;";
               modifyCount++;
               break;
            case '>':
               appendStr = "&gt;";
               modifyCount++;
               break;
            case '&':
               appendStr = "&amp;";
               modifyCount++;
               break;
            case '"':
               appendStr = "&quot;";
               modifyCount++;
               break;
            case '\'':
               appendStr = "&#39;";
               modifyCount++;
               break;
         }
         if (modifyCount == 1)
         {
            temp = StringTool.createStringAppender(str.length() + 16);
            temp.append(str.substring(0, i));
            //这里将modifyCount的个数增加, 防止下一次调用使他继续进入这个初始化
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
      for (int i = 0; i < str.length(); i++)
      {
         char c = str.charAt(i);
         switch (c)
         {
            case '<':
               out.write("&lt;");
               break;
            case '>':
               out.write("&gt;");
               break;
            case '&':
               out.write("&amp;");
               break;
            case '"':
               out.write("&quot;");
               break;
            case '\'':
               out.write("&#39;");
               break;
            default:
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
      for (int i = 0; i < str.length(); i++)
      {
         char c = str.charAt(i);
         String appendStr = null;
         if (c < ' ')
         {
            if (c == '\r')
            {
               appendStr = "\\r";
            }
            else if (c == '\n')
            {
               appendStr = "\\n";
            }
            else if (c == '\t')
            {
               appendStr = "\\t";
            }
            else if (c == '\b')
            {
               appendStr = "\\b";
            }
            else if (c == '\f')
            {
               appendStr = "\\f";
            }
            else
            {
               appendStr = " ";
            }
            modifyCount++;
         }
         else if (c == '"')
         {
            appendStr = "\\\"";
            modifyCount++;
         }
         else if (c == '\'')
         {
            appendStr = "\\'";
            modifyCount++;
         }
         else if (c == '\\')
         {
            appendStr = "\\\\";
            modifyCount++;
         }
         else if (c == '/')
         {
            appendStr = "\\/";
            modifyCount++;
         }
         else if (c == '<')
         {
            appendStr = "\\u003C";  // 074 = 0x3C = '<'
            modifyCount++;
         }
         if (modifyCount == 1)
         {
            temp = StringTool.createStringAppender(str.length() + 16);
            temp.append(str.substring(0, i));
            //这里将modifyCount的个数增加, 防止下一次调用使他继续进入这个初始化
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
      for (int i = 0; i < str.length(); i++)
      {
         char c = str.charAt(i);
         String appendStr = null;
         if (c < ' ')
         {
            if (c == '\r')
            {
               appendStr = "\\r";
            }
            else if (c == '\n')
            {
               appendStr = "\\n";
            }
            else if (c == '\t')
            {
               appendStr = "\\t";
            }
            else if (c == '\b')
            {
               appendStr = "\\b";
            }
            else if (c == '\f')
            {
               appendStr = "\\f";
            }
            else
            {
               appendStr = " ";
            }
         }
         else if (c == '"')
         {
            appendStr = "\\\"";
         }
         else if (c == '\'')
         {
            appendStr = "\\'";
         }
         else if (c == '\\')
         {
            appendStr = "\\\\";
         }
         else if (c == '/')
         {
            appendStr = "\\/";
         }
         else if (c == '<')
         {
            appendStr = "\\u003C";  // 074 = 0x3C = '<'
         }
         if (appendStr == null)
         {
            out.write(c);
         }
         else
         {
            out.write(appendStr);
         }
      }
   }

}
