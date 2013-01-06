
package self.micromagic.eterna.security;

public class EmptyPermission
		implements Permission
{
	private static Permission instance = new EmptyPermission();

	private EmptyPermission()
	{
	}

	public boolean hasPermission(String name)
	{
		return false;
	}

	public boolean hasPermission(int id)
	{
		return false;
	}

	public static Permission getInstance()
	{
		return EmptyPermission.instance;
	}

}