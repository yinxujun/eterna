
package self.micromagic.eterna.sql.preparer;

import java.sql.SQLException;

import self.micromagic.eterna.sql.PreparedStatementWrap;

public class BytesPreparer extends AbstractValuePreparer
{
   private byte[] value;

   public BytesPreparer(int index, byte[] value)
   {
      this.setRelativeIndex(index);
      this.value = value;
   }

   public void setValueToStatement(int index, PreparedStatementWrap stmtWrap)
         throws SQLException
   {
      stmtWrap.setBytes(this.getName(), index, this.value);
   }

}