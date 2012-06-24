
package self.micromagic.eterna.tag;

import self.micromagic.eterna.digester.FactoryManager;
import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.util.Utility;
import org.apache.commons.logging.Log;

/**
 * 默认的工厂实例查询者.
 */
public class DefaultFinder
      implements InstanceFinder
{
   /**
    * 默认的工厂实例查询者, 如果有自己的实现类, 可以重新对此变量赋值.
    */
   public static InstanceFinder finder = new DefaultFinder();

   /**
    * 用于记录日志.
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
