
package self.micromagic.eterna.share;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.util.Jdk14Factory;
import self.micromagic.util.StringTool;
import self.micromagic.util.Utility;

/**
 * 一些辅助工具
 */
public class Tool
{
   public static final String CAPTION_TRANSLATE_TAG = "caption.translate";
   public static final String CAPTION_TRANSLATE_MAP_TAG = "caption.translate.map";
   public static final String CAPTION_TRANSLATE_MAP_FACTORY_TAG = "caption.translate.map.factory";

   /**
    * 根据标题翻译列表的配置进行翻译
    */
   public static String translateCaption(EternaFactory factory, String name)
         throws ConfigurationException
   {
      Map translateMap = (Map) factory.getAttribute(CAPTION_TRANSLATE_MAP_TAG);
      Object checkFactory = factory.getAttribute(CAPTION_TRANSLATE_MAP_FACTORY_TAG);
      if (translateMap == null || checkFactory != factory)
      {
         translateMap = getCaptionTranslateMap(factory);
         if (translateMap == null)
         {
            return null;
         }
      }
      return (String) translateMap.get(name);
   }

   /**
    * 获得标题翻译用的map
    */
   public static synchronized Map getCaptionTranslateMap(EternaFactory factory)
         throws ConfigurationException
   {
      String translateStr = (String) factory.getAttribute(CAPTION_TRANSLATE_TAG);
      if (translateStr == null)
      {
         return null;
      }
      Map translateMap = (Map) factory.getAttribute(CAPTION_TRANSLATE_MAP_TAG);
      Object checkFactory = factory.getAttribute(CAPTION_TRANSLATE_MAP_FACTORY_TAG);
      if (translateMap == null || checkFactory != factory)
      {
         EternaFactory share = factory.getShareFactory();
         boolean needTranslate = true;
         if (share != null)
         {
            String shareStr = (String) share.getAttribute(CAPTION_TRANSLATE_TAG);
            if (shareStr != null)
            {
               if (shareStr == translateStr)
               {
                  translateMap = getCaptionTranslateMap(share);
                  needTranslate = false;
               }
               else
               {
                  translateMap = new HashMap(getCaptionTranslateMap(share));
               }
            }
         }
         if (translateMap == null)
         {
            translateMap = new HashMap();
         }
         if (needTranslate)
         {
            String[] tmps = StringTool.separateString(
                  Utility.resolveDynamicPropnames(translateStr), ";", true);
            for (int i = 0; i < tmps.length; i++)
            {
               int index = tmps[i].indexOf('=');
               if (index != -1)
               {
                  translateMap.put(tmps[i].substring(0, index).trim(),
                        tmps[i].substring(index + 1).trim());
               }
            }
            factory.setAttribute(CAPTION_TRANSLATE_MAP_TAG, Collections.unmodifiableMap(translateMap));
         }
         else
         {
            factory.setAttribute(CAPTION_TRANSLATE_MAP_TAG, translateMap);
         }
         factory.setAttribute(CAPTION_TRANSLATE_MAP_FACTORY_TAG, factory);
      }
      return translateMap;
   }

   public static final String BEAN_CLASS_NAMES = "bean.class.names";
   /**
    * 存放作为bean的类名的集合
    */
   private static Set beanClassNameSet = new HashSet();

   /**
    * 注册作为bean的类, 多个类名之间用";"隔开
    */
   public static void registerBean(String classNames)
   {
      String[] names = StringTool.separateString(
            Utility.resolveDynamicPropnames(classNames), ";", true);
      for (int i = 0; i < names.length; i++)
      {
         beanClassNameSet.add(names[i]);
      }
   }

   /**
    * 判断所给出的类是否是bean
    */
   public static boolean isBean(Class c)
   {
      return isBean(c.getName());
   }

   /**
    * 判断所给出的类名是否是bean
    */
   public static boolean isBean(String className)
   {
      return beanClassNameSet.contains(className);
   }

   /**
    * 获得bean类中的所有公有非静态的属性
    */
   public static Field[] getBeanFields(Class c)
   {
      List result = new ArrayList();
      Field[] fs = c.getFields();
      for (int i = 0; i < fs.length; i++)
      {
         Field f = fs[i];
         if ((f.getModifiers() & Modifier.STATIC) == 0)
         {
            result.add(f);
         }
      }
      return (Field[]) result.toArray(new Field[0]);
   }

   /**
    * 获得bean类中的所有公有非静态的没有参数的get方法
    * 不过getClass除外
    */
   public static Method[] getBeanMethods(Class c)
   {
      List result = new ArrayList();
      Method[] ms = c.getMethods();
      for (int i = 0; i < ms.length; i++)
      {
         Method m = ms[i];
         if ((m.getModifiers() & Modifier.STATIC) == 0 && m.getParameterTypes().length == 0)
         {
            if (m.getReturnType() != void.class)
            {
               String fName = m.getName();
               if (!fName.equals("getClass") && fName.length() >= 3)
               {
                  String first3 = fName.substring(0, 3);
                  if (first3.equals("get") || first3.equals("Get") || first3.equals("GET"))
                  {
                     result.add(m);
                  }
               }
            }
         }
      }
      return (Method[]) result.toArray(new Method[0]);
   }

   private static int CBP_LOG_TYPE = 0;
   public static void setCreateBeanPrinterLogType(String type)
   {
      try
      {
         CBP_LOG_TYPE = Integer.parseInt(type);
      }
      catch (Exception ex) {}
   }
   static
   {
      try
      {
         Utility.addMethodPropertyManager(Jdk14Factory.EXCEPTION_LOG_PROPERTY,
               Tool.class, "setCreateBeanPrinterLogType");
      }
      catch (Throwable ex) {}
   }

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
    * @return                    返回相应的处理类
    */
   public static Object createBeanProcesser(Class beanClass, Class interfaceClass, String methodHead,
         String beanParamName, String unitTemplate, String primitiveTemplate, String[] imports)
   {
      try
      {
         return JavassistTool.createBeanProcesser(beanClass, interfaceClass, methodHead,
               beanParamName, unitTemplate, primitiveTemplate, imports);
      }
      catch (Throwable ex)
      {
         if (CBP_LOG_TYPE == 1)
         {
            EternaFactoryImpl.log.error("Error in createBeanPrinter.", ex);
         }
         return null;
      }
   }

}
