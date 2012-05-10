
package self.micromagic.eterna.digester;

import java.util.Arrays;

import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.xml.sax.Attributes;
import self.micromagic.eterna.share.Generator;
import self.micromagic.util.Utility;
import self.micromagic.util.StringTool;

/**
 * �������Ե�����, ͨ��PropertySetRule������.
 */
public abstract class PropertySetter
{
   protected static final Log log = FactoryManager.log;

   protected Digester digester;

   protected String methodName;
   protected int objectIndex = 0;

   /**
    * @param methodName     �������Ե��õķ���
    */
   public PropertySetter(String methodName)
   {
      this.methodName = methodName;
   }

   /**
    * @param methodName     �������Ե��õķ���
    * @param objectIndex    ���������Զ����ڶ�ջ�е�����ֵ
    */
   public PropertySetter(String methodName, int objectIndex)
   {
      this.methodName = methodName;
      this.objectIndex = objectIndex;
   }

   /**
    * ���������Ľ�����.
    */
   public void setDigester(Digester digester)
   {
      this.digester = digester;
   }

   /**
    * �������Զ����ڶ�ջ�е�����ֵ.
    */
   public void setObjectIndex(int index)
   {
      this.objectIndex = index;
   }

   /**
    * ���õ������Ƿ�������.
    */
   public abstract boolean isMustExist();

   /**
    * ׼�����õ�����.
    *
    * @param namespace      xml�����ļ��������ռ�
    * @param name           ��ǰxmlԪ�ص�����
    * @param attributes     ��ǰxmlԪ�ص����Լ�
    * @return               ׼���õ����Զ���
    */
   public abstract Object prepareProperty(String namespace, String name, Attributes attributes)
         throws Exception;

   /**
    * �����ı�����׼�����õ�����.
    *
    * @param namespace      xml�����ļ��������ռ�
    * @param name           ��ǰxmlԪ�ص�����
    * @param text           ��ǰxmlԪ�ص��ı�����
    * @return               ׼���õ����Զ���
    */
   public Object prepareProperty(String namespace, String name, BodyText text)
         throws Exception
   {
      return null;
   }

   /**
    * �Ƿ���ҪxmlԪ�ص��ı�����.
    */
   public boolean requireBodyValue()
   {
      return false;
   }

   /**
    * ��׼���õ��������õ�������.
    */
   public abstract void setProperty()
         throws Exception;

   /**
    * ׼�����Բ��������õ�������. <p>
    * ����õĴ�������:
    * <blockquote><pre>
    * this.prepareProperty(namespace, name, attributes);
    * this.setProperty();
    * </pre></blockquote>
    *
    * @param namespace      xml�����ļ��������ռ�
    * @param name           ��ǰxmlԪ�ص�����
    * @param attributes     ��ǰxmlԪ�ص����Լ�
    */
   public void setProperty(String namespace, String name, Attributes attributes)
         throws Exception
   {
      this.prepareProperty(namespace, name, attributes);
      this.setProperty();
   }

}

/**
 * ���������õĹ�����.
 */
abstract class SinglePropertySetter extends PropertySetter
{
   public final static Class[] STRING_TYPE = new Class[]{String.class};
   public final static Class[] INTEGER_TYPE = new Class[]{int.class};
   public final static Class[] BOOLEAN_TYPE = new Class[]{boolean.class};

   protected String attributeName;
   protected String defaultValue;
   protected boolean mustExist;

   protected Object[] value;
   protected Class[] type;

   /**
    * �Ƿ�Ҫ�Ի�ȡ���ַ�������intern����. <p>
    * ��һЩƵ�����ֵ��ַ���intern��������Խ�ʡ�ڴ�ʹ�õĿռ䡣
    */
   boolean needIntern = true;

   /**
    * @param attributeName   ��xml���Լ��л�ȡֵ������
    * @param methodName      �������Ե��õķ���
    * @param mustExist       xml���Լ����Ƿ���������Ҫ��ֵ
    */
   public SinglePropertySetter(String attributeName, String methodName, boolean mustExist)
   {
      super(methodName);
      this.attributeName = attributeName;
      this.mustExist = mustExist;
   }

   /**
    * @param attributeName   ��xml���Լ��л�ȡֵ������
    * @param methodName      �������Ե��õķ���
    * @param defaultValue    xml���Լ��в���������Ҫ��ֵ��ʹ�õ�Ĭ��ֵ
    */
   public SinglePropertySetter(String attributeName, String methodName, String defaultValue)
   {
      super(methodName);
      this.attributeName = attributeName;
      this.defaultValue = defaultValue;
      this.mustExist = false;
   }

   /**
    * �Ƿ�Ҫ�Ի�ȡ���ַ�������intern����.
    */
   public boolean isNeedIntern()
   {
      return this.needIntern;
   }

   /**
    * �����Ƿ�Ҫ�Ի�ȡ���ַ�������intern����.
    */
   public void setNeedIntern(boolean needIntern)
   {
      this.needIntern = needIntern;
   }

   /**
    * ���õ������Ƿ�������.
    */
   public boolean isMustExist()
   {
      return this.mustExist;
   }

   /**
    * ��xml���Լ��л�ȡֵ.
    *
    * @param namespace      xml�����ļ��������ռ�
    * @param name           ��ǰxmlԪ�ص�����
    * @param attributes     ��ǰxmlԪ�ص����Լ�
    */
   protected String getValue(String namespace, String name, Attributes attributes)
         throws InvalidAttributesException
   {
      if (this.attributeName == null)
      {
         return this.mustExist ? null : this.defaultValue;
      }
      String value = attributes.getValue(this.attributeName);
      if (value == null)
      {
         if (this.isMustExist())
         {
            throw new InvalidAttributesException("Not fount the attribute '"
                  + this.attributeName + "' in " + name + ".");
         }
         return this.needIntern ? StringTool.intern(this.defaultValue) : this.defaultValue;
      }
      return this.needIntern ? StringTool.intern(value) : value;
   }

   /**
    * ��׼���õ��������õ�������.
    */
   public void setProperty()
         throws Exception
   {
      if (this.value == null)
      {
         return;
      }
      Object obj = this.digester.peek(this.objectIndex);
      try
      {
         MethodUtils.invokeExactMethod(obj, this.methodName, this.value, this.type);
      }
      catch (Exception ex)
      {
         log.error("Method invoke error. method:" + this.methodName + "  param:"
               + Arrays.asList(this.type) + "  obj:" + (obj == null ? null : obj.getClass())
               + "  value:" + Arrays.asList(this.value));
         throw ex;
      }
   }

}

/**
 * �������õ������������, ��������������.
 */
class ObjectPropertySetter extends SinglePropertySetter
{
   protected Class classType;
   protected Object theObject;
   protected boolean objectClassMustExist = true;

   public ObjectPropertySetter(String attributeName, String methodName,
         String className, Class classType)
   {
      super(attributeName, methodName, className);
      this.objectClassMustExist = className == null;
      this.classType = classType;
      this.type = new Class[]{this.classType};
   }

   /**
    * ��xml���Լ���(��Ĭ��ֵ)��ȡ���������ɶ���.
    */
   protected void setMyObject(String namespace, String name, Attributes attributes)
         throws Exception
   {
      String cName = ObjectCreateRule.getClassName(
            this.attributeName, this.defaultValue, attributes);
      if (cName == null)
      {
         throw new InvalidAttributesException("Not fount the attribute '"
               + this.attributeName + "' in " + name + ".");
      }

      this.digester.getLogger().debug("New " + cName);
      this.theObject = ObjectCreateRule.createObject(cName, this.objectClassMustExist);
   }

   public Object prepareProperty(String namespace, String name, Attributes attributes)
         throws Exception
   {
      this.setMyObject(namespace, name, attributes);
      ObjectCreateRule.checkType(this.classType, this.theObject);
      return this.theObject;
   }

   /**
    * ����value����, ��setProperty�����л���ô˷����������õ�ֵ.
    */
   protected void setMyValue()
         throws Exception
   {
      this.value = new Object[]{this.theObject};
   }

   public void setProperty()
         throws Exception
   {
      if (this.theObject == null)
      {
         return;
      }
      Object obj = this.digester.peek(this.objectIndex);
      this.setMyValue();
      try
      {
         MethodUtils.invokeExactMethod(obj, this.methodName, this.value, this.type);
      }
      catch (Exception ex)
      {
         log.error("Method invoke error. method:" + this.methodName + "  param:"
               + Arrays.asList(this.type) + "  obj:" + obj.getClass()
               + "  value:" + Arrays.asList(this.value));
         throw ex;
      }
   }
}

/**
 * ����Generator���ɵ�����.
 */
class GeneratorPropertySetter extends ObjectPropertySetter
{
   protected Generator generator;
   protected boolean withName;

   public GeneratorPropertySetter(String attributeName, String methodName,
         String className, Class classType)
   {
      this(attributeName, methodName, className, classType, false);
   }

   public GeneratorPropertySetter(String attributeName, String methodName,
         String className, Class classType, boolean withName)
   {
      super(attributeName, methodName, className, classType);
      if (withName)
      {
         // withName Ϊfalse�����, �ڸ��������ù���.
         this.type = new Class[]{String.class, this.classType};
      }
      this.withName = withName;
   }

   public Object prepareProperty(String namespace, String name, Attributes attributes)
         throws Exception
   {
      this.setMyObject(namespace, name, attributes);
      ObjectCreateRule.checkType(Generator.class, this.theObject);
      this.generator = (Generator) this.theObject;
      return this.generator;
   }

   protected void setMyValue()
         throws Exception
   {
      this.generator.setFactory(FactoryManager.getCurrentFactory());
      if (this.withName)
      {
         this.value = new Object[]{this.generator.getName(), this.generator.create()};
      }
      else
      {
         this.value = new Object[]{this.generator.create()};
      }
   }

}

/**
 * ���ôӶ�ջ�л�ȡ������.
 */
class StackPropertySetter extends SinglePropertySetter
{
   protected Class classType;
   protected int stackObjectIndex = 0;

   public StackPropertySetter(String methodName, Class classType, int objectIndex)
   {
      super(null, methodName, true);
      this.objectIndex = objectIndex;
      this.classType = classType;
      this.type = new Class[]{this.classType};
   }

   public StackPropertySetter(String methodName, Class classType,
         int objectIndex, int stackIndex)
   {
      super(null, methodName, true);
      this.objectIndex = objectIndex;
      this.stackObjectIndex = stackIndex;
      this.classType = classType;
      this.type = new Class[]{this.classType};
   }

   public Object prepareProperty(String namespace, String name, Attributes attributes)
         throws Exception
   {
      Object obj = this.digester.peek(this.stackObjectIndex);
      if (obj instanceof Generator)
      {
         ((Generator) obj).setFactory(FactoryManager.getCurrentFactory());
      }
      this.value = new Object[]{obj};
      return obj;
   }

}

/**
 * ����String���͵�����.
 */
class StringPropertySetter extends SinglePropertySetter
{
   public StringPropertySetter(String attributeName, String methodName, boolean mustExist)
   {
      super(attributeName, methodName, mustExist);
      this.type = STRING_TYPE;
   }

   public StringPropertySetter(String attributeName, String methodName, boolean mustExist,
         boolean needIntern)
   {
      super(attributeName, methodName, mustExist);
      this.type = STRING_TYPE;
      this.needIntern = needIntern;
   }

   public StringPropertySetter(String attributeName, String methodName, String defaultValue)
   {
      super(attributeName, methodName, defaultValue);
      this.type = STRING_TYPE;
   }

   public Object prepareProperty(String namespace, String name, Attributes attributes)
         throws Exception
   {
      String strValue = this.getValue(namespace, name, attributes);
      if (strValue == null)
      {
         this.value = null;
         return null;
      }

      this.value = new Object[]{strValue};
      return strValue;
   }

}

/**
 * ���ô�body�л�ȡ��String���͵�����.
 */
class BodyPropertySetter extends StringPropertySetter
{
   private boolean trimLines = true;
   private boolean noLine = false;
   private String noLineAttributeName = "noLine";
   private boolean[] bodyTextSetting = new boolean[2];

   public BodyPropertySetter(String attributeName, String methodName)
   {
      super(attributeName, methodName, false);
      this.needIntern = false;
   }

   public BodyPropertySetter(String attributeName, String methodName, boolean trimLines)
   {
      super(attributeName, methodName, false);
      this.trimLines = trimLines;
      this.needIntern = false;
   }

   public BodyPropertySetter(String attributeName, String methodName, boolean trimLines,
         boolean needIntern)
   {
      this(attributeName, methodName, trimLines);
      this.needIntern = needIntern;
   }

   public void setNoLine(String attributeName, boolean noLine)
   {
      this.noLineAttributeName = attributeName;
      this.noLine = noLine;
   }

   public Object prepareProperty(String namespace, String name, Attributes attributes)
         throws Exception
   {
      // ��bodyText�Ļ�ȡ���� ��Ϊ��ʼֵ
      this.bodyTextSetting[0] = this.trimLines;
      this.bodyTextSetting[1] = this.noLine;
      String strValue = this.getValue(namespace, name, attributes);
      if (strValue != null)
      {
         this.bodyTextSetting[0] = "true".equalsIgnoreCase(strValue);
      }
      strValue = attributes.getValue(this.noLineAttributeName);
      if (strValue != null)
      {
         this.bodyTextSetting[1] = "true".equalsIgnoreCase(strValue);
      }
      return this.bodyTextSetting;
   }

   public boolean requireBodyValue()
   {
      return true;
   }

   public Object prepareProperty(String namespace, String name, BodyText text)
         throws Exception
   {
      String bodyStr = this.bodyTextSetting[0] ?
            text.trimEveryLineSpace(this.bodyTextSetting[1]) : text.toString();
      if (this.needIntern)
      {
         bodyStr = StringTool.intern(bodyStr, true);
      }
      this.value = new Object[]{bodyStr};
      return this.value;
   }

}

/**
 * ����boolean���͵�����.
 */
class BooleanPropertySetter extends SinglePropertySetter
{
   public BooleanPropertySetter(String attributeName, String methodName, boolean mustExist)
   {
      super(attributeName, methodName, mustExist);
      this.type = BOOLEAN_TYPE;
      this.needIntern = false;
   }

   public BooleanPropertySetter(String attributeName, String methodName, String defaultValue)
   {
      super(attributeName, methodName, defaultValue);
      this.type = BOOLEAN_TYPE;
      this.needIntern = false;
   }

   public Object prepareProperty(String namespace, String name, Attributes attributes)
         throws Exception
   {
      String strValue = this.getValue(namespace, name, attributes);
      if (strValue == null)
      {
         this.value = null;
         return null;
      }

      this.value = new Object[]{"true".equalsIgnoreCase(strValue) ? Boolean.TRUE : Boolean.FALSE};
      return this.value[0];
   }

}

/**
 * ����int���͵�����.
 */
class IntegerPropertySetter extends SinglePropertySetter
{
   public IntegerPropertySetter(String attributeName, String methodName, boolean mustExist)
   {
      super(attributeName, methodName, mustExist);
      this.type = INTEGER_TYPE;
      this.needIntern = false;
   }

   public IntegerPropertySetter(String attributeName, String methodName, String defaultValue)
   {
      super(attributeName, methodName, defaultValue);
      this.type = INTEGER_TYPE;
      this.needIntern = false;
   }

   public Object prepareProperty(String namespace, String name, Attributes attributes)
         throws Exception
   {
      String strValue = this.getValue(namespace, name, attributes);
      if (strValue == null)
      {
         this.value = null;
         return null;
      }

      int intValue;
      try
      {
         if (strValue.length() > 2 && "0x".equalsIgnoreCase(strValue.substring(0, 2)))
         {
            // �����16���Ƶ���ʼ��ǣ������16����ת��
            intValue = Integer.parseInt(strValue.substring(2), 16);
         }
         else
         {
            intValue = Integer.parseInt(strValue);
         }
      }
      catch (NumberFormatException ex)
      {
         throw new ConfigurationException(ex.getMessage()
               + "(name:" + name + ", attribute:" + this.attributeName + ")");
      }
      this.value = new Object[]{Utility.createInteger(intValue)};
      return this.value[0];
   }

}

/**
 * �������κ�����, ֻ����һ���޲εķ���.
 */
class EmptyPropertySetter extends PropertySetter
{
   public final static Class[] EMPTY_TYPE = new Class[0];
   public final static Object[] EMPTY_VALUE = new Object[0];

   public EmptyPropertySetter(String methodName)
   {
      super(methodName);
   }

   public boolean isMustExist()
   {
      return false;
   }

   public Object prepareProperty(String namespace, String name, Attributes attributes)
         throws Exception
   {
      return null;
   }

   public void setProperty()
         throws Exception
   {
      Object obj = this.digester.peek(this.objectIndex);
      MethodUtils.invokeExactMethod(obj, this.methodName, EMPTY_VALUE, EMPTY_TYPE);
   }

}
