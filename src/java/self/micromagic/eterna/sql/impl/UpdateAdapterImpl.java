
package self.micromagic.eterna.sql.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.dom4j.Element;
import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.model.AppDataLogExecute;
import self.micromagic.eterna.sql.UpdateAdapter;
import self.micromagic.eterna.sql.UpdateAdapterGenerator;
import self.micromagic.eterna.sql.SQLAdapter;

public class UpdateAdapterImpl extends SQLAdapterImpl
      implements UpdateAdapter, UpdateAdapterGenerator
{
   public String getType()
   {
      return SQL_TYPE_UPDATE;
   }

	public SQLAdapter createSQLAdapter()
			throws ConfigurationException
	{
		return this.createUpdateAdapter();
	}

   public UpdateAdapter createUpdateAdapter()
         throws ConfigurationException
   {
		UpdateAdapterImpl other = new UpdateAdapterImpl();
		this.copy(other);
		return other;
   }

   public void execute(Connection conn)
         throws ConfigurationException, SQLException
   {
      long startTime = System.currentTimeMillis();
      Statement stmt = null;
      Throwable exception = null;
      try
      {
         if (this.hasActiveParam())
         {
            PreparedStatement temp = conn.prepareStatement(this.getPreparedSQL());
            stmt = temp;
            this.prepareValues(temp);
            temp.execute();
         }
         else
         {
            stmt = conn.createStatement();
            stmt.execute(this.getPreparedSQL());
         }
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
         this.logSQL(System.currentTimeMillis() - startTime, exception, conn);
         if (stmt != null)
         {
            stmt.close();
         }
      }
   }

   public int executeUpdate(Connection conn)
         throws ConfigurationException, SQLException
   {
      long startTime = System.currentTimeMillis();
      Statement stmt = null;
      Throwable exception = null;
      int result = -1;
      try
      {
         if (this.hasActiveParam())
         {
            PreparedStatement temp = conn.prepareStatement(this.getPreparedSQL());
            stmt = temp;
            this.prepareValues(temp);
            result = temp.executeUpdate();
         }
         else
         {
            stmt = conn.createStatement();
            result = stmt.executeUpdate(this.getPreparedSQL());
         }
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
            if (result != -1 && AppData.getAppLogType() == 1)
            {
               Element nowNode = AppData.getCurrentData().getCurrentNode();
               if (nowNode != null)
               {
                  AppDataLogExecute.printObject(nowNode.addElement("result"), new Integer(result));
               }
            }
         }
         if (stmt != null)
         {
            stmt.close();
         }
      }
   }

}
