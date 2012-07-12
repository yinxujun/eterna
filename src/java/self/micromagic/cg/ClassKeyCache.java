
package self.micromagic.cg;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.HashMap;
import java.util.Iterator;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;

import self.micromagic.util.Utility;
import org.apache.commons.collections.ReferenceMap;

/**
 * ��<code>Class</code>Ϊ��ֵ�������, ��Щ�����ڼ�ֵ<code>Class</code>��
 * �ͷ�ʱ, Ҳ��Ҫͬʱ���ͷ�. <p>
 * ����ʹ��<code>WeakHashMap</code>�޷�����������Ҫ��, ��Ϊ�洢��ֵ��Ҳ������
 * ��ֵ��<code>Class</code>������й����Ķ���. �����ͻ���ɼ�ֵ<code>Class</code>
 * �����޷����ͷ�, Ҳ���޷��ﵽ֮ǰ��Ҫ��Ŀ��. <p>
 * �����ʵ�ַ�ʽΪ: ͨ���ڼ�ֵ<code>Class</code>��<code>ClassLoader</code>����
 * ��һ����, ��������д���һ����̬����, ����Ϊ<code>Map</code>, �������map��
 * �����е�ֵ��������<code>ClassLoader</code>���ͷŶ��ͷŵ�. <p>
 */
public class ClassKeyCache
{
   /**
    * ��<code>ClassLoader</code>Ϊ��ֵ, ��ŵ�<code>CacheCell</code>.
    */
   private Map caches;

   private ClassKeyCache()
   {
      this.caches = new WeakHashMap();
   }

   /**
    * ���һ��<code>ClassKeyCache</code>��ʵ��.
    */
   public static ClassKeyCache getInstance()
   {
      return new ClassKeyCache();
   }

   /**
    * ����һ������.
    *
    * @param c         ��ΪΪ��ֵ��<code>Class</code>
    * @param property  Ҫ���õ�����ֵ
    */
   public void setProperty(Class c, Object property)
   {
      CacheCell ccm = this.getCacheCell(c);
      if (ccm != null)
      {
         ccm.put(c, property);
      }
   }

   /**
    * ��ȡһ�����Ե�ֵ.
    *
    * @param c     ��ΪΪ��ֵ��<code>Class</code>
    */
   public Object getProperty(Class c)
   {
      CacheCell ccm = this.getCacheCell(c);
      if (ccm != null)
      {
         return ccm.get(c);
      }
      return null;
   }

   /**
    * �Ƴ�һ������.
    *
    * @param c     ��ΪΪ��ֵ��<code>Class</code>
    */
   public void removeProperty(Class c)
   {
      CacheCell ccm = this.getCacheCell(c);
      if (ccm != null)
      {
         ccm.remove(c);
      }
   }

   /**
    * ��û���Ķ�����.
    */
   public int size()
   {
      int result = 0;
      Iterator itr = this.caches.values().iterator();
      while (itr.hasNext())
      {
         CacheCell cc = (CacheCell) itr.next();
         result += cc.size();
      }
      return result;
   }

   /**
    * ������е�����ֵ.
    */
   public void clearPropertys()
   {
      this.caches.clear();
   }

   /**
    * ��ȡ���浥Ԫ.
    *
    * @param c     ��ΪΪ��ֵ��<code>Class</code>
    */
   private CacheCell getCacheCell(Class c)
   {
      if (c == null)
      {
         return null;
      }
      ClassLoader cl = c.getClassLoader();
      CacheCell cc = (CacheCell) this.caches.get(cl);
      if (cc == null)
      {
         cc = getCacheCell0(cl, this.caches);
      }
      return cc;
   }

   private static synchronized CacheCell getCacheCell0(ClassLoader cl, Map caches)
   {
      CacheCell cc = (CacheCell) caches.get(cl);
      if (cc == null)
      {
         if (cl == null)
         {
            cc = new CacheCellImpl0();
         }
         else
         {
            try
            {
               cc = new CacheCellImpl1(getCachesClass(cl));
            }
            catch (Throwable ex)
            {
               // �������Class�Ĺ����г��ִ���, ����ϵͳ�Ļ��浥Ԫ
               cc = new CacheCellImpl0();
            }
         }
         caches.put(cl, cc);
      }
      return cc;
   }

   private static Class getCachesClass(ClassLoader loader)
         throws Exception
   {
      Class cachesClass = (Class) cachesClassCache.get(loader);
      if (cachesClass != null)
      {
         return cachesClass;
      }
      byte[] b = cachesClassDef;
      if (b == null)
      {
         return CacheCellImpl0.class;
      }
      Class cl = Class.forName("java.lang.ClassLoader");
      Class[] paramTypes = { String.class, byte[].class, int.class, int.class};
      Object[] args = {cachesClassName, b, new Integer(0), new Integer(b.length)};
      java.lang.reflect.Method method = cl.getDeclaredMethod("defineClass", paramTypes);
      try
      {
         method.setAccessible(true);
         cachesClass = (Class) method.invoke(loader, args);
         method.setAccessible(false);
      }
      catch (ClassFormatError ex)
      {
         cachesClass = loader.loadClass(cachesClassName);
      }
      cachesClassCache.put(loader, cachesClass);
      return cachesClass;
   }

   private static byte[] getCachesClassDef(String name)
   {
      try
      {
         String path = name.replace('.', '/') + ".class";
         InputStream in = ClassKeyCache.class.getClassLoader().getResourceAsStream(path);
         ByteArrayOutputStream bOut = new ByteArrayOutputStream(128);
         Utility.copyStream(in, bOut);
         in.close();
         return bOut.toByteArray();
      }
      catch (Exception ex)
      {
         if (ClassGenerator.COMPILE_LOG_TYPE > CG.COMPILE_LOG_TYPE_ERROR)
         {
            CG.log.error("Init caches class def error.", ex);
         }
         return null;
      }
   }

   /**
    * ��Ż������ݵ��� �Ļ���
    */
   private static Map cachesClassCache = new ReferenceMap(ReferenceMap.WEAK, ReferenceMap.WEAK);

   /**
    * ��Ż������ݵ�����.
    */
   private static final String cachesClassName;

   /**
    * ��Ż������ݵ��ඨ��.
    */
   private static final byte[] cachesClassDef;

   static
   {
      cachesClassName = ClassKeyCache.class.getName() + "$Caches";
      cachesClassDef = getCachesClassDef(cachesClassName);
   }

   /**
    * ���浥Ԫ.
    */
   private interface CacheCell
   {
      public Object get(Class c);

      public Object put(Class c, Object value);

      public Object remove(Class c);

      public int size();

   }

   /**
    * ���ϵͳClassLoader(Ϊnull)�� ���浥Ԫ ��ʵ��
    */
   private static class CacheCellImpl0
         implements CacheCell
   {
      private Map cache = new HashMap();

      public Object get(Class c)
      {
         return this.cache.get(c);
      }

      public Object put(Class c, Object value)
      {
         return this.cache.put(c, value);
      }

      public Object remove(Class c)
      {
         return this.cache.remove(c);
      }

      public int size()
      {
         return this.cache.size();
      }

   }

   /**
    * ���һ���ClassLoader�� ���浥Ԫ ��ʵ��
    */
   private static class CacheCellImpl1
         implements CacheCell
   {
      /**
       * ����ʹ��<code>WeakReference</code>�����ö�Ӧ����ͻ���, �����Ͳ���Ӱ�����������ͷ�.
       */
      private WeakReference cellClass;
      private WeakReference cacheObj;

      public CacheCellImpl1(Class cellClass)
      {
         this.cellClass = new WeakReference(cellClass);
      }

      private Map getCache()
      {
         Map cache = this.cacheObj == null ? null : (Map) this.cacheObj.get();
         if (cache == null)
         {
            Class c = (Class) this.cellClass.get();
            if (c == null)
            {
               return null;
            }
            try
            {
               // ��δ���Ӧ��ֻ��ִ��һ��, ֻҪ�಻���ͷ�, �������Ҳ���ᱻ�ͷ�
               Field f = c.getField("caches");
               Map caches = (Map) f.get(null);
               cache = (Map) caches.get(this);
               if (cache == null)
               {
                  cache = new HashMap();
                  caches.put(this, cache);
               }
               this.cacheObj = new WeakReference(cache);
            }
            catch (Exception ex)
            {
               if (ClassGenerator.COMPILE_LOG_TYPE > CG.COMPILE_LOG_TYPE_ERROR)
               {
                  CG.log.error("Create cache error.", ex);
               }
               return null;
            }
         }
         return cache;
      }

      public Object get(Class c)
      {
         Map cache = this.getCache();
         if (cache != null)
         {
            return cache.get(c);
         }
         return null;
      }

      public Object put(Class c, Object value)
      {
         Map cache = this.getCache();
         if (cache != null)
         {
            return cache.put(c, value);
         }
         return null;
      }

      public Object remove(Class c)
      {
         Map cache = this.getCache();
         if (cache != null)
         {
            return cache.remove(c);
         }
         return null;
      }

      public int size()
      {
         Map cache = this.getCache();
         if (cache != null)
         {
            return cache.size();
         }
         return 0;
      }

   }

}
