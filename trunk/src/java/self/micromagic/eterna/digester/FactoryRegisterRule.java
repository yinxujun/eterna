
package self.micromagic.eterna.digester;

import org.xml.sax.Attributes;
import self.micromagic.eterna.share.Factory;
import self.micromagic.cg.ClassGenerator;

/**
 * ע��һ�������ĳ�ʼ������.
 */
public class FactoryRegisterRule extends ObjectCreateRule
{
   protected String registerName = null;

   /**
    * @param className        ������ʵ����, ���������û��ָ���Ὣ����ΪĬ��ֵ
    * @param attributeName    �������ĸ�������ָ��������ʵ����
    * @param classType        ����ʵ�ֵĽӿ���
    * @param registerName     ע��˹����ķ�����
    */
   public FactoryRegisterRule(String className, String attributeName, Class classType,
         String registerName)
   {
      super(className, attributeName, classType);
      this.registerName = registerName;
   }

   /**
    * @param className        ������ʵ����, ���������û��ָ���Ὣ����ΪĬ��ֵ
    * @param attributeName    �������ĸ�������ָ��������ʵ����
    * @param classType        ����ʵ�ֵĽӿ���
    */
   public FactoryRegisterRule(String className, String attributeName, Class classType)
   {
      this(className, attributeName, classType, null);
   }

   public void myBegin(String namespace, String name, Attributes attributes)
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
      if (this.classType != null && !this.classType.isInstance(instance))
      {
         throw new InvalidAttributesException(realClassName + " is not instance of "
               + ClassGenerator.getClassName(this.classType));
      }
      this.digester.push(instance);

      if (this.registerName != null && register)
      {
         FactoryManager.addFactory(this.registerName, instance);
      }
   }

}
