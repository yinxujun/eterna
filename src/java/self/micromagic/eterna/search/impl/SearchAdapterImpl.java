/*
 * Copyright 2009-2015 xinjunli (micromagic@sina.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package self.micromagic.eterna.search.impl;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;
import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.search.ColumnSetting;
import self.micromagic.eterna.search.ConditionBuilder;
import self.micromagic.eterna.search.ConditionProperty;
import self.micromagic.eterna.search.ParameterSetting;
import self.micromagic.eterna.search.SearchAdapter;
import self.micromagic.eterna.search.SearchAdapterGenerator;
import self.micromagic.eterna.search.SearchManager;
import self.micromagic.eterna.security.EmptyPermission;
import self.micromagic.eterna.security.Permission;
import self.micromagic.eterna.security.PermissionSet;
import self.micromagic.eterna.security.User;
import self.micromagic.eterna.security.UserManager;
import self.micromagic.eterna.share.AbstractGenerator;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.share.OrderManager;
import self.micromagic.util.container.SessionCache;
import self.micromagic.eterna.share.TypeManager;
import self.micromagic.eterna.sql.QueryAdapter;
import self.micromagic.eterna.sql.ResultIterator;
import self.micromagic.eterna.sql.ResultReaderManager;
import self.micromagic.eterna.sql.preparer.PreparerManager;
import self.micromagic.util.BooleanRef;
import self.micromagic.util.MemoryChars;

/**
 * @author micromagic@sina.com
 */
public class SearchAdapterImpl extends AbstractGenerator
		implements SearchAdapter, SearchAdapterGenerator
{
	private static final int[] conditionDocumentCounts = {1, 3, 7};

	private int maxPageSize = -1;

	private String parentName;
	private SearchAdapter[] parents;

	private String sessionQueryTag;
	private String searchManagerName = null;
	private String queryName;
	private int queryIndex;
	private String columnType;
	private ColumnSetting columnSetting = null;
	private ParameterSetting parameterSetting = null;
	private int countType = QueryAdapter.TOTAL_COUNT_AUTO;
	private String countReaderName = null;
	private String countSearchName = null;
	private int countSearchIndex = -1;

	private String otherName;
	private SearchAdapter[] others = null;
	private String conditionPropertyOrderWithOther = null;

	private boolean needWrap = true;
	private boolean specialCondition = false;
	private int conditionIndex;

	private String conditionPropertyOrder = null;
	private PermissionSet[] permissionSets = null;
	private Map conditionPropertyMap =  new HashMap();
	private List conditionProperties = new LinkedList();
	private ConditionProperty[] allConditionProperties = null;
	private ConditionProperty[] allConditionPropertiesWithOther = null;
	private int conditionDocumentCount = 1;
	private MemoryChars conditionDocument = null;
	private MemoryChars[] conditionDocuments = null;

	/**
	 * 执行搜索时(doSearch), 是否要加同步锁.
	 * 在search的attribute中通过needSynchronize属性名进行设置.
	 */
	protected boolean needSynchronize = false;

	private boolean initialized = false;

	public void initialize(EternaFactory factory)
			throws ConfigurationException
	{
		if (this.initialized)
		{
			return;
		}
		this.initialized = true;
		this.sessionQueryTag = "s:" + this.getName() + ":" + factory.getFactoryManager().getId();
		if (this.parentName != null)
		{
			if (this.parentName.indexOf(',') == -1)
			{
				this.parents = new SearchAdapter[1];
				this.parents[0] = factory.createSearchAdapter(this.parentName);
				if (this.parents[0] == null)
				{
					log.warn("The search parent [" + this.parentName + "] not found.");
				}
			}
			else
			{
				StringTokenizer token = new StringTokenizer(this.parentName, ",");
				this.parents = new SearchAdapter[token.countTokens()];
				for (int i = 0; i < this.parents.length; i++)
				{
					String temp = token.nextToken().trim();
					this.parents[i] = factory.createSearchAdapter(temp);
					if (this.parents[i] == null)
					{
						log.warn("The search parent [" + temp + "] not found.");
					}
				}
			}
		}

		if (this.otherName != null)
		{
			if (this.otherName.indexOf(',') == -1)
			{
				this.others = new SearchAdapter[1];
				this.others[0] = factory.createSearchAdapter(this.otherName);
				if (this.others[0] == null)
				{
					log.warn("The search parent [" + this.otherName + "] not found.");
				}
			}
			else
			{
				StringTokenizer token = new StringTokenizer(this.otherName, ",");
				this.others = new SearchAdapter[token.countTokens()];
				for (int i = 0; i < this.others.length; i++)
				{
					String temp = token.nextToken().trim();
					this.others[i] = factory.createSearchAdapter(temp);
					if (this.others[i] == null)
					{
						log.warn("The search parent [" + temp + "] not found.");
					}
				}
			}
		}

		if (this.queryName != null && !NONE_QUERY_NAME.equals(this.queryName))
		{
			this.queryIndex = this.getFactory().getQueryAdapterId(this.queryName);
		}
		else
		{
			this.queryName = NONE_QUERY_NAME;
			this.queryIndex = -1;
		}

		if (this.searchManagerName == null)
		{
			this.searchManagerName = this.sessionQueryTag;
		}
		if (this.countSearchName != null)
		{
			this.countSearchIndex = this.getFactory().getSearchAdapterId(this.countSearchName);
		}

		Iterator cps = this.conditionProperties.iterator();
		while (cps.hasNext())
		{
			ConditionProperty cp = (ConditionProperty) cps.next();
			cp.initialize(factory);
		}

		if (this.parameterSetting != null)
		{
			this.parameterSetting.initParameterSetting(this);
		}
		if (this.columnSetting != null)
		{
			this.columnSetting.initColumnSetting(this);
		}

		/*
		当conditionIndex为0时不设置条件, 无论是否设置了ConditionProperty
		这样在多个search共同运作时便于定义一个公共的search让其它的search继承,
		而这个search本身可能不会设置任何条件
		if (this.conditionIndex == 0)
		{
			if (this.getConditionPropertyCount() > 0)
			{
				throw new ConfigurationException("Can't set conditionIndex 0 in a search witch has conditionProperty.");
			}
		}
		*/

		String tmpStr = (String) this.getAttribute("needSynchronize");
		if (tmpStr != null)
		{
			this.needSynchronize = "true".equalsIgnoreCase(tmpStr);
		}
	}

	public Object create()
			throws ConfigurationException
	{
		return this.createSearchAdapter();
	}

	public SearchAdapter createSearchAdapter()
			throws ConfigurationException
	{
		return this;
	}

	public EternaFactory getFactory()
	{
		return (EternaFactory) this.factory;
	}

	public void setQueryName(String queryName)
	{
		this.queryName = queryName;
	}

	public String getQueryName()
	{
		return this.queryName;
	}

	public boolean isSpecialCondition()
	{
		return this.specialCondition;
	}

	public void setSpecialCondition(boolean special)
	{
		this.specialCondition = special;
	}

	public void setCountType(String countType)
			throws ConfigurationException
	{
		if ("auto".equals(countType))
		{
			this.countType = QueryAdapter.TOTAL_COUNT_AUTO;
		}
		else if ("count".equals(countType))
		{
			this.countType = QueryAdapter.TOTAL_COUNT_COUNT;
		}
		else if ("none".equals(countType))
		{
			this.countType = QueryAdapter.TOTAL_COUNT_NONE;
		}
		else if (countType != null && countType.startsWith("search:"))
		{
			int index = countType.indexOf(',');
			if (index == -1)
			{
				throw new ConfigurationException("Error count type:[" + countType + "].");
			}
			this.countSearchName = countType.substring(7, index).trim();
			this.countReaderName = countType.substring(index + 1).trim();
		}
		else
		{
			throw new ConfigurationException("Error count type:[" + countType + "].");
		}
	}

	public boolean isNeedWrap()
	{
		return this.needWrap;
	}

	public void setNeedWrap(boolean needWrap)
	{
		this.needWrap = needWrap;
	}

	public int getConditionIndex()
	{
		return this.conditionIndex;
	}

	public void setConditionIndex(int index)
	{
		this.conditionIndex = index;
	}

	public String getColumnSettingType()
	{
		return this.columnType;
	}

	public void setColumnSettingType(String type)
	{
		this.columnType = type;
	}

	public ColumnSetting getColumnSetting()
	{
		return this.columnSetting;
	}

	public void setColumnSetting(ColumnSetting setting)
	{
		this.columnSetting = setting;
	}

	public ParameterSetting getParameterSetting()
	{
		return this.parameterSetting;
	}

	public void setParameterSetting(ParameterSetting setting)
	{
		this.parameterSetting = setting;
	}

	public String getOtherSearchManagerName()
	{
		return this.otherName;
	}

	public SearchAdapter[] getOtherSearchs()
	{
		if (this.others == null)
		{
			return null;
		}
		SearchAdapter[] result = new SearchAdapter[this.others.length];
		System.arraycopy(this.others, 0, result, 0, this.others.length);
		return result;
	}

	public void setOtherSearchManagerName(String otherName)
	{
		this.otherName = otherName;
	}

	public String getConditionPropertyOrderWithOther()
	{
		return this.conditionPropertyOrderWithOther;
	}

	public void setConditionPropertyOrderWithOther(String order)
	{
		this.conditionPropertyOrderWithOther = order;
	}

	private PermissionSet[] getPermissionSet(ConditionProperty[] cps)
			throws ConfigurationException
	{
		Set psSet = null;
		for (int i = 0; i < cps.length; i++)
		{
			PermissionSet ps = cps[i].getPermissionSet();
			if (ps != null)
			{
				if (psSet == null)
				{
					psSet = new HashSet();
				}
				psSet.add(ps);
			}
		}
		if (psSet == null)
		{
			this.conditionDocuments = null;
			this.conditionDocumentCount = 1;
			return new PermissionSet[0];
		}
		else
		{
			PermissionSet[] result = new PermissionSet[psSet.size()];
			psSet.toArray(result);
			if (result.length <= conditionDocumentCounts.length)
			{
				this.conditionDocuments = new MemoryChars[conditionDocumentCounts[result.length - 1]];
			}
			else
			{
				this.conditionDocuments = null;
			}
			this.conditionDocumentCount = (int) Math.pow(2, result.length);
			return result;
		}
	}

	private MemoryChars getConditionDocument0(Permission permission)
			throws ConfigurationException
	{
		int cdId = 0;
		int addInt = 1;
		for (int i = 0; i < this.permissionSets.length; i++)
		{
			if (this.permissionSets[i].checkPermission(permission))
			{
				cdId += addInt;
			}
			addInt *= 2;
		}
		if (cdId == this.conditionDocumentCount - 1)
		{
			if (this.conditionDocument == null)
			{
				this.conditionDocument = this.createConditionDocument(permission);
			}
			return this.conditionDocument;
		}
		if (this.conditionDocuments != null)
		{
			if (this.conditionDocuments[cdId] == null)
			{
				this.conditionDocuments[cdId] = this.createConditionDocument(permission);
			}
			return this.conditionDocuments[cdId];
		}
		return this.createConditionDocument(permission);
	}

	private MemoryChars createConditionDocument(Permission permission)
			throws ConfigurationException
	{
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("eterna");
		Element el_cps = root.addElement("condition-propertys");
		if (this.others != null && this.others.length > 0)
		{
			el_cps.addAttribute("noGroup", "1");
		}
		Element el_cbls = root.addElement("condition-builder-lists");

		this.list2Document(this.getConditionPropertysWithOther(),
				el_cps, el_cbls, permission);
		MemoryChars mcs = new MemoryChars(2, 256);
		XMLWriter writer = new XMLWriter(mcs.getWriter());
		try
		{
			writer.write(document);
			writer.close();
		}
		catch (IOException ex)
		{
			//use MemoryChars, so not IOException
		}
		return mcs;
	}

	public Reader getConditionDocument(Permission permission)
			throws ConfigurationException
	{
		if (this.permissionSets == null)
		{
			synchronized (this)
			{
				if (this.permissionSets == null)
				{
					this.permissionSets = this.getPermissionSet(this.getConditionPropertysWithOther());
				}
			}
		}
		return this.getConditionDocument0(permission).getReader();
	}

	private void list2Document(ConditionProperty[] cps, Element el_conditionPropertys,
			Element el_conditionBuilserLists, Permission permission)
			throws ConfigurationException
	{
		Set addedBuilders = new HashSet();

		for (int i = 0; i < cps.length; i++)
		{
			ConditionProperty cp = cps[i];
			if (checkPermission(cp, permission))
			{
				Element el_cp = el_conditionPropertys.addElement("condition-property");
				el_cp.addAttribute("name", cp.getName());
				el_cp.addAttribute("colId", i + "");
				el_cp.addAttribute("caption", cp.getColumnCaption());
				el_cp.addAttribute("inputType", cp.getConditionInputType());
				el_cp.addAttribute("type",
						TypeManager.getTypeName(TypeManager.getPureType(cp.getColumnType())));
				el_cp.addAttribute("builderList", cp.getConditionBuilderListName());

				String[] pNames = cp.getAttributeNames();
				if (pNames.length > 0)
				{
					for (int j = 0; j < pNames.length; j++)
					{
						el_cp.addElement("parameter").addAttribute("name", pNames[j])
								.addAttribute("value", cp.getAttribute(pNames[j]));
					}
				}

				if (!addedBuilders.contains(cp.getConditionBuilderListName()))
				{
					addedBuilders.add(cp.getConditionBuilderListName());
					Element el_cbl = el_conditionBuilserLists.addElement("builder-list");
					el_cbl.addAttribute("name", cp.getConditionBuilderListName());
					Iterator cbl = cp.getConditionBuilderList().iterator();
					while (cbl.hasNext())
					{
						ConditionBuilder cb = (ConditionBuilder) cbl.next();
						el_cbl.addElement("builder").addAttribute("name", cb.getName())
								.addAttribute("caption", cb.getCaption());
					}
				}
			}
		}
	}

	private boolean checkPermission(ConditionProperty cp, Permission permission)
			throws ConfigurationException
	{
		if (permission == null)
		{
			return true;
		}
		PermissionSet ps = cp.getPermissionSet();
		if (ps == null)
		{
			return true;
		}
		return ps.checkPermission(permission);
	}

	public String getParentConditionPropretyName()
	{
		return this.parentName;
	}

	public void setParentConditionPropretyName(String parentName)
	{
		this.parentName = parentName;
	}

	public String getConditionPropertyOrder()
	{
		return this.conditionPropertyOrder;
	}

	public void setConditionPropertyOrder(String order)
	{
		this.conditionPropertyOrder = order;
	}

	public void clearConditionPropertys() throws ConfigurationException
	{
		this.allConditionProperties = null;
		this.conditionProperties.clear();
	}

	public void addConditionProperty(ConditionProperty cp) throws ConfigurationException
	{
		this.allConditionProperties = null;
		if (this.conditionPropertyMap.containsKey(cp.getName()))
		{
			throw new ConfigurationException(
					"Duplicate [ConditionProperty] name:" + cp.getName() + ".");
		}
		this.conditionProperties.add(cp);
		this.conditionPropertyMap.put(cp.getName(), cp);
	}

	public int getConditionPropertyCount()
			throws ConfigurationException
	{
		return this.getConditionPropertys0().length;
	}

	public ConditionProperty getConditionProperty(int colId)
			throws ConfigurationException
	{
		ConditionProperty[] temp = this.getConditionPropertys0();
		return temp[colId];
	}

	public ConditionProperty getConditionProperty(String name)
			throws ConfigurationException
	{
		this.getConditionPropertys0();
		return (ConditionProperty) this.conditionPropertyMap.get(name);
	}

	public int getPageSize()
			throws ConfigurationException
	{
		if (this.maxPageSize == -1)
		{
			int tempSize = -1;
			Object size = this.getFactory().getAttribute(PAGE_SIZE_ATTRIBUTE);
			if (size != null)
			{
				try
				{
					tempSize = Integer.parseInt((String) size);
				}
				catch (NumberFormatException ex) {}
			}
			this.maxPageSize = tempSize < 1 ? 10 : tempSize;
		}
		return this.maxPageSize;
	}

	public void setPageSize(int pageSize)
	{
		if (pageSize > 0)
		{
			this.maxPageSize = pageSize;
		}
	}

	public String getSearchManagerName()
	{
		return this.searchManagerName;
	}

	public void setSearchManagerName(String name)
	{
		this.searchManagerName = name;
	}

	public SearchManager getSearchManager(AppData data)
			throws ConfigurationException
	{
		SearchManager manager = this.getSearchManager0(data.getSessionAttributeMap());
		manager.setPageNumAndCondition(data, this);
		return manager;
	}

	public Result doSearch(AppData data, Connection conn)
			throws ConfigurationException, SQLException
	{
		if (this.needSynchronize)
		{
			synchronized (this)
			{
				return this.doSearch0(data, conn, false);
			}
		}
		return this.doSearch0(data, conn, false);
	}

	/**
	 * 执行查询, 并获得结果.
	 *
	 * @param data        AppData对象
	 * @param conn        数据库连接
	 * @param onlySearch  是否为仅执行搜索, 不进行列设置或全记录获取
	 */
	protected Result doSearch0(AppData data, Connection conn, boolean onlySearch)
			throws ConfigurationException, SQLException
	{
		if (log.isDebugEnabled())
		{
			log.debug("Start prepare query:" + System.currentTimeMillis());
		}

		Map raMap = data.getRequestAttributeMap();
		BooleanRef isFirst = new BooleanRef();
		SearchManager manager = this.getSearchManager0(data.getSessionAttributeMap());
		QueryAdapter query = getQueryAdapter(data, conn, this, isFirst, this.sessionQueryTag, manager,
				this.queryIndex, onlySearch ? null : this.columnSetting, onlySearch ? null : this.columnType);
		manager.setPageNumAndCondition(data, this);

		if (query == null)
		{
			log.warn("The search [" + this.getName() + "] can't execute!");
			return null;
		}
		int maxRow = manager.getPageSize(this.getPageSize());
		int pageNum = manager.getPageNum();
		int startRow = pageNum * maxRow;
		if ("1".equals(raMap.get(READ_ALL_ROW)) && !onlySearch)
		{
			startRow = 0;
			pageNum = -1;
			maxRow = -1;
		}
		else
		{
			Object start_and_count = raMap.get(READ_ROW_START_AND_COUNT);
			if (start_and_count != null & start_and_count instanceof StartAndCount)
			{
				StartAndCount temp = (StartAndCount) start_and_count;
				maxRow = temp.count;
				if (temp.start >= 0)
				{
					startRow = temp.start - 1;
					pageNum = -1;
				}
				else
				{
					startRow = pageNum * maxRow;
				}
			}
		}
		query.setMaxRows(maxRow);
		query.setStartRow(startRow + 1);
		dealOthers(data, conn, this.others, query, isFirst.value);
		if (this.conditionIndex > 0)
		{
			if (this.specialCondition)
			{
				String subConSQL = manager.getSpecialConditionPart(this, this.needWrap);
				PreparerManager spm = manager.getSpecialPreparerManager(this);
				query.setSubSQL(this.conditionIndex, subConSQL, spm);
			}
			else
			{
				query.setSubSQL(this.conditionIndex, manager.getConditionPart(this.needWrap),
						manager.getPreparerManager());
			}
		}
		if (this.parameterSetting != null)
		{
			this.parameterSetting.setParameter(query, this, isFirst.value, data, conn);
		}
		String singleOrederName = null;
		boolean singleOrederDesc = false;
		if (query.canOrder())
		{
			String orderStr = data.getRequestParameter(this.getName() + SINGLE_ORDER_SUFIX);
			if (orderStr != null)
			{
				int orderType = 0;
				String orderTypeStr = data.getRequestParameter(this.getName() + SINGLE_ORDER_TYPE);
				if (orderTypeStr != null)
				{
					try
					{
						orderType = Integer.parseInt(orderTypeStr);
					}
					catch (Exception ex) {}
				}
				query.setSingleOrder(orderStr, orderType);
			}
			BooleanRef tmp = new BooleanRef();
			singleOrederName = query.getSingleOrder(tmp);
			singleOrederDesc = tmp.value;
		}

		if (log.isDebugEnabled())
		{
			log.debug("Search SQL:" + query.getPreparedSQL());
			log.debug("End prepare query:" + System.currentTimeMillis());
		}
		//System.out.println("Search SQL:" + query.getPreparedSQL());
		ResultIterator countRitr = null;
		ResultIterator ritr;
		if ("1".equals(raMap.get(HOLD_CONNECTION)) && !onlySearch)
		{
			ritr = query.executeQueryHoldConnection(conn);
		}
		else
		{
			if (this.countSearchIndex != -1)
			{
				SearchAdapter tmpSearch = this.getFactory().createSearchAdapter(this.countSearchIndex);
				Object oldObj = raMap.get(READ_ROW_START_AND_COUNT);
				raMap.put(READ_ROW_START_AND_COUNT, new StartAndCount(1, 1));
				countRitr = tmpSearch.doSearch(data, conn).queryResult;
				int count = countRitr.nextRow().getInt(this.countReaderName);
				query.setTotalCount(count);
				countRitr.beforeFirst();
				if (oldObj == null)
				{
					raMap.remove(READ_ROW_START_AND_COUNT);
				}
				else
				{
					raMap.put(READ_ROW_START_AND_COUNT, oldObj);
				}
			}
			else if (this.countType != 0)
			{
				query.setTotalCount(this.countType);
			}
			ritr = query.executeQuery(conn);
		}
		Result result = new Result(this.name, this.queryName, ritr, countRitr, maxRow, pageNum,
				singleOrederName, singleOrederDesc);
		if (log.isDebugEnabled())
		{
			log.debug("End execute query:" + System.currentTimeMillis());
		}
		return result;
	}

	/**
	 * 通过其他的辅助search来设置条件.
	 *
	 * @param data      AppData对象
	 * @param conn      数据库连接
	 * @param others    其他的辅助search
	 * @param query     用于执行查询的query对象
	 * @param first     是否为第一次执行
	 */
	protected static void dealOthers(AppData data, Connection conn, SearchAdapter[] others,
			QueryAdapter query, boolean first)
			throws ConfigurationException
	{
		if (others == null)
		{
			return;
		}
		for (int i = 0; i < others.length; i++)
		{
			SearchAdapter other = others[i];
			if (other.getConditionIndex() > 0)
			{
				SearchManager om = other.getSearchManager(data);
				if (other.isSpecialCondition())
				{
					String subConSQL = om.getSpecialConditionPart(other, other.isNeedWrap());
					PreparerManager spm = om.getSpecialPreparerManager(other);
					query.setSubSQL(other.getConditionIndex(), subConSQL, spm);
				}
				else
				{
					query.setSubSQL(other.getConditionIndex(), om.getConditionPart(other.isNeedWrap()),
							om.getPreparerManager());
				}
			}
			ParameterSetting ps = other.getParameterSetting();
			if (ps != null)
			{
				ps.setParameter(query, other, first, data, conn);
			}
		}
	}


	/**
	 * 获得一个用于执行查询的query对象.
	 *
	 * @param data              AppData对象
	 * @param conn              数据库连接, 在获取列设置时会使用到
	 * @param search            当前的search对象
	 * @param first             出参, 是否为第一次执行, 第一次进入或重新设置了条件时, 值为true
	 * @param sessionQueryTag   query放在session中使用的名称
	 * @param searchManager     搜索的管理器, 用于控制分页及查询条件
	 * @param queryIndex        用于获取查询的索引值
	 * @param columnSetting     用于进行列设置的对象
	 * @param columnType        列设置的类型, 用于区分读取哪个列设置
	 */
	protected static QueryAdapter getQueryAdapter(AppData data, Connection conn, SearchAdapter search,
			BooleanRef first, String sessionQueryTag, SearchManager searchManager, int queryIndex,
			ColumnSetting columnSetting, String columnType)
			throws ConfigurationException
	{
		if (queryIndex == -1)
		{
			return null;
		}
		QueryAdapter query = null;
		Map raMap = data.getRequestAttributeMap();
		if ("1".equals(raMap.get(FORCE_LOAD_COLUMN_SETTING)) && columnSetting != null)
		{
			query = search.getFactory().createQueryAdapter(queryIndex);
			String[] colSetting = columnSetting.getColumnSetting(columnType, query, search, true, data, conn);
			if (colSetting != null)
			{
				ResultReaderManager readerManager = query.getReaderManager();
				readerManager.setReaderList(colSetting);
				query.setReaderManager(readerManager);
			}
			UserManager um = search.getFactory().getUserManager();
			if (um != null)
			{
				User user = um.getUser(data);
				if (user != null)
				{
					query.setPermission(user.getPermission());
				}
				else
				{
					query.setPermission(EmptyPermission.getInstance());
				}
			}
			first.value = true;
			return query;
		}

		Map saMap = data.getSessionAttributeMap();
		Map queryMap = (Map) SessionCache.getInstance().getProperty(saMap, SESSION_SEARCH_QUERY);
		if (queryMap == null)
		{
			queryMap = new HashMap();
			SessionCache.getInstance().setProperty(saMap, SESSION_SEARCH_QUERY, queryMap);
		}

		QueryContainer qc = (QueryContainer) queryMap.get(sessionQueryTag);
		int qcVersion;
		if (qc == null)
		{
			qcVersion = 0;
		}
		else
		{
			qcVersion = qc.conditionVersion;
		}
		boolean isFirst = searchManager.hasQueryType(data) || searchManager.getConditionVersion() > qcVersion;
		qcVersion = searchManager.getConditionVersion();
		if (isFirst || qc == null)
		{
			isFirst = true;
			query = search.getFactory().createQueryAdapter(queryIndex);
			UserManager um = search.getFactory().getUserManager();
			if (um != null)
			{
				User user = um.getUser(data);
				if (user != null)
				{
					query.setPermission(user.getPermission());
				}
				else
				{
					query.setPermission(EmptyPermission.getInstance());
				}
			}
			queryMap.put(sessionQueryTag, new QueryContainer(query, qcVersion));
		}
		else
		{
			query = qc.query;
		}
		if (columnSetting != null)
		{
			String[] colSetting = columnSetting.getColumnSetting(columnType, query, search, isFirst, data, conn);
			if (colSetting != null)
			{
				ResultReaderManager readerManager = query.getReaderManager();
				readerManager.setReaderList(colSetting);
				query.setReaderManager(readerManager);
			}
		}
		first.value = isFirst;
		return query;
	}

	private SearchManager getSearchManager0(Map saMap)
			throws ConfigurationException
	{
		Map managerMap = (Map) SessionCache.getInstance().getProperty(saMap, SESSION_SEARCH_MANAGER);
		if (managerMap == null)
		{
			managerMap = new HashMap();
			SessionCache.getInstance().setProperty(saMap, SESSION_SEARCH_MANAGER, managerMap);
		}
		SearchManager manager = (SearchManager) managerMap.get(this.searchManagerName);
		if (manager == null)
		{
			manager = this.getFactory().createSearchManager();
			managerMap.put(this.searchManagerName, manager);
		}
		return manager;
	}

	private ConditionProperty[] getConditionPropertys0()
			throws ConfigurationException
	{
		if (this.allConditionProperties != null)
		{
			return this.allConditionProperties;
		}
		OrderManager om = new OrderManager();
		List resultList = om.getOrder(new MyOrderItem(), this.parents, this.conditionPropertyOrder,
				this.conditionProperties, this.conditionPropertyMap);
		this.allConditionProperties = (ConditionProperty[]) resultList.toArray(new ConditionProperty[0]);
		return this.allConditionProperties;
	}

	private ConditionProperty[] getConditionPropertysWithOther()
			throws ConfigurationException
	{
		if (this.allConditionPropertiesWithOther != null)
		{
			return this.allConditionPropertiesWithOther;
		}
		OrderManager om = new OrderManager("other");
		Map temp = new HashMap(this.conditionPropertyMap);
		List resultList = om.getOrder(new MyOrderItem(), this.others, this.conditionPropertyOrderWithOther,
				this.conditionProperties, temp);
		this.allConditionPropertiesWithOther = (ConditionProperty[]) resultList.toArray(new ConditionProperty[0]);
		return this.allConditionPropertiesWithOther;
	}

	private static class MyOrderItem extends OrderManager.OrderItem
	{
		private ConditionProperty cp;

		public MyOrderItem()
		{
			super("", null);
		}

		protected MyOrderItem(String name, Object obj)
		{
			super(name, obj);
			this.cp = (ConditionProperty) obj;
		}

		public boolean isIgnore()
				throws ConfigurationException
		{
			return this.cp.isIgnore();
		}

		public OrderManager.OrderItem create(Object obj)
				throws ConfigurationException
		{
			if (obj == null)
			{
				return null;
			}
			ConditionProperty cp = (ConditionProperty) obj;
			return new MyOrderItem(cp.getName(), cp);
		}

		public Iterator getOrderItemIterator(Object container)
				throws ConfigurationException
		{
			SearchAdapter search = (SearchAdapter) container;
			return new MyIterator(search);
		}

	}

	private static class MyIterator
			implements Iterator
	{
		private int index = 0;
		private int count;
		SearchAdapter search;

		public MyIterator(SearchAdapter search)
				throws ConfigurationException
		{
			this.search = search;
			this.count = search.getConditionPropertyCount();
		}

		public boolean hasNext()
		{
			return this.index < this.count;
		}

		public Object next()
		{
			try
			{
				return this.search.getConditionProperty(this.index++);
			}
			catch (ConfigurationException ex)
			{
				log.error("Search my iterator next.", ex);
				throw new UnsupportedOperationException(ex.getMessage());
			}
		}

		public void remove()
		{
			throw new UnsupportedOperationException();
		}

	}

	protected static class QueryContainer
	{
		public final QueryAdapter query;
		public final int conditionVersion;

		public QueryContainer(QueryAdapter query, int conditionVersion)
		{
			this.query = query;
			this.conditionVersion = conditionVersion;
		}

	}

}

