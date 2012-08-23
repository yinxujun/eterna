
package self.micromagic.eterna.sql.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.logging.Log;
import self.micromagic.eterna.sql.ResultIterator;
import self.micromagic.eterna.sql.ResultRow;

public abstract class AbstractResultSetIterator
      implements ResultIterator, Runnable
{
   protected static final Log log = SQLManager.log;

   private int idleTime = 0;
   private Connection conn;
   private Statement stmt;
   private ResultSet resultSet;
   protected List preFetchList = null;
   protected ResultRow currentRow = null;

   private boolean hasNext = false;
   private boolean isMovedNext = false;
   private boolean closed = false;

   public AbstractResultSetIterator(Connection conn, Statement stmt, ResultSet rs)
   {
      this.conn = conn;
      this.stmt = stmt;
      this.resultSet = rs;
      Thread t = new Thread(this);
      t.start();
   }

   private void moveToNext()
         throws SQLException
   {
      if (this.isMovedNext)
      {
         return;
      }
      this.isMovedNext = true;
      this.hasNext = this.resultSet.next();
   }

   public int getRealRecordCount() throws SQLException
   {
      return -1;
   }

   public int getRecordCount() throws SQLException
   {
      return -1;
   }

   public boolean isRealRecordCountAvailable() throws SQLException
   {
      return false;
   }

   public boolean isHasMoreRecord() throws SQLException
   {
      return false;
   }

   public boolean hasMoreRow()
         throws SQLException
   {
      this.idleTime = 0;
      if (this.preFetchList != null && this.preFetchList.size() > 0)
      {
         return true;
      }
      this.moveToNext();
      return this.hasNext;
   }

   private boolean hasMoreRow0()
         throws SQLException
   {
      this.idleTime = 0;
      this.moveToNext();
      return this.hasNext;
   }

   public ResultRow preFetch()
         throws SQLException
   {
      return this.preFetch(1);
   }

   public ResultRow preFetch(int index)
         throws SQLException
   {
      this.idleTime = 0;
      if (this.preFetchList != null && this.preFetchList.size() >= index)
      {
         return (ResultRow) this.preFetchList.get(index - 1);
      }
      if (this.preFetchList == null)
      {
         this.preFetchList = new LinkedList();
      }
      for (int i = this.preFetchList.size(); i < index; i++)
      {
         if (this.hasMoreRow0())
         {
            this.isMovedNext = false;
            this.preFetchList.add(this.getResultRow(this.resultSet));
         }
         else
         {
            return null;
         }
      }
      return (ResultRow) this.preFetchList.get(index - 1);
   }

   public ResultRow getCurrentRow()
   {
      return this.currentRow;
   }

   public ResultRow nextRow()
         throws SQLException
   {
      this.idleTime = 0;
      if (this.preFetchList != null && this.preFetchList.size() > 0)
      {
         this.currentRow  = (ResultRow) this.preFetchList.remove(0);
         return this.currentRow;
      }
      if (this.hasMoreRow())
      {
         this.isMovedNext = false;
         this.currentRow = this.getResultRow(this.resultSet);
         return this.currentRow;
      }
      throw new NoSuchElementException();
   }

   protected abstract ResultRow getResultRow(ResultSet rs) throws SQLException;


   public boolean beforeFirst()
   {
      try
      {
         this.resultSet.beforeFirst();
         this.currentRow = null;
         this.preFetchList = null;
         return true;
      }
      catch (SQLException ex)
      {
         return false;
      }
   }

   public void close()
         throws SQLException
   {
      if (this.closed)
      {
         return;
      }
      this.resultSet.close();
      this.stmt.close();
      this.conn.close();
      if (log.isDebugEnabled())
      {
         log.debug("I am closed [" + this.hashCode() + "].");
      }
      this.closed = true;
   }

   public ResultIterator copy()
   {
      return null;
   }

   public boolean hasNext()
   {
      try
      {
         return this.hasMoreRow();
      }
      catch (SQLException ex)
      {
         return false;
      }
   }

   public Object next()
   {
      try
      {
         return this.nextRow();
      }
      catch (SQLException ex)
      {
         throw new NoSuchElementException();
      }
   }

   public void remove()
   {
      throw new UnsupportedOperationException();
   }

   public void run()
   {
      while (!this.closed)
      {
         try
         {
            Thread.sleep(100);
         }
         catch (InterruptedException ex) {}
         this.idleTime += 100;
         if (this.idleTime > 30000)
         {
            // 如果30妙后仍未有操作, 则退出循环, 关闭数据库链接
            break;
         }
      }
      try
      {
         this.close();
      }
      catch (SQLException e) {}
   }

}
