
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
 * 动态编译java代码来构造一个执行器.
 *
 * 需设置的属性
 * code                  执行的java代码                                                           2选1
 * attrCode              从factory的属性中获取执行的java代码                                      2选1
 *
 * imports               需要引入的包, 如：java.lang, 只需给出包路径, 以","分隔                   可选
 * extends               继承的类                                                                 可选
 * codeParam             预编译执行代码的参数, 格式为: key1=value1;key2=value2                    可选
 * throwCompileError     是否需要将编译的错误抛出, 抛出错误会打断初始化的执行                     默认为false
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
