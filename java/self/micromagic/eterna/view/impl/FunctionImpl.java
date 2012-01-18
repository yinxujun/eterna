
package self.micromagic.eterna.view.impl;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.AbstractGenerator;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.view.BaseManager;
import self.micromagic.eterna.view.Function;
import self.micromagic.eterna.view.FunctionGenerator;
import self.micromagic.eterna.view.ViewAdapter;
import self.micromagic.eterna.view.ViewAdapterGenerator;

public class FunctionImpl extends AbstractGenerator
         implements Function, FunctionGenerator
{
   private String param = "";
   private String scriptBody = "";

   private ViewAdapterGenerator.ModifiableViewRes viewRes = null;

   public String getParam()
   {
      return this.param;
   }

   public void setParam(String param)
   {
      this.param = param;
   }

   public String getBody()
   {
      return this.scriptBody;
   }

   public void setBody(String body)
   {
      this.scriptBody = body;
   }

   public EternaFactory getFactory()
   {
      return (EternaFactory) this.factory;
   }

   public ViewAdapter.ViewRes getViewRes()
         throws ConfigurationException
   {
      if (this.viewRes == null)
      {
         this.viewRes = new ModifiableViewResImpl();
         this.scriptBody = BaseManager.dealScriptPart(
               this.viewRes, this.scriptBody, BaseManager.GRAMMER_TYPE_EXPRESSION, this.getFactory());
      }
      return this.viewRes;
   }

   public Function createFunction()
   {
      return this;
   }

   public Object create()
   {
      return this.createFunction();
   }

}
