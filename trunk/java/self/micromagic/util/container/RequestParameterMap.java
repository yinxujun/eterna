
package self.micromagic.util.container;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletRequest;

public class RequestParameterMap
      implements Map
{
   private Map paramMap;
   private Map originParamMap = null;
   private boolean readOnly = true;
   private boolean selfMap = false;

   private RequestParameterMap(Map requestMap)
   {
      this.paramMap = requestMap;
      this.originParamMap = this.paramMap;
   }

   private RequestParameterMap(Map requestMap, boolean readOnly)
   {
      this(requestMap);
      this.readOnly = readOnly;
   }

   private RequestParameterMap(ServletRequest request)
   {
      this.paramMap = request.getParameterMap();
      this.originParamMap = this.paramMap;
   }

   private RequestParameterMap(ServletRequest request, boolean readOnly)
   {
      this(request);
      this.readOnly = readOnly;
   }

   public static String getFirstParam(Object obj)
   {
      if (obj == null)
      {
         return null;
      }
      if (obj instanceof String[])
      {
         String[] arr = (String[]) obj;
         return arr.length > 0 ? arr[0] : null;
      }
      return obj.toString();
   }

   public static Map create(ServletRequest request)
   {
      return request == null ? null : new RequestParameterMap(request);
   }

   /**
    * 构造一个request.parameter的map.
    *
    * @param readOnly   是否为只读, 如果设为ture, 表示不可以设置属性.
    */
   public static Map create(ServletRequest request, boolean readOnly)
   {
      return request == null ? null : new RequestParameterMap(request, readOnly);
   }

   public static Map create(Map requestMap)
   {
      return requestMap == null ? null : new RequestParameterMap(requestMap);
   }

   /**
    * 构造一个request.parameter的map.
    *
    * @param readOnly   是否为只读, 如果设为ture, 表示不可以设置属性.
    */
   public static Map create(Map requestMap, boolean readOnly)
   {
      return requestMap == null ? null : new RequestParameterMap(requestMap, readOnly);
   }

   public Map getOriginParamMap()
   {
      return this.originParamMap;
   }

   public boolean isReadOnly()
   {
      return this.readOnly;
   }

   public boolean equals(Object obj)
   {
      return this.paramMap.equals(obj);
   }

   public int hashCode()
   {
      return this.paramMap.hashCode();
   }

   public int size()
   {
      return this.paramMap.size();
   }

   public boolean isEmpty()
   {
      return this.paramMap.isEmpty();
   }

   public boolean containsKey(Object key)
   {
      return this.paramMap.containsKey(key);
   }

   public boolean containsValue(Object value)
   {
      return this.paramMap.containsValue(value);
   }

   public String getFirstString(Object key)
   {
      return getFirstParam(this.paramMap.get(key));
   }

   public Object get(Object key)
   {
      return this.paramMap.get(key);
   }

   public Object put(Object key, Object value)
   {
      this.checkEdit();
      return this.paramMap.put(key, value);
   }

   public Object remove(Object key)
   {
      this.checkEdit();
      return this.paramMap.remove(key);
   }

   public void putAll(Map t)
   {
      this.checkEdit();
      this.paramMap.putAll(t);
   }

   public void clear()
   {
      this.checkEdit();
      this.paramMap.clear();
   }

   public Set keySet()
   {
      return this.paramMap.keySet();
   }

   public Collection values()
   {
      return this.paramMap.values();
   }

   public Set entrySet()
   {
      this.checkEdit();
      return this.paramMap.entrySet();
   }

   private boolean checkEdit()
   {
      if (!this.readOnly)
      {
         if (!this.selfMap)
         {
            this.paramMap = new HashMap(this.originParamMap);
            this.selfMap = true;
         }
         return true;
      }
      return false;
   }

}
