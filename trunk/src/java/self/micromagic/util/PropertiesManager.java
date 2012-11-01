
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
 * �������ԵĹ�����.
 *
 * @author micromagic@sina.com
 */
public class PropertiesManager
{
   /**
    * Ĭ�ϵ������ļ���.
	 * ע: �����ļ���������classpath��.
    */
   public static final String PROPERTIES_NAME = "micromagic_config.properties";

   /**
    * ��Ÿ������ļ���������.
    */
   public static final String PARENT_PROPERTIES = "self.micromagic.parent.properties";


	/**
	 * �����ļ���.
	 * ע: �����ļ���������classpath��.
	 */
	private String propName;

	/**
	 * ��ȡ�����ļ���ʹ�õ�<code>ClassLoader</code>.
	 */
	private ClassLoader classLoader;

	/**
	 * ��ǰ����ȡ����������.
	 */
	private Properties properties = new Properties();

	/**
	 * ���Ա仯�������б�.
	 */
   private List plList = new LinkedList();

	/**
	 * �������ԵĹ�������, Ĭ�ϵ����Ա仯������.
	 */
   private DefaultPropertyListener defaultPL = new DefaultPropertyListener();

	/**
	 * Ĭ�ϵĹ��캯��.
	 * Ĭ�ϵ������ļ����������<code>ClassLoader</code>.
	 */
	public PropertiesManager()
	{
		this(null, null);
	}

	/**
	 * ����һ���������ԵĹ�����.
	 *
	 * @param propName      �����ļ���, ������classpath��, ���������classpath·��
	 *                      ��: com/xxx.properties
	 * @param classLoader   ��ȡ�����ļ���ʹ�õ�<code>ClassLoader</code>
	 */
	public PropertiesManager(String propName, ClassLoader classLoader)
	{
		this.propName = propName == null ? PROPERTIES_NAME : propName;
		this.classLoader = classLoader == null ? this.getClass().getClassLoader() : classLoader;
		this.addPropertyListener(this.defaultPL);
		this.reload();
	}

   /**
    * (����)��������.
    */
   public void reload()
   {
		this.reload(null);
	}

   /**
    * (����)��������.
    *
    * @param msg   ����, ��������ʱ�ĳ�����Ϣ
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
         // ���ñ�ɾ��������
         Enumeration e = this.properties.propertyNames();
         while (e.hasMoreElements())
         {
            String name = (String) e.nextElement();
            if (temp.getProperty(name) == null)
            {
               this.setProperty(name, null);
            }
         }
         // ���������Ѿ����������� ���Բ��ö�properties��ֵ;
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
	 * ��ȡ����ֵ.
	 *
	 * @param key  �������ڵļ�ֵ
	 */
   public String getProperty(String key)
   {
      return this.properties.getProperty(key);
   }

	/**
	 * ��ȡ����ֵ.
	 *
	 * @param key          �������ڵļ�ֵ
	 * @param defaultValue �˼�ֵ��û����ʱ�����ش�Ĭ��ֵ
	 */
   public String getProperty(String key, String defaultValue)
   {
      return this.properties.getProperty(key, defaultValue);
   }

	/**
	 * ��������ֵ.
	 *
	 * @param key    �������ڵļ�ֵ
	 * @param value  ��Ҫ���õ�ֵ
	 */
   public void setProperty(String key, String value)
   {
      String oldValue = this.properties.getProperty(key);
      // �ж��µ�ֵ��ԭֵ�Ƿ����
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
	 * �Ƴ�����ֵ.
	 *
	 * @param key    �������ڵļ�ֵ
	 */
   public void removeProperty(String key)
   {
		this.properties.setProperty(key, null);
	}

   /**
    * ���ü����������, �жϲ������Ƿ�Ҫ�������е�ֵ���õ�Ŀ����.
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
            // �������Ҫ���õ�ֵ, ��δ���ù�ֵ, ��Ҫ��ֵ���õ�����ص�������
            pm.changeProperty(temp);
         }
      }
      catch (Throwable ex) {}
   }

	/**
	 * ���һ�����ü����, �����õ�ֵ�ı�ʱ, �����Զ�����ָ����ľ�̬���Գ�Ա, ������
	 * �����Ϳ�����: <code>String</code>, <code>int</code>��<code>boolean</code>.
	 *
	 * @param key            ���õļ�ֵ
	 * @param theClass       ���޸ĵ��������ڵ���
	 * @param fieldName      ��Ҫ���޸ĵľ�̬��������
	 */
	public void addFieldPropertyManager(String key, Class theClass, String fieldName)
			throws NoSuchFieldException
	{
		this.addFieldPropertyManager(key, theClass, fieldName, null);
	}

	/**
	 * ���һ�����ü����, �����õ�ֵ�ı�ʱ, �����Զ�����ָ����ľ�̬���Գ�Ա, ������
	 * �����Ϳ�����: <code>String</code>, <code>int</code>��<code>boolean</code>.
	 *
	 * @param key            ���õļ�ֵ
	 * @param theClass       ���޸ĵ��������ڵ���
	 * @param fieldName      ��Ҫ���޸ĵľ�̬��������
	 * @param defaultValue   �������в�����ָ���ļ�ֵʱʹ�õ�Ĭ��ֵ
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
	 * �Ƴ�һ�����ü����.
	 *
	 * @param key            ���õļ�ֵ
	 * @param theClass       ���޸ĵ��������ڵ���
	 * @param fieldName      ��Ҫ���޸ĵľ�̬��������
	 */
	public void removeFieldPropertyManager(String key, Class theClass, String fieldName)
			throws NoSuchFieldException
	{
		PropertyManager pm = new PropertyManager(key, theClass,
				theClass.getDeclaredField(fieldName), this.defaultPL);
		this.defaultPL.removePropertyManager(key, pm);
	}

	/**
	 * ���һ�����ü����, �����õ�ֵ�ı�ʱ, �����Զ�����ָ����ľ�̬����,
	 * �˷���������ֻ��һ��<code>String</code>���͵Ĳ���.
	 *
	 * @param key            ���õļ�ֵ
	 * @param theClass       �����õķ������ڵ���
	 * @param methodName     ��Ҫ�����õľ�̬��������
	 */
	public void addMethodPropertyManager(String key, Class theClass, String methodName)
			throws NoSuchMethodException
	{
		this.addMethodPropertyManager(key, theClass, methodName, null);
	}

	/**
	 * ���һ�����ü����, �����õ�ֵ�ı�ʱ, �����Զ�����ָ����ľ�̬����,
	 * �˷���������ֻ��һ��<code>String</code>���͵Ĳ���.
	 *
	 * @param key            ���õļ�ֵ
	 * @param theClass       �����õķ������ڵ���
	 * @param methodName     ��Ҫ�����õľ�̬��������
	 * @param defaultValue   �������в�����ָ���ļ�ֵʱʹ�õ�Ĭ��ֵ
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
	 * �Ƴ�һ�����ü����.
	 *
	 * @param key            ���õļ�ֵ
	 * @param theClass       �����õķ������ڵ���
	 * @param methodName     ��Ҫ�����õľ�̬��������
	 */
	public void removeMethodPropertyManager(String key, Class theClass, String methodName)
			throws NoSuchMethodException
	{
		PropertyManager pm = new PropertyManager(key, theClass,
				theClass.getDeclaredMethod(methodName, Utility.STR_PARAM), this.defaultPL);
		this.defaultPL.removePropertyManager(key, pm);
	}

   /**
    * ���һ�����ñ仯�ļ�����.
    */
   public synchronized void addPropertyListener(PropertyListener l)
   {
      if (!this.plList.contains(l))
      {
         this.plList.add(l);
      }
   }

   /**
    * �Ƴ�һ�����ñ仯�ļ�����.
    */
   public synchronized void removePropertyListener(PropertyListener l)
   {
      this.plList.remove(l);
   }

   /**
    * ���븸����
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
	 * ���ñ仯�ļ�����.
	 */
	public interface PropertyListener extends EventListener
	{
		/**
		 * ��ĳ������ֵ�����˸ı�ʱ, ����ô˷���.
		 *
		 * @param key       �����ı�����õļ�ֵ
		 * @param oldValue  �ı�ǰ���õ�ԭʼֵ
		 * @param newValue  �ı�����õ�ֵ
		 */
		public void propertyChanged(String key, String oldValue, String newValue);

	}

	/**
	 * Ĭ�ϵ����ñ仯������.
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
			// �ж��µ�ֵ��ԭֵ�Ƿ����
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
	 * �������ԵĹ�����, ��Ĭ�ϵ����ñ仯������ʹ��.
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
		 * �������weak��ʽ�����ö���.
		 */
		private final ReferenceQueue queue = new ReferenceQueue();

		/**
		 * ��Ӧ���Եļ�ֵ.
		 */
		private String key;

		/**
		 * ����ʹ��<code>WeakReference</code>�����ö�Ӧ����, �������ͷ�ʱɾ�������Թ�����.
		 */
		private WeakReference baseClass;

		/**
		 * ����ʹ��<code>WeakReference</code>�����ö�Ӧ�ĳ�Ա, ��������Ӱ����������ͷ�.
		 */
		private WeakReference optMember;

		/**
		 * Ҫ�����ĳ�Ա����.
		 */
		private String optMemberName;

		/**
		 * ��ʶ�Ƿ������Գ�Ա, <code>true</code>��ʾ���Գ�Ա, <code>false</code>��ʾ������Ա.
		 */
		private boolean fieldMember;

		/**
		 * �����ù��������ڵ�listener.
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
		 * ����һ�������������õ����ù�����.
		 */
		PropertyManager(String key, Class theClass, Method theMethod, DefaultPropertyListener listener)
		{
			this(key, false, theClass, theMethod, listener);
		}

		/**
		 * ����һ����������ֵ�޸ĵ����ù�����.
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
			// ��������ĳ�ԱΪnull, ��ִ�б��.
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
		 * ������ڵ����Թ�����.
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
