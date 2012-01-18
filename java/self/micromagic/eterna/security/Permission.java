
package self.micromagic.eterna.security;

public interface Permission
{
   boolean hasPermission(String name);

   boolean hasPermission(int id);

}
