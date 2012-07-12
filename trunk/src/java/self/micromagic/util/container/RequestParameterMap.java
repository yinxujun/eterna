
package self.micromagic.util.container;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.util.Arrays;

import javax.servlet.ServletRequest;

import self.micromagic.util.Utility;

public class RequestParameterMap
      implements Map
{
   /**
    * �����Ƿ���Ҫ��request�е�����ȡֵ���н���.
    */
   public static final String PARSE_PARAM_PROPERTY = "self.micromagic.parse.request.param";

   /**
    * �Ƿ���Ҫ��request�е�����ȡֵ���н���. <p>
    * ������н����Ļ�, ʹ����ͨ������(��: name)ʱ, ֻ��ȡ���ַ��������еĵ�һ��;
    * ʹ�����������(��: name[])ʱ, �Ż����������ʽȡ������.
    */
   private static boolean PARSE_PARAM = false;

   static
   {
      try
      {
         Utility.addFieldPropertyManager(PARSE_PARAM_PROPERTY, RequestParameterMap.class, "PARSE_PARAM");
      }
      catch (Throwable ex) {}
   }

   private Map paramMap;
   private Map originParamMap;
   private boolean readOnly = true;
   private boolean selfMap = false;
   private boolean parseValue = PARSE_PARAM;

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

   /**
    * ��ȡ�����еĵ�һ���ַ���.
    */
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
      if (obj instanceof String)
      {
         return (String) obj;
      }
      if (obj instanceof Object[])
      {
         Object[] arr = (Object[]) obj;
         return arr.length > 0 ? getFirstParam(arr[0]) : null;
      }
      if (obj instanceof Collection)
      {
         Collection c = (Collection) obj;
         return c.size() > 0 ? getFirstParam(c.iterator().next()) : null;
      }
      return obj.toString();
   }

   /**
    * ͨ��request������һ��request.parameter��map.
    */
   public static RequestParameterMap create(ServletRequest request)
   {
      return request == null ? null : new RequestParameterMap(request);
   }

   /**
    * ͨ��request������һ��request.parameter��map.
    *
    * @param readOnly   �Ƿ�Ϊֻ��, �����Ϊture, ��ʾ��������������.
    */
   public static RequestParameterMap create(ServletRequest request, boolean readOnly)
   {
      return request == null ? null : new RequestParameterMap(request, readOnly);
   }

   /**
    * ͨ��map������һ��request.parameter��map. <p>
    * һ����portlet��Ԫ���ԵĻ�����ʹ��.
    */
   public static RequestParameterMap create(Map requestMap)
   {
      return requestMap == null ? null : new RequestParameterMap(requestMap);
   }

   /**
    * ͨ��map������һ��request.parameter��map. <p>
    * һ����portlet��Ԫ���ԵĻ�����ʹ��.
    *
    * @param readOnly   �Ƿ�Ϊֻ��, �����Ϊture, ��ʾ��������������.
    */
   public static RequestParameterMap create(Map requestMap, boolean readOnly)
   {
      return requestMap == null ? null : new RequestParameterMap(requestMap, readOnly);
   }

   /**
    * ���ԭʼ�Ĳ���map.
    */
   public Map getOriginParamMap()
   {
      return this.originParamMap;
   }

   /**
    * ��ȡ�Ƿ�Ҫ�Զ�ȡ��value���д���.
    */
   public boolean isParseValue()
   {
      return parseValue;
   }

   /**
    * �����Ƿ�Ҫ�Ի�ȡ��value���д���. <p>
    * �����Ϊ<code>true</code>, �������������Ϊ��ͨ������, ��ͨ��getFirstParam����
    * ��ȡ��һ���ַ���, ���������������"[]"��β��, �����ַ����������ʽ����.
    * �����Ϊ<code>false</code>, ��������, ֱ�ӷŻ�.
    *
    * @see #getFirstParam
    */
   public void setParseValue(boolean parseValue)
   {
      this.parseValue = parseValue;
   }

   /**
    * ��ȡ�˲���map�Ƿ���ֻ����.
    */
   public boolean isReadOnly()
   {
      return this.readOnly;
   }

   /**
    * ��ȡ���������еĵ�һ���ַ���.
    */
   public String getFirstString(Object key)
   {
      return getFirstParam(this.paramMap.get(key));
   }

   /**
    * ���˲���map�Ƿ�ɱ༭, ����ǿɱ༭��, ��ͨ��ԭʼ�Ĳ���map������һ���µ�map.
    */
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

   public Object get(Object key)
   {
      if (this.parseValue)
      {
         if (key == null)
         {
            return this.getFirstString(key);
         }
         String strKey = key.toString();
         if (strKey.endsWith("[]"))
         {
            Object value = this.paramMap.get(strKey.substring(0, strKey.length() - 2));
            if (value == null)
            {
               return null;
            }
            if (value instanceof String[])
            {
               return (String[]) value;
            }
            if (value instanceof String)
            {
               return new String[]{(String) value};
            }
            if (value instanceof Object[])
            {
               // ����Ǹ������������ｫ����Collection
               // ����һ���ж������д���
               value = Arrays.asList((Object[]) value);
            }
            if (value instanceof Collection)
            {
               Collection c = (Collection) value;
               String[] arr = new String[c.size()];
               Iterator itr = c.iterator();
               int index = 0;
               while (itr.hasNext())
               {
                  Object obj = itr.next();
                  arr[index++] = obj == null ? null : obj.toString();
               }
               return arr;
            }
            return new String[]{value.toString()};
         }
         else
         {
            return this.getFirstString(key);
         }
      }
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
		if (this.parseValue)
		{
			return new RequestParameterMapEntrySet(this, true);
		}
      return this.paramMap.values();
   }

   public Set entrySet()
   {
      this.checkEdit();
		if (this.parseValue)
		{
			return new RequestParameterMapEntrySet(this, false);
		}
      return this.paramMap.entrySet();
   }

	/**
	 * ��ȡ������EntrySet
	 */
	Set realEntrySet()
	{
      this.checkEdit();
      return this.paramMap.entrySet();
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
		if (this.parseValue)
		{
			Set s = new RequestParameterMapEntrySet(this, true);
			return s.contains(value);
		}
      return this.paramMap.containsValue(value);
   }

}
