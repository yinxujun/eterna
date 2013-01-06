
package self.micromagic.eterna.share;

import self.micromagic.eterna.digester.ConfigurationException;

public interface AdapterGenerator extends Generator
{
	void initialize(EternaFactory factory) throws ConfigurationException;

	EternaFactory getFactory() throws ConfigurationException;

	void destroy();

}