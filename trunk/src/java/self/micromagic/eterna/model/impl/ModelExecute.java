
package self.micromagic.eterna.model.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.io.IOException;

import self.micromagic.eterna.model.Execute;
import self.micromagic.eterna.model.ModelExport;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.model.ModelExecuteGenerator;
import self.micromagic.eterna.model.ModelAdapter;
import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.util.ObjectRef;

public class ModelExecute extends AbstractExecute
      implements Execute, ModelExecuteGenerator
{
   private String exportName = null;
   private boolean noJump = false;
   private ModelExport export = null;
   private int exeModelIndex = -1;
   private int transactionType = -1;

   public void initialize(ModelAdapter model)
         throws ConfigurationException
   {
      if (this.initialized)
      {
         return;
      }
      super.initialize(model);

      if (this.exportName != null)
      {
         this.export = model.getFactory().getModelExport(this.exportName);
         if (this.export == null)
         {
            log.warn("The Model Export [" + this.exportName + "] not found.");
         }
      }
      if (this.getName() != null)
      {
         this.exeModelIndex = model.getFactory().getModelAdapterId(this.getName());
      }

      if (this.exeModelIndex == -1 && this.export == null)
      {
         throw new ConfigurationException("Must give a ModelAdapter or ModelExport.");
      }
   }

   public String getExecuteType()
         throws ConfigurationException
   {
      return "model";
   }

   public void setNoJump(boolean noJump)
   {
      this.noJump = noJump;
   }

   public void setExportName(String name)
   {
      this.exportName = name;
   }

   public void setTransactionType(String tType)
         throws ConfigurationException
   {
      this.transactionType = ModelAdapterImpl.parseTransactionType(tType);
   }

   public ModelExport execute(AppData data, Connection conn)
         throws ConfigurationException, SQLException, IOException
   {
      if (this.exeModelIndex != -1)
      {
         EternaFactory f = this.getModelAdapter().getFactory();
         ObjectRef preConn = (ObjectRef) data.getSpcialData(ModelAdapter.MODEL_CACHE, ModelAdapter.PRE_CONN);
         ModelAdapter tmpModel = f.createModelAdapter(this.exeModelIndex);
         int tType = this.transactionType == -1 ? tmpModel.getTransactionType() : this.transactionType;
         if (this.noJump)
         {
            try
            {
               f.getModelCaller().callModel(data, tmpModel, this.export, tType, preConn);
            }
            catch (Throwable ex)
            {
               log.error("Error in model execute", ex);
            }
         }
         else
         {
            return f.getModelCaller().callModel(data, tmpModel, this.export, tType, preConn);
         }
      }
      return this.export;
   }

}
