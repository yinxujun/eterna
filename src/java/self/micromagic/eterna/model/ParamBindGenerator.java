
package self.micromagic.eterna.model;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.Generator;

public interface ParamBindGenerator extends Generator
{
	public void setSrc(String src) throws ConfigurationException;

	public void setNames(String names) throws ConfigurationException;

	public void setLoop(boolean loop) throws ConfigurationException;

	public void setSubSQL(boolean subSQL) throws ConfigurationException;

	ParamBind createParamBind() throws ConfigurationException;

}