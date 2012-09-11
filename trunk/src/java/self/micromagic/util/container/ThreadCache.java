
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
	 *
	 * �°汾��ȡ��������ʵ�ַ�ʽ.
	 * ��Ϊ��Ӧ�÷�������ʹ���̻߳����, �����߳��ǲ��ᱻ�ͷŵ�, �ǻ������߳�
	 * �еĶ���Ҳ���ᱻ�ͷ�. ��Ӧ�ñ����¼��ص�ʱ��, ԭ����Ӧ�þ��޷����ͷ�,
	 * ��Ϊ�µ�Ӧ�ò������µ�ThreadLocal, ���Ḳ��ԭ����, �Ӷ�����ڴ��й©.
    */
   //private static final ThreadLocal localCache = new ThreadLocalCache();

	/**
	 * �������д������̻߳���.
	 */
	private static final Map threadCaches = new SynHashMap(32, SynHashMap.WEAK);


   private Map propertys;

   private ThreadCache()
   {
      this.propertys = new HashMap();
   }

   /**
    * ���һ��<code>ThreadCache</code>��ʵ��.
    */
   public static ThreadCache getInstance()
   {
		/*
      ThreadCache threadCache = (ThreadCache) localCache.get();
      if (threadCache == null)
      {
         threadCache = new ThreadCache();
         localCache.set(threadCache);
      }
		*/
		Thread t = Thread.currentThread();
		ThreadCache threadCache = (ThreadCache) threadCaches.get(t);
		if (threadCache == null)
		{
			synchronized (threadCaches)
			{
				// ��ͬ���Ļ��������ж��Ƿ����, �����ڵĻ�������
				threadCache = (ThreadCache) threadCaches.get(t);
				if (threadCache == null)
				{
					threadCache = new ThreadCache();
					threadCaches.put(t, threadCache);
				}
			}
		}
      return threadCache;
   }

   /**
    * ��ǰ���̻߳�������һ������.
    *
    * @param name        Ҫ���õ����Ե�����
    * @param property    Ҫ���õ�����ֵ
    */
   public void setProperty(String name, Object property)
   {
      this.propertys.put(name, property);
   }

   /**
    * ��ȡ��ǰ���̻߳�����һ�����Ե�ֵ.
    *
    * @param name        Ҫ��ȡ�����Ե�����
    */
   public Object getProperty(String name)
   {
		return this.propertys.get(name);
   }

   /**
    * �Ƴ���ǰ���̻߳����е�һ������.
    *
    * @param name        Ҫ�Ƴ������Ե�����
    */
   public void removeProperty(String name)
   {
		this.propertys.remove(name);
   }

   /**
    * ��û���Ķ�����.
    */
   public int size()
   {
      return this.propertys.size();
   }

   /**
    * ��յ�ǰ�̻߳����е�����ֵ.
    */
   public void clearPropertys()
   {
		this.propertys.clear();
   }

   /**
    * ��������̻߳����е�����ֵ.
    */
   public static void clearAllPropertys()
   {
		threadCaches.clear();
   }

	/*
	ȡ����ThreadLocal�ķ�ʽ��ȡ�̻߳���
   private static class ThreadLocalCache extends ThreadLocal
   {
      protected Object initialValue()
      {
         return new ThreadCache();
      }

   }
	*/

}
