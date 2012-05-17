
package self.micromagic.dc;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import self.micromagic.app.BaseExecute;
import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.model.ModelExport;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.util.StringTool;

/**
 * ��̬����java����������һ��ִ����.
 *
 * �����õ�����
 * code                  ִ�е�java����                                                           2ѡ1
 * attrCode              ��factory�������л�ȡִ�е�java����                                      2ѡ1
 *
 * imports               ��Ҫ����İ�, �磺java.lang, ֻ�������·��, ��","�ָ�                   ��ѡ
 * extends               �̳е���                                                                 ��ѡ
 * throwCompileError     �Ƿ���Ҫ������Ĵ����׳�, �׳�������ϳ�ʼ����ִ��                     Ĭ��Ϊfalse
 *
 * otherParams
 * Ԥ���봦���������ɴ���Ĳ���, ������Ҫ������еĲ�������ƥ��, ֵΪ��������Ӧ�Ĵ���
 */
public class JavaCodeExecute extends BaseExecute
{
   private ExecuteCode executeCode;

   protected void plusInit()
         throws ConfigurationException
   {
      this.executeType = "javaCode";
      String code = CodeClassTool.getCode(this, this.factory, "code", "attrCode");
      try
      {
         Class codeClass = this.createCodeClass(code);
         this.executeCode = (ExecuteCode) codeClass.newInstance();
         this.executeCode.setGenerator(this, this.factory);
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
            String pos = "model:[" + this.getModelAdapter().getName() + "], execute:["
                  + this.getName() + "]";
            CodeClassTool.logCodeError(code, pos, ex);
         }
      }
   }

   private Class createCodeClass(String code)
         throws Exception
   {
      String extendsStr = (String) this.getAttribute("extends");
      Class extendsClass = ExecuteCodeImpl.class;
      if (extendsStr != null)
      {
         extendsClass = Class.forName(extendsStr);
      }
      String methodHead = "public Object invoke(AppData data, Connection conn)\n      throws Exception";
      String[] iArr = null;
      String imports = (String) this.getAttribute("imports");
      if (imports != null)
      {
         iArr = StringTool.separateString(imports, ",", true);
      }
      return CodeClassTool.createJavaCodeClass(extendsClass, ExecuteCode.class, methodHead, code, iArr);
   }

   protected ModelExport dealProcess(AppData data, Connection conn)
         throws ConfigurationException, SQLException, IOException, InnerExport
   {
      if (this.executeCode == null)
      {
         return null;
      }
      try
      {
         Object obj = this.executeCode.invoke(data, conn);
         if (obj instanceof ModelExport)
         {
            return (ModelExport) obj;
         }
         return null;
      }
      catch (Exception ex)
      {
         if (ex instanceof ConfigurationException)
         {
            throw (ConfigurationException) ex;
         }
         if (ex instanceof SQLException)
         {
            throw (SQLException) ex;
         }
         if (ex instanceof IOException)
         {
            throw (IOException) ex;
         }
         if (ex instanceof InnerExport)
         {
            throw (InnerExport) ex;
         }
         throw new ConfigurationException(ex);
      }
   }

   public interface ExecuteCode
   {
      public void setGenerator(JavaCodeExecute generator, EternaFactory factory);

      public Object invoke(AppData data, Connection conn) throws Exception;

   }

   public static abstract class ExecuteCodeImpl extends BaseExecute
         implements ExecuteCode
   {
      protected JavaCodeExecute generator;

      public void setGenerator(JavaCodeExecute generator, EternaFactory factory)
      {
         this.factory = factory;
         this.generator = generator;
      }

   }

}
