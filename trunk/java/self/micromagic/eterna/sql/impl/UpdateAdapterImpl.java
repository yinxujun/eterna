
package self.micromagic.eterna.sql.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.sql.UpdateAdapter;
import self.micromagic.eterna.sql.UpdateAdapterGenerator;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.model.AppDataLogExecute;
import self.micromagic.util.Utility;
import org.dom4j.Element;

public class UpdateAdapterImpl extends SQLAdapterImpl
      implements UpdateAdapter, UpdateAdapterGenerator
{
   public UpdateAdapter createUpdateAdapter()
         throws ConfigurationException
   {
      return (UpdateAdapter) this.create();
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
         this.logSQL("update", System.currentTimeMillis() - startTime, exception, conn);
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
         if (this.logSQL("update", System.currentTimeMillis() - startTime, exception, conn))
         {
            if (result != -1 && AppData.getAppLogType() == 1)
            {
               Element nowNode = AppData.getCurrentData().getCurrentNode();
               if (nowNode != null)
               {
                  AppDataLogExecute.printObject(nowNode.addElement("result"), Utility.createInteger(result));
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
