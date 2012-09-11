
package test;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import self.micromagic.app.EternaServlet;
import self.micromagic.eterna.share.EternaInitialize;
import self.micromagic.util.Utility;

public class Test extends EternaServlet 
		implements EternaInitialize
{
	/*
	 * ����h2���ݿ�����Ŀ¼��������.
	 */
	public static final String H2_BASE_DIR_FLAG = "h2.baseDir";
	
	public Test()
	{
	}

	@Override
	public void init(ServletConfig config) 
			throws ServletException
	{
		if (Utility.getProperty(H2_BASE_DIR_FLAG) == null)
		{
			// ��ʼ��h2���ݿ��ļ����ڵ�Ŀ¼
			String baseDir = config.getServletContext().getRealPath("/WEB-INF/db");
			Utility.setProperty(H2_BASE_DIR_FLAG, baseDir);
			System.out.println(H2_BASE_DIR_FLAG + ":" + baseDir);
		}
		super.init(config);
	}

	static long autoReloadTime()
	{
		// ��������ļ�����ʱ������Ϊ5��
		return 5000L;
	}

	static
	{
		// ע��H2�Զ�ע������ݿ�����, ����tomcat�Ͳ�����
		// clearReferencesJdbcʱ, �����ݿ�����δע���ľ���
		org.h2.Driver.unload();
	}

	private static final long serialVersionUID = 2481944292489365997L;

}
