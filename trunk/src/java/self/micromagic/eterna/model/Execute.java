
package self.micromagic.eterna.model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import self.micromagic.eterna.digester.ConfigurationException;

public interface Execute
{
	void initialize(ModelAdapter model) throws ConfigurationException;

	String getName() throws ConfigurationException;

	ModelAdapter getModelAdapter() throws ConfigurationException;

	String getExecuteType() throws ConfigurationException;

	ModelExport execute(AppData data, Connection conn)
			throws ConfigurationException, SQLException, IOException;

	void destroy();

}