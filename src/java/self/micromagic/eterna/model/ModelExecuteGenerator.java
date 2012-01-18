
package self.micromagic.eterna.model;

import self.micromagic.eterna.share.Generator;
import self.micromagic.eterna.digester.ConfigurationException;

public interface ModelExecuteGenerator extends Generator
{
   void setExportName(String name) throws ConfigurationException;

   void setTransactionType(String tType) throws ConfigurationException;

   Execute createExecute() throws ConfigurationException;

}
