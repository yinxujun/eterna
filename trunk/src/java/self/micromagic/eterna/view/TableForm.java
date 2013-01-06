
package self.micromagic.eterna.view;

import java.util.Iterator;

import self.micromagic.eterna.digester.ConfigurationException;

public interface TableForm extends Component
{
	public static final String TR_NAME_PERFIX = "tableForm_TR";

	boolean isAutoArrange() throws ConfigurationException;

	boolean isPercentWidth() throws ConfigurationException;

	boolean isCaculateWidth() throws ConfigurationException;

	int getCaculateWidthFix() throws ConfigurationException;

	String getColumns() throws ConfigurationException;

	Component getTR() throws ConfigurationException;

	String getBaseName() throws ConfigurationException;

	String getDataName() throws ConfigurationException;

	Iterator getCells() throws ConfigurationException;

	interface Cell extends Component
	{
		int getTitleSize() throws ConfigurationException;

		String getTitleParam() throws ConfigurationException;

		int getContainerSize() throws ConfigurationException;

		String getContainerParam() throws ConfigurationException;

		int getRowSpan() throws ConfigurationException;

		boolean isIgnoreGlobalTitleParam() throws ConfigurationException;

		boolean isIgnoreGlobalContainerParam() throws ConfigurationException;

		String getCaption() throws ConfigurationException;

		String getDefaultValue() throws ConfigurationException;

		boolean isIgnore() throws ConfigurationException;

		boolean isNewRow() throws ConfigurationException;

		String getSrcName() throws ConfigurationException;

		String getDataName() throws ConfigurationException;

		boolean isOtherData() throws ConfigurationException;

		boolean isRequired() throws ConfigurationException;

		boolean isNeedIndex() throws ConfigurationException;

		Component getTypicalComponent() throws ConfigurationException;

		String getInitParam() throws ConfigurationException;

	}

}