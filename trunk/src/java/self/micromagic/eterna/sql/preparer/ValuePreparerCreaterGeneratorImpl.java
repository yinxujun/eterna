
package self.micromagic.eterna.sql.preparer;

import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.NumberFormat;
import java.text.DecimalFormat;

import self.micromagic.eterna.share.AbstractGenerator;
import self.micromagic.eterna.share.TypeManager;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.sql.converter.BooleanConverter;
import self.micromagic.eterna.sql.converter.DateConverter;
import self.micromagic.eterna.sql.converter.DoubleConverter;
import self.micromagic.eterna.sql.converter.IntegerConverter;
import self.micromagic.eterna.sql.converter.LongConverter;
import self.micromagic.eterna.sql.converter.StringConverter;
import self.micromagic.eterna.sql.converter.TimeConverter;
import self.micromagic.eterna.sql.converter.TimestampConverter;
import self.micromagic.eterna.sql.converter.BytesConverter;
import self.micromagic.eterna.sql.converter.StreamConverter;
import self.micromagic.eterna.sql.converter.ReaderConverter;
import self.micromagic.util.StringTool;

public class ValuePreparerCreaterGeneratorImpl extends AbstractGenerator
      implements ValuePreparerCreaterGenerator
{
   protected boolean initialized = false;

   private ValuePreparerCreater[] creaters
         = new ValuePreparerCreater[TypeManager.TYPES_COUNT];

   {
      this.creaters[TypeManager.TYPE_IGNORE] = new IgnorePreparerCreater();
      this.creaters[TypeManager.TYPE_STRING] = new StringPreparerCreater();
      this.creaters[TypeManager.TYPE_BIGSTRING] = new StringPreparerCreater();
      this.creaters[TypeManager.TYPE_BOOLEAN] = new BooleanPreparerCreater();

      this.creaters[TypeManager.TYPE_BYTE] = new IntegerPreparerCreater();
      this.creaters[TypeManager.TYPE_SHORT] = new IntegerPreparerCreater();
      this.creaters[TypeManager.TYPE_INTEGER] = new IntegerPreparerCreater();
      this.creaters[TypeManager.TYPE_LONG] = new LongPreparerCreater();
      this.creaters[TypeManager.TYPE_FLOAT] = new DoublePreparerCreater();
      this.creaters[TypeManager.TYPE_DOUBLE] = new DoublePreparerCreater();

      this.creaters[TypeManager.TYPE_DATE] = new DatePreparerCreater();
      this.creaters[TypeManager.TYPE_TIME] = new TimePreparerCreater();
      this.creaters[TypeManager.TYPE_TIMPSTAMP] = new TimestampPreparerCreater();
      this.creaters[TypeManager.TYPE_OBJECT] = new ObjectPreparerCreater();

      this.creaters[TypeManager.TYPE_BYTES] = new BytesPreparerCreater();
      this.creaters[TypeManager.TYPE_STREAM] = new StreamPreparerCreater();
      this.creaters[TypeManager.TYPE_READER] = new ReaderPreparerCreater();
   }

   public void initialize(EternaFactory factory)
   {
      if (this.initialized)
      {
         return;
      }
      this.initialized = true;
      String tmp;

      tmp = (String) this.getAttribute("dateFormat");
      if (tmp != null)
      {
         this.creaters[TypeManager.TYPE_DATE] = new DatePreparerCreater(tmp);
      }
      tmp = (String) this.getAttribute("timeFormat");
      if (tmp != null)
      {
         this.creaters[TypeManager.TYPE_TIME] = new TimePreparerCreater(tmp);
      }
      tmp = (String) this.getAttribute("datetimeFormat");
      if (tmp != null)
      {
         this.creaters[TypeManager.TYPE_TIMPSTAMP] = new TimestampPreparerCreater(tmp);
      }

      tmp = (String) this.getAttribute("stringFormat");
      if (tmp != null)
      {
         this.creaters[TypeManager.TYPE_STRING] = new StringPreparerCreater(tmp);
      }
      tmp = (String) this.getAttribute("booleanFormat");
      if (tmp != null)
      {
         this.creaters[TypeManager.TYPE_STRING] = new BooleanPreparerCreater(tmp);
      }

      tmp = (String) this.getAttribute("numberFormat");
      if (tmp != null)
      {
         this.creaters[TypeManager.TYPE_BYTE] = new IntegerPreparerCreater(tmp);
         this.creaters[TypeManager.TYPE_SHORT] = new IntegerPreparerCreater(tmp);
         this.creaters[TypeManager.TYPE_INTEGER] = new IntegerPreparerCreater(tmp);
         this.creaters[TypeManager.TYPE_LONG] = new LongPreparerCreater(tmp);
         this.creaters[TypeManager.TYPE_FLOAT] = new DoublePreparerCreater(tmp);
         this.creaters[TypeManager.TYPE_DOUBLE] = new DoublePreparerCreater(tmp);
      }

      tmp = (String) this.getAttribute("charset");
      if (tmp != null)
      {
         this.creaters[TypeManager.TYPE_BYTES] = new BytesPreparerCreater(tmp);
      }
   }

   public Object create()
   {
      return this.createValuePreparerCreater(TypeManager.TYPE_STRING);
   }

   public ValuePreparerCreater createValuePreparerCreater(int pureType)
   {
      return this.creaters[pureType];
   }

   public class BooleanPreparerCreater extends ValuePreparerCreater
   {
      BooleanConverter convert = new BooleanConverter();
      String[] trueValues = null;

      public BooleanPreparerCreater()
      {
      }

      public BooleanPreparerCreater(String formatStr)
      {
         this.trueValues = StringTool.separateString(formatStr, ";", true);
      }

      public ValuePreparer createPreparer(Object value)
      {
         if (value == null)
         {
            return new NullPreparer(0, Types.BOOLEAN);
         }
         return new BooleanPreparer(0, this.convert.convertToBoolean(value, this.trueValues));
      }

      public ValuePreparer createPreparer(String value)
      {
         if (value == null)
         {
            return new NullPreparer(0, Types.BOOLEAN);
         }
         return new BooleanPreparer(0, this.convert.convertToBoolean(value, this.trueValues));
      }

   }

   public class StringPreparerCreater extends ValuePreparerCreater
   {
      StringConverter convert = new StringConverter();
      String beginStr = "";
      String endStr = "";
      int appendLength = 0;

      public StringPreparerCreater()
      {
      }

      public StringPreparerCreater(String formatStr)
      {
         int index = formatStr.indexOf('$');
         if (index != -1)
         {
            this.beginStr = formatStr.substring(0, index);
            this.endStr = formatStr.substring(index + 1);
         }
         else
         {
            this.endStr = formatStr;
         }
         this.appendLength = this.beginStr.length() + this.endStr.length();
      }

      public ValuePreparer createPreparer(Object value)
      {
         return this.createPreparer(this.convert.convertToString(value));
      }

      public ValuePreparer createPreparer(String value)
      {
         if (this.appendLength == 0)
         {
            return new StringPreparer(0, value);
         }
         if (value == null) value = "";
         StringBuffer buf = new StringBuffer(value.length() + this.appendLength);
         buf.append(this.beginStr).append(value).append(this.endStr);
         return new StringPreparer(0, buf.toString());
      }

   }

   public class IntegerPreparerCreater extends ValuePreparerCreater
   {
      IntegerConverter convert = new IntegerConverter();
      NumberFormat format = null;

      public IntegerPreparerCreater()
      {
      }

      public IntegerPreparerCreater(String formatStr)
      {
         this.format = new DecimalFormat(formatStr);
      }

      public ValuePreparer createPreparer(Object value)
      {
         if (value == null)
         {
            return new NullPreparer(0, Types.INTEGER);
         }
         return new IntegerPreparer(0, this.convert.convertToInt(value, this.format));
      }

      public ValuePreparer createPreparer(String value)
      {
         if (value == null)
         {
            return new NullPreparer(0, Types.INTEGER);
         }
         return new IntegerPreparer(0, this.convert.convertToInt(value, this.format));
      }

   }

   public class LongPreparerCreater extends ValuePreparerCreater
   {
      LongConverter convert = new LongConverter();
      NumberFormat format = null;

      public LongPreparerCreater()
      {
      }

      public LongPreparerCreater(String formatStr)
      {
         this.format = new DecimalFormat(formatStr);
      }

      public ValuePreparer createPreparer(Object value)
      {
         if (value == null)
         {
            return new NullPreparer(0, Types.BIGINT);
         }
         return new LongPreparer(0, this.convert.convertToLong(value, this.format));
      }

      public ValuePreparer createPreparer(String value)
      {
         if (value == null)
         {
            return new NullPreparer(0, Types.BIGINT);
         }
         return new LongPreparer(0, this.convert.convertToLong(value, this.format));
      }

   }

   public class DoublePreparerCreater extends ValuePreparerCreater
   {
      DoubleConverter convert = new DoubleConverter();
      NumberFormat format = null;

      public DoublePreparerCreater()
      {
      }

      public DoublePreparerCreater(String formatStr)
      {
         this.format = new DecimalFormat(formatStr);
      }

      public ValuePreparer createPreparer(Object value)
      {
         if (value == null)
         {
            return new NullPreparer(0, Types.DOUBLE);
         }
         return new DoublePreparer(0, this.convert.convertToDouble(value, this.format));
      }

      public ValuePreparer createPreparer(String value)
      {
         if (value == null)
         {
            return new NullPreparer(0, Types.DOUBLE);
         }
         return new DoublePreparer(0, this.convert.convertToDouble(value, this.format));
      }

   }

   public class DatePreparerCreater extends ValuePreparerCreater
   {
      DateConverter convert = new DateConverter();
      DateFormat format = null;

      public DatePreparerCreater()
      {
      }

      public DatePreparerCreater(String formatStr)
      {
         this.format = new SimpleDateFormat(formatStr);
      }

      public ValuePreparer createPreparer(Object value)
      {
         return new DatePreparer(0, this.convert.convertToDate(value, this.format));
      }

      public ValuePreparer createPreparer(String value)
      {
         return new DatePreparer(0, this.convert.convertToDate(value, this.format));
      }

   }

   public class TimePreparerCreater extends ValuePreparerCreater
   {
      TimeConverter convert = new TimeConverter();
      DateFormat format = null;

      public TimePreparerCreater()
      {
      }

      public TimePreparerCreater(String formatStr)
      {
         this.format = new SimpleDateFormat(formatStr);
      }

      public ValuePreparer createPreparer(Object value)
      {
         return new TimePreparer(0, this.convert.convertToTime(value, this.format));
      }

      public ValuePreparer createPreparer(String value)
      {
         return new TimePreparer(0, this.convert.convertToTime(value, this.format));
      }

   }

   public class TimestampPreparerCreater extends ValuePreparerCreater
   {
      TimestampConverter convert = new TimestampConverter();
      DateFormat[] formats = null;

      public TimestampPreparerCreater()
      {
      }

      public TimestampPreparerCreater(String formatStr)
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
            return new TimestampPreparer(0, this.convert.convertToTimestamp(value));
         }
         for (int i = 0; i < this.formats.length; i++)
         {
            try
            {
               return new TimestampPreparer(0, this.convert.convertToTimestamp(value, this.formats[i]));
            }
            catch (Throwable ex) {}
         }
         throw new ClassCastException("Can't cast [" + value + "](" + value.getClass() + ") to Timestamp.");
      }

      public ValuePreparer createPreparer(String value)
      {
         if (this.formats == null)
         {
            return new TimestampPreparer(0, this.convert.convertToTimestamp(value));
         }
         for (int i = 0; i < this.formats.length; i++)
         {
            try
            {
               return new TimestampPreparer(0, this.convert.convertToTimestamp(value, this.formats[i]));
            }
            catch (Throwable ex) {}
         }
         throw new ClassCastException("Can't cast [" + value + "](" + value.getClass() + ") to Timestamp.");
      }

   }

   public class IgnorePreparerCreater extends ValuePreparerCreater
   {
      public ValuePreparer createPreparer(Object value)
      {
         return new NullPreparer(0, java.sql.Types.VARCHAR);
      }

      public ValuePreparer createPreparer(String value)
      {
         return new NullPreparer(0, java.sql.Types.VARCHAR);
      }

   }

   public class ObjectPreparerCreater extends ValuePreparerCreater
   {
      public ValuePreparer createPreparer(Object value)
      {
         return new ObjectPreparer(0, value);
      }

      public ValuePreparer createPreparer(String value)
      {
         return new ObjectPreparer(0, value);
      }

   }

   public class BytesPreparerCreater extends ValuePreparerCreater
   {
      BytesConverter convert = new BytesConverter();
      String charset = null;

      public BytesPreparerCreater()
      {
         this.charset = "UTF-8";
      }

      public BytesPreparerCreater(String charset)
      {
         this.charset = charset;
      }

      public ValuePreparer createPreparer(Object value)
      {
         return new BytesPreparer(0, this.convert.convertToBytes(value, this.charset));
      }

      public ValuePreparer createPreparer(String value)
      {
         return new BytesPreparer(0, this.convert.convertToBytes(value, this.charset));
      }

   }

   public class StreamPreparerCreater extends ValuePreparerCreater
   {
      StreamConverter convert = new StreamConverter();
      String charset = null;

      public StreamPreparerCreater()
      {
         this.charset = "UTF-8";
      }

      public StreamPreparerCreater(String charset)
      {
         this.charset = charset;
      }

      public ValuePreparer createPreparer(Object value)
      {
         return new StreamPreparer(0, this.convert.convertToStream(value, this.charset));
      }

      public ValuePreparer createPreparer(String value)
      {
         return new StreamPreparer(0, this.convert.convertToStream(value, this.charset));
      }

   }

   public class ReaderPreparerCreater extends ValuePreparerCreater
   {
      ReaderConverter convert = new ReaderConverter();

      public ValuePreparer createPreparer(Object value)
      {
         return new ReaderPreparer(0, this.convert.convertToReader(value));
      }

      public ValuePreparer createPreparer(String value)
      {
         return new ReaderPreparer(0, this.convert.convertToReader(value));
      }

   }

}

