
package self.micromagic.eterna.view;

import self.micromagic.eterna.share.Generator;
import self.micromagic.eterna.digester.ConfigurationException;

/**
 * 文本资源构造器.
 */
public interface ResourceGenerator extends Generator
{
   /**
    * 设置此文本资源的文本.
    */
   void setResourceText(String text) throws ConfigurationException;

   Resource createResource() throws ConfigurationException;

}
