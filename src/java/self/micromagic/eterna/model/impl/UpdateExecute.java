
package self.micromagic.eterna.model.impl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.model.Execute;
import self.micromagic.eterna.model.UpdateExecuteGenerator;
import self.micromagic.eterna.model.ModelExport;
import self.micromagic.eterna.model.ParamSetManager;
import self.micromagic.eterna.model.ModelAdapter;
import self.micromagic.eterna.sql.UpdateAdapter;
import org.dom4j.Element;

public class UpdateExecute extends SQLExecute
      implements Execute, UpdateExecuteGenerator
{
   protected boolean multiType = false;
   protected int updateAdapterIndex;

   public void initialize(ModelAdapter model)
         throws ConfigurationException
   {
      if (this.initialized)
      {
         return;
      }
      super.initialize(model);

      this.updateAdapterIndex = this.getModelAdapter().getFactory().getUpdateAdapterId(this.getName());
   }

   public String getExecuteType()
   {
      return "update";
   }

   public void setMultiType(boolean multi)
   {
      this.multiType = multi;
   }

   public ModelExport execute(AppData data, Connection conn)
         throws ConfigurationException, SQLException, IOException
   {
      boolean inCache = false;
      UpdateAdapter update = null;
      if (this.sqlCacheIndex != -1)
      {
         Object temp = data.caches[this.sqlCacheIndex];
         if (temp instanceof UpdateAdapter)
         {
            update = (UpdateAdapter) temp;
            if (!update.getName().equals(this.getName()))
            {
               update = null;
            }
            else
            {
               inCache = true;
            }
         }
      }
      if (update == null)
      {
         update = this.getModelAdapter().getFactory().createUpdateAdapter(this.updateAdapterIndex);
         if (this.sqlCacheIndex != -1)
         {
            data.caches[this.sqlCacheIndex] = update;
         }
      }

      if (AppData.getAppLogType() != 0)
      {
         Element nowNode = data.getCurrentNode();
         nowNode.addAttribute("updateName", update.getName());
         if (this.sqlCacheIndex != -1)
         {
            nowNode.addAttribute("sqlCacheIndex", String.valueOf(this.sqlCacheIndex));
            if (inCache)
            {
               nowNode.addAttribute("inCache", "true");
            }
         }
         if (!this.doExecute)
         {
            nowNode.addAttribute("doExecute", String.valueOf(this.doExecute));
         }
      }
      ParamSetManager psm = new ParamSetManager(update);
      int count = this.setParams(data, psm, 0);
      if (this.doExecute)
      {
         if (count != 0)
         {
            if (this.multiType)
            {
               update.execute(conn);
               for (int i = 1; i < count; i++)
               {
                  this.setParams(data, psm, i);
                  update.execute(conn);
               }
               if (this.pushResult)
               {
                  data.push(null);
               }
            }
            else
            {
               int result = update.executeUpdate(conn);
               int[] results = null;
               if (this.pushResult)
               {
                  results = count > 1 ? new int[count] : new int[1];
                  results[0] = result;
                  data.push(results);
               }
               for (int i = 1; i < count; i++)
               {
                  this.setParams(data, psm, i);
                  result = update.executeUpdate(conn);
                  if (this.pushResult)
                  {
                     results[i] = result;
                  }
               }
            }
         }
         else if (this.pushResult)
         {
            data.push(new int[0]);
         }
      }
      return null;
   }

}
