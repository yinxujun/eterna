
package self.micromagic.eterna.sql;

import java.sql.Connection;
import java.sql.SQLException;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.security.Permission;
import self.micromagic.util.BooleanRef;

public interface QueryAdapter extends SQLAdapter
{
   /**
    * �����Ƿ���Ҫ������ݿ����Ƶı�ǩ, �����Ϊtrue(Ĭ��ֵ)��ʾ��Ҫ���.
	 * ����һЩ���ݿ�, ��������startRow��maxRowsʱ, ����ӻ�ȡ��¼�����Ƶ����.
    */
   public static final String CHECK_DATABASE_NAME_FLAG = "checkDatabaseName";

   /**
    * �����õ�����ResultReaderManager�������б�, �Զ��ŷָ�.
    */
   public static final String OTHER_READER_MANAGER_SET_FLAG = "otherReaderManagerSet";

   /**
    * �Զ������ܼ�¼��, ͨ��ѭ��ResultSet��next����������.
    */
   public static final int TOTAL_COUNT_AUTO = -1;

   /**
    * �������ܼ�¼��.
    */
   public static final int TOTAL_COUNT_NONE = -2;

   /**
    * ͨ���Զ����ɵ�count���, ��ȡ�ܼ�¼��.
    */
   public static final int TOTAL_COUNT_COUNT = -3;

   /**
    * �����ѯʱҪ����Ȩ���������е���ʾ, ����ִ��ǰ����Ȩ����.
    */
   void setPermission(Permission permission);

	/**
	 * ���ԭʼ�Ĳ�ѯ���.
	 * ������<code>getPreparedSQL</code>, ��ȡ��SQL�п����Ǳ�ת������,
	 * ��: ��������startRow��maxRowsʱ, ����ӻ�ȡ��¼�����Ƶ�����.
	 *
	 * @see #getPreparedSQL
	 */
	String getPrimitiveQuerySQL() throws ConfigurationException;

   /**
    * ���ResultReader������ʽ�ַ���.
    */
   String getReaderOrder() throws ConfigurationException;

   /**
    * ��õ�ǰ��ѯ�����ResultReaderManager. <p>
    * �÷�����Ҫ���ڶ�ResultReaderManager����һЩ����, ���Զ�����.
    * Ȼ��ͨ��{@link #setReaderManager(ResultReaderManager)}������
    * �Ķ����õ���ѯ������.
    */
   ResultReaderManager getReaderManager() throws ConfigurationException;

   /**
    * ���øò�ѯ��ResultReaderManager, �����õ�ResultReaderManager�����Ʊ���
    * ��ԭ����������ͬ, ������<code>otherReaderManagerSet</code>�б���.
    */
   void setReaderManager(ResultReaderManager readerManager) throws ConfigurationException;

   /**
    * �Ƿ����������. <p>
    * ����ڶ���ʱ������orderIndex���query�ǿ����������.
    */
   boolean canOrder() throws ConfigurationException;

   /**
    * ���õ����������. <p>
    * �����������Ϊ�Զ�����, ��ǰһ�������˸���, ��ʼΪ����, ���ٴ�
    * ����ʱ�ͻ��Ϊ����. ���ǰһ�����õĲ��Ǹ���, ��Ĭ��Ϊ����.
    */
   void setSingleOrder(String readerName) throws ConfigurationException;

   /**
    * ���õ����������.
    *
    * @param orderType   0    ��ʾ�Զ�ѡ��˳��, ͬ{@link #setSingleOrder(String)}
    *                    ���� ��ʾʹ�ý���
    *                    ���� ��ʾʹ������
    */
   void setSingleOrder(String readerName, int orderType) throws ConfigurationException;

   /**
    * ��ȡ�������������, ������Ϊĳ��reader������. <p>
    * �����δ���õ�������, �򷵻�null.
    *
    * @param desc  ��ʾ�Ƿ�Ϊ����, true��ʾ�ǽ���
    */
   String getSingleOrder(BooleanRef desc) throws ConfigurationException;

   /**
    * ���ö�������. <p>
    * ע: �����������õĲ���reader������, ����reader�������ټ��������.
    * ��: ���� readerName + 'A', ���� readerName + 'D'
    *
    * @param orderNames   ��������������������
    */
   void setMultipleOrder(String[] orderNames) throws ConfigurationException;

   /**
    * ��ȡ�����ѯ�����Ƿ�ֻ����forwardOnlyģʽ��ִ��.
    */
   boolean isForwardOnly() throws ConfigurationException;

   /**
    * ��ȡ�ò�ѯ�����Ǵӵڼ�����¼��ʼ��ȡ, Ĭ��ֵΪ"1".
    */
   int getStartRow() throws SQLException;

   /**
    * ���ôӵڼ�����¼��ʼȡֵ(��1��ʼ����).
    *
    * @param startRow   ��ʼ�к�
    */
   void setStartRow(int startRow) throws SQLException;

   /**
    * ��ȡ�ò�ѯ�����ȡ������¼��, Ĭ��ֵΪ"-1", ��ʾ
    * ȡ��Ϊֹ.
    */
   int getMaxRows() throws SQLException;

   /**
    * ����ȡ��������¼����-1��ʾȡ��Ϊֹ.
    *
    * @param maxRows   ȡ��������¼��
    */
   void setMaxRows(int maxRows) throws SQLException;

   /**
    * ��ȡ�ò�ѯ�������õ��ܼ�¼��.
    */
   int getTotalCount() throws ConfigurationException;

   /**
    * ���øò�ѯ����ļ�¼��. <p>
    * Ĭ��ֵΪ<code>TOTAL_COUNT_AUTO(-1)</code>.
    *
    * @param totalCount   �ܼ�¼��.
    *                     <code>TOTAL_COUNT_AUTO(-1)</code>, <code>TOTAL_COUNT_NONE(-2)</code>,
    *                     <code>TOTAL_COUNT_COUNT(-3)</code>Ϊ���������. 0-NΪֱ�������ܼ�¼��.
    *
    * @see #TOTAL_COUNT_AUTO
    * @see #TOTAL_COUNT_NONE
    * @see #TOTAL_COUNT_COUNT
    */
   void setTotalCount(int totalCount) throws ConfigurationException;

   /**
    * ��ò�ѯ�Ľ��, ��������(100������)���ݵĲ�ѯ.
    * <p>ʵ����Ҫ������, �����ݿ�����<code>Connection</code>���رյ�ʱ��,
    * ҲҪ�ܹ���ȡ����.
    *
    * @param conn      ���ݿ������
    * @return          ��ѯ�����Ľ����
    * @throws ConfigurationException   ��������ó���ʱ
    * @throws SQLException    ����������ݿ�ʱ����
    */
   ResultIterator executeQuery(Connection conn)
         throws ConfigurationException, SQLException;

   /**
    * ��ò�ѯ�Ľ��, ���ڴ���(500������)���ݵĲ�ѯ.
    * <p>ʵ���߲��豣����, ����ֱ����ʹ��<code>ResultSet</code>��Ϊ���������.
    * �����ݿ�����<code>Connection</code>���رյ�ʱ��, Ҳ���޷���ȡ����.
    *
    * ע: ʹ�����ֶ�ȡ��ʽʱ, ������startRow��maxRow������.
    *
    * @param conn      ���ݿ������
    * @return          ��ѯ�����Ľ����
    * @throws ConfigurationException  ��������ó���ʱ
    * @throws SQLException    ����������ݿ�ʱ����
    */
   ResultIterator executeQueryHoldConnection(Connection conn)
         throws ConfigurationException, SQLException;

}
