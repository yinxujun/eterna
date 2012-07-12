
package self.micromagic.eterna.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import self.micromagic.app.WebApp;
import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.view.ViewAdapter;
import self.micromagic.util.StringTool;

/**
 * 在JSP中, 可通过此标签在页面中初始化Eterna对象.
 */
public class EternaInit extends InitBaseTag
{
	/**
	 * 不输出html代码
	 */
	public static final int PRINT_HTML_NONE = 0;

	/**
	 * 仅输出eterna容器对象的div代码
	 */
	public static final int PRINT_HTML_PART = 1;

	/**
	 * 输出完整的html页面代码
	 */
	public static final int PRINT_HTML_ALL = 2;

	/**
	 * 参数中控制debug的参数名
	 */
	public static final String PARAM_DEBUF_FLAG = "___debug";

   private String view;
   private String appData;
   private String jsList;
   private String cssList;
	private int printHTML;
	private String charset;
	private String divClass;

   public int doStartTag()
         throws JspException
   {
      try
      {
			String viewTag = this.view == null ? WebApp.VIEW_TAG : this.view;
			String appDataTag = this.appData == null ? WebApp.APPDATA_TAG : this.appData;
			JspWriter out = this.pageContext.getOut();
			ViewAdapter view = (ViewAdapter) this.pageContext.findAttribute(viewTag);
			AppData data = (AppData) this.pageContext.findAttribute(appDataTag);
			String dataType = view.getDataType(data);
			if (ViewAdapter.DATA_TYPE_WEB.equals(dataType))
			{
				this.printInitPage(view, data, out);
			}
			else
			{
				view.printView(out, data, this.getCacheMap(view));
			}
      }
      catch (ConfigurationException ex)
      {
         DefaultFinder.log.warn("Error in service.", ex);
      }
      catch (Throwable ex)
      {
         DefaultFinder.log.error("Other Error in service.", ex);
      }
      return SKIP_BODY;
   }

	private void printSourceList(JspWriter out, boolean jsResource, String resList)
         throws IOException
	{
		if (resList != null)
		{
			String[] arr = StringTool.separateString(resList, ";", true);
			for (int i = 0; i < arr.length; i++)
			{
				out.println("window._loadResource(\"" + arr[i] + "\", " + jsResource + ");");
			}
		}
	}

	/**
	 * 输出初始化的脚本.
	 */
	private void printInitScript(ViewAdapter view, AppData data, JspWriter out)
         throws IOException, ConfigurationException
	{
		out.println("<script language=\"javascript\">");
		out.println("(function() {");

		// 创建载入资源的方法
		out.println("if (typeof _pageInitializedURL == \"undefined\")");
		out.println("{");
		out.println("window._pageInitializedURL = {};");
		out.println("window._loadResource = function (url, jsResource)");
		out.println("{");
		out.println("if (window._pageInitializedURL[url])");
		out.println("{");
		out.println("return;");
		out.println("}");
		out.println("window._pageInitializedURL[url] = 1;");
		out.println("var resObj;");
		out.println("if (jsResource)");
		out.println("{");
		out.println("resObj = document.createElement(\"script\");");
		out.println("resObj.type = \"text/javascript\";");
		out.println("resObj.async = true;");
		out.println("resObj.src = url;");
		out.println("}");
		out.println("else");
		out.println("{");
		out.println("resObj = document.createElement(\"link\");");
		out.println("resObj.type = \"text/css\";");
		out.println("resObj.rel = \"stylesheet\";");
		out.println("resObj.href = url;");
		out.println("}");
		out.println("var s = document.getElementsByTagName('script')[0];");
		out.println("s.parentNode.insertBefore(resObj, s);");
		out.println("};");
		out.println("}"); // end if (typeof _pageInitializedURL == \"undefined\")

		this.printSourceList(out, true, this.jsList);
		this.printSourceList(out, false, this.cssList);
		out.println("var retryFind = false;");
		this.printEternaScript(view, data, out);

		// 如果定义了jQuery
		out.println("if (typeof jQuery != \"undefined\")");
		out.println("{");
		out.println("jQuery(document).ready(function(){");
		out.println("eCheckInitFn();");
		out.println("});");
		out.println("}");
		out.println("{");
		out.println("retryFind = true;");
		// 如果未定义jQuery, 则延迟200毫秒后再尝试
		out.println("setTimeout(eCheckInitFn, 200);");
		out.println("}");

		out.println("})();");
		out.println("</script>");
	}

	/**
	 * 打印Eterna的初始化脚本.
	 */
	private void printEternaScript(ViewAdapter view, AppData data, JspWriter out)
         throws IOException, ConfigurationException
	{
		// 定义初始化Eterna的方法
		out.println("var eInitFn = function ()");
		out.println("{");
		out.print("var $E = ");
		view.printView(out, data, this.getCacheMap(view));
		out.println(";");
		out.println("var eternaData = $E;");
		String debug = this.pageContext.getRequest().getParameter(PARAM_DEBUF_FLAG);
		if (debug == null)
		{
			debug = view.getDebug() + "";
		}
		out.println("var eterna_debug = " + debug + ";");
		out.println("var _eterna = new Eterna(eternaData, eterna_debug, null);");
		if (this.isUseAJAX())
		{
			out.println("_eterna.cache.useAJAX = true;");
		}
		out.println("if (retryFind)");
		out.println("{");
		out.println("_eterna.cache.retryFindCount = 5;");
		out.println("}");
		out.println("_eterna.reInit();");
		out.println("};");

		// 定义检查并初始化Eterna的方法
		out.println("var eCheckInitFn = function ()");
		out.println("{");
		out.println("if (typeof jQuery != \"undefined\" && typeof Eterna != \"undefined\")");
		out.println("{");
		out.println("eInitFn();");
		out.println("}");
		out.println("else");
		out.println("{");
		// 如果有需要的对象未生成, 则延迟200毫秒后再尝试
		out.println("setTimeout(eCheckInitFn, 200);");
		out.println("}");
		out.println("};");
	}

	/**
	 * 输出初始化的页面.
	 */
	private void printInitPage(ViewAdapter view, AppData data, JspWriter out)
         throws IOException, ConfigurationException
	{
		if (this.printHTML == PRINT_HTML_ALL)
		{
			String charset = this.charset == null ? "UTF-8" : this.charset;
			out.println("<html>");
			out.println("<head>");
			out.println("<meta http-equiv=\"content-type\" content=\"text/html; charset=" + charset + "\"/>");
			out.println("<meta http-equiv=\"pragma\" content=\"no-cache\"/>");
		}
		this.printInitScript(view, data, out);
		if (this.printHTML == PRINT_HTML_ALL)
		{
			out.println("</head>");
			out.println("<body>");
		}
		if (this.printHTML >= PRINT_HTML_PART)
		{
			String divName = this.getParentElement();
			String sId = this.getSuffixId();
			if (sId != null)
			{
				divName += sId;
			}
			String classDef = this.divClass == null ? "" : " class=\"" + this.divClass + "\"";
			out.println("<div id=\"" + divName + "\"" + classDef + "></div>");
		}
		if (this.printHTML == PRINT_HTML_ALL)
		{
			out.println("</body>");
			out.println("</html>");
		}
	}

   public void release()
   {
      this.view = null;
      this.appData = null;
      this.jsList = null;
      this.cssList = null;
		this.printHTML = 0;
		this.charset = null;
		this.divClass = null;
      super.release();
   }

	public String getView()
	{
		return this.view;
	}

	public void setView(String view)
	{
		this.view = view;
	}

	public String getAppData()
	{
		return this.appData;
	}

	public void setAppData(String appData)
	{
		this.appData = appData;
	}

	public String getJsList()
	{
		return this.jsList;
	}

	public void setJsList(String jsList)
	{
		this.jsList = jsList;
	}

	public String getCssList()
	{
		return this.cssList;
	}

	public void setCssList(String cssList)
	{
		this.cssList = cssList;
	}

	public int getPrintHTML()
	{
		return this.printHTML;
	}

	public void setPrintHTML(int printHTML)
	{
		this.printHTML = printHTML;
	}

	public String getCharset()
	{
		return this.charset;
	}

	public void setCharset(String charset)
	{
		this.charset = charset;
	}

	public String getDivClass()
	{
		return this.divClass;
	}

	public void setDivClass(String divClass)
	{
		this.divClass = divClass;
	}

}
