
package self.micromagic.eterna.digester;

import org.xml.sax.Attributes;
import self.micromagic.cg.ClassGenerator;
import self.micromagic.eterna.share.Tool;
import self.micromagic.util.StringTool;

/**
 * ���ö��������ֵ{key, value}��.
 */
public class AttributeSetRule extends MyRule
{
   /**
    * ���value������Ϊ<code>$useBodyText</code>, ����Ҫʹ��body�е��ı���Ϊvalueֵ.
    */
   public static final String USE_BODY_TEXT = "$useBodyText";

   /**
    * ���value������Ϊ<code>$useBodyText.NO_trimLine</code>, ����Ҫʹ��body�е��ı�
    * ��Ϊvalueֵ, ���Ҳ���ÿһ�н���ȥ�����߿ո�Ĵ���.
    */
   public static final String USE_BODY_TEXT_NOTRIMLINE = "$useBodyText.NO_trimLine";

   /**
    * ���value������Ϊ<code>$useBodyText.NO_line</code>, ����Ҫʹ��body�е��ı�
    * ��Ϊvalueֵ, ���ҽ����еĻ����滻�ɿո�.
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
    * Ĭ�ϵĹ��캯��. <p>
    * Ĭ�ϵ��õķ�����ΪsetAttribute
    * Ĭ�϶�ȡ��������Ϊname��value
    */
   public AttributeSetRule()
   {
      this.method = "setAttribute";
      this.attributeNameTag = "name";
      this.attributeValueTag = "value";
      this.valueType = Object.class;
   }

   /**
    * ���Ƶ��õķ����Ͷ�ȡ��������.
    *
    * @param method              ���õķ�����
    * @param attributeNameTag    ��ȡ�������Ƶ�������
    * @param attributeValueTag   ��ȡ����ֵ��������
    * @param valueType           ���õ�ֵ������
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
