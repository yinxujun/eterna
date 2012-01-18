
package self.micromagic.eterna.view;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.Generator;

public interface FunctionGenerator extends Generator
{
   void setName(String name) throws ConfigurationException;

   void setParam(String param) throws ConfigurationException;

   void setBody(String body) throws ConfigurationException;

   Function createFunction() throws ConfigurationException;

}
