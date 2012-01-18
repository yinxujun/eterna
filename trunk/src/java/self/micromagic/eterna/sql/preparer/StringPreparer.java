
package self.micromagic.eterna.sql.preparer;

import java.sql.SQLException;

import self.micromagic.eterna.sql.PreparedStatementWrap;

public class StringPreparer extends AbstractValuePreparer
{
   private String value;

   public StringPreparer(int index, String value)
   {
      this.setRelativeIndex(index);
      this.value = value;
   }

   public void setValueToStatement(int index, PreparedStatementWrap stmtWrap)
         throws SQLException
   {
      stmtWrap.setString(this.getName(), index, this.value);
   }

}