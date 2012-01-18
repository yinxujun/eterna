
package self.micromagic.eterna.sql;

import self.micromagic.eterna.digester.ConfigurationException;

public interface QueryAdapterGenerator extends SQLAdapterGenerator
{
   /**
    * �����������sql������ڵ�λ��.
    */
   void setOrderIndex(int index) throws ConfigurationException;

   /**
    * ��������ѯ�Ƿ�ֻ����forwardOnlyģʽ��ִ��.
    */
   void setForwardOnly(boolean forwardOnly) throws ConfigurationException;

   /**
    * ����ResultReader������ʽ�ַ���.
    */
   void setReaderOrder(String readerOrder) throws ConfigurationException;

   /**
    * ���ü̳е�ResultReaderManager������.
    */
   void setReaderManagerName(String name) throws ConfigurationException;

   /**
    * �ڼ̳е�ResultReaderManager�Ļ��������һ��ResultReader, ���
    * ������ResultReaderManager�е��ظ�, �򸲸�ԭ����.
    */
   void addResultReader(ResultReader reader) throws ConfigurationException;

   /**
    * ���һ��<code>QueryAdapter</code>��ʵ��. <p>
    *
    * @return <code>QueryAdapter</code>��ʵ��.
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   QueryAdapter createQueryAdapter() throws ConfigurationException;

}
