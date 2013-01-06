
package self.micromagic.eterna.security;

public interface User
{
	String getUserId();

	String getUserName();

	Role[] getRoles();

	Role getRole(String name);

	Object getAttribute(String name);

	Permission getPermission();

}