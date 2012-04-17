
package self.micromagic.dc;

import self.micromagic.eterna.search.ConditionBuilderGenerator;
import self.micromagic.eterna.search.ConditionBuilder;
import self.micromagic.eterna.search.ConditionProperty;
import self.micromagic.eterna.share.AbstractGenerator;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.dc.CodeClassTool;
import self.micromagic.util.StringTool;

/**
 * ��̬����java����������һ��ConditionBuilder.
 *
 * �����õ�����
 * code                  �����������ɵ�java����                                                   2ѡ1
 * attrCode              ��factory�������л�ȡ�����������ɵ�java����                              2ѡ1
 *
 * imports               ��Ҫ����İ�, �磺java.lang, ֻ�������·��, ��","�ָ�                   ��ѡ
 * extends               �̳е���                                                                 ��ѡ
 * codeParam             Ԥ���봦���������ɴ���Ĳ���, ��ʽΪ: key1=value1;key2=value2            ��ѡ
 * throwCompileError     �Ƿ���Ҫ������Ĵ����׳�, �׳�������ϳ�ʼ����ִ��                     Ĭ��Ϊfalse
 */
public class JavaCodeConditionBuilder extends AbstractGenerator
      implements ConditionBuilder, ConditionBuilderGenerator
{
   private String caption;
   private String operator;
   private ConditionBuilderCode conditionBuilderCode;

   public void initialize(EternaFactory factory)
         throws ConfigurationException
   {
      if (this.conditionBuilderCode != null)
      {
         return;
      }
      String code = CodeClassTool.getCode(this, factory, "code", "attrCode", "codeParam");
      try
      {
         Class codeClass = this.createCodeClass(code);
         this.conditionBuilderCode = (ConditionBuilderCode) codeClass.newInstance();
         this.conditionBuilderCode.setGenerator(this, factory);
      }
      catch (Exception ex)
      {
         if ("true".equalsIgnoreCase((String) this.getAttribute("throwCompileError")))
         {
            if (ex instanceof ConfigurationException)
            {
               throw (ConfigurationException) ex;
            }
            throw new ConfigurationException(ex);
         }
         else
         {
            log.error("Error in compile java code in condition builder ["
                  + this.getName() + "].", ex);
         }
      }
   }

   public ConditionBuilder.Condition buildeCondition(String colName, String value, ConditionProperty cp)
         throws ConfigurationException
   {
      try
      {
         if (this.conditionBuilderCode != null)
         {
            return this.conditionBuilderCode.invoke(colName, value, cp);
         }
      }
      catch (Exception ex)
      {
         if (ex instanceof ConfigurationException)
         {
            throw (ConfigurationException) ex;
         }
         throw new ConfigurationException(ex);
      }
      return null;
   }

   public String getCaption()
   {
      return this.caption;
   }

   public void setCaption(String caption)
   {
      this.caption = caption;
   }

   public String getOperator()
   {
      return this.operator;
   }

   public void setOperator(String operator)
   {
      this.operator = operator;
   }

   private Class createCodeClass(String code)
         throws Exception
   {
      String extendsStr = (String) this.getAttribute("extends");
      Class extendsClass = ConditionBuilderCodeImpl.class;
      if (extendsStr != null)
      {
         extendsClass = Class.forName(extendsStr);
      }
      String methodHead = "public ConditionBuilder$Condition invoke(String colName, String value, "
            + "ConditionProperty cp)\n      throws Exception";
      String[] iArr = null;
      String imports = (String) this.getAttribute("imports");
      if (imports != null)
      {
         iArr = StringTool.separateString(imports, ",", true);
      }
      return CodeClassTool.createJavaCodeClass(extendsClass, ConditionBuilderCode.class,
            methodHead, code, iArr);
   }

   public ConditionBuilder createConditionBuilder()
         throws ConfigurationException
   {
      return this;
   }

   public Object create()
         throws ConfigurationException
   {
      return this.createConditionBuilder();
   }

   public interface ConditionBuilderCode
   {
      public void setGenerator(JavaCodeConditionBuilder generator, EternaFactory factory)
            throws ConfigurationException;

      public ConditionBuilder.Condition invoke(String colName, String value, ConditionProperty cp)
            throws Exception;

   }

   public static abstract class ConditionBuilderCodeImpl
         implements ConditionBuilderCode
   {
      protected JavaCodeConditionBuilder generator;
      protected EternaFactory factory;

      public void setGenerator(JavaCodeConditionBuilder generator, EternaFactory factory)
      {
         this.factory = factory;
         this.generator = generator;
      }

   }

}
