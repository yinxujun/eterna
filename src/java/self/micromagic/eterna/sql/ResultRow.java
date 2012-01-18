
package self.micromagic.eterna.sql;

import java.sql.SQLException;

import self.micromagic.eterna.digester.ConfigurationException;

public interface ResultRow
{
   /**
    * ��õ�ǰ��ResultRow���ڵ�ResultIterator.
    */
   ResultIterator getResultIterator() throws SQLException, ConfigurationException;

   /**
    * �ڵ�ǰ��<code>ResultRow</code>������ȡ����ʽ����ָ������.
    *
    * @param columnIndex  ����������ֵ, ��һ����1, �ڶ�����2, ....
    * @return ��ʽ���������.
    *
    * @exception SQLException ����������ݿ�ʱ����
    */
   String getFormated(int columnIndex) throws SQLException, ConfigurationException;

   /**
    * �ڵ�ǰ��<code>ResultRow</code>������ȡ����ʽ����ָ������.
    *
    * @param columnName  ����������.
    * @return ��ʽ���������.
    *
    * @exception SQLException ����������ݿ�ʱ����
    */
   String getFormated(String columnName) throws SQLException, ConfigurationException;

   /**
    * Reports whether
    * the last column read had a value of SQL <code>NULL</code>.
    * �������һ�ζ�ȡ����ֵ�Ƿ�ΪSQL��<code>NULL</code>.
    * Note that you must first call one of the getter methods
    * on a column to try to read its value and then call
    * the method <code>wasNull</code> to see if the value read was
    * SQL <code>NULL</code>.
    *
    * @return <code>true</code> if the last column value read was SQL
    *         <code>NULL</code> and <code>false</code> otherwise
    *         �����һ�ζ�ȡ����ֵ��SQL��<code>NULL</code>ʱ�򷵻�
    *         <code>true</code>���򷵻�<code>false</code>
    * @exception SQLException if a database access error occurs
    *                         <p>����������ݿ�ʱ����
    */
   boolean wasNull() throws SQLException, ConfigurationException;

   //======================================================================
   // Methods for accessing results by column index
   //======================================================================

   /**
    * Retrieves the value of the designated column in the current row
    * of this <code>ResultSet</code> object as
    * a <code>String</code> in the Java programming language.
    * �ڵ�ǰ��<code>ResultRow</code>��������<code>String</code>����ʽ
    * ȡ��ָ���е�ֵ.
    *
    * @param columnIndex the first column is 1, the second is 2, ...
    *                    <p>��һ��������1, �ڶ�����2, ...
    * @return the column value; if the value is SQL <code>NULL</code>, the
    * value returned is <code>null</code>
    *         <p>����е�ֵ; ������ֵ��SQL��<code>NULL</code>, �򷵻�ֵ
    * ��<code>null</code>
    * @exception SQLException if a database access error occurs
    *                         <p>����������ݿ�ʱ����
    */
   String getString(int columnIndex) throws SQLException, ConfigurationException;

   /**
    * Retrieves the value of the designated column in the current row
    * of this <code>ResultSet</code> object as
    * a <code>boolean</code> in the Java programming language.
    * �ڵ�ǰ��<code>ResultRow</code>��������<code>boolean</code>����ʽ
    * ȡ��ָ���е�ֵ.
    *
    * @param columnIndex the first column is 1, the second is 2, ...
    *                    <p>��һ��������1, �ڶ�����2, ...
    * @return the column value; if the value is SQL <code>NULL</code>, the
    * value returned is <code>false</code>
    *         <p>����е�ֵ; ������ֵ��SQL��<code>NULL</code>, �򷵻�ֵ
    * ��<code>false</code>
    * @exception SQLException if a database access error occurs
    *                         <p>����������ݿ�ʱ����
    */
   boolean getBoolean(int columnIndex) throws SQLException, ConfigurationException;

   /**
    * Retrieves the value of the designated column in the current row
    * of this <code>ResultSet</code> object as
    * a <code>byte</code> in the Java programming language.
    * �ڵ�ǰ��<code>ResultRow</code>��������<code>byte</code>����ʽ
    * ȡ��ָ���е�ֵ.
    *
    * @param columnIndex the first column is 1, the second is 2, ...
    *                    <p>��һ��������1, �ڶ�����2, ...
    * @return the column value; if the value is SQL <code>NULL</code>, the
    * value returned is <code>0</code>
    *         <p>����е�ֵ; ������ֵ��SQL��<code>NULL</code>, �򷵻�ֵ
    * ��<code>0</code>
    * @exception SQLException if a database access error occurs
    *                         <p>����������ݿ�ʱ����
    */
   byte getByte(int columnIndex) throws SQLException, ConfigurationException;

   /**
    * Retrieves the value of the designated column in the current row
    * of this <code>ResultSet</code> object as
    * a <code>short</code> in the Java programming language.
    * �ڵ�ǰ��<code>ResultRow</code>��������<code>short</code>����ʽ
    * ȡ��ָ���е�ֵ.
    *
    * @param columnIndex the first column is 1, the second is 2, ...
    *                    <p>��һ��������1, �ڶ�����2, ...
    * @return the column value; if the value is SQL <code>NULL</code>, the
    * value returned is <code>0</code>
    *         <p>����е�ֵ; ������ֵ��SQL��<code>NULL</code>, �򷵻�ֵ
    * ��<code>0</code>
    * @exception SQLException if a database access error occurs
    *                         <p>����������ݿ�ʱ����
    */
   short getShort(int columnIndex) throws SQLException, ConfigurationException;

   /**
    * Retrieves the value of the designated column in the current row
    * of this <code>ResultSet</code> object as
    * an <code>int</code> in the Java programming language.
    * �ڵ�ǰ��<code>ResultRow</code>��������<code>int</code>����ʽ
    * ȡ��ָ���е�ֵ.
    *
    * @param columnIndex the first column is 1, the second is 2, ...
    *                    <p>��һ��������1, �ڶ�����2, ...
    * @return the column value; if the value is SQL <code>NULL</code>, the
    * value returned is <code>0</code>
    *         <p>����е�ֵ; ������ֵ��SQL��<code>NULL</code>, �򷵻�ֵ
    * ��<code>0</code>
    * @exception SQLException if a database access error occurs
    *                         <p>����������ݿ�ʱ����
    */
   int getInt(int columnIndex) throws SQLException, ConfigurationException;

   /**
    * Retrieves the value of the designated column in the current row
    * of this <code>ResultSet</code> object as
    * a <code>long</code> in the Java programming language.
    * �ڵ�ǰ��<code>ResultRow</code>��������<code>long</code>����ʽ
    * ȡ��ָ���е�ֵ.
    *
    * @param columnIndex the first column is 1, the second is 2, ...
    *                    <p>��һ��������1, �ڶ�����2, ...
    * @return the column value; if the value is SQL <code>NULL</code>, the
    * value returned is <code>0</code>
    *         <p>����е�ֵ; ������ֵ��SQL��<code>NULL</code>, �򷵻�ֵ
    * ��<code>0</code>
    * @exception SQLException if a database access error occurs
    *                         <p>����������ݿ�ʱ����
    */
   long getLong(int columnIndex) throws SQLException, ConfigurationException;

   /**
    * Retrieves the value of the designated column in the current row
    * of this <code>ResultSet</code> object as
    * a <code>float</code> in the Java programming language.
    * �ڵ�ǰ��<code>ResultRow</code>��������<code>float</code>����ʽ
    * ȡ��ָ���е�ֵ.
    *
    * @param columnIndex the first column is 1, the second is 2, ...
    *                    <p>��һ��������1, �ڶ�����2, ...
    * @return the column value; if the value is SQL <code>NULL</code>, the
    * value returned is <code>0</code>
    *         <p>����е�ֵ; ������ֵ��SQL��<code>NULL</code>, �򷵻�ֵ
    * ��<code>0</code>
    * @exception SQLException if a database access error occurs
    *                         <p>����������ݿ�ʱ����
    */
   float getFloat(int columnIndex) throws SQLException, ConfigurationException;

   /**
    * Retrieves the value of the designated column in the current row
    * of this <code>ResultSet</code> object as
    * a <code>double</code> in the Java programming language.
    * �ڵ�ǰ��<code>ResultRow</code>��������<code>double</code>����ʽ
    * ȡ��ָ���е�ֵ.
    *
    * @param columnIndex the first column is 1, the second is 2, ...
    *                    <p>��һ��������1, �ڶ�����2, ...
    * @return the column value; if the value is SQL <code>NULL</code>, the
    * value returned is <code>0</code>
    *         <p>����е�ֵ; ������ֵ��SQL��<code>NULL</code>, �򷵻�ֵ
    * ��<code>0</code>
    * @exception SQLException if a database access error occurs
    *                         <p>����������ݿ�ʱ����
    */
   double getDouble(int columnIndex) throws SQLException, ConfigurationException;

   /**
    * Retrieves the value of the designated column in the current row
    * of this <code>ResultSet</code> object as
    * a <code>byte</code> array in the Java programming language.
    * The bytes represent the raw values returned by the driver.
    * �ڵ�ǰ��<code>ResultRow</code>��������<code>byte</code>�������ʽ
    * ȡ��ָ���е�ֵ.
    *
    * @param columnIndex the first column is 1, the second is 2, ...
    *                    <p>��һ��������1, �ڶ�����2, ...
    * @return the column value; if the value is SQL <code>NULL</code>, the
    * value returned is <code>null</code>
    *         <p>����е�ֵ; ������ֵ��SQL��<code>NULL</code>, �򷵻�ֵ
    * ��<code>null</code>
    * @exception SQLException if a database access error occurs
    *                         <p>����������ݿ�ʱ����
    */
   byte[] getBytes(int columnIndex) throws SQLException, ConfigurationException;

   /**
    * Retrieves the value of the designated column in the current row
    * of this <code>ResultSet</code> object as
    * a <code>java.sql.Date</code> object in the Java programming language.
    * �ڵ�ǰ��<code>ResultRow</code>��������<code>java.sql.Date</code>����ʽ
    * ȡ��ָ���е�ֵ.
    *
    * @param columnIndex the first column is 1, the second is 2, ...
    *                    <p>��һ��������1, �ڶ�����2, ...
    * @return the column value; if the value is SQL <code>NULL</code>, the
    * value returned is <code>null</code>
    *         <p>����е�ֵ; ������ֵ��SQL��<code>NULL</code>, �򷵻�ֵ
    * ��<code>null</code>
    * @exception SQLException if a database access error occurs
    *                         <p>����������ݿ�ʱ����
    */
   java.sql.Date getDate(int columnIndex) throws SQLException, ConfigurationException;

   /**
    * Retrieves the value of the designated column in the current row
    * of this <code>ResultSet</code> object as
    * a <code>java.sql.Time</code> object in the Java programming language.
    * �ڵ�ǰ��<code>ResultRow</code>��������<code>java.sql.Time</code>����ʽ
    * ȡ��ָ���е�ֵ.
    *
    * @param columnIndex the first column is 1, the second is 2, ...
    *                    <p>��һ��������1, �ڶ�����2, ...
    * @return the column value; if the value is SQL <code>NULL</code>, the
    * value returned is <code>null</code>
    *         <p>����е�ֵ; ������ֵ��SQL��<code>NULL</code>, �򷵻�ֵ
    * ��<code>null</code>
    * @exception SQLException if a database access error occurs
    *                         <p>����������ݿ�ʱ����
    */
   java.sql.Time getTime(int columnIndex) throws SQLException, ConfigurationException;

   /**
    * Retrieves the value of the designated column in the current row
    * of this <code>ResultSet</code> object as
    * a <code>java.sql.Timestamp</code> object in the Java programming language.
    * �ڵ�ǰ��<code>ResultRow</code>��������<code>java.sql.Timestamp</code>����ʽ
    * ȡ��ָ���е�ֵ.
    *
    * @param columnIndex the first column is 1, the second is 2, ...
    *                    <p>��һ��������1, �ڶ�����2, ...
    * @return the column value; if the value is SQL <code>NULL</code>, the
    * value returned is <code>null</code>
    *         <p>����е�ֵ; ������ֵ��SQL��<code>NULL</code>, �򷵻�ֵ
    * ��<code>null</code>
    * @exception SQLException if a database access error occurs
    *                         <p>����������ݿ�ʱ����
    */
   java.sql.Timestamp getTimestamp(int columnIndex) throws SQLException, ConfigurationException;

   /**
    * Retrieves the value of the designated column in the current row
    * of this <code>ResultSet</code> object as a binary stream of
    * uninterpreted bytes. The value can then be read in chunks from the
    * stream. This method is particularly
    * suitable for retrieving large <code>LONGVARBINARY</code> values.
    *
    * <P><B>Note:</B> All the data in the returned stream must be
    * read prior to getting the value of any other column. The next
    * call to a getter method implicitly closes the stream.  Also, a
    * stream may return <code>0</code> when the method
    * <code>InputStream.available</code>
    * is called whether there is data available or not.
    *
    * @param columnIndex the first column is 1, the second is 2, ...
    * @return a Java input stream that delivers the database column value
    *         as a stream of uninterpreted bytes;
    *         if the value is SQL <code>NULL</code>, the value returned is
    *         <code>null</code>
    * @exception SQLException if a database access error occurs
    *
    */
   java.io.InputStream getBinaryStream(int columnIndex) throws SQLException, ConfigurationException;

   //======================================================================
   // Methods for accessing results by column name
   //======================================================================

   /**
    * Retrieves the value of the designated column in the current row
    * of this <code>ResultSet</code> object as
    * a <code>String</code> in the Java programming language.
    * �ڵ�ǰ��<code>ResultRow</code>��������<code>String</code>����ʽ
    * ȡ��ָ���е�ֵ.
    *
    * @param columnName the SQL name of the column
    *                   <p>����е�SQL����
    * @return the column value; if the value is SQL <code>NULL</code>, the
    * value returned is <code>null</code>
    *         <p>����е�ֵ; ������ֵ��SQL��<code>NULL</code>, �򷵻�ֵ
    * ��<code>null</code>
    * @exception SQLException if a database access error occurs
    *                         <p>����������ݿ�ʱ����
    */
   String getString(String columnName) throws SQLException, ConfigurationException;

   /**
    * Retrieves the value of the designated column in the current row
    * of this <code>ResultSet</code> object as
    * a <code>boolean</code> in the Java programming language.
    * �ڵ�ǰ��<code>ResultRow</code>��������<code>boolean</code>����ʽ
    * ȡ��ָ���е�ֵ.
    *
    * @param columnName the SQL name of the column
    *                   <p>����е�SQL����
    * @return the column value; if the value is SQL <code>NULL</code>, the
    * value returned is <code>false</code>
    *         <p>����е�ֵ; ������ֵ��SQL��<code>NULL</code>, �򷵻�ֵ
    * ��<code>false</code>
    * @exception SQLException if a database access error occurs
    *                         <p>����������ݿ�ʱ����
    */
   boolean getBoolean(String columnName) throws SQLException, ConfigurationException;

   /**
    * Retrieves the value of the designated column in the current row
    * of this <code>ResultSet</code> object as
    * a <code>byte</code> in the Java programming language.
    * �ڵ�ǰ��<code>ResultRow</code>��������<code>byte</code>����ʽ
    * ȡ��ָ���е�ֵ.
    *
    * @param columnName the SQL name of the column
    *                   <p>����е�SQL����
    * @return the column value; if the value is SQL <code>NULL</code>, the
    * value returned is <code>0</code>
    *         <p>����е�ֵ; ������ֵ��SQL��<code>NULL</code>, �򷵻�ֵ
    * ��<code>0</code>
    * @exception SQLException if a database access error occurs
    *                         <p>����������ݿ�ʱ����
    */
   byte getByte(String columnName) throws SQLException, ConfigurationException;

   /**
    * Retrieves the value of the designated column in the current row
    * of this <code>ResultSet</code> object as
    * a <code>short</code> in the Java programming language.
    * �ڵ�ǰ��<code>ResultRow</code>��������<code>short</code>����ʽ
    * ȡ��ָ���е�ֵ.
    *
    * @param columnName the SQL name of the column
    *                   <p>����е�SQL����
    * @return the column value; if the value is SQL <code>NULL</code>, the
    * value returned is <code>0</code>
    *         <p>����е�ֵ; ������ֵ��SQL��<code>NULL</code>, �򷵻�ֵ
    * ��<code>0</code>
    * @exception SQLException if a database access error occurs
    *                         <p>����������ݿ�ʱ����
    */
   short getShort(String columnName) throws SQLException, ConfigurationException;

   /**
    * Retrieves the value of the designated column in the current row
    * of this <code>ResultSet</code> object as
    * an <code>int</code> in the Java programming language.
    * �ڵ�ǰ��<code>ResultRow</code>��������<code>int</code>����ʽ
    * ȡ��ָ���е�ֵ.
    *
    * @param columnName the SQL name of the column
    *                   <p>����е�SQL����
    * @return the column value; if the value is SQL <code>NULL</code>, the
    * value returned is <code>0</code>
    *         <p>����е�ֵ; ������ֵ��SQL��<code>NULL</code>, �򷵻�ֵ
    * ��<code>0</code>
    * @exception SQLException if a database access error occurs
    *                         <p>����������ݿ�ʱ����
    */
   int getInt(String columnName) throws SQLException, ConfigurationException;

   /**
    * Retrieves the value of the designated column in the current row
    * of this <code>ResultSet</code> object as
    * a <code>long</code> in the Java programming language.
    * �ڵ�ǰ��<code>ResultRow</code>��������<code>long</code>����ʽ
    * ȡ��ָ���е�ֵ.
    *
    * @param columnName the SQL name of the column
    *                   <p>����е�SQL����
    * @return the column value; if the value is SQL <code>NULL</code>, the
    * value returned is <code>0</code>
    *         <p>����е�ֵ; ������ֵ��SQL��<code>NULL</code>, �򷵻�ֵ
    * ��<code>0</code>
    * @exception SQLException if a database access error occurs
    *                         <p>����������ݿ�ʱ����
    */
   long getLong(String columnName) throws SQLException, ConfigurationException;

   /**
    * Retrieves the value of the designated column in the current row
    * of this <code>ResultSet</code> object as
    * a <code>float</code> in the Java programming language.
    * �ڵ�ǰ��<code>ResultRow</code>��������<code>float</code>����ʽ
    * ȡ��ָ���е�ֵ.
    *
    * @param columnName the SQL name of the column
    *                   <p>����е�SQL����
    * @return the column value; if the value is SQL <code>NULL</code>, the
    * value returned is <code>0</code>
    *         <p>����е�ֵ; ������ֵ��SQL��<code>NULL</code>, �򷵻�ֵ
    * ��<code>0</code>
    * @exception SQLException if a database access error occurs
    *                         <p>����������ݿ�ʱ����
    */
   float getFloat(String columnName) throws SQLException, ConfigurationException;

   /**
    * Retrieves the value of the designated column in the current row
    * of this <code>ResultSet</code> object as
    * a <code>double</code> in the Java programming language.
    * �ڵ�ǰ��<code>ResultRow</code>��������<code>double</code>����ʽ
    * ȡ��ָ���е�ֵ.
    *
    * @param columnName the SQL name of the column
    *                   <p>����е�SQL����
    * @return the column value; if the value is SQL <code>NULL</code>, the
    * value returned is <code>0</code>
    *         <p>����е�ֵ; ������ֵ��SQL��<code>NULL</code>, �򷵻�ֵ
    * ��<code>0</code>
    * @exception SQLException if a database access error occurs
    *                         <p>����������ݿ�ʱ����
    */
   double getDouble(String columnName) throws SQLException, ConfigurationException;

   /**
    * Retrieves the value of the designated column in the current row
    * of this <code>ResultSet</code> object as
    * a <code>byte</code> array in the Java programming language.
    * The bytes represent the raw values returned by the driver.
    * �ڵ�ǰ��<code>ResultRow</code>��������<code>byte</code>��������ʽ
    * ȡ��ָ���е�ֵ.
    *
    * @param columnName the SQL name of the column
    *                   <p>����е�SQL����
    * @return the column value; if the value is SQL <code>NULL</code>, the
    * value returned is <code>null</code>
    *         <p>����е�ֵ; ������ֵ��SQL��<code>NULL</code>, �򷵻�ֵ
    * ��<code>null</code>
    * @exception SQLException if a database access error occurs
    *                         <p>����������ݿ�ʱ����
    */
   byte[] getBytes(String columnName) throws SQLException, ConfigurationException;

   /**
    * Retrieves the value of the designated column in the current row
    * of this <code>ResultSet</code> object as
    * a <code>java.sql.Date</code> object in the Java programming language.
    * �ڵ�ǰ��<code>ResultRow</code>��������<code>java.sql.Date</code>����ʽ
    * ȡ��ָ���е�ֵ.
    *
    * @param columnName the SQL name of the column
    *                   <p>����е�SQL����
    * @return the column value; if the value is SQL <code>NULL</code>, the
    * value returned is <code>null</code>
    *         <p>����е�ֵ; ������ֵ��SQL��<code>NULL</code>, �򷵻�ֵ
    * ��<code>null</code>
    * @exception SQLException if a database access error occurs
    *                         <p>����������ݿ�ʱ����
    */
   java.sql.Date getDate(String columnName) throws SQLException, ConfigurationException;

   /**
    * Retrieves the value of the designated column in the current row
    * of this <code>ResultSet</code> object as
    * a <code>java.sql.Time</code> object in the Java programming language.
    * �ڵ�ǰ��<code>ResultRow</code>��������<code>java.sql.Time</code>����ʽ
    * ȡ��ָ���е�ֵ.
    *
    * @param columnName the SQL name of the column
    *                   <p>����е�SQL����
    * @return the column value; if the value is SQL <code>NULL</code>, the
    * value returned is <code>null</code>
    *         <p>����е�ֵ; ������ֵ��SQL��<code>NULL</code>, �򷵻�ֵ
    * ��<code>null</code>
    * @exception SQLException if a database access error occurs
    *                         <p>����������ݿ�ʱ����
    */
   java.sql.Time getTime(String columnName) throws SQLException, ConfigurationException;

   /**
    * Retrieves the value of the designated column in the current row
    * of this <code>ResultSet</code> object as
    * a <code>java.sql.Timestamp</code> object.
    * �ڵ�ǰ��<code>ResultRow</code>��������<code>java.sql.Timestamp</code>����ʽ
    * ȡ��ָ���е�ֵ.
    *
    * @param columnName the SQL name of the column
    *                   <p>����е�SQL����
    * @return the column value; if the value is SQL <code>NULL</code>, the
    * value returned is <code>null</code>
    *         <p>����е�ֵ; ������ֵ��SQL��<code>NULL</code>, �򷵻�ֵ
    * ��<code>null</code>
    * @exception SQLException if a database access error occurs
    *                         <p>����������ݿ�ʱ����
    */
   java.sql.Timestamp getTimestamp(String columnName) throws SQLException, ConfigurationException;

   /**
    * Retrieves the value of the designated column in the current row
    * of this <code>ResultSet</code> object as a stream of uninterpreted
    * <code>byte</code>s.
    * The value can then be read in chunks from the
    * stream. This method is particularly
    * suitable for retrieving large <code>LONGVARBINARY</code>
    * values.
    *
    * <P><B>Note:</B> All the data in the returned stream must be
    * read prior to getting the value of any other column. The next
    * call to a getter method implicitly closes the stream. Also, a
    * stream may return <code>0</code> when the method <code>available</code>
    * is called whether there is data available or not.
    *
    * @param columnName the SQL name of the column
    * @return a Java input stream that delivers the database column value
    * as a stream of uninterpreted bytes;
    * if the value is SQL <code>NULL</code>, the result is <code>null</code>
    * @exception SQLException if a database access error occurs
    */
   java.io.InputStream getBinaryStream(String columnName) throws SQLException, ConfigurationException;


   //=====================================================================
   // Advanced features:
   //=====================================================================

   /**
    * <p>Gets the value of the designated column in the current row
    * of this <code>ResultSet</code> object as
    * an <code>Object</code> in the Java programming language.
    * �ڵ�ǰ��<code>ResultRow</code>��������<code>Object</code>����ʽ
    * ȡ��ָ���е�ֵ.
    *
    * <p>This method will return the value of the given column as a
    * Java object.  The type of the Java object will be the default
    * Java object type corresponding to the column's SQL type,
    * following the mapping for built-in types specified in the JDBC
    * specification. If the value is an SQL <code>NULL</code>,
    * the driver returns a Java <code>null</code>.
    *
    * <p>This method may also be used to read database-specific
    * abstract data types.
    *
    * In the JDBC 2.0 API, the behavior of method
    * <code>getObject</code> is extended to materialize
    * data of SQL user-defined types.  When a column contains
    * a structured or distinct value, the behavior of this method is as
    * if it were a call to: <code>getObject(columnIndex,
    * this.getStatement().getConnection().getTypeMap())</code>.
    *
    * @param columnIndex the first column is 1, the second is 2, ...
    *                    <p>��һ��������1, �ڶ�����2, ...
    * @return a <code>java.lang.Object</code> holding the column value
    *         ���и���ֵ��<code>java.lang.Object</code>
    * @exception SQLException if a database access error occurs
    *                         <p>����������ݿ�ʱ����
    */
   Object getObject(int columnIndex) throws SQLException, ConfigurationException;

   /**
    * <p>Gets the value of the designated column in the current row
    * of this <code>ResultSet</code> object as
    * an <code>Object</code> in the Java programming language.
    * �ڵ�ǰ��<code>ResultRow</code>��������<code>Object</code>����ʽ
    * ȡ��ָ���е�ֵ.
    *
    * <p>This method will return the value of the given column as a
    * Java object.  The type of the Java object will be the default
    * Java object type corresponding to the column's SQL type,
    * following the mapping for built-in types specified in the JDBC
    * specification. If the value is an SQL <code>NULL</code>,
    * the driver returns a Java <code>null</code>.
    * <P>
    * This method may also be used to read database-specific
    * abstract data types.
    * <P>
    * In the JDBC 2.0 API, the behavior of the method
    * <code>getObject</code> is extended to materialize
    * data of SQL user-defined types.  When a column contains
    * a structured or distinct value, the behavior of this method is as
    * if it were a call to: <code>getObject(columnIndex,
    * this.getStatement().getConnection().getTypeMap())</code>.
    *
    * @param columnName the SQL name of the column
    *                   <p>����е�SQL����
    * @return a <code>java.lang.Object</code> holding the column value
    *         ���и���ֵ��<code>java.lang.Object</code>
    * @exception SQLException if a database access error occurs
    *                         <p>����������ݿ�ʱ����
    */
   Object getObject(String columnName) throws SQLException, ConfigurationException;

   //----------------------------------------------------------------

   /**
    * Maps the given <code>ResultSet</code> column name to its
    * <code>ResultSet</code> column index.
    * ��<code>ResultRow</code>�н�����ӳ��δ�е�����ֵ.
    *
    * @param columnName the name of the column
    *                   <p>���е�����
    * @return the column index of the given column name
    *         ��������������ֵ
    * @exception SQLException if the <code>ResultSet</code> object
    * does not contain <code>columnName</code> or a database access error occurs
    *                         ����<code>ResultRow</code>�����в����ڸ�������
    * �������ݿ�ʱ��������
    */
   int findColumn(String columnName) throws SQLException, ConfigurationException;


   //--------------------------JDBC 2.0-----------------------------------

   //---------------------------------------------------------------------
   // Getters and Setters
   //---------------------------------------------------------------------

   /**
    * Retrieves the value of the designated column in the current row
    * of this <code>ResultSet</code> object as a
    * <code>java.io.Reader</code> object.
    * @return a <code>java.io.Reader</code> object that contains the column
    * value; if the value is SQL <code>NULL</code>, the value returned is
    * <code>null</code> in the Java programming language.
    * @param columnIndex the first column is 1, the second is 2, ...
    * @exception SQLException if a database access error occurs
    * @since 1.2
    */
   java.io.Reader getCharacterStream(int columnIndex) throws SQLException, ConfigurationException;

   /**
    * Retrieves the value of the designated column in the current row
    * of this <code>ResultSet</code> object as a
    * <code>java.io.Reader</code> object.
    *
    * @param columnName the name of the column
    * @return a <code>java.io.Reader</code> object that contains the column
    * value; if the value is SQL <code>NULL</code>, the value returned is
    * <code>null</code> in the Java programming language
    * @exception SQLException if a database access error occurs
    * @since 1.2
    */
   java.io.Reader getCharacterStream(String columnName) throws SQLException, ConfigurationException;

}
