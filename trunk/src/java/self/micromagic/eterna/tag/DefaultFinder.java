
package self.micromagic.eterna.tag;

import self.micromagic.eterna.digester.FactoryManager;
import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.util.Utility;
import org.apache.commons.logging.Log;

/**
 * Ĭ�ϵĹ���ʵ����ѯ��.
 */
public class DefaultFinder
      implements InstanceFinder
{
   /**
    * Ĭ�ϵĹ���ʵ����ѯ��, ������Լ���ʵ����, �������¶Դ˱�����ֵ.
    */
   public static InstanceFinder finder = new DefaultFinder();

   /**
    * ���ڼ�¼��־.
    */
   static final Log log = Utility.createLog("tag");


   public FactoryManager.Instance findInstance(String name)
   {
      try
      {
         return FactoryManager.getFactoryManager(name);
      }
      catch (ConfigurationException ex)
      {
         return null;
      }
   }

}
