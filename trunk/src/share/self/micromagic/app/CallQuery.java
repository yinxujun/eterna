
package self.micromagic.app;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Element;
import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.model.AppDataLogExecute;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.share.TypeManager;
import self.micromagic.eterna.sql.ResultIterator;
import self.micromagic.eterna.sql.ResultReader;
import self.micromagic.eterna.sql.ResultReaderManager;
import self.micromagic.eterna.sql.QueryAdapter;
import self.micromagic.eterna.sql.impl.QueryAdapterImpl;
import self.micromagic.eterna.sql.preparer.PreparedStatementWrapImpl;

public class CallQuery extends QueryAdapterImpl
{
	public QueryAdapter createQueryAdapter()
			throws ConfigurationException
	{
		CallQuery other = new CallQuery();
		this.copy(other);
		return other;
	}

   public void initialize(EternaFactory factory)
         throws ConfigurationException
   {
      if (this.initialized)
      {
         return;
      }
      super.initialize(factory);
      ResultReaderManager rm = this.getReaderManager0(null);
      List readerList = rm.getReaderList();
      Iterator itr = readerList.iterator();
      while (itr.hasNext())
      {
         ResultReader reader = (ResultReader) itr.next();
         if (!reader.isUseColumnName())
         {
            throw new ConfigurationException("You can't use colIndex in reader for CallQuery.");
         }
         try
         {
            this.getParameter(reader.getColumnName());
         }
         catch (ConfigurationException ex)
         {
            throw new ConfigurationException("The reader[" + reader.getName()
                  + "]'s colName must in parameters.");
         }
      }
   }

	public String getType()
	{
		return "call";
	}

   public ResultIterator executeQueryHoldConnection(Connection conn)
         throws ConfigurationException, SQLException
   {
      throw new ConfigurationException("You can't use executeQueryHoldConnection in CallQuery.");
   }

   public ResultIterator executeQuery(Connection conn)
         throws ConfigurationException, SQLException
   {
      long startTime = System.currentTimeMillis();
      Statement stmt = null;
      Throwable exception = null;
      ResultIterator result = null;
      try
      {
         CallableStatement call = conn.prepareCall(this.getPreparedSQL());
         stmt = call;

         int[] indexs = new int[this.getParameterCount()];
         this.getPreparerManager().prepareValues(new PreparedStatementWrapImpl(call), indexs);
         ResultReaderManager rm = this.getReaderManager0(null);
         List readerList = rm.getReaderList();
         Iterator itr = readerList.iterator();
         while (itr.hasNext())
         {
            ResultReader reader = (ResultReader) itr.next();
            int index = indexs[this.getParameter(reader.getColumnName()).getIndex() - 1];
            if (index != -1)
            {
               call.registerOutParameter(index, TypeManager.getSQLType(reader.getType()));
            }
         }
         call.execute();

         self.micromagic.util.CustomResultIterator critr
               = new self.micromagic.util.CustomResultIterator(rm, this.getPermission0());
         if (readerList.size() > 0)
         {
            Object[] values = new Object[readerList.size()];
            itr = readerList.iterator();
            for (int i = 0; i < values.length; i++)
            {
               ResultReader reader = (ResultReader) itr.next();
               int index = indexs[this.getParameter(reader.getColumnName()).getIndex() - 1];
               if (index != -1)
               {
                  values[i] = reader.readCall(call, index);
               }
            }
            critr.createRow(values);
         }
         critr.finishCreateRow();
         result = critr;
         return critr;
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
         if (stmt != null)
         {
            stmt.close();
         }
      }
   }

   public void execute(Connection conn)
         throws ConfigurationException, SQLException
   {
      this.executeQuery(conn);
   }

}
