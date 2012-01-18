
package self.micromagic.eterna.model;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.AdapterGenerator;

public interface ModelAdapterGenerator extends AdapterGenerator
{
   void setName(String name) throws ConfigurationException;

   void setKeepCaches(boolean keep) throws ConfigurationException;

   void setNeedFrontModel(boolean needFrontModel) throws ConfigurationException;

   void setFrontModelName(String frontModelName) throws ConfigurationException;

   void setModelExportName(String name) throws ConfigurationException;

   void setErrorExportName(String name) throws ConfigurationException;

   void addExecute(Execute execute) throws ConfigurationException;

   void setTransactionType(String tType) throws ConfigurationException;

   void setDataSourceName(String dsName) throws ConfigurationException;

   void setAllowPosition(String positions) throws ConfigurationException;

   ModelAdapter createModelAdapter() throws ConfigurationException;

}
