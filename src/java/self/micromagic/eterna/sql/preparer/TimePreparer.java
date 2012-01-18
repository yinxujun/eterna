
package self.micromagic.eterna.sql.preparer;

import java.sql.SQLException;
import java.sql.Time;
import java.util.Calendar;

import self.micromagic.eterna.sql.PreparedStatementWrap;

public class TimePreparer extends AbstractValuePreparer
{
   protected Time value;
   protected Calendar calendar;

   public TimePreparer(int index, Time value)
   {
           this(index, value, null);
   }

   public TimePreparer(int index, Time value, Calendar calendar)
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
         stmtWrap.setTime(this.getName(), index, this.value);
      }
      else
      {
         stmtWrap.setTime(this.getName(), index, this.value, this.calendar);
      }
   }

}