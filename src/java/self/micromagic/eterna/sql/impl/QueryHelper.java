
package self.micromagic.eterna.sql.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.sql.QueryAdapter;
import self.micromagic.util.StringAppender;
import self.micromagic.util.StringTool;
import self.micromagic.util.Utility;

/**
 * ��ѯִ�еĸ�������.
 * 1 ���ڴ������¼�����ƵĲ�ѯ��������.
 * 2 ��ȡ��Ӧ�ļ�¼.
 */
public class QueryHelper
{
	/**
	 * mysql���ݿ�����.
	 */
	public static final String DB_NAME_MYSQL = "MySQL";

	/**
	 * oracle���ݿ�����.
	 */
	public static final String DB_NAME_ORACLE = "Oracle";

	/**
	 * ������ͨ���ݿ�����
	 */
	public static final String DB_NAME_COMMON = "Common";

	/**
	 * ��ȡһ����ѯ�������ߵ�ʵ��.
	 *
	 * @param query      ��ѯ�Ķ���, ���ڹ����ѯ��������
	 * @param conn       ���ݿ�����, ���ڻ�ȡ���ݿ������
	 * @param oldHelper  �ɵĲ�ѯ��������, �����ǰ���ݿ����ͺ;ɵĲ�ѯ�������ߵ�������ͬ,
	 *                   �򷵻�����ɵĲ�ѯ��������
	 */
	public static QueryHelper getInstance(QueryAdapter query, Connection conn, QueryHelper oldHelper)
			throws SQLException
	{
		String dbName = conn.getMetaData().getDatabaseProductName();
		if (DB_NAME_ORACLE.equals(dbName))
		{
			return oldHelper != null && DB_NAME_ORACLE.equals(oldHelper.getType()) ?
			 		oldHelper : new OracleQueryHelper(query);
		}
		else if (DB_NAME_MYSQL.equals(dbName))
		{
			return oldHelper != null && DB_NAME_MYSQL.equals(oldHelper.getType()) ?
			 		oldHelper : new MySqlQueryHelper(query);
		}
		return oldHelper != null && DB_NAME_COMMON.equals(oldHelper.getType()) ?
				oldHelper : new QueryHelper(query);
	}


	private QueryAdapter query;

	/**
	 * ���캯��.
	 *
	 * @param query    ִ�в�ѯ�Ķ���
	 */
	public QueryHelper(QueryAdapter query)
	{
		this.query = query;
	}

	/**
	 * ��ȡִ�в�ѯ�Ķ���.
	 */
	protected QueryAdapter getQueryAdapter()
	{
		return this.query;
	}

	/**
	 * ��ȡ��ǰ��ѯ���ߵ�����.
	 */
	public String getType()
	{
		return DB_NAME_COMMON;
	}

	/**
	 * ����ԭʼ���, ���ɴ�����, ����¼�����ƵĲ�ѯ���.
	 */
	public String getQuerySQL(String preparedSQL)
   		throws ConfigurationException
	{
      return preparedSQL;
	}

   /**
    * ��ȡ���β�ѯ�ӵڼ�����¼��ʼ��ȡ, Ĭ��ֵΪ"1".
    */
	public int getStartRow()
   		throws SQLException
	{
		return this.query.getStartRow();
	}

   /**
    * ��ȡ���β�ѯ��ȡ������¼��, Ĭ��ֵΪ"-1", ��ʾȡ��Ϊֹ.
    */
	public int getMaxRows()
   		throws SQLException
	{
		return this.query.getMaxRows();
	}

   /**
    * ��ȡ���β�ѯ���õ��ܼ�¼��.
    */
	public int getTotalCount()
   		throws ConfigurationException
	{
		return this.query.getTotalCount();
	}

   /**
    * ��ȡ�ò�ѯ�������õ��ܼ�¼����չ��Ϣ.
    */
   public QueryAdapter.TotalCountExt getTotalCountExt()
			throws ConfigurationException
	{
		return this.query.getTotalCountExt();
	}

	/**
	 * ��ȡ������е�����.
	 *
	 * @param rs           ����ȡ���ݵĽ����
	 * @param readerList   ����Ҫ��ȡ�����ݵ�ResultReader�����б�
	 * @return     �������ݽ����List����, �����ÿ��ֵΪObject����
	 */
   public List readResults(ResultSet rs, List readerList)
         throws ConfigurationException, SQLException
   {
      int start = this.getStartRow() - 1;
      int tmpRecordCount = 0;
      this.recordCount = 0;
      this.realRecordCount = 0;
      this.realRecordCountAvailable = false;
      this.hasMoreRecord = false;
		this.needCount = false;
      boolean hasRecord = true;
      boolean isForwardOnly = rs.getType() == ResultSet.TYPE_FORWARD_ONLY;

      if (start > 0)
      {
         if (!isForwardOnly)
         {
				// ���ﲻ��Ҫ��1, ��Ϊ��Ҫ��λ��ǰһ��
            hasRecord = rs.absolute(start);
				if (!hasRecord)
				{
					rs.last();
				}
				tmpRecordCount = hasRecord ? rs.getRow() : rs.getRow() + 1;
         }
         else
         {
            for (; tmpRecordCount < start && hasRecord; tmpRecordCount++, hasRecord = rs.next());
         }
      }
      ArrayList result;
      if (!hasRecord)
      {
			int totalCount = this.getTotalCount();
			if (totalCount >= 0)
			{
				this.setTotalCountInfo(totalCount, this.getTotalCountExt());
			}
			else
			{
				// û�м�¼����ʾ�Ѿ��Ƶ������һ��, ��ʱ��¼��-1Ϊ�ܼ�¼��
				this.realRecordCount = tmpRecordCount - 1;
				this.realRecordCountAvailable = true;
				this.hasMoreRecord = false;
			}
         result = new ArrayList(0);
      }
      else
      {
			int maxRows = this.getMaxRows();
         result = new ArrayList(maxRows == -1 ? 32 : maxRows);
         if (maxRows == -1)
         {
            while (rs.next())
            {
               tmpRecordCount++;
               result.add(QueryAdapterImpl.getResults(this.query, readerList, rs));
            }
				int totalCount = this.getTotalCount();
				if (totalCount >= 0)
				{
					this.setTotalCountInfo(totalCount, this.getTotalCountExt());
				}
				else
				{
					this.realRecordCount = tmpRecordCount;
					this.realRecordCountAvailable = true;
					this.hasMoreRecord = false;
				}
         }
         else
         {
            int i = 0;
            for (; i < maxRows && (this.hasMoreRecord = rs.next()); i++)
            {
               tmpRecordCount++;
               result.add(QueryAdapterImpl.getResults(this.query, readerList, rs));
            }
            // ��ô�ж��Ƿ�ֹĳЩjdbc�ڵ�һ��nextΪfalse��, �����next�ֱ��true
            if (this.hasMoreRecord && (this.hasMoreRecord = rs.next()))
            {
               tmpRecordCount++;
               this.realRecordCountAvailable = false;
            }
            else
            {
               this.realRecordCountAvailable = true;
            }

				int totalCount = this.getTotalCount();
				if (totalCount == QueryAdapter.TOTAL_COUNT_AUTO)
				{
					if (!isForwardOnly)
					{
						rs.last();
						this.realRecordCount = rs.getRow();
					}
					else
					{
						if (this.hasMoreRecord)
						{
							for (; rs.next(); tmpRecordCount++);
						}
						this.realRecordCount = tmpRecordCount;
					}
					this.realRecordCountAvailable = true;
				}
				else if (totalCount == QueryAdapter.TOTAL_COUNT_NONE)
				{
					this.realRecordCount = tmpRecordCount;
				}
				else if (totalCount == QueryAdapter.TOTAL_COUNT_COUNT)
				{
					if (!this.realRecordCountAvailable)
					{
						this.needCount = true;
					}
					else
					{
						this.realRecordCount = tmpRecordCount;
					}
				}
				else if (totalCount >= 0)
				{
					this.setTotalCountInfo(totalCount, this.getTotalCountExt());
				}
         }
      }

      this.recordCount = result.size();
      return result;
   }

	/**
	 * ��totalCountΪ0-Nʱ�����ܼ�¼������Ϣ.
	 */
	protected void setTotalCountInfo(int totalCount, QueryAdapter.TotalCountExt ext)
	{
		this.realRecordCount = totalCount;
		this.realRecordCountAvailable = true;
		if (ext != null)
		{
			this.hasMoreRecord = ext.hasMoreRecord;
			this.realRecordCountAvailable = ext.realRecordCountAvailable;
		}
	}

	protected int recordCount;
   protected int realRecordCount;
   protected boolean realRecordCountAvailable;
   protected boolean hasMoreRecord;
	protected boolean needCount;

	/**
	 * ���ζ�ȡ����еļ�¼��.
	 */
	public int getRecordCount()
	{
		return this.recordCount;
	}

	/**
	 * ʵ�ʲ�ѯ����е��ܼ�¼��.
	 */
	public int getRealRecordCount()
	{
		return this.realRecordCount;
	}

	/**
	 * �ܼ�¼���е�ֵ�Ƿ���Ч.
	 */
	public boolean isRealRecordCountAvailable()
	{
		return this.realRecordCountAvailable;
	}

	/**
	 * ʵ�ʲ�ѯ������Ƿ��и���ļ�¼.
	 */
	public boolean isHasMoreRecord()
	{
		return this.hasMoreRecord;
	}

	/**
	 * �Ƿ���Ҫͨ��������ѯ��ȡ�ܼ�¼��.
	 */
	public boolean needCount()
	{
		return this.needCount;
	}

	static abstract class SpecialQueryHelper extends QueryHelper
	{
		protected int nowStartRow = 1;
		protected int nowMaxRows = -1;
		protected int nowTotalCount = QueryAdapter.TOTAL_COUNT_NONE;
		protected QueryAdapter.TotalCountExt nowTotalCountExt;
		protected String oldPreparedSQL;
		protected String cacheSQL;
		protected boolean useOldSQL;

		public SpecialQueryHelper(QueryAdapter query)
		{
			super(query);
		}

		/**
		 * ������������ڷ�ҳ��SQL���.
		 */
		protected abstract String createSpecialSQL(String preparedSQL);

		public String getQuerySQL(String preparedSQL)
				throws ConfigurationException
		{
			QueryAdapter query = this.getQueryAdapter();
			if (this.cacheSQL != null)
			{
				try
				{
					if (this.oldPreparedSQL != preparedSQL || this.nowStartRow != query.getStartRow()
							|| this.nowMaxRows != query.getMaxRows() || this.nowTotalCount != query.getTotalCount()
							|| !Utility.objectEquals(this.nowTotalCountExt, query.getTotalCountExt()))
					{
						this.cacheSQL = null;
					}
				}
				catch (SQLException ex)
				{
					throw new ConfigurationException(ex);
				}
			}
			if (this.cacheSQL == null)
			{
				this.oldPreparedSQL = preparedSQL;
				try
				{
					this.nowStartRow = query.getStartRow();
					this.nowMaxRows = query.getMaxRows();
					this.nowTotalCount = query.getTotalCount();
					this.nowTotalCountExt = query.getTotalCountExt();
				}
				catch (SQLException ex)
				{
					throw new ConfigurationException(ex);
				}
				this.useOldSQL = (this.nowMaxRows == -1 && this.nowStartRow == 1)
						|| this.nowTotalCount == QueryAdapter.TOTAL_COUNT_AUTO;
				if (this.useOldSQL)
				{
					this.cacheSQL = preparedSQL;
				}
				else
				{
					this.cacheSQL = this.createSpecialSQL(preparedSQL);
				}
			}
			return this.cacheSQL;
		}

		public List readResults(ResultSet rs, List readerList)
				throws ConfigurationException, SQLException
		{
			if (this.useOldSQL)
			{
				return super.readResults(rs, readerList);
			}
			int tmpRecordCount = 0;
			this.recordCount = 0;
			this.realRecordCount = 0;
			this.realRecordCountAvailable = false;
			this.hasMoreRecord = false;
			this.needCount = false;

			QueryAdapter query = this.getQueryAdapter();
			ArrayList result = new ArrayList(this.nowMaxRows == -1 ? 32 : this.nowMaxRows);
			if (this.nowMaxRows == -1)
			{
				while (rs.next())
				{
					tmpRecordCount++;
					result.add(QueryAdapterImpl.getResults(query, readerList, rs));
				}
				int totalCount = this.nowTotalCount;
				if (totalCount >= 0)
				{
					this.setTotalCountInfo(totalCount, this.nowTotalCountExt);
				}
				else
				{
					if (tmpRecordCount > 0)
					{
						this.realRecordCount = tmpRecordCount += this.nowStartRow - 1;
						this.realRecordCountAvailable = true;
					}
					this.hasMoreRecord = false;
				}
			}
			else
			{
				int i = 0;
				for (; i < this.nowMaxRows && (this.hasMoreRecord = rs.next()); i++)
				{
					tmpRecordCount++;
					result.add(QueryAdapterImpl.getResults(query, readerList, rs));
				}
				// ��ô�ж��Ƿ�ֹĳЩjdbc�ڵ�һ��nextΪfalse��, �����next�ֱ��true
				if (this.hasMoreRecord && (this.hasMoreRecord = rs.next()))
				{
					tmpRecordCount += this.nowStartRow;
					this.realRecordCountAvailable = false;
				}
				else if (tmpRecordCount > 0)
				{
            	this.realRecordCount = tmpRecordCount += this.nowStartRow - 1;
					this.realRecordCountAvailable = true;
				}

				int totalCount = this.nowTotalCount;
				if (totalCount == QueryAdapter.TOTAL_COUNT_NONE)
				{
					this.realRecordCount = tmpRecordCount;
				}
				else if (totalCount == QueryAdapter.TOTAL_COUNT_COUNT)
				{
					if (!this.realRecordCountAvailable)
					{
						this.needCount = true;
					}
					else
					{
						this.realRecordCount = tmpRecordCount;
					}
				}
				else if (totalCount >= 0)
				{
					this.setTotalCountInfo(totalCount, this.nowTotalCountExt);
				}
			}

			this.recordCount = result.size();
			return result;
		}

	}

	static class OracleQueryHelper extends SpecialQueryHelper
	{
		public OracleQueryHelper(QueryAdapter query)
		{
			super(query);
		}

		/**
		 * ��ȡ��ǰ��ѯ���ߵ�����.
		 */
		public String getType()
		{
			return DB_NAME_ORACLE;
		}

		protected String createSpecialSQL(String preparedSQL)
		{
			if (this.nowStartRow == 1)
			{
				String part1 = "select * from (";
				String part2 = ") tmpTable where rownum <= " + (this.nowMaxRows + 1);
				StringAppender buf = StringTool.createStringAppender(
						part1.length() + part2.length() + preparedSQL.length());
				buf.append(part1).append(preparedSQL).append(part2);
				return buf.toString();
			}
			else
			{
				String condition1 = this.nowMaxRows == -1 ? ""
						: " where rownum <= " + (this.nowMaxRows + this.nowStartRow);
				String condition2 = " where theOracleRuwNum >= " + this.nowStartRow;
				String part1 = "select * from (select tmpTable1.*, rownum as theOracleRuwNum from (";
				String part2 = ") tmpTable1" + condition1 + ") tmpTable2" + condition2;
				StringAppender buf = StringTool.createStringAppender(
						part1.length() + part2.length() + preparedSQL.length());
				buf.append(part1).append(preparedSQL).append(part2);
				return buf.toString();
			}
		}

	}

	static class MySqlQueryHelper extends SpecialQueryHelper
	{
		public MySqlQueryHelper(QueryAdapter query)
		{
			super(query);
		}

		/**
		 * ��ȡ��ǰ��ѯ���ߵ�����.
		 */
		public String getType()
		{
			return DB_NAME_MYSQL;
		}

		protected String createSpecialSQL(String preparedSQL)
		{
			if (this.nowStartRow == 1)
			{
				String appendStr = " limit " + (this.nowMaxRows + 1);
				return preparedSQL + appendStr;
			}
			else
			{
				int count = this.nowMaxRows == -1 ? Integer.MAX_VALUE : this.nowMaxRows + 1;
				String appendStr = " limit " + (this.nowStartRow - 1) + ", " + count;
				return preparedSQL + appendStr;
			}
		}

	}

}