
package self.micromagic.util.container;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Enumeration;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;

import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.sql.ResultRow;
import self.micromagic.eterna.sql.ResultMetaData;
import self.micromagic.eterna.digester.ConfigurationException;
import org.apache.commons.collections.iterators.IteratorEnumeration;

/**
 * ע: ʹ��ʱ��ע��, ���û��ͨ��ValueContainerMap����ֱ�Ӷ�
 * Attribute���û�ɾ��ֵ�Ļ�, ���ٷ���ValueContainerMap��
 * values��keySet�Ļ���������ݲ�һ��
 */
public class ValueContainerMap extends AbstractMap
      implements Map
{
   private ValueContainerMapEntrySet vEntrySet;
   private ValueContainer vContainer;

   /**
    * ��ִ��put��removeʱ, �Ƿ�Ҫ��ȡԭʼֵ.
    * <code>true</code>Ϊ��Ҫ��ȡԭʼֵ.
    */
   private boolean loadOldValue = false;

   private ValueContainerMap(ValueContainer vContainer)
   {
      this.vContainer = vContainer;
      this.vEntrySet = new ValueContainerMapEntrySet(this, this.vContainer);
   }

   public static ValueContainerMap create(ValueContainer vContainer)
   {
      if (vContainer == null)
      {
         return null;
      }
      return new ValueContainerMap(vContainer);
   }

   public static ValueContainerMap createResultRowMap(ResultRow row)
   {
      if (row == null)
      {
         return null;
      }
      ValueContainer vContainer = new ResultRowContainer(row);
      return new ValueContainerMap(vContainer);
   }


   public static ValueContainerMap createRequestAttributeMap(ServletRequest request)
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
   public static ValueContainerMap createSessionAttributeMap(HttpSession session)
   {
      if (session == null)
      {
         return null;
      }
      ValueContainer vContainer = new SessionAttributeContainer(session);
      return new ValueContainerMap(vContainer);
   }

   public static ValueContainerMap createSessionAttributeMap(HttpServletRequest request)
   {
      if (request == null)
      {
         return null;
      }
      ValueContainer vContainer = new SessionAttributeContainer(request);
      return new ValueContainerMap(vContainer);
   }

   public static ValueContainerMap createApplicationAttributeMap(ServletContext context)
   {
      if (context == null)
      {
         return null;
      }
      ValueContainer vContainer = new ApplicationAttributeContainer(context);
      return new ValueContainerMap(vContainer);
   }

   /**
    * ��ִ��put��removeʱ, �Ƿ�Ҫ��ȡԭʼֵ.
    */
   public boolean isLoadOldValue()
   {
      return loadOldValue;
   }

   /**
    * ������ִ��put��removeʱ, �Ƿ�Ҫ��ȡԭʼֵ.
    */
   public void setLoadOldValue(boolean loadOldValue)
   {
      this.loadOldValue = loadOldValue;
   }

   public boolean containsKey(Object key)
   {
      return this.vEntrySet.containsKey(key);
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


   private static class ResultRowContainer
         implements ValueContainer
   {
      private ResultRow row;

      public ResultRowContainer(ResultRow row)
      {
         this.row = row;
      }

      public boolean equals(Object o)
      {
         if (o instanceof ResultRowContainer)
         {
            ResultRowContainer other = (ResultRowContainer) o;
            return this.row.equals(other.row);
         }
         return false;
      }

      public int hashCode()
      {
         return this.row.hashCode();
      }

      public Object getValue(Object key)
      {
         try
         {
            return this.row.getObject(key == null ? null : key.toString(), true);
         }
         catch (SQLException ex)
         {
            return null;
         }
         catch (ConfigurationException ex)
         {
            return null;
         }
      }

      public void setValue(Object key, Object value)
      {
	      throw new UnsupportedOperationException();
      }

      public void removeValue(Object key)
      {
	      throw new UnsupportedOperationException();
      }

      public Enumeration getKeys()
      {
         try
         {
            ResultMetaData rmd = this.row.getResultIterator().getMetaData();
            int count = rmd.getColumnCount();
            List names = new ArrayList(rmd.getColumnCount());
            for (int i = 0; i < count; i++)
            {
               names.add(rmd.getColumnReader(i + 1));
            }
            return new IteratorEnumeration(names.iterator());
         }
         catch (SQLException ex)
         {
            return null;
         }
         catch (ConfigurationException ex)
         {
            return null;
         }
      }

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
         return this.request.getAttribute(key == null ? null : key.toString());
      }

      public void setValue(Object key, Object value)
      {
         this.request.setAttribute(key == null ? null : key.toString(), value);
      }

      public void removeValue(Object key)
      {
         this.request.removeAttribute(key == null ? null : key.toString());
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
            // ����sessionʱ���ܻ����, �����Ѿ��ύ��Ӧ��֮��
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
         return this.context.getAttribute(key == null ? null : key.toString());
      }

      public void setValue(Object key, Object value)
      {
         this.context.setAttribute(key == null ? null : key.toString(), value);
      }

      public void removeValue(Object key)
      {
         this.context.removeAttribute(key == null ? null : key.toString());
      }

      public Enumeration getKeys()
      {
         return this.context.getAttributeNames();
      }

   }

}