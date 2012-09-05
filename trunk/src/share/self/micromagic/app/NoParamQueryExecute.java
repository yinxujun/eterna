
package self.micromagic.app;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.HashMap;

import javax.sql.DataSource;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.model.Execute;
import self.micromagic.eterna.model.ModelAdapter;
import self.micromagic.eterna.model.ModelExport;
import self.micromagic.eterna.model.impl.AbstractExecute;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.share.Generator;
import self.micromagic.eterna.sql.QueryAdapter;
import self.micromagic.eterna.sql.ResultIterator;
import self.micromagic.util.Utils;
import self.micromagic.util.container.SynHashMap;

/**
 * ���ݴ���Ĳ���queryNameִ����Ӧ��query, ��ִ�е�query����
 * ��û�в�����.
 *
 * �����õ������б�
 *
 * queryNameTag    �Ӳ����л�ȡquery���ƵĲ�����, Ĭ��ֵΪ"queryName"
 *
 * queryName       ֱ������query������, ��������˴˲���, ������queryNameTag
 *
 * cacheName       ��ѯ�������cache������, �����ڶ��ʵ���й���ͬһ������,
 *                 Ĭ��ֵΪ: cache
 *
 *
 * ���ڶ�Ӧ��query�����õ�����
 *
 * cacheMinute    ��ѯ�������ķ�����, -1��ʾ���û���, 0��ʾ������, Ĭ��ֵΪ0
 */
public class NoParamQueryExecute extends AbstractExecute
      implements Execute, Generator
{
   public static final String CACHE_TIME_TAG = "cacheMinute";

	/**
	 * cacheMap�Ļ���.
	 */
	private static final Map caches = new HashMap();

	/**
	 * Ĭ�ϵĻ�������.
	 */
	private static final String DEFAULT_CACHE_NAME = "cache";

   protected Map cacheMap;

   protected String queryNameTag = "queryName";
   protected String queryName = null;
   protected String dataSourceName;
   protected EternaFactory factory;

   public void initialize(ModelAdapter model)
         throws ConfigurationException
   {
      if (this.initialized)
      {
         return;
      }
      this.factory = model.getFactory();
      this.dataSourceName = model.getDataSourceName();
      String tmp = (String) this.getAttribute("queryNameTag");
      if (tmp != null)
      {
         this.queryNameTag = tmp;
      }
      tmp = (String) this.getAttribute("queryName");
      if (tmp != null)
      {
         this.queryName = tmp;
      }

      String cacheName = (String) this.getAttribute("cacheName");
      if (cacheName == null)
      {
         cacheName = DEFAULT_CACHE_NAME;
      }
		synchronized (caches)
		{
			this.cacheMap = (Map) caches.get(cacheName);
			if (this.cacheMap == null)
			{
				this.cacheMap = new SynHashMap();
				caches.put(cacheName, this.cacheMap);
			}
		}
   }

   public String getExecuteType()
			throws ConfigurationException
   {
      return "noParamQuery";
   }

   public ModelExport execute(AppData data, Connection conn)
         throws ConfigurationException, SQLException, IOException
   {
      String name = this.queryName != null ? this.queryName
            : data.getRequestParameter(this.queryNameTag);
      QueryAdapter query = factory.createQueryAdapter(name);
      String tmp = (String) query.getAttribute(CACHE_TIME_TAG);
      if (tmp != null)
      {
         int minute = Utils.parseInt(tmp);
         if (minute == 0)
         {
            data.dataMap.put(name, this.queryCodes(query, conn));
         }
         else
			{
         	CacheContainer cc = (CacheContainer) this.cacheMap.get(name);
				long now = System.currentTimeMillis();
				if (cc != null && (minute == -1 || now < cc.expiredTime))
				{
					data.dataMap.put(name, cc.getQueryResult());
				}
				else
				{
					ResultIterator ritr = this.queryCodes(query, conn);
					cc = new CacheContainer(ritr, minute == -1 ? -1L : now + (minute * 60 * 1000L));
					this.cacheMap.put(name, cc);
					data.dataMap.put(name, cc.getQueryResult());
				}
			}
      }
      else
      {
         data.dataMap.put(name, this.queryCodes(query, conn));
      }
      return null;
   }

   protected ResultIterator queryCodes(QueryAdapter query, Connection conn)
         throws ConfigurationException, SQLException
   {
      Connection myConn = conn;
      try
      {
         if (conn == null)
         {
            DataSource ds;
            if (this.dataSourceName == null)
            {
               ds = this.factory.getDataSourceManager().getDefaultDataSource();
            }
            else
            {
               ds = this.factory.getDataSourceManager().getDataSource(this.dataSourceName);
            }
            myConn = ds.getConnection();
            myConn.setAutoCommit(true);
         }
         return query.executeQuery(myConn);
      }
      finally
      {
         if (conn == null && myConn != null)
         {
            myConn.close();
         }
      }
   }

	/**
	 * ��ѯ������������.
	 */
   static class CacheContainer
   {
		/**
		 * ��ѯ�Ľ��.
		 */
      private ResultIterator queryResult;

		/**
		 * ����Ĺ���ʱ��.
		 */
      public final long expiredTime;

      public CacheContainer(ResultIterator queryResult, long expiredTime)
      {
         this.queryResult = queryResult;
         this.expiredTime = expiredTime;
      }

		/**
		 * ��ȡ����Ĳ�ѯ���, �Ὣ��������ƺ󷵻�.
		 */
      public ResultIterator getQueryResult()
            throws ConfigurationException
      {
         return this.queryResult.copy();
      }

   }

}
