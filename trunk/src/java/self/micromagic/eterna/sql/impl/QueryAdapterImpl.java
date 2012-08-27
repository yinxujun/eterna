
package self.micromagic.eterna.sql.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.dom4j.Element;
import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.model.AppDataLogExecute;
import self.micromagic.eterna.model.ModelAdapter;
import self.micromagic.eterna.sql.QueryAdapter;
import self.micromagic.eterna.sql.ResultIterator;
import self.micromagic.eterna.sql.ResultMetaData;
import self.micromagic.eterna.sql.ResultReaderManager;
import self.micromagic.eterna.sql.ResultRow;

public class QueryAdapterImpl extends AbstractQueryAdapter
      implements QueryAdapter
{
	public QueryAdapter createQueryAdapter()
			throws ConfigurationException
	{
		QueryAdapterImpl other = new QueryAdapterImpl();
		this.copy(other);
		return other;
	}

   public ResultIterator executeQueryHoldConnection(Connection conn)
         throws ConfigurationException, SQLException
   {
      long startTime = System.currentTimeMillis();
      Statement stmt = null;
      Throwable exception = null;
      ResultIterator result = null;
      try
      {
         ResultSet rs;
         if (this.hasActiveParam())
         {
            PreparedStatement temp;
            if (this.isForwardOnly())
            {
               temp = conn.prepareStatement(this.getPreparedSQL());
            }
            else
            {
               temp = conn.prepareStatement(this.getPreparedSQL(),
                     ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            }
            stmt = temp;
            this.prepareValues(temp);
            rs = temp.executeQuery();
         }
         else
         {
            if (this.isForwardOnly())
            {
               stmt = conn.createStatement();
            }
            else
            {
               stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                     ResultSet.CONCUR_READ_ONLY);
            }
            rs = stmt.executeQuery(this.getPreparedSQL());
         }
         List readerList = this.getReaderManager0(rs).getReaderList(this.getPermission0());
         result = new ResultSetIteratorImpl(conn, stmt, rs, this.getReaderManager0(null), readerList);
         // 查询执行完成, 表示已接管了数据库链接的控制, 可以设置链接接管标志
         AppData.getCurrentData().addSpcialData(ModelAdapter.MODEL_CACHE, ModelAdapter.CONN_HOLDED, "1");
         return result;
      }
      catch (ConfigurationException ex)
      {
         exception = ex;
         throw ex;
      }
      catch (SQLException ex)
      {
         exception = ex;
         throw ex;
      }
      catch (RuntimeException ex)
      {
         exception = ex;
         throw ex;
      }
      catch (Error ex)
      {
         exception = ex;
         throw ex;
      }
      finally
      {
         if (this.logSQL(System.currentTimeMillis() - startTime, exception, conn))
         {
            if (result != null && AppData.getAppLogType() == 1)
            {
               Element nowNode = AppData.getCurrentData().getCurrentNode();
               if (nowNode != null)
               {
                  AppDataLogExecute.printObject(nowNode.addElement("result"), result);
               }
            }
         }
         // 这里需要保持连接，所以stmt不关闭
      }
   }

   protected ResultRow readResults(ResultReaderManager readerManager, Object[] row,
			ResultIterator resultIterator)
         throws ConfigurationException, SQLException
   {
      ResultRowImpl rowSet = new ResultRowImpl(row, resultIterator, this.getPermission0());
      return rowSet;
   }

   private class ResultSetIteratorImpl extends AbstractResultSetIterator
   {
      private ResultReaderManager readerManager;
      private List readerList;
      private ResultMetaData metaData = null;

      public ResultSetIteratorImpl(Connection conn, Statement stmt, ResultSet rs,
            ResultReaderManager readerManager, List readerList)
      {
         super(conn, stmt, rs);
         this.readerManager = readerManager;
         this.readerList = readerList;
      }

      public ResultMetaData getMetaData()
				throws ConfigurationException
		{
			if (this.metaData == null)
			{
         	this.metaData = new ResultMetaDataImpl(
						this.readerList, this.readerManager, QueryAdapterImpl.this);
			}
			return this.metaData;
      }

      protected ResultRow getResultRow(ResultSet rs)
            throws SQLException
      {
         Object[] values;
         try
         {
            values = getResults(QueryAdapterImpl.this, this.readerList, rs);
				return new ResultRowImpl(values, this, QueryAdapterImpl.this.getPermission0());
         }
         catch (ConfigurationException ex)
         {
				log.error("Error in get results.", ex);
            throw new SQLException(ex.getMessage());
         }
      }

   }

}
