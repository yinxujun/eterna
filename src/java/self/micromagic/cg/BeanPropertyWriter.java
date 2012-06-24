
package self.micromagic.cg;

/**
 * 设置一个bean属性的工具.
 */
public interface BeanPropertyWriter
{
   /**
    * 设置一个属性的值.
    *
    * @param cd            属性描述类
    * @param indexs        如果属性是个数组或Collection, 可通过此索引值来控制设置哪个值
    * @param bean          属性所在的bean对象
    * @param value         要设置的值
    * @param prefix        当前的名称前缀
    * @param beanMap       当前的BeanMap对象
    * @param originObj     设置的值所在的原始对象, 可能是一个Map, 也可能是一个ResultRow,
    *                      也可能是null(当原始对象不存在时)
    * @param oldValue      该属性的原始值
    * @return              成功设置了值的属性的个数
    */
   public int setBeanValue(CellDescriptor cd, int[] indexs, Object bean, Object value,
         String prefix, BeanMap beanMap, Object originObj, Object oldValue)
         throws Exception;

}
