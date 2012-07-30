
package self.micromagic.eterna.tag;

import java.io.IOException;

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

public class EternaRes extends TagSupport
{
   /**
    * 在request范围内, 存放载入资源方法已初始化的名称.
    */
   public static final String LOAD_RES_INITED_FLAG = "___loadResInited";

   private String url;
	private String charset;
   private boolean jsResource = true;

   public int doStartTag()
         throws JspException
   {
      try
      {
			JspWriter out = this.pageContext.getOut();
			out.println("<script language=\"javascript\">");
			String inited = (String) this.pageContext.getAttribute(
					LOAD_RES_INITED_FLAG, PageContext.REQUEST_SCOPE);
			if (inited == null)
			{
				this.pageContext.setAttribute(LOAD_RES_INITED_FLAG, "1", PageContext.REQUEST_SCOPE);
				this.printLoadResScript(out);
			}
			String charsetDef = this.charset == null ? "" : ", \"" + this.charset + "\"";
			String tmpURL = this.url;
			if (tmpURL.startsWith("/"))
			{
				ServletRequest req = this.pageContext.getRequest();
				if (req instanceof HttpServletRequest)
				{
					tmpURL = ((HttpServletRequest) req).getContextPath() + this.url;
				}
			}
			String params = this.jsResource + ", \"" + tmpURL + "\"" + charsetDef;
			out.println( "window._loadResource(" + params + ");");
			out.println("</script>");
		}
      catch (Throwable ex)
      {
         DefaultFinder.log.error("Other Error in service.", ex);
      }
      return SKIP_BODY;
   }

	protected void printLoadResScript(JspWriter out)
			throws IOException
	{
		// 创建载入资源的方法
		out.println("if (typeof _pageInitializedURL == \"undefined\")");
		out.println('{');
		out.println("window._pageInitializedURL = {};");
		out.println("window._loadResource = function (jsResource, url, charset)");
		out.println('{');
		out.println("if (window._pageInitializedURL[url])");
		out.println('{');
		out.println("return;");
		out.println('}');
		out.println("window._pageInitializedURL[url] = 1;");
		out.println("var resObj;");
		out.println("if (jsResource)");
		out.println('{');
		out.println("resObj = document.createElement(\"script\");");
		out.println("resObj.type = \"text/javascript\";");
		out.println("resObj.async = true;");
		out.println("resObj.src = url;");
		out.println('}');
		out.println("else");
		out.println('{');
		out.println("resObj = document.createElement(\"link\");");
		out.println("resObj.type = \"text/css\";");
		out.println("resObj.rel = \"stylesheet\";");
		out.println("resObj.href = url;");
		out.println('}');
		out.println("if (charset != null)");
		out.println('{');
		out.println("resObj.charset = charset;");
		out.println('}');
		out.println("var s = document.getElementsByTagName('script')[0];");
		out.println("s.parentNode.insertBefore(resObj, s);");
		out.println("};");
		out.println('}'); // end if (typeof _pageInitializedURL == \"undefined\")
	}

   public void release()
   {
      this.url = null;
      this.charset = null;
		this.jsResource = true;
      super.release();
   }

	public boolean isJsResource()
	{
		return this.jsResource;
	}

	public void setJsResource(boolean jsResource)
	{
		this.jsResource = jsResource;
	}

	public String getUrl()
	{
		return this.url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public String getCharset()
	{
		return this.charset;
	}

	public void setCharset(String charset)
	{
		this.charset = charset;
	}

}
