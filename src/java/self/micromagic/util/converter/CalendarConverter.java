
package self.micromagic.util.converter;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;

import self.micromagic.util.StringRef;
import self.micromagic.util.FormatTool;
import self.micromagic.util.ObjectRef;
import self.micromagic.util.container.RequestParameterMap;
import self.micromagic.eterna.share.TypeManager;
import self.micromagic.eterna.digester.ConfigurationException;

public class CalendarConverter extends ObjectConverter
{
   private DateFormat[] dateFormats;

   public void setDateFormat(DateFormat dateFormat)
   {
      this.dateFormats = new DateFormat[]{dateFormat};
   }

   public void setDateFormats(DateFormat[] dateFormats)
   {
      this.dateFormats = dateFormats;
   }

   public int getConvertType(StringRef typeName)
   {
      if (typeName != null)
      {
         typeName.setString("Calendar");
      }
      return TypeManager.TYPE_TIMPSTAMP;
   }

   public Calendar getResult(Object result)
         throws ConfigurationException
   {
      try
      {
         return this.convertToCalendar(result);
      }
      catch (Exception ex)
      {
         throw getErrorTypeException(result, "Calendar");
      }
   }

   public Calendar convertToCalendar(Object value)
   {
      return this.convertToCalendar(value, this.dateFormats);
   }

   public Calendar convertToCalendar(Object value, DateFormat format)
   {
      return this.convertToCalendar(value, new DateFormat[]{format});
	}

   public Calendar convertToCalendar(Object value, DateFormat[] formats)
   {
      if (value == null)
      {
         return null;
      }
      if (value instanceof Calendar)
      {
         return (Calendar) value;
      }
      if (value instanceof java.util.Date)
      {
         Calendar c = Calendar.getInstance();
         c.setTimeInMillis(((java.util.Date) value).getTime());
         return c;
      }
      if (value instanceof Number)
      {
         Calendar c = Calendar.getInstance();
         c.setTimeInMillis(((Number) value).longValue());
         return c;
      }
      if (value instanceof String)
      {
         return this.convertToCalendar((String) value, formats);
      }
      if (value instanceof String[])
      {
         String str = RequestParameterMap.getFirstParam(value);
         return this.convertToCalendar(str, formats);
      }
      if (value instanceof ObjectRef)
      {
         return this.convertToCalendar(((ObjectRef) value).getObject(), formats);
      }
      throw new ClassCastException(getCastErrorMessage(value, "Calendar"));
   }

   public Calendar convertToCalendar(String value)
   {
      return this.convertToCalendar(value, this.dateFormats);
   }

   public Calendar convertToCalendar(String value, DateFormat format)
   {
      return this.convertToCalendar(value, new DateFormat[]{format});
	}

   public Calendar convertToCalendar(String value, DateFormat[] formats)
   {
      if (value == null)
      {
         return null;
      }
      try
      {
         if (formats == null)
         {
            Calendar c = Calendar.getInstance();
            try
            {
               c.setTimeInMillis(FormatTool.parseDatetime(value).getTime());
            }
            catch (ParseException ex)
            {
               c.setTimeInMillis(FormatTool.parseDate(value).getTime());
            }
            return c;
         }
         else
         {
            Calendar c = Calendar.getInstance();
				for (int i = 0; i < formats.length; i++)
				{
					try
					{
						c.setTimeInMillis(formats[i].parse(value).getTime());
						return c;
					}
					catch (Throwable ex) {}
				}
         }
      }
      catch (ParseException ex) {}
      throw new ClassCastException(getCastErrorMessage(value, "Calendar"));
   }

   public Object convert(Object value)
   {
      if (value instanceof Calendar)
      {
         return (Calendar) value;
      }
      try
      {
         return this.convertToCalendar(value);
      }
      catch (Exception ex)
      {
         if (this.needThrow)
         {
            if (ex instanceof RuntimeException)
            {
               throw (RuntimeException) ex;
            }
            throw new ClassCastException(getCastErrorMessage(value, "Calendar"));
         }
         else
         {
            return null;
         }
      }
   }

   public Object convert(String value)
   {
      try
      {
         return this.convertToCalendar(value);
      }
      catch (Exception ex)
      {
         if (this.needThrow)
         {
            if (ex instanceof RuntimeException)
            {
               throw (RuntimeException) ex;
            }
            throw new ClassCastException(getCastErrorMessage(value, "Calendar"));
         }
         else
         {
            return null;
         }
      }
   }

}