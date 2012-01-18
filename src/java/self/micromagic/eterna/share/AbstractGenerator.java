
package self.micromagic.eterna.share;

import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import self.micromagic.eterna.digester.ConfigurationException;

public abstract class AbstractGenerator
      implements Generator
{
   protected final static Log log = EternaFactoryImpl.log;

   protected AttributeManager attributes = new AttributeManager();

   protected String name;
   protected Factory factory;

   public Object getAttribute(String name)
   {
      return this.attributes.getAttribute(name);
   }

   public String[] getAttributeNames()
   {
      return this.attributes.getAttributeNames();
   }

   public Object setAttribute(String name, Object value)
   {
      return this.attributes.setAttribute(name, value);
   }

   public Object removeAttribute(String name)
   {
      return this.attributes.removeAttribute(name);
   }

   public void setFactory(Factory factory)
         throws ConfigurationException
   {
      this.factory = factory;
   }

   public String getName()
          throws ConfigurationException
   {
      return this.name;
   }

   public void setName(String name)
          throws ConfigurationException
   {
      if (!this.checkName(name))
      {
         throw new ConfigurationException("The name [" + name
               + "] can't use (\",\", \";\", \"#\", \"$\", \"?\", \":\", \"/\","
               + " \"{\", \"}\", \"[\", \"]\", \"(\", \")\", \"[space]\").");
      }
      this.name = name;
   }

   protected boolean checkName(String name)
   {
      if (name == null)
      {
         return true;
      }
      StringTokenizer st = new StringTokenizer(name, ",;#$?:/{}[]() \t\r\n", true);
      return st.countTokens() <= 1;
   }

   public void destroy()
   {

   }

}
