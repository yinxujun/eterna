
package self.micromagic.eterna.model.impl;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.model.Execute;
import self.micromagic.eterna.model.ModelAdapter;
import self.micromagic.eterna.model.ParamBind;
import self.micromagic.eterna.model.ParamSetManager;
import self.micromagic.eterna.share.EternaFactory;

public abstract class SQLExecute extends AbstractExecute
      implements Execute
{
   protected List binds = new LinkedList();
   protected boolean pushResult = true;
   protected int sqlCacheIndex = -1;
   protected boolean doExecute = true;
   protected EternaFactory factory;

   public void initialize(ModelAdapter model)
         throws ConfigurationException
   {
      if (this.initialized)
      {
         return;
      }
      super.initialize(model);
      this.factory = model.getFactory();
      Iterator itr = this.binds.iterator();
      while (itr.hasNext())
      {
         ((ParamBind) itr.next()).initialize(model, this);
      }
   }

   public boolean isPushResult()
   {
      return this.pushResult;
   }

   public void setPushResult(boolean push)
   {
      this.pushResult = push;
   }

   public void setCache(int cacheIndex)
   {
      this.sqlCacheIndex = cacheIndex;
   }

   public void setDoExecute(boolean execute)
   {
      this.doExecute = execute;
   }

   public void addParamBind(ParamBind bind)
   {
      this.binds.add(bind);
   }

   public int setParams(AppData data, ParamSetManager psm, int loopIndex)
         throws ConfigurationException, SQLException
   {
      int loopCount = -1;
      boolean hasLoop = false;
      Iterator itr = this.binds.iterator();
      while (itr.hasNext())
      {
         ParamBind bind = (ParamBind) itr.next();
         if (loopIndex == 0 || bind.isLoop())
         {
            int temp = bind.setParam(data, psm, loopIndex);
            if (bind.isLoop())
            {
               hasLoop = true;
            }

            if (temp != -1)
            {
               if (loopCount == -1)
               {
                  loopCount = temp;
               }
               if (loopCount != temp)
               {
                  throw new ConfigurationException("The param count not same, "
                        + loopCount + " and " + temp + ".");
               }
            }
         }
      }
      if (hasLoop)
      {
         loopCount = loopCount == -1 ? 0 : loopCount;
      }
      return loopCount;
   }

}
