
package self.micromagic.eterna.view;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.EternaFactory;

public interface Function
{
   public static final String ETERNA_FUNCTION_BEGIN = "{$eternaFunction:";
   public static final String ETERNA_FUNCTION_END = "}";

   String getName() throws ConfigurationException;

   String getParam() throws ConfigurationException;

   String getBody() throws ConfigurationException;

   EternaFactory getFactory() throws ConfigurationException;

   ViewAdapter.ViewRes getViewRes() throws ConfigurationException;

}
