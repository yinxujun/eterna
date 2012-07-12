
package self.micromagic.eterna.sql.preparer;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import self.micromagic.eterna.sql.PreparedStatementWrap;
import self.micromagic.util.converter.TimestampConverter;
import self.micromagic.util.StringTool;

class TimestampPreparer extends AbstractValuePreparer
{
   protected Timestamp value;
   protected Calendar calendar;

   public TimestampPreparer(ValuePreparerCreater vpc, Timestamp value)
   {
      this(vpc, value, null);
   }

   public TimestampPreparer(ValuePreparerCreater vpc, Timestamp value, Calendar calendar)
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
         stmtWrap.setTimestamp(this.getName(), index, this.value);
      }
      else
      {
         stmtWrap.setTimestamp(this.getName(), index, this.value, this.calendar);
      }
   }

   static class Creater extends AbstractCreater
   {
      TimestampConverter convert = new TimestampConverter();
      DateFormat[] formats = null;

      public Creater(ValuePreparerCreaterGenerator vpcg)
      {
         super(vpcg);
      }

      public void setFormat(String formatStr)
      {
         String[] strs = StringTool.separateString(formatStr, ";", true);
         this.formats = new DateFormat[strs.length];
         for (int i = 0; i < strs.length; i++)
         {
            this.formats[i] = new SimpleDateFormat(strs[i]);
         }
      }

      public ValuePreparer createPreparer(Object value)
      {
         if (this.formats == null)
         {
            return new TimestampPreparer(this, this.convert.convertToTimestamp(value));
         }
         for (int i = 0; i < this.formats.length; i++)
         {
            try
            {
               return new TimestampPreparer(this, this.convert.convertToTimestamp(value, this.formats[i]));
            }
            catch (Throwable ex) {}
         }
         throw new ClassCastException("Can't cast [" + value + "](" + value.getClass() + ") to Timestamp.");
      }

      public ValuePreparer createPreparer(String value)
      {
         if (this.formats == null)
         {
            return new TimestampPreparer(this, this.convert.convertToTimestamp(value));
         }
         for (int i = 0; i < this.formats.length; i++)
         {
            try
            {
               return new TimestampPreparer(this, this.convert.convertToTimestamp(value, this.formats[i]));
            }
            catch (Throwable ex) {}
         }
         throw new ClassCastException("Can't cast [" + value + "](" + value.getClass() + ") to Timestamp.");
      }

      public ValuePreparer createPreparer(Timestamp value)
      {
         return new TimestampPreparer(this, value);
      }

      public ValuePreparer createPreparer(Timestamp value, Calendar calendar)
      {
         return new TimestampPreparer(this, value, calendar);
      }

   }

}