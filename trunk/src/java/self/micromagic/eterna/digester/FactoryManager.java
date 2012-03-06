
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
 * 配置说明:
 *
 * self.micromagic.eterna.digester.initfiles
 * 要进行全局初始化的文件列表
 *
 * self.micromagic.eterna.digester.subinitfiles
 * 要进行全局初始化的子文件列表,
 * 子文件列表中的对象会覆盖掉全局初始化的文件列表中的同名对象
 *
 * self.micromagic.eterna.digester.initClasses
 * 要进行全局初始化的类名列表
 *
 * self.micromagic.eterna.digester.loadDefaultConfig
 * 全局初始化时是否要载入默认的配置
 * cp:self/micromagic/eterna/share/eterna_share.xml;cp:eterna_global.xml;
 *
 */
public class FactoryManager
{
   public static final Log log = Tool.log;

   /**
    * 要进行全局初始化的文件列表.
    */
   public static final String INIT_FILES_PROPERTY
         = "self.micromagic.eterna.digester.initfiles";

   /**
    * 要进行全局初始化的子文件列表, 子文件列表中的对象会覆盖掉全局初始化
    * 的文件列表中的同名对象.
    */
   public static final String INIT_SUBFILES_PROPERTY
         = "self.micromagic.eterna.digester.subinitfiles";

   /**
    * 要进行全局初始化的类名列表.
    */
   public static final String INIT_CLASSES_PROPERTY
         = "self.micromagic.eterna.digester.initClasses";

   /**
    * 全局初始化时是否要载入默认的配置.
    */
   public static final String LOAD_DEFAULT_CONFIG
         = "self.micromagic.eterna.digester.loadDefaultConfig";

   /**
    * 全局初始化时要载入的默认配置.
    */
   public static final String DEFAULT_CONFIG_FILE
         = "cp:self/micromagic/eterna/share/eterna_share.xml;cp:eterna_global.xml;";

   /**
    * 实例初始化的文件列表.
    */
   public static final String CONFIG_INIT_FILES = "initFiles";

   /**
    * 实例初始化的父文件列表.
    */
   public static final String CONFIG_INIT_PARENTFILES = "parentFiles";

   /**
    * 实例初始化的配置列表.
    */
   public static final String CONFIG_INIT_NAME = "initConfig";

   /**
    * 实例初始化的父配置列表.
    */
   public static final String CONFIG_INIT_PARENTNAME = "parentConfig";

   /**
    * 初始化时使用的线程缓存.
    */
   public static final String ETERNA_INIT_CACHE = "eterna.init.cache";

   /**
    * 默认需要加载的工厂EternaFactory.
    */
   public static final String ETERNA_FACTORY
         = "self.micromagic.eterna.EternaFactory";

   /**
    * 初始化时是否需要对脚本语言进行语法检查.
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
    * 标识当前是否在初始化父配置
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
    * 是否在初始化父配置
    */
   public static boolean isSuperInit()
   {
      return superInitLevel > 0;
   }

   /**
    * 初始化父配置的level等级, 0为基本配置 1为第一级 2为第二级 ...
    */
   public static int getSuperInitLevel()
   {
      return superInitLevel;
   }

   /**
    * 初始化时是否需要对脚本语言进行语法检查.
    */
   public static boolean isCheckGrammer()
   {
      return checkGrammer;
   }

   /**
    * 设置初始化时是否需要对脚本语言进行语法检查.
    *
    * @param check   设成true为需要
    */
   public static void setCheckGrammer(String check)
   {
      checkGrammer = "true".equalsIgnoreCase(check);
   }

   /**
    * 生成一个记录SQL日志的节点.
    *
    * @param name   SQL的类型名称
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
         // 当节点过多时, 清除最先添加的几个节点
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
            // 当去除节点出错时, 则清空日志
            log.warn("Remove sql log error.", ex);
            logDocument = null;
            return createLogNode(name);
         }
      }
      return logs.addElement(name);
   }

   /**
    * 将记录的日志输出.
    *
    * @param out     日志的输出流
    * @param clear   是否要在输出完后清空日志
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
    * 获得当前正在初始化的Factory.
    * 只有在初始化时才会返回值, 否则返回null.
    */
   public static Factory getCurrentFactory()
   {
      return currentFactory;
   }

   /**
    * 获得全局的工厂管理器的实例.
    */
   public static FactoryManager.Instance getGlobeFactoryManager()
   {
      return globeInstance;
   }

   /**
    * 根据 基础配置 各级父配置 生成配置字符串.
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
         // 这里添加一个空串用于占位
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
    * 解析配置.
    *
    * @param config    要解析的配置
    * @param result    解析完的结果列表, 本次解析的结果也要放进去
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
    * 根据一个类获取工厂管理器的实例.
    * 会将[类名.xml]作为配置来读取.
    *
    * @param baseClass    初始化的基础类
    */
   public static FactoryManager.Instance createClassFactoryManager(Class baseClass)
   {
      return createClassFactoryManager(baseClass, null);
   }

   /**
    * 根据一个类及配置获取工厂管理器的实例.
    *
    * @param baseClass    初始化的基础类
    * @param initConfig   初始化的配置
    */
   public static FactoryManager.Instance createClassFactoryManager(Class baseClass, String initConfig)
   {
      String conf = getConfig(initConfig, null);
      Object instance = conf != null ? classInstanceMap.get(baseClass.getName() + "=" + conf)
            : classInstanceMap.get(baseClass.getName());
      if (instance != null && instance instanceof ClassImpl)
      {
         ClassImpl ci = (ClassImpl) instance;
         // 如果基于的类相同则不重新加载（当使用了不同的ClassLoader时，基于的类就会不同）
         if (ci.baseClass == baseClass)
         {
            return ci;
         }
      }
      return createClassFactoryManager(baseClass, null, initConfig, null, false);
   }

   /**
    * 根据一个类及配置获取工厂管理器的实例.
    *
    * @param baseClass    初始化的基础类
    * @param initConfig   初始化的配置
    * @param registry     是否需要重新注册此实例, 设为true则会将原来已存在的实例删除
    */
   public static FactoryManager.Instance createClassFactoryManager(Class baseClass,
         String initConfig, boolean registry)
   {
      return createClassFactoryManager(baseClass, null, initConfig, null, registry);
   }

   /**
    * 根据一个类及配置获取工厂管理器的实例.
    *
    * @param baseClass    初始化的基础类
    * @param baseObj      基础类的一个实例
    * @param initConfig   初始化的配置
    * @param registry     是否需要重新注册此实例, 设为true则会将原来已存在的实例删除
    */
   public static FactoryManager.Instance createClassFactoryManager(Class baseClass,
         Object baseObj, String initConfig, boolean registry)
   {
      return createClassFactoryManager(baseClass, baseObj, initConfig, null, registry);
   }

   /**
    * 根据一个类及配置获取工厂管理器的实例.
    *
    * @param baseClass        初始化的基础类
    * @param baseObj          基础类的一个实例
    * @param initConfig       初始化的配置
    * @param parentConfig     初始化的父配置
    * @param registry         是否需要重新注册此实例, 设为true则会将原来已存在的实例删除
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
    * 根据一个类及配置获取工厂管理器的实例.
    *
    * @param baseClass        初始化的基础类
    * @param baseObj          基础类的一个实例
    * @param initConfig       初始化的配置
    * @param parentConfig     初始化的父配置
    * @param instanceClass    工厂管理器的实现类
    * @param registry         是否需要重新注册此实例, 设为true则会将原来已存在的实例删除
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
               // 如果基于的类相同则不重新加载（当使用了不同的ClassLoader时，基于的类就会不同）
               if (ci.baseClass == baseClass)
               {
                  // 如果baseObj不同，此方法会将其加入列表中
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
    * 从工厂管理器的实现类中寻找一个合适的构造函数.
    *
    * @param params           出参, 构造类时使用的参数
    * @param baseClass        初始化的基础类
    * @param baseObj          基础类的一个实例
    * @param initConfig       初始化的配置
    * @param parentConfig     初始化的父配置
    * @param instanceClass    工厂管理器的实现类
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
    * (重新)初始化所有的工厂管理器的实例.
    */
   public static void reInitEterna()
   {
      reInitEterna(null);
   }

   /**
    * (重新)初始化所有的工厂管理器的实例.
    *
    * @param msg        出参, 初始化过程中返回的信息
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
    * 从当前工厂管理器的实例中获取一个工厂实例.
    *
    * @param name          工厂的名称
    * @param className     工厂的实现类名称
    */
   public static synchronized Factory getFactory(String name, String className)
         throws ConfigurationException
   {
      return current.getFactory(name, className);
   }

   /**
    * 将一个工厂实例设置到当前工厂管理器的实例中.
    *
    * @param name          工厂的名称
    * @param factory       工厂实例
    */
   static synchronized void addFactory(String name, Factory factory)
         throws ConfigurationException
   {
      current.addFactory(name, factory);
   }

   /**
    * 从全局工厂理器的实例中获取一个EternaFactory实例.
    */
   public static EternaFactory getEternaFactory()
         throws ConfigurationException
   {
      return globeInstance.getEternaFactory();
   }

   /**
    * 获取初始化的缓存.
    */
   public static Map getInitCache()
   {
      Map cache = (Map) ThreadCache.getInstance().getProperty(ETERNA_INIT_CACHE);
      return cache;
   }

   /**
    * 设置初始化的缓存.
    */
   public static void setInitCache(Map cache)
   {
      ThreadCache.getInstance().setProperty(ETERNA_INIT_CACHE, cache);
   }

   /**
    * 处理配置中的引用信息.
    */
   private static String resolveLocate(String locate)
   {
      return Utility.resolveDynamicPropnames(locate);
   }

   /**
    * 根据地址及基础类获取配置的数据流.
    *
    * @param locate       配置的地址
    * @param baseClass    初始化的基础类
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
    * 获取xml的解析器.
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
    * FactoryManager实例所包含的对象的容器.
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
    * FactoryManager的实例接口.
    */
   public interface Instance
   {
      /**
       * 获得本工厂管理器实例的id.
       */
      String getId();

      /**
       * 获得本工厂管理器的初始化配置.
       */
      String getInitConfig();

      /**
       * (重新)初始化工厂
       * @param msg  存放初始化的返回信息
       */
      void reInit(StringRef msg);

      /**
       * 获得一个工厂实例.
       *
       * @param name       工厂分类名
       * @param className  工厂实现类名
       * @return   工厂实例
       */
      Factory getFactory(String name, String className)
            throws ConfigurationException;

      /**
       * 添加一个工厂实例.
       *
       * @param name        工厂分类名
       * @param factory     工厂实例
       */
      void addFactory(String name, Factory factory)
            throws ConfigurationException;

      /**
       * 获得分类名为"eterna"的工厂实例.
       */
      EternaFactory getEternaFactory()
            throws ConfigurationException;

      /**
       * 当此工厂实例的生命周期结束时, 会调用此方法.
       */
      void destroy();

   }

   /**
    * FactoryManager的实例接口的抽象实现类, 实现了一些公用的方法.
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
       * 设置与此实例共享的实例.
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
       * 获得整合好的配置字符串.
       */
      protected String getConfigString(String initConfig, String[] parentConfig)
      {
         return getConfig(initConfig, parentConfig);
      }

      /**
       * 设置初始化的等级.
       */
      protected void setSuperInitLevel(int level)
      {
         FactoryManager.superInitLevel = level;
      }

      /**
       * 获得配置的输入流.
       */
      protected InputStream getConfigStream(String locate, Class baseClass)
            throws IOException, ConfigurationException
      {
         return FactoryManager.getConfigStream(locate, baseClass);
      }

      /**
       * (重新)初始化工厂管理器
       * @param msg  存放初始化的返回信息
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
       * 生成xml流并进行初始化. <p>
       * 将xml流交给解析器进行初始化, 解析器通过
       * <code>createDigester()</code>方法获得.
       *
       * @param msg  存放初始化的返回信息
       *
       * @see #createDigester()
       */
      protected abstract void initializeXML(StringRef msg) throws Throwable;

      /**
       * 初始化完成后, 处理剩余内容.
       */
      protected void initializeElse()
            throws ConfigurationException
      {
      }

      /**
       * 初始化完成后, 处理剩余内容.
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
       * 构造一个初始化用的xml流解析器.
       */
      protected Digester createDigester()
      {
         return FactoryManager.createDigester();
      }

      /**
       * 初始化指定的工厂.
       *
       * @param factory   需初始化的工厂
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
       * 获得一个工厂map.
       *
       * @param name  工厂分类名
       * @return  工厂map
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
       * 初始化所有的工厂.
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
       * 获得一个工厂实例.
       *
       * @param name       工厂分类名
       * @param className  工厂实现类名
       * @return   工厂实例
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
       * 添加一个工厂实例.
       *
       * @param name        工厂分类名
       * @param factory     工厂实例
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
       * 获得分类名为"eterna"的工厂实例.
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
       * 当此工厂实例的生命周期结束时, 会调用此方法.
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
    * 全局FactoryManager的实例的实现类.
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
    * 基于类的FactoryManager的实例的实现类.
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
               // 这里不会出现IO异常因为全是内存操作
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
               // 此obj已在列表中，所以不需要再添加
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
               // @old 去掉了对父配置完整性的判断, 因为没有太大意义
               for (int i = 0; i < this.parentConfig.length; i++)
               {
                  if (this.parentConfig[i] != null)
                  {
                     // 打开父配置正在初始化的标志, 将父配置初始化进来
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
    * 基于类的FactoryManager的实例的实现类, 同时会检测配置是否有更新,
    * 如果更新过会自动重新初始化.
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
         // 判断是否在初始化状态
         if (this.atInitialize)
         {
            // 防止初始化时其他线程也进来, 让其他线程等待初始化,
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
                  // 再次检查前一次检测时间, 如果未到说明已在其他线程中重载了
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
                     // 在initializeElse中重设过检测时间, 这里就不用再设了
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
    * 配置更新的检测器.
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
