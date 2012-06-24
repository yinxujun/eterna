
package self.micromagic.util.container;

import java.util.HashMap;
import java.util.Map;

/**
 * �����߳��л��������.
 */
public class ThreadCache
{
   /**
    * ���<code>ThreadCache</code>ʵ����<code>ThreadLocal</code>��ʵ��.
    */
   private static ThreadLocal cache = new ThreadLocalCache();

   private static int globalVersion = 0;

   private Map propertys;
   private int thisVersion = 0;

   private ThreadCache()
   {
      this.propertys = new HashMap();
      this.thisVersion = globalVersion;
   }

   /**
    * ���һ��<code>ThreadCache</code>��ʵ��.
    */
   public static ThreadCache getInstance()
   {
      ThreadCache threadCache = (ThreadCache) cache.get();
      if (threadCache == null)
      {
         threadCache = new ThreadCache();
         cache.set(threadCache);
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
    * ��û���Ķ�����.
    */
   public int size()
   {
      this.checkVersion();
      return this.propertys.size();
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

   private static class ThreadLocalCache extends ThreadLocal
   {
      protected Object initialValue()
      {
         return new ThreadCache();
      }
   }

}
