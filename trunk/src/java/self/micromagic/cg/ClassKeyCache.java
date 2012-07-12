
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
 * 以<code>Class</code>为键值缓存对象, 这些对象在键值<code>Class</code>被
 * 释放时, 也需要同时被释放. <p>
 * 仅仅使用<code>WeakHashMap</code>无法满足这样的要求, 因为存储的值中也会引用
 * 键值的<code>Class</code>或和其有关联的对象. 这样就会造成键值<code>Class</code>
 * 本身无法被释放, 也就无法达到之前想要的目的. <p>
 * 这里的实现方式为: 通过在键值<code>Class</code>的<code>ClassLoader</code>中载
 * 入一个类, 在这个类中创建一个静态属性, 类型为<code>Map</code>, 这样这个map中
 * 的所有的值都会随着<code>ClassLoader</code>被释放而释放掉. <p>
 */
public class ClassKeyCache
{
   /**
    * 以<code>ClassLoader</code>为键值, 存放的<code>CacheCell</code>.
    */
   private Map caches;

   private ClassKeyCache()
   {
      this.caches = new WeakHashMap();
   }

   /**
    * 获得一个<code>ClassKeyCache</code>的实例.
    */
   public static ClassKeyCache getInstance()
   {
      return new ClassKeyCache();
   }

   /**
    * 设置一个属性.
    *
    * @param c         作为为键值的<code>Class</code>
    * @param property  要设置的属性值
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
    * 获取一个属性的值.
    *
    * @param c     作为为键值的<code>Class</code>
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
    * 移除一个属性.
    *
    * @param c     作为为键值的<code>Class</code>
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
    * 获得缓存的对象数.
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
    * 清空所有的属性值.
    */
   public void clearPropertys()
   {
      this.caches.clear();
   }

   /**
    * 获取缓存单元.
    *
    * @param c     作为为键值的<code>Class</code>
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
               // 如果定义Class的过程中出现错误, 则用系统的缓存单元
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
    * 存放缓存数据的类 的缓存
    */
   private static Map cachesClassCache = new ReferenceMap(ReferenceMap.WEAK, ReferenceMap.WEAK);

   /**
    * 存放缓存数据的类名.
    */
   private static final String cachesClassName;

   /**
    * 存放缓存数据的类定义.
    */
   private static final byte[] cachesClassDef;

   static
   {
      cachesClassName = ClassKeyCache.class.getName() + "$Caches";
      cachesClassDef = getCachesClassDef(cachesClassName);
   }

   /**
    * 缓存单元.
    */
   private interface CacheCell
   {
      public Object get(Class c);

      public Object put(Class c, Object value);

      public Object remove(Class c);

      public int size();

   }

   /**
    * 针对系统ClassLoader(为null)的 缓存单元 的实现
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
    * 针对一般的ClassLoader的 缓存单元 的实现
    */
   private static class CacheCellImpl1
         implements CacheCell
   {
      /**
       * 这里使用<code>WeakReference</code>来引用对应的类和缓存, 这样就不会影响其正常的释放.
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
               // 这段代码应该只会执行一次, 只要类不被释放, 这个缓存也不会被释放
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
