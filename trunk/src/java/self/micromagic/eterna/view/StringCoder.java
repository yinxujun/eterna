
package self.micromagic.eterna.view;

import java.io.IOException;
import java.io.Writer;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.EternaFactory;

public interface StringCoder
{
   void initStringCoder(EternaFactory factory) throws ConfigurationException;

   /**
    * ����jsonʹ�����Ե�����. <p>
    * ����Ǹ��Ϸ�������, ��ֱ����<code>.name</code>.
    * ������������ַ�������, ��ֱʹ��<code>["name"]</code>.
    */
   String parseJsonRefName(String str);

   String toHTML(String str);

   void toHTML(Writer out, String str) throws IOException;

   String toJsonString(String str);

   void toJsonString(Writer out, String str) throws IOException;

}
