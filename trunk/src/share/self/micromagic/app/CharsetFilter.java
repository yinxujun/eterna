
package self.micromagic.app;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import self.micromagic.util.Utility;

/**
 * ���ñ����ʽ���������͵Ĺ�����.
 *
 * �����õĲ�������:
 *
 * charset            ʹ�õ��ַ���, Ĭ��ֵΪ: UTF-8
 *
 * contentType        �������������, Ĭ��ֵΪ: text/html
 *
 * forceSet           ���Ѿ����ù������ʽʱ, �Ƿ�Ҫ��������
 *                    Ĭ��ֵΪ: false
 *
 */
public class CharsetFilter
      implements Filter, WebApp
{
   private String charset = "UTF-8";
	private String contentType = "text/html";
   private boolean forceSet = false;

   public void init(FilterConfig filterConfig)
         throws ServletException
   {
      String temp = filterConfig.getInitParameter("charset");
      if (temp != null)
      {
         this.charset = temp;
      }
      else
      {
			this.charset = getConfigCharset(this.charset);
      }
		temp = filterConfig.getInitParameter("contentType");
		if (temp != null)
		{
			this.contentType = temp;
		}
		temp = filterConfig.getInitParameter("forceSet");
		if (temp != null)
		{
			this.forceSet = temp.equalsIgnoreCase("true");
		}
   }

   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
         throws IOException, ServletException
   {
      if (this.forceSet || request.getCharacterEncoding() == null)
      {
         request.setCharacterEncoding(this.charset);
			if (this.contentType.startsWith("text/"))
			{
				response.setContentType(this.contentType + ";charset=" + this.charset);
			}
			else
			{
				response.setContentType(this.contentType);
			}
      }
      chain.doFilter(request, response);
   }

   public void destroy()
   {
   }

	/**
	 * �������л�ȡ�����ʽ.
	 *
	 * @param defaultValue  �������в����ڱ����ʽ����ʱ, ʹ�ô�Ĭ��ֵ
	 */
	public static String getConfigCharset(String defaultValue)
	{
      String charset = Utility.getProperty(Utility.CHARSET_TAG);
		return charset == null ? defaultValue : charset;
	}

}
