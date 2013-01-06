
package self.micromagic.eterna.model;

import self.micromagic.eterna.share.Generator;
import self.micromagic.eterna.digester.ConfigurationException;

public interface CheckExecuteGenerator extends Generator
{
	public static final String MAX_LOOP_COUNT_PROPERTY = "self.micromagic.eterna.model.maxLoopCount";

	void setCheckPattern(String pattern) throws ConfigurationException;

	void setLoopType(int type) throws ConfigurationException;

	void setTrueExportName(String name) throws ConfigurationException;

	void setFalseExportName(String name) throws ConfigurationException;

	void setTrueTransactionType(String tType) throws ConfigurationException;

	void setFalseTransactionType(String tType) throws ConfigurationException;

	void setTrueModelName(String name) throws ConfigurationException;

	void setFalseModelName(String name) throws ConfigurationException;

	Execute createExecute() throws ConfigurationException;

}