
package self.micromagic.eterna.tag;

import self.micromagic.eterna.digester.FactoryManager;

/**
 * 工厂实例的查询者.
 */
public interface InstanceFinder
{
   /**
    * 根据给出的名称查找一个工厂的实例.
    *
    * @param name   将通过此名称查找工厂实例
    * @return  查到的工厂实例, 或<code>null</code>没有查到
    */
   FactoryManager.Instance findInstance(String name);

}
