
package self.micromagic.util;

import java.io.Writer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import self.micromagic.eterna.view.impl.ComponentImpl;
import self.micromagic.eterna.view.Component;
import self.micromagic.eterna.view.ComponentGenerator;
import self.micromagic.eterna.view.ViewAdapter;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.model.AppData;

/**
 * 通过factory中attribute的值来生成这个component. <p>
 * component的类型必须设为none, 必须在component中设置名称为attrName的属性,
 * 该值为所使用的factory中attribute的名称.
 * 此component中不可设置子component.
 */
public final class AttributeComponent extends ComponentImpl
      implements Component, ComponentGenerator
{
   public static final String FACTORY_ATTRIBUTE_NAME = "attrName";

   private String bodyHTML;
   private Map bindRes;

   {
      this.type = "div";
   }

   public void initialize(EternaFactory factory, Component parent)
         throws ConfigurationException
   {
      if (this.initialized)
      {
         return;
      }
      super.initialize(factory, parent);
      String tmp;

      tmp = (String) this.getAttribute("bindRes");
      if (tmp != null)
      {
         this.bindRes = new HashMap();
         String[] tmps = StringTool.separateString(Utility.resolveDynamicPropnames(tmp), ",", true);
         for (int i = 0; i < tmps.length; i++)
         {
            int index = tmps[i].indexOf(':');
            if (index != -1)
            {
               this.bindRes.put(tmps[i].substring(0, index).trim(),
                     tmps[i].substring(index + 1).trim());
            }
         }
      }

      tmp = (String) this.getAttribute(FACTORY_ATTRIBUTE_NAME);
      if (tmp == null)
      {
         throw new ConfigurationException("Must set attribute [" + FACTORY_ATTRIBUTE_NAME
               + "] in AttributeComponent.");
      }
      this.bodyHTML = (String) factory.getAttribute(tmp);
      if (this.bodyHTML == null)
      {
         throw new ConfigurationException("Not found attribute [" + tmp + "] in factory.");
      }
      this.bodyHTML = Utility.resolveDynamicPropnames(this.bodyHTML, this.bindRes);
      boolean autoSet = true;
      String autoSetStr = (String) this.getAttribute("autoSet");
      if (autoSetStr != null)
      {
         autoSet = "true".equalsIgnoreCase(autoSetStr);
      }

      if (autoSet)
      {
         String myScript = "webObj.html(objConfig.bodyString);";
         if (this.initScript != null)
         {
            this.initScript = this.initScript + myScript;
         }
         else
         {
            this.initScript = myScript;
         }
      }
   }

   public void setType(String type)
         throws ConfigurationException
   {
      if (!"div".equals(type))
      {
         throw new ConfigurationException("Must set type as [div] in AttributeComponent.");
      }
   }

   public void addComponent(Component com)
         throws ConfigurationException
   {
      throw new ConfigurationException("Can't add sub component in AttributeComponent.");
   }

   public void printSpecialBody(Writer out, AppData data, ViewAdapter view)
         throws IOException, ConfigurationException
   {
      super.printSpecialBody(out, data, view);
      out.write(",bodyString:");
      out.write("\"");
      out.write(this.stringCoder.toJsonString(this.bodyHTML));
      out.write("\"");
   }

}
