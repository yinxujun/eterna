
package self.micromagic.cg;

import java.lang.reflect.Method;

/**
 * 描述bean的一个方法的信息类.
 */
public class BeanMethodInfo
{
   /**
    * 方法对应属性的名称.
    */
   public final String name;

   /**
    * 属性的类型.
    */
   public final Class type;

   /**
    * 对属性操作的方法.
    */
   public final Method method;

   /**
    * 是否为设置的方法.
    */
   public final boolean doSet;

   /**
    * 是否为读取的方法.
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
