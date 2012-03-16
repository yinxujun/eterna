
package self.micromagic.eterna.share;

import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javassist.CtNewMethod;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.CannotCompileException;
import self.micromagic.util.Utility;

class JavassistTool
{
   /**
    * bean�������id
    */
   private static int BEAN_PROCESSER_ID = 1;

   /**
    * ����һ��bean�Ĵ�����
    *
    * @param beanClass           bean��
    * @param interfaceClass      ����ӿ�
    * @param methodHead          ����ͷ��
    * @param beanParamName       bean����������
    * @param unitTemplate        ��Ԫ����ģ��
    * @param primitiveTemplate   �������͵�Ԫ����ģ��
    * @param imports             Ҫ����İ�
    * @return                    ������Ӧ�Ĵ�����
    */
   static Object createBeanProcesser(Class beanClass, Class interfaceClass, String methodHead,
         String beanParamName, String unitTemplate, String primitiveTemplate, String linkTemplate,
         String[] imports)
         throws NotFoundException, CannotCompileException, InstantiationException, IllegalAccessException
   {
      ClassPool pool = ClassPool.getDefault();
      pool.appendClassPath(new ClassClassPath(beanClass));
      pool.appendClassPath(new ClassClassPath(interfaceClass));
      pool.appendClassPath(new ClassClassPath(JavassistTool.class));
      String namePrefix = "eterna.bp_" + (BEAN_PROCESSER_ID++) + ".";
      CtClass cc = pool.makeClass(namePrefix + beanClass.getName());
      cc.addInterface(pool.get(interfaceClass.getName()));
      if (imports != null)
      {
         for (int i = 0; i < imports.length; i++)
         {
            pool.importPackage(imports[i]);
         }
      }

      StringBuffer function = new StringBuffer(256);
      function.append(methodHead).append("\n{\n");
      function.append(beanClass.getName()).append(" beanObj = (")
            .append(beanClass.getName()).append(") ").append(beanParamName).append(";\n");
      boolean first = true;

      Map dataMap = new HashMap();

      Field[] fields = Tool.getBeanFields(beanClass);
      for (int i = 0; i < fields.length; i++)
      {
         if (!first)
         {
            function.append(Utility.resolveDynamicPropnames(linkTemplate, dataMap)).append("\n");
         }
         first = false;
         dataMap.clear();
         Field f = fields[i];
         dataMap.put("name", f.getName());
         dataMap.put("l_name", f.getName());
         dataMap.put("type", "field");
         if (f.getType().isPrimitive())
         {
            String pType = f.getType().getName();
            dataMap.put("primitive", pType);
            dataMap.put("u_primitive", Character.toUpperCase(pType.charAt(0)) + pType.substring(1));
            dataMap.put("value", "String.valueOf(beanObj." + f.getName() + ")");
            dataMap.put("o_value", "beanObj." + f.getName());
            function.append(Utility.resolveDynamicPropnames(primitiveTemplate, dataMap)).append("\n");
         }
         else
         {
            dataMap.put("value", "beanObj." + f.getName());
            function.append(Utility.resolveDynamicPropnames(unitTemplate, dataMap)).append("\n");
         }
      }
      Method[] methods = Tool.getBeanMethods(beanClass);
      for (int i = 0; i < methods.length; i++)
      {
         if (!first)
         {
            function.append(Utility.resolveDynamicPropnames(linkTemplate, dataMap)).append("\n");
         }
         first = false;
         Method m = methods[i];
         String fname = Tool.getBeanAttrName(m.getName());
         dataMap.put("name", m.getName());
         dataMap.put("l_name", fname);
         dataMap.put("type", "method");
         if (m.getReturnType().isPrimitive())
         {
            String pType = m.getReturnType().getName();
            dataMap.put("primitive", pType);
            dataMap.put("u_primitive", Character.toUpperCase(pType.charAt(0)) + pType.substring(1));
            dataMap.put("value", "String.valueOf(beanObj." + m.getName() + "())");
            dataMap.put("o_value", "beanObj." + m.getName() + "()");
            function.append(Utility.resolveDynamicPropnames(primitiveTemplate, dataMap)).append("\n");
         }
         else
         {
            dataMap.put("value", "beanObj." + m.getName() + "()");
            function.append(Utility.resolveDynamicPropnames(unitTemplate, dataMap)).append("\n");
         }
      }
      function.append("}");

      ClassLoader cl = beanClass.getClassLoader();
      if (cl == null) cl = Tool.class.getClassLoader();
      cc.addMethod(CtNewMethod.make(function.toString(), cc));
      Class c = cc.toClass(cl);

      return c.newInstance();
   }

}
