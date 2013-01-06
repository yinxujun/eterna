
package self.micromagic.eterna.share;

public interface CacheData
{
	public Object setProperty(String groupName, String propName, Object property);

	public Object getProperty(String groupName, String name);

	public Object removeProperty(String groupName, String name);

	public void clearPropertyGroup(String groupName);

}