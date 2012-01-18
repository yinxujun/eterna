
package self.micromagic.eterna.share;

import java.util.HashMap;
import java.util.Map;

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

   public void setProperty(String name, Object property)
   {
      this.checkVersion();
      this.propertys.put(name, property);
   }

   public Object getProperty(String name)
   {
      if (this.checkVersion())
      {
         return this.propertys.get(name);
      }
      return null;
   }

   public void removeProperty(String name)
   {
      if (this.checkVersion())
      {
         this.propertys.remove(name);
      }
   }

   public void clearPropertys()
   {
      if (this.checkVersion())
      {
         this.propertys.clear();
      }
   }

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
