
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
import java.util.LinkedList;
import java.util.Collection;
import java.util.Iterator;
import java.beans.Introspector;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;

import org.apache.commons.logging.Log;
import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.util.StringTool;
import self.micromagic.util.Utility;

/**
 * 框架中需要用到的一些公共方法.
 */
public class Tool
{
   public static final Log log = Utility.createLog("eterna");

   /**
    * 设置是否要输出bean处理类创建的日志信息.
    */
   public static final String BP_CREATE_LOG_PROPERTY = "eterna.bp.create.log";

   /**
    * 设置是否要使用默认的bean检查器.
    */
   public static final String BP_USE_DBC_PROPERTY = "eterna.bp.use.defaultBeanChecker";

   public static final String CAPTION_TRANSLATE_TAG = "caption.translate";
   public static final String CAPTION_TRANSLATE_MAP_TAG = "caption.translate.map";
   public static final String CAPTION_TRANSLATE_MAP_FACTORY_TAG = "caption.translate.map.factory";

   /**
    * 根据标题翻译列表的配置进行翻译.
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
    * 获得标题翻译用的map.
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

   /**
    * 在factory中注册bean名称的属性名.
    */
   public static final String BEAN_CLASS_NAMES = "bean.class.names";

   /**
    * 存放作为bean的类名的集合.
    */
   private static Set beanClassNameSet = new HashSet();

   /**
    * 注册作为bean的类, 多个类名之间用","或";"隔开.
    */
   public static void registerBean(String classNames)
   {
      String[] names = StringTool.separateString(
            Utility.resolveDynamicPropnames(classNames), ",;", true);
      for (int i = 0; i < names.length; i++)
      {
         beanClassNameSet.add(names[i]);
      }
   }

   /**
    * 判断所给出的类是否是bean.
    */
   public static boolean isBean(Class c)
   {
      return checkBean(c);
   }

   /**
    * 判断所给出的类名是否是bean.
    */
   public static boolean isBean(String className)
   {
      return beanClassNameSet.contains(className);
   }

   /**
    * 判断所给出的类名是否是bean.
    */
   private static boolean checkBean(Class type)
   {
      String className = type.getName();
      if (beanClassNameSet.contains(className))
      {
         return true;
      }
      if (beanCheckers.size() > 0)
      {
         Iterator itr = beanCheckers.iterator();
         while (itr.hasNext())
         {
            BeanChecker bc = (BeanChecker) itr.next();
            if (bc.check(type) == BEAN_CHECK_TYPE_IS_BEAN)
            {
               beanClassNameSet.add(className);
               return true;
            }
            else if (bc.check(type) == BEAN_CHECK_TYPE_NOT_BEAN)
            {
               return false;
            }
         }
      }
      if (BP_USE_DEFAULT_BEAN_CHECKER)
      {
         if (defaultBeanChecker.check(type) == BEAN_CHECK_TYPE_IS_BEAN)
         {
            beanClassNameSet.add(className);
            return true;
         }
      }
      return false;
   }

   /**
    * 一个用于检查给出的类名是否是一个bean的检测器列表.
    */
   private static List beanCheckers = new LinkedList();

   private static BeanChecker defaultBeanChecker = new DefaultBeanChecker();

   /**
    * 注册一般bean的检测器.
    */
   public synchronized static void registerBeanChecker(BeanChecker bc)
   {
      if (!beanCheckers.contains(bc))
      {
         beanCheckers.add(bc);
      }
   }

   /**
    * 去除一个bean的检测器.
    */
   public synchronized static void removeBeanChecker(BeanChecker bc)
   {
      beanCheckers.remove(bc);
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
      return (Field[]) result.toArray(new Field[result.size()]);
   }

   /**
    * 获得bean类中的所有公开的get方法
    */
   public static BeanMethodInfo[] getBeanReadMethods(Class c)
   {
      try
      {
         BeanInfo info = Introspector.getBeanInfo(c, Object.class);
         PropertyDescriptor[] pds = info.getPropertyDescriptors();
         List result = new ArrayList();
         for (int i = 0; i < pds.length; i++)
         {
            Method m = pds[i].getReadMethod();
            if (m != null)
            {
               result.add(new BeanMethodInfo(pds[i].getName(), m, pds[i].getPropertyType(), false, true));
            }
         }
         return (BeanMethodInfo[]) result.toArray(new BeanMethodInfo[result.size()]);
      }
      catch (IntrospectionException ex)
      {
         log.error("Error in getBeanReadMethods.", ex);
         return new BeanMethodInfo[0];
      }
   }

   /**
    * 获得bean类中的所有公开的set方法
    */
   public static BeanMethodInfo[] getBeanWriteMethods(Class c)
   {
      try
      {
         BeanInfo info = Introspector.getBeanInfo(c, Object.class);
         PropertyDescriptor[] pds = info.getPropertyDescriptors();
         List result = new ArrayList();
         for (int i = 0; i < pds.length; i++)
         {
            Method m = pds[i].getWriteMethod();
            if (m != null)
            {
               result.add(new BeanMethodInfo(pds[i].getName(), m, pds[i].getPropertyType(), true, false));
            }
         }
         return (BeanMethodInfo[]) result.toArray(new BeanMethodInfo[result.size()]);
      }
      catch (IntrospectionException ex)
      {
         log.error("Error in getBeanWriteMethods.", ex);
         return new BeanMethodInfo[0];
      }
   }

   /**
    * 获取给定类的包路径字符串.
    */
   public static String getPackageString(Class c)
   {
      String cName = c.getName();
      int lastIndex = cName.lastIndexOf('.');
      if (lastIndex == -1)
      {
         return "";
      }
      return cName.substring(0, lastIndex);
   }

   static int BP_CREATE_LOG_TYPE = 0;
   public static void setBeanProcesserLogType(String type)
   {
      try
      {
         BP_CREATE_LOG_TYPE = Integer.parseInt(type);
      }
      catch (Exception ex) {}
   }
   private static boolean BP_USE_DEFAULT_BEAN_CHECKER = true;
   public static void setUseDefaultBeanChecker(String use)
   {
      if (use == null)
      {
         return;
      }
      BP_USE_DEFAULT_BEAN_CHECKER = "true".equalsIgnoreCase(use);
   }
   static
   {
      try
      {
         Utility.addMethodPropertyManager(BP_CREATE_LOG_PROPERTY, Tool.class, "setBeanProcesserLogType");
         Utility.addMethodPropertyManager(BP_USE_DBC_PROPERTY, Tool.class, "setUseDefaultBeanChecker");
      }
      catch (Throwable ex) {}
   }

   /**
    * 处理读取的过程.
    */
   public static int BEAN_PROCESSER_TYPE_R = 0;

   /**
    * 处理写入的过程.
    */
   public static int BEAN_PROCESSER_TYPE_W = 1;

   /**
    * 生成一个bean的处理类
    *
    * @param beanClass           bean类
    * @param interfaceClass      处理接口
    * @param methodHead          方法头部
    * @param beanParamName       bean参数的名称
    * @param unitTemplate        单元代码模板
    * @param primitiveTemplate   基本类型单元代码模板
    * @param linkTemplate        两个类型单元之间的连接模板
    * @param imports             要引入的包
    * @param processerType       处理的是设置的过程还是读取的过程
    * @return                    返回相应的处理类
    */
   public static Object createBeanProcesser(Class beanClass, Class interfaceClass, String methodHead,
         String beanParamName, String unitTemplate, String primitiveTemplate, String linkTemplate,
         String[] imports, int processerType)
   {
      try
      {
         return JavassistTool.createBeanProcesser(beanClass, interfaceClass, methodHead,
               beanParamName, unitTemplate, primitiveTemplate, linkTemplate, imports,
               processerType);
      }
      catch (Throwable ex)
      {
         if (BP_CREATE_LOG_TYPE > 0)
         {
            log.error("Error in createBeanPrinter.", ex);
         }
         return null;
      }
   }

   /**
    * bean检查器的检查结果，是一个bean.
    */
   public static final int BEAN_CHECK_TYPE_IS_BEAN = 1;

   /**
    * bean检查器的检查结果，不是一个bean.
    */
   public static final int BEAN_CHECK_TYPE_NOT_BEAN = -1;

   /**
    * bean检查器的检查结果，不明无法判断是否是bean.
    */
   public static final int BEAN_CHECK_TYPE_UNKNOW = 0;

   /**
    * 判断给出的类名是否为bean的检查器.
    */
   public interface BeanChecker
   {
      public int check(Class beanClass);

   }

   private static class DefaultBeanChecker
         implements BeanChecker
   {
      public int check(Class beanClass)
      {
         if (beanClass == null)
         {
            return BEAN_CHECK_TYPE_NOT_BEAN;
         }
         String beanClassName = beanClass.getName();
         if (beanClassName.startsWith("java.") || beanClassName.startsWith("javax."))
         {
            return BEAN_CHECK_TYPE_NOT_BEAN;
         }
         if (beanClass.isPrimitive() || beanClass.isArray() || beanClass.isInterface())
         {
            return BEAN_CHECK_TYPE_NOT_BEAN;
         }
         if (Collection.class.isAssignableFrom(beanClass))
         {
            return BEAN_CHECK_TYPE_NOT_BEAN;
         }
         if (Map.class.isAssignableFrom(beanClass))
         {
            return BEAN_CHECK_TYPE_NOT_BEAN;
         }
         return BEAN_CHECK_TYPE_IS_BEAN;
      }

   }

   /**
    * 描述bean的一个方法的信息类.
    */
   public static class BeanMethodInfo
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

}
