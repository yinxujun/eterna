
package self.micromagic.eterna.share;

import java.util.Map;

import javax.naming.Context;
import javax.sql.DataSource;

import self.micromagic.eterna.digester.ConfigurationException;

public interface DataSourceManager
{
   /**
    * �ڳ�ʼ������(��������������ʵ��)�з�������Դӳ��������.
    */
   public static final String DATA_SOURCE_MAP = "dataSourceMap";
   /**
    * �ڳ�ʼ������(��������������ʵ��)�з���Ĭ��ʹ�õ�����Դ������.
    */
   public static final String DEFAULT_DATA_SOURCE_NAME = "defaultDataSourceName";

   /**
    * ��ʼ�����DataSourceManager.
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
