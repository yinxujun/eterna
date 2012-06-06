
package self.micromagic.util.ext;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.CallableStatement;
import java.sql.DatabaseMetaData;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.util.Map;

/**
 * һ�����׵����ݿ����Ӹ�����, ��û������autoCommit����ʱ, �����
 * ��setAutoCommit commit rollback��Щ�����ĵ���. ��Ϊ��һЩ��֧��
 * ��������ݿ�����, ������Щ����ʱ�����, ���������ε�, ��ʹ��ʱ
 * �����迼��������صķ����Ƿ�ɵ�����.
 */
public class SimpleConnection
      implements Connection
{
   private Connection oldConn;
   private boolean autoCommitSetted;

   public SimpleConnection(boolean autoCommitSetted, Connection oldConn)
   {
      this.autoCommitSetted = autoCommitSetted;
      this.oldConn = oldConn;
   }

   public Statement createStatement()
         throws SQLException
   {
      return this.oldConn.createStatement();
   }

   public PreparedStatement prepareStatement(String sql)
         throws SQLException
   {
      return this.oldConn.prepareStatement(sql);
   }

   public CallableStatement prepareCall(String sql)
         throws SQLException
   {
      return this.oldConn.prepareCall(sql);
   }

   public String nativeSQL(String sql)
         throws SQLException
   {
      return this.oldConn.nativeSQL(sql);
   }

   public void setAutoCommit(boolean autoCommit)
         throws SQLException
   {
      if (this.autoCommitSetted)
      {
         this.oldConn.setAutoCommit(autoCommit);
      }
   }

   public boolean getAutoCommit()
         throws SQLException
   {
      if (this.autoCommitSetted)
      {
         this.oldConn.getAutoCommit();
      }
      return true;
   }

   public void commit()
         throws SQLException
   {
      if (this.autoCommitSetted)
      {
         this.oldConn.commit();
      }
   }

   public void rollback()
         throws SQLException
   {
      if (this.autoCommitSetted)
      {
         this.oldConn.rollback();
      }
   }

   public void close()
         throws SQLException
   {
      this.oldConn.close();
   }

   public boolean isClosed()
         throws SQLException
   {
      return this.oldConn.isClosed();
   }

   public DatabaseMetaData getMetaData()
         throws SQLException
   {
      return this.oldConn.getMetaData();
   }

   public void setReadOnly(boolean readOnly)
         throws SQLException
   {
      this.oldConn.setReadOnly(readOnly);
   }

   public boolean isReadOnly()
         throws SQLException
   {
      return this.oldConn.isReadOnly();
   }

   public void setCatalog(String catalog)
         throws SQLException
   {
      this.oldConn.setCatalog(catalog);
   }

   public String getCatalog()
         throws SQLException
   {
      return this.oldConn.getCatalog();
   }

   public void setTransactionIsolation(int level)
         throws SQLException
   {
      if (this.autoCommitSetted)
      {
         this.oldConn.setTransactionIsolation(level);
      }
   }

   public int getTransactionIsolation()
         throws SQLException
   {
      return this.oldConn.getTransactionIsolation();
   }

   public SQLWarning getWarnings()
         throws SQLException
   {
      return this.oldConn.getWarnings();
   }

   public void clearWarnings()
         throws SQLException
   {
      this.oldConn.clearWarnings();
   }

   public Statement createStatement(int resultSetType, int resultSetConcurrency)
         throws SQLException
   {
      return this.oldConn.createStatement(resultSetType, resultSetConcurrency);
   }

   public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
         throws SQLException
   {
      return this.oldConn.prepareStatement(sql, resultSetType, resultSetConcurrency);
   }

   public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)
         throws SQLException
   {
      return this.oldConn.prepareCall(sql, resultSetType, resultSetConcurrency);
   }

   public Map getTypeMap()
         throws SQLException
   {
      return this.oldConn.getTypeMap();
   }

   public void setTypeMap(Map map)
         throws SQLException
   {
      this.oldConn.setTypeMap(map);
   }

   public void setHoldability(int holdability)
         throws SQLException
   {
      this.oldConn.setHoldability(holdability);
   }

   public int getHoldability()
         throws SQLException
   {
      return this.oldConn.getHoldability();
   }

   public Savepoint setSavepoint()
         throws SQLException
   {
      if (this.autoCommitSetted)
      {
         return this.oldConn.setSavepoint();
      }
      return null;
   }

   public Savepoint setSavepoint(String name)
         throws SQLException
   {
      if (this.autoCommitSetted)
      {
         return this.oldConn.setSavepoint(name);
      }
      return null;
   }

   public void rollback(Savepoint savepoint)
         throws SQLException
   {
      if (this.autoCommitSetted)
      {
         this.oldConn.rollback(savepoint);
      }
   }

   public void releaseSavepoint(Savepoint savepoint)
         throws SQLException
   {
      if (this.autoCommitSetted)
      {
         this.oldConn.releaseSavepoint(savepoint);
      }
   }

   public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
         throws SQLException
   {
      return this.oldConn.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
   }

   public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
         int resultSetHoldability)
         throws SQLException
   {
      return this.oldConn.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
   }

   public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
         int resultSetHoldability)
         throws SQLException
   {
      return this.oldConn.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
   }

   public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
         throws SQLException
   {
      return this.oldConn.prepareStatement(sql, autoGeneratedKeys);
   }

   public PreparedStatement prepareStatement(String sql, int columnIndexes[])
         throws SQLException
   {
      return this.oldConn.prepareStatement(sql, columnIndexes);
   }

   public PreparedStatement prepareStatement(String sql, String columnNames[])
         throws SQLException
   {
      return this.oldConn.prepareStatement(sql, columnNames);
   }

}
