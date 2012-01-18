
package self.micromagic.eterna.model;

import self.micromagic.eterna.digester.ConfigurationException;

public interface TransOperator
{
   Object change(Object value) throws ConfigurationException;

}
