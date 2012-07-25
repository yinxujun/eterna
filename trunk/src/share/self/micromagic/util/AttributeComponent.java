
package self.micromagic.util;

import java.io.IOException;
import java.io.Writer;
import java.io.File;
import java.io.StringWriter;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Map;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.view.Component;
import self.micromagic.eterna.view.ComponentGenerator;
import self.micromagic.eterna.view.ViewAdapter;
import self.micromagic.eterna.view.impl.ComponentImpl;

/**
 * ͨ��factory��attribute��ֵ���������component. <p>
 * component�����ͱ�����Ϊnone, ������component����������ΪattrName������,
 * ��ֵΪ��ʹ�õ�factory��attribute������.
 * ��component�����滻swapId��ͬ���ӽڵ�.
 */
public final class AttributeComponent extends ComponentImpl
      implements Component, ComponentGenerator
{
   public static final String FACTORY_ATTRIBUTE_NAME = "attrName";
   public static final String FILE_SOURCE_NAME = "htmlSource";
   public static final String FILE_ROOT_ATTRIBUTE_NAME = "htmlSource.root";

   private String bodyHTML;
   private String swapFlag;
   private String charset = "UTF-8";

   public AttributeComponent()
   {
      this.type = NORMAL_TYPE_DIV;
   }

   public void initialize(EternaFactory factory, Component parent)
         throws ConfigurationException
   {
      if (this.initialized)
      {
         return;
      }
      super.initialize(factory, parent);

      if (NORMAL_TYPE_DIV.equals(this.type))
      {
         this.initbodyHTML();
      }
      boolean autoSet = true;
      String autoSetStr = (String) this.getAttribute("autoSet");
      if (autoSetStr != null)
      {
         autoSet = "true".equalsIgnoreCase(autoSetStr);
      }
      this.swapFlag = (String) this.getAttribute("swapFlag");

      if (autoSet)
      {
         StringAppender buf = StringTool.createStringAppender();
         buf.appendln().append("if (objConfig.bodyString != null)").appendln().append("{").appendln()
               .append("webObj.html(objConfig.bodyString);").appendln().append("}").appendln();
         if (this.swapFlag == null)
         {
            buf.append("{$ef:swapAttributeComponentSubs}(webObj, objConfig);").appendln();
         }
         else
         {
            buf.append("{$ef:swapAttributeComponentSubs}(webObj, objConfig, \"").append(this.swapFlag)
                  .append("\");").appendln();
         }
         String myScript = buf.toString();
         if (this.initScript != null)
         {
            this.initScript = this.initScript + myScript;
         }
         else
         {
            this.initScript = myScript;
         }
      }
   }

   private void initbodyHTML()
         throws ConfigurationException
   {
      String tmp;
      Map bindRes = null;
      tmp = (String) this.getAttribute("bindRes");
      if (tmp != null)
      {
         bindRes = StringTool.string2Map(tmp, ",", ':');
      }

      tmp = (String) this.getAttribute(FACTORY_ATTRIBUTE_NAME);
      if (tmp == null)
      {
         tmp = (String) this.getAttribute(FILE_SOURCE_NAME);
         if (tmp == null)
         {
            throw new ConfigurationException("Must set attribute [" + FACTORY_ATTRIBUTE_NAME
                  + "] or [" + FILE_SOURCE_NAME + "] in AttributeComponent.");
         }
         String fileRoot = (String) factory.getAttribute(FILE_ROOT_ATTRIBUTE_NAME);
         if (fileRoot == null)
         {
            fileRoot = ".";
         }
         else
         {
            fileRoot = Utility.resolveDynamicPropnames(fileRoot);
         }
         File htmlFile = new File(fileRoot, tmp);
         if (!htmlFile.isFile())
         {
            throw new ConfigurationException("Not found html source [" + htmlFile.getPath() + "].");
         }
         tmp = (String) this.getAttribute("charset");
         if (tmp != null)
         {
            this.charset = tmp;
         }
         try
         {
            int size = (int) htmlFile.length();
            StringWriter sw = new StringWriter(size);
            FileInputStream fis = new FileInputStream(htmlFile);
            InputStreamReader isr = new InputStreamReader(fis, this.charset);
            Utility.copyChars(isr, sw);
            isr.close();
            fis.close();
            String htmlStr = sw.toString();
            if (htmlStr.length() < size)
            {
               // ��ΪbodyHTML��Ҫ���ڱ����, �����ж����ַ��Ļ�����������һ���ַ���
               this.bodyHTML = new String(htmlStr);
            }
            else
            {
               this.bodyHTML = htmlStr;
            }
         }
         catch (IOException ex)
         {
            throw new ConfigurationException(ex);
         }
      }
      else
      {
         this.bodyHTML = (String) factory.getAttribute(tmp);
         if (this.bodyHTML == null)
         {
            throw new ConfigurationException("Not found attribute [" + tmp + "] in factory.");
         }
      }
      this.bodyHTML = Utility.resolveDynamicPropnames(this.bodyHTML, bindRes);
   }

   public void setType(String type)
         throws ConfigurationException
   {
      if (NORMAL_TYPE_DIV.equals(type) || SPECIAL_TYPE_INHERIT.equals(type))
      {
         this.type = type;
      }
      else
      {
         throw new ConfigurationException("Must set type as [" + NORMAL_TYPE_DIV + "] or ["
               + SPECIAL_TYPE_INHERIT + "] in AttributeComponent.");
      }
   }

   public void printSpecialBody(Writer out, AppData data, ViewAdapter view)
         throws IOException, ConfigurationException
   {
      super.printSpecialBody(out, data, view);
      out.write(',');
      out.write(NO_SUB_FLAG);
      out.write(":1");
      if (!StringTool.isEmpty(this.bodyHTML))
      {
         out.write(",bodyString:\"");
         this.stringCoder.toJsonStringWithoutCheck(out, this.bodyHTML);
         out.write('"');
      }
      if (SPECIAL_TYPE_INHERIT.equals(this.type))
      {
         out.write(',');
         out.write(INHERIT_GLOBAL_SEARCH);
         out.write(":{gSearch:1");
         if (this.swapFlag != null)
         {
            out.write(',');
            out.write(FLAG_TAG);
            out.write(":\"");
            this.stringCoder.toJsonStringWithoutCheck(out, this.swapFlag);
            out.write('"');
         }
         out.write('}');
      }
   }

}
