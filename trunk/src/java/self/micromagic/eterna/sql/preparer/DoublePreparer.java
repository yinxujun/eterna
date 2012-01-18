
package self.micromagic.eterna.sql.preparer;

import java.sql.SQLException;

import self.micromagic.eterna.sql.PreparedStatementWrap;

public class DoublePreparer extends AbstractValuePreparer
{
   private double value;

   public DoublePreparer(int index, double value)
   {
      this.setRelativeIndex(index);
      this.value = value;
   }

   public void setValueToStatement(int index, PreparedStatementWrap stmtWrap)
         throws SQLException
   {
      stmtWrap.setDouble(this.getName(), index, this.value);
   }

}
