
package self.micromagic.util.container;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Enumeration;
import java.util.Set;

import javax.servlet.ServletRequest;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;

import self.micromagic.eterna.model.AppData;

/**
 * 注: 使用时需注意, 如果没有通过ValueContainerMap而是直接对
 * Attribute设置或删除值的话, 那再访问ValueContainerMap的
 * values和keySet的话会造成数据不一致
 */
public class ValueContainerMap extends AbstractMap
      implements Map
{
   private ValueContainerMapEntrySet vEntrySet;
   private ValueContainer vContainer;

   private ValueContainerMap(ValueContainer vContainer)
   {
      this.vContainer = vContainer;
      this.vEntrySet = new ValueContainerMapEntrySet(this.vContainer);
   }

   public static Map create(ValueContainer vContainer)
   {
      if (vContainer == null)
      {
         return null;
      }
      return new ValueContainerMap(vContainer);
   }

   public static Map createRequestAttributeMap(ServletRequest request)
   {
      if (request == null)
      {
         return null;
      }
      ValueContainer vContainer = new RequestAttributeContainer(request);
      return new ValueContainerMap(vContainer);
   }

   /**
    * @deprecated
    * @see #createSessionAttributeMap(HttpServletRequest)
    */
   public static Map createSessionAttributeMap(HttpSession session)
   {
      if (session == null)
      {
         return null;
      }
      ValueContainer vContainer = new SessionAttributeContainer(session);
      return new ValueContainerMap(vContainer);
   }

   public static Map createSessionAttributeMap(HttpServletRequest request)
   {
      if (request == null)
      {
         return null;
      }
      ValueContainer vContainer = new SessionAttributeContainer(request);
      return new ValueContainerMap(vContainer);
   }

   public static Map createApplicationAttributeMap(ServletContext context)
   {
      if (context == null)
      {
         return null;
      }
      ValueContainer vContainer = new ApplicationAttributeContainer(context);
      return new ValueContainerMap(vContainer);
   }

   public boolean equals(Object o)
   {
      if (o instanceof ValueContainerMap)
      {
         ValueContainerMap other = (ValueContainerMap) o;
         return this.vContainer.equals(other.vContainer);
      }
      return super.equals(o);
   }

   public int hashCode()
   {
      return this.vContainer.hashCode();
   }

   public boolean containsKey(Object key)
   {
      return this.get(key) != null;
   }

   public Object get(Object key)
   {
      return this.vContainer.getValue(key);
   }

   public Object put(Object key, Object value)
   {
      return this.vEntrySet.addValue(key, value);
   }

   public Object remove(Object key)
   {
      return this.vEntrySet.removeValue(key);
   }

   public Set entrySet()
   {
      return this.vEntrySet;
   }

   private static class RequestAttributeContainer
         implements ValueContainer
   {
      private ServletRequest request;

      public RequestAttributeContainer(ServletRequest request)
      {
         this.request = request;
      }

      public boolean equals(Object o)
      {
         if (o instanceof RequestAttributeContainer)
         {
            RequestAttributeContainer other = (RequestAttributeContainer) o;
            return this.request.equals(other.request);
         }
         return false;
      }

      public int hashCode()
      {
         return this.request.hashCode();
      }

      public Object getValue(Object key)
      {
         return this.request.getAttribute(
               key == null ? null : key.toString());
      }

      public void setValue(Object key, Object value)
      {
         this.request.setAttribute(
               key == null ? null : key.toString(), value);
      }

      public void removeValue(Object key)
      {
         this.request.removeAttribute(
               key == null ? null : key.toString());
      }

      public Enumeration getKeys()
      {
         return this.request.getAttributeNames();
      }

   }

   private static class SessionAttributeContainer
         implements ValueContainer
   {
      private HttpServletRequest request;
      private HttpSession session;

      public SessionAttributeContainer(HttpSession session)
      {
         this.session = session;
      }

      public SessionAttributeContainer(HttpServletRequest request)
      {
         this.request = request;
      }

      public boolean equals(Object o)
      {
         if (!this.checkSession(false))
         {
            return o == null;
         }
         if (o instanceof SessionAttributeContainer)
         {
            SessionAttributeContainer other = (SessionAttributeContainer) o;
            return this.session.equals(other.session);
         }
         return false;
      }

      public int hashCode()
      {
         if (!this.checkSession(false))
         {
            return 0;
         }
         return this.session.hashCode();
      }

      public Object getValue(Object key)
      {
         if (!this.checkSession(false))
         {
            return null;
         }
         return this.session.getAttribute(key == null ? null : key.toString());
      }

      public void setValue(Object key, Object value)
      {
         if (this.checkSession(value != null))
         {
            this.session.setAttribute(key == null ? null : key.toString(), value);
         }
      }

      public void removeValue(Object key)
      {
         if (this.checkSession(false))
         {
            this.session.removeAttribute(key == null ? null : key.toString());
         }
      }

      public Enumeration getKeys()
      {
         if (!this.checkSession(false))
         {
            return UnmodifiableIterator.EMPTY_ENUMERATION;
         }
         return this.session.getAttributeNames();
      }

      private boolean checkSession(boolean create)
      {
         if (this.session != null)
         {
            return true;
         }
         if (this.request == null)
         {
            return false;
         }
         try
         {
            this.session = this.request.getSession(create);
         }
         catch (Exception ex)
         {
            // 创建session时可能会出错, 比如已经提交了应答之后
            if (AppData.getAppLogType() != 0)
            {
               AppData.log.warn("Error in create session.", ex);
            }
         }
         return this.session != null;
      }

   }


   private static class ApplicationAttributeContainer
         implements ValueContainer
   {
      private ServletContext context;

      public ApplicationAttributeContainer(ServletContext context)
      {
         this.context = context;
      }

      public boolean equals(Object o)
      {
         if (o instanceof ApplicationAttributeContainer)
         {
            ApplicationAttributeContainer other = (ApplicationAttributeContainer) o;
            return this.context.equals(other.context);
         }
         return false;
      }

      public int hashCode()
      {
         return this.context.hashCode();
      }

      public Object getValue(Object key)
      {
         return this.context.getAttribute(
               key == null ? null : key.toString());
      }

      public void setValue(Object key, Object value)
      {
         this.context.setAttribute(
               key == null ? null : key.toString(), value);
      }

      public void removeValue(Object key)
      {
         this.context.removeAttribute(
               key == null ? null : key.toString());
      }

      public Enumeration getKeys()
      {
         return this.context.getAttributeNames();
      }

   }

}
