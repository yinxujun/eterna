
package self.micromagic.eterna.search;

import java.io.Reader;
import java.io.Writer;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.security.Permission;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.sql.ResultIterator;
import self.micromagic.eterna.view.DataPrinter;

public interface SearchAdapter
{
   static final String SESSION_SEARCH_MANAGER = "ETERNA_SESSION_SEARCH_MANAGER";
   static final String SESSION_SEARCH_QUERY = "ETERNA_SESSION_SEARCH_QUERY";

   /**
    * ���õ�������Ĳ�����. <p>
    * ������ʽΪ: [searchName]/order��
    */
   static final String SINGLE_ORDER_SUFIX = ".order";

   /**
    * ����Ĭ��ÿҳ��������������. <p>
    * ���������а����·�������:
    * <search>
    *    <attributes>
    *       <attribute name="SearchAdapter.Attribute.pageSize" value="10"/>
    *    </attributes>
    * </search>
    */
   static final String PAGE_SIZE_ATTRIBUTE = "SearchAdapter.Attribute.pageSize";

   /**
    * ���ڱ�־�Ƿ�Ҫǿ�ƶ�ȡ������. <p>
    * ���������õ���Ϣ��������, ������Ҫǿ�ƶ�ȡ��������Ϣ�Ļ�, ���
    * �ڵ���ǰ�����·�������:
    * request.setAttribute(SearchAdapter.FORCE_LOAD_COLUMN_SETTING, "1");
    * ����, �����Ҫ�������õı�־ȥ��, ����ʹ�����·���:
    * request.removeAttribute(SearchManager.FORCE_LOAD_COLUMN_SETTING);
    *
    * ע: ʹ�ô˱�־ǿ�ƶ�ȡ�������ò��ᱻ����, Ҳ����˵�´�ǿ�ƶ�ȡ��ʾδ
    *     ���õĻ�, ��ȡ����������Ȼ��ǰһ�λ����ֵ
    */
   static final String FORCE_LOAD_COLUMN_SETTING = "ETERNA_FORCE_LOAD_COLUMN_SETTING";

   /**
    * ���ڱ�־�Ƿ�Ҫ��ȡ���еļ�¼. <p>
    * ���ڲ�ѯģ���������˷�ҳ����, �����Ҫ��ȡ���м�¼�Ļ�, ���
    * �ڵ���ǰ�����·�������:
    * request.setAttribute(SearchAdapter.READ_ALL_ROW, "1");
    * ����, �����Ҫ�������õı�־ȥ��, ����ʹ�����·���:
    * request.removeAttribute(SearchManager.READ_ALL_ROW);
    */
   static final String READ_ALL_ROW = "ETERNA_READ_ALL_ROW";

   /**
    * ���ڱ�־��ȡ�ļ�¼��. <p>
    * ���ڲ�ѯģ���������˷�ҳ����, �������������˶�ȡ�ļ�¼��, �����Ҫ
    * ���ö�ȡ����ʼ��¼�Ͷ�ȡ�ļ�¼��������ڵ���ǰ�����·�������:
    * request.setAttribute(SearchAdapter.READ_ROW_START_AND_COUNT, new StartAndCount(start, count));
    * ����, �����Ҫ�������õı�־ȥ��, ����ʹ�����·���:
    * request.removeAttribute(SearchManager.READ_ROW_START_AND_COUNT);
    */
   static final String READ_ROW_START_AND_COUNT = "ETERNA_READ_ROW_START_AND_COUNT";

   /**
    * ���ڱ�־�Ƿ�Ҫ��ȡ���еļ�¼. <p>
    * ���ڲ�ѯģ���������˷�ҳ����, ���Զ�ȡ���������ݻ���ȡ����. ����, ����
    * ���������ݵĻ�, ������ȡ�����ķ�ʽ��ռ�ô������ڴ�, ���Կ��Բ��ñ���
    * ���ӵķ�ʽ, ��Ҫ���õĻ����ڵ���ǰ�����·�������:
    * request.setAttribute(SearchAdapter.HOLD_CONNECTION, "1");
    * ����, �����Ҫ�������õı�־ȥ��, ����ʹ�����·���:
    * request.removeAttribute(SearchManager.HOLD_CONNECTION);
    */
   static final String HOLD_CONNECTION = "ETERNA_HODE_CONNECTION";

   String getName() throws ConfigurationException;

   EternaFactory getFactory() throws ConfigurationException;

   Object getAttribute(String name) throws ConfigurationException;

   String[] getAttributeNames() throws ConfigurationException;

   String getOtherSearchManagerName() throws ConfigurationException;

	/**
	 * ��ȡ������������������������search, ���ڷֲ�ʽ������ѯ��ʱ��ʹ��.
	 */
	SearchAdapter[] getOtherSearchs() throws ConfigurationException;

   String getConditionPropertyOrderWithOther() throws ConfigurationException;

   /**
    * ��ù��������ѯ���������˵����XML�ĵ�.
    */
   Reader getConditionDocument(Permission permission) throws ConfigurationException;

   /**
    * �Ƿ������������, ��Ҫ���¹������������..
    */
   boolean isSpecialCondition() throws ConfigurationException;

   /**
    * �ж��Ƿ���Ҫ�����������������"(", ")".
    */
   boolean isNeedWrap() throws ConfigurationException;

   /**
    * ���ColumnSetting������, �������ֶ�ȡ�ĸ�ColumnSetting.
    */
   String getColumnSettingType() throws ConfigurationException;

   /**
    * ������õ�ColumnSetting, SearchAdapter�����������ò�ѯ����.
    */
   ColumnSetting getColumnSetting() throws ConfigurationException;

   /**
    * ��ð󶨵Ĳ���������<code>ParameterSetting</code>.
    *
    * @return  ���δ���򷵻�null, ����Ѱ��򷵻ز���������
    */
   ParameterSetting getParameterSetting() throws ConfigurationException;

   String getParentConditionPropretyName() throws ConfigurationException;

   String getConditionPropertyOrder() throws ConfigurationException;

   int getConditionPropertyCount() throws ConfigurationException;

   ConditionProperty getConditionProperty(int colId) throws ConfigurationException;

   ConditionProperty getConditionProperty(String name) throws ConfigurationException;

   int getConditionIndex() throws ConfigurationException;

   int getPageSize() throws ConfigurationException;

   String getSearchManagerName() throws ConfigurationException;

   /**
    * ���һ��SearchManager.
    *
    * @param data   ����, ���������request��parameter, request��attribute,
    *               session��attritute
    */
   SearchManager getSearchManager(AppData data) throws ConfigurationException;

   /**
    * ִ�в�ѯ, ����ý��.
    *
    * @param data   ����, ���������request��parameter, request��attribute,
    *               session��attritute
	 * @param conn   ���ݿ�����
    */
   Result doSearch(AppData data, Connection conn) throws ConfigurationException, SQLException;

	/**
	 * �����Ľ��.
	 */
   static final class Result
			implements DataPrinter.BeanPrinter
   {
		/**
		 * ��ҳ�ĳߴ�.
		 */
      public final int pageSize;

		/**
		 * ��ǰ��ҳ��.
		 * ��0��ʼ, ��һҳΪ0 �ڶ�ҳΪ1 ...
		 */
      public final int pageNum;

		/**
		 * ʹ�õ�search���������.
		 */
      public final String searchName;

		/**
		 * ʹ�õ�query���������.
		 */
      public final String queryName;

		/**
		 * �����Ľ����.
		 */
      public final ResultIterator queryResult;

		/**
		 * ����ͳ�ƵĽ����.
		 */
      public final ResultIterator searchCount;

		/**
		 * ���õĵ��������reader����.
		 */
      public final String singleOrderName;

		/**
		 * ���������Ƿ�Ϊ����.
		 */
      public final boolean singleOrderDesc;

      public Result(String searchName, String queryName, ResultIterator queryResult, ResultIterator searchCount,
            int pageSize, int pageNum, String singleOrderName, boolean singleOrderDesc)
      {
         this.pageSize = pageSize;
         this.pageNum = pageNum;
         this.searchName = searchName;
         this.queryName = queryName;
         this.queryResult = queryResult;
         this.searchCount = searchCount;
         this.singleOrderName = singleOrderName;
         this.singleOrderDesc = singleOrderDesc;
      }

		public Result(Result old, String queryName, ResultIterator queryResult)
		{
         this.pageSize = old.pageSize;
         this.pageNum = old.pageNum;
         this.searchName = old.searchName;
         this.queryName = queryName;
         this.queryResult = queryResult;
         this.searchCount = old.searchCount;
         this.singleOrderName = old.singleOrderName;
         this.singleOrderDesc = old.singleOrderDesc;
		}

		public void print(DataPrinter p, Writer out, Object bean)
				throws IOException, ConfigurationException
		{
			try
			{
				p.printObjectBegin(out);
				p.printResultIterator(out, this.queryResult);
				p.printPair(out, "pageNum", this.pageNum, false);
				p.printPair(out, "pageSize", this.pageSize, false);
				p.printPair(out, "searchName", this.searchName, false);
				if (this.queryResult.isRealRecordCountAvailable())
				{
					p.printPair(out, "totalCount", this.queryResult.getRealRecordCount(), false);
				}
				if (this.singleOrderName != null)
				{
					p.printPair(out, "orderName", this.singleOrderName, false);
					p.printPair(out, "orderDesc", this.singleOrderDesc ? 1 : 0, false);
				}
				p.printPair(out, "hasNextPage", this.queryResult.isHasMoreRecord() ? 1 : 0, false);
				p.printObjectEnd(out);
			}
			catch (SQLException ex)
			{
				throw new ConfigurationException(ex);
			}
		}

   }

	/**
	 * ��ѯ����ʼֵ����ȡ�ļ�¼��.
	 */
   static final class StartAndCount
   {
		/**
		 * ��ѯ����ʼֵ.
		 */
      public final int start;

		/**
		 * ��ȡ�ļ�¼��.
		 */
      public final int count;

      public StartAndCount(int start, int count)
      {
         this.start = start;
         this.count = count;
      }

   }

}
