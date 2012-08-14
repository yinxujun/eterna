
package self.micromagic.eterna.sql.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.List;
import java.util.ArrayList;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.sql.QueryAdapter;
import self.micromagic.util.StringAppender;
import self.micromagic.util.StringTool;

/**
 * 查询执行的辅助工具.
 * 1 用于处理带记录数限制的查询语句的生成.
 * 2 获取相应的记录.
 */
public class QueryHelper
{
	/**
	 * 获取一个查询辅助工具的实例.
	 *
	 * @param query      查询的对象, 用于构造查询辅助工具
	 * @param conn       数据库链接, 用于获取数据库的类型
	 * @param oldHelper  旧的查询辅助工具, 如果当前数据库类型和旧的查询辅助工具的类型相同,
	 *                   则返回这个旧的查询辅助工具
	 */
	public static QueryHelper getInstance(QueryAdapter query, Connection conn, QueryHelper oldHelper)
			throws SQLException
	{
		String dbName = conn.getMetaData().getDatabaseProductName();
		if ("Oracle".equals(dbName))
		{
			return oldHelper != null && dbName.equals(oldHelper.getType()) ?
			 		oldHelper : new OracleQueryHelper(query);
		}
		return oldHelper != null && "Common".equals(oldHelper.getType()) ?
				oldHelper : new QueryHelper(query);
	}


	private QueryAdapter query;

	/**
	 * 构造函数.
	 *
	 * @param query    执行查询的对象
	 */
	public QueryHelper(QueryAdapter query)
	{
		this.query = query;
	}

	/**
	 * 获取执行查询的对象.
	 */
	protected QueryAdapter getQueryAdapter()
	{
		return this.query;
	}

	/**
	 * 获取当前查询工具的类型.
	 */
	public String getType()
	{
		return "Common";
	}

	/**
	 * 根据原始语句, 生成处理后的, 带记录数限制的查询语句.
	 */
	public String getQuerySQL(String preparedSQL)
   		throws ConfigurationException
	{
      return preparedSQL;
	}

   /**
    * 获取本次查询从第几条记录开始读取, 默认值为"1".
    */
	public int getStartRow()
   		throws SQLException
	{
		return this.query.getStartRow();
	}

   /**
    * 获取本次查询读取的最大记录数, 默认值为"-1", 表示取完为止.
    */
	public int getMaxRows()
   		throws SQLException
	{
		return this.query.getMaxRows();
	}

   /**
    * 获取本次查询设置的总记录数.
    */
	public int getTotalCount()
   		throws ConfigurationException
	{
		return this.query.getTotalCount();
	}

	/**
	 * 读取结果集中的数据.
	 *
	 * @param rs           被读取数据的结果集
	 * @param readerList   描述要读取的数据的ResultReader对象列表
	 * @return     包含数据结果的List对象, 里面的每个值为Object数组
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
				// 这里不需要加1, 因为需要定位到前一条
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
			// 没有记录数表示已经移到的最后一条, 临时记录数-1为总记录数
         this.realRecordCount = tmpRecordCount - 1;
         this.realRecordCountAvailable = true;
         this.hasMoreRecord = false;
         result = new ArrayList(0);
      }
      else
      {
			int maxRows = this.getMaxRows();
         result = new ArrayList(maxRows == -1 ? 32 : this.getMaxRows());
         if (maxRows == -1)
         {
            while (rs.next())
            {
               tmpRecordCount++;
               result.add(QueryAdapterImpl.getResults(this.query, readerList, rs));
            }
            this.realRecordCount = tmpRecordCount;
            this.realRecordCountAvailable = true;
            this.hasMoreRecord = false;
         }
         else
         {
            int i = 0;
            for (; i < maxRows && (this.hasMoreRecord = rs.next()); i++)
            {
               tmpRecordCount++;
               result.add(QueryAdapterImpl.getResults(this.query, readerList, rs));
            }
            // 这么判断是防止某些jdbc在第一次next为false后, 后面的next又变回true
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
					if (!this.realRecordCountAvailable)
					{
						this.realRecordCount = totalCount;
						this.realRecordCountAvailable = true;
					}
					else
					{
						this.realRecordCount = tmpRecordCount;
					}
				}
         }
      }

      this.recordCount = result.size();
      return result;
   }

	protected int recordCount;
   protected int realRecordCount;
   protected boolean realRecordCountAvailable;
   protected boolean hasMoreRecord;
	protected boolean needCount;

	/**
	 * 本次读取结果中的记录数.
	 */
	public int getRecordCount()
	{
		return this.recordCount;
	}

	/**
	 * 实际查询结果中的总记录数.
	 */
	public int getRealRecordCount()
	{
		return this.realRecordCount;
	}

	/**
	 * 总记录数中的值是否有效.
	 */
	public boolean isRealRecordCountAvailable()
	{
		return this.realRecordCountAvailable;
	}

	/**
	 * 实际查询结果中是否还有更多的记录.
	 */
	public boolean hasMoreRecord()
	{
		return this.hasMoreRecord;
	}

	/**
	 * 是否需要通过计数查询获取总记录数.
	 */
	public boolean needCount()
	{
		return this.needCount;
	}

	static class OracleQueryHelper extends QueryHelper
	{
		private int nowStartRow = 1;
		private int nowMaxRows = -1;
		private int nowTotalCount = QueryAdapter.TOTAL_COUNT_NONE;
		private String oldPreparedSQL;
		private String cacheSQL;
		private boolean useOldSQL = false;

		public OracleQueryHelper(QueryAdapter query)
		{
			super(query);
		}

		/**
		 * 获取当前查询工具的类型.
		 */
		public String getType()
		{
			return "Oracle";
		}

		public String getQuerySQL(String preparedSQL)
				throws ConfigurationException
		{
			QueryAdapter query = this.getQueryAdapter();
			if (this.cacheSQL != null)
			{
				try
				{
					if (this.oldPreparedSQL != preparedSQL || this.nowStartRow != query.getStartRow()
							|| this.nowMaxRows != query.getMaxRows() || this.nowTotalCount != query.getTotalCount())
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
               if (this.nowStartRow == 1)
					{
						String part1 = "select * from (";
						String part2 = ") tmpTable where rownum <= " + (this.nowMaxRows + 1);
						StringAppender buf = StringTool.createStringAppender(
								part1.length() + part2.length() + preparedSQL.length());
						buf.append(part1).append(preparedSQL).append(part2);
						this.cacheSQL = buf.toString();
					}
					else
					{
						String condition1 = this.nowMaxRows == - 1 ? ""
								: " where rownum <= " + (this.nowMaxRows + this.nowStartRow);
						String condition2 = " where theOracleRuwNum >= " + this.nowStartRow;
						String part1 = "select * from (select tmpTable1.*, rownum as theOracleRuwNum from (";
						String part2 = ") tmpTable1" + condition1 + ") tmpTable2" + condition2;
						StringAppender buf = StringTool.createStringAppender(
								part1.length() + part2.length() + preparedSQL.length());
						buf.append(part1).append(preparedSQL).append(part2);
						this.cacheSQL = buf.toString();
					}
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

			int maxRows = this.getMaxRows();
			QueryAdapter query = this.getQueryAdapter();
			ArrayList result = new ArrayList(maxRows == -1 ? 32 : this.getMaxRows());
			if (maxRows == -1)
			{
				while (rs.next())
				{
					tmpRecordCount++;
					result.add(QueryAdapterImpl.getResults(query, readerList, rs));
				}
				if (tmpRecordCount > 0)
				{
					this.realRecordCount = tmpRecordCount += this.nowStartRow - 1;
					this.realRecordCountAvailable = true;
				}
            this.hasMoreRecord = false;
			}
			else
			{
				int i = 0;
				for (; i < maxRows && (this.hasMoreRecord = rs.next()); i++)
				{
					tmpRecordCount++;
					result.add(QueryAdapterImpl.getResults(query, readerList, rs));
				}
				// 这么判断是防止某些jdbc在第一次next为false后, 后面的next又变回true
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

				int totalCount = this.getTotalCount();
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
					if (!this.realRecordCountAvailable)
					{
						this.realRecordCount = totalCount;
						this.realRecordCountAvailable = true;
					}
					else
					{
						this.realRecordCount = tmpRecordCount;
					}
				}
			}

			this.recordCount = result.size();
			return result;
		}

	}

}