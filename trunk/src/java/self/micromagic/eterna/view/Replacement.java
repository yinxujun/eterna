
package self.micromagic.eterna.view;

import java.util.Map;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.EternaFactory;

public interface Replacement extends Component
{
	/**
	 * 引用父控件中的脚本的表达式.
	 */
   public static final String PARENT_SCRIPT = "{$parentScript}";

	/**
	 * 初始化替换. <p>
	 * 当base参数不为null时, 先通过base进行初始化, 即调用<code>initBase</code>. 此时将忽略parent
	 * 参数.
	 * 如果base参数为null时(parent不可为null), 则需要先将本节点替换到parent的子节点中. 然后将本
	 * <code>Replacement</code>中的子节点替换到<code>baseComponent</code>的子节点中.
	 * 注:
	 * 当base不是Replacement的实例时, 才对<code>baseComponent</code>的子节点进行替换.
	 *
	 * @param factory     本控件所在的factory
	 * @param base        用于对本控件进行初始化的<code>baseComponent</code>
	 * @param parent      需要在此对象中寻找可替换成本控件的节点
	 * @see #initBase
	 */
   void initReplace(EternaFactory factory, Component base, Replacement parent)
			throws ConfigurationException;

	/**
	 * 通过<code>baseComponent</code>进行初始化.
	 *
	 * @param factory   本控件所在的factory
	 * @param base      用于对本控件进行初始化的<code>baseComponent</code>
	 */
   void initBase(EternaFactory factory, Component base) throws ConfigurationException;

	/**
	 * 将控件替换到<code>baseComponent</code>的子节点中.
	 *
	 * @param factory       本控件所在的factory
	 * @param newReplace    需要替换的控件, 其会替换<code>baseComponent</code>子节点中的
	 *                      同名控件
	 */
   void replaceComponent(EternaFactory factory, Component newReplace) throws ConfigurationException;

   /**
    * 获得直接匹配控件的映射表.
	 * 控件映射表是用户替换<code>baseComponent</code>中任意层次下的同名节点.
    */
   Map getDirectMatchMap() throws ConfigurationException;

	/**
	 * 获取当前控件的<code>baseComponent</code>.
	 */
   Component getBaseComponent() throws ConfigurationException;

}
