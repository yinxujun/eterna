
package self.micromagic.eterna.model;

import java.sql.SQLException;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.EternaFactory;

public interface ParamBind
{
	void initialize(ModelAdapter model, Execute execute) throws ConfigurationException;

	public int setParam(AppData data, ParamSetManager psm, int loopIndex)
			throws ConfigurationException, SQLException;

	public boolean isLoop() throws ConfigurationException;

	public boolean isSubSQL() throws ConfigurationException;

}