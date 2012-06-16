
package self.micromagic.util.container;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理线程中缓存的属性.
 */
public class ThreadCache
{
   private static ThreadLocal threadMap = new CacheThreadLocal();
   private static int globalVersion = 0;

   private Map propertys;
   private int thisVersion = 0;

   private ThreadCache()
   {
      this.propertys = new HashMap();
      this.thisVersion = globalVersion;
   }

   /**
    * 获得一个ThreadCache的实例.
    */
   public static ThreadCache getInstance()
   {
      ThreadCache threadCache = (ThreadCache) threadMap.get();
      if (threadCache == null)
      {
         threadCache = new ThreadCache();
         threadMap.set(threadCache);
      }
      return threadCache;
   }

   /**
    * 检查当前的线程缓存是否有效.
    */
   private boolean checkVersion()
   {
      if (this.thisVersion != globalVersion)
      {
         this.propertys.clear();
         this.thisVersion = globalVersion;
         return false;
      }
      return true;
   }

   /**
    * 向当前的线程缓存设置一个属性.
    *
    * @param name        要设置的属性的名称
    * @param property    要设置的属性值
    */
   public void setProperty(String name, Object property)
   {
      this.checkVersion();
      this.propertys.put(name, property);
   }

   /**
    * 获取当前的线程缓存中一个属性的值.
    *
    * @param name        要获取的属性的名称
    */
   public Object getProperty(String name)
   {
      if (this.checkVersion())
      {
         return this.propertys.get(name);
      }
      return null;
   }

   /**
    * 移除当前的线程缓存中的一个属性.
    *
    * @param name        要移除的属性的名称
    */
   public void removeProperty(String name)
   {
      if (this.checkVersion())
      {
         this.propertys.remove(name);
      }
   }

   /**
    * 清空当前线程缓存中的属性值.
    */
   public void clearPropertys()
   {
      if (this.checkVersion())
      {
         this.propertys.clear();
      }
   }

   /**
    * 清空所有线程缓存中的属性值.
    */
   public static void clearAllPropertys()
   {
      globalVersion++;
   }

   private static class CacheThreadLocal extends ThreadLocal
   {
      protected Object initialValue()
      {
         return new ThreadCache();
      }
   }

}
