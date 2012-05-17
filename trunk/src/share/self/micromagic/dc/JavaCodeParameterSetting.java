
package self.micromagic.dc;

import java.sql.Connection;

import self.micromagic.eterna.search.ParameterSetting;
import self.micromagic.eterna.search.SearchAdapter;
import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.sql.QueryAdapter;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.share.Generator;
import self.micromagic.eterna.share.Factory;
import self.micromagic.util.StringTool;

/**
 * ��̬����java����������һ��ParameterSetting.
 *
 * ����search�����õ�����
 * code                  ���ò���ִ�е�java����                                                   2ѡ1
 * attrCode              ��factory�������л�ȡ���ò���ִ�е�java����                              2ѡ1
 *
 * imports               ��Ҫ����İ�, �磺java.lang, ֻ�������·��, ��","�ָ�                   ��ѡ
 * extends               �̳е���                                                                 ��ѡ
 * throwCompileError     �Ƿ���Ҫ������Ĵ����׳�, �׳�������ϳ�ʼ����ִ��                     Ĭ��Ϊfalse
 *
 * otherParams
 * Ԥ���봦���������ɴ���Ĳ���, ������Ҫ������еĲ�������ƥ��, ֵΪ��������Ӧ�Ĵ���
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
      String code = CodeClassTool.getCode(this, search.getFactory(), "code", "attrCode");
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
            String pos = "search:[" + search.getName() + "], ParameterSetting";
            CodeClassTool.logCodeError(code, pos, ex);
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
