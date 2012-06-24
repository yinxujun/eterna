
package self.micromagic.eterna.share;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import self.micromagic.cg.BeanTool;
import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.util.StringTool;
import self.micromagic.util.Utility;

/**
 * �������Ҫ�õ���һЩ��������.
 */
public class Tool
{
   /**
    * ���ڼ�¼��־.
    */
   public static final Log log = Utility.createLog("eterna");

   public static final String CAPTION_TRANSLATE_TAG = "caption.translate";
   public static final String CAPTION_TRANSLATE_MAP_TAG = "caption.translate.map";
   public static final String CAPTION_TRANSLATE_MAP_FACTORY_TAG = "caption.translate.map.factory";

   /**
    * ���ݱ��ⷭ���б�����ý��з���.
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
    * ��ñ��ⷭ���õ�map.
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
    * ��factory��ע��bean���Ƶ�������.
    */
   public static final String BEAN_CLASS_NAMES = "bean.class.names";

   /**
    * ע����Ϊbean����, �������֮����","��";"����.
    */
   public static void registerBean(String classNames)
   {
      BeanTool.registerBean(classNames);
   }

   /**
    * �ж������������Ƿ���bean.
    */
   public static boolean isBean(Class c)
   {
      return BeanTool.checkBean(c);
   }

   /**
    * ����һ��bean�����������.
    *
    * @param beanClass           bean��
    * @param interfaceClass      ����ӿ�
    * @param methodHead          ����ͷ��
    * @param beanParamName       bean����������
    * @param unitTemplate        ��Ԫ����ģ��
    * @param primitiveTemplate   �������͵�Ԫ����ģ��
    * @param linkTemplate        �������͵�Ԫ֮�������ģ��
    * @param imports             Ҫ����İ�
    * @return                    ������Ӧ�Ĵ�����
    */
   public static Object createBeanPrinter(Class beanClass, Class interfaceClass, String methodHead,
         String beanParamName, String unitTemplate, String primitiveTemplate, String linkTemplate,
         String[] imports)
   {
      return BeanTool.createBeanProcesser("Printer", beanClass, interfaceClass, methodHead, beanParamName,
            unitTemplate, primitiveTemplate, linkTemplate, imports, BeanTool.BEAN_PROCESSER_TYPE_R);
   }

   /**
    * ����һ������ķ���.
    *
    * @param object          �����÷����Ķ���, ����˶����Ǹ�<code>Class</code>, ��
    *                        �����ķ����������Ǵ���ľ�̬����
    * @param methodName      ����������
    * @param args            ���õĲ���
    * @param parameterTypes  ���õĲ�������
    * @return  �����õķ����ķ��ؽ��
    */
   public static Object invokeExactMethod(Object object, String methodName, Object[] args,
         Class[] parameterTypes)
         throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
   {
      Class c;
      if (object instanceof Class)
      {
         c = (Class) object;
      }
      else
      {
         c = object.getClass();
      }
      Method method = c.getMethod(methodName, parameterTypes);
      return method.invoke(object, args);
   }

}
