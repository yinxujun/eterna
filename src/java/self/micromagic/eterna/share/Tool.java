
package self.micromagic.eterna.share;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import self.micromagic.cg.BeanTool;
import self.micromagic.cg.ClassGenerator;
import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.util.StringTool;
import self.micromagic.util.Utility;

/**
 * 框架中需要用到的一些公共方法.
 */
public class Tool
{
   /**
    * 用于记录日志.
    */
   public static final Log log = Utility.createLog("eterna");

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
    * 注册作为bean的类, 多个类名之间用","或";"隔开.
    */
   public static void registerBean(String classNames)
   {
      BeanTool.registerBean(classNames);
   }

   /**
    * 判断所给出的类是否是bean.
    */
   public static boolean isBean(Class c)
   {
      return BeanTool.checkBean(c);
   }

   static int COMPILE_LOG_TYPE = 1;
   static
   {
      try
      {
         Utility.addFieldPropertyManager(ClassGenerator.COMPILE_LOG_PROPERTY, Tool.class,
               "COMPILE_LOG_TYPE", "1");
      }
      catch (Throwable ex) {}
   }

   /**
    * 生成一个bean的属性输出类.
    *
    * @param beanClass           bean类
    * @param interfaceClass      处理接口
    * @param methodHead          方法头部
    * @param beanParamName       bean参数的名称
    * @param unitTemplate        单元代码模板
    * @param primitiveTemplate   基本类型单元代码模板
    * @param linkTemplate        两个类型单元之间的连接模板
    * @param imports             要引入的包
    * @return                    返回相应的处理类
    */
   public static Object createBeanPrinter(Class beanClass, Class interfaceClass, String methodHead,
         String beanParamName, String unitTemplate, String primitiveTemplate, String linkTemplate,
         String[] imports)
   {
      return BeanTool.createBeanProcesser("Printer", beanClass, interfaceClass, methodHead, beanParamName,
            unitTemplate, primitiveTemplate, linkTemplate, imports, BeanTool.BEAN_PROCESSER_TYPE_R);
   }

}
