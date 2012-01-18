
package self.micromagic.app;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.logging.Log;
import self.micromagic.util.Utility;

public class CharsetFilter
      implements Filter, WebApp
{
   private String charset = "utf-8";

   public void init(FilterConfig filterConfig)
         throws ServletException
   {
      String temp = filterConfig.getInitParameter("charset");
      if (temp != null)
      {
         this.charset = temp;
         Utility.setProperty(Utility.CHARSET_TAG, this.charset);
      }
      else
      {
         temp = Utility.getProperty(Utility.CHARSET_TAG);
         if (temp != null && !temp.equals(this.charset))
         {
            this.charset = temp;
         }
      }
   }

   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
         throws IOException, ServletException
   {
      if (!this.charset.equals(request.getCharacterEncoding()))
      {
         request.setCharacterEncoding(this.charset);
      }
      chain.doFilter(request, response);
   }

   public void destroy()
   {
   }

}
