
package self.micromagic.cg;

/**
 * �������͵�ת����.
 */
public interface ArrayConverter
{
	/**
	 * �����������͵�ת��.
	 *
	 * @param array     ��Ҫ��ת��������
	 * @param destArr   Ŀ���������
	 * @param converter ����ת����, ������BeanMap��ValueConverter
	 * @return  ת���������
	 */
	Object convertArray(Object array, Object destArr, Object converter) throws Exception;

	/**
	 * �����������͵�ת��.
	 *
	 * @param array     ��Ҫ��ת��������
	 * @param converter ����ת����, ������BeanMap��ValueConverter
	 * @return  ת���������
	 */
	Object convertArray(Object array, Object converter) throws Exception;

}
