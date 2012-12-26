
package self.micromagic.cg;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

/**
 * Ĭ�ϵ�bean�����.
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
      // java, javax �� org ���µ��಻��bean
      if (beanClassName.startsWith("java.") || beanClassName.startsWith("javax.")
            || beanClassName.startsWith("org."))
      {
         return CHECK_RESULT_NO;
      }
      // �������� ���� �ӿ� ����bean
      if (beanClass.isPrimitive() || beanClass.isArray() || beanClass.isInterface())
      {
         return CHECK_RESULT_NO;
      }
      // ��public���಻��bean
      if (!Modifier.isPublic(beanClass.getModifiers()))
      {
         BeanTool.beanClassNameCheckMap.put(beanClassName, Boolean.FALSE);
         return CHECK_RESULT_NO;
      }
      // ʵ����Collection�ӿڵ��಻��bean
      if (Collection.class.isAssignableFrom(beanClass))
      {
         BeanTool.beanClassNameCheckMap.put(beanClassName, Boolean.FALSE);
         return CHECK_RESULT_NO;
      }
      // ʵ����Map�ӿڵ��಻��bean
      if (Map.class.isAssignableFrom(beanClass))
      {
         BeanTool.beanClassNameCheckMap.put(beanClassName, Boolean.FALSE);
         return CHECK_RESULT_NO;
      }
      try
      {
			BeanMethodInfo[] arr = BeanMethodInfo.getBeanMethods(beanClass);
         // ������������Ϣ����򹫹����ԵĲ���bean
         if (arr == null || arr.length == 0)
         {
				boolean hasPublicField = false;
				Field[] fields = beanClass.getFields();
				for (int i = 0; i < fields.length; i++)
				{
					if (!Modifier.isStatic(fields[i].getModifiers()))
					{
						hasPublicField = true;
						break;
					}
				}
				if (!hasPublicField)
				{
					BeanTool.beanClassNameCheckMap.put(beanClassName, Boolean.FALSE);
					return CHECK_RESULT_NO;
				}
         }
         // �������޲εĹ��캯�����಻��bean
         if (beanClass.getConstructor(new Class[0]) == null)
         {
            BeanTool.beanClassNameCheckMap.put(beanClassName, Boolean.FALSE);
            return CHECK_RESULT_NO;
         }
      }
      catch (Throwable ex)
      {
         // ����bean�Ĺ����г����쳣, ���ж�Ϊ����bean
         BeanTool.beanClassNameCheckMap.put(beanClassName, Boolean.FALSE);
         return CHECK_RESULT_NO;
      }
      return CHECK_RESULT_YES;
   }

}
