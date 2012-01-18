
package self.micromagic.eterna.sql.preparer;

import java.io.InputStream;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import self.micromagic.eterna.sql.PreparedStatementWrap;
import self.micromagic.util.Utility;

public class StreamPreparer extends AbstractValuePreparer
{
   private InputStream value;
   private int length;

   public StreamPreparer(int index, InputStream value, int length)
   {
      this.setRelativeIndex(index);
      this.length = length;
      this.value = value;
   }
   public StreamPreparer(int index, InputStream value)
   {
      this.setRelativeIndex(index);
      this.length = -1;
      this.value = value;
   }

   public void setValueToStatement(int index, PreparedStatementWrap stmtWrap)
         throws SQLException
   {
      if (this.length == -1)
      {
         stmtWrap.setObject(this.getName(), index, this.value, java.sql.Types.LONGVARBINARY);
      }
      else
      {
         stmtWrap.setBinaryStream(this.getName(), index, this.value, this.length);
      }
   }

}