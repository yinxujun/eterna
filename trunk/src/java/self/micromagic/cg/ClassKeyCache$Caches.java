
package self.micromagic.cg;

import java.util.Map;

import self.micromagic.util.container.SynHashMap;

/**
 * 需要定义在<code>ClassLoader</code>中, 存放缓存数据的类.
 *
 * @author micromagic@sina.com
 */
public class ClassKeyCache$Caches
{
	public static Map caches = new SynHashMap(8, SynHashMap.WEAK);

}