
package self.micromagic.eterna.sql;

import self.micromagic.eterna.digester.ConfigurationException;

public interface ResultMetaData
{
   /**
    * ��ȡ<code>ResultIterator</code>��Ӧ��<code>QueryAdapter</code>.
    */
   QueryAdapter getQuery() throws ConfigurationException;

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
    * �������ڶ�ȡĳ�е�ResultReader����.
    *
    * @param columnName ĳ�е�����
    */
   ResultReader findColumnReader(String columnName) throws ConfigurationException;

}
