
package self.micromagic.cg;

import java.lang.reflect.Field;

import self.micromagic.util.StringRef;

/**
 * 对一个属性单元的代码生成器.
 *
 * @author micromagic@sina.com
 */
public interface UnitProcesser
{
	/**
	 * 获得对属性的处理代码.
	 *
	 * @param f              属性对象
	 * @param type           属性的类型
	 * @param wrapName       如果是基本类型的话, 外覆类的名称
	 * @param processerType  处理类型, 写或读
	 * @param cg             生成处理类的代码生成器
	 * @return   对这个属性的处理代码
	 */
	public String getFieldCode(Field f, Class type, String wrapName, int processerType, ClassGenerator cg);

	/**
	 * 获得对方法的处理代码.
	 *
	 * @param m              方法信息
	 * @param type           属性的类型
	 * @param wrapName       如果是基本类型的话, 外覆类的名称
	 * @param processerType  处理类型, 写或读
	 * @param cg             生成处理类的代码生成器
	 * @return   对这个方法的处理代码
	 */
	public String getMethodCode(BeanMethodInfo m, Class type, String wrapName, int processerType,
			ClassGenerator cg);

}