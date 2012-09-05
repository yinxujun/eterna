
package self.micromagic.eterna.tag;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import self.micromagic.app.WebApp;
import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.view.ViewAdapter;

/**
 * ��JSP��, ��ͨ���˱�ǩ��ҳ���г�ʼ��Eterna����.
 */
public class EternaInit extends InitBaseTag
{
	/**
	 * �����html����
	 */
	public static final int PRINT_HTML_NONE = 0;

	/**
	 * �����eterna���������div����
	 */
	public static final int PRINT_HTML_PART = 1;

	/**
	 * ���������htmlҳ�����
	 */
	public static final int PRINT_HTML_ALL = 2;

	/**
	 * �����п���debug�Ĳ�����
	 */
	public static final String PARAM_DEBUF_FLAG = "___debug";

   private String view;
   private String appData;
	private int printHTML;
	private String charset;
	private String divClass;
	private boolean includeBody = false;

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
				this.includeBody = true;
				this.printInitPage(view, data, out);
				return EVAL_BODY_INCLUDE;
			}
			else
			{
				this.includeBody = false;
				view.printView(out, data, this.getCacheMap(view));
			}
      }
      catch (ConfigurationException ex)
      {
         DefaultFinder.log.warn("Error in init.", ex);
      }
      catch (Throwable ex)
      {
         DefaultFinder.log.error("Other Error in init.", ex);
      }
      return SKIP_BODY;
   }

	public int doEndTag()
			throws JspException
	{
		if (!this.includeBody)
		{
			return EVAL_PAGE;
		}
      try
      {
			JspWriter out = this.pageContext.getOut();
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
      catch (Throwable ex)
      {
         DefaultFinder.log.error("Other Error in init.", ex);
      }
		return EVAL_PAGE;
	}

	/**
	 * �����ʼ���Ľű�.
	 */
	private void printInitScript(ViewAdapter view, AppData data, JspWriter out)
         throws IOException, ConfigurationException
	{
		out.println("<script type=\"text/javascript\">");
		out.println("(function() {");
		out.println("var retryFind = false;");
		this.printEternaScript(view, data, out);

		// ���������jQuery
		out.println("if (typeof jQuery != \"undefined\")");
		out.println('{');
		out.println("jQuery(document).ready(function(){");
		out.println("eCheckInitFn();");
		out.println("});");
		out.println('}');
		out.println('{');
		out.println("retryFind = true;");
		// ���δ����jQuery, ���ӳ�200������ٳ���
		out.println("setTimeout(eCheckInitFn, 200);");
		out.println('}');

		out.println("})();");
		out.println("</script>");
	}

	/**
	 * ��ӡEterna�ĳ�ʼ���ű�.
	 */
	private void printEternaScript(ViewAdapter view, AppData data, JspWriter out)
         throws IOException, ConfigurationException
	{
		// �����ʼ��Eterna�ķ���
		out.println("var eInitFn = function ()");
		out.println('{');
		out.print("var $E = ");
		view.printView(out, data, this.getCacheMap(view));
		out.println(';');
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
		out.println('{');
		out.println("_eterna.cache.retryFindCount = 5;");
		out.println('}');
		out.println("_eterna.reInit();");
		out.println("};");

		// �����鲢��ʼ��Eterna�ķ���
		out.println("var eCheckInitFn = function ()");
		out.println('{');
		out.println("if (typeof jQuery != \"undefined\" && typeof Eterna != \"undefined\")");
		out.println('{');
		out.println("eInitFn();");
		out.println('}');
		out.println("else");
		out.println('{');
		// �������Ҫ�Ķ���δ����, ���ӳ�200������ٳ���
		out.println("setTimeout(eCheckInitFn, 200);");
		out.println('}');
		out.println("};");
	}

	/**
	 * �����ʼ����ҳ��.
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
	}

   public void release()
   {
      this.view = null;
      this.appData = null;
		this.printHTML = 0;
		this.charset = null;
		this.divClass = null;
		this.includeBody = false;
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
