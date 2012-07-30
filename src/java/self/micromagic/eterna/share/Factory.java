
package self.micromagic.eterna.share;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.digester.FactoryManager;

public interface Factory
{
   static final int MAX_ADAPTER_COUNT = 1024 * 16;

   void initialize(FactoryManager.Instance factoryManager, Factory shareFactory)
         throws ConfigurationException;

   void setName(String name) throws ConfigurationException;

   String getName() throws ConfigurationException;

   FactoryManager.Instance getFactoryManager() throws ConfigurationException;

   Object getAttribute(String name) throws ConfigurationException;

   String[] getAttributeNames() throws ConfigurationException;

   Object setAttribute(String name, Object value) throws ConfigurationException;

   Object removeAttribute(String name) throws ConfigurationException;

   void destroy();

}
