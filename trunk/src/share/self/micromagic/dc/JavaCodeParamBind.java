
package self.micromagic.dc;

import java.sql.SQLException;

import self.micromagic.eterna.share.AbstractGenerator;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.model.ParamBindGenerator;
import self.micromagic.eterna.model.ParamBind;
import self.micromagic.eterna.model.ModelAdapter;
import self.micromagic.eterna.model.Execute;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.model.ParamSetManager;
import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.dc.CodeClassTool;
import self.micromagic.util.StringTool;

/**
 * ��̬����java����������һ��ParamBind.
 *
 * �����õ�����
 * src        ��ʽΪ attrName[:throwCompileError][:extends]
 *            attrName               ��factory�������л�ȡ��������󶨵�java����
 *            throwCompileError      �Ƿ���Ҫ������Ĵ����׳�, �׳�������ϳ�ʼ����ִ��, Ĭ��Ϊfalse
 *            extends                �̳е���
 */
public class JavaCodeParamBind extends AbstractGenerator
      implements ParamBindGenerator, ParamBind
{
   protected String src = "";
   protected String names = "";
   protected boolean loop = false;
   protected boolean subSQL = false;
   private ParamBindCode paramBindCode;

   public void initialize(ModelAdapter model, Execute execute)
         throws ConfigurationException
   {
      if (this.paramBindCode != null)
      {
         return;
      }
      String[] tmpArr = StringTool.separateString(this.src, ":", true);
      String attrCode = tmpArr[0];
      String extendsStr = null;
      boolean throwCompileError = false;
      if (tmpArr.length >= 2)
      {
         throwCompileError = "true".equalsIgnoreCase(tmpArr[1]);
      }
      if (tmpArr.length >= 3)
      {
         extendsStr = tmpArr[2];
      }
      String code = (String) factory.getAttribute(attrCode);
      if (code == null)
      {
         throw new ConfigurationException("Not found the [" + attrCode + "] in factory attribute.");
      }
      try
      {
         Class codeClass = this.createCodeClass(code, extendsStr);
         this.paramBindCode = (ParamBindCode) codeClass.newInstance();
         this.paramBindCode.setGenerator(this, model.getFactory());
      }
      catch (Exception ex)
      {
         if (throwCompileError)
         {
            if (ex instanceof ConfigurationException)
            {
               throw (ConfigurationException) ex;
            }
            throw new ConfigurationException(ex);
         }
         else
         {
            String pos = "model:[" + model.getName() + "], execute:[" + execute.getName()
                  + "] type:[" + execute.getExecuteType() + "], src:[" + this.src + "]";
            CodeClassTool.logCodeError(code, pos, ex);
         }
      }
   }

   public int setParam(AppData data, ParamSetManager psm, int loopIndex)
         throws ConfigurationException, SQLException
   {
      try
      {
         if (this.paramBindCode != null)
         {
            return this.paramBindCode.invoke(data, psm, loopIndex);
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
      return 1;
   }

   public String getSrc()
   {
      return this.src;
   }

   public void setSrc(String src)
   {
      this.src = src;
   }

   public String getNames()
   {
      return this.names;
   }

   public void setNames(String names)
   {
      this.names = names;
   }

   public boolean isLoop()
   {
      return this.loop;
   }

   public void setLoop(boolean loop)
   {
      this.loop = loop;
   }

   public boolean isSubSQL()
   {
      return this.subSQL;
   }

   public void setSubSQL(boolean subSQL)
   {
      this.subSQL = subSQL;
   }

   public ParamBind createParamBind()
         throws ConfigurationException
   {
      return this;
   }

   private Class createCodeClass(String code, String extendsStr)
         throws Exception
   {
      Class extendsClass = ParamBindCodeImpl.class;
      if (extendsStr != null)
      {
         extendsClass = Class.forName(extendsStr);
      }
      String methodHead = "public int invoke(AppData data, ParamSetManager psm, int loopIndex)"
            + "\n      throws Exception";
      String[] iArr = null;
      String imports = (String) this.getAttribute("imports");
      if (imports != null)
      {
         iArr = StringTool.separateString(imports, ",", true);
      }
      return CodeClassTool.createJavaCodeClass(extendsClass, ParamBindCode.class,
            methodHead, code, iArr);
   }

   public Object create()
         throws ConfigurationException
   {
      return this.createParamBind();
   }

   public interface ParamBindCode
   {
      public void setGenerator(JavaCodeParamBind generator, EternaFactory factory)
            throws ConfigurationException;

      public int invoke(AppData data, ParamSetManager psm, int loopIndex)
            throws Exception;

   }

   public static abstract class ParamBindCodeImpl
         implements ParamBindCode
   {
      protected JavaCodeParamBind generator;
      protected EternaFactory factory;

      public void setGenerator(JavaCodeParamBind generator, EternaFactory factory)
      {
         this.factory = factory;
         this.generator = generator;
      }

   }

}
