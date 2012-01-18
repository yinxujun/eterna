
package self.micromagic.eterna.sql.preparer;

import java.sql.SQLException;

import self.micromagic.eterna.sql.PreparedStatementWrap;

public class IntegerPreparer extends AbstractValuePreparer
{
   private int value;

   public IntegerPreparer(int index, int value)
   {
      this.setRelativeIndex(index);
      this.value = value;
   }

   public void setValueToStatement(int index, PreparedStatementWrap stmtWrap)
         throws SQLException
   {
      stmtWrap.setInt(this.getName(), index, this.value);
   }

}
