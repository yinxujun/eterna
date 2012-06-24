
package self.micromagic.cg;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;

/**
 * 默认的bean检测器.
 */
class DefaultBeanChecker
      implements BeanChecker
{
   public int check(Class beanClass)
   {
      if (beanClass == null)
      {
         return CHECK_RESULT_NO;
      }
      String beanClassName = beanClass.getName();
      // java, javax 及 org 包下的类不是bean
      if (beanClassName.startsWith("java.") || beanClassName.startsWith("javax.")
            || beanClassName.startsWith("org."))
      {
         return CHECK_RESULT_NO;
      }
      // 基本类型 数组 接口 不是bean
      if (beanClass.isPrimitive() || beanClass.isArray() || beanClass.isInterface())
      {
         return CHECK_RESULT_NO;
      }
      // 非public的类不是bean
      if (!Modifier.isPublic(beanClass.getModifiers()))
      {
         BeanTool.beanClassNameCheckMap.put(beanClassName, Boolean.FALSE);
         return CHECK_RESULT_NO;
      }
      // 实现了Collection接口的类不是bean
      if (Collection.class.isAssignableFrom(beanClass))
      {
         BeanTool.beanClassNameCheckMap.put(beanClassName, Boolean.FALSE);
         return CHECK_RESULT_NO;
      }
      // 实现了Map接口的类不是bean
      if (Map.class.isAssignableFrom(beanClass))
      {
         BeanTool.beanClassNameCheckMap.put(beanClassName, Boolean.FALSE);
         return CHECK_RESULT_NO;
      }
      try
      {
         BeanInfo info = Introspector.getBeanInfo(beanClass, Object.class);
         PropertyDescriptor[] arr = info.getPropertyDescriptors();
         // 不存在属性信息的类不是bean
         if (arr == null || arr.length == 0)
         {
            BeanTool.beanClassNameCheckMap.put(beanClassName, Boolean.FALSE);
            return CHECK_RESULT_NO;
         }
         // 不存在无参的构造函数的类不是bean
         if (beanClass.getConstructor(new Class[0]) == null)
         {
            BeanTool.beanClassNameCheckMap.put(beanClassName, Boolean.FALSE);
            return CHECK_RESULT_NO;
         }
      }
      catch (Throwable ex)
      {
         // 解析bean的过程中出现异常, 则判定为不是bean
         BeanTool.beanClassNameCheckMap.put(beanClassName, Boolean.FALSE);
         return CHECK_RESULT_NO;
      }
      return CHECK_RESULT_YES;
   }

}
