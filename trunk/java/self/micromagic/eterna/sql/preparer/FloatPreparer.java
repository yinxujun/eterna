
package self.micromagic.eterna.sql.preparer;

import java.sql.SQLException;

import self.micromagic.eterna.sql.PreparedStatementWrap;

public class FloatPreparer extends AbstractValuePreparer
{
   private float value;

   public FloatPreparer(int index, float value)
   {
      this.setRelativeIndex(index);
      this.value = value;
   }

   public void setValueToStatement(int index, PreparedStatementWrap stmtWrap)
         throws SQLException
   {
      stmtWrap.setFloat(this.getName(), index, this.value);
   }

}
