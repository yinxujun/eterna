
package self.micromagic.app;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import self.micromagic.eterna.digester.FactoryManager;

/**
 * 在servlet初始化完成后, 向全局的工程管理器实例中添加
 * <code>SERVLET_CONTEXT</code>属性.
 *
 * @see FactoryManager#SERVLET_CONTEXT
 */
public class EternaServletContextListener
		implements ServletContextListener
{
	public void contextInitialized(ServletContextEvent sce)
	{
		FactoryManager.getGlobalFactoryManager().setAttribute(
				FactoryManager.SERVLET_CONTEXT, sce.getServletContext());
	}

	public void contextDestroyed(ServletContextEvent sce)
	{
	}

}