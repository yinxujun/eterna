
package self.micromagic.eterna.share;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;

public class AttributeManager
{
   protected Map attributes = null;

   public Object getAttribute(String name)
   {
      if (this.attributes == null)
      {
         return null;
      }
      return this.attributes.get(name);
   }

   public String[] getAttributeNames()
   {
      if (this.attributes == null)
      {
         return new String[0];
      }
      Set keys = attributes.keySet();
      return (String[]) keys.toArray(new String[0]);
   }

   public Object setAttribute(String name, Object value)
   {
      if (this.attributes == null)
      {
         this.attributes = new HashMap();
      }
      return this.attributes.put(name, value);
   }

   public Object removeAttribute(String name)
   {
      if (this.attributes == null)
      {
         return null;
      }
      return this.attributes.remove(name);
   }

   public boolean hasAttribute(String name)
   {
      if (this.attributes == null)
      {
         return false;
      }
      return this.attributes.containsKey(name);
   }

}