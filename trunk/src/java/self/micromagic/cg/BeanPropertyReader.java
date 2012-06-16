
package self.micromagic.cg;

/**
 * 读取一个bean属性的工具.
 */
public interface BeanPropertyReader
{
   /**
    * 读取一个属性的值.
    *
    * @param cd            属性描述类
    * @param indexs        如果属性是个数组或Collection, 可通过此索引值来控制读取哪个值
    * @param bean          属性所在的bean对象
    * @param prefix        当前的名称前缀
    * @param beanMap       当前的BeanMap对象
    * @return              对应的属性的值
    */
   public Object getBeanValue(CellDescriptor cd, int[] indexs, Object bean, String prefix,
         BeanMap beanMap)
         throws Exception;

}
