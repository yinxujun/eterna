
package self.micromagic.util.container;

import java.util.Map;
import java.util.Iterator;
import java.util.Collection;
import java.io.Serializable;
import java.io.IOException;

import javax.servlet.http.HttpSession;

/**
 * ����session�е�����. <p>
 * ����Ҫ���þ��ǽ�����Ķ����װ��<code>Property</code>, ����Ҫ���л�ʱ,
 * ���鱻��װ�Ķ����Ƿ�����л�, ������޷������л��Ķ���, �򲻻�����
 * �����л�, �ڷ����л�ʱ������Ϊnull.
 */
public class SessionCache
{
   private static SessionCache cache = new SessionCache();
   private static int globalVersion = 0;

   private SessionCache()
   {
   }

   /**
    * ���һ��SessionCache��ʵ��.
    */
   public static SessionCache getInstance()
   {
      return cache;
   }

   /**
    * ��session����������.
    * �����portlet, ����ͨ���˷�������session������.
    *
    * @param saMap      ��ת����map��session
    * @param name       Ҫ���õ����Ե�����
    * @param property   Ҫ���õ�����ֵ
    * @see ValueContainerMap#createSessionAttributeMap(javax.servlet.http.HttpServletRequest)
    */
   public void setProperty(Map saMap, String name, Object property)
   {
      saMap.put(name, new PropertyImpl(globalVersion, property));
   }

   /**
    * ��session����������.
    *
    * @param session    ��������session����
    * @param name       Ҫ���õ����Ե�����
    * @param property   Ҫ���õ�����ֵ
    */
   public void setProperty(HttpSession session, String name, Object property)
   {
      session.setAttribute(name, new PropertyImpl(globalVersion, property));
   }

   /**
    * ��session�л�ȡ����ֵ.
    * �����portlet, ����ͨ���˷�����ȡsession������.
    *
    * @param saMap      ��ת����map��session
    * @param name       Ҫ��ȡ�����Ե�����
    * @see ValueContainerMap#createSessionAttributeMap(javax.servlet.http.HttpServletRequest)
    */
   public Object getProperty(Map saMap, String name)
   {
      Object obj = saMap.get(name);
      if (obj != null && obj instanceof Property)
      {
         Property p = (Property) obj;
         if (p.getPropertyVersion() == globalVersion)
         {
            return ((Property) obj).getValue();
         }
         else
         {
            saMap.remove(name);
            return null;
         }
      }
      return obj;
   }

   /**
    * ��session�л�ȡ����ֵ.
    *
    * @param session    ��������session����
    * @param name       Ҫ��ȡ�����Ե�����
    */
   public Object getProperty(HttpSession session, String name)
   {
      Object obj = session.getAttribute(name);
      if (obj != null && obj instanceof Property)
      {
         Property p = (Property) obj;
         if (p.getPropertyVersion() == globalVersion)
         {
            return ((Property) obj).getValue();
         }
         else
         {
            session.removeAttribute(name);
            return null;
         }
      }
      return obj;
   }

   /**
    * ��session���Ƴ�һ������.
    * �����portlet, ����ͨ���˷����Ƴ�session������.
    *
    * @param saMap      ��ת����map��session
    * @param name       Ҫ�Ƴ������Ե�����
    * @see ValueContainerMap#createSessionAttributeMap(javax.servlet.http.HttpServletRequest)
    */
   public void removeProperty(Map saMap, String name)
   {
      saMap.remove(name);
   }

   /**
    * ��session���Ƴ�һ������.
    *
    * @param session    ��������session����
    * @param name       Ҫ�Ƴ������Ե�����
    */
   public void removeProperty(HttpSession session, String name)
   {
      session.removeAttribute(name);
   }

   /**
    * �������session�е�����ֵ.
    */
   public static void clearAllPropertys()
   {
      globalVersion++;
   }

   /**
    * ͨ��SessionCache��ŵ�session�еĶ���.
    */
   public interface Property
   {
      /**
       * ��ȡ���Ե�ԭʼֵ.
       */
      Object getValue();

      /**
       * ��ȡ���Եİ汾��.
       */
      int getPropertyVersion();

   }

   private static class PropertyImpl
         implements Property, Serializable
   {
      private int propertyVersion;
      private transient Object value;

      public PropertyImpl(int propertyVersion, Object value)
      {
         this.propertyVersion = propertyVersion;
         this.value = value;
      }

      public int getPropertyVersion()
      {
         return this.propertyVersion;
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
