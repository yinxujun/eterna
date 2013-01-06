
package self.micromagic.eterna.share;

import self.micromagic.eterna.digester.FactoryManager;
import self.micromagic.eterna.digester.ConfigurationException;

public abstract class AbstractFactory
		implements Factory
{
	protected AttributeManager attributes = new AttributeManager();

	protected String name;
	protected Factory shareFactory;
	protected FactoryManager.Instance factoryManager;

	public void initialize(FactoryManager.Instance factoryManager, Factory shareFactory)
			throws ConfigurationException
	{
		if (shareFactory == this)
		{
			throw new ConfigurationException("The parent can't same this.");
		}
		this.factoryManager = factoryManager;
		this.shareFactory = shareFactory;
	}

	public String getName()
			throws ConfigurationException
	{
		return this.name;
	}

	public void setName(String name)
			throws ConfigurationException
	{
		this.name = name;
	}

	public FactoryManager.Instance getFactoryManager()
			throws ConfigurationException
	{
		return this.factoryManager;
	}

	public Object getAttribute(String name)
			throws ConfigurationException
	{
		return this.attributes.getAttribute(name);
	}

	public String[] getAttributeNames()
			throws ConfigurationException
	{
		return this.attributes.getAttributeNames();
	}

	public Object setAttribute(String name, Object value)
			throws ConfigurationException
	{
		return this.attributes.setAttribute(name, value);
	}

	public Object removeAttribute(String name)
			throws ConfigurationException
	{
		return this.attributes.removeAttribute(name);
	}

	public boolean hasAttribute(String name)
			throws ConfigurationException
	{
		return this.attributes.hasAttribute(name);
	}

	public void destroy()
	{
	}

}