
package self.micromagic.eterna.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.sql.preparer.PreparerManager;
import self.micromagic.eterna.sql.preparer.ValuePreparer;

/**
 * ���ڴ���Ԥ��SQL���.
 *
 * @author  micromagic
 * @version 1.0, 2002-10-13
 */
public interface SQLAdapter
{
   public static final String SQL_LOG_PROPERTY = "self.micromagic.eterna.sql.logType";
   public static final int SQL_LOG_TYPE_NONE = -1;
   public static final int SQL_LOG_TYPE_SAVE = 0x2;
   public static final int SQL_LOG_TYPE_PRINT = 0x1;
   public static final int SQL_LOG_TYPE_SPECIAL = 0x4;

   public static final String SQL_TYPE_UPDATE = "update";
   public static final String SQL_TYPE_QUERY = "query";
   public static final String SQL_TYPE_COUNT = "count";
   public static final String SQL_TYPE_SQL = "SQL";

   /**
    * ��ȡ��SQL������������.
    */
   String getName() throws ConfigurationException;

   /**
    * ��ȡ��SQL������������.
    * ��: query, update.
    */
   String getType() throws ConfigurationException;

   /**
    * ��ȡ��SQL������ĳ�����õ�����.
    */
   Object getAttribute(String name) throws ConfigurationException;

   /**
    * ��ȡ��SQL���������õ��������Ե�����.
    */
   String[] getAttributeNames() throws ConfigurationException;

   /**
    * ��ȡ��SQL������sql��־�ļ�¼��ʽ
    */
   int getLogType() throws ConfigurationException;

   /**
    * ��ȡ���ɱ��������Ĺ���.
    */
   EternaFactory getFactory() throws ConfigurationException;

   /**
    * ��ò����ĸ���.
    *
    * @return    <code>SQLAdapter</code>�в����ĸ���.
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   int getParameterCount() throws ConfigurationException;

   /**
    * ���ʵ����Ч�Ĳ�������.
    *
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   public int getActiveParamCount() throws ConfigurationException;

   /**
    * �ж��Ƿ�����Ч�Ĳ���.
    *
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   public boolean hasActiveParam() throws ConfigurationException;

   /**
    * ����Parameter�����ƻ�ȡһ��SQLParameter.
    */
   SQLParameter getParameter(String paramName) throws ConfigurationException;

   /**
    * ��ȡ��������SQLParameter�ĵ�����.
	 * �������е�����SQLParameter���谴����ֵ��С����˳������.
	 *
	 * @see SQLParameter#getIndex
    */
   Iterator getParameterIterator() throws ConfigurationException;

   /**
    * ִ�б�SQL������.
    */
   void execute(Connection conn) throws ConfigurationException, SQLException;

   /**
    * ��������ĸ���.
    *
    * @return    <code>SQLAdapter</code>�������ĸ���.
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   int getSubSQLCount() throws ConfigurationException;

   /**
    * ���Ԥ��SQL���. <p>
    * ��Ԥ��SQL����Ǿ�����һ���������������������Ԥ��SQL��䡣
    *
    * @return    ������һ������Ԥ��SQL���.
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   String getPreparedSQL() throws ConfigurationException;

   /**
    * ���������.
    *
    * @param index    ����������ֵ.
    * @param subPart  Ҫ���õ������.
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   void setSubSQL(int index, String subPart) throws ConfigurationException;

   /**
    * ���������, ��Ϊ��������Ӧ�Ĳ���.
    *
    * @param index    ����������ֵ.
    * @param subPart  Ҫ���õ������.
    * @param pm       Ҫ���ϵĲ���.
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   void setSubSQL(int index, String subPart, PreparerManager pm) throws ConfigurationException;

   /**
    * ��ñ�sql�����������ò���.
    */
   PreparerManager getPreparerManager() throws ConfigurationException;

   /**
    * ��������ֵ�ж϶�Ӧ�Ĳ����Ƿ��Ƕ�̬����.
    *
    * @param index    ��һ��������1, �ڶ�����2, ...
    */
   boolean isDynamicParameter(int index) throws ConfigurationException;

   /**
    * ���ݲ����������ж϶�Ӧ�Ĳ����Ƿ��Ƕ�̬����.
    *
    * @param name    ����������
    */
   boolean isDynamicParameter(String name) throws ConfigurationException;

   /**
    * ͨ��ValuePreparer�����ò���.
    */
   void setValuePreparer(ValuePreparer preparer) throws ConfigurationException;

   /**
    * ���������õ�PreparedStatement��.
    */
   void prepareValues(PreparedStatement stmt) throws ConfigurationException, SQLException;

   /**
    * ���������õ�PreparedStatementWrap��.
    */
   void prepareValues(PreparedStatementWrap stmtWrap) throws ConfigurationException, SQLException;

   /**
    * ������õĲ�����Ϊ����.
    * ֻ�ж�̬�����ſ�����Ϊ����.
    *
    * @param parameterIndex ��һ��������1, �ڶ�����2, ...
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   void setIgnore(int parameterIndex) throws ConfigurationException;

   /**
    * ������õĲ�����Ϊ����.
    * ֻ�ж�̬�����ſ�����Ϊ����.
    *
    * @param parameterName  �������������
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   void setIgnore(String parameterName) throws ConfigurationException;

   /* *
    * ��һ��<code>Timestamp</code>������Ŀ�����.
    *
    * @param parameterIndex   ����������ֵ.
    * @param x                ������ֵ.
    * @param cal              ��������Timestamp��<code>Calendar</code>.
    * @throws ConfigurationException     ��������ó���ʱ.
    *
   public void setTimestamp(int parameterIndex, java.sql.Timestamp x, Calendar cal)
         throws ConfigurationException;
   */

   /* *
    * ��һ��<code>Timestamp</code>������Ŀ�����.
    *
    * @param parameterName    ����������.
    * @param x                ������ֵ.
    * @param cal              ��������Timestamp��<code>Calendar</code>.
    * @throws ConfigurationException     ��������ó���ʱ.
    *
   public void setTimestamp(String parameterName, java.sql.Timestamp x, Calendar cal)
         throws ConfigurationException;
   */

   /* *
    * ��һ��<code>Object</code>������Ŀ�����.
    *
    * @param parameterIndex   ����������ֵ.
    * @param x                ������ֵ.
    * @throws ConfigurationException     ��������ó���ʱ.
    *
   public void setObject(int parameterIndex, Object x)
         throws ConfigurationException;
   */

   /*
    * ��һ��<code>Object</code>������Ŀ�����.
    *
    * @param parameterName    ����������.
    * @param x                ������ֵ.
    * @throws ConfigurationException     ��������ó���ʱ.
    *
   public void setObject(String parameterName, Object x)
         throws ConfigurationException;
   */

   /* *
    * ��һ��<code>Object</code>������Ŀ�����.
    *
    * @param parameterIndex   ����������ֵ.
    * @param x                ������ֵ.
    * @param targetSqlType    SQL�е�����(��java.sql.Types�ж���).
    * @throws ConfigurationException     ��������ó���ʱ.
    *
   public void setObject(int parameterIndex, Object x, int targetSqlType)
         throws ConfigurationException;
   */

   /* *
    * ��һ��<code>Object</code>������Ŀ�����.
    *
    * @param parameterName    ����������.
    * @param x                ������ֵ.
    * @param targetSqlType    SQL�е�����(��java.sql.Types�ж���).
    * @throws ConfigurationException     ��������ó���ʱ.
    *
   public void setObject(String parameterName, Object x, int targetSqlType)
         throws ConfigurationException;
   */

   /* *
    * ��һ��<code>Object</code>������Ŀ�����.
    *
    * @param parameterIndex   ����������ֵ.
    * @param x                ������ֵ.
    * @param targetSqlType    SQL�е�����(��java.sql.Types�ж���).
    * @param scale            ����DECIMAL��NUMERIC��С��λ��.
    * @throws ConfigurationException     ��������ó���ʱ.
    *
   public void setObject(int parameterIndex, Object x, int targetSqlType, int scale)
         throws ConfigurationException;
   */

   /* *
    * ��һ��<code>Object</code>������Ŀ�����.
    *
    * @param parameterName    ����������.
    * @param x                ������ֵ.
    * @param targetSqlType    SQL�е�����(��java.sql.Types�ж���).
    * @param scale            ����DECIMAL��NUMERIC��С��λ��.
    * @throws ConfigurationException     ��������ó���ʱ.
    *
   public void setObject(String parameterName, Object x, int targetSqlType, int scale)
         throws ConfigurationException;
   */

   /**
    * Sets the designated parameter to SQL <code>NULL</code>.
    * ������õĲ�����ΪSQL��<code>NULL</code>.
    *
    * <P><B>Note:</B> You must specify the parameter's SQL type.
    * <P><B>ע:</B> �����ָ��������SQL����.
    *
    * @param parameterIndex the first parameter is 1, the second is 2, ...
    *                       <p>��һ��������1, �ڶ�����2, ...
    * @param sqlType the SQL type code defined in <code>java.sql.Types</code>
    *                <p>��<code>java.sql.Types</code>�ж����SQL��
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   void setNull(int parameterIndex, int sqlType) throws ConfigurationException;

   /**
    * Sets the designated parameter to SQL <code>NULL</code>.
    * ������õĲ�����ΪSQL��<code>NULL</code>.
    *
    * <P><B>Note:</B> You must specify the parameter's SQL type.
    * <P><B>ע:</B> �����ָ��������SQL����.
    *
    * @param parameterName  the parameter name
    *                       <p>�������������
    * @param sqlType the SQL type code defined in <code>java.sql.Types</code>
    *                <p>��<code>java.sql.Types</code>�ж����SQL��
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   void setNull(String parameterName, int sqlType) throws ConfigurationException;

   /**
    * Sets the designated parameter to the given Java <code>boolean</code> value.
    * ������õĲ�����ΪJava��<code>boolean</code>ֵ.
    * The driver converts this
    * to an SQL <code>BIT</code> value when it sends it to the database.
    * SQL����Ҫ�ڽ����ֵ���͵����ݿ�ʱתΪSQL��<code>BIT</code>����.
    *
    * @param parameterIndex the first parameter is 1, the second is 2, ...
    *                       <p>��һ��������1, �ڶ�����2, ...
    * @param x the parameter value
    *          <p> ���������ֵ
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   void setBoolean(int parameterIndex, boolean x) throws ConfigurationException;

   /**
    * Sets the designated parameter to the given Java <code>boolean</code> value.
    * ������õĲ�����ΪJava��<code>boolean</code>ֵ.
    * to an SQL <code>BIT</code> value when it sends it to the database.
    * SQL����Ҫ�ڽ����ֵ���͵����ݿ�ʱתΪSQL��<code>BIT</code>����.
    *
    * @param parameterName  the parameter name
    *                       <p>�������������
    * @param x the parameter value
    *          <p> ���������ֵ
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   void setBoolean(String parameterName, boolean x) throws ConfigurationException;

   /**
    * Sets the designated parameter to the given Java <code>byte</code> value.
    * ������õĲ�����ΪJava��<code>byte</code>ֵ.
    * The driver converts this
    * to an SQL <code>TINYINT</code> value when it sends it to the database.
    * SQL����Ҫ�ڽ����ֵ���͵����ݿ�ʱתΪSQL��<code>TINYINT</code>����.
    *
    * @param parameterIndex the first parameter is 1, the second is 2, ...
    *                       <p>��һ��������1, �ڶ�����2, ...
    * @param x the parameter value
    *          <p> ���������ֵ
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   void setByte(int parameterIndex, byte x) throws ConfigurationException;

   /**
    * Sets the designated parameter to the given Java <code>byte</code> value.
    * ������õĲ�����ΪJava��<code>byte</code>ֵ.
    * The driver converts this
    * to an SQL <code>TINYINT</code> value when it sends it to the database.
    * SQL����Ҫ�ڽ����ֵ���͵����ݿ�ʱתΪSQL��<code>TINYINT</code>����.
    *
    * @param parameterName  the parameter name
    *                       <p>�������������
    * @param x the parameter value
    *          <p> ���������ֵ
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   void setByte(String parameterName, byte x) throws ConfigurationException;

   /**
    * Sets the designated parameter to the given Java <code>short</code> value.
    * ������õĲ�����ΪJava��<code>short</code>ֵ.
    * The driver converts this
    * to an SQL <code>SMALLINT</code> value when it sends it to the database.
    * SQL����Ҫ�ڽ����ֵ���͵����ݿ�ʱתΪSQL��<code>SMALLINT</code>����.
    *
    * @param parameterIndex the first parameter is 1, the second is 2, ...
    *                       <p>��һ��������1, �ڶ�����2, ...
    * @param x the parameter value
    *          <p> ���������ֵ
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   void setShort(int parameterIndex, short x) throws ConfigurationException;

   /**
    * Sets the designated parameter to the given Java <code>short</code> value.
    * ������õĲ�����ΪJava��<code>short</code>ֵ.
    * The driver converts this
    * to an SQL <code>SMALLINT</code> value when it sends it to the database.
    * SQL����Ҫ�ڽ����ֵ���͵����ݿ�ʱתΪSQL��<code>SMALLINT</code>����.
    *
    * @param parameterName  the parameter name
    *                       <p>�������������
    * @param x the parameter value
    *          <p> ���������ֵ
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   void setShort(String parameterName, short x) throws ConfigurationException;

   /**
    * Sets the designated parameter to the given Java <code>int</code> value.
    * ������õĲ�����ΪJava��<code>int</code>ֵ.
    * The driver converts this
    * to an SQL <code>INTEGER</code> value when it sends it to the database.
    * SQL����Ҫ�ڽ����ֵ���͵����ݿ�ʱתΪSQL��<code>INTEGER</code>����.
    *
    * @param parameterIndex the first parameter is 1, the second is 2, ...
    *                       <p>��һ��������1, �ڶ�����2, ...
    * @param x the parameter value
    *          <p> ���������ֵ
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   void setInt(int parameterIndex, int x) throws ConfigurationException;

   /**
    * Sets the designated parameter to the given Java <code>int</code> value.
    * ������õĲ�����ΪJava��<code>int</code>ֵ.
    * The driver converts this
    * to an SQL <code>INTEGER</code> value when it sends it to the database.
    * SQL����Ҫ�ڽ����ֵ���͵����ݿ�ʱתΪSQL��<code>INTEGER</code>����.
    *
    * @param parameterName  the parameter name
    *                       <p>�������������
    * @param x the parameter value
    *          <p> ���������ֵ
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   void setInt(String parameterName, int x) throws ConfigurationException;

   /**
    * Sets the designated parameter to the given Java <code>long</code> value.
    * ������õĲ�����ΪJava��<code>long</code>ֵ.
    * The driver converts this
    * to an SQL <code>BIGINT</code> value when it sends it to the database.
    * SQL����Ҫ�ڽ����ֵ���͵����ݿ�ʱתΪSQL��<code>BIGINT</code>����.
    *
    * @param parameterIndex the first parameter is 1, the second is 2, ...
    *                       <p>��һ��������1, �ڶ�����2, ...
    * @param x the parameter value
    *          <p> ���������ֵ
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   void setLong(int parameterIndex, long x) throws ConfigurationException;

   /**
    * Sets the designated parameter to the given Java <code>long</code> value.
    * ������õĲ�����ΪJava��<code>long</code>ֵ.
    * The driver converts this
    * to an SQL <code>BIGINT</code> value when it sends it to the database.
    * SQL����Ҫ�ڽ����ֵ���͵����ݿ�ʱתΪSQL��<code>BIGINT</code>����.
    *
    * @param parameterName  the parameter name
    *                       <p>�������������
    * @param x the parameter value
    *          <p> ���������ֵ
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   void setLong(String parameterName, long x) throws ConfigurationException;

   /**
    * Sets the designated parameter to the given Java <code>float</code> value.
    * ������õĲ�����ΪJava��<code>float</code>ֵ.
    * The driver converts this
    * to an SQL <code>FLOAT</code> value when it sends it to the database.
    * SQL����Ҫ�ڽ����ֵ���͵����ݿ�ʱתΪSQL��<code>FLOAT</code>����.
    *
    * @param parameterIndex the first parameter is 1, the second is 2, ...
    *                       <p>��һ��������1, �ڶ�����2, ...
    * @param x the parameter value
    *          <p> ���������ֵ
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   void setFloat(int parameterIndex, float x) throws ConfigurationException;

   /**
    * Sets the designated parameter to the given Java <code>float</code> value.
    * ������õĲ�����ΪJava��<code>float</code>ֵ.
    * The driver converts this
    * to an SQL <code>FLOAT</code> value when it sends it to the database.
    * SQL����Ҫ�ڽ����ֵ���͵����ݿ�ʱתΪSQL��<code>FLOAT</code>����.
    *
    * @param parameterName  the parameter name
    *                       <p>�������������
    * @param x the parameter value
    *          <p> ���������ֵ
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   void setFloat(String parameterName, float x) throws ConfigurationException;

   /**
    * Sets the designated parameter to the given Java <code>double</code> value.
    * ������õĲ�����ΪJava��<code>double</code>ֵ.
    * The driver converts this
    * to an SQL <code>DOUBLE</code> value when it sends it to the database.
    * SQL����Ҫ�ڽ����ֵ���͵����ݿ�ʱתΪSQL��<code>DOUBLE</code>����.
    *
    * @param parameterIndex the first parameter is 1, the second is 2, ...
    *                       <p>��һ��������1, �ڶ�����2, ...
    * @param x the parameter value
    *          <p> ���������ֵ
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   void setDouble(int parameterIndex, double x) throws ConfigurationException;

   /**
    * Sets the designated parameter to the given Java <code>double</code> value.
    * ������õĲ�����ΪJava��<code>double</code>ֵ.
    * The driver converts this
    * to an SQL <code>DOUBLE</code> value when it sends it to the database.
    * SQL����Ҫ�ڽ����ֵ���͵����ݿ�ʱתΪSQL��<code>DOUBLE</code>����.
    *
    * @param parameterName  the parameter name
    *                       <p>�������������
    * @param x the parameter value
    *          <p> ���������ֵ
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   void setDouble(String parameterName, double x) throws ConfigurationException;

   /* *
    * Sets the designated parameter to the given <code>java.math.BigDecimal</code> value.
    * ������õĲ�����ΪJava��<code>java.math.BigDecimal</code>ֵ.
    * The driver converts this to an SQL <code>NUMERIC</code> value when
    * it sends it to the database.
    * SQL����Ҫ�ڽ����ֵ���͵����ݿ�ʱתΪSQL��<code>NUMERIC</code>����.
    *
    * @param parameterIndex the first parameter is 1, the second is 2, ...
    *                       <p>��һ��������1, �ڶ�����2, ...
    * @param x the parameter value
    *          <p> ���������ֵ
    * @throws ConfigurationException     ��������ó���ʱ.
    * δ֧��
    *
   void setBigDecimal(int parameterIndex, BigDecimal x) throws ConfigurationException;
   */

   /* *
    * Sets the designated parameter to the given <code>java.math.BigDecimal</code> value.
    * ������õĲ�����ΪJava��<code>java.math.BigDecimal</code>ֵ.
    * The driver converts this to an SQL <code>NUMERIC</code> value when
    * it sends it to the database.
    * SQL����Ҫ�ڽ����ֵ���͵����ݿ�ʱתΪSQL��<code>NUMERIC</code>����.
    *
    * @param parameterName  the parameter name
    *                       <p>�������������
    * @param x the parameter value
    *          <p> ���������ֵ
    * @throws ConfigurationException     ��������ó���ʱ.
    * δ֧��
    *
   void setBigDecimal(String parameterName, BigDecimal x) throws ConfigurationException;
   */

   /**
    * Sets the designated parameter to the given Java <code>String</code> value.
    * ������õĲ�����ΪJava��<code>String</code>ֵ.
    * The driver converts this
    * to an SQL <code>VARCHAR</code> or <code>LONGVARCHAR</code> value
    * (depending on the argument's
    * size relative to the driver's limits on <code>VARCHAR</code> values)
    * when it sends it to the database.
    * SQL����Ҫ�ڽ����ֵ���͵����ݿ�ʱתΪSQL��<code>VARCHAR</code>��
    * <code>LONGVARCHAR</code>����.
    *
    * @param parameterIndex the first parameter is 1, the second is 2, ...
    *                       <p>��һ��������1, �ڶ�����2, ...
    * @param x the parameter value
    *          <p> ���������ֵ
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   void setString(int parameterIndex, String x) throws ConfigurationException;

   /**
    * Sets the designated parameter to the given Java <code>String</code> value.
    * ������õĲ�����ΪJava��<code>String</code>ֵ.
    * The driver converts this
    * to an SQL <code>VARCHAR</code> or <code>LONGVARCHAR</code> value
    * (depending on the argument's
    * size relative to the driver's limits on <code>VARCHAR</code> values)
    * when it sends it to the database.
    * SQL����Ҫ�ڽ����ֵ���͵����ݿ�ʱתΪSQL��<code>VARCHAR</code>��
    * <code>LONGVARCHAR</code>����.
    *
    * @param parameterName  the parameter name
    *                       <p>�������������
    * @param x the parameter value
    *          <p> ���������ֵ
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   void setString(String parameterName, String x) throws ConfigurationException;

   /**
    * Sets the designated parameter to the given Java array of bytes.
    * ������õĲ�����ΪJava��<code>byte</code>����.
    * The driver converts
    * this to an SQL <code>VARBINARY</code> or <code>LONGVARBINARY</code>
    * (depending on the argument's size relative to the driver's limits on
    * <code>VARBINARY</code> values) when it sends it to the database.
    * SQL����Ҫ�ڽ����ֵ���͵����ݿ�ʱתΪSQL��<code>VARBINARY</code>��
    * <code>LONGVARBINARY</code>����.
    *
    * @param parameterIndex the first parameter is 1, the second is 2, ...
    *                       <p>��һ��������1, �ڶ�����2, ...
    * @param x the parameter value
    *          <p> ���������ֵ
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   void setBytes(int parameterIndex, byte[] x) throws ConfigurationException;

   /**
    * Sets the designated parameter to the given Java array of bytes.
    * ������õĲ�����ΪJava��<code>byte</code>����.
    * The driver converts
    * this to an SQL <code>VARBINARY</code> or <code>LONGVARBINARY</code>
    * (depending on the argument's size relative to the driver's limits on
    * <code>VARBINARY</code> values) when it sends it to the database.
    * SQL����Ҫ�ڽ����ֵ���͵����ݿ�ʱתΪSQL��<code>VARBINARY</code>��
    * <code>LONGVARBINARY</code>����.
    *
    * @param parameterName  the parameter name
    *                       <p>�������������
    * @param x the parameter value
    *          <p> ���������ֵ
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   void setBytes(String parameterName, byte[] x) throws ConfigurationException;

   /**
    * Sets the designated parameter to the given <code>java.sql.Date</code> value.
    * ������õĲ�����ΪJava��<code>java.sql.Date</code>����.
    * The driver converts this
    * to an SQL <code>DATE</code> value when it sends it to the database.
    * SQL����Ҫ�ڽ����ֵ���͵����ݿ�ʱתΪSQL��<code>DATE</code>����.
    *
    * @param parameterIndex the first parameter is 1, the second is 2, ...
    *                       <p>��һ��������1, �ڶ�����2, ...
    * @param x the parameter value
    *          <p> ���������ֵ
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   void setDate(int parameterIndex, java.sql.Date x) throws ConfigurationException;

   /**
    * Sets the designated parameter to the given <code>java.sql.Date</code> value.
    * ������õĲ�����ΪJava��<code>java.sql.Date</code>����.
    * The driver converts this
    * to an SQL <code>DATE</code> value when it sends it to the database.
    * SQL����Ҫ�ڽ����ֵ���͵����ݿ�ʱתΪSQL��<code>DATE</code>����.
    *
    * @param parameterName  the parameter name
    *                       <p>�������������
    * @param x the parameter value
    *          <p> ���������ֵ
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   void setDate(String parameterName, java.sql.Date x) throws ConfigurationException;

   /**
    * Sets the designated parameter to the given <code>java.sql.Time</code> value.
    * ������õĲ�����ΪJava��<code>java.sql.Time</code>����.
    * The driver converts this
    * to an SQL <code>TIME</code> value when it sends it to the database.
    * SQL����Ҫ�ڽ����ֵ���͵����ݿ�ʱתΪSQL��<code>TIME</code>����.
    *
    * @param parameterIndex the first parameter is 1, the second is 2, ...
    *                       <p>��һ��������1, �ڶ�����2, ...
    * @param x the parameter value
    *          <p> ���������ֵ
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   void setTime(int parameterIndex, java.sql.Time x) throws ConfigurationException;

   /**
    * Sets the designated parameter to the given <code>java.sql.Time</code> value.
    * ������õĲ�����ΪJava��<code>java.sql.Time</code>����.
    * The driver converts this
    * to an SQL <code>TIME</code> value when it sends it to the database.
    * SQL����Ҫ�ڽ����ֵ���͵����ݿ�ʱתΪSQL��<code>TIME</code>����.
    *
    * @param parameterName  the parameter name
    *                       <p>�������������
    * @param x the parameter value
    *          <p> ���������ֵ
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   void setTime(String parameterName, java.sql.Time x) throws ConfigurationException;

   /**
    * Sets the designated parameter to the given <code>java.sql.Timestamp</code> value.
    * ������õĲ�����ΪJava��<code>java.sql.Timestamp</code>����.
    * The driver converts this
    * to an SQL <code>TIMESTAMP</code> value when it sends it to the database.
    * SQL����Ҫ�ڽ����ֵ���͵����ݿ�ʱתΪSQL��<code>TIMESTAMP</code>����.
    *
    * @param parameterIndex the first parameter is 1, the second is 2, ...
    *                       <p>��һ��������1, �ڶ�����2, ...
    * @param x the parameter value
    *          <p> ���������ֵ
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   void setTimestamp(int parameterIndex, java.sql.Timestamp x)
         throws ConfigurationException;

   /**
    * Sets the designated parameter to the given <code>java.sql.Timestamp</code> value.
    * ������õĲ�����ΪJava��<code>java.sql.Timestamp</code>����.
    * The driver converts this
    * to an SQL <code>TIMESTAMP</code> value when it sends it to the database.
    * SQL����Ҫ�ڽ����ֵ���͵����ݿ�ʱתΪSQL��<code>TIMESTAMP</code>����.
    *
    * @param parameterName  the parameter name
    *                       <p>�������������
    * @param x the parameter value
    *          <p> ���������ֵ
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   void setTimestamp(String parameterName, java.sql.Timestamp x)
         throws ConfigurationException;

   /*  *
    * Sets the designated parameter to the given input stream, which will have
    * the specified number of bytes.
    * When a very large ASCII value is input to a <code>LONGVARCHAR</code>
    * parameter, it may be more practical to send it via a
    * <code>java.io.InputStream</code>. Data will be read from the stream
    * as needed until end-of-file is reached.  The JDBC driver will
    * do any necessary conversion from ASCII to the database char format.
    *
    * <P><B>Note:</B> This stream object can either be a standard
    * Java stream object or your own subclass that implements the
    * standard interface.
    *
    * @param parameterIndex the first parameter is 1, the second is 2, ...
    * @param x the Java input stream that contains the ASCII parameter value
    * @param length the number of bytes in the stream
    * @exception java.sql.SQLException if a database access error occurs
    * δ֧��
   void setAsciiStream(int parameterIndex, java.io.InputStream x, int length)
         throws SQLException;
   */

   /* *
    * Sets the designated parameter to the given input stream, which
    * will have the specified number of bytes. A Unicode character has
    * two bytes, with the first byte being the high byte, and the second
    * being the low byte.
    *
    * When a very large Unicode value is input to a <code>LONGVARCHAR</code>
    * parameter, it may be more practical to send it via a
    * <code>java.io.InputStream</code> object. The data will be read from the
    * stream as needed until end-of-file is reached.  The JDBC driver will
    * do any necessary conversion from Unicode to the database char format.
    *
    * <P><B>Note:</B> This stream object can either be a standard
    * Java stream object or your own subclass that implements the
    * standard interface.
    *
    * @param parameterIndex the first parameter is 1, the second is 2, ...
    * @param x a <code>java.io.InputStream</code> object that contains the
    *        Unicode parameter value as two-byte Unicode characters
    * @param length the number of bytes in the stream
    * @exception java.sql.SQLException if a database access error occurs
    * @deprecated
    * δ֧��
    *
   void setUnicodeStream(int parameterIndex, java.io.InputStream x,
         int length) throws SQLException;
   */

   /**
    * Sets the designated parameter to the given input stream, which will have
    * the specified number of bytes.
    * When a very large binary value is input to a <code>LONGVARBINARY</code>
    * parameter, it may be more practical to send it via a
    * <code>java.io.InputStream</code> object. The data will be read from the
    * stream as needed until end-of-file is reached.
    *
    * <P><B>Note:</B> This stream object can either be a standard
    * Java stream object or your own subclass that implements the
    * standard interface.
    *
    * @param parameterIndex the first parameter is 1, the second is 2, ...
    * @param x the java input stream which contains the binary parameter value
    * @param length the number of bytes in the stream
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   void setBinaryStream(int parameterIndex, java.io.InputStream x, int length)
         throws ConfigurationException;

   /**
    * Sets the designated parameter to the given input stream, which will have
    * the specified number of bytes.
    * When a very large binary value is input to a <code>LONGVARBINARY</code>
    * parameter, it may be more practical to send it via a
    * <code>java.io.InputStream</code> object. The data will be read from the
    * stream as needed until end-of-file is reached.
    *
    * <P><B>Note:</B> This stream object can either be a standard
    * Java stream object or your own subclass that implements the
    * standard interface.
    *
    * @param parameterName  �������������
    * @param x the java input stream which contains the binary parameter value
    * @param length the number of bytes in the stream
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   void setBinaryStream(String parameterName, java.io.InputStream x, int length)
         throws ConfigurationException;

   /* *
    * Clears the current parameter values immediately.
    * <P>In general, parameter values remain in force for repeated use of a
    * statement. Setting a parameter value automatically clears its
    * previous value.  However, in some cases it is useful to immediately
    * release the resources used by the current parameter values; this can
    * be done by calling the method <code>clearParameters</code>.
    *
    * @exception java.sql.SQLException if a database access error occurs
    * δ֧��
    *
   void clearParameters() throws SQLException;
   */

   //----------------------------------------------------------------------
   // Advanced features:

   /* *
    * <p>Sets the value of the designated parameter with the given object. The second
    * argument must be an object type; for integral values, the
    * <code>java.lang</code> equivalent objects should be used.
    *
    * <p>The given Java object will be converted to the given targetSqlType
    * before being sent to the database.
    *
    * If the object has a custom mapping (is of a class implementing the
    * interface <code>SQLData</code>),
    * the JDBC driver should call the method <code>SQLData.writeSQL</code> to
    * write it to the SQL data stream.
    * If, on the other hand, the object is of a class implementing
    * <code>Ref</code>, <code>Blob</code>, <code>Clob</code>, <code>Struct</code>,
    * or <code>Array</code>, the driver should pass it to the database as a
    * value of the corresponding SQL type.
    *
    * <p>Note that this method may be used to pass database-specific
    * abstract data types.
    *
    * @param parameterIndex the first parameter is 1, the second is 2, ...
    * @param x the object containing the input parameter value
    * @param targetSqlType the SQL type (as defined in java.sql.Types) to be
    * sent to the database. The scale argument may further qualify this type.
    * @param scale for java.sql.Types.DECIMAL or java.sql.Types.NUMERIC types,
    *          this is the number of digits after the decimal point.  For all other
    *          types, this value will be ignored.
    * @see java.sql.Types
    * δ֧��
    *
   void setObject(int parameterIndex, Object x, int targetSqlType, int scale)
         throws SQLException;
   */

   /* *
    * Sets the value of the designated parameter with the given object.
    * This method is like the method <code>setObject</code>
    * above, except that it assumes a scale of zero.
    *
    * @param parameterIndex the first parameter is 1, the second is 2, ...
    * @param x the object containing the input parameter value
    * @param targetSqlType the SQL type (as defined in java.sql.Types) to be
    *                      sent to the database
    * @exception java.sql.SQLException if a database access error occurs
    * δ֧��
    *
   void setObject(int parameterIndex, Object x, int targetSqlType)
         throws SQLException;
   */

   /**
    * <p>Sets the value of the designated parameter using the given object.
    * The second parameter must be of type <code>Object</code>; therefore, the
    * <code>java.lang</code> equivalent objects should be used for built-in types.
    *
    * <p>The JDBC specification specifies a standard mapping from
    * Java <code>Object</code> types to SQL types.  The given argument
    * will be converted to the corresponding SQL type before being
    * sent to the database.
    *
    * <p>Note that this method may be used to pass datatabase-
    * specific abstract data types, by using a driver-specific Java
    * type.
    *
    * If the object is of a class implementing the interface <code>SQLData</code>,
    * the JDBC driver should call the method <code>SQLData.writeSQL</code>
    * to write it to the SQL data stream.
    * If, on the other hand, the object is of a class implementing
    * <code>Ref</code>, <code>Blob</code>, <code>Clob</code>, <code>Struct</code>,
    * or <code>Array</code>, the driver should pass it to the database as a
    * value of the corresponding SQL type.
    * <P>
    * This method throws an exception if there is an ambiguity, for example, if the
    * object is of a class implementing more than one of the interfaces named above.
    *
    * @param parameterIndex the first parameter is 1, the second is 2, ..
    *                       <p>��һ��������1, �ڶ�����2, ....
    * @param x the object containing the input parameter value
    *          <p> �����������ֵ�Ķ���
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   void setObject(int parameterIndex, Object x) throws ConfigurationException;

   /**
    * <p>Sets the value of the designated parameter using the given object.
    * The second parameter must be of type <code>Object</code>; therefore, the
    * <code>java.lang</code> equivalent objects should be used for built-in types.
    *
    * <p>The JDBC specification specifies a standard mapping from
    * Java <code>Object</code> types to SQL types.  The given argument
    * will be converted to the corresponding SQL type before being
    * sent to the database.
    *
    * <p>Note that this method may be used to pass datatabase-
    * specific abstract data types, by using a driver-specific Java
    * type.
    *
    * If the object is of a class implementing the interface <code>SQLData</code>,
    * the JDBC driver should call the method <code>SQLData.writeSQL</code>
    * to write it to the SQL data stream.
    * If, on the other hand, the object is of a class implementing
    * <code>Ref</code>, <code>Blob</code>, <code>Clob</code>, <code>Struct</code>,
    * or <code>Array</code>, the driver should pass it to the database as a
    * value of the corresponding SQL type.
    * <P>
    * This method throws an exception if there is an ambiguity, for example, if the
    * object is of a class implementing more than one of the interfaces named above.
    *
    * @param parameterName  the parameter name
    *                       <p>�������������
    * @param x the parameter value
    *          <p> ���������ֵ
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   void setObject(String parameterName, Object x) throws ConfigurationException;

   //--------------------------JDBC 2.0-----------------------------

   /* *
    * Adds a set of parameters to this <code>PreparedStatement</code>
    * object's batch of commands.
    *
    * @exception SQLException if a database access error occurs
    * @see java.sql.Statement#addBatch
    * @since 1.2
    * δ֧��
    *
   void addBatch() throws SQLException;
   */

   /**
    * Sets the designated parameter to the given <code>Reader</code>
    * object, which is the given number of characters long.
    * When a very large UNICODE value is input to a <code>LONGVARCHAR</code>
    * parameter, it may be more practical to send it via a
    * <code>java.io.Reader</code> object. The data will be read from the stream
    * as needed until end-of-file is reached.  The JDBC driver will
    * do any necessary conversion from UNICODE to the database char format.
    *
    * <P><B>Note:</B> This stream object can either be a standard
    * Java stream object or your own subclass that implements the
    * standard interface.
    *
    * @param parameterIndex the first parameter is 1, the second is 2, ...
    * @param reader the <code>java.io.Reader</code> object that contains the
    *        Unicode data
    * @param length the number of characters in the stream
    * @throws ConfigurationException     ��������ó���ʱ.
    * @since 1.2
    */
   void setCharacterStream(int parameterIndex, java.io.Reader reader, int length)
         throws ConfigurationException;

   /**
    * Sets the designated parameter to the given <code>Reader</code>
    * object, which is the given number of characters long.
    * When a very large UNICODE value is input to a <code>LONGVARCHAR</code>
    * parameter, it may be more practical to send it via a
    * <code>java.io.Reader</code> object. The data will be read from the stream
    * as needed until end-of-file is reached.  The JDBC driver will
    * do any necessary conversion from UNICODE to the database char format.
    *
    * <P><B>Note:</B> This stream object can either be a standard
    * Java stream object or your own subclass that implements the
    * standard interface.
    *
    * @param parameterName  �������������
    * @param reader the <code>java.io.Reader</code> object that contains the
    *        Unicode data
    * @param length the number of characters in the stream
    * @throws ConfigurationException     ��������ó���ʱ.
    * @since 1.2
    */
   void setCharacterStream(String parameterName, java.io.Reader reader, int length)
         throws ConfigurationException;

   /* *
    * Sets the designated parameter to the given
    *  <code>REF(&lt;structured-type&gt;)</code> value.
    * The driver converts this to an SQL <code>REF</code> value when it
    * sends it to the database.
    *
    * @param i the first parameter is 1, the second is 2, ...
    * @param x an SQL <code>REF</code> value
    * @exception java.sql.SQLException if a database access error occurs
    * @since 1.2
    * δ֧��
    *
   void setRef(int i, Ref x) throws SQLException;
   */

   /* *
    * Sets the designated parameter to the given <code>Blob</code> object.
    * The driver converts this to an SQL <code>BLOB</code> value when it
    * sends it to the database.
    *
    * @param i the first parameter is 1, the second is 2, ...
    * @param x a <code>Blob</code> object that maps an SQL <code>BLOB</code> value
    * @exception java.sql.SQLException if a database access error occurs
    * @since 1.2
    * δ֧��
    *
   void setBlob(int i, Blob x) throws SQLException;
   */

   /* *
    * Sets the designated parameter to the given <code>Clob</code> object.
    * The driver converts this to an SQL <code>CLOB</code> value when it
    * sends it to the database.
    *
    * @param i the first parameter is 1, the second is 2, ...
    * @param x a <code>Clob</code> object that maps an SQL <code>CLOB</code> value
    * @exception java.sql.SQLException if a database access error occurs
    * @since 1.2
    * δ֧��
    *
   void setClob(int i, Clob x) throws SQLException;
   */

   /* *
    * Sets the designated parameter to the given <code>Array</code> object.
    * The driver converts this to an SQL <code>ARRAY</code> value when it
    * sends it to the database.
    *
    * @param i the first parameter is 1, the second is 2, ...
    * @param x an <code>Array</code> object that maps an SQL <code>ARRAY</code> value
    * @exception java.sql.SQLException if a database access error occurs
    * @since 1.2
    * δ֧��
    *
   void setArray(int i, Array x) throws SQLException;
   */

   /* *
    * Retrieves a <code>ResultSetMetaData</code> object that contains
    * information about the columns of the <code>ResultSet</code> object
    * that will be returned when this <code>PreparedStatement</code> object
    * is executed.
    * <P>
    * Because a <code>PreparedStatement</code> object is precompiled, it is
    * possible to know about the <code>ResultSet</code> object that it will
    * return without having to execute it.  Consequently, it is possible
    * to invoke the method <code>getMetaData</code> on a
    * <code>PreparedStatement</code> object rather than waiting to execute
    * it and then invoking the <code>ResultSet.getMetaData</code> method
    * on the <code>ResultSet</code> object that is returned.
    * <P>
    * <B>NOTE:</B> Using this method may be expensive for some drivers due
    * to the lack of underlying DBMS support.
    *
    * @return the description of a <code>ResultSet</code> object's columns or
    *         <code>null</code> if the driver cannot return a
    *         <code>ResultSetMetaData</code> object
    * @exception java.sql.SQLException if a database access error occurs
    * @since 1.2
    * ��֧��
    *
   ResultSetMetaData getMetaData() throws SQLException;
   */

   /* *
    * Sets the designated parameter to the given <code>java.sql.Date</code> value,
    * using the given <code>Calendar</code> object.  The driver uses
    * the <code>Calendar</code> object to construct an SQL <code>DATE</code> value,
    * which the driver then sends to the database.  With
    * a <code>Calendar</code> object, the driver can calculate the date
    * taking into account a custom timezone.  If no
    * <code>Calendar</code> object is specified, the driver uses the default
    * timezone, which is that of the virtual machine running the application.
    *
    * @param parameterIndex the first parameter is 1, the second is 2, ...
    * @param x the parameter value
    * @param cal the <code>Calendar</code> object the driver will use
    *            to construct the date
    * @exception java.sql.SQLException if a database access error occurs
    * @since 1.2
    * δ֧��
    *
   void setDate(int parameterIndex, java.sql.Date x, Calendar cal)
         throws SQLException;
   */

   /* *
    * Sets the designated parameter to the given <code>java.sql.Time</code> value,
    * using the given <code>Calendar</code> object.  The driver uses
    * the <code>Calendar</code> object to construct an SQL <code>TIME</code> value,
    * which the driver then sends to the database.  With
    * a <code>Calendar</code> object, the driver can calculate the time
    * taking into account a custom timezone.  If no
    * <code>Calendar</code> object is specified, the driver uses the default
    * timezone, which is that of the virtual machine running the application.
    *
    * @param parameterIndex the first parameter is 1, the second is 2, ...
    * @param x the parameter value
    * @param cal the <code>Calendar</code> object the driver will use
    *            to construct the time
    * @exception java.sql.SQLException if a database access error occurs
    * @since 1.2
    * δ֧��
    *
   void setTime(int parameterIndex, java.sql.Time x, Calendar cal)
         throws SQLException;
   */

   /* *
    * Sets the designated parameter to the given <code>java.sql.Timestamp</code> value,
    * using the given <code>Calendar</code> object.  The driver uses
    * the <code>Calendar</code> object to construct an SQL <code>TIMESTAMP</code> value,
    * which the driver then sends to the database.  With a
    *  <code>Calendar</code> object, the driver can calculate the timestamp
    * taking into account a custom timezone.  If no
    * <code>Calendar</code> object is specified, the driver uses the default
    * timezone, which is that of the virtual machine running the application.
    *
    * @param parameterIndex the first parameter is 1, the second is 2, ...
    * @param x the parameter value
    * @param cal the <code>Calendar</code> object the driver will use
    *            to construct the timestamp
    * @exception java.sql.SQLException if a database access error occurs
    * @since 1.2
    * δ֧��
    *
   void setTimestamp(int parameterIndex, java.sql.Timestamp x, Calendar cal)
         throws SQLException;
   */

   /* *
    * Sets the designated parameter to SQL <code>NULL</code>.
    * This version of the method <code>setNull</code> should
    * be used for user-defined types and REF type parameters.  Examples
    * of user-defined types include: STRUCT, DISTINCT, JAVA_OBJECT, and
    * named array types.
    *
    * <P><B>Note:</B> To be portable, applications must give the
    * SQL type code and the fully-qualified SQL type name when specifying
    * a NULL user-defined or REF parameter.  In the case of a user-defined type
    * the name is the type name of the parameter itself.  For a REF
    * parameter, the name is the type name of the referenced type.  If
    * a JDBC driver does not need the type code or type name information,
    * it may ignore it.
    *
    * Although it is intended for user-defined and Ref parameters,
    * this method may be used to set a null parameter of any JDBC type.
    * If the parameter does not have a user-defined or REF type, the given
    * typeName is ignored.
    *
    *
    * @param paramIndex the first parameter is 1, the second is 2, ...
    * @param sqlType a value from <code>java.sql.Types</code>
    * @param typeName the fully-qualified name of an SQL user-defined type;
    *  ignored if the parameter is not a user-defined type or REF
    * @exception java.sql.SQLException if a database access error occurs
    * @since 1.2
    * δ֧��
    *
   void setNull(int paramIndex, int sqlType, String typeName)
         throws SQLException;
   */

   //------------------------- JDBC 3.0 -----------------------------------

   /* *
    * Sets the designated parameter to the given <code>java.net.URL</code> value.
    * The driver converts this to an SQL <code>DATALINK</code> value
    * when it sends it to the database.
    *
    * @param parameterIndex the first parameter is 1, the second is 2, ...
    * @param x the <code>java.net.URL</code> object to be set
    * @exception java.sql.SQLException if a database access error occurs
    * @since 1.4
    * δ֧��
    *
   void setURL(int parameterIndex, java.net.URL x) throws SQLException;
   */

   /* *
    * Retrieves the number, types and properties of this
    * <code>PreparedStatement</code> object's parameters.
    *
    * @return a <code>ParameterMetaData</code> object that contains information
    *         about the number, types and properties of this
    *         <code>PreparedStatement</code> object's parameters
    * @exception java.sql.SQLException if a database access error occurs
    * @see java.sql.ParameterMetaData
    * @since 1.4
    * ��֧��
    *
   ParameterMetaData getParameterMetaData() throws SQLException;
   */
}
