
package self.micromagic.eterna.share;

import java.util.Map;
import java.util.Iterator;
import java.util.Collection;
import java.io.Serializable;
import java.io.IOException;

import javax.servlet.http.HttpSession;

public class SessionCache
{
   private static SessionCache sessionCache = new SessionCache();

   private SessionCache()
   {
   }

   public static SessionCache getInstance()
   {
      return sessionCache;
   }

   public void setProperty(Map saMap, String name, Object property)
   {
      saMap.put(name, new PropertyImpl(property));
   }

   public void setProperty(HttpSession session, String name, Object property)
   {
      session.setAttribute(name, new PropertyImpl(property));
   }

   public Object getProperty(Map saMap, String name)
   {
      Object obj = saMap.get(name);
      if (obj != null && obj instanceof Property)
      {
         return ((Property) obj).getValue();
      }
      return obj;
   }

   public Object getProperty(HttpSession session, String name)
   {
      Object obj = session.getAttribute(name);
      if (obj != null && obj instanceof Property)
      {
         return ((Property) obj).getValue();
      }
      return obj;
   }

   public void removeProperty(Map saMap, String name)
   {
      saMap.remove(name);
   }

   public void removeProperty(HttpSession session, String name)
   {
      session.removeAttribute(name);
   }

   public interface Property
   {
      Object getValue();
   }

   private static class PropertyImpl
         implements Property, Serializable
   {
      public transient Object value;

      public PropertyImpl(Object value)
      {
         this.value = value;
      }

      public Object getValue()
      {
         return this.value;
      }

      private void writeObject(java.io.ObjectOutputStream s)
            throws IOException
      {
	      s.defaultWriteObject();
         if (value != null && value instanceof Serializable)
         {
            Iterator itr = null;
            if (value instanceof Map)
            {
               itr = ((Map) value).values().iterator();
            }
            else if (value instanceof Collection)
            {
               itr = ((Collection) value).iterator();
            }
            if (itr != null)
            {
               while (itr.hasNext())
               {
                  if (!(itr.next() instanceof Serializable))
                  {
                     s.writeBoolean(false);
                     return;
                  }
               }
            }
            s.writeBoolean(true);
            s.writeObject(this.value);
         }
         else
         {
            s.writeBoolean(false);
         }
      }

      private void readObject(java.io.ObjectInputStream s)
            throws IOException, ClassNotFoundException
      {
         s.defaultReadObject();
         boolean canSerialize = s.readBoolean();
         this.value = canSerialize ? s.readObject() : null;
      }

   }

}
