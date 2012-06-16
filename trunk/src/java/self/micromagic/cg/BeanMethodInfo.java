
package self.micromagic.cg;

import java.lang.reflect.Method;

/**
 * ����bean��һ����������Ϣ��.
 */
public class BeanMethodInfo
{
   /**
    * ������Ӧ���Ե�����.
    */
   public final String name;

   /**
    * ���Ե�����.
    */
   public final Class type;

   /**
    * �����Բ����ķ���.
    */
   public final Method method;

   /**
    * �Ƿ�Ϊ���õķ���.
    */
   public final boolean doSet;

   /**
    * �Ƿ�Ϊ��ȡ�ķ���.
    */
   public final boolean doGet;

   public BeanMethodInfo(String name, Method method, Class type,
         boolean doSet, boolean doGet)
   {
      this.name = name;
      this.method = method;
      this.type = type;
      this.doSet = doSet;
      this.doGet = doGet;
   }

}
