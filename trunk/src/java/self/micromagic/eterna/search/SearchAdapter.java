
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
   public final static String SESSION_SEARCH_MANAGER = "ETERNA_SESSION_SEARCH_MANAGER";
   public final static String SESSION_SEARCH_QUERY = "ETERNA_SESSION_SEARCH_QUERY";

   /**
    * ���õ�������Ĳ�����. <p>
    * ������ʽΪ: [searchName]/order��
    */
   public final static String SINGLE_ORDER_SUFIX = ".order";

   /**
    * ����Ĭ��ÿҳ��������������. <p>
    * ���������а����·�������:
    * <search>
    *    <attributes>
    *       <attribute name="SearchAdapter.Attribute.pageSize" value="10"/>
    *    </attributes>
    * </search>
    */
   public final static String PAGE_SIZE_ATTRIBUTE = "SearchAdapter.Attribute.pageSize";

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
   public final static String FORCE_LOAD_COLUMN_SETTING = "ETERNA_FORCE_LOAD_COLUMN_SETTING";

   /**
    * ���ڱ�־�Ƿ�Ҫ��ȡ���еļ�¼. <p>
    * ���ڲ�ѯģ���������˷�ҳ����, �����Ҫ��ȡ���м�¼�Ļ�, ���
    * �ڵ���ǰ�����·�������:
    * request.setAttribute(SearchAdapter.READ_ALL_ROW, "1");
    * ����, �����Ҫ�������õı�־ȥ��, ����ʹ�����·���:
    * request.removeAttribute(SearchManager.READ_ALL_ROW);
    */
   public final static String READ_ALL_ROW = "ETERNA_READ_ALL_ROW";

   /**
    * ���ڱ�־��ȡ�ļ�¼��. <p>
    * ���ڲ�ѯģ���������˷�ҳ����, �������������˶�ȡ�ļ�¼��, �����Ҫ
    * ���ö�ȡ����ʼ��¼�Ͷ�ȡ�ļ�¼��������ڵ���ǰ�����·�������:
    * request.setAttribute(SearchAdapter.READ_ROW_START_AND_COUNT, new StartAndCount(start, count));
    * ����, �����Ҫ�������õı�־ȥ��, ����ʹ�����·���:
    * request.removeAttribute(SearchManager.READ_ROW_START_AND_COUNT);
    */
   public final static String READ_ROW_START_AND_COUNT = "ETERNA_READ_ROW_START_AND_COUNT";

   /**
    * ���ڱ�־�Ƿ�Ҫ��ȡ���еļ�¼. <p>
    * ���ڲ�ѯģ���������˷�ҳ����, ���Զ�ȡ���������ݻ���ȡ����. ����, ����
    * ���������ݵĻ�, ������ȡ�����ķ�ʽ��ռ�ô������ڴ�, ���Կ��Բ��ñ���
    * ���ӵķ�ʽ, ��Ҫ���õĻ����ڵ���ǰ�����·�������:
    * request.setAttribute(SearchAdapter.HOLD_CONNECTION, "1");
    * ����, �����Ҫ�������õı�־ȥ��, ����ʹ�����·���:
    * request.removeAttribute(SearchManager.HOLD_CONNECTION);
    */
   public final static String HOLD_CONNECTION = "ETERNA_HODE_CONNECTION";

   String getName() throws ConfigurationException;

   EternaFactory getFactory() throws ConfigurationException;

   Object getAttribute(String name) throws ConfigurationException;

   String[] getAttributeNames() throws ConfigurationException;

   String getOtherSearchManagerName() throws ConfigurationException;

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
   SearchManager getSearchManager(AppData data)
         throws ConfigurationException;

   /**
    * ִ�в�ѯ, ����ý��.
    *
    * @param data   ����, ���������request��parameter, request��attribute,
    *               session��attritute
    */
   Result doSearch(AppData data, Connection conn)
         throws ConfigurationException, SQLException;

   public static final class Result
			implements DataPrinter.BeanPrinter
   {
      public final int pageSize;
      public final int pageNum;
      public final String searchName;
      public final String queryName;
      public final ResultIterator queryResult;
      public final ResultIterator searchCount;
      public final String singleOrderName;
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

   public static class StartAndCount
   {
      public final int start;
      public final int count;

      public StartAndCount(int start, int count)
      {
         this.start = start;
         this.count = count;
      }
   }

}
