
package self.micromagic.app;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.model.Execute;
import self.micromagic.eterna.model.ModelAdapter;
import self.micromagic.eterna.model.ModelExport;
import self.micromagic.eterna.model.impl.AbstractExecute;
import self.micromagic.eterna.share.Generator;
import self.micromagic.util.StringTool;
import self.micromagic.util.container.RequestParameterMap;

public class EditParameterMapExecute extends AbstractExecute
		implements Execute, Generator
{
	protected Map initValues = null;

	public void initialize(ModelAdapter model)
				throws ConfigurationException
	{
		if (this.initialized)
		{
			return;
		}
		super.initialize(model);
		String tmp;

		tmp = (String) this.getAttribute("initValues");
		if (tmp != null)
		{
			this.initValues = StringTool.string2Map(tmp, ",;", '=');
		}
	}

	public String getExecuteType() throws ConfigurationException
	{
		return "editParameterMap";
	}

	public ModelExport execute(AppData data, Connection conn)
			throws ConfigurationException, SQLException, IOException
	{
		Map map = data.getRequestParameterMap();
		boolean changeMap = true;
		if (map instanceof RequestParameterMap)
		{
			RequestParameterMap rpm = (RequestParameterMap) map;
			if (!rpm.isReadOnly())
			{
				changeMap = false;
			}
			else
			{
				map = rpm.getOriginParamMap();
			}
		}
		if (changeMap)
		{
			Map tmp = RequestParameterMap.create(map, false);
			data.maps[AppData.REQUEST_PARAMETER_MAP] = tmp;
			map = tmp;
		}
		if (this.initValues != null)
		{
			map.putAll(this.initValues);
		}
		return null;
	}

}