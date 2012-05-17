
package self.micromagic.eterna.sql.preparer;

import java.sql.SQLException;
import java.sql.Time;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import self.micromagic.eterna.sql.PreparedStatementWrap;
import self.micromagic.eterna.sql.converter.TimeConverter;

class TimePreparer extends AbstractValuePreparer
{
   protected Time value;
   protected Calendar calendar;

   public TimePreparer(ValuePreparerCreater vpc, Time value)
   {
      this(vpc, value, null);
   }

   public TimePreparer(ValuePreparerCreater vpc, Time value, Calendar calendar)
   {
      super(vpc);
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

   static class Creater extends AbstractCreater
   {
      TimeConverter convert = new TimeConverter();
      DateFormat format = null;

      public Creater(ValuePreparerCreaterGenerator vpcg)
      {
         super(vpcg);
      }

      public void setFormat(String formatStr)
      {
         this.format = new SimpleDateFormat(formatStr);
      }

      public ValuePreparer createPreparer(Object value)
      {
         return new TimePreparer(this, this.convert.convertToTime(value, this.format));
      }

      public ValuePreparer createPreparer(String value)
      {
         return new TimePreparer(this, this.convert.convertToTime(value, this.format));
      }

      public ValuePreparer createPreparer(Time value)
      {
         return new TimePreparer(this, value);
      }

      public ValuePreparer createPreparer(Time value, Calendar calendar)
      {
         return new TimePreparer(this, value, calendar);
      }

   }

}