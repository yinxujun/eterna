
package self.micromagic.eterna.view;

import java.io.IOException;
import java.io.Writer;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.EternaFactory;

public interface StringCoder
{
   void initStringCoder(EternaFactory factory) throws ConfigurationException;

   /**
    * 解析json使用属性的名称. <p>
    * 如果是个合法的名称, 则直接用<code>.name</code>.
    * 如果是有特殊字符的名称, 则直使用<code>["name"]</code>.
    */
   String parseJsonRefName(String str);

   String toHTML(String str);

   void toHTML(Writer out, String str) throws IOException;

   String toJsonString(String str);

   void toJsonString(Writer out, String str) throws IOException;

}
