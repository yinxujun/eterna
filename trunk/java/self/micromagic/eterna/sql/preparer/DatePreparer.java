
package self.micromagic.eterna.sql.preparer;

import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;

import self.micromagic.eterna.sql.PreparedStatementWrap;

public class DatePreparer extends AbstractValuePreparer
{
   protected Date value;
   protected Calendar calendar;

   public DatePreparer(int index, Date value)
   {
           this(index, value, null);
   }

   public DatePreparer(int index, Date value, Calendar calendar)
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
         stmtWrap.setDate(this.getName(), index, this.value);
      }
      else
      {
         stmtWrap.setDate(this.getName(), index, this.value, this.calendar);
      }
   }

}