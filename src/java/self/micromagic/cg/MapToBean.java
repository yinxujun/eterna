
package self.micromagic.cg;

import java.util.Map;

/**
 * 将map中的属性设置到bean中的工具.
 */
public interface MapToBean
{
   /**
    * 将map中的数据设置到bean的属性中.
    *
    * @param bean     被设置属性的beean
    * @param values   值所在的map
    * @param prefix   获取值所用的名称的前缀
    *                 如：
    *                 prefix = "" 时使用values.get("name")
    *                 prefix = "sub." 时使用values.get("sub.name")
    * @return     成功设置了的属性个数
    */
   public int setBeanValues(Object bean, Map values, String prefix) throws Exception;

}
