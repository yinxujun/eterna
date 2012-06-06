
package self.micromagic.app;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import self.micromagic.eterna.digester.FactoryManager;

/**
 * ��servlet��ʼ����ɺ�, ��ȫ�ֵĹ��̹�����ʵ�������
 * <code>SERVLET_CONTEXT</code>����.
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
