
package self.micromagic.eterna.view;

import self.micromagic.eterna.digester.ConfigurationException;

public interface ReplacementGenerator extends ComponentGenerator
{
   void setBaseComponentName(String name) throws ConfigurationException;

}
