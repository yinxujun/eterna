
package self.micromagic.eterna.digester;

import java.util.Map;
import java.util.HashMap;

import org.xml.sax.Attributes;

/**
 * 检查父配置中的对象是否与子配置中相同的初始化规则.
 * 如果相同, 则不处理此对象, 通过设置MyRule.dealRule来控制.
 *
 * @author micromagic@sina.com
 */
public class SameCheckRule extends MyRule
{
	private static final Object FILL_OBJ = new Object();
	private static Map dealedObjMap;

	private String objName;
	private String attrName;
	private boolean ignoreObj = false;

	/**
	 * @param objName      对象的类型名称
	 * @param attrName     配置中指定对象名称的属性
	 */
	public SameCheckRule(String objName, String attrName)
	{
		this.objName = objName;
		this.attrName = attrName;
	}

	public void begin(String namespace, String name, Attributes attributes)
			throws Exception
	{
		if (!dealRule)
		{
			return;
		}
		String objFlag = this.objName;
		if (this.attrName != null)
		{
			objFlag = this.objName + ":" + attributes.getValue(this.attrName);
		}
		boolean hasObj = dealedObjMap.put(objFlag, FILL_OBJ) != null;
		if (hasObj && FactoryManager.getSuperInitLevel() > 0)
		{
			dealRule = false;
			this.ignoreObj = true;
			if (FactoryManager.log.isDebugEnabled())
			{
				FactoryManager.log.debug(objFlag + " has bean overwrited.");
			}
		}
	}

	public void end(String namespace, String name)
			throws Exception
	{
		if (this.ignoreObj)
		{
			this.ignoreObj = false;
			dealRule = true;
		}
	}

	/**
	 * 初始化已处理对象的缓存.
	 */
	static void initDealedObjMap()
	{
		dealedObjMap = new HashMap(512);
	}

	/**
	 * 清空已处理对象的缓存.
	 */
	static void clearDealedObjMap()
	{
		dealedObjMap = null;
	}

}