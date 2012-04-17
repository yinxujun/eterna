
package self.micromagic.dc;

import java.sql.Connection;

import self.micromagic.eterna.search.ParameterSetting;
import self.micromagic.eterna.search.SearchAdapter;
import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.digester.FactoryManager;
import self.micromagic.eterna.sql.QueryAdapter;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.share.Generator;
import self.micromagic.eterna.share.Factory;
import self.micromagic.util.StringTool;

/**
 * 动态编译java代码来构造一个ParameterSetting.
 *
 * 需在search中设置的属性
 * code                  设置参数执行的java代码                                                   2选1
 * attrCode              从factory的属性中获取设置参数执行的java代码                              2选1
 *
 * imports               需要引入的包, 如：java.lang, 只需给出包路径, 以","分隔                   可选
 * extends               继承的类                                                                 可选
 * codeParam             预编译设置参数执行代码的参数, 格式为: key1=value1;key2=value2            可选
 * throwCompileError     是否需要将编译的错误抛出, 抛出错误会打断初始化的执行                     默认为false
 */
public class JavaCodeParameterSetting
      implements ParameterSetting, Generator
{
   private SearchAdapter search;
   private ParameterSettingCode parameterSettingCode;

   public void initParameterSetting(SearchAdapter search)
         throws ConfigurationException
   {
      this.search = search;
      if (this.parameterSettingCode != null)
      {
         return;
      }
      String code = CodeClassTool.getCode(this, search.getFactory(), "code", "attrCode", "codeParam");
      try
      {
         Class codeClass = this.createCodeClass(search, code);
         this.parameterSettingCode = (ParameterSettingCode) codeClass.newInstance();
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
            FactoryManager.log.error("Error in compile java code in parameter setting ["
                  + this.getName() + "].", ex);
         }
      }
   }

   public void setParameter(QueryAdapter query, SearchAdapter search, boolean first,
         AppData data, Connection conn)
         throws ConfigurationException
   {
      try
      {
         if (this.parameterSettingCode != null)
         {
            this.parameterSettingCode.invoke(query, search, first, data, conn);
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
   }

   public Object getAttribute(String name)
         throws ConfigurationException
   {
      return this.search.getAttribute(name);
   }

   public String[] getAttributeNames()
         throws ConfigurationException
   {
      return this.search.getAttributeNames();
   }

   public String getName()
         throws ConfigurationException
   {
      return this.search.getName();
   }

   private Class createCodeClass(SearchAdapter search, String code)
         throws Exception
   {
      String extendsStr = (String) search.getAttribute("extends");
      Class extendsClass = null;
      if (extendsStr != null)
      {
         extendsClass = Class.forName(extendsStr);
      }
      String methodHead = "public void invoke(QueryAdapter query, SearchAdapter search, boolean first, "
            + "AppData data, Connection conn)\n      throws Exception";
      String[] iArr = null;
      String imports = (String) this.getAttribute("imports");
      if (imports != null)
      {
         iArr = StringTool.separateString(imports, ",", true);
      }
      return CodeClassTool.createJavaCodeClass(extendsClass, ParameterSettingCode.class,
            methodHead, code, iArr);
   }

   public Object create()
   {
      return this;
   }

   public void setFactory(Factory factory)
   {
   }

   public Object setAttribute(String name, Object value)
   {
      return null;
   }

   public Object removeAttribute(String name)
   {
      return null;
   }

   public void setName(String name)
   {
   }

   public interface ParameterSettingCode
   {
      public void invoke(QueryAdapter query, SearchAdapter search, boolean first,
            AppData data, Connection conn)
            throws Exception;

   }

}
