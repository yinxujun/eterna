
package self.micromagic.app;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.DeflaterOutputStream;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtNewMethod;
import org.apache.commons.collections.ReferenceMap;
import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.model.ModelAdapter;
import self.micromagic.eterna.model.ModelExport;
import self.micromagic.util.StringTool;
import self.micromagic.util.Utility;

/**
 * 通过Javassist将代码预编译后执行.
 *
 * 需设置的属性
 * code        执行的java代码                                                          2选1
 * attrCode    从factory的属性中获取执行的java代码                                     2选1
 *
 * imports     需要引入的包, 如：self.micromagic.app, 只需给出包路径, 以","分隔        可选
 * extends     继承的类                                                                可选
 * codeParam   预编译执行代码的参数来源, 必需是Map类型的对象                           可选
 */
public class JavaCodeExecute extends BaseExecute
{
   /**
    * 预编译的java代码的id
    */
   private static int JAVA_CODE_ID = 1;
   private static Map defaultCodeCache = new HashMap();
   private static Map codeCache = new ReferenceMap(ReferenceMap.WEAK, ReferenceMap.HARD);

   private JavaCode javaCode;

   protected void plusInit()
         throws ConfigurationException
   {
      this.executeType = "javaCode";

      String code = (String) this.getAttribute("code");
      String attrCode = (String) this.getAttribute("attrCode");
      if (code == null && attrCode == null)
      {
         throw new ConfigurationException("Not found the code or attrCode attribute.");
      }
      if (code == null)
      {
         code = (String) this.factory.getAttribute(attrCode);
         if (code == null)
         {
            throw new ConfigurationException("Not found the [" + attrCode + "] in factory attribute.");
         }
      }
      String codeParam = (String) this.getAttribute("codeParam");
      if (codeParam != null)
      {
         Map paramMap = StringTool.string2Map(codeParam, ";", '=');
         code = Utility.resolveDynamicPropnames(code, paramMap);
      }
      else
      {
         code = Utility.resolveDynamicPropnames(code);
      }

      try
      {
         String extendsStr = (String) this.getAttribute("extends");
         Class extendsClass = BaseExecute.class;
         if (extendsStr != null)
         {
            extendsClass = Class.forName(extendsStr);
         }
         Class codeClass = this.createCodeClass(extendsClass, code);
         this.javaCode = (JavaCode) codeClass.newInstance();
         this.javaCode.initialize(this.getModelAdapter());
      }
      catch (Exception ex)
      {
         if (ex instanceof ConfigurationException)
         {
            throw (ConfigurationException) ex;
         }
         throw new ConfigurationException(ex);
      }
   }

   private Class createCodeClass(Class baseClass, String code)
         throws Exception
   {
      Map cache;
      if (baseClass == BaseExecute.class)
      {
         cache = defaultCodeCache;
      }
      else
      {
         cache = (Map) codeCache.get(baseClass);
         if (cache == null)
         {
            cache = new HashMap();
            codeCache.put(baseClass, cache);
         }
      }
      CodeKey key = new CodeKey(code);
      Class codeClass = (Class) cache.get(key);
      if (codeClass != null)
      {
         return codeClass;
      }

      ClassPool pool = ClassPool.getDefault();
      pool.appendClassPath(new ClassClassPath(JavaCodeExecute.class));
      if (baseClass != BaseExecute.class)
      {
         pool.appendClassPath(new ClassClassPath(baseClass));
      }
      String namePrefix = "eterna.javacode_" + (JAVA_CODE_ID++) + ".";
      CtClass cc = pool.makeClass(namePrefix + baseClass.getName());
      cc.addInterface(pool.get(JavaCode.class.getName()));
      cc.setSuperclass(pool.get(baseClass.getName()));
      pool.importPackage("self.micromagic.app");
      pool.importPackage("self.micromagic.util");
      pool.importPackage("self.micromagic.eterna.sql");
      pool.importPackage("self.micromagic.eterna.model");
      pool.importPackage("self.micromagic.eterna.search");
      pool.importPackage("java.util");
      pool.importPackage("java.sql");
      String imports = (String) this.getAttribute("imports");
      if (imports != null)
      {
         String[] iArr = StringTool.separateString(imports, ",", true);
         for (int i = 0; i < iArr.length; i++)
         {
            pool.importPackage(iArr[i]);
         }
      }
      ClassLoader cl = baseClass.getClassLoader();
      StringBuffer tmpCode = new StringBuffer(code.length() + 32);
      tmpCode.append("public Object invoke(AppData data, Connection conn)\n      throws Exception\n{\n");
      tmpCode.append(code);
      tmpCode.append("\n}");
      cc.addMethod(CtNewMethod.make(tmpCode.toString(), cc));
      codeClass = cc.toClass(cl);
      cache.put(key, codeClass);
      return codeClass;
   }

   protected ModelExport dealProcess(AppData data, Connection conn)
         throws ConfigurationException, SQLException, IOException, InnerExport
   {
      try
      {
         Object obj = this.javaCode.invoke(data, conn);
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

   public interface JavaCode
   {
      public void initialize(ModelAdapter model) throws ConfigurationException;

      public Object invoke(AppData data, Connection conn) throws Exception;

   }

   private static class CodeKey
   {
      private byte[] compressedCode;
      private int strCodeHash;

      public CodeKey(String code)
            throws IOException
      {
         this.strCodeHash = code.hashCode();
         ByteArrayOutputStream byteOut = new ByteArrayOutputStream(128);
         DeflaterOutputStream out = new DeflaterOutputStream(byteOut);
         byte[] buf = code.getBytes("UTF-8");
         out.write(buf);
         out.close();
         this.compressedCode = byteOut.toByteArray();
      }

      public int hashCode()
      {
         return this.strCodeHash;
      }

      public boolean equals(Object obj)
      {
         if (obj instanceof CodeKey)
         {
            CodeKey other = (CodeKey) obj;
            if (other.compressedCode.length == this.compressedCode.length
                  && other.strCodeHash == this.strCodeHash)
            {
               for (int i = 0; i < this.compressedCode.length; i++)
               {
                  if (other.compressedCode[i] != this.compressedCode[i])
                  {
                     return false;
                  }
               }
               return true;
            }
         }
         return false;
      }

   }

}
