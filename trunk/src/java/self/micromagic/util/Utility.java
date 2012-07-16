
package self.micromagic.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.rmi.server.UID;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.WeakHashMap;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import self.micromagic.util.converter.BooleanConverter;
import self.micromagic.util.converter.IntegerConverter;
import self.micromagic.util.converter.ValueConverter;
import self.micromagic.cg.ClassGenerator;

public class Utility
{
   public static final int MEMORY_CACHE_SIZE_THRESHOLD = 1024 * 1024 * 8;

   public static final String CHARSET_TAG = "self.micromagic.charset";

   /**
    * 配置文件名
    */
   public static final String PROPERTIES_NAME = "micromagic_config.properties";

   /**
    * 配置在处理文本的动态属性时, 是否要显示处理失败的信息
    */
   public static final String SHOW_RDP_FAIL_PROPERTY = "self.micromagic.show.rdp.fail";

   /**
    * 存放父配置文件名的属性
    */
   public static final String PARENT_PROPERTIES = "self.micromagic.parent.properties";

   public static final Integer INTEGER_MINUS1 = new Integer(-1);
   public static final Integer INTEGER_0 = new Integer(0);
   public static final Integer INTEGER_1 = new Integer(1);
   public static final Integer INTEGER_2 = new Integer(2);
   public static final Integer INTEGER_3 = new Integer(3);
   public static final Integer INTEGER_4 = new Integer(4);
   public static final Integer INTEGER_5 = new Integer(5);
   public static final Integer INTEGER_6 = new Integer(6);
   public static final Integer INTEGER_7 = new Integer(7);
   public static final Integer INTEGER_8 = new Integer(8);
   public static final Integer INTEGER_9 = new Integer(9);
   public static final Integer INTEGER_10 = new Integer(10);
   public static final Integer INTEGER_11 = new Integer(11);
   public static final Integer INTEGER_12 = new Integer(12);
   public static final Integer INTEGER_13 = new Integer(13);
   public static final Integer INTEGER_14 = new Integer(14);
   public static final Integer INTEGER_15 = new Integer(15);

   public static final Integer[] INTEGER_ARRAY = new Integer[]{
      INTEGER_0, INTEGER_1, INTEGER_2, INTEGER_3, INTEGER_4,
      INTEGER_5, INTEGER_6, INTEGER_7, INTEGER_8, INTEGER_9,
      INTEGER_10, INTEGER_11, INTEGER_12, INTEGER_13,
      INTEGER_14, INTEGER_15
   };

   public static final String LINE_SEPARATOR;

   private static final int DEFAULT_BUFSIZE = 1024;

   /**
    * 在处理文本的动态属性时, 是否要显示处理失败的信息
    */
   private static boolean SHOW_RDP_FAIL = false;

   private static Properties properties;
   private static URL properties_URL;
   private static Map classLoaderPropsMap = new WeakHashMap();
   private static Map propertiesMap = new HashMap();
   private static Map dataSourceMap = new HashMap();

   private static DefaultPropertyListener defaultPL = new DefaultPropertyListener();
   private static List plList = new LinkedList();

   static
   {
      properties = new Properties();
      try
      {
         ClassLoader cl = Utility.class.getClassLoader();
         properties_URL = cl.getResource(PROPERTIES_NAME);
         if (properties_URL != null)
         {
            InputStream is = properties_URL.openStream();
            if (is != null)
            {
               properties.load(is);
               is.close();
               loadParentProperties(properties, cl);
            }
         }
         Map tmpMap = new HashMap(2);
         tmpMap.put(PROPERTIES_NAME, properties_URL);
         classLoaderPropsMap.put(cl, tmpMap);
         propertiesMap.put(properties_URL, properties);
      }
      catch (Throwable ex)
      {
         System.err.println(FormatTool.getCurrentDatetimeString()
               + ": Error when init Utility.");
         ex.printStackTrace(System.err);
      }

      String nextLine = "\n";
      try
      {
         addPropertyListener(defaultPL);
         nextLine = (String) java.security.AccessController.doPrivileged(
               new sun.security.action.GetPropertyAction("line.separator"));
         Utility.addFieldPropertyManager(SHOW_RDP_FAIL_PROPERTY, Utility.class, "SHOW_RDP_FAIL");
      }
      catch (Throwable ex)
      {
         System.err.println(FormatTool.getCurrentDatetimeString()
               + ": Error when init Utility.");
         ex.printStackTrace(System.err);
      }
      LINE_SEPARATOR = nextLine;
   }

   /**
    * 载入父配置
    */
   private static void loadParentProperties(Properties props, ClassLoader cl)
         throws IOException
   {
      String pName = props.getProperty(PARENT_PROPERTIES);
      if (pName == null)
      {
         return;
      }
      URL url = cl.getResource(pName);
      if (url == null)
      {
         return;
      }
      InputStream is = url.openStream();
      if (is != null)
      {
         Properties tmpProps = new Properties();
         tmpProps.load(is);
         is.close();
         loadParentProperties(tmpProps, cl);
         Iterator itr = tmpProps.entrySet().iterator();
         while (itr.hasNext())
         {
            Map.Entry entry = (Map.Entry) itr.next();
            if (!props.containsKey(entry.getKey()))
            {
               props.put(entry.getKey(), entry.getValue());
            }
         }
      }
   }

   /**
    * 重新载入配置
    *
    * @param msg   载入配置时的出错信息
    */
   public static void reload(StringRef msg)
   {
      try
      {
         Properties temp = new Properties();
         if (properties_URL != null)
         {
            InputStream is = properties_URL.openStream();
            if (is != null)
            {
               temp.load(is);
               is.close();
               ClassLoader cl = Utility.class.getClassLoader();
               loadParentProperties(temp, cl);
            }
         }
         java.util.Enumeration e = temp.propertyNames();
         while (e.hasMoreElements())
         {
            String name = (String) e.nextElement();
            setProperty(name, temp.getProperty(name));
         }
         // 设置被删除的属性
         e = properties.propertyNames();
         while (e.hasMoreElements())
         {
            String name = (String) e.nextElement();
            if (temp.getProperty(name) == null)
            {
               setProperty(name, null);
            }
         }
         // 由于上面已经设置了属性 所以不用 Utility.properties = temp;
      }
      catch (Throwable ex)
      {
         System.err.println(FormatTool.getCurrentDatetimeString()
               + ": Error when reload.");
         ex.printStackTrace(System.err);
         if (msg != null)
         {
            msg.setString("Reload error:" + ex.getMessage());
         }
      }
   }


   public static String getUID()
   {
      return new UID().toString().replace(':', '_').replace('-', '_');
   }

   public static Log createLog(String name)
   {
      if ("true".equalsIgnoreCase(properties.getProperty(Jdk14Factory.USE_ETERNA_LOG)))
      {
         return new Jdk14Factory().getInstance(name);
      }
      else
      {
         return LogFactory.getLog(name);
      }
   }

   public static ClassLoader getContextClassLoader()
   {
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      return cl != null ? cl : Utility.class.getClassLoader();
   }

   public static Integer createInteger(int i)
   {
      return i >= 0 && i <= 15 ? INTEGER_ARRAY[i] : new Integer(i);
   }

   /**
    * 根据ClassLoader获得本上下文中的配置文件
    */
   public static Properties getProperties(ClassLoader cl, ObjectRef prop_URL)
   {
      return getProperties(cl, prop_URL, null);
   }

   /**
    * 根据ClassLoader获得本上下文中的指定的配置文件
    */
   public static synchronized Properties getProperties(ClassLoader cl, ObjectRef prop_URL, String propLocal)
   {
      String tmpLocal = propLocal == null ? PROPERTIES_NAME : propLocal;
      try
      {
         // 根据ClassLoader获取配置表
         Map urlMap = (Map) classLoaderPropsMap.get(cl);
         if (urlMap == null)
         {
            urlMap = new HashMap();
            classLoaderPropsMap.put(cl, urlMap);
         }
         // 在配置表中获取对应的配置文件的URL
         URL tempURL = (URL) urlMap.get(tmpLocal);
         Properties tmpProps = null;
         if (tempURL != null)
         {
            // 根据配置文件的URL在属性表中获取属性
            tmpProps = (Properties) propertiesMap.get(tempURL);
            if (tmpProps != null)
            {
               if (prop_URL != null) prop_URL.setObject(tempURL);
               return tmpProps;
            }
         }
         // 没有对应的属性, 则根据配置从ClassLoader获取URL资源
         Enumeration e = cl.getResources(tmpLocal);
         if (e == null)
         {
            // 获取不到URL资源, 则返回默认的
            if (propLocal == null || tmpLocal.equals(PROPERTIES_NAME))
            {
               urlMap.put(tmpLocal, properties_URL);
               if (prop_URL != null) prop_URL.setObject(properties_URL);
               return properties;
            }
            return null;
         }

         while (e.hasMoreElements())
         {
            // 对每个URL在属性表中获取属性, 如果存在属性, 则作为备选
            tempURL = (URL) e.nextElement();
            tmpProps = (Properties) propertiesMap.get(tempURL);
            if (tmpProps != null)
            {
               if (prop_URL != null) prop_URL.setObject(tempURL);
            }
            else
            {
               // 资源中不存在属性, 则读取此属性并返回
               InputStream is = tempURL.openStream();
               if (is != null)
               {
                  tmpProps = new Properties();
                  tmpProps.load(is);
                  is.close();
                  loadParentProperties(tmpProps, cl);
                  urlMap.put(tmpLocal, tempURL);
                  propertiesMap.put(tempURL, tmpProps);
                  if (prop_URL != null) prop_URL.setObject(tempURL);
                  return tmpProps;
               }
            }
         }
         // 能执行到这里表示没有获取到新的属性, 使用备选属性
         if (tmpProps != null)
         {
            urlMap.put(tmpLocal, tempURL);
            return tmpProps;
         }
         // 获取不到属性, 则返回默认的
         if (propLocal == null || tmpLocal.equals(PROPERTIES_NAME))
         {
            urlMap.put(tmpLocal, properties_URL);
            if (tmpProps != null)
            {
               prop_URL.setObject(properties_URL);
            }
            return properties;
         }
      }
      catch (Throwable ex)
      {
         System.err.println(FormatTool.getCurrentDatetimeString()
               + ": Error when getProperties in Utility.");
         ex.printStackTrace(System.err);
      }
      return null;
   }

   public static String getProperty(String key)
   {
      return properties.getProperty(key);
   }

   public static String getProperty(String key, String defaultValue)
   {
      return properties.getProperty(key, defaultValue);
   }

   public static void setProperty(String key, String value)
   {
      String oldValue = properties.getProperty(key);
      // 判断新的值和原值是否相等
      if (oldValue != null)
      {
         if (oldValue.equals(value))
         {
            return;
         }
      }
      if (value == null)
      {
         return;
      }

      properties.setProperty(key, value);
      Iterator itr = plList.iterator();
      while (itr.hasNext())
      {
         ((PropertyListener) itr.next()).propertyChanged(key, oldValue, value);
      }
   }

   /**
    * 判断两个对象是否相同. <p>
    * 如:
    * null == null
    * obj != null
    * null != obj
    * obj1 != obj2
    * obj1 == obj1
    */
   public static boolean objectEquals(Object thisObj, Object otherObj)
   {
      if (thisObj == null)
      {
         return otherObj == null;
      }
      return thisObj.equals(otherObj);
   }

   /**
    * 配置监控者添加完后, 判断并处理是否要将配置中的值设置到目标中.
    */
   private static void dealChangeProperty(String key, String defaultValue, PropertyManager pm)
   {
      String temp = getProperty(key);
      boolean setted = false;
      if (temp == null && defaultValue != null)
      {
         temp = defaultValue;
         setProperty(key, defaultValue);
         setted = true;
      }
      try
      {
         if (temp != null && !setted)
         {
            // 如果存在要设置的值, 且未设置过值, 则要将值设置到被监控的属性中
            pm.changeProperty(temp);
         }
      }
      catch (Throwable ex) {}
   }

   /**
    * 单个String类型的参数.
    */
   private static Class[] STR_PARAM = {String.class};

   /**
    * 添加一个配置监控者, 当配置的值改变时, 它会自动更新指定类的静态属性成员, 该属性
    * 的类型可以是: <code>String</code>, <code>int</code>或<code>boolean</code>.
    *
    * @param key            配置的键值
    * @param theClass       被修改的属性所在的类
    * @param fieldName      需要被修改的静态属性名称
    */
   public static void addFieldPropertyManager(String key, Class theClass, String fieldName)
         throws NoSuchFieldException
   {
      addFieldPropertyManager(key, theClass, fieldName, null);
   }

   /**
    * 添加一个配置监控者, 当配置的值改变时, 它会自动更新指定类的静态属性成员, 该属性
    * 的类型可以是: <code>String</code>, <code>int</code>或<code>boolean</code>.
    *
    * @param key            配置的键值
    * @param theClass       被修改的属性所在的类
    * @param fieldName      需要被修改的静态属性名称
    * @param defaultValue   当配置中不存在指定的键值时使用的默认值
    */
   public static void addFieldPropertyManager(String key, Class theClass, String fieldName,
         String defaultValue)
         throws NoSuchFieldException
   {
      PropertyManager pm = new PropertyManager(key, theClass, theClass.getDeclaredField(fieldName));
      defaultPL.addPropertyManager(key, pm);
      dealChangeProperty(key, defaultValue, pm);
   }

   /**
    * 移除一个配置监控者.
    *
    * @param key            配置的键值
    * @param theClass       被修改的属性所在的类
    * @param fieldName      需要被修改的静态属性名称
    */
   public static void removeFieldPropertyManager(String key, Class theClass, String fieldName)
         throws NoSuchFieldException
   {
      PropertyManager pm = new PropertyManager(key, theClass, theClass.getDeclaredField(fieldName));
      defaultPL.removePropertyManager(key, pm);
   }

   /**
    * 添加一个配置监控者, 当配置的值改变时, 它会自动调用指定类的静态方法,
    * 此方法必须是只有一个<code>String</code>类型的参数.
    *
    * @param key            配置的键值
    * @param theClass       被调用的方法所在的类
    * @param methodName     需要被调用的静态方法名称
    */
   public static void addMethodPropertyManager(String key, Class theClass, String methodName)
         throws NoSuchMethodException
   {
      addMethodPropertyManager(key, theClass, methodName, null);
   }

   /**
    * 添加一个配置监控者, 当配置的值改变时, 它会自动调用指定类的静态方法,
    * 此方法必须是只有一个<code>String</code>类型的参数.
    *
    * @param key            配置的键值
    * @param theClass       被调用的方法所在的类
    * @param methodName     需要被调用的静态方法名称
    * @param defaultValue   当配置中不存在指定的键值时使用的默认值
    */
   public static void addMethodPropertyManager(String key, Class theClass, String methodName,
         String defaultValue)
         throws NoSuchMethodException
   {
      PropertyManager pm = new PropertyManager(key, theClass,
            theClass.getDeclaredMethod(methodName, STR_PARAM));
      defaultPL.addPropertyManager(key, pm);
      dealChangeProperty(key, defaultValue, pm);
   }

   /**
    * 移除一个配置监控者.
    *
    * @param key            配置的键值
    * @param theClass       被调用的方法所在的类
    * @param methodName     需要被调用的静态方法名称
    */
   public static void removeMethodPropertyManager(String key, Class theClass, String methodName)
         throws NoSuchMethodException
   {
      PropertyManager pm = new PropertyManager(key, theClass,
            theClass.getDeclaredMethod(methodName, STR_PARAM));
      defaultPL.removePropertyManager(key, pm);
   }

   /**
    * 添加一个配置变更的监听者.
    */
   public static synchronized void addPropertyListener(PropertyListener l)
   {
      if (!plList.contains(l))
      {
         plList.add(l);
      }
   }

   /**
    * 移除一个配置变更的监听者.
    */
   public static synchronized void removePropertyListener(PropertyListener l)
   {
      plList.remove(l);
   }

   /**
    * 将in中的值全部复制到out中, 但是不关闭in和out.
    */
   public static void copyStream(InputStream in, OutputStream out)
         throws IOException
   {
      copyStream(in, out, DEFAULT_BUFSIZE);
   }

   /**
    * 将in中的值部分复制到out中(复制limit个字节), 但是不关闭in和out.
    *
    * @param limit   复制的字节个数, 如果为-1, 则表示没有限制
    *
    * @return   实际复制的字节数, 如果参数limit设置为-1, 则不会计算实际复制的个数,
    *           返回值为-1
    */
   public static int copyStream(int limit, InputStream in, OutputStream out)
         throws IOException
   {
      return copyStream(limit, in, out, DEFAULT_BUFSIZE);
   }

   /**
    * 将in中的值全部复制到out中, 但是不关闭in和out.
    *
    * @param bufSize  复制时使用的缓存的大小
    */
   public static void copyStream(InputStream in, OutputStream out, int bufSize)
         throws IOException
   {
      bufSize = bufSize <= 0 ? DEFAULT_BUFSIZE : bufSize;
      byte[] buf = new byte[bufSize];
      int readCount = in.read(buf);
      while (readCount > 0)
      {
         out.write(buf, 0, readCount);
         readCount = in.read(buf);
      }
   }

   /**
    * 将in中的值部分复制到out中(复制limit个字节), 但是不关闭in和out.
    *
    * @param limit     复制的字节个数, 如果为-1, 则表示没有限制
    * @param bufSize   复制时使用的缓存的大小
    *
    * @return   实际复制的字节数, 如果参数limit设置为-1, 则不会计算实际复制的个数,
    *           返回值为-1
    */
   public static int copyStream(int limit, InputStream in, OutputStream out, int bufSize)
         throws IOException
   {
      if (limit == -1)
      {
         copyStream(in, out, bufSize);
         return -1;
      }
      if (limit < 0)
      {
         throw new IllegalArgumentException("Error limit:" + limit);
      }
      if (limit == 0)
      {
         return 0;
      }
      bufSize = bufSize <= 0 ? DEFAULT_BUFSIZE : bufSize;
      byte[] buf = new byte[limit > bufSize ? bufSize : limit];
      int allCount = 0;
      int leftCount = limit;
      int readCount = in.read(buf);
      while (readCount > 0)
      {
         out.write(buf, 0, readCount);
         allCount += readCount;
         leftCount -= readCount;
         if (allCount >= limit)
         {
            break;
         }
         readCount = in.read(buf, 0, buf.length > leftCount ? leftCount : buf.length);
      }
      return allCount;
   }

   /**
    * 将in中的值全部复制到out中, 但是不关闭in和out.
    */
   public static void copyChars(Reader in, Writer out)
         throws IOException
   {
      copyChars(in, out, DEFAULT_BUFSIZE);
   }

   /**
    * 将in中的值部分复制到out中(复制limit个字节), 但是不关闭in和out.
    *
    * @param limit   复制的字节个数, 如果为-1, 则表示没有限制
    *
    * @return   实际复制的字节数, 如果参数limit设置为-1, 则不会计算实际复制的个数,
    *           返回值为-1
    */
   public static int copyChars(int limit, Reader in, Writer out)
         throws IOException
   {
      return copyChars(limit, in, out, DEFAULT_BUFSIZE);
   }

   /**
    * 将in中的值全部复制到out中, 但是不关闭in和out.
    *
    * @param bufSize  复制时使用的缓存的大小
    */
   public static void copyChars(Reader in, Writer out, int bufSize)
         throws IOException
   {
      bufSize = bufSize <= 0 ? DEFAULT_BUFSIZE : bufSize;
      char[] buf = new char[bufSize];
      int readCount = in.read(buf);
      while (readCount > 0)
      {
         out.write(buf, 0, readCount);
         readCount = in.read(buf);
      }
   }

   /**
    * 将in中的值部分复制到out中(复制limit个字节), 但是不关闭in和out.
    *
    * @param limit     复制的字节个数, 如果为-1, 则表示没有限制
    * @param bufSize   复制时使用的缓存的大小
    *
    * @return   实际复制的字节数, 如果参数limit设置为-1, 则不会计算实际复制的个数,
    *           返回值为-1
    */
   public static int copyChars(int limit, Reader in, Writer out, int bufSize)
         throws IOException
   {
      if (limit == -1)
      {
         copyChars(in, out, bufSize);
         return -1;
      }
      if (limit < 0)
      {
         throw new IllegalArgumentException("Error limit:" + limit);
      }
      if (limit == 0)
      {
         return 0;
      }
      bufSize = bufSize <= 0 ? DEFAULT_BUFSIZE : bufSize;
      char[] buf = new char[limit > bufSize ? bufSize : limit];
      int allCount = 0;
      int leftCount = limit;
      int readCount = in.read(buf);
      while (readCount > 0)
      {
         out.write(buf, 0, readCount);
         allCount += readCount;
         leftCount -= readCount;
         if (allCount >= limit)
         {
            break;
         }
         readCount = in.read(buf, 0, buf.length > leftCount ? leftCount : buf.length);
      }
      return allCount;
   }

   public static DataSource getDataSource()
   {
      ClassLoader cl = getContextClassLoader();
      if (cl == null)
      {
         cl = Utility.class.getClassLoader();
      }
      ObjectRef urlRef = new ObjectRef();
      URL prop_URL;
      Properties prop = getProperties(cl, urlRef);
      if (prop == null)
      {
         prop = properties;
         prop_URL = properties_URL;
      }
      else
      {
         prop_URL = (URL) urlRef.getObject();
      }
      DataSource dataSource = (DataSource) dataSourceMap.get(prop_URL);
      if (dataSource != null)
      {
         return dataSource;
      }

      synchronized (dataSourceMap)
      {
         dataSource = (DataSource) dataSourceMap.get(prop_URL);
         if (dataSource != null)
         {
            return dataSource;
         }
         System.out.println("Start creat datasource, URL:" + prop_URL);

         String className = prop.getProperty("dataSource.className");
         if (className != null && className.length() > 0)
         {
            System.out.println("Creat datasource:" + className + ".");
         }
         else
         {
            className = "org.apache.struts.legacy.GenericDataSource";
            System.out.println("Creat default datasource:" + className + ".");
         }

         try
         {
            Class c = Class.forName(className);
            dataSource = (DataSource) c.newInstance();
            setDataSourceProperties(dataSource, prop);
         }
         catch (Exception ex)
         {
            System.out.println("Error! Creat datasource:" + className + " message:" + ex.getMessage());
         }
         dataSourceMap.put(prop_URL, dataSource);
         return dataSource;
      }
   }

   private final static String[] DATASOURCE_PROPERTIES = {
      "description", "String", "driverClass", "String", "maxCount", "int", "minCount", "int",
      "url", "String", "user", "String", "password", "String", "autoCommit", "boolean"
   };

   private static void setDataSourceProperties(DataSource dataSource, Properties prop)
         throws Exception
   {
      Class c = dataSource.getClass();
      for (int i = 0; i < DATASOURCE_PROPERTIES.length; i += 2)
      {
         String name = DATASOURCE_PROPERTIES[i];
         String type = DATASOURCE_PROPERTIES[i + 1];
         String value = prop.getProperty("dataSource." + name);
         if (value != null)
         {
            String fName = "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
            Class[] params = STR_PARAM;
            if ("int".equals(type))
            {
               params = new Class[]{int.class};
            }
            else if ("boolean".equals(type))
            {
               params = new Class[]{boolean.class};
            }
            Method m = c.getDeclaredMethod(fName, params);
            Object v = value;
            if ("int".equals(type))
            {
               v = new Integer(value);
            }
            else if ("boolean".equals(type))
            {
               v = new Boolean(value);
            }
            m.invoke(dataSource, new Object[]{v});
         }
      }
   }


	/**
    * 动态属性名称的前缀: "${"
    */
	public static final String DYNAMIC_PROPNAME_PREFIX = "${";
	/**
    * 动态属性名称的后缀:: "}"
    */
	public static final String DYNAMIC_PROPNAME_SUFFIX = "}";

   /**
	 * 处理文本中"${...}"的动态属性, 将他们替换成配置文件
    * (micromagic_config.properties 或 System.property)中的对应值.
    *
	 * @param text      要处理的文本
	 * @return 处理完的文本
	 * @see #DYNAMIC_PROPNAME_PREFIX
	 * @see #DYNAMIC_PROPNAME_SUFFIX
	 */
	public static String resolveDynamicPropnames(String text)
   {
      return resolveDynamicPropnames(text, null, false);
   }

   /**
	 * 处理文本中"${...}"的动态属性, 将他们替换成配置文件
    * (bindRes 或 micromagic_config.properties 或 System.property)中的对应值.
    *
	 * @param text      要处理的文本
	 * @param bindRes   绑定的资源, 会先在bindRes寻找对应的值
	 * @return 处理完的文本
	 * @see #DYNAMIC_PROPNAME_PREFIX
	 * @see #DYNAMIC_PROPNAME_SUFFIX
	 */
	public static String resolveDynamicPropnames(String text, Map bindRes)
   {
      return resolveDynamicPropnames(text, bindRes, false);
   }

   /**
	 * 处理文本中"${...}"的动态属性, 将他们替换成配置文件
    * (bindRes 或 micromagic_config.properties 或 System.property)中的对应值.
    *
	 * @param text      要处理的文本
	 * @param bindRes   绑定的资源, 会先在bindRes寻找对应的值
	 * @param onlyRes   设置为<code>true</code>时, 只对绑定的资源进行处理, 设置为
    *                  <code>false</code>时, 如果绑定的资源中不存在对应的值会再到
    *                  micromagic_config.properties 或 System.property中寻找
	 * @return 处理完的文本
	 * @see #DYNAMIC_PROPNAME_PREFIX
	 * @see #DYNAMIC_PROPNAME_SUFFIX
	 */
	public static String resolveDynamicPropnames(String text, Map bindRes, boolean onlyRes)
   {
      if (text == null)
      {
         return text;
      }
      int startIndex = text.indexOf(DYNAMIC_PROPNAME_PREFIX);
      if (startIndex == -1)
      {
         return text;
      }

      String tempStr = text;
      StringAppender result = StringTool.createStringAppender(text.length() + 32);
      while (startIndex != -1)
      {
         result.append(tempStr.substring(0, startIndex));
         int endIndex = tempStr.indexOf(DYNAMIC_PROPNAME_SUFFIX, startIndex + DYNAMIC_PROPNAME_PREFIX.length());
         if (endIndex != -1)
         {
            String dName = tempStr.substring(startIndex + DYNAMIC_PROPNAME_PREFIX.length(), endIndex);
            try
            {
               String pValue = null;
               if (bindRes != null)
               {
                  Object obj = bindRes.get(dName);
                  if (obj != null)
                  {
                     pValue = String.valueOf(obj);
                  }
               }
               if (!onlyRes)
               {
                  if (pValue == null)
                  {
                     // 如果bindRes为null或其中不存在, 则到micromagic_config.properties中查找
                     pValue = getProperty(dName);
                  }
                  if (pValue == null)
                  {
                     // 如果micromagic_config.properties中不存在, 则到系统属性中查找
                     pValue = System.getProperty(dName);
                  }
               }
               if (pValue != null)
               {
                  result.append(resolveDynamicPropnames(pValue, bindRes));
               }
               else
               {
                  result.append(tempStr.substring(startIndex, endIndex + 1));
                  if (SHOW_RDP_FAIL)
                  {
                     Utility.createLog("util").warn("Could not resolve dynamic name '" + dName
                           + "' in [" + text + "] as config property.");
                  }
               }
            }
            catch (Throwable ex)
            {
               if (SHOW_RDP_FAIL)
               {
                  String msg = "Could not resolve dynamic name '" + dName
                        + "' in [" + text + "] as config property.";
                  Utility.createLog("util").warn(msg, ex);
               }
            }
            tempStr = tempStr.substring(endIndex + DYNAMIC_PROPNAME_SUFFIX.length());
            startIndex = tempStr.indexOf(DYNAMIC_PROPNAME_PREFIX);
         }
         else
         {
            tempStr = tempStr.substring(startIndex);
            startIndex = -1;
         }
      }
      result.append(tempStr);

      return result.toString();
   }


   /**
    * 配置监听者.
    */
   public interface PropertyListener extends EventListener
   {
      /**
       * 当某个配置值发生了改变时, 会调用此方法.
       *
       * @param key       发生改变的配置的键值
       * @param oldValue  改变前配置的原始值
       * @param newValue  改变后配置的值
       */
      public void propertyChanged(String key, String oldValue, String newValue);

   }

   private static class DefaultPropertyListener
         implements PropertyListener
   {
      private Map propertyMap = new HashMap();

      public synchronized void addPropertyManager(String key, PropertyManager pm)
      {
         PropertyManager[] pms = (PropertyManager[]) this.propertyMap.get(key);
         if (pms == null)
         {
            pms = new PropertyManager[]{pm};
         }
         else
         {
            for (int i = 0; i < pms.length; i++)
            {
               if (pms[i].equals(pm))
               {
                  return;
               }
            }
            PropertyManager[] newPms = new PropertyManager[pms.length + 1];
            System.arraycopy(pms, 0, newPms, 0, pms.length);
            newPms[pms.length] = pm;
            pms = newPms;
         }
         this.propertyMap.put(key, pms);
      }

      public synchronized void removePropertyManager(String key, PropertyManager pm)
      {
         PropertyManager[] pms = (PropertyManager[]) this.propertyMap.get(key);
         if (pms == null)
         {
            return;
         }

         for (int i = 0; i < pms.length; i++)
         {
            //System.out.println(pms.length + ":" + pm);
            if (pms[i].equals(pm))
            {
               if (pms.length == 1)
               {
                  this.propertyMap.remove(key);
                  return;
               }
               PropertyManager[] newPms = new PropertyManager[pms.length - 1];
               System.arraycopy(pms, 0, newPms, 0, i);
               System.arraycopy(pms, i + 1, newPms, i, pms.length - i - 1);
               pms = newPms;
               this.propertyMap.put(key, pms);
               return;
            }
         }
      }

      public void propertyChanged(String key, String oldValue, String newValue)
      {
         // 判断新的值和原值是否相等
         if (oldValue != null)
         {
            if (oldValue.equals(newValue))
            {
               return;
            }
         }
         if (newValue == null)
         {
            return;
         }

         PropertyManager[] pms = (PropertyManager[]) this.propertyMap.get(key);
         if (pms == null)
         {
            return;
         }

         try
         {
            for (int i = 0; i < pms.length; i++)
            {
               pms[i].changeProperty(newValue);
            }
         }
         catch (Exception ex)
         {
            Log log = Utility.createLog("Utility");
            log.warn("Error when change property.", ex);
         }
      }

   }

   private static class PropertyManager
   {
      private static final IntegerConverter intConverter;
      private static final BooleanConverter boolanConverter;

      static
      {
         intConverter = new IntegerConverter();
         boolanConverter = new BooleanConverter();
         intConverter.setNeedThrow(true);
         boolanConverter.setNeedThrow(true);
      }

      /**
       * 用于清楚weak方式的引用队列.
       */
      private static final ReferenceQueue queue = new ReferenceQueue();

      /**
       * 对应属性的键值.
       */
      private String key;

      /**
       * 这里使用<code>WeakReference</code>来引用对应的类, 并在其释放时删除本属性管理者.
       */
      private WeakReference baseClass;

      /**
       * 这里使用<code>WeakReference</code>来引用对应的成员, 这样不会影响类的正常释放.
       */
      private WeakReference optMember;

      /**
       * 要操作的成员名称.
       */
      private String optMemberName;

      /**
       * 标识是否是属性成员, <code>true</code>表示属性成员, <code>false</code>表示方法成员.
       */
      private boolean fieldMember;

      /**
       * 如果为属性成员时, 当类型为整型或布尔型时, 使用的转换器.
       */
      private ValueConverter fieldConverter = null;

      private PropertyManager(String key, boolean fieldMember, Class baseClass, Member optMember)
      {
         expunge();
         if (key == null)
         {
            throw new IllegalArgumentException("The property key can't be null.");
         }
         this.key = key;
         this.fieldMember = fieldMember;
         this.baseClass = new BaseClassRef(this, baseClass, this.queue);
         this.optMember = new WeakReference(optMember);
         this.optMemberName = optMember.getName();
         if (!Modifier.isStatic(optMember.getModifiers()))
         {
            throw new IllegalArgumentException("The opt member must be static.");
         }
      }

      public PropertyManager(String key, Class theClass, Method theMethod)
      {
         this(key, false, theClass, theMethod);
      }

      public PropertyManager(String key, Class theClass, Field theField)
      {
         this(key, true, theClass, theField);
         if (Modifier.isFinal(theField.getModifiers()))
         {
            throw new IllegalArgumentException("The field can't be final.");
         }
         if (int.class == theField.getType())
         {
            this.fieldConverter = intConverter;
         }
         else if (boolean.class == theField.getType())
         {
            this.fieldConverter = boolanConverter;
         }
         else if (String.class != theField.getType())
         {
            throw new IllegalArgumentException("Error field type, class:["
                  + ClassGenerator.getClassName(theClass) + "], field:[" + theField.getName()
                  + "], type:[" + ClassGenerator.getClassName(theField.getType()) + "].");
         }
      }

      private Member getOptMember()
            throws NoSuchFieldException, NoSuchMethodException
      {
         Member m = (Member) this.optMember.get();
         if (m != null)
         {
            return m;
         }
         Class c = (Class) this.baseClass.get();
         if (c == null)
         {
            return null;
         }
         if (this.fieldMember)
         {
            m = c.getDeclaredField(this.optMemberName);
         }
         else
         {
            m = c.getDeclaredMethod(this.optMemberName, STR_PARAM);
         }
         this.optMember = new WeakReference(m);
         return m;
      }

      public void changeProperty(String value)
            throws Exception
      {
         expunge();
         Member member = this.getOptMember();
         // 如果操作的成员为null, 则不执行变更.
         if (member == null)
         {
            return;
         }
         if (this.fieldMember)
         {
            Object objValue = value;
            Field theField = (Field) member;
            if (this.fieldConverter != null)
            {
               try
               {
                  objValue = this.fieldConverter.convert(value);
               }
               catch (Throwable ex)
               {
                  Utility.createLog("util").warn("Type convert error.", ex);
                  return;
               }
            }
            if (!theField.isAccessible())
            {
               theField.setAccessible(true);
               theField.set(null, objValue);
               theField.setAccessible(false);
            }
            else
            {
               theField.set(null, objValue);
            }
         }
         else
         {
            Method theMethod = (Method) member;
            if (!theMethod.isAccessible())
            {
               theMethod.setAccessible(true);
               theMethod.invoke(null, new Object[]{value});
               theMethod.setAccessible(false);
            }
            else
            {
               theMethod.invoke(null, new Object[]{value});
            }
         }
      }

      /**
       * 清除过期的属性管理者.
       */
      private static void expunge()
      {
         BaseClassRef bcr = (BaseClassRef) queue.poll();
         while (bcr != null)
         {
            PropertyManager pm = bcr.getPropertyManager();
            defaultPL.removePropertyManager(pm.key, pm);
            bcr = (BaseClassRef) queue.poll();
         }
      }

      public boolean equals(Object obj)
      {
         if (this == obj)
         {
            return true;
         }
         if (obj instanceof PropertyManager)
         {
            PropertyManager pm = (PropertyManager) obj;
            if (!this.key.equals(pm.key))
            {
               return false;
            }
            if (!objectEquals(this.baseClass.get(), pm.baseClass.get()))
            {
               return false;
            }
            if (this.fieldMember != pm.fieldMember)
            {
               return false;
            }
            if (!objectEquals(this.optMemberName, pm.optMemberName))
            {
               return false;
            }
            return true;
         }
         return false;
      }

      public String toString()
      {
         StringAppender temp = StringTool.createStringAppender(128);
         Class baseClass = (Class) this.baseClass.get();
         temp.append("PropertyManager[class:").append(
               baseClass == null ? "<released>" : ClassGenerator.getClassName(baseClass));
         Member member = (Member) this.optMember.get();
         if (this.fieldMember)
         {
            temp.append(" field:(");
         }
         else
         {
            temp.append(" method:(");
         }
         temp.append(member == null ? "<released>" : member.getName()).append(")").append("]");
         return temp.toString();
      }

   }

   private static class BaseClassRef extends WeakReference
   {
      private PropertyManager pm;

      public BaseClassRef(PropertyManager pm, Object baseClass, ReferenceQueue q)
      {
         super(baseClass, q);
         this.pm = pm;
      }

      public PropertyManager getPropertyManager()
      {
         return this.pm;
      }

   }


}
