
package self.micromagic.app;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;

import self.micromagic.eterna.model.AppData;

/**
 * 用于生成AppData的过滤器.
 *
 * @author micromagic@sina.com
 */
public class EternaFilter
		implements Filter
{
	protected FilterConfig config;

	public void init(FilterConfig filterConfig)
	{
		this.config = filterConfig;
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException
	{
		AppData data = AppData.getCurrentData();
		if (request instanceof HttpServletRequest)
		{
			data.contextRoot = ((HttpServletRequest) request).getContextPath();
		}

		if (filterChain != null)
		{
			String oldModelName = data.modelName;
			ServletRequest oldRequest = data.request;
			ServletResponse oldResponse = data.response;
			FilterConfig oldConfig = data.filterConfig;
			data.request = request;
			data.response = response;
			data.filterConfig = this.config;
			data.position = AppData.POSITION_FILTER;
			data.export = null;
			try
			{
				this.beginChain(data);
				filterChain.doFilter(data.request, data.response);
			}
			finally
			{
				try
				{
					this.endChain(data);
				}
				catch (Throwable ex) {}
				data.export = null;
				data.request = oldRequest;
				data.response = oldResponse;
				data.filterConfig = oldConfig;
				data.modelName = oldModelName;
				if (oldRequest == null && oldResponse == null)
				{
					data.clearData();
				}
			}
		}
	}

	/**
	 * 开始调用过滤链表时会调用此方法.
	 */
	protected void beginChain(AppData data)
	{
	}

	/**
	 * 过滤链表调用完成后会调用此方法.
	 */
	protected void endChain(AppData data)
	{
	}

	public void destroy()
	{
	}

}