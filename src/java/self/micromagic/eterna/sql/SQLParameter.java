
package self.micromagic.eterna.sql;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.sql.preparer.ValuePreparer;

public interface SQLParameter
{
   /**
    * ��ʼ����SQLParameter.
    */
   void initialize(EternaFactory factory) throws ConfigurationException;

   /**
    * ��ȡ���SQLParameter������.
    */
   String getName() throws ConfigurationException;

   /**
    * ��ȡ��Ӧ������.
    */
   String getColumnName() throws ConfigurationException;

   /**
    * ��ȡ���SQLParameter����������.
    */
   int getType() throws ConfigurationException;

   /**
    * ��ȡ���SQLParameter�Ĵ���������.
    */
   int getPureType() throws ConfigurationException;

   /**
    * ��ȡ���SQLParameter�Ĳ�������ֵ.
    */
   int getIndex() throws ConfigurationException;

   /**
    * ��ȡ���SQLParameter��������������.
    */
   String getTypeName() throws ConfigurationException;

   /**
    * ͨ��String���͵����ݹ���һ��ValuePreparer.
    */
   ValuePreparer createValuePreparer(String value) throws ConfigurationException;

   /**
    * ͨ��Object���͵����ݹ���һ��ValuePreparer.
    */
   ValuePreparer createValuePreparer(Object value) throws ConfigurationException;

}
