
package self.micromagic.eterna.sql.preparer;

import java.sql.SQLException;

import self.micromagic.eterna.sql.PreparedStatementWrap;

public class NullPreparer extends AbstractValuePreparer
{
   private int type;

   public NullPreparer(int index, int type)
   {
      this.setRelativeIndex(index);
      this.type = type;
   }

   public void setValueToStatement(int index, PreparedStatementWrap stmtWrap)
         throws SQLException
   {
      stmtWrap.setNull(this.getName(), index, this.type);
   }

}
