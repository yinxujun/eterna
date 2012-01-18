
package self.micromagic.eterna.model.impl;

import self.micromagic.eterna.share.Generator;
import self.micromagic.eterna.share.AbstractGenerator;
import self.micromagic.eterna.model.Execute;
import self.micromagic.eterna.model.ModelAdapter;
import self.micromagic.eterna.digester.ConfigurationException;

public abstract class AbstractExecute extends AbstractGenerator
      implements Execute, Generator
{
   protected ModelAdapter model;
   protected boolean initialized = false;

   public void initialize(ModelAdapter model)
         throws ConfigurationException
   {
      this.initialized = true;
      this.model = model;
   }

   public Object create()
         throws ConfigurationException
   {
      return this.createExecute();
   }

   public Execute createExecute()
         throws ConfigurationException
   {
      return this;
   }

   public ModelAdapter getModelAdapter()
         throws ConfigurationException
   {
      return this.model;
   }

   public void destroy()
   {
   }

}