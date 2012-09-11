
package self.micromagic.util.ext;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Driver;
import java.io.PrintWriter;
import java.util.Properties;

import javax.sql.DataSource;

import self.micromagic.util.ext.SimpleConnection;
import self.micromagic.util.Utility;

/**
 * һ�����׵�����Դʵ��, ��ʹ�����ݿ����ӻ����, �رպ�ֱ���ͷ�.
 */
public class SimpleDataSource
      implements DataSource
{
   protected PrintWriter logWriter;
   protected int loginTimeout;

   protected String description;
   protected String url;
   protected String driverClass;
   protected String user;
   protected String password;
   protected boolean autoCommit = true;
   protected boolean autoCommitSetted;

   /**
    * ���ݿ��������.
    */
   protected Driver driver;

	/**
	 * Ĭ�ϵ���������.
	 */
	protected Properties defaultProperties;

   /**
    * ��ȡ���ݿ�����.
    */
   public Connection getConnection()
         throws SQLException
   {
      return this.getConnection(null, null);
   }

   /**
    * ����ָ�����û����������ȡ���ݿ�����.
    */
   public Connection getConnection(String username, String password)
         throws SQLException
   {
		if (this.url == null)
		{
			throw new SQLException("The connetion url hasn't setted!");
		}
		if (this.driverClass == null)
		{
			throw new SQLException("The connetion driverClass hasn't setted!");
		}
		if (this.driver == null)
		{
			try
			{
				this.driver = createDriver(this.driverClass);
			}
			catch (Exception ex)
			{
				throw new SQLException("open: " + ex);
			}
		}
		Connection conn;
		if (username == null && password == null)
		{
			conn = new SimpleConnection(this.autoCommitSetted,
					this.driver.connect(this.url, this.getDefaultProperties()));
		}
		else
		{
			Properties p = new Properties();
			p.setProperty("user", user);
			p.setProperty("password", password);
			conn = new SimpleConnection(this.autoCommitSetted, this.driver.connect(this.url, p));
		}
		conn.setAutoCommit(this.autoCommit);
		return conn;
   }

   /**
    * ���û�ȡ�����ݿ�����Ĭ���Ƿ���Ҫ�Զ��ύ.
    */
   public void setAutoCommit(boolean autoCommit)
   {
      this.autoCommitSetted = true;
      this.autoCommit = autoCommit;
   }

   /**
    * �����������ݿ�ʹ�õ�������.
    */
   public void setDriverClass(String driverClass)
   {
      this.driverClass = driverClass;
   }

   /**
    * �����������ݿ�ʹ�õ������ַ���.
    */
   public void setUrl(String url)
   {
      this.url = url;
      if (this.description == null)
      {
         this.description = url;
      }
   }

   /**
    * �����������ݿ�ʹ�õ��û���.
    */
   public void setUser(String user)
   {
      this.user = user;
   }

   /**
    * �����������ݿ�ʹ�õ�����.
    */
   public void setPassword(String password)
   {
      this.password = password;
   }

   /**
    * ��ȡ������Դ��˵��.
    */
   public String getDescription()
   {
      return this.description;
   }

   /**
    * ���öԴ�����Դ��˵��.
    */
   public void setDescription(String description)
   {
      this.description = description;
   }

   /**
    * ��ȡautoCommit�����Ƿ����ù�, <code>true</code>��ʾ���ù�.
    */
   public boolean isAutoCommitSetted()
   {
      return autoCommitSetted;
   }

	/**
	 * ���ݸ���������, �������ݿ��������.
	 */
   private static Driver createDriver(String className)
   		throws ClassNotFoundException, IllegalAccessException, InstantiationException
	{
		return (Driver) Utility.getContextClassLoader().loadClass(className).newInstance();
   }

	/**
	 * ��ȡĬ�ϵ���������.
	 */
   private Properties getDefaultProperties()
	{
		if (this.defaultProperties == null)
		{
			synchronized (this)
			{
				if (this.defaultProperties == null)
				{
					this.defaultProperties = new Properties();
					this.defaultProperties.setProperty("user", this.user);
					this.defaultProperties.setProperty("password", this.password);
				}
			}
		}
		return this.defaultProperties;
   }

   public PrintWriter getLogWriter()
         throws SQLException
   {
      return this.logWriter;
   }

   public void setLogWriter(PrintWriter out)
         throws SQLException
   {
      this.logWriter = out;
   }

   public void setLoginTimeout(int seconds)
         throws SQLException
   {
      this.loginTimeout = seconds;
   }

   public int getLoginTimeout()
         throws SQLException
   {
      return this.loginTimeout;
   }

}
