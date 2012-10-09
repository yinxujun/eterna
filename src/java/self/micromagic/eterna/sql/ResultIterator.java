
package self.micromagic.eterna.sql;

import java.sql.SQLException;
import java.util.Iterator;

import self.micromagic.eterna.digester.ConfigurationException;

/**
 * @author micromagic@sina.com
 */
public interface ResultIterator extends Iterator
{
   /**
    * ��ȡ���<code>ResultIterator</code>������, �Լ�ÿ�е����� ���
    * ���� reader����.
    */
   ResultMetaData getMetaData() throws SQLException, ConfigurationException;

   /**
    * �ж��Ƿ��и���ļ�¼.
    */
   boolean hasMoreRow() throws SQLException, ConfigurationException;

   /**
    * Ԥȡ��һ��<code>ResultRow</code>. <p>
    * ������������Ὣ�α�ָ����һ��, ���Ծ����ǵ��ö��, Ҳֻ��ȡ����һ��.
    * ���û����һ��, �Ƿ���null.
    */
   ResultRow preFetch() throws SQLException, ConfigurationException;

   /**
    * Ԥȡ�����ĳ��<code>ResultRow</code>. <p>
    * ������������Ὣ�α��ƶ�, ���Ծ����ǵ��ö��, �α�Ҳ����ԭ��λ��.
    * ���ʣ���¼��û��ô��, �Ƿ���null.
    *
    * @param index    ҪԤȡ֮��ĵڼ�����¼, 1Ϊ��һ�� 2Ϊ�ڶ���
    */
   ResultRow preFetch(int index) throws SQLException, ConfigurationException;

   /**
    * ��ȡ��ǰ<code>ResultRow</code>.
    * ���δִ�й� nextRow��next, ���߸�ִ�й�beforeFirst, �Ƿ���null.
    */
   ResultRow getCurrentRow() throws SQLException, ConfigurationException;

   /**
    * ��ȡ��һ��<code>ResultRow</code>.
    */
   ResultRow nextRow() throws SQLException, ConfigurationException;

   /**
    * ���α��Ƶ���һ��֮ǰ.
    *
    * @return <code>true</code> �����α��ƶ��ɹ�
    *         <code>false</code> �α��޷��ƶ�
   */
   boolean beforeFirst() throws SQLException, ConfigurationException;

   /**
    * �ر����<code>ResultIterator</code>����, �رյ�ͬʱ��رն�Ӧ
    * �����ݿ�����.
    */
   void close() throws SQLException, ConfigurationException;

   /**
    * ��ȡ���<code>ResultIterator</code>�ĸ���.
	 * ���ɸ�����ͬʱ�����beforeFirst����, ���α��Ƶ���һ��֮ǰ.
    *
    * @return    ���ɵĸ���, ����޷����ɸ����򷵻�<code>null</code>.
	 * @see #beforeFirst
    */
   ResultIterator copy() throws ConfigurationException;

   /**
    * ȡ�ý������ʵ�ʵļ�¼��. <p>
    */
   int getRealRecordCount() throws SQLException, ConfigurationException;

   /**
    * ȡ�ôӽ�����л�ȡ�ļ�¼��. <p>
    * �����ȡ�������м�¼���򷵻صļ�¼���뷽��{@link #getRealRecordCount}
    * ���صļ�¼����ͬ����֮����ȡ�ļ�¼�����Ǳ�ʵ�ʵļ�¼��С��
    */
   int getRecordCount() throws SQLException, ConfigurationException;

   /**
    * {@link #getRealRecordCount}�еõ��������ʵ�ʵļ�¼���Ƿ���Ч. <p>
    * ���������������<code>TYPE_SCROLL_INSENSITIVE</code>�����ȡ���еļ�¼��
    * �ͻ᷵��true���������false����{@link #getRealRecordCount}���صĸ�����
    * ����ʵ�ʵļ�¼������
    */
   boolean isRealRecordCountAvailable() throws SQLException, ConfigurationException;

   /**
    * ʵ�ʽ�������Ƿ��и���ļ�¼. <p>
    * �����ȡ�Ĳ������еļ�¼, ���ͨ������������ж�ʵ�ʵĽ�������Ƿ���
    * ����ļ�¼.
    */
   boolean isHasMoreRecord() throws SQLException, ConfigurationException;

}
