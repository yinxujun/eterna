
package self.micromagic.cg;

/**
 * ��ȡһ��bean���ԵĹ���.
 */
public interface BeanPropertyReader
{
   /**
    * ��ȡһ�����Ե�ֵ.
    *
    * @param cd            ����������
    * @param indexs        ��������Ǹ������Collection, ��ͨ��������ֵ�����ƶ�ȡ�ĸ�ֵ
    * @param bean          �������ڵ�bean����
    * @param prefix        ��ǰ������ǰ׺
    * @param beanMap       ��ǰ��BeanMap����
    * @return              ��Ӧ�����Ե�ֵ
    */
   public Object getBeanValue(CellDescriptor cd, int[] indexs, Object bean, String prefix,
         BeanMap beanMap)
         throws Exception;

}
