
package self.micromagic.eterna.sql;

import java.util.List;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.security.Permission;
import self.micromagic.eterna.share.EternaFactory;

public interface ResultReaderManager
{
   /**
    * ��ʼ����ResultReaderManager����, ϵͳ���ڳ�ʼ��ʱ���ô˷���. <p>
    * �÷�������Ҫ�����ǳ�ʼ��ÿ��ResultReader����, �����ݸ�����������Լ�
    * �Լ���reader�б�.
    *
    * @param factory  EternaFactory��ʵ��, ���Դ��л�ø�����
    */
   void initialize(EternaFactory factory) throws ConfigurationException;

   /**
    * ���ñ�ResultReaderManager������.
    */
   void setName(String name) throws ConfigurationException;

   /**
    * ��ȡ��ResultReaderManager������.
    */
   String getName() throws ConfigurationException;

   /**
    * ���ø�ResultReaderManager������.
    */
   void setParentName(String name) throws ConfigurationException;

   /**
    * ��ȡ��ResultReaderManager������.
    */
   String getParentName() throws ConfigurationException;

   /**
    * ���ñ�ResultReaderManager������.
    */
   ResultReaderManager getParent() throws ConfigurationException;

   /**
    * ��ȡ���ɱ�ResultReaderManager�Ĺ���.
    */
   EternaFactory getFactory() throws ConfigurationException;

   /**
    * ���������Ƿ��Ǵ�Сд���е�, Ĭ�ϵ������е�.<p>
    * ע: ֻ����û�����reader��ʱ����������������.
    */
   void setColNameSensitive(boolean colNameSensitive) throws ConfigurationException;

   /**
    * ���ResultReader������ʽ�ַ���.
    */
   String getReaderOrder() throws ConfigurationException;

   /**
    * ����ResultReader������ʽ�ַ���.
    */
   void setReaderOrder(String readerOrder) throws ConfigurationException;

   /**
    * ���ResultReaderManager�е�ResultReader�ܸ���, �������һ����ڻ����
    * ReaderList�е�ResultReader����. <p>
    *
    * @see #getReaderList(Permission)
    * @see #getReaderInList(int)
    */
   int getReaderCount() throws ConfigurationException;

   /**
    * ͨ��reader�����ƻ�ȡһ��ResultReader.
    */
   ResultReader getReader(String name) throws ConfigurationException;

   /**
    * ���һ��<code>ResultReader</code>.
    * <p>�����<code>ResultReader</code>�������Ѿ�����, ��Ḳ��ԭ����reader.
    *
    * @param reader  Ҫ��ӵ�<code>ResultReader</code>
    * @return     ����<code>ResultReader</code>�������Ѿ�����ʱ�򷵻ر����ǵ�
    *             ��<code>ResultReader</code>, ���򷵻�<code>null</code>.
    * @throws ConfigurationException  ��������ó���ʱ
    */
   ResultReader addReader(ResultReader reader) throws ConfigurationException;

   /**
    * ����<code>ResultReader</code>������˳���Լ���ѯ���������.
    *
    * @param names     ���<code>ResultReader</code>�����Ƽ����������,
    *                  <code>ResultReader</code>�������������ָ����˳������,
    *                  ������������������.
    *                  ����������ĸ�ʽΪ[����][����(1���ַ�)].
    *                  ����ֱ�Ϊ: "-"��, "A"����, "D"����.
    *
    * @throws ConfigurationException  ��������ó���ʱ
    */
   void setReaderList(String[] names) throws ConfigurationException;

   /**
    * ͨ��reader�����ƻ�ȡ��reader�������ڵ�����ֵ.
    *
    * @param name      reader������
    * @param notThrow  ��Ϊ<code>true<code>ʱ, ����Ӧ���Ƶ�reader������ʱ
    *                  �����׳��쳣, ��ֻ�Ƿ���-1
    * @return  reader���ڵ�����ֵ, ��-1(����Ӧ���Ƶ�reader������ʱ)
	 *          ��һ��ֵΪ1, �ڶ���ֵΪ2, ...
    */
   int getIndexByName(String name, boolean notThrow) throws ConfigurationException;

   /**
    * ͨ��reader�����ƻ�ȡ��reader�������ڵ�����ֵ.
    */
   int getIndexByName(String name) throws ConfigurationException;

   /**
    * ��ȡ���������sql�����.
    */
   String getOrderByString() throws ConfigurationException;

   /**
    * ���һ��<code>ResultReader</code>���б�.
    * �˷����г��������е�<code>ResultReader</code>.
    *
    * @return  ���ڶ�ȡ���ݵ�����<code>ResultReader</code>���б�.
    * @throws ConfigurationException  ��������ó���ʱ
    */
   List getReaderList() throws ConfigurationException;

   /**
    * ����Ȩ��, ���һ��<code>ResultReader</code>���б�.
    * ���ĳ����û�ж�ȡȨ�޵Ļ�, ����Ӧ���л��滻Ϊ<code>NullResultReader</code>
    * ��ʵ��.
    *
    * @return  ��ʽ���ڶ�ȡ���ݵ�<code>ResultReader</code>���б�.
    * @throws ConfigurationException  ��������ó���ʱ
    */
   List getReaderList(Permission permission) throws ConfigurationException;

   /**
    * ��������ֵ, ��reader�б��л�ȡһ��ResultReader.
    * reader�б�����������ö��ı�.
	 * ��һ��ֵΪ0, �ڶ���ֵΪ1, ...
    */
   ResultReader getReaderInList(int index) throws ConfigurationException;

   /**
    * ��ס�Լ�����������, ����ʹ����ֻ�ܶ�ȡ, �������޸�. <p>
    * һ������ͨ��xmlװ�غ�, ��EternaFactory�ĳ�ʼ���е��ô˷���.
    * ע:�ڵ�����copy������, �¸��Ƶ�ResultReaderManager�ǲ�����ס��.
    *
    * @see #copy(String)
    */
   void lock() throws ConfigurationException;

   /**
    * �����������������, ������.
    * ��copyName��Ϊnullʱ, ���ƽ���Ϊ:"[ԭname]+[copyName]".
    * ��֮���ƽ�����ı�.
    */
   ResultReaderManager copy(String copyName) throws ConfigurationException;

}
