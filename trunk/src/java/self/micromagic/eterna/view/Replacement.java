
package self.micromagic.eterna.view;

import java.util.Map;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.EternaFactory;

public interface Replacement extends Component
{
	/**
	 * ���ø��ؼ��еĽű��ı��ʽ.
	 */
   public static final String PARENT_SCRIPT = "{$parentScript}";

	/**
	 * ��ʼ���滻. <p>
	 * ��base������Ϊnullʱ, ��ͨ��base���г�ʼ��, ������<code>initBase</code>. ��ʱ������parent
	 * ����.
	 * ���base����Ϊnullʱ(parent����Ϊnull), ����Ҫ�Ƚ����ڵ��滻��parent���ӽڵ���. Ȼ�󽫱�
	 * <code>Replacement</code>�е��ӽڵ��滻��<code>baseComponent</code>���ӽڵ���.
	 * ע:
	 * ��base����Replacement��ʵ��ʱ, �Ŷ�<code>baseComponent</code>���ӽڵ�����滻.
	 *
	 * @param factory     ���ؼ����ڵ�factory
	 * @param base        ���ڶԱ��ؼ����г�ʼ����<code>baseComponent</code>
	 * @param parent      ��Ҫ�ڴ˶�����Ѱ�ҿ��滻�ɱ��ؼ��Ľڵ�
	 * @see #initBase
	 */
   void initReplace(EternaFactory factory, Component base, Replacement parent)
			throws ConfigurationException;

	/**
	 * ͨ��<code>baseComponent</code>���г�ʼ��.
	 *
	 * @param factory   ���ؼ����ڵ�factory
	 * @param base      ���ڶԱ��ؼ����г�ʼ����<code>baseComponent</code>
	 */
   void initBase(EternaFactory factory, Component base) throws ConfigurationException;

	/**
	 * ���ؼ��滻��<code>baseComponent</code>���ӽڵ���.
	 *
	 * @param factory       ���ؼ����ڵ�factory
	 * @param newReplace    ��Ҫ�滻�Ŀؼ�, ����滻<code>baseComponent</code>�ӽڵ��е�
	 *                      ͬ���ؼ�
	 */
   void replaceComponent(EternaFactory factory, Component newReplace) throws ConfigurationException;

   /**
    * ���ֱ��ƥ��ؼ���ӳ���.
	 * �ؼ�ӳ������û��滻<code>baseComponent</code>���������µ�ͬ���ڵ�.
    */
   Map getDirectMatchMap() throws ConfigurationException;

	/**
	 * ��ȡ��ǰ�ؼ���<code>baseComponent</code>.
	 */
   Component getBaseComponent() throws ConfigurationException;

}
