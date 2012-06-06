
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
 * 通过factory中attribute的值来生成这个component. <p>
 * component的类型必须设为none, 必须在component中设置名称为attrName的属性,
 * 该值为所使用的factory中attribute的名称.
 * 子component将会替换swapId相同的子节点.
 */
public final class AttributeComponent extends ComponentImpl
      implements Component, ComponentGenerator
{
   public static final String FACTORY_ATTRIBUTE_NAME = "attrName";
   public static final String FILE_SOURCE_NAME = "htmlSource";
   public static final String FILE_ROOT_ATTRIBUTE_NAME = "htmlSource.root";

   private String bodyHTML;
   private String swapFlag;
   private Map bindRes;
   private String charset = "UTF-8";

   public AttributeComponent()
   {
      this.type = "div";
   }

   public void initialize(EternaFactory factory, Component parent)
         throws ConfigurationException
   {
      if (this.initialized)
      {
         return;
      }
      super.initialize(factory, parent);
      String tmp;

      tmp = (String) this.getAttribute("bindRes");
      if (tmp != null)
      {
         this.bindRes = StringTool.string2Map(tmp, ",", ':');
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
               // 因为bodyHTML是要长期保存的, 所以有多余字符的话就重新生成一个字符串
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
      this.bodyHTML = Utility.resolveDynamicPropnames(this.bodyHTML, this.bindRes);
      boolean autoSet = true;
      String autoSetStr = (String) this.getAttribute("autoSet");
      if (autoSetStr != null)
      {
         autoSet = "true".equalsIgnoreCase(autoSetStr);
      }
      this.swapFlag = (String) this.getAttribute("swapFlag");

      if (autoSet)
      {
         String myScript;
         if (this.swapFlag == null)
         {
            myScript = "webObj.html(objConfig.bodyString);\n{$ef:swapAttributeComponentSubs}(webObj, objConfig);\n";
         }
         else
         {
            myScript = "webObj.html(objConfig.bodyString);\n"
                  + "{$ef:swapAttributeComponentSubs}(webObj, objConfig, '" + this.swapFlag + "');\n";
         }
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

   public void setType(String type)
         throws ConfigurationException
   {
      if (!"div".equals(type))
      {
         throw new ConfigurationException("Must set type as [div] in AttributeComponent.");
      }
   }

   public void printSpecialBody(Writer out, AppData data, ViewAdapter view)
         throws IOException, ConfigurationException
   {
      super.printSpecialBody(out, data, view);
      out.write(",noSub:1,bodyString:");
      out.write("\"");
      out.write(this.stringCoder.toJsonString(this.bodyHTML));
      out.write("\"");
   }

}
