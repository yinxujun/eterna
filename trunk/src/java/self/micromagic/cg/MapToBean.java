
package self.micromagic.cg;

import java.util.Map;

/**
 * ��map�е��������õ�bean�еĹ���.
 */
public interface MapToBean
{
   /**
    * ��map�е��������õ�bean��������.
    *
    * @param bean     ���������Ե�beean
    * @param values   ֵ���ڵ�map
    * @param prefix   ��ȡֵ���õ����Ƶ�ǰ׺
    *                 �磺
    *                 prefix = "" ʱʹ��values.get("name")
    *                 prefix = "sub." ʱʹ��values.get("sub.name")
    * @return     �ɹ������˵����Ը���
    */
   public int setBeanValues(Object bean, Map values, String prefix) throws Exception;

}
