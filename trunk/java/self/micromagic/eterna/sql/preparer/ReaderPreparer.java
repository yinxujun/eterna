
package self.micromagic.eterna.sql.preparer;

import java.io.Reader;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import self.micromagic.eterna.sql.PreparedStatementWrap;
import self.micromagic.util.Utility;

public class ReaderPreparer extends AbstractValuePreparer
{
   private Reader value;
   private int length;

   public ReaderPreparer(int index, Reader value, int length)
   {
      this.setRelativeIndex(index);
      this.length = length;
      this.value = value;
   }

   public ReaderPreparer(int index, Reader value)
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
         stmtWrap.setObject(this.getName(), index, this.value, java.sql.Types.LONGVARCHAR);
      }
      else
      {
         stmtWrap.setCharacterStream(this.getName(), index, this.value, this.length);
      }
   }

}