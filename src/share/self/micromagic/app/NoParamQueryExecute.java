
package self.micromagic.app;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

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

/**
 * 根据传入的参数queryName执行相应的query, 被执行的query必须
 * 是没有参数的.
 *
 * 可设置的属性列表
 *
 * queryNameTag    从参数中获取query名称的参数名, 默认值为"queryName"
 *
 * queryName       直接设置query的名称, 如果设置了此参数, 将忽略queryNameTag
 *
 *
 * 可在对应的query中设置的属性
 *
 * cacheMinute    查询结果缓存的分钟数, -1表示永久缓存, 0表示不缓存, 默认值为0
 */
public class NoParamQueryExecute extends AbstractExecute
      implements Execute, Generator
{
   public static final String CACHE_TIME_TAG = "cacheMinute";

   protected Map cacheMap = new HashMap();

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
   }

   public String getExecuteType() throws ConfigurationException
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
         CacheContainer cc = (CacheContainer) this.cacheMap.get(name);
         int minute = Utils.parseInt(tmp);
         if (minute == 0)
         {
            data.dataMap.put(name, this.queryCodes(query, conn));
         }
         else if (cc != null && (minute == -1 || System.currentTimeMillis() < cc.cachedTime + (minute * 1000L * 60)))
         {
            data.dataMap.put(name, cc.getQueryResult());
         }
         else
         {
            ResultIterator ritr = this.queryCodes(query, conn);
            cc = new CacheContainer(ritr, System.currentTimeMillis());
            this.cacheMap.put(name, cc);
            data.dataMap.put(name, cc.getQueryResult());
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

   class CacheContainer
   {
      private final ResultIterator queryResult;
      public final long cachedTime;

      public CacheContainer(ResultIterator queryResult, long cachedTime)
      {
         this.queryResult = queryResult;
         this.cachedTime = cachedTime;
      }

      public ResultIterator getQueryResult()
            throws ConfigurationException
      {
         return this.queryResult.copy();
      }

   }

}
