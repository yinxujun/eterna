
package self.micromagic.eterna.view;

import self.micromagic.eterna.share.Generator;
import self.micromagic.eterna.digester.ConfigurationException;

/**
 * �ı���Դ������.
 */
public interface ResourceGenerator extends Generator
{
   /**
    * ���ô��ı���Դ���ı�.
    */
   void setResourceText(String text) throws ConfigurationException;

   Resource createResource() throws ConfigurationException;

}
