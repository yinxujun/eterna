
package self.micromagic.cg;

/**
 * ����һ��bean���ԵĹ���.
 */
public interface BeanPropertyWriter
{
   /**
    * ����һ�����Ե�ֵ.
    *
    * @param cd            ����������
    * @param indexs        ��������Ǹ������Collection, ��ͨ��������ֵ�����������ĸ�ֵ
    * @param bean          �������ڵ�bean����
    * @param value         Ҫ���õ�ֵ
    * @param prefix        ��ǰ������ǰ׺
    * @param beanMap       ��ǰ��BeanMap����
    * @param originObj     ���õ�ֵ���ڵ�ԭʼ����, ������һ��Map, Ҳ������һ��ResultRow,
    *                      Ҳ������null(��ԭʼ���󲻴���ʱ)
    * @param oldValue      �����Ե�ԭʼֵ
    * @return              �ɹ�������ֵ�����Եĸ���
    */
   public int setBeanValue(CellDescriptor cd, int[] indexs, Object bean, Object value,
         String prefix, BeanMap beanMap, Object originObj, Object oldValue)
         throws Exception;

}
