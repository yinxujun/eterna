
package self.micromagic.eterna.share;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.digester.FactoryManager;
import self.micromagic.eterna.digester.ObjectLogRule;

public class FactoryGeneratorManager
{
	private Map generatorMap = new HashMap();
	private List generatorList = new ArrayList();

	private String managerName;
	private EternaFactory factory;

	private FactoryGeneratorManager shareFGM;
	private boolean initialized = false;

	public FactoryGeneratorManager(String managerName, EternaFactory factory)
	{
		this.managerName = managerName;
		this.factory = factory;
	}

	public void initialize(FactoryGeneratorManager shareFGM)
			throws ConfigurationException
	{
		if (this.initialized)
		{
			return;
		}
		this.initialized = true;
		this.shareFGM = shareFGM;

		List temp = new ArrayList(this.generatorList.size() + (this.shareFGM == null ? 32 : 2));
		temp.addAll(this.generatorList);
		this.generatorList = temp;

		int size = temp.size();
		GeneratorContainer container;
		for (int i = 0; i < size; i++)
		{
			container = (GeneratorContainer) this.generatorList.get(i);
			ObjectLogRule.setObjName(this.managerName, container.generator.getName());
			container.generator.initialize(this.factory);
		}
	}

	public void destroy()
	{
		Iterator itr = this.generatorList.iterator();
		while (itr.hasNext())
		{
			GeneratorContainer container = (GeneratorContainer) itr.next();
			container.generator.destroy();
		}
	}

	public Object create(String name)
			throws ConfigurationException
	{
		GeneratorContainer container = (GeneratorContainer) this.generatorMap.get(name);
		if (container == null)
		{
			if (this.shareFGM != null)
			{
				return this.shareFGM.create(name);
			}
			throw new ConfigurationException(
					"Not found [" + this.managerName + "] name:" + name + ".");
		}
		return container.generator.create();
	}

	public Object create(int id)
			throws ConfigurationException
	{
		if (id < 0 || id >= this.generatorList.size())
		{
			if (this.shareFGM != null && id >= Factory.MAX_ADAPTER_COUNT)
			{
				return this.shareFGM.create(id - Factory.MAX_ADAPTER_COUNT);
			}
			throw new ConfigurationException(
					"Not found [" + this.managerName + "] id:" + id + ".");
		}

		GeneratorContainer container = (GeneratorContainer) this.generatorList.get(id);
		if (container == null)
		{
			throw new ConfigurationException(
					"Not found [" + this.managerName + "] id:" + id + ".");
		}
		return container.generator.create();
	}

	public int getIdByName(String name)
			throws ConfigurationException
	{
		GeneratorContainer container = (GeneratorContainer) this.generatorMap.get(name);
		if (container == null)
		{
			if (this.shareFGM != null)
			{
				return this.shareFGM.getIdByName(name) + Factory.MAX_ADAPTER_COUNT;
			}
			throw new ConfigurationException(
					"Not found [" + this.managerName + "] name:" + name + ".");
		}
		return container.id;
	}

	public void register(AdapterGenerator generator)
			throws ConfigurationException
	{
		if (generator == null)
		{
			throw new NullPointerException();
		}
		String name = generator.getName();
		if (this.generatorMap.containsKey(name))
		{
			if (!FactoryManager.isSuperInit())
			{
				throw new ConfigurationException(
						"Duplicate [" + this.managerName + "] name:" + name + ".");
			}
			else
			{
				return;
			}
		}
		if (this.initialized)
		{
			generator.initialize(this.factory);
		}
		GeneratorContainer container;
		int id = this.generatorList.size();
		if (id >= Factory.MAX_ADAPTER_COUNT)
		{
			throw new ConfigurationException("Max adapter count:" + id + "," + Factory.MAX_ADAPTER_COUNT + ".");
		}
		container = new GeneratorContainer(id, generator);
		this.generatorList.add(container);

		this.generatorMap.put(name, container);
	}

	public void deregister(String name)
			throws ConfigurationException
	{
		GeneratorContainer container = (GeneratorContainer) this.generatorMap.get(name);
		if (container == null)
		{
			throw new ConfigurationException(
					"Not found [" + this.managerName + "] name:" + name + ".");
		}
		this.generatorList.set(container.id, null);
		this.generatorMap.remove(name);
		container.generator = null;
	}

	private class GeneratorContainer
	{
		public final int id;
		public AdapterGenerator generator;

		public GeneratorContainer(int id, AdapterGenerator generator)
		{
			this.id = id;
			this.generator = generator;
		}

	}

}