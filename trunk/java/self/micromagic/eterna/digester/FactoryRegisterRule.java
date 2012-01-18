
package self.micromagic.eterna.digester;

import org.xml.sax.Attributes;
import self.micromagic.eterna.share.Factory;

public class FactoryRegisterRule extends ObjectCreateRule
{
   protected String registerName = null;

   public FactoryRegisterRule(String className, String attributeName, Class classType,
         String registerName)
   {
      super(className, attributeName, classType);
      this.registerName = registerName;
   }

   public FactoryRegisterRule(String className, String attributeName, Class classType)
   {
      this(className, attributeName, classType, null);
   }

   public void begin(String namespace, String name, Attributes attributes)
         throws Exception
   {
      String realClassName = ObjectCreateRule.getClassName(
            this.attributeName, this.className, attributes);
      this.digester.getLogger().debug("New " + realClassName);

      Factory instance = null;
      boolean register = false;
      if (this.registerName != null)
      {
         try
         {
            instance = FactoryManager.getFactory(this.registerName, realClassName);
         }
         catch (Exception ex) {}
      }
      if (instance == null)
      {
         register = true;
         instance = (Factory) ObjectCreateRule.createObject(realClassName);
      }
      if (classType != null && !classType.isInstance(instance))
      {
         throw new InvalidAttributesException(realClassName
               + " is not instance of " + classType.getName());
      }
      this.digester.push(instance);

      if (this.registerName != null && register)
      {
         FactoryManager.addFactory(this.registerName, instance);
      }
   }

}
