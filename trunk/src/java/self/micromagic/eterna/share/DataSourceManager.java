
package self.micromagic.eterna.share;

import java.util.Map;

import javax.naming.Context;
import javax.sql.DataSource;

import self.micromagic.eterna.digester.ConfigurationException;

public interface DataSourceManager
{
   /**
    * 在初始化缓存(即工厂管理器的实例)中放置数据源映射表的名称.
    */
   public static final String DATA_SOURCE_MAP = "dataSourceMap";
   /**
    * 在初始化缓存(即工厂管理器的实例)中放置默认使用的数据源的名称.
    */
   public static final String DEFAULT_DATA_SOURCE_NAME = "defaultDataSourceName";

   /**
    * 初始化这个DataSourceManager.
    */
   void initialize(EternaFactory factory) throws ConfigurationException;

   DataSource getDefaultDataSource() throws ConfigurationException;

   DataSource getDataSource(String name) throws ConfigurationException;

   Map getDataSourceMap() throws ConfigurationException;

   String getDefaultDataSourceName() throws ConfigurationException;

   void setDefaultDataSourceName(String name) throws ConfigurationException;

   void addDataSource(Context context, String dataSourceConfig)
         throws ConfigurationException;

}
