
package self.micromagic.util.ext;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.io.PrintWriter;

import javax.sql.DataSource;

import self.micromagic.util.ext.SimpleConnection;

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
   protected String user = null;
   protected String password = null;
   protected boolean autoCommit = true;
   protected boolean autoCommitSetted = false;

   /**
    * ��ȡ���ݿ�����.
    */
   public Connection getConnection()
         throws SQLException
   {
      return this.getConnection(this.user, this.password);
   }

   /**
    * ����ָ�����û����������ȡ���ݿ�����.
    */
   public Connection getConnection(String username, String password)
         throws SQLException
   {
      try
      {
         if (this.url == null)
         {
            throw new SQLException("The connetion url hasn't setted!");
         }
         if (this.driverClass == null)
         {
            throw new SQLException("The connetion driverClass hasn't setted!");
         }
         Class.forName(this.driverClass);
         Connection conn;
         if (username == null && password == null)
         {
            conn = new SimpleConnection(this.autoCommitSetted, DriverManager.getConnection(this.url));
         }
         else
         {
            conn = new SimpleConnection(this.autoCommitSetted,
                  DriverManager.getConnection(this.url, username, password));
         }
         conn.setAutoCommit(this.autoCommit);
         return conn;
      }
      catch (ClassNotFoundException ex)
      {
         throw new SQLException(ex.getMessage());
      }
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
