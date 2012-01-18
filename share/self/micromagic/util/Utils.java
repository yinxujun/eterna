
package self.micromagic.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspWriter;

import self.micromagic.eterna.search.SearchAdapter;
import self.micromagic.eterna.search.SearchManager;
import self.micromagic.eterna.sql.ResultRow;
import self.micromagic.eterna.sql.ResultIterator;
import self.micromagic.eterna.digester.ConfigurationException;

public class Utils
{
   public static char[] CODE16 = {
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
      'A', 'B', 'C', 'D', 'E', 'F'
   };

   public static String TOTAL_RECORD_TAG = "self.total.record";
   public static String PAGE_COUNT_TAG = "self.page.count";
   public static String PRE_PAGE_TAG = "self.pre.page";
   public static String NEXT_PAGE_TAG = "self.next.page";

   public final static String SERVER_ROOT_TAG = "self.server.root";

   public static int parseInt(String str)
   {
      return parseInt(str, 0);
   }

   public static int parseInt(String str, int defaultValue)
   {
      try
      {
         return Integer.parseInt(str);
      }
      catch (Exception ex)
      {
         return defaultValue;
      }
   }

   public static double parseDouble(String str)
   {
      return parseDouble(str, 0.0);
   }

   public static double parseDouble(String str, double defaultValue)
   {
      try
      {
         return Double.parseDouble(str);
      }
      catch (Exception ex)
      {
         return defaultValue;
      }
   }

   public static void setListPageAttributes(String listURL, SearchManager.Attributes attributes,
         SearchAdapter.Result result, HttpServletRequest request)
         throws SQLException, ConfigurationException
   {
      String root = request.getContextPath();
      int totalRecord = result.queryResult.getRealRecordCount();
      int pageCount;
      if ((totalRecord % result.pageSize) == 0)
      {
         pageCount = totalRecord / result.pageSize;
      }
      else
      {
         pageCount = totalRecord / result.pageSize + 1;
      }
      request.setAttribute(TOTAL_RECORD_TAG, totalRecord + "");
      request.setAttribute(PAGE_COUNT_TAG, pageCount + "");

      String prePageHref, nextPageFref;
      int prePage = (result.pageNum - 1) > pageCount ? (pageCount - 1) : result.pageNum - 1;
      if (result.pageNum > 0)
      {
         StringBuffer temp = new StringBuffer(256);
         temp.append("<a href=\"").append(root).append(listURL).append(attributes.pageNumTag)
               .append("=").append(prePage).append("\">��һҳ</a>");
         prePageHref = temp.toString();
      }
      else
      {
         prePageHref = "<a>��һҳ</a>";
      }
      if (result.queryResult.isHasMoreRecord())
      {
         StringBuffer temp = new StringBuffer(256);
         temp.append("<a href=\"").append(root).append(listURL).append(attributes.pageNumTag)
               .append("=").append(result.pageNum + 1).append("\">��һҳ</a>");
         nextPageFref = temp.toString();
      }
      else
      {
         nextPageFref = "<a>��һҳ</a>";
      }
      request.setAttribute(PRE_PAGE_TAG, prePageHref);
      request.setAttribute(NEXT_PAGE_TAG, nextPageFref);
   }

   public static String getServerRoot(HttpServletRequest request)
   {
      HttpSession session = request.getSession();
      String serverRoot = (String) session.getAttribute(SERVER_ROOT_TAG);
      if (serverRoot == null)
      {
         serverRoot = request.getScheme() + "://" + request.getServerName()
               + ":" + request.getServerPort();
         session.setAttribute(SERVER_ROOT_TAG, serverRoot);
      }
      return serverRoot;
   }

   public static String getThisPageHref(HttpServletRequest request)
   {
      String href;
      String queryStr = request.getQueryString();
      if (queryStr != null)
      {
         href = request.getRequestURL().append("?")
               .append(request.getQueryString()).toString();
      }
      else
      {
         href = request.getRequestURL().toString();
      }
      return href;
   }

   /**
    * ������������ѡ���script, ��������ΪdoTreeSelect<p>
    * ע: value������ַ��������������˫����<p>
    * ����ʾ��:
    * <p><blockquote><pre>
    *    function doTreeSelect()
    *    {
    *       var sName = [value];
    *       showModalDialog("[root]/eterna/tree.jsp?treeName=[treeName]&selectedName=" + sName,
    *             new Array(window, [returnMethod]), "dialogWidth:380px;dialogHeight:460px;center:yes;status:no;help:no");
    *    }
    * </pre></blockquote>
    * ����, ���withScriptTagΪtrue, ������ǰ�󻹻����<script language="javascript">��</script>
    */
   public static void printTreeSelectScript(JspWriter out, String root, String treeName,
         String value, String returnMethod, boolean withScriptTag)
         throws IOException
   {
      if (withScriptTag)
      {
         out.println("<script language=\"javascript\">");
      }

      out.println("function doTreeSelect()");
      out.println("{");
      out.print("   var sName = ");
      out.print(value);
      out.println(";");

      out.print("   showModalDialog(\"");
      out.print(root);
      out.print("/eterna/tree.jsp?treeName=");
      out.print(treeName);
      out.println("&selectedName=\" + sName,");

      out.print("         new Array(window, ");
      out.print(returnMethod);
      out.println("), \"dialogWidth:380px;dialogHeight:460px;center:yes;status:no;help:no\");");
      out.println("}");

      if (withScriptTag)
      {
         out.println("</script>");
      }
   }

   /**
    * �������ڲ�ѯ��javascript����, ��������ΪdoSearch<p>
    * ����ʾ��:
    * <p><blockquote><pre>
    *    function doSearch()
    *    {
    *       var queryXML = document.[formName].[attributes.querySettingTag];
    *       queryXML.value = "";
    *       showModalDialog("[root]/eterna/query.jsp?searchName==[searchName]",
    *             new Array(window, queryXML), "dialogWidth:520px;dialogHeight:420px;center:yes;status:no;help:no");
    *       if (queryXML.value != "")
    *       {
    *          document.[formName].submit();
    *       }
    *    }
    * </pre></blockquote>
    * ����, ���withScriptTagΪtrue, ������ǰ�󻹻����<script language="javascript">��</script>
    */
   public static void printQueryScript(JspWriter out, SearchManager.Attributes attributes,
         String root, String searchName, String formName, boolean withScriptTag)
         throws IOException
   {
      if (withScriptTag)
      {
         out.println("<script language=\"javascript\">");
      }

      out.println("function doSearch()");
      out.println("{");
      out.print("   var queryXML = document.");
      out.print(formName);
      out.print(".");
      out.print(attributes.querySettingTag);
      out.println(";");
      out.println("   queryXML.value = \"\";");

      out.print("   showModalDialog(\"");
      out.print(root);
      out.print("/eterna/query.jsp?searchName=");
      out.print(searchName);
      out.println("\",");
      out.println("         new Array(window, queryXML), \"dialogWidth:520px;dialogHeight:420px;center:yes;status:no;help:no\");");

      out.println("   if (queryXML.value != \"\")");
      out.println("   {");
      out.print("      document.");
      out.print(formName);
      out.println(".submit();");
      out.println("   }");
      out.println("}");

      if (withScriptTag)
      {
         out.println("</script>");
      }
   }

   /**
    * ����������ת��ĳҳ��javascript����, ��������ΪdoPageJump<p>
    * ����ʾ��:
    * <p><blockquote><pre>
    *    function doPageJump()
    *    {
    *       var queryXML = document.[formName].[attributes.querySettingTag];
    *       queryXML.value = "";
    *       showModalDialog("[root]/eterna/query.jsp?searchName==[searchName]",
    *             new Array(window, queryXML), "dialogWidth:520px;dialogHeight:420px;center:yes;status:no;help:no");
    *       if (queryXML.value != "")
    *       {
    *          document.queryForm.submit();
    *       }
    *    }
    * </pre></blockquote>
    * ����, ���withScriptTagΪtrue, ������ǰ�󻹻����<script language="javascript">��</script>
    */
   public static void printPageJumpScript(JspWriter out, SearchManager.Attributes attributes,
         String root, String searchName, String formName, boolean withScriptTag)
         throws IOException
   {
      if (withScriptTag)
      {
         out.println("<script language=\"javascript\">");
      }

      out.println("function doSearch()");
      out.println("{");
      out.print("   var queryXML = document.");
      out.print(formName);
      out.print(".");
      out.print(attributes.querySettingTag);
      out.println(";");
      out.println("   queryXML.value = \"\";");

      out.print("   showModalDialog(\"");
      out.print(root);
      out.print("/eterna/query.jsp?searchName=");
      out.print(searchName);
      out.println("\",");
      out.println("         new Array(window, queryXML), \"dialogWidth:520px;dialogHeight:420px;center:yes;status:no;help:no\");");

      out.println("   if (queryXML.value != \"\")");
      out.println("   {");
      out.println("      document.queryForm.submit();");
      out.println("   }");
      out.println("}");

      if (withScriptTag)
      {
         out.println("</script>");
      }
   }

   /**
    * �������ڲ�ѯform<p>
    * ����ʾ��:
    * <p><blockquote><pre>
    *    <form name="[formName]" method="post" action="[root]+[queryUrl]">
    *       <input type="hidden" name="[attributes.querySettingTag]" value="">
    *       <input type="hidden" name="[attributes.queryTypeTag]" value="[attributes.queryTypeReset]">
    *       <input type="hidden" name="[attributes.pageNumTag]" value="0">
    *    </form>
    * </pre></blockquote>
    *
    * @param withFormEnd   �Ƿ�Ҫ���form�Ľ�����ǩ&lt;/form&gt;
    */
   public static void printQueryForm(JspWriter out, SearchManager.Attributes attributes,
         String root, String formName, String queryUrl, boolean withFormEnd)
         throws IOException
   {
      out.print("<form name=\"");
      out.print(formName);
      out.print("\" method=\"post\" action=\"");
      out.print(root);
      out.print(queryUrl);
      out.println("\">");

      out.print("   <input type=\"hidden\" name=\"");
      out.print(attributes.querySettingTag);
      out.println("\" value=\"\">");
      out.print("   <input type=\"hidden\" name=\"");
      out.print(attributes.queryTypeTag);
      out.print("\" value=\"");
      out.print(attributes.queryTypeReset);
      out.println("\">");
      out.print("   <input type=\"hidden\" name=\"");
      out.print(attributes.pageNumTag);
      out.println("\" value=\"0\">");

      if (withFormEnd)
      {
         out.println("</form>");
      }
   }

   /**
    * �������ڲ�ѯform<p>
    * ����ʾ��:
    * <p><blockquote><pre>
    *    <form name="[formName]" method="post" action="[root]+[queryUrl]">
    *       <input type="hidden" name="[attributes.querySettingTag]" value="">
    *       <input type="hidden" name="[attributes.queryTypeTag]" value="[attributes.queryTypeReset]">
    *       <input type="hidden" name="[attributes.pageNumTag]" value="0">
    *    </form>
    * </pre></blockquote>
    */
   public static void printQueryForm(JspWriter out, SearchManager.Attributes attributes,
         String root, String formName, String queryUrl)
         throws IOException
   {
      printQueryForm(out, attributes, root, formName, queryUrl, true);
   }


   /**
    * ����select�ؼ���һ��option<p>
    */
   public static void printOptions(JspWriter out, List rows, String indentSpace)
         throws IOException, SQLException, ConfigurationException
   {
      printOptions(out, rows, indentSpace, "codeId", "codeValue");
   }

   /**
    * ����select�ؼ���һ��option<p>
    */
   public static void printOptions(JspWriter out, List rows, String indentSpace,
         String colNameCodeId, String colNameCodeValue)
         throws IOException, SQLException, ConfigurationException
   {
      Iterator itr = rows.iterator();
      while (itr.hasNext())
      {
         ResultRow row = (ResultRow) itr.next();
         printOption(out, row, indentSpace, colNameCodeId, colNameCodeValue);
      }
   }

   /**
    * ����select�ؼ���option<p>
    * ����ʾ��:
    * <p><blockquote><pre>
    *    <option value="[codeId]">[codeValue]</option>
    * </pre></blockquote>
    */
   public static void printOption(JspWriter out, ResultRow row, String indentSpace)
         throws IOException, SQLException, ConfigurationException
   {
      printOption(out, row, indentSpace, "codeId", "codeValue");
   }

   /**
    * ����select�ؼ���option<p>
    * ����ʾ��:
    * <p><blockquote><pre>
    *    <option value="[codeId]">[codeValue]</option>
    * </pre></blockquote>
    */
   public static void printOption(JspWriter out, ResultRow row, String indentSpace,
         String colNameCodeId, String colNameCodeValue)
         throws IOException, SQLException, ConfigurationException
   {
      out.print(indentSpace);
      out.print("<option value=\"");
      out.print(Utils.getResult(row, colNameCodeId, false));
      out.print("\">");
      out.print(Utils.getResult(row, colNameCodeValue, true));
      out.println("</option>");
   }

   /**
    * �����ַ���, ����ת��Ϊ�ɷ���˫�����ڸ�ֵ���ַ���.
    * ͬʱ�ὫС�ڿո�Ĵ���(������:\r,\n,\t), ת��Ϊ�ո�.
    */
   public static String dealString2EditCode(String str)
   {
      if (str == null)
      {
         return "";
      }
      StringBuffer temp = null;
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
         else if (c == '<')
         {
            appendStr = "\\074";  // 074 = 0x3C = '<'
            modifyCount++;
         }
         if (modifyCount == 1)
         {
            temp = new StringBuffer(str.length() + 16);
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

   /**
    * �����ַ���, ����ת��ΪHTML��ʽ�Ĵ���, ��ֱ�����.
    * ���ַ����ʺϴ���Ƚϳ����ַ���
    */
   public static void dealString2HTML(String str, Writer out)
         throws IOException
   {
      dealString2HTML(str, out, false);
   }

   /**
    * �����ַ���, ����ת��ΪHTML��ʽ�Ĵ���, ��ֱ�����.
    * ���ַ����ʺϴ���Ƚϳ����ַ���
    *
    * @param dealNewLine  �Ƿ�Ҫ������"\n"�����"<br>"
    */
   public static void dealString2HTML(String str, Writer out, boolean dealNewLine)
         throws IOException
   {
      if (str == null)
      {
         return;
      }
      boolean preSpace = true;
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
            case '\n':
               if (dealNewLine)
               {
                  out.write("\n<br>");
                  preSpace = true;
               }
               else
               {
                  out.write("\n");
               }
               break;
            case ' ':
               if (dealNewLine)
               {
                  if (preSpace)
                  {
                     out.write("&nbsp;");
                     preSpace = false;
                  }
                  else
                  {
                     out.write(" ");
                     preSpace = true;
                  }
               }
               else
               {
                  out.write(" ");
               }
               break;
            default:
               out.write(c);
         }
         if (c > ' ')
         {
            preSpace = false;
         }
      }
   }

   /**
    * �����ַ���, ����ת��ΪHTML��ʽ�Ĵ���.
    */
   public static String dealString2HTML(String str)
   {
      return dealString2HTML(str, false);
   }

   /**
    * �����ַ���, ����ת��ΪHTML��ʽ�Ĵ���.
    *
    * @param dealNewLine  �Ƿ�Ҫ������"\n"�����"<br>"
    */
   public static String dealString2HTML(String str, boolean dealNewLine)
   {
      if (str == null)
      {
         return "";
      }
      StringBuffer temp = null;
      int modifyCount = 0;
      boolean preSpace = true;
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
            case '\n':
               if (dealNewLine)
               {
                  appendStr = "\n<br>";
                  modifyCount++;
                  preSpace = true;
               }
               break;
            case ' ':
               if (dealNewLine)
               {
                  if (preSpace)
                  {
                     appendStr = "&nbsp;";
                     modifyCount++;
                     preSpace = false;
                  }
                  else
                  {
                     preSpace = true;
                  }
               }
               break;
         }
         if (c > ' ')
         {
            preSpace = false;
         }
         if (modifyCount == 1)
         {
            temp = new StringBuffer(str.length() + 16);
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

   /**
    * �����ַ���, ����ת��ΪURL��ʽ�Ĵ���.
    */
   public static String dealString2URL(String str)
   {
      return dealString2URL(str, null);
   }

   /**
    * �����ַ���, ����ת��ΪURL��ʽ�Ĵ���.
    */
   public static String dealString2URL(String str, String charsetName)
   {
      if (str == null)
      {
         return "";
      }
      StringBuffer temp = null;
      int modifyCount = 0;
      for (int i = 0; i < str.length(); i++)
      {
         int c = (int) str.charAt(i);
         String appendStr = null;
         if (charsetName != null && c >= 128)
         {
            try
            {
               byte[] bytes = null;
               /*
                * If this character represents the start of a Unicode
                * surrogate pair, then pass in two characters. It's not
                * clear what should be done if a bytes reserved in the
                * surrogate pairs range occurs outside of a legal
                * surrogate pair. For now, just treat it as if it were
                * any other character.
                */
               if (c >= 0xD800 && c <= 0xDBFF)
               {
                  if ((i + 1) < str.length())
                  {
                     int d = (int) str.charAt(i + 1);
                     if (d >= 0xDC00 && d <= 0xDFFF)
                     {
                        bytes = (((char) c) + "" + ((char) d)).getBytes(charsetName);
                        i++;
                     }
                  }
                  if (bytes == null)
                  {
                     bytes = (((char) c) + "").getBytes(charsetName);
                  }
               }
               else
               {
                  bytes = (((char) c) + "").getBytes(charsetName);
               }
               StringBuffer tAS = new StringBuffer(bytes.length * 3);
               for (int index = 0; index < bytes.length; index++)
               {
                  tAS.append("%");
                  int tbyte = bytes[index] & 0xff;
                  if (tbyte < 16)
                  {
                     tAS.append("0");
                  }
                  else
                  {
                     tAS.append(CODE16[tbyte >> 4]);
                  }
                  tAS.append(CODE16[tbyte & 0xf]);
               }
               appendStr = tAS.toString();
               modifyCount++;
            }
            catch (UnsupportedEncodingException ex)
            {
               throw new RuntimeException(ex);
            }
         }
         else
         {
            if (c == ' ')
            {
               appendStr = "+";
               modifyCount++;
            }
            else if (!(c >= 'a' && c <= 'z') && !(c >= '0' && c <= '9') && !(c >= 'A' && c <= 'Z')
                  && c != '.' && c != '-' && c != '_' && c != '*')
            {
               char[] tAS = new char[3];
               tAS[0] = '%';
               if (c < 16)
               {
                  tAS[1] = '0';
               }
               else
               {
                  tAS[1] = CODE16[(c >> 4) & 0xf];
               }
               tAS[2] = CODE16[c & 0xf];
               appendStr = new String(tAS);
               modifyCount++;
            }
         }
         if (modifyCount == 1)
         {
            temp = new StringBuffer(str.length() + 16);
            temp.append(str.substring(0, i));
            //���ｫmodifyCount�ĸ�������, ��ֹ��һ�ε���ʹ���������������ʼ��
            modifyCount++;
         }
         if (modifyCount > 0)
         {
            if (appendStr == null)
            {
               temp.append((char) c);
            }
            else
            {
               temp.append(appendStr);
            }
         }
      }
      return temp == null ? str : temp.toString();
   }

   /**
    * ��ȡĳ�µ���ʼ�����ַ���, ���ڰ����ڲ�ѯ.
    *
    * @param offset   �뱾�µ�ƫ������, ��ǰΪ��
    */
   public static String getMonthFirstDayString(int offset)
   {
      return getMonthDayString(offset, 1);
   }

   /**
    * ��ȡĳ�µ���ʼ�����ַ���, ���ڰ����ڲ�ѯ.
    *
    * @param offset   �뱾�µ�ƫ������, ��ǰΪ��
    * @param monthDay   ָ����ĳ������, ������1��25֮��
    */
   public static String getMonthDayString(int offset, int monthDay)
   {
      if (monthDay < 1 || monthDay > 25)
      {
         monthDay = 1;
      }
      Calendar c = Calendar.getInstance();
      int year = c.get(Calendar.YEAR);
      int month = c.get(Calendar.MONTH);
      month += offset;
      if (month < 0)
      {
         int offYear = (-month) / 12 + 1;
         year -= offYear;
         month += 12 * offYear;
      }
      else if (month >= 12)
      {
         int offYear = month / 12;
         year += offYear;
         month -= 12 * offYear;
      }
      StringBuffer date = new StringBuffer(10);
      return date.append(year).append("-").append(month + 1).append("-")
            .append(monthDay).toString();
   }

   /**
    * ��ȡĳ�µ���ʼ����, ���ڰ����ڲ�ѯ.
    *
    * @param offset   �뱾�µ�ƫ������, ��ǰΪ��
    */
   public static Date getMonthFirstDay(int offset)
   {
      return getMonthDay(offset, 1);
   }

   /**
    * ��ȡĳ�µ�ָ������, ���ڰ����ڲ�ѯ.
    *
    * @param offset     �뱾�µ�ƫ������, ��ǰΪ��
    * @param monthDay   ָ����ĳ������, ������1��25֮��
    */
   public static Date getMonthDay(int offset, int monthDay)
   {
      if (monthDay < 1 || monthDay > 25)
      {
         monthDay = 1;
      }
      Calendar c = Calendar.getInstance();
      int year = c.get(Calendar.YEAR);
      int month = c.get(Calendar.MONTH);
      month += offset;
      if (month < 0)
      {
         int offYear = (-month) / 12 + 1;
         year -= offYear;
         month += 12 * offYear;
      }
      else if (month >= 12)
      {
         int offYear = month / 12;
         year += offYear;
         month -= 12 * offYear;
      }
      c.set(year, month, monthDay, 0, 0, 0);
      c.set(Calendar.MILLISECOND, 0);
      return new Date(c.getTimeInMillis());
   }

   /**
    * ����һ���ַ����ĳ���, Ӣ����ĸ(0x20~0x7f)��������.
    * ����ַ����ĳ��ȳ���, ��ֻ��������ڵ��ַ���, �����������"...".
    */
   public static String formatLength(String str, int length)
   {
      if (str == null)
      {
         return "";
      }
      if (str.length() < length)
      {
         return str;
      }

      int countLimit = length * 2;
      int count = 0;
      int preCount = 0;
      for (int i = 0; i < str.length(); i++)
      {
         preCount = count;
         char c = str.charAt(i);
         if (c >= 0x20 && c < 0x7f)
         {
            count++;
         }
         else
         {
            count += 2;
         }
         if (count > countLimit)
         {
            int end = i - 1;
            c = str.charAt(i - 1);
            if (c >= 0x20 && c < 0x7f && preCount == countLimit)
            {
               end--;
            }
            return str.substring(0, end) + "...";
         }
      }
      return str;
   }

   /**
    * ������̬��񴫵ݹ����������ݡ�
    * ����һ��List�������������ַ������������档
    */
   public static List parseDynamicTableValue(String str)
   {
      return separateString(str, '\t');
   }

   /**
    * ���ݷָ���"separate"���ָ�һ���ַ���"str".
    * ����һ��List���ָ��������ַ������������档
    */
   public static List separateString(String str, char separate)
   {
      String strSep = separate + "";
      StringTokenizer token = new StringTokenizer(str, strSep, true);
      ArrayList list = new ArrayList();
      String nowValue = "";
      while (token.hasMoreTokens())
      {
         String temp = token.nextToken();
         if (temp.equals(strSep))
         {
            list.add(nowValue);
            nowValue = "";
         }
         else
         {
            nowValue = temp;
         }
      }
      list.add(nowValue);
      return list;
   }

   /**
    * ��һ���ַ�������"arr"��һ���������ַ���"linkChar"����������
    * ע: ����ֵ��������һ�������ַ�����
    */
   public static String linkStringArray(String[] arr, String linkChar)
   {
      if (arr == null)
      {
         return null;
      }
      StringBuffer buf = new StringBuffer(linkChar.length() * arr.length
            + arr.length * 8);
      for (int i = 0; i < arr.length; i++)
      {
         buf.append(arr[i]);
         buf.append(linkChar);
      }
      return buf.toString();
   }

   /**
    * �ֽ�ѡ���е����ݣ���ʽΪ��ÿ����¼�á�;���ָÿ���á�,���ָ
    * ����һ��List��ÿ����¼��Ϊһ��String��������List�С�
    */
   public static List parseSelection(String selectionStr)
   {
      StringTokenizer token = new StringTokenizer(selectionStr, ";");
      ArrayList list = new ArrayList();
      while (token.hasMoreTokens())
      {
         String tempStr = token.nextToken();
         StringTokenizer subToken = new StringTokenizer(tempStr, ",");
         String[] record = new String[subToken.countTokens()];
         for (int i = 0; i < record.length; i++)
         {
            record[i] = subToken.nextToken();
         }
         list.add(record);
      }
      return list;
   }

   /**
    * ��һ��Iteratorת��ΪList��
    */
   public static List iterator2List(Iterator itr)
   {
      LinkedList list = new LinkedList();
      while (itr.hasNext())
      {
         list.add(itr.next());
      }
      return list;
   }

   /**
    * ����Ϊnull�Ķ�����ַ���ת��
    */
   public static String dealNull(Object str)
   {
      return str == null ? "" : str.toString();
   }

   /**
    * ����name��ȡrow�е�����, ����null��Ϊ�յ��ַ���.
    * ���rowΪnull, ��ֱ�ӷ��ؿ��ַ���.
    *
    * @param toHTML   �Ƿ�Ҫ����HTML���ַ�
    */
   public static String getResult(ResultRow row, String name, boolean toHTML)
         throws SQLException, ConfigurationException
   {
      if (row == null)
      {
         return "";
      }
      String str = row.getFormated(name);
      return str == null ? "" : toHTML ? Utils.dealString2HTML(str) : str;
   }

   public static abstract class Print
   {
      public void print(String name)
            throws SQLException, IOException, ConfigurationException
      {
         this.print(name, true);
      }

      public void print(String name, String defaultVale)
            throws SQLException, IOException, ConfigurationException
      {
         this.print(name, true, defaultVale);
      }

      public void print(String name, boolean toHTML)
            throws SQLException, IOException, ConfigurationException
      {
         this.print(name, toHTML, null);
      }

      public abstract void print(String name, boolean toHTML, String defaultVale)
            throws SQLException, IOException, ConfigurationException;
   }

   public static class ResultPrint extends Print
   {
      private Writer out;
      private ResultRow row;

      public ResultPrint(Writer out, Object obj)
            throws SQLException, ConfigurationException
      {
         this.out = out;
         this.row = null;
         if (obj != null)
         {
            if (obj instanceof ResultRow)
            {
               this.row = (ResultRow) obj;
            }
            else if (obj instanceof ResultIterator)
            {
               ResultIterator ritr = (ResultIterator) obj;
               if (ritr.hasMoreRow())
               {
                  this.row = ritr.nextRow();
               }
            }
         }
      }

      public ResultPrint(Writer out, ResultRow row)
      {
         this.out = out;
         this.row = row;
      }

      public void print(String name, boolean toHTML, String defaultVale)
            throws SQLException, IOException, ConfigurationException
      {
         if (this.row == null)
         {
            if (defaultVale != null)
            {
               this.out.write(defaultVale);
            }
            return;
         }
         if (toHTML)
         {
            Utils.dealString2HTML(this.row.getFormated(name), this.out, true);
         }
         else
         {
            this.out.write(this.row.getFormated(name));
         }
      }

   }

   public static class ConditionPrint extends Print
   {
      private Writer out;
      private SearchManager searchManager;

      public ConditionPrint(Writer out, Object searchManager)
      {
         this.out = out;
         if (searchManager instanceof SearchManager)
         {
            this.searchManager = (SearchManager) searchManager;
         }
      }

      public ConditionPrint(Writer out, SearchManager searchManager)
      {
         this.out = out;
         this.searchManager = searchManager;
      }

      public void print(String name, boolean toHTML, String defaultVale)
            throws SQLException, IOException
      {
         if (this.searchManager == null)
         {
            if (defaultVale != null)
            {
               this.out.write(defaultVale);
            }
            return;
         }
         SearchManager.Condition condition = this.searchManager.getCondition(name);
         if (condition == null || condition.value == null)
         {
            return;
         }
         if (toHTML)
         {
            Utils.dealString2HTML(condition.value, this.out, true);
         }
         else
         {
            this.out.write(condition.value);
         }
      }

   }

   public static void main(String[] args)
         throws Exception
   {
      /*
      System.out.println(dealString2HTML("<a>"));
      System.out.println(Utility.getProperty("self.micromagic.logger.level", "INFO"));
      Log log = Utility.createLog("test");
      System.out.println(Level.parse(Utility.getProperty("self.micromagic.logger.level", "INFO")));
      System.out.println(((Jdk14Logger) log).getLogger().getLevel());
      System.out.println(log.isDebugEnabled());
      System.out.println(log.isInfoEnabled());
      java.lang.reflect.Field f = String.class.getDeclaredField("value");
      f.setAccessible(true);
      String str = "123";
      char[] buf = (char[]) f.get(str);
      System.out.println(str);
      buf[0] = 'd';
      System.out.println(str);
      */
   }

}
