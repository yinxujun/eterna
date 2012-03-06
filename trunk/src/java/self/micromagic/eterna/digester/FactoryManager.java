
package self.micromagic.eterna.digester;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Constructor;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.zip.DeflaterOutputStream;

import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.share.EternaFactoryImpl;
import self.micromagic.eterna.share.EternaInitialize;
import self.micromagic.eterna.share.Factory;
import self.micromagic.eterna.share.ThreadCache;
import self.micromagic.eterna.share.Tool;
import self.micromagic.util.StringRef;
import self.micromagic.util.Utility;
import self.micromagic.util.ObjectRef;
import self.micromagic.util.FormatTool;
import self.micromagic.coder.Base64;

/**
 * ����˵��:
 *
 * self.micromagic.eterna.digester.initfiles
 * Ҫ����ȫ�ֳ�ʼ�����ļ��б�
 *
 * self.micromagic.eterna.digester.subinitfiles
 * Ҫ����ȫ�ֳ�ʼ�������ļ��б�,
 * ���ļ��б��еĶ���Ḳ�ǵ�ȫ�ֳ�ʼ�����ļ��б��е�ͬ������
 *
 * self.micromagic.eterna.digester.initClasses
 * Ҫ����ȫ�ֳ�ʼ���������б�
 *
 * self.micromagic.eterna.digester.loadDefaultConfig
 * ȫ�ֳ�ʼ��ʱ�Ƿ�Ҫ����Ĭ�ϵ�����
 * cp:self/micromagic/eterna/share/eterna_share.xml;cp:eterna_global.xml;
 *
 */
public class FactoryManager
{
   public static final Log log = Tool.log;

   /**
    * Ҫ����ȫ�ֳ�ʼ�����ļ��б�.
    */
   public static final String INIT_FILES_PROPERTY
         = "self.micromagic.eterna.digester.initfiles";

   /**
    * Ҫ����ȫ�ֳ�ʼ�������ļ��б�, ���ļ��б��еĶ���Ḳ�ǵ�ȫ�ֳ�ʼ��
    * ���ļ��б��е�ͬ������.
    */
   public static final String INIT_SUBFILES_PROPERTY
         = "self.micromagic.eterna.digester.subinitfiles";

   /**
    * Ҫ����ȫ�ֳ�ʼ���������б�.
    */
   public static final String INIT_CLASSES_PROPERTY
         = "self.micromagic.eterna.digester.initClasses";

   /**
    * ȫ�ֳ�ʼ��ʱ�Ƿ�Ҫ����Ĭ�ϵ�����.
    */
   public static final String LOAD_DEFAULT_CONFIG
         = "self.micromagic.eterna.digester.loadDefaultConfig";

   /**
    * ȫ�ֳ�ʼ��ʱҪ�����Ĭ������.
    */
   public static final String DEFAULT_CONFIG_FILE
         = "cp:self/micromagic/eterna/share/eterna_share.xml;cp:eterna_global.xml;";

   /**
    * ʵ����ʼ�����ļ��б�.
    */
   public static final String CONFIG_INIT_FILES = "initFiles";

   /**
    * ʵ����ʼ���ĸ��ļ��б�.
    */
   public static final String CONFIG_INIT_PARENTFILES = "parentFiles";

   /**
    * ʵ����ʼ���������б�.
    */
   public static final String CONFIG_INIT_NAME = "initConfig";

   /**
    * ʵ����ʼ���ĸ������б�.
    */
   public static final String CONFIG_INIT_PARENTNAME = "parentConfig";

   /**
    * ��ʼ��ʱʹ�õ��̻߳���.
    */
   public static final String ETERNA_INIT_CACHE = "eterna.init.cache";

   /**
    * Ĭ����Ҫ���صĹ���EternaFactory.
    */
   public static final String ETERNA_FACTORY
         = "self.micromagic.eterna.EternaFactory";

   /**
    * ��ʼ��ʱ�Ƿ���Ҫ�Խű����Խ����﷨���.
    */
   public static final String CHECK_GRAMMER_PROPERTY
         = "self.micromagic.eterna.digester.checkGrammer";
   private static boolean checkGrammer = true;

   private static Document logDocument = null;
   private static Element logs = null;
   private static Map classInstanceMap = new HashMap();
   private static GlobeImpl globeInstance;
   private static Instance current;
   private static Factory currentFactory;

   /**
    * ��ʶ��ǰ�Ƿ��ڳ�ʼ��������
    */
   private static int superInitLevel = 0;

   static
   {
      globeInstance = new GlobeImpl();
      current = globeInstance;
      try
      {
         reInitEterna();
         Utility.addMethodPropertyManager(CHECK_GRAMMER_PROPERTY, FactoryManager.class, "setCheckGrammer");
      }
      catch (Throwable ex)
      {
         log.error("Error in class init.", ex);
      }
   }

   /**
    * �Ƿ��ڳ�ʼ��������
    */
   public static boolean isSuperInit()
   {
      return superInitLevel > 0;
   }

   /**
    * ��ʼ�������õ�level�ȼ�, 0Ϊ�������� 1Ϊ��һ�� 2Ϊ�ڶ��� ...
    */
   public static int getSuperInitLevel()
   {
      return superInitLevel;
   }

   /**
    * ��ʼ��ʱ�Ƿ���Ҫ�Խű����Խ����﷨���.
    */
   public static boolean isCheckGrammer()
   {
      return checkGrammer;
   }

   /**
    * ���ó�ʼ��ʱ�Ƿ���Ҫ�Խű����Խ����﷨���.
    *
    * @param check   ���trueΪ��Ҫ
    */
   public static void setCheckGrammer(String check)
   {
      checkGrammer = "true".equalsIgnoreCase(check);
   }

   /**
    * ����һ����¼SQL��־�Ľڵ�.
    *
    * @param name   SQL����������
    */
   public static synchronized Element createLogNode(String name)
   {
      if (logDocument == null)
      {
         logDocument = DocumentHelper.createDocument();
         Element root = logDocument.addElement("eterna");
         logs = root.addElement("logs");
      }
      if (logs.elements().size() > 2048)
      {
         // ���ڵ����ʱ, ���������ӵļ����ڵ�
         Iterator itr = logs.elementIterator();
         try
         {
            for (int i = 0; i < 1536; i++)
            {
               itr.next();
               itr.remove();
            }
         }
         catch (Exception ex)
         {
            // ��ȥ���ڵ����ʱ, �������־
            log.warn("Remove sql log error.", ex);
            logDocument = null;
            return createLogNode(name);
         }
      }
      return logs.addElement(name);
   }

   /**
    * ����¼����־���.
    *
    * @param out     ��־�������
    * @param clear   �Ƿ�Ҫ�������������־
    */
   public static synchronized void printLog(Writer out, boolean clear)
         throws IOException
   {
      if (logDocument == null)
      {
         return;
      }
      XMLWriter writer = new XMLWriter(out);
      writer.write(logDocument);
      writer.flush();
      if (clear)
      {
         logDocument = null;
         logs = null;
      }
   }

   /**
    * ��õ�ǰ���ڳ�ʼ����Factory.
    * ֻ���ڳ�ʼ��ʱ�Ż᷵��ֵ, ���򷵻�null.
    */
   public static Factory getCurrentFactory()
   {
      return currentFactory;
   }

   /**
    * ���ȫ�ֵĹ�����������ʵ��.
    */
   public static FactoryManager.Instance getGlobeFactoryManager()
   {
      return globeInstance;
   }

   /**
    * ���� �������� ���������� ���������ַ���.
    */
   private static String getConfig(String initConfig, String[] parentConfig)
   {
      List result = new ArrayList();
      if (initConfig != null)
      {
         parseConfig(initConfig, result);
      }
      if (result.size() == 0)
      {
         // �������һ���մ�����ռλ
         result.add("");
      }
      if (parentConfig != null)
      {
         for (int i = 0; i < parentConfig.length; i++)
         {
            if (parentConfig[i] != null)
            {
               parseConfig(parentConfig[i], result);
            }
         }
      }
      if (result.size() <= 1 && initConfig == null)
      {
         return null;
      }
      StringBuffer buf = new StringBuffer();
      Iterator itr = result.iterator();
      while (itr.hasNext())
      {
         buf.append(itr.next()).append('|');
      }
      return buf.toString();
   }

   /**
    * ��������.
    *
    * @param config    Ҫ����������
    * @param result    ������Ľ���б�, ���ν����Ľ��ҲҪ�Ž�ȥ
    */
   private static void parseConfig(String config, List result)
   {
      String temp;
      List tmpSet = new ArrayList();
      if (config != null)
      {
         StringTokenizer token = new StringTokenizer(FactoryManager.resolveLocate(config), ";");
         while (token.hasMoreTokens())
         {
            temp = token.nextToken().trim();
            if (temp.length() == 0)
            {
               continue;
            }
            tmpSet.add(temp);
         }
      }
      StringBuffer buf = new StringBuffer();
      Iterator itr = tmpSet.iterator();
      while (itr.hasNext())
      {
         buf.append(itr.next()).append(';');
      }
      result.add(buf.toString());
   }

   /**
    * ����һ�����ȡ������������ʵ��.
    * �Ὣ[����.xml]��Ϊ��������ȡ.
    *
    * @param baseClass    ��ʼ���Ļ�����
    */
   public static FactoryManager.Instance createClassFactoryManager(Class baseClass)
   {
      return createClassFactoryManager(baseClass, null);
   }

   /**
    * ����һ���༰���û�ȡ������������ʵ��.
    *
    * @param baseClass    ��ʼ���Ļ�����
    * @param initConfig   ��ʼ��������
    */
   public static FactoryManager.Instance createClassFactoryManager(Class baseClass, String initConfig)
   {
      String conf = getConfig(initConfig, null);
      Object instance = conf != null ? classInstanceMap.get(baseClass.getName() + "=" + conf)
            : classInstanceMap.get(baseClass.getName());
      if (instance != null && instance instanceof ClassImpl)
      {
         ClassImpl ci = (ClassImpl) instance;
         // ������ڵ�����ͬ�����¼��أ���ʹ���˲�ͬ��ClassLoaderʱ�����ڵ���ͻ᲻ͬ��
         if (ci.baseClass == baseClass)
         {
            return ci;
         }
      }
      return createClassFactoryManager(baseClass, null, initConfig, null, false);
   }

   /**
    * ����һ���༰���û�ȡ������������ʵ��.
    *
    * @param baseClass    ��ʼ���Ļ�����
    * @param initConfig   ��ʼ��������
    * @param registry     �Ƿ���Ҫ����ע���ʵ��, ��Ϊtrue��Ὣԭ���Ѵ��ڵ�ʵ��ɾ��
    */
   public static FactoryManager.Instance createClassFactoryManager(Class baseClass,
         String initConfig, boolean registry)
   {
      return createClassFactoryManager(baseClass, null, initConfig, null, registry);
   }

   /**
    * ����һ���༰���û�ȡ������������ʵ��.
    *
    * @param baseClass    ��ʼ���Ļ�����
    * @param baseObj      �������һ��ʵ��
    * @param initConfig   ��ʼ��������
    * @param registry     �Ƿ���Ҫ����ע���ʵ��, ��Ϊtrue��Ὣԭ���Ѵ��ڵ�ʵ��ɾ��
    */
   public static FactoryManager.Instance createClassFactoryManager(Class baseClass,
         Object baseObj, String initConfig, boolean registry)
   {
      return createClassFactoryManager(baseClass, baseObj, initConfig, null, registry);
   }

   /**
    * ����һ���༰���û�ȡ������������ʵ��.
    *
    * @param baseClass        ��ʼ���Ļ�����
    * @param baseObj          �������һ��ʵ��
    * @param initConfig       ��ʼ��������
    * @param parentConfig     ��ʼ���ĸ�����
    * @param registry         �Ƿ���Ҫ����ע���ʵ��, ��Ϊtrue��Ὣԭ���Ѵ��ڵ�ʵ��ɾ��
    */
   public static FactoryManager.Instance createClassFactoryManager(Class baseClass,
         Object baseObj, String initConfig, String[] parentConfig, boolean registry)
   {
      Class instanceClass = null;
      if (Instance.class.isAssignableFrom(baseClass))
      {
         instanceClass = baseClass;
      }
      return createClassFactoryManager(baseClass, baseObj, initConfig, parentConfig, instanceClass, registry);
   }

   /**
    * ����һ���༰���û�ȡ������������ʵ��.
    *
    * @param baseClass        ��ʼ���Ļ�����
    * @param baseObj          �������һ��ʵ��
    * @param initConfig       ��ʼ��������
    * @param parentConfig     ��ʼ���ĸ�����
    * @param instanceClass    ������������ʵ����
    * @param registry         �Ƿ���Ҫ����ע���ʵ��, ��Ϊtrue��Ὣԭ���Ѵ��ڵ�ʵ��ɾ��
    */
   public static synchronized FactoryManager.Instance createClassFactoryManager(Class baseClass,
         Object baseObj, String initConfig, String[] parentConfig, Class instanceClass, boolean registry)
   {
      String conf = getConfig(initConfig, parentConfig);
      String tempName = conf != null ? baseClass.getName() + "=" + conf : baseClass.getName();
      if (!registry)
      {
         Object instance = classInstanceMap.get(tempName);
         if (instance != null)
         {
            if (instance instanceof ClassImpl)
            {
               ClassImpl ci = (ClassImpl) instance;
               // ������ڵ�����ͬ�����¼��أ���ʹ���˲�ͬ��ClassLoaderʱ�����ڵ���ͻ᲻ͬ��
               if (ci.baseClass == baseClass)
               {
                  // ���baseObj��ͬ���˷����Ὣ������б���
                  ci.addBaseObj(baseObj);
                  return ci;
               }
            }
            else
            {
               return (FactoryManager.Instance) instance;
            }
         }
      }
      Instance instance = null;
      if (instanceClass != null)
      {
         if (Instance.class.isAssignableFrom(instanceClass))
         {
            try
            {
               ObjectRef ref = new ObjectRef();
               Constructor constructor = findConstructor(instanceClass, ref,
                     baseClass, baseObj, initConfig, parentConfig);
               if (constructor != null)
               {
                  constructor.setAccessible(true);
                  Object[] params = (Object[]) ref.getObject();
                  instance = (Instance) constructor.newInstance(params);
               }
            }
            catch (Throwable ex)
            {
               String msg = "Error in createClassFactoryManager, when create special instance class:"
                     + instanceClass + ".";
               log.error(msg, ex);
               throw new RuntimeException(msg);
            }
         }
         else
         {
            String msg = "Error in createClassFactoryManager, unexpected instance class type:"
                  + instanceClass + ".";
            throw new RuntimeException(msg);
         }
      }
      if (instance == null)
      {
         if (EternaInitialize.class.isAssignableFrom(baseClass))
         {
            try
            {
               Method method = baseClass.getDeclaredMethod( "autoReloadTime", new Class[0]);
               method.setAccessible(true);
               Long autoReloadTime = (Long) method.invoke(baseObj, new Object[0]);
               instance = new AutoReloadImpl(baseClass, baseObj, initConfig, parentConfig,
                     autoReloadTime.longValue());
            }
            catch (Throwable ex)
            {
               log.info("At createClassFactoryManager, when invoke autoReloadTime:" + baseClass + ".");
            }
         }
         if (instance == null)
         {
            instance = new ClassImpl(baseClass, baseObj, initConfig, parentConfig);
         }
      }
      current = instance;
      instance.reInit(null);
      current = globeInstance;
      Instance old = (Instance) classInstanceMap.put(tempName, instance);
      if (old != null)
      {
         old.destroy();
      }
      return instance;
   }

   /**
    * �ӹ�����������ʵ������Ѱ��һ�����ʵĹ��캯��.
    *
    * @param params           ����, ������ʱʹ�õĲ���
    * @param baseClass        ��ʼ���Ļ�����
    * @param baseObj          �������һ��ʵ��
    * @param initConfig       ��ʼ��������
    * @param parentConfig     ��ʼ���ĸ�����
    * @param instanceClass    ������������ʵ����
    */
   private static Constructor findConstructor(Class instanceClass, ObjectRef params, Class baseClass,
         Object baseObj, String initConfig, String[] parentConfig)
   {
      Constructor[] constructors = instanceClass.getDeclaredConstructors();
      Constructor constructor = null;
      Class[] paramTypes = new Class[0];
      CONSTRUCTOR_LOOP:
      for (int i = 0; i < constructors.length; i++)
      {
         Constructor tmpC = constructors[i];
         Class[] types = tmpC.getParameterTypes();
         if (types.length >= paramTypes.length && types.length <= 4)
         {
            Object[] tmpParams = new Object[types.length];
            for (int j = 0; j < types.length; j++)
            {
               if (Object.class == types[j])
               {
                  tmpParams[j] = baseObj;
               }
               else if (Class.class == types[j])
               {
                  tmpParams[j] = baseClass;
               }
               else if (String.class == types[j])
               {
                  tmpParams[j] = initConfig;
               }
               else if (String[].class == types[j])
               {
                  tmpParams[j] = parentConfig;
               }
               else
               {
                  continue CONSTRUCTOR_LOOP;
               }
            }
            paramTypes = types;
            constructor = tmpC;
            params.setObject(tmpParams);
         }
      }
      if (constructor == null)
      {
         log.error("In instance class type:" + instanceClass + ", can't find proper constructor.");
      }
      return constructor;
   }

   /**
    * (����)��ʼ�����еĹ�����������ʵ��.
    */
   public static void reInitEterna()
   {
      reInitEterna(null);
   }

   /**
    * (����)��ʼ�����еĹ�����������ʵ��.
    *
    * @param msg        ����, ��ʼ�������з��ص���Ϣ
    */
   public static synchronized void reInitEterna(StringRef msg)
   {
      current = globeInstance;
      globeInstance.reInit(msg);
      Iterator itr = classInstanceMap.values().iterator();
      while (itr.hasNext())
      {
         FactoryManager.Instance instance = (FactoryManager.Instance) itr.next();
         current = instance;
         instance.reInit(msg);
      }
      current = globeInstance;
   }

   /**
    * �ӵ�ǰ������������ʵ���л�ȡһ������ʵ��.
    *
    * @param name          ����������
    * @param className     ������ʵ��������
    */
   public static synchronized Factory getFactory(String name, String className)
         throws ConfigurationException
   {
      return current.getFactory(name, className);
   }

   /**
    * ��һ������ʵ�����õ���ǰ������������ʵ����.
    *
    * @param name          ����������
    * @param factory       ����ʵ��
    */
   static synchronized void addFactory(String name, Factory factory)
         throws ConfigurationException
   {
      current.addFactory(name, factory);
   }

   /**
    * ��ȫ�ֹ���������ʵ���л�ȡһ��EternaFactoryʵ��.
    */
   public static EternaFactory getEternaFactory()
         throws ConfigurationException
   {
      return globeInstance.getEternaFactory();
   }

   /**
    * ��ȡ��ʼ���Ļ���.
    */
   public static Map getInitCache()
   {
      Map cache = (Map) ThreadCache.getInstance().getProperty(ETERNA_INIT_CACHE);
      return cache;
   }

   /**
    * ���ó�ʼ���Ļ���.
    */
   public static void setInitCache(Map cache)
   {
      ThreadCache.getInstance().setProperty(ETERNA_INIT_CACHE, cache);
   }

   /**
    * ���������е�������Ϣ.
    */
   private static String resolveLocate(String locate)
   {
      return Utility.resolveDynamicPropnames(locate);
   }

   /**
    * ���ݵ�ַ���������ȡ���õ�������.
    *
    * @param locate       ���õĵ�ַ
    * @param baseClass    ��ʼ���Ļ�����
    */
   private static InputStream getConfigStream(String locate, Class baseClass)
         throws IOException
   {
      if (locate.startsWith("cp:"))
      {
         URL url;
         if (baseClass == null)
         {
            url = Utility.getContextClassLoader().getResource(locate.substring(3));
         }
         else
         {
            url = baseClass.getClassLoader().getResource(locate.substring(3));
         }
         if (url != null)
         {
            return url.openStream();
         }
         return null;
      }
      else if (locate.startsWith("http:"))
      {
         URL url = new URL(locate);
         return url.openStream();
      }
      else if (locate.startsWith("note:"))
      {
         return null;
      }
      else
      {
         File file = new File(locate);
         return file.isFile() ? new FileInputStream(file) : null;
      }
   }

   /**
    * ��ȡxml�Ľ�����.
    */
   private static Digester createDigester()
   {
      Digester digester = new Digester();

      // Register our local copy of the DTDs that we can find
      URL url = FactoryManager.class.getClassLoader().getResource(
            "self/micromagic/eterna/digester/eterna_1_5.dtd");
      digester.register("eterna", url.toString());

      digester.addRuleSet(new ShareSet());

      // Configure the processing rules
      digester.addRuleSet(new SQLRuleSet());
      digester.addRuleSet(new SearchRuleSet());
      digester.addRuleSet(new ModelRuleSet());
      digester.addRuleSet(new ViewRuleSet());

      return digester;
   }

   /**
    * FactoryManagerʵ���������Ķ��������.
    */
   public static class ContainObject
   {
      public final Instance shareInstance;
      public final Object baseObj;

      public ContainObject(Instance shareInstance, Object baseObj)
      {
         this.shareInstance = shareInstance;
         this.baseObj = baseObj;
      }

   }

   /**
    * FactoryManager��ʵ���ӿ�.
    */
   public interface Instance
   {
      /**
       * ��ñ�����������ʵ����id.
       */
      String getId();

      /**
       * ��ñ������������ĳ�ʼ������.
       */
      String getInitConfig();

      /**
       * (����)��ʼ������
       * @param msg  ��ų�ʼ���ķ�����Ϣ
       */
      void reInit(StringRef msg);

      /**
       * ���һ������ʵ��.
       *
       * @param name       ����������
       * @param className  ����ʵ������
       * @return   ����ʵ��
       */
      Factory getFactory(String name, String className)
            throws ConfigurationException;

      /**
       * ���һ������ʵ��.
       *
       * @param name        ����������
       * @param factory     ����ʵ��
       */
      void addFactory(String name, Factory factory)
            throws ConfigurationException;

      /**
       * ��÷�����Ϊ"eterna"�Ĺ���ʵ��.
       */
      EternaFactory getEternaFactory()
            throws ConfigurationException;

      /**
       * ���˹���ʵ�����������ڽ���ʱ, ����ô˷���.
       */
      void destroy();

   }

   /**
    * FactoryManager��ʵ���ӿڵĳ���ʵ����, ʵ����һЩ���õķ���.
    */
   public static abstract class AbstractInstance
         implements Instance
   {
      protected Map instanceMaps = new HashMap();
      protected boolean initialized = false;
      protected Throwable initException = null;
      protected boolean initFactorys = false;
      protected Factory defaultFactory = null;
      protected Instance shareInstance = null;

      /**
       * �������ʵ�������ʵ��.
       */
      public void setShareInstance(FactoryManager.Instance shareInstance)
      {
         if (shareInstance == null)
         {
            this.shareInstance = globeInstance;
         }
         else
         {
            this.shareInstance = shareInstance;
         }
      }

      /**
       * ������Ϻõ������ַ���.
       */
      protected String getConfigString(String initConfig, String[] parentConfig)
      {
         return getConfig(initConfig, parentConfig);
      }

      /**
       * ���ó�ʼ���ĵȼ�.
       */
      protected void setSuperInitLevel(int level)
      {
         FactoryManager.superInitLevel = level;
      }

      /**
       * ������õ�������.
       */
      protected InputStream getConfigStream(String locate, Class baseClass)
            throws IOException, ConfigurationException
      {
         return FactoryManager.getConfigStream(locate, baseClass);
      }

      /**
       * (����)��ʼ������������
       * @param msg  ��ų�ʼ���ķ�����Ϣ
       */
      public void reInit(StringRef msg)
      {
         synchronized (FactoryManager.class)
         {
            SameCheckRule.initDealedObjMap();
            Instance oldInstance = FactoryManager.current;
            Factory oldCF = FactoryManager.currentFactory;
            this.destroy();
            FactoryManager.currentFactory = null;
            FactoryManager.current = this;
            this.initialized = false;
            this.initException = null;
            this.instanceMaps.clear();
            this.defaultFactory = null;

            try
            {
               ThreadCache.getInstance().setProperty(ConfigurationException.IN_INITIALIZE, "1");
               this.initializeXML(msg);
               ConfigurationException.config = null;
               ConfigurationException.objName = null;
               this.initializeFactorys();
               ConfigurationException.objName = null;
               this.initializeElse();
               this.initialized = true;
            }
            catch (Throwable ex)
            {
               this.initException = ex;
               StringBuffer temp = new StringBuffer();

               if (ConfigurationException.config != null)
               {
                  temp.append("Config:").append(ConfigurationException.config).append("; ");
               }
               else
               {
                  temp.append("InitConfig:{").append(this.getInitConfig()).append("}; ");
               }
               if (ConfigurationException.objName != null)
               {
                  temp.append("Object:").append(ConfigurationException.objName).append("; ");
               }
               temp.append("Message:").append("When " + this.getClass().getName() + " initialize.");
               log.error(temp.toString(), ex);
               if (msg != null)
               {
                  if (msg.getString() != null)
                  {
                     StringBuffer tmpBuf = new StringBuffer();
                     tmpBuf.append(msg.getString());
                     tmpBuf.append(Utility.LINE_SEPARATOR);
                     tmpBuf.append(temp.toString());
                     temp = tmpBuf;
                  }
                  msg.setString(temp.append("[").append(ex.getMessage()).append("]").toString());
               }
               ConfigurationException.config = null;
               ConfigurationException.objName = null;
            }
            finally
            {
               ThreadCache.getInstance().removeProperty(ConfigurationException.IN_INITIALIZE);
               FactoryManager.currentFactory = oldCF;
               FactoryManager.current = oldInstance;
               SameCheckRule.clearDealedObjMap();
            }
         }
      }

      /**
       * ����xml�������г�ʼ��. <p>
       * ��xml���������������г�ʼ��, ������ͨ��
       * <code>createDigester()</code>�������.
       *
       * @param msg  ��ų�ʼ���ķ�����Ϣ
       *
       * @see #createDigester()
       */
      protected abstract void initializeXML(StringRef msg) throws Throwable;

      /**
       * ��ʼ����ɺ�, ����ʣ������.
       */
      protected void initializeElse()
            throws ConfigurationException
      {
      }

      /**
       * ��ʼ����ɺ�, ����ʣ������.
       */
      protected void callAfterEternaInitialize(Object obj)
            throws ConfigurationException
      {
         if (obj == null)
         {
            return;
         }
         Class theClass;
         Object[] objs;
         if (obj instanceof Class)
         {
            theClass = (Class) obj;
            objs = null;
         }
         else
         {
            theClass = obj.getClass();
            if (theClass.isArray())
            {
               objs = (Object[]) obj;
               theClass = theClass.getComponentType();
            }
            else
            {
               objs = new Object[]{obj};
            }
         }
         if (!EternaInitialize.class.isAssignableFrom(theClass))
         {
            return;
         }
         try
         {
            Method method = theClass.getDeclaredMethod(
                  "afterEternaInitialize", new Class[]{FactoryManager.Instance.class});
            method.setAccessible(true);
            Object[] params = new Object[]{this};
            if (Modifier.isStatic(method.getModifiers()))
            {
               method.invoke(null, params);
            }
            else if (objs != null)
            {
               for (int i = 0; i < objs.length; i++)
               {
                  Object baseObj = objs[i];
                  if (baseObj != null)
                  {
                     method.invoke(baseObj, params);
                  }
               }
            }
         }
         catch (NoSuchMethodException ex)
         {
            log.warn("Not found method initializeElse, when invoke init:" + theClass + ".");
         }
         catch (Exception ex)
         {
            if (ex instanceof ConfigurationException)
            {
               throw (ConfigurationException) ex;
            }
            log.info("At initializeElse, when invoke init:" + theClass + ".", ex);
         }
      }

      /**
       * ����һ����ʼ���õ�xml��������.
       */
      protected Digester createDigester()
      {
         return FactoryManager.createDigester();
      }

      /**
       * ��ʼ��ָ���Ĺ���.
       *
       * @param factory   ���ʼ���Ĺ���
       */
      protected void initFactory(Factory factory)
            throws ConfigurationException
      {
         Factory shareFactory = null;
         if (this.shareInstance != null)
         {
            try
            {
               String fName = factory.getName();
               String cName = factory.getClass().getName();
               shareFactory = this.shareInstance.getFactory(fName, cName);
            }
            catch (Exception ex) {}
         }
         factory.initialize(this, shareFactory);
      }

      /**
       * ���һ������map.
       *
       * @param name  ����������
       * @return  ����map
       */
      protected Map getFactoryMap(String name, boolean mustExists)
            throws ConfigurationException
      {
         Map map = (Map) this.instanceMaps.get(name);
         if (map == null && mustExists)
         {
            throw new ConfigurationException("Not found the factory name:" + name + ".");
         }
         return map;
      }

      /**
       * ��ʼ�����еĹ���.
       */
      protected void initializeFactorys()
            throws ConfigurationException
      {
         this.initFactorys = true;
         try
         {
            Iterator itr1 = this.instanceMaps.values().iterator();
            while (itr1.hasNext())
            {
               Map temp = (Map) itr1.next();
               Iterator itr2 = temp.values().iterator();
               while (itr2.hasNext())
               {
                  this.initFactory((Factory) itr2.next());
               }
            }
         }
         finally
         {
            this.initFactorys = false;
         }
      }

      /**
       * ���һ������ʵ��.
       *
       * @param name       ����������
       * @param className  ����ʵ������
       * @return   ����ʵ��
       */
      public Factory getFactory(String name, String className)
            throws ConfigurationException
      {
         Map map = this.getFactoryMap(name, !this.initialized);
         if (map == null && this.shareInstance != null)
         {
            return this.shareInstance.getFactory(name, className);
         }
         Factory factory = (Factory) map.get(className);
         if (this.initFactorys)
         {
            this.initFactory(factory);
         }
         if (!this.initialized)
         {
            FactoryManager.currentFactory = factory;
         }
         return factory;
      }

      /**
       * ���һ������ʵ��.
       *
       * @param name        ����������
       * @param factory     ����ʵ��
       */
      public void addFactory(String name, Factory factory)
            throws ConfigurationException
      {
         factory.setName(name);
         if (this.initialized)
         {
            this.initFactory(factory);
         }
         else
         {
            FactoryManager.currentFactory = factory;
         }
         Map map = (Map) this.instanceMaps.get(name);
         if (map == null)
         {
            map = new HashMap();
            this.instanceMaps.put(name, map);
         }
         map.put(factory.getClass().getName(), factory);
      }

      /**
       * ��÷�����Ϊ"eterna"�Ĺ���ʵ��.
       */
      public EternaFactory getEternaFactory()
            throws ConfigurationException
      {
         if (this.defaultFactory == null)
         {
            this.defaultFactory = this.getFactory(ETERNA_FACTORY, EternaFactoryImpl.class.getName());
         }
         return (EternaFactory) this.defaultFactory;
      }

      /**
       * ���˹���ʵ�����������ڽ���ʱ, ����ô˷���.
       */
      public void destroy()
      {
         Iterator itr1 = this.instanceMaps.values().iterator();
         while (itr1.hasNext())
         {
            Map temp = (Map) itr1.next();
            Iterator itr2 = temp.values().iterator();
            while (itr2.hasNext())
            {
               ((Factory) itr2.next()).destroy();
            }
         }
      }

   }

   /**
    * ȫ��FactoryManager��ʵ����ʵ����.
    */
   private static class GlobeImpl extends AbstractInstance
         implements Instance
   {
      public String getId()
      {
         return "eterna.FactoryManager.Instance.globe";
      }

      public String getInitConfig()
      {
         String initFiles = Utility.getProperty(INIT_FILES_PROPERTY);
         String subFiles = Utility.getProperty(INIT_SUBFILES_PROPERTY);
         String[] parentConfig = null;
         if (subFiles != null)
         {
            if (initFiles != null)
            {
               parentConfig = new String[]{initFiles};
            }
            initFiles = subFiles;
         }
         return getConfig(initFiles, parentConfig);
      }

      public void setShareInstance(Instance shareInstance)
      {
      }

      protected void initializeXML(StringRef msg)
            throws Throwable
      {
         Digester digester = this.createDigester();
         try
         {
            String temp = Utility.getProperty(INIT_SUBFILES_PROPERTY);
            if (temp != null)
            {
               this.dealXML(temp, digester);
               FactoryManager.superInitLevel = 1;
            }

            String filenames = Utility.getProperty(INIT_FILES_PROPERTY);
            if (filenames == null)
            {
               log.warn("The property " + INIT_FILES_PROPERTY + " not found.");
            }
            else
            {
               this.dealXML(filenames, digester);
               FactoryManager.superInitLevel += 1;
            }

            temp = Utility.getProperty(LOAD_DEFAULT_CONFIG);
            if (temp == null || "true".equalsIgnoreCase(temp))
            {
               temp = DEFAULT_CONFIG_FILE;
               this.dealXML(temp, digester);
            }
         }
         finally
         {
            FactoryManager.superInitLevel = 0;
         }
      }

      protected void dealXML(String config, Digester digester)
            throws Exception
      {
         StringTokenizer token = new StringTokenizer(FactoryManager.resolveLocate(config), ";");
         while (token.hasMoreTokens())
         {
            String temp = token.nextToken().trim();
            if (temp.length() == 0)
            {
               continue;
            }
            ConfigurationException.config = temp;
            ConfigurationException.objName = null;
            InputStream is = FactoryManager.getConfigStream(temp, null);
            if (is != null)
            {
               log.debug("The XML locate is \"" + temp + "\".");
               digester.parse(is);
               is.close();
            }
            else
            {
               log.info("The XML locate \"" + temp + "\" not avilable.");
            }
         }
      }

      protected void initializeElse()
            throws ConfigurationException
      {
         Class[] initClasses;
         String classNames = Utility.getProperty(INIT_CLASSES_PROPERTY);
         if (classNames == null)
         {
            initClasses = new Class[0];
         }
         else
         {
            StringTokenizer token = new StringTokenizer(classNames, ";");
            initClasses = new Class[token.countTokens()];
            String temp;
            int index = 0;
            while (token.hasMoreTokens())
            {
               temp = token.nextToken().trim();
               if (temp.length() == 0)
               {
                  continue;
               }
               try
               {
                  initClasses[index] = Class.forName(temp);
               }
               catch (Exception ex)
               {
                  log.warn("At initializeElse, when loadClass:" + temp + ".", ex);
                  initClasses[index] = null;
               }
               index++;
            }
         }
         for (int i = 0; i < initClasses.length; i++)
         {
            if (initClasses[i] == null)
            {
               continue;
            }
            this.callAfterEternaInitialize(initClasses[i]);
         }
      }

   }

   /**
    * �������FactoryManager��ʵ����ʵ����.
    */
   private static class ClassImpl extends AbstractInstance
         implements Instance
   {
      protected String instanceId = null;
      protected String initConfig;
      protected String[] parentConfig;

      protected Class baseClass;
      protected Object[] baseObjs;

      public ClassImpl(Class baseClass, Object baseObj, String initConfig, String[] parentConfig)
      {
         this.baseClass = baseClass;
         this.baseObjs = (Object[]) Array.newInstance(baseClass, 1);
         this.initConfig = initConfig;
         this.parentConfig = parentConfig;
         if (baseObj instanceof ContainObject)
         {
            ContainObject co = (ContainObject) baseObj;
            this.setShareInstance(co.shareInstance);
            this.baseObjs[0] = co.baseObj;
         }
         else
         {
            this.baseObjs[0] = baseObj;
            this.shareInstance = FactoryManager.globeInstance;
         }
      }

      public String getId()
      {
         if (this.instanceId == null)
         {
            try
            {
               String tmp = getConfig(this.initConfig, this.parentConfig);
               if (tmp == null)
               {
                  tmp = this.baseClass.getName();
               }
               else
               {
                  tmp = this.baseClass.getName() + "=" + tmp;
               }
               ByteArrayOutputStream byteOut = new ByteArrayOutputStream(128);
               DeflaterOutputStream out = new DeflaterOutputStream(byteOut);
               byte[] buf = tmp.getBytes("UTF-8");
               out.write(buf);
               out.close();
               byte[] result = byteOut.toByteArray();
               this.instanceId = new Base64().byteArrayToBase64(result);
            }
            catch (IOException ex)
            {
               // ���ﲻ�����IO�쳣��Ϊȫ���ڴ����
            }
         }
         return this.instanceId;
      }

      public String getInitConfig()
      {
         String tmp = getConfig(this.initConfig, this.parentConfig);
         if (tmp == null)
         {
            tmp = "cp:" + this.baseClass.getName().replace('.', '/') + ".xml";
         }
         return tmp;
      }

      public void addBaseObj(Object obj)
      {
         if (obj == null)
         {
            return;
         }
         int nullIndex = -1;
         for (int i = 0; i < this.baseObjs.length; i++)
         {
            if (obj == this.baseObjs[i])
            {
               // ��obj�����б��У����Բ���Ҫ�����
               return;
            }
            if (this.baseObjs[i] == null)
            {
               nullIndex = i;
            }
         }
         if (nullIndex != -1)
         {
            this.baseObjs[nullIndex] = obj;
         }
         else
         {
            Object[] temp = this.baseObjs;
            this.baseObjs = (Object[]) Array.newInstance(this.baseClass, temp.length + 1);
            this.baseObjs[temp.length] = obj;
            System.arraycopy(temp, 0, this.baseObjs, 0, temp.length);
         }
      }

      protected void initializeXML(StringRef msg)
            throws Throwable
      {
         ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
         Thread.currentThread().setContextClassLoader(this.baseClass.getClassLoader());
         try
         {
            Digester digester = this.createDigester();
            String filenames = this.initConfig == null ?
                  "cp:" + this.baseClass.getName().replace('.', '/') + ".xml" : this.initConfig;
            this.dealXML(filenames, digester);

            if (this.parentConfig != null)
            {
               // @old ȥ���˶Ը����������Ե��ж�, ��Ϊû��̫������
               for (int i = 0; i < this.parentConfig.length; i++)
               {
                  if (this.parentConfig[i] != null)
                  {
                     // �򿪸��������ڳ�ʼ���ı�־, �������ó�ʼ������
                     FactoryManager.superInitLevel = i + 1;
                     try
                     {
                        this.dealXML(this.parentConfig[i], digester);
                     }
                     finally
                     {
                        FactoryManager.superInitLevel = 0;
                     }
                  }
               }
            }
         }
         finally
         {
            Thread.currentThread().setContextClassLoader(oldCL);
         }
      }

      protected void dealXML(String config, Digester digester)
            throws Exception
      {
         StringTokenizer token = new StringTokenizer(FactoryManager.resolveLocate(config), ";");
         while (token.hasMoreTokens())
         {
            String temp = token.nextToken().trim();
            if (temp.length() == 0)
            {
               continue;
            }
            ConfigurationException.config = temp;
            ConfigurationException.objName = null;
            InputStream is = FactoryManager.getConfigStream(temp, this.baseClass);
            if (is != null)
            {
               log.debug("The XML locate is \"" + temp + "\".");
               digester.parse(is);
               is.close();
            }
            else
            {
               log.info("The XML locate \"" + temp + "\" not avilable.");
            }
         }
      }

      protected void initializeElse()
            throws ConfigurationException
      {
         this.callAfterEternaInitialize(this.baseObjs);
      }

   }

   /**
    * �������FactoryManager��ʵ����ʵ����, ͬʱ���������Ƿ��и���,
    * ������¹����Զ����³�ʼ��.
    */
   private static class AutoReloadImpl extends ClassImpl
         implements Instance
   {
      private long preInitTime;
      private long preCheckTime;
      private long autoReloadTime;
      private ConfigMonitor[] monitors = null;
      private boolean atInitialize = false;

      private Map initCache = null;

      public AutoReloadImpl(Class baseClass, Object baseObj, String initConfig, String[] parentConfig,
            long autoReloadTime)
      {
         super(baseClass, baseObj, initConfig, parentConfig);
         List tempList = new LinkedList(this.getFiles(initConfig));
         if (parentConfig != null)
         {
            for (int i = 0; i < parentConfig.length; i++)
            {
               if (parentConfig[i] != null)
               {
                  tempList.addAll(this.getFiles(parentConfig[i]));
               }
            }
         }
         if (tempList.size() > 0)
         {
            this.monitors = new ConfigMonitor[tempList.size()];
            tempList.toArray(this.monitors);
         }
         this.autoReloadTime = autoReloadTime < 200 ? 200 : autoReloadTime;
      }

      private ConfigMonitor parseFileName(String fileName, URL url)
      {
         File file = new File(fileName);
         if (file.isFile())
         {
            return new ConfigMonitor(file);
         }
         if (url != null)
         {
            return new ConfigMonitor(url);
         }
         return null;
      }

      private List getFiles(String config)
      {
         ConfigMonitor temp;
         List result = new ArrayList();
         if (config == null)
         {
            URL url = this.baseClass.getClassLoader().getResource(
                  this.baseClass.getName().replace('.', '/') + ".xml");
            if (url != null && "file".equals(url.getProtocol()))
            {
               temp = this.parseFileName(url.getFile(), url);
               if (temp != null)
               {
                  result.add(temp);
               }
            }
         }
         else
         {
            StringTokenizer token = new StringTokenizer(FactoryManager.resolveLocate(config), ";");
            while (token.hasMoreTokens())
            {
               String tStr = token.nextToken().trim();
               if (tStr.length() == 0)
               {
                  continue;
               }
               if (tStr.startsWith("cp:"))
               {
                  URL url = this.baseClass.getClassLoader().getResource(tStr.substring(3));
                  if (url != null && "file".equals(url.getProtocol()))
                  {
                     temp = this.parseFileName(url.getFile(), url);
                     if (temp != null)
                     {
                        result.add(temp);
                     }
                  }
               }
               else if (tStr.startsWith("http:"))
               {
                  try
                  {
                     result.add(new URL(tStr));
                  }
                  catch (IOException ex) {}
               }
               else
               {
                  temp = this.parseFileName(tStr, null);
                  if (temp != null)
                  {
                     result.add(temp);
                  }
               }
            }
         }
         return result;
      }

      public void reInit(StringRef msg)
      {
         Map oldCache = getInitCache();
         if (this.initCache == null)
         {
            this.initCache = oldCache;
         }
         else
         {
            setInitCache(this.initCache);
         }
         this.atInitialize = true;
         super.reInit(msg);
         this.atInitialize = false;
         if (oldCache != null)
         {
            setInitCache(oldCache);
         }
      }

      protected void initializeElse()
            throws ConfigurationException
      {
         super.initializeElse();
         long time = System.currentTimeMillis();
         if (this.preInitTime < time)
         {
            this.preInitTime = time;
            this.preCheckTime = this.preInitTime;
         }
      }

      private void checkReload()
      {
         // �ж��Ƿ��ڳ�ʼ��״̬
         if (this.atInitialize)
         {
            // ��ֹ��ʼ��ʱ�����߳�Ҳ����, �������̵߳ȴ���ʼ��,
            synchronized (this)
            {
               if (this.atInitialize)
               {
                  return;
               }
            }
         }
         if (System.currentTimeMillis() - this.autoReloadTime > this.preCheckTime
               && this.monitors != null)
         {
            boolean needReload = false;
            for (int i = 0; i < this.monitors.length; i++)
            {
               long lm = this.monitors[i].getLastModified();
               if (lm > this.preInitTime)
               {
                  needReload = true;
                  this.preInitTime = lm;
                  break;
               }
            }
            if (needReload)
            {
               synchronized (this)
               {
                  // �ٴμ��ǰһ�μ��ʱ��, ���δ��˵�����������߳���������
                  if (System.currentTimeMillis() - this.autoReloadTime > this.preCheckTime)
                  {
                     StringRef sr = new StringRef();
                     this.reInit(sr);
                     if (log.isInfoEnabled())
                     {
                        log.info("Auto reload at time:" + FormatTool.getCurrentDatetimeString()
                              + ". with message:");
                        log.info(sr.toString());
                     }
                     // ��initializeElse����������ʱ��, ����Ͳ���������
                  }
               }
            }
            this.preCheckTime = System.currentTimeMillis();
         }
      }

      public Factory getFactory(String name, String className)
            throws ConfigurationException
      {
         this.checkReload();
         return super.getFactory(name, className);
      }

      public EternaFactory getEternaFactory()
            throws ConfigurationException
      {
         this.checkReload();
         return super.getEternaFactory();
      }

   }

   /**
    * ���ø��µļ����.
    */
   private static class ConfigMonitor
   {
      private File configFile = null;
      private URL configURL = null;
      private boolean valid = true;

      public ConfigMonitor(File configFile)
      {
         this.configFile = configFile;
      }

      public ConfigMonitor(URL configURL)
      {
         this.configURL = configURL;
      }

      public boolean isValid()
      {
         return this.valid;
      }

      public long getLastModified()
      {
         if (this.valid)
         {
            try
            {
               if (this.configFile == null)
               {
                  return this.configURL.openConnection().getLastModified();
               }
               else
               {
                  return this.configFile.lastModified();
               }
            }
            catch (Throwable ex)
            {
               StringBuffer buf = new StringBuffer(128);
               buf.append("Error in check configFile:[").append(this.configFile)
                     .append("], configURL:[").append(this.configURL).append("].");
               log.error(buf, ex);
               this.configFile = null;
               this.configURL = null;
               this.valid = false;
            }
         }
         return 0L;
      }

   }

}
