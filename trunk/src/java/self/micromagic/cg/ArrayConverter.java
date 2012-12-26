
package self.micromagic.cg;

/**
 * 数组类型的转换器.
 */
public interface ArrayConverter
{
	/**
	 * 处理数组类型的转换.
	 *
	 * @param array     需要被转换的数组
	 * @param destArr   目标数组对象
	 * @param converter 类型转换器, 可以是BeanMap或ValueConverter
	 * @return  转换后的数组
	 */
	Object convertArray(Object array, Object destArr, Object converter) throws Exception;

	/**
	 * 处理数组类型的转换.
	 *
	 * @param array     需要被转换的数组
	 * @param converter 类型转换器, 可以是BeanMap或ValueConverter
	 * @return  转换后的数组
	 */
	Object convertArray(Object array, Object converter) throws Exception;

}
