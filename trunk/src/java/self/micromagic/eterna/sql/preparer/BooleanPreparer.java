
package self.micromagic.eterna.sql.preparer;

import java.sql.SQLException;

import self.micromagic.eterna.sql.PreparedStatementWrap;

public class BooleanPreparer extends AbstractValuePreparer
{
   private boolean value;

   public BooleanPreparer(int index, boolean value)
   {
      this.setRelativeIndex(index);
      this.value = value;
   }

   public void setValueToStatement(int index, PreparedStatementWrap stmtWrap)
         throws SQLException
   {
      stmtWrap.setBoolean(this.getName(), index, this.value);
   }

}
