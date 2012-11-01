
package self.micromagic.util;

import java.util.Properties;
import java.util.Iterator;
import java.util.Map;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.lang.reflect.Method;
import java.lang.reflect.Field;

import org.apache.commons.logging.Log;
import self.micromagic.util.converter.IntegerConverter;
import self.micromagic.util.converter.BooleanConverter;
import self.micromagic.util.converter.ConverterFinder;
import self.micromagic.cg.ClassGenerator;

/**
 * 配置属性的管理器.
 *
 * @author micromagic@sina.com
 */
public class PropertiesManager
{
   /**
    * 默认的配置文件名.
	 * 注: 配置文件都必须在classpath下.
    */
   public static final String PROPERTIES_NAME = "micromagic_config.properties";

   /**
    * 存放父配置文件名的属性.
    */
   public static final String PARENT_PROPERTIES = "self.micromagic.parent.properties";


	/**
	 * 配置文件名.
	 * 注: 配置文件都必须在classpath下.
	 */
	private String propName;

	/**
	 * 读取配置文件所使用的<code>ClassLoader</code>.
	 */
	private ClassLoader classLoader;

	/**
	 * 当前所读取的配置属性.
	 */
	private Properties properties = new Properties();

	/**
	 * 属性变化监听者列表.
	 */
   private List plList = new LinkedList();

	/**
	 * 配置属性的管理器中, 默认的属性变化监听者.
	 */
   private DefaultPropertyListener defaultPL = new DefaultPropertyListener();

	/**
	 * 默认的构造函数.
	 * 默认的配置文件名及本类的<code>ClassLoader</code>.
	 */
	public PropertiesManager()
	{
		this(null, null);
	}

	/**
	 * 构造一个配置属性的管理器.
	 *
	 * @param propName      配置文件名, 必须在classpath下, 需给出的是classpath路径
	 *                      如: com/xxx.properties
	 * @param classLoader   读取配置文件所使用的<code>ClassLoader</code>
	 */
	public PropertiesManager(String propName, ClassLoader classLoader)
	{
		this.propName = propName == null ? PROPERTIES_NAME : propName;
		this.classLoader = classLoader == null ? this.getClass().getClassLoader() : classLoader;
		this.addPropertyListener(this.defaultPL);
		this.reload();
	}

   /**
    * (重新)载入配置.
    */
   public void reload()
   {
		this.reload(null);
	}

   /**
    * (重新)载入配置.
    *
    * @param msg   出参, 载入配置时的出错信息
    */
   public void reload(StringRef msg)
   {
      try
      {
			URL url = this.classLoader.getResource(this.propName);
			if (url == null)
			{
				if (msg != null)
				{
					msg.setString("The properties name:[" + this.propName + "] not found in class loader.");
				}
				return;
			}
         Properties temp = new Properties();
			InputStream inStream = url.openStream();
			temp.load(inStream);
			inStream.close();
			this.loadParentProperties(temp);
         Iterator itr = temp.entrySet().iterator();
         while (itr.hasNext())
         {
            Map.Entry entry = (Map.Entry) itr.next();
            this.setProperty((String) entry.getKey(), (String) entry.getValue());
         }
         // 设置被删除的属性
         Enumeration e = this.properties.propertyNames();
         while (e.hasMoreElements())
         {
            String name = (String) e.nextElement();
            if (temp.getProperty(name) == null)
            {
               this.setProperty(name, null);
            }
         }
         // 由于上面已经设置了属性 所以不用对properties赋值;
      }
      catch (Throwable ex)
      {
         System.err.println(FormatTool.getCurrentDatetimeString()
               + ": Error when reload properties.");
         ex.printStackTrace(System.err);
         if (msg != null)
         {
            msg.setString("Reload properties error:" + ex.getMessage());
         }
      }
   }

	/**
	 * 获取属性值.
	 *
	 * @param key  属性所在的键值
	 */
   public String getProperty(String key)
   {
      return this.properties.getProperty(key);
   }

	/**
	 * 获取属性值.
	 *
	 * @param key          属性所在的键值
	 * @param defaultValue 此键值下没属性时将返回此默认值
	 */
   public String getProperty(String key, String defaultValue)
   {
      return this.properties.getProperty(key, defaultValue);
   }

	/**
	 * 设置属性值.
	 *
	 * @param key    属性所在的键值
	 * @param value  需要设置的值
	 */
   public void setProperty(String key, String value)
   {
      String oldValue = this.properties.getProperty(key);
      // 判断新的值和原值是否相等
      if (oldValue != null)
      {
         if (oldValue.equals(value))
         {
            return;
         }
      }
      else if (value == null)
      {
         return;
      }

		if (value == null)
		{
			this.properties.remove(key);
		}
		else
		{
      	this.properties.setProperty(key, value);
		}
      Iterator itr = this.plList.iterator();
      while (itr.hasNext())
      {
         ((PropertyListener) itr.next()).propertyChanged(key, oldValue, value);
      }
   }

	/**
	 * 移除属性值.
	 *
	 * @param key    属性所在的键值
	 */
   public void removeProperty(String key)
   {
		this.properties.setProperty(key, null);
	}

   /**
    * 配置监控者添加完后, 判断并处理是否要将配置中的值设置到目标中.
    */
   private void dealChangeProperty(String key, String defaultValue, PropertyManager pm)
   {
      String temp = this.getProperty(key);
      boolean setted = false;
      if (temp == null && defaultValue != null)
      {
         temp = defaultValue;
         this.setProperty(key, defaultValue);
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
	 * 添加一个配置监控者, 当配置的值改变时, 它会自动更新指定类的静态属性成员, 该属性
	 * 的类型可以是: <code>String</code>, <code>int</code>或<code>boolean</code>.
	 *
	 * @param key            配置的键值
	 * @param theClass       被修改的属性所在的类
	 * @param fieldName      需要被修改的静态属性名称
	 */
	public void addFieldPropertyManager(String key, Class theClass, String fieldName)
			throws NoSuchFieldException
	{
		this.addFieldPropertyManager(key, theClass, fieldName, null);
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
	public void addFieldPropertyManager(String key, Class theClass, String fieldName,
			String defaultValue)
			throws NoSuchFieldException
	{
		PropertyManager pm = new PropertyManager(key, theClass,
				theClass.getDeclaredField(fieldName), this.defaultPL);
		this.defaultPL.addPropertyManager(key, pm);
		this.dealChangeProperty(key, defaultValue, pm);
	}

	/**
	 * 移除一个配置监控者.
	 *
	 * @param key            配置的键值
	 * @param theClass       被修改的属性所在的类
	 * @param fieldName      需要被修改的静态属性名称
	 */
	public void removeFieldPropertyManager(String key, Class theClass, String fieldName)
			throws NoSuchFieldException
	{
		PropertyManager pm = new PropertyManager(key, theClass,
				theClass.getDeclaredField(fieldName), this.defaultPL);
		this.defaultPL.removePropertyManager(key, pm);
	}

	/**
	 * 添加一个配置监控者, 当配置的值改变时, 它会自动调用指定类的静态方法,
	 * 此方法必须是只有一个<code>String</code>类型的参数.
	 *
	 * @param key            配置的键值
	 * @param theClass       被调用的方法所在的类
	 * @param methodName     需要被调用的静态方法名称
	 */
	public void addMethodPropertyManager(String key, Class theClass, String methodName)
			throws NoSuchMethodException
	{
		this.addMethodPropertyManager(key, theClass, methodName, null);
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
	public void addMethodPropertyManager(String key, Class theClass, String methodName,
			String defaultValue)
			throws NoSuchMethodException
	{
		PropertyManager pm = new PropertyManager(key, theClass,
				theClass.getDeclaredMethod(methodName, Utility.STR_PARAM), this.defaultPL);
		this.defaultPL.addPropertyManager(key, pm);
		this.dealChangeProperty(key, defaultValue, pm);
	}

	/**
	 * 移除一个配置监控者.
	 *
	 * @param key            配置的键值
	 * @param theClass       被调用的方法所在的类
	 * @param methodName     需要被调用的静态方法名称
	 */
	public void removeMethodPropertyManager(String key, Class theClass, String methodName)
			throws NoSuchMethodException
	{
		PropertyManager pm = new PropertyManager(key, theClass,
				theClass.getDeclaredMethod(methodName, Utility.STR_PARAM), this.defaultPL);
		this.defaultPL.removePropertyManager(key, pm);
	}

   /**
    * 添加一个配置变化的监听者.
    */
   public synchronized void addPropertyListener(PropertyListener l)
   {
      if (!this.plList.contains(l))
      {
         this.plList.add(l);
      }
   }

   /**
    * 移除一个配置变化的监听者.
    */
   public synchronized void removePropertyListener(PropertyListener l)
   {
      this.plList.remove(l);
   }

   /**
    * 载入父配置
    */
   private void loadParentProperties(Properties props)
         throws IOException
   {
      String pName = props.getProperty(PARENT_PROPERTIES);
      if (pName == null)
      {
         return;
      }
      URL url = this.classLoader.getResource(pName);
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
         this.loadParentProperties(tmpProps);
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
	 * 配置变化的监听者.
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

	/**
	 * 默认的配置变化监听者.
	 */
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

	/**
	 * 单个属性的管理器, 给默认的配置变化监听者使用.
	 */
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
		private final ReferenceQueue queue = new ReferenceQueue();

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
		 * 该配置管理器所在的listener.
		 */
		private DefaultPropertyListener listener;

		private PropertyManager(String key, boolean fieldMember, Class baseClass, Member optMember,
				DefaultPropertyListener listener)
		{
			expunge();
			if (key == null)
			{
				throw new IllegalArgumentException("The property key can't be null.");
			}
			this.listener = listener;
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

		/**
		 * 构造一个触发方法调用的配置管理器.
		 */
		PropertyManager(String key, Class theClass, Method theMethod, DefaultPropertyListener listener)
		{
			this(key, false, theClass, theMethod, listener);
		}

		/**
		 * 构造一个触发属性值修改的配置管理器.
		 */
		PropertyManager(String key, Class theClass, Field theField, DefaultPropertyListener listener)
		{
			this(key, true, theClass, theField, listener);
			if (Modifier.isFinal(theField.getModifiers()))
			{
				throw new IllegalArgumentException("The field can't be final.");
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
				m = c.getDeclaredMethod(this.optMemberName, Utility.STR_PARAM);
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
				if (theField.getType() != String.class)
				{
					try
					{
						objValue = ConverterFinder.findConverter(theField.getType(), false).convert(value);
					}
					catch (Throwable ex)
					{
						String typeName = ClassGenerator.getClassName(theField.getType());
						Utility.createLog("util").warn("Type convert error for:[" + typeName + "].", ex);
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
		private void expunge()
		{
			BaseClassRef bcr = (BaseClassRef) this.queue.poll();
			while (bcr != null)
			{
				PropertyManager pm = bcr.getPropertyManager();
				this.listener.removePropertyManager(pm.key, pm);
				bcr = (BaseClassRef) this.queue.poll();
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
				if (!Utility.objectEquals(this.baseClass.get(), pm.baseClass.get()))
				{
					return false;
				}
				if (this.fieldMember != pm.fieldMember)
				{
					return false;
				}
				if (!Utility.objectEquals(this.optMemberName, pm.optMemberName))
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
			temp.append(member == null ? "<released>" : member.getName()).append(')').append(']');
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
