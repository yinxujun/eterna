
package self.micromagic.eterna.sql.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.sql.QueryAdapter;
import self.micromagic.eterna.sql.ResultIterator;
import self.micromagic.eterna.sql.ResultMetaData;
import self.micromagic.eterna.sql.ResultReaderManager;
import self.micromagic.eterna.sql.ResultRow;
import self.micromagic.eterna.sql.converter.BooleanConverter;
import self.micromagic.eterna.sql.converter.ByteConverter;
import self.micromagic.eterna.sql.converter.BytesConverter;
import self.micromagic.eterna.sql.converter.DateConverter;
import self.micromagic.eterna.sql.converter.DoubleConverter;
import self.micromagic.eterna.sql.converter.FloatConverter;
import self.micromagic.eterna.sql.converter.IntegerConverter;
import self.micromagic.eterna.sql.converter.LongConverter;
import self.micromagic.eterna.sql.converter.ShortConverter;
import self.micromagic.eterna.sql.converter.StringConverter;
import self.micromagic.eterna.sql.converter.TimeConverter;
import self.micromagic.eterna.sql.converter.TimestampConverter;
import self.micromagic.eterna.sql.converter.StreamConverter;
import self.micromagic.eterna.sql.converter.ReaderConverter;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.model.AppDataLogExecute;
import org.dom4j.Element;

public class QueryAdapterImpl extends AbstractQueryAdapter
      implements QueryAdapter
{
   static StringConverter strConvert = new StringConverter();
   static BooleanConverter boolConvert = new BooleanConverter();
   static ByteConverter byteConvert = new ByteConverter();
   static ShortConverter shortConvert = new ShortConverter();
   static IntegerConverter intConvert = new IntegerConverter();
   static LongConverter longConvert = new LongConverter();
   static FloatConverter floatConvert = new FloatConverter();
   static DoubleConverter doubleConvert = new DoubleConverter();
   static BytesConverter bytesConvert = new BytesConverter();
   static DateConverter dateConvert = new DateConverter();
   static TimeConverter timeConvert = new TimeConverter();
   static TimestampConverter timestampConvert = new TimestampConverter();
   static StreamConverter streamConvert = new StreamConverter();
   static ReaderConverter readerConvert = new ReaderConverter();

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
         if (this.logSQL("query", System.currentTimeMillis() - startTime, exception, conn))
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

   protected ResultRow readResults(List readerList, ResultSet rs,
         ResultIterator resultIterator)
         throws ConfigurationException, SQLException
   {
      ResultRowImpl rowSet = new ResultRowImpl(this.getResults(readerList, rs),
            resultIterator, this.getReaderManager0(null), this.getPermission0());
      return rowSet;
   }

   private class ResultSetIteratorImpl extends AbstractResultSetIterator
   {
      private ResultReaderManager readerManager;
      private List readerList;

      public ResultSetIteratorImpl(Connection conn, Statement stmt, ResultSet rs,
            ResultReaderManager readerManager, List readerList)
      {
         super(conn, stmt, rs);
         this.readerManager = readerManager;
         this.readerList = readerList;
      }

      public ResultMetaData getMetaData()
      {
         return new ResultMetaDataImpl(this.readerList, QueryAdapterImpl.this);
      }

      protected ResultRow getResultRow(ResultSet rs)
            throws SQLException
      {
         Object[] values;
         try
         {
            values = QueryAdapterImpl.this.getResults(this.readerList, rs);
         }
         catch (ConfigurationException ex)
         {
            throw new SQLException(ex.getMessage());
         }
         ResultRowImpl rowSet = new ResultRowImpl(values, this, this.readerManager,
               QueryAdapterImpl.this.getPermission0());
         return rowSet;
      }
   }

}
