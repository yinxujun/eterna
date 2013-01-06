
package self.micromagic.eterna.share;

/**
 * @deprecated
 * @see self.micromagic.util.container.ThreadCache
 */
public class ThreadCache
{
	private self.micromagic.util.container.ThreadCache cache;

	private ThreadCache()
	{
		this.cache = self.micromagic.util.container.ThreadCache.getInstance();
	}

	/**
	 * 获得一个ThreadCache的实例.
	 */
	public static ThreadCache getInstance()
	{
		return new ThreadCache();
	}

	public void setProperty(String name, Object property)
	{
		this.cache.setProperty(name, property);
	}

	public Object getProperty(String name)
	{
		return this.cache.getProperty(name);
	}

	public void removeProperty(String name)
	{
		this.cache.removeProperty(name);
	}

	public void clearPropertys()
	{
		this.cache.clearPropertys();
	}

	public static void clearAllPropertys()
	{
		self.micromagic.util.container.ThreadCache.clearAllPropertys();
	}

}