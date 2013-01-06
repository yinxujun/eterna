
package self.micromagic.eterna.model;

public class ModelExport
{
	public static final String DEFAULT_NAME = "[default]";

	private String name;
	private String modelName;
	private String viewName;
	private String path;
	private boolean redirect;
	private boolean nextModel;
	private boolean errorExport = false;

	public ModelExport(String name, boolean redirect, String modelName)
	{
		this.name = name;
		this.modelName = modelName;
		this.redirect = redirect;
		this.nextModel = true;
	}

	public ModelExport(String name, String path, boolean redirect)
	{
		this.name = name;
		this.path = path;
		this.redirect = redirect;
	}

	public ModelExport(String name, String path, String viewName, boolean redirect)
	{
		this.name = name;
		this.path = path;
		this.viewName = viewName;
		this.redirect = redirect;
	}

	public boolean equals(Object obj)
	{
		if (obj != null && obj instanceof ModelExport)
		{
			ModelExport other = (ModelExport) obj;
			if (this.redirect != other.redirect)
			{
				return false;
			}
			if (this.nextModel != other.nextModel)
			{
				return false;
			}
			if (this.errorExport != other.errorExport)
			{
				return false;
			}
			if (this.modelName == null)
			{
				if (other.modelName != null)
				{
					return false;
				}
			}
			else if (!this.modelName.equals(other.modelName))
			{
				return false;
			}
			if (this.viewName == null)
			{
				if (other.viewName != null)
				{
					return false;
				}
			}
			else if (!this.viewName.equals(other.viewName))
			{
				return false;
			}
			if (this.path == null)
			{
				if (other.path != null)
				{
					return false;
				}
			}
			else if (!this.path.equals(other.path))
			{
				return false;
			}
			return true;
		}
		return false;
	}

	public String getName()
	{
		return this.name;
	}

	public boolean isErrorExport()
	{
		return this.errorExport;
	}

	public void setErrorExport(boolean errorExport)
	{
		this.errorExport = errorExport;
	}

	public String getViewName()
	{
		return this.viewName;
	}

	public String getModelName()
	{
		return this.modelName;
	}

	public String getPath()
	{
		return this.path;
	}

	public boolean isRedirect()
	{
		return this.redirect;
	}

	public boolean isNextModel()
	{
		return nextModel;
	}

}