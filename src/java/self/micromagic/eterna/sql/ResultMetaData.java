
package self.micromagic.eterna.sql;

import self.micromagic.eterna.digester.ConfigurationException;

public interface ResultMetaData
{
   /**
    * ��ȡ<code>ResultIterator</code>��Ӧ��<code>QueryAdapter</code>.
    */
   QueryAdapter getQuery() throws ConfigurationException;

   /**
    * ��ȡ<code>ResultIterator</code>��Ӧ��<code>ResultReaderManager</code>.
    */
   ResultReaderManager getReaderManager() throws ConfigurationException;

   /**
    * ��ȡ<code>ResultIterator</code>��Ӧ������.
	 * �����ƿ����Ƕ�Ӧquery������, Ҳ�����Ƕ�Ӧreader-manager������.
    */
   String getName() throws ConfigurationException;

   /**
    * ��ȡ<code>ResultIterator</code>���еĸ���.
    */
   int getColumnCount() throws ConfigurationException;

   /**
    * ��ȡĳ�е���ʾ���.
    *
    * @param column ��һ��Ϊ1, �ڶ���Ϊ2, ...
    */
   int getColumnWidth(int column) throws ConfigurationException;

   /**
    * ��ȡĳ�е���ʾ����.
    *
    * @param column ��һ��Ϊ1, �ڶ���Ϊ2, ...
    */
   String getColumnCaption(int column) throws ConfigurationException;

   /**
    * ��ȡĳ�е�����.
    *
    * @param column ��һ��Ϊ1, �ڶ���Ϊ2, ...
    */
   String getColumnName(int column) throws ConfigurationException;

   /**
    * ��ȡ���ڶ�ȡ���е�ResultReader����.
    *
    * @param column ��һ��Ϊ1, �ڶ���Ϊ2, ...
    */
   ResultReader getColumnReader(int column) throws ConfigurationException;

   /**
    * �����������Ҵ������ڵ�����ֵ.
    *
    * @param columnName ĳ�е�����
	 * @return  �������ڵ�����ֵ
	 *          ��һ��Ϊ1, �ڶ���Ϊ2, ...
    */
   int findColumn(String columnName) throws ConfigurationException;

   /**
    * �����������Ҵ������ڵ�����ֵ.
    *
    * @param columnName  ĳ�е�����
    * @param notThrow    ��Ϊ<code>true<code>ʱ, ��������������ʱ�����׳��쳣,
	 *                    ��ֻ�Ƿ���-1
    * @return  �����������ڵ�����ֵ, ��-1(��������������ʱ)
	 *          ��һ��Ϊ1, �ڶ���Ϊ2, ...
    */
   int findColumn(String columnName, boolean notThrow) throws ConfigurationException;

}
