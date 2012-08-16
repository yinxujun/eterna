
package self.micromagic.eterna.search;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.search.impl.SearchAdapterImpl;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.share.TypeManager;
import self.micromagic.eterna.sql.QueryAdapter;
import self.micromagic.eterna.sql.ResultIterator;
import self.micromagic.eterna.sql.ResultReaderManager;
import self.micromagic.eterna.sql.ResultRow;
import self.micromagic.eterna.sql.preparer.PreparerManager;
import self.micromagic.eterna.sql.preparer.ValuePreparer;
import self.micromagic.eterna.sql.preparer.ValuePreparerCreater;
import self.micromagic.util.BooleanRef;
import self.micromagic.util.StringAppender;
import self.micromagic.util.StringTool;

/**
 * ���β�ѯ������.
 * ��һ�β�ѯ, ȡ�����е�����; �ڶ��β�ѯ, ����ǰһ�β�ѯ
 * ������ȡ����ʽ�Ľ��. <p>
 *
 * �����õ���������:
 *
 * nextQueryName(��ѡ)             �ڶ��β�ѯ��query����
 *
 * keyConditionIndex               �������query������key����������ֵ, Ĭ��ֵΪ1
 *
 * keyNameList(��ѡ)               ���������б�, ��ʽΪ  name1,name2,... �� name1:col1,name2,...
 *
 * assistSearchName                ������search����, ������������������, ��ʹ��$same��ʾʹ�ñ�search
 *
 * needAssistCondition             �Ƿ���Ҫ����search������, ��assistSearchNameΪ$sameʱĬ��ֵΪfalse
 *                                 ��assistSearchNameΪ����search������ʱĬ��ֵΪtrue
 */
public class DoubleQuerySearch extends SearchAdapterImpl
		implements SearchAdapter, SearchAdapterGenerator
{
   private String sessionNextQueryTag;
	private int nextQueryIndex;
	private int keyConditionIndex = 1;
	private int[] keyIndexs;
	private String[] colNames;
	private ValuePreparerCreater[] vpcs;
	private int assistSearchIndex = -1;
	private boolean sameSearch = false;
	private boolean needAssistCondition = false;

	/**
	 * ÿ��������Ԫ��ռ���ַ���.
	 */
	private int conditionItemSize = 0;

   private boolean initialized = false;

	public void initialize(EternaFactory factory)
			throws ConfigurationException
	{
      if (this.initialized)
      {
         return;
      }
      this.initialized = true;
		super.initialize(factory);
		String tmp;

		tmp = (String) this.getAttribute("nextQueryName");
		if (tmp == null)
		{
			throw new ConfigurationException("Not found attribute [nextQueryName].");
		}
		this.sessionNextQueryTag = "q:" + tmp + ":" + factory.getFactoryManager().getId();
		this.nextQueryIndex = factory.getQueryAdapterId(tmp);
		tmp = (String) this.getAttribute("keyConditionIndex");
		if (tmp != null)
		{
			this.keyConditionIndex = Integer.parseInt(tmp);
		}

		tmp = (String) this.getAttribute("keyNameList");
		if (tmp == null)
		{
			throw new ConfigurationException("Not found attribute [keyNameList].");
		}
		String[] keyList = StringTool.separateString(tmp, ",", true);
		QueryAdapter keyQuery = factory.createQueryAdapter(this.getQueryName());
		ResultReaderManager keyReaders = keyQuery.getReaderManager();
		this.keyIndexs = new int[keyList.length];
		this.colNames = new String[keyList.length];
		this.vpcs = new ValuePreparerCreater[keyList.length];
		for (int i = 0; i < keyList.length; i++)
		{
			String str = keyList[i];
			int tmpI = str.indexOf(':');
			String keyN, colN;
			if (tmpI == -1)
			{
				keyN = colN = str;
			}
			else
			{
				keyN = str.substring(0, tmpI);
				colN = str.substring(tmpI + 1);
			}
			this.keyIndexs[i] = keyReaders.getIndexByName(keyN);
			this.vpcs[i] = factory.createValuePreparerCreater(
					TypeManager.getPureType(keyReaders.getReader(keyN).getType()));
			this.colNames[i] = colN;
			this.conditionItemSize += colN.length() + 4;
			if (i > 1)
			{
				this.conditionItemSize += 10;
			}
		}

		tmp = (String) this.getAttribute("assistSearchName");
		if (tmp != null)
		{
			if ("$same".equals(tmp))
			{
				this.sameSearch = true;
			}
			else
			{
				this.assistSearchIndex = factory.getSearchAdapterId(tmp);
				this.needAssistCondition = true;
			}
		}
		tmp = (String) this.getAttribute("needAssistCondition");
		if (tmp != null)
		{
			this.needAssistCondition = "true".equalsIgnoreCase(tmp);
		}
	}

	public Result doSearch(AppData data, Connection conn)
			throws ConfigurationException, SQLException
	{
		Result result = super.doSearch(data, conn);
		ResultIterator ritr = result.queryResult;
		QueryAdapter nextQuery;
		if (this.sameSearch || this.assistSearchIndex != -1)
		{
			SearchAdapter assistSearch = this.sameSearch ? this
					: this.getFactory().createSearchAdapter(this.assistSearchIndex);
			BooleanRef first = new BooleanRef();
			SearchManager manager = this.getSearchManager(data);
			nextQuery = getQueryAdapter(data, conn, assistSearch, first, this.sessionNextQueryTag, manager,
					this.nextQueryIndex, assistSearch.getColumnSetting(), assistSearch.getColumnSettingType());
			if (this.needAssistCondition)
			{
				if (assistSearch.getOtherSearchs() != null)
				{
					dealOthers(data, conn, assistSearch.getOtherSearchs(), nextQuery, first.value);
				}
				if (assistSearch.getConditionIndex() > 0)
				{
					if (assistSearch.isSpecialCondition())
					{
						String subConSQL = manager.getSpecialConditionPart(assistSearch, assistSearch.isNeedWrap());
						PreparerManager spm = manager.getSpecialPreparerManager(assistSearch);
						nextQuery.setSubSQL(assistSearch.getConditionIndex(), subConSQL, spm);
					}
					else
					{
						nextQuery.setSubSQL(assistSearch.getConditionIndex(),
								manager.getConditionPart(assistSearch.isNeedWrap()), manager.getPreparerManager());
					}
				}
			}
			if (assistSearch.getParameterSetting() != null)
			{
				assistSearch.getParameterSetting().setParameter(nextQuery, this, first.value, data, conn);
			}
		}
		else
		{
			nextQuery = this.getFactory().createQueryAdapter(this.nextQueryIndex);
		}
		if (nextQuery.canOrder())
		{
			if (result.singleOrderName != null)
			{
				nextQuery.setSingleOrder(result.singleOrderName, result.singleOrderDesc ? -1 : 1);
			}
		}
		nextQuery.setTotalCount(ritr.getRealRecordCount(),
				new QueryAdapter.TotalCountExt(ritr.isHasMoreRecord(), ritr.isRealRecordCountAvailable()));
		this.setNextQueryCondition(ritr, nextQuery);
		return new Result(result, nextQuery.getName(), nextQuery.executeQuery(conn));
	}

	/**
	 * ��ȡִ�еڶ��β�ѯ��query����.
	 */
	private void setNextQueryCondition(ResultIterator keyIterator, QueryAdapter nextQuery)
			throws ConfigurationException, SQLException
	{
		boolean multiKey = this.keyIndexs.length > 1;
		if (keyIterator.getRecordCount() == 0)
		{
			nextQuery.setSubSQL(this.keyConditionIndex, "(" + this.colNames[0] + " = null)");
		}
		else
		{
			StringAppender buf = StringTool.createStringAppender(
					this.conditionItemSize * keyIterator.getRecordCount());
			List vpList = new LinkedList();
			int vpIndex = 0;
			buf.append('(');
			while (keyIterator.hasMoreRow())
			{
				if (multiKey)
				{
					buf.append('(');
				}
				ResultRow row = keyIterator.nextRow();
				for (int i = 0; i < this.keyIndexs.length; i++)
				{
					if (i > 0)
					{
						buf.append(" AND ");
					}
					int keyIndex = this.keyIndexs[i];
					Object keyValue = row.getObject(keyIndex);
					if (keyValue == null)
					{
						buf.append(this.colNames[i]).append(" is null");
					}
					else
					{
						ValuePreparer vp = this.vpcs[i].createPreparer(keyValue);
						vp.setRelativeIndex(++vpIndex);
						vpList.add(vp);
						buf.append(this.colNames[i]).append(" = ?");
					}
				}
				if (multiKey)
				{
					buf.append(')');
				}
				if (keyIterator.hasMoreRow())
				{
					buf.append(" OR ");
				}
			}
			buf.append(')');
			if (vpList.size() > 0)
			{
				PreparerManager pm = new PreparerManager(vpList.size());
				for (Iterator iterator = vpList.iterator(); iterator.hasNext();)
				{
					pm.setValuePreparer((ValuePreparer) iterator.next());
				}
				nextQuery.setSubSQL(this.keyConditionIndex, buf.toString(), pm);
			}
			else
			{
				nextQuery.setSubSQL(this.keyConditionIndex, buf.toString());
			}
		}
	}

}
