
package self.micromagic.eterna.digester;

import org.xml.sax.Attributes;
import self.micromagic.cg.ClassGenerator;
import self.micromagic.eterna.share.Tool;
import self.micromagic.util.StringTool;

/**
 * 设置对象的属性值{key, value}对.
 */
public class AttributeSetRule extends MyRule
{
   /**
    * 如果value的属性为<code>$useBodyText</code>, 则需要使用body中的文本作为value值.
    */
   public static final String USE_BODY_TEXT = "$useBodyText";

   /**
    * 如果value的属性为<code>$useBodyText.NO_trimLine</code>, 则需要使用body中的文本
    * 作为value值, 并且不对每一行进行去除两边空格的处理.
    */
   public static final String USE_BODY_TEXT_NOTRIMLINE = "$useBodyText.NO_trimLine";

   /**
    * 如果value的属性为<code>$useBodyText.NO_line</code>, 则需要使用body中的文本
    * 作为value值, 并且将所有的换行替换成空格.
    */
   public static final String USE_BODY_TEXT_NOLINE = "$useBodyText.NO_line";

   private String method;
   private String attributeNameTag;
   private String attributeValueTag;
   private Class valueType;

   private String name = null;
   private String value = null;
   private String bodyValue = null;
   private Object object = null;
   private boolean trimLine = true;
   private boolean noLine = false;

   /**
    * 默认的构造函数. <p>
    * 默认调用的方法名为setAttribute
    * 默认读取的属性名为name和value
    */
   public AttributeSetRule()
   {
      this.method = "setAttribute";
      this.attributeNameTag = "name";
      this.attributeValueTag = "value";
      this.valueType = Object.class;
   }

   /**
    * 定制调用的方法和读取的属性名.
    *
    * @param method              调用的方法名
    * @param attributeNameTag    读取设置名称的属性名
    * @param attributeValueTag   读取设置值的属性名
    * @param valueType           设置的值的类型
    */
   public AttributeSetRule(String method, String attributeNameTag, String attributeValueTag,
         Class valueType)
   {
      this.method = method;
      this.attributeNameTag = attributeNameTag;
      this.attributeValueTag = attributeValueTag;
      this.valueType = valueType;
   }

   public void myBegin(String namespace, String name, Attributes attributes)
         throws Exception
   {
      String theName = attributes.getValue(this.attributeNameTag);
      if (theName == null)
      {
         throw new InvalidAttributesException("Not fount the attribute '" + this.attributeNameTag + "'.");
      }
      String value = attributes.getValue(this.attributeValueTag);
      if (value == null)
      {
         throw new InvalidAttributesException("Not fount the attribute '" + this.attributeValueTag + "'.");
      }
      this.name = StringTool.intern(theName);
      this.value = value;
      this.bodyValue = null;
      this.object = this.digester.peek();
      this.useBodyText = false;
      this.trimLine = true;
      this.noLine = false;
      if (USE_BODY_TEXT.equals(this.value))
      {
         this.useBodyText = true;
      }
      else if (USE_BODY_TEXT_NOTRIMLINE.equals(this.value))
      {
         this.trimLine = false;
         this.useBodyText = true;
      }
      else if (USE_BODY_TEXT_NOLINE.equals(this.value))
      {
         this.noLine = true;
         this.useBodyText = true;
      }
   }

   public void myBody(String namespace, String name, BodyText text)
         throws Exception
   {
      this.bodyValue = this.trimLine ? text.trimEveryLineSpace(this.noLine) : text.toString();
   }

   public void myEnd(String namespace, String name) throws Exception
   {
      if (this.bodyValue != null)
      {
         this.value = this.bodyValue;
      }
      try
      {
         String theValue = StringTool.intern(this.value, true);
         Tool.invokeExactMethod(this.object, this.method,
               new Object[]{this.name, theValue}, new Class[]{String.class, this.valueType});
      }
      catch (Exception ex)
      {
         FactoryManager.log.error("Method invoke error. method:" + this.method + "  param:(Stirng, "
               +  ClassGenerator.getClassName(this.valueType) + ")  obj:" + this.object.getClass()
               + "  value:(" + this.name + ", " + this.value + ")");
         throw ex;
      }
   }

}
