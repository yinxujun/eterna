
package self.micromagic.app;

import java.util.Enumeration;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;

import self.micromagic.util.container.ValueContainer;
import self.micromagic.util.container.ValueContainerMap;
import self.micromagic.util.container.UnmodifiableIterator;

public class PortletValueMap
{
   public static Map createRequestAttributeMap(PortletRequest request)
   {
      if (request == null)
      {
         return null;
      }
      ValueContainer vContainer = new RequestAttributeContainer(request);
      return ValueContainerMap.create(vContainer);
   }

   /**
    * @deprecated
    * @see #createSessionAttributeMap(PortletRequest, int)
    */
   public static Map createSessionAttributeMap(PortletSession session)
   {
      if (session == null)
      {
         return null;
      }
      ValueContainer vContainer = new SessionAttributeContainer(session);
      return ValueContainerMap.create(vContainer);
   }

   public static Map createSessionAttributeMap(PortletRequest request, int scope)
   {
      if (request == null)
      {
         return null;
      }
      ValueContainer vContainer = new SessionAttributeContainer(request, scope);
      return ValueContainerMap.create(vContainer);
   }

   private static class RequestAttributeContainer
         implements ValueContainer
   {
      private PortletRequest request;

      public RequestAttributeContainer(PortletRequest request)
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
      private PortletRequest request;
      private PortletSession session;
      private int scope = PortletSession.APPLICATION_SCOPE;

      public SessionAttributeContainer(PortletSession session)
      {
         this.session = session;
      }

      public SessionAttributeContainer(PortletSession session, int scope)
      {
         this.session = session;
         this.scope = scope;
      }

      public SessionAttributeContainer(PortletRequest request, int scope)
      {
         this.request = request;
         this.scope = scope;
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
         return this.session.getAttribute(
               key == null ? null : key.toString(), this.scope);
      }

      public void setValue(Object key, Object value)
      {
         this.checkSession(value != null);
         if (this.session != null)
         {
            this.session.setAttribute(
                  key == null ? null : key.toString(), value, this.scope);
         }
      }

      public void removeValue(Object key)
      {
         if (!this.checkSession(false))
         {
            return;
         }
         this.session.removeAttribute(
               key == null ? null : key.toString(), this.scope);
      }

      public Enumeration getKeys()
      {
         if (!this.checkSession(false))
         {
            return UnmodifiableIterator.EMPTY_ENUMERATION;
         }
         return this.session.getAttributeNames(this.scope);
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
         this.session = this.request.getPortletSession(create);
         return this.session != null;
      }

   }

}
