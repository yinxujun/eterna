
package self.micromagic.eterna.view;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.share.Generator;

public interface ComponentGenerator extends Generator
{
	void addComponent(Component com) throws ConfigurationException;

	void deleteComponent(Component com) throws ConfigurationException;

	void clearComponents() throws ConfigurationException;

	void addEvent(Component.Event event) throws ConfigurationException;

	void deleteEvent(Component.Event event) throws ConfigurationException;

	void clearEvent() throws ConfigurationException;

	void setType(String type) throws ConfigurationException;

	void setIgnoreGlobalParam(boolean ignore) throws ConfigurationException;

	void setComponentParam(String param) throws ConfigurationException;

	void setBeforeInit(String condition) throws ConfigurationException;

	void setInitScript(String body) throws ConfigurationException;

	void setAttributes(String attributes) throws ConfigurationException;

	void initAttributes(EternaFactory factory, String attributes) throws ConfigurationException;

	Component createComponent() throws ConfigurationException;

	interface EventGenerator extends Generator
	{
		void setName(String name) throws ConfigurationException;

		void setScriptParam(String param) throws ConfigurationException;

		void setScriptBody(String body) throws ConfigurationException;

		Component.Event createEvent() throws ConfigurationException;

	}

}