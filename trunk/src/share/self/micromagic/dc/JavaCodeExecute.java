
package self.micromagic.dc;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.model.ModelAdapter;
import self.micromagic.eterna.model.ModelExport;
import self.micromagic.dc.CodeClassTool;
import self.micromagic.util.StringTool;
import self.micromagic.app.BaseExecute;

/**
 * ��̬����java����������һ��ִ����.
 *
 * �����õ�����
 * code                  ִ�е�java����                                                           2ѡ1
 * attrCode              ��factory�������л�ȡִ�е�java����                                      2ѡ1
 *
 * imports               ��Ҫ����İ�, �磺java.lang, ֻ�������·��, ��","�ָ�                   ��ѡ
 * extends               �̳е���                                                                 ��ѡ
 * codeParam             Ԥ����ִ�д���Ĳ���, ��ʽΪ: key1=value1;key2=value2                    ��ѡ
 * throwCompileError     �Ƿ���Ҫ������Ĵ����׳�, �׳�������ϳ�ʼ����ִ��                     Ĭ��Ϊfalse
 */
public class JavaCodeExecute extends BaseExecute
{
   private ExecuteCode executeCode;

   protected void plusInit()
         throws ConfigurationException
   {
      this.executeType = "javaCode";
      String code = CodeClassTool.getCode(this, this.factory, "code", "attrCode", "codeParam");
      try
      {
         String extendsStr = (String) this.getAttribute("extends");
         Class extendsClass = BaseExecute.class;
         if (extendsStr != null)
         {
            extendsClass = Class.forName(extendsStr);
         }
         Class codeClass = this.createCodeClass(extendsClass, code);
         this.executeCode = (ExecuteCode) codeClass.newInstance();
         this.executeCode.initialize(this.getModelAdapter());
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
            log.error("Error in compile java code in model ["
                  + this.getModelAdapter().getName() + "].", ex);
         }
      }
   }

   private Class createCodeClass(Class baseClass, String code)
         throws Exception
   {
      String methodHead = "public Object invoke(AppData data, Connection conn)\n      throws Exception";
      String[] iArr = null;
      String imports = (String) this.getAttribute("imports");
      if (imports != null)
      {
         iArr = StringTool.separateString(imports, ",", true);
      }
      return CodeClassTool.createJavaCodeClass(baseClass, ExecuteCode.class, methodHead, code, iArr);
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
      public void initialize(ModelAdapter model) throws ConfigurationException;

      public Object invoke(AppData data, Connection conn) throws Exception;

   }

}
