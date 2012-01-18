
package self.micromagic.eterna.security;

public interface Role
{
   String getRoleId();

   String getRoleName();

   Object getAttribute(String name);

   Permission getPermission();

}
