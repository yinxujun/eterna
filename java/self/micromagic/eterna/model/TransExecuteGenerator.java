
package self.micromagic.eterna.model;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.Generator;

public interface TransExecuteGenerator extends Generator
{
   void setPushResult(boolean push) throws ConfigurationException;

   void setFrom(String from) throws ConfigurationException;

   void setRemoveFrom(boolean remove) throws ConfigurationException;

   void setMustExist(boolean mustExist) throws ConfigurationException;

   void setOpt(String opt) throws ConfigurationException;

   void setTo(String toStr) throws ConfigurationException;

   Execute createExecute() throws ConfigurationException;

}
