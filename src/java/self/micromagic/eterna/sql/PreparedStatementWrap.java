
package self.micromagic.eterna.sql;

import java.sql.SQLException;
import java.util.Calendar;

public interface PreparedStatementWrap
{
   void setNull(String parameterName, int parameterIndex, int sqlType) throws SQLException;

   void setBoolean(String parameterName, int parameterIndex, boolean x) throws SQLException;

   void setByte(String parameterName, int parameterIndex, byte x) throws SQLException;

   void setShort(String parameterName, int parameterIndex, short x) throws SQLException;

   void setInt(String parameterName, int parameterIndex, int x) throws SQLException;

   void setLong(String parameterName, int parameterIndex, long x) throws SQLException;

   void setFloat(String parameterName, int parameterIndex, float x) throws SQLException;

   void setDouble(String parameterName, int parameterIndex, double x) throws SQLException;

   void setString(String parameterName, int parameterIndex, String x) throws SQLException;

   void setBytes(String parameterName, int parameterIndex, byte[] x) throws SQLException;

   void setDate(String parameterName, int parameterIndex, java.sql.Date x) throws SQLException;

   void setDate(String parameterName, int parameterIndex, java.sql.Date x, Calendar cal)
	      throws SQLException;

   void setTime(String parameterName, int parameterIndex, java.sql.Time x) throws SQLException;

   void setTime(String parameterName, int parameterIndex, java.sql.Time x, Calendar cal)
	      throws SQLException;

   void setTimestamp(String parameterName, int parameterIndex, java.sql.Timestamp x) throws SQLException;

   void setTimestamp(String parameterName, int parameterIndex, java.sql.Timestamp x, Calendar cal)
	      throws SQLException;

   void setBinaryStream(String parameterName, int parameterIndex, java.io.InputStream x, int length)
         throws SQLException;

   void setCharacterStream(String parameterName, int parameterIndex, java.io.Reader x, int length)
         throws SQLException;

   void setObject(String parameterName, int parameterIndex, Object x) throws SQLException;

   void setObject(String parameterName, int parameterIndex, Object x, int targetSqlType) throws SQLException;

   void setObject(String parameterName, int parameterIndex, Object x, int targetSqlType, int scale)
         throws SQLException;

}
