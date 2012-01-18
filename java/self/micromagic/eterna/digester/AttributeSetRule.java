
package self.micromagic.eterna.digester;

import org.apache.commons.beanutils.MethodUtils;
import org.xml.sax.Attributes;
import self.micromagic.util.StringTool;

public class AttributeSetRule extends MyRule
{
   public static final String USE_BODY_TEXT = "$useBodyText";
   public static final String USE_BODY_TEXT_NOTRIMLINE = "$useBodyText.NO_trimLine";

   private String method;
   private String attributeNameTag;
   private String attributeValueTag;
   private Class valueType;

   private String name = null;
   private String value = null;
   private String bodyValue = null;
   private Object object = null;
   private boolean trimLine = true;

   public AttributeSetRule()
   {
      this.method = "setAttribute";
      this.attributeNameTag = "name";
      this.attributeValueTag = "value";
      this.valueType = Object.class;
   }

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
      if (USE_BODY_TEXT.equals(this.value))
      {
         this.trimLine = true;
         this.useBodyText = true;
      }
      else if (USE_BODY_TEXT_NOTRIMLINE.equals(this.value))
      {
         this.trimLine = false;
         this.useBodyText = true;
      }
   }

   public void myBody(String namespace, String name, BodyText text)
         throws Exception
   {
      this.bodyValue = this.trimLine ? text.trimEveryLineSpace(false) : text.toString();
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
         MethodUtils.invokeExactMethod(this.object, this.method,
               new Object[]{this.name, theValue}, new Class[]{String.class, this.valueType});
      }
      catch (Exception ex)
      {
         FactoryManager.log.error("Method invoke error. method:" + this.method + "  param:(Stirng, "
               +  this.valueType.getName() + ")  obj:" + this.object.getClass()
               + "  valuetype:(" + this.name + ", " + this.value + ")");
         throw ex;
      }
   }

}
