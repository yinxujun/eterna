
package self.micromagic.eterna.share;

import self.micromagic.eterna.digester.ConfigurationException;

public interface Generator
{
   void setFactory(Factory factory) throws ConfigurationException;

   Object getAttribute(String name) throws ConfigurationException;

   String[] getAttributeNames() throws ConfigurationException;

   Object setAttribute(String name, Object value) throws ConfigurationException;

   Object removeAttribute(String name) throws ConfigurationException;

   void setName(String name) throws ConfigurationException;

   String getName() throws ConfigurationException;

   Object create() throws ConfigurationException;

}
