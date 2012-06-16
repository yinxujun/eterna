
package self.micromagic.util.container;

import java.util.HashMap;
import java.util.Map;

/**
 * �����߳��л��������.
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
    * ���һ��ThreadCache��ʵ��.
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
    * ��鵱ǰ���̻߳����Ƿ���Ч.
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
    * ��ǰ���̻߳�������һ������.
    *
    * @param name        Ҫ���õ����Ե�����
    * @param property    Ҫ���õ�����ֵ
    */
   public void setProperty(String name, Object property)
   {
      this.checkVersion();
      this.propertys.put(name, property);
   }

   /**
    * ��ȡ��ǰ���̻߳�����һ�����Ե�ֵ.
    *
    * @param name        Ҫ��ȡ�����Ե�����
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
    * �Ƴ���ǰ���̻߳����е�һ������.
    *
    * @param name        Ҫ�Ƴ������Ե�����
    */
   public void removeProperty(String name)
   {
      if (this.checkVersion())
      {
         this.propertys.remove(name);
      }
   }

   /**
    * ��յ�ǰ�̻߳����е�����ֵ.
    */
   public void clearPropertys()
   {
      if (this.checkVersion())
      {
         this.propertys.clear();
      }
   }

   /**
    * ��������̻߳����е�����ֵ.
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
