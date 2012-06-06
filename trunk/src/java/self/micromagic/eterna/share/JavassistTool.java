
package self.micromagic.eterna.share;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import self.micromagic.util.StringAppender;
import self.micromagic.util.StringTool;
import self.micromagic.util.Utility;

class JavassistTool
{
   /**
    * 基本类型单对应的外覆类.
    */
   private static Map primitiveWrapClass = new HashMap();

   static
   {
      primitiveWrapClass.put("boolean", "Boolean");
      primitiveWrapClass.put("char", "Character");
      primitiveWrapClass.put("byte", "Byte");
      primitiveWrapClass.put("short", "Short");
      primitiveWrapClass.put("int", "Integer");
      primitiveWrapClass.put("long", "Long");
      primitiveWrapClass.put("float", "Float");
      primitiveWrapClass.put("double", "Double");
   }

   /**
    * bean处理类的id
    */
   private static volatile int BEAN_PROCESSER_ID = 1;

   /**
    * 生成一个bean的处理类
    *
    * @param beanClass           bean类
    * @param interfaceClass      处理接口
    * @param methodHead          方法头部
    * @param beanParamName       bean参数的名称
    * @param unitTemplate        单元代码模板
    * @param primitiveTemplate   基本类型单元代码模板
    * @param imports             要引入的包
    * @param processerType       处理的是设置的过程还是读取的过程
    * @return                    返回相应的处理类
    */
   static Object createBeanProcesser(Class beanClass, Class interfaceClass, String methodHead,
         String beanParamName, String unitTemplate, String primitiveTemplate, String linkTemplate,
         String[] imports, int processerType)
         throws NotFoundException, CannotCompileException, InstantiationException, IllegalAccessException
   {
      CtClass cc = createClass(beanClass, interfaceClass, imports);
      StringAppender function = StringTool.createStringAppender(256);
      function.append(methodHead).appendln().append("{").appendln();
      function.append("   ").append(beanClass.getName()).append(" ").append(BeanTool.BEAN_NAME).append(" = (")
            .append(beanClass.getName()).append(") ").append(beanParamName).append(";").appendln();;
      boolean first = true;

      Map dataMap = new HashMap();

      Field[] fields = Tool.getBeanFields(beanClass);
      for (int i = 0; i < fields.length; i++)
      {
         if (processerType == Tool.BEAN_PROCESSER_TYPE_W
               && (fields[i].getModifiers() & Modifier.FINAL) != 0)
         {
            continue;
         }
         if (!first)
         {
            function.append(Utility.resolveDynamicPropnames(linkTemplate, dataMap)).append("\n");
         }
         first = false;
         dataMap.clear();
         Field f = fields[i];
         dataMap.put("name", f.getName());
         dataMap.put("type", "field");
         if (f.getType().isPrimitive())
         {
            String pType = f.getType().getName();
            dataMap.put("primitive", pType);
            dataMap.put("value", "String.valueOf(" + BeanTool.BEAN_NAME + "." + f.getName() + ")");
            dataMap.put("o_value", BeanTool.BEAN_NAME + "." + f.getName());
            dataMap.put("wrapName", primitiveWrapClass.get(pType));
            function.append(Utility.resolveDynamicPropnames(primitiveTemplate, dataMap)).append("\n");
         }
         else
         {
            dataMap.put("value", BeanTool.BEAN_NAME + "." + f.getName());
            function.append(Utility.resolveDynamicPropnames(unitTemplate, dataMap)).append("\n");
         }
      }
      Tool.BeanMethodInfo[] methods = processerType == Tool.BEAN_PROCESSER_TYPE_W ?
            Tool.getBeanWriteMethods(beanClass) : Tool.getBeanReadMethods(beanClass);
      for (int i = 0; i < methods.length; i++)
      {
         if (!first)
         {
            function.append(Utility.resolveDynamicPropnames(linkTemplate, dataMap)).append("\n");
         }
         first = false;
         Tool.BeanMethodInfo m = methods[i];
         dataMap.put("name", m.name);
         dataMap.put("type", "method");
         if (m.type.isPrimitive())
         {
            String pType = m.type.getName();
            dataMap.put("primitive", pType);
            dataMap.put("value", "String.valueOf(" + BeanTool.BEAN_NAME + "." + m.method.getName() + "())");
            dataMap.put("o_value", BeanTool.BEAN_NAME + "." + m.method.getName() + "()");
            dataMap.put("wrapName", primitiveWrapClass.get(pType));
            function.append(Utility.resolveDynamicPropnames(primitiveTemplate, dataMap)).append("\n");
         }
         else
         {
            dataMap.put("value", BeanTool.BEAN_NAME + "." + m.method.getName() + "()");
            function.append(Utility.resolveDynamicPropnames(unitTemplate, dataMap)).append("\n");
         }
      }
      function.append("}");
      if (Tool.BP_CREATE_LOG_TYPE > 0)
      {
         Tool.log.info(cc.getName() + ":" + function);
      }

      ClassLoader cl = beanClass.getClassLoader();
      if (cl == null) cl = Tool.class.getClassLoader();
      cc.addMethod(CtNewMethod.make(function.toString(), cc));
      Class c = cc.toClass(cl);

      return c.newInstance();
   }


   /**
    * 生成一个bean的处理类
    *
    * @param beanClass           bean类
    * @param interfaceClass      处理接口
    * @param methodHead          方法头部
    * @param beanParamName       bean参数的名称
    * @param beginCode           方法开始部分的代码
    * @param endCode             方法结束部分的代码
    * @param unitProcesser       属性单元的处理器
    * @param imports             要引入的包
    * @param processerType       处理的是设置的过程还是读取的过程
    * @return                    返回相应的处理类
    */
   static Object createBeanProcesser(Class beanClass, Class interfaceClass, String methodHead, String beanParamName,
         String beginCode, String endCode, UnitProcesser unitProcesser, String[] imports, int processerType)
         throws NotFoundException, CannotCompileException, InstantiationException, IllegalAccessException
   {
      CtClass cc = createClass(beanClass, interfaceClass, imports);
      StringAppender function = StringTool.createStringAppender(256);
      function.append(methodHead).appendln().append("{").appendln();
      function.append("   ").append(beanClass.getName()).append(" ").append(BeanTool.BEAN_NAME).append(" = (")
            .append(beanClass.getName()).append(") ").append(beanParamName).append(";").appendln();
      function.append(beginCode).appendln();

      Field[] fields = Tool.getBeanFields(beanClass);
      for (int i = 0; i < fields.length; i++)
      {
         if (processerType == Tool.BEAN_PROCESSER_TYPE_W
               && (fields[i].getModifiers() & Modifier.FINAL) != 0)
         {
            continue;
         }
         String code;
         Field f = fields[i];
         if (f.getType().isPrimitive())
         {
            String wrapName = (String) primitiveWrapClass.get(f.getType().getName());
            code = unitProcesser.getFieldCode(f, f.getType(), wrapName, processerType);
         }
         else
         {
            code = unitProcesser.getFieldCode(f, f.getType(), null, processerType);
         }
         function.append(code).appendln();
      }
      Tool.BeanMethodInfo[] methods = processerType == Tool.BEAN_PROCESSER_TYPE_W ?
            Tool.getBeanWriteMethods(beanClass) : Tool.getBeanReadMethods(beanClass);
      for (int i = 0; i < methods.length; i++)
      {
         String code;
         Tool.BeanMethodInfo m = methods[i];
         if (m.type.isPrimitive())
         {
            String wrapName = (String) primitiveWrapClass.get(m.type.getName());
            code = unitProcesser.getMethodCode(m, m.type, wrapName, processerType);
         }
         else
         {
            code = unitProcesser.getMethodCode(m, m.type, null, processerType);
         }
         function.append(code).appendln();
      }
      function.append(endCode).appendln().append("}");
      if (Tool.BP_CREATE_LOG_TYPE > 0)
      {
         Tool.log.info(cc.getName() + ":" + function);
      }

      ClassLoader cl = beanClass.getClassLoader();
      if (cl == null) cl = Tool.class.getClassLoader();
      cc.addMethod(CtNewMethod.make(function.toString(), cc));
      Class c = cc.toClass(cl);

      return c.newInstance();
   }

   /**
    * 对一个bean生成一组属性的处理类.
    *
    * @param beanClass           bean类
    * @param interfaceClass      处理接口
    * @param methodHead          方法头部
    * @param beanParamName       bean参数的名称
    * @param beginCode           方法开始部分的代码
    * @param endCode             方法结束部分的代码
    * @param unitProcesser       属性单元的处理器
    * @param imports             要引入的包
    * @param processerType       处理的是设置的过程还是读取的过程
    * @return                    返回相应的处理类
    */
   static Map createPropertyProcessers(Class beanClass, Class interfaceClass, String methodHead, String beanParamName,
         String beginCode, String endCode, UnitProcesser unitProcesser, String[] imports, int processerType)
         throws NotFoundException, CannotCompileException, InstantiationException, IllegalAccessException
   {
      Map result = new HashMap();
      String beginCode0 = StringTool.createStringAppender(256)
            .append(methodHead).appendln().append("{").appendln()
            .append("   ").append(beanClass.getName()).append(" ").append(BeanTool.BEAN_NAME).append(" = (")
            .append(beanClass.getName()).append(") ").append(beanParamName).append(";").appendln()
            .append(beginCode).appendln().toString();
      String endCode0 = StringTool.isEmpty(endCode) ? "}"
            : StringTool.createStringAppender(endCode, 5, false).appendln().append("}").toString();

      Field[] fields = Tool.getBeanFields(beanClass);
      for (int i = 0; i < fields.length; i++)
      {
         if (processerType == Tool.BEAN_PROCESSER_TYPE_W
               && (fields[i].getModifiers() & Modifier.FINAL) != 0)
         {
            continue;
         }
         String code;
         Field f = fields[i];
         if (f.getType().isPrimitive())
         {
            String wrapName = (String) primitiveWrapClass.get(f.getType().getName());
            code = unitProcesser.getFieldCode(f, f.getType(), wrapName, processerType);
         }
         else
         {
            code = unitProcesser.getFieldCode(f, f.getType(), null, processerType);
         }
         Object p = createPropertyProcesser(beanClass, interfaceClass, beginCode0, code, endCode0, imports);
         result.put(f.getName(), new ProcesserInfo(f.getName(), f.getType(), p));
      }
      Tool.BeanMethodInfo[] methods = processerType == Tool.BEAN_PROCESSER_TYPE_W ?
            Tool.getBeanWriteMethods(beanClass) : Tool.getBeanReadMethods(beanClass);
      for (int i = 0; i < methods.length; i++)
      {
         String code;
         Tool.BeanMethodInfo m = methods[i];
         if (m.type.isPrimitive())
         {
            String wrapName = (String) primitiveWrapClass.get(m.type.getName());
            code = unitProcesser.getMethodCode(m, m.type, wrapName, processerType);
         }
         else
         {
            code = unitProcesser.getMethodCode(m, m.type, null, processerType);
         }
         Object p = createPropertyProcesser(beanClass, interfaceClass, beginCode0, code, endCode0, imports);
         result.put(m.name, new ProcesserInfo(m.name, m.type, p));
      }

      return result;
   }
   /**
    * 生成一个属性的处理类.
    *
    * @param beanClass           bean类
    * @param interfaceClass      处理接口
    * @param beginCode           方法起始部分的代码
    * @param bodyCode            方法主题部分的代码
    * @param endCode             方法结束部分的代码
    * @param imports             要引入的包
    * @return                    返回相应的处理类
    */
   static Object createPropertyProcesser(Class beanClass, Class interfaceClass, String beginCode, String bodyCode,
         String endCode, String[] imports)
         throws NotFoundException, CannotCompileException, InstantiationException, IllegalAccessException
   {
      CtClass cc = createClass(beanClass, interfaceClass, imports);
      StringAppender function = StringTool.createStringAppender(256);
      function.append(beginCode).appendln()
            .append(bodyCode).appendln()
            .append(endCode).appendln();
      if (Tool.BP_CREATE_LOG_TYPE > 0)
      {
         Tool.log.info(cc.getName() + ":" + function);
      }
      ClassLoader cl = beanClass.getClassLoader();
      if (cl == null) cl = Tool.class.getClassLoader();
      cc.addMethod(CtNewMethod.make(function.toString(), cc));
      Class c = cc.toClass(cl);
      return c.newInstance();
   }

   private static CtClass createClass(Class beanClass, Class interfaceClass, String[] imports)
         throws NotFoundException
   {
      ClassPool pool = new ClassPool();
      pool.appendSystemPath();
      pool.appendClassPath(new ClassClassPath(beanClass));
      if (beanClass.getClassLoader() != interfaceClass.getClassLoader())
      {
         pool.appendClassPath(new ClassClassPath(interfaceClass));
      }
      if (beanClass.getClassLoader() != JavassistTool.class.getClassLoader()
            || interfaceClass.getClassLoader() != JavassistTool.class.getClassLoader())
      {
         pool.appendClassPath(new ClassClassPath(JavassistTool.class));
      }
      String nameSuffix;
      synchronized (JavassistTool.class)
      {
         nameSuffix = "$$EBP_" + beanClass.hashCode() + "_" + (BEAN_PROCESSER_ID++);
      }
      CtClass cc = pool.makeClass(beanClass.getName() + nameSuffix);
      cc.addInterface(pool.get(interfaceClass.getName()));
      if (imports != null)
      {
         for (int i = 0; i < imports.length; i++)
         {
            if (!StringTool.isEmpty(imports[i]))
            {
               pool.importPackage(imports[i]);
            }
         }
      }
      return cc;
   }

   public interface UnitProcesser
   {
      public String getFieldCode(Field f, Class type, String wrapName, int processerType);

      public String getMethodCode(Tool.BeanMethodInfo m, Class type, String wrapName, int processerType);

   }

   static class ProcesserInfo
   {
      public final String name;
      public final Class type;
      public final Object processer;

      public ProcesserInfo(String name, Class type, Object processer)
      {
         this.name = name;
         this.type = type;
         this.processer = processer;
      }

   }

}
