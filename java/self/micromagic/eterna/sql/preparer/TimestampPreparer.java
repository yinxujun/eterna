
package self.micromagic.eterna.sql.preparer;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;

import self.micromagic.eterna.sql.PreparedStatementWrap;

public class TimestampPreparer extends AbstractValuePreparer
{
   protected Timestamp value;
   protected Calendar calendar;

   public TimestampPreparer(int index, Timestamp value)
   {
      this(index, value, null);
   }

   public TimestampPreparer(int index, Timestamp value, Calendar calendar)
   {
      this.setRelativeIndex(index);
      this.value = value;
      this.calendar = calendar;
   }

   public void setValueToStatement(int index, PreparedStatementWrap stmtWrap)
         throws SQLException
   {
      if (this.calendar == null)
      {
         stmtWrap.setTimestamp(this.getName(), index, this.value);
      }
      else
      {
         stmtWrap.setTimestamp(this.getName(), index, this.value, this.calendar);
      }
   }

}