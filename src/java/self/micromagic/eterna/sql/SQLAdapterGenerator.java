
package self.micromagic.eterna.sql;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.AdapterGenerator;

public interface SQLAdapterGenerator extends AdapterGenerator
{
   /**
    * ����Ҫ�����SQL������������.
    */
   void setName(String name) throws ConfigurationException;

   /**
    * ��ȡҪ�����SQL������������.
    */
   String getName() throws ConfigurationException;

   /**
    * ���ñ�SQL������sql��־�ļ�¼��ʽ
    */
   void setLogType(String logType) throws ConfigurationException;

   /**
    * ����Ԥ��SQL���. <p>
    *
    * @param sql      Ҫ���õ�Ԥ��SQL���.
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   void setPreparedSQL(String sql) throws ConfigurationException;

   /**
    * ��ղ�����. <p>
    *
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   void clearParameters() throws ConfigurationException;

   /**
    * ���һ������. <p>
    *
    * @param paramGenerator     ����������.
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   void addParameter(SQLParameterGenerator paramGenerator) throws ConfigurationException;

   /**
    * ���һ��������. <p>
    *
    * @param groupName     ����������.
    * @param ignoreList    ���ԵĲ����б�.
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   void addParameterRef(String groupName, String ignoreList) throws ConfigurationException;

   /**
    * ���һ��<code>SQLAdapter</code>��ʵ��. <p>
    *
    * @return <code>SQLAdapter</code>��ʵ��.
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   SQLAdapter createSQLAdapter() throws ConfigurationException;

}
