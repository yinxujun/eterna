
package self.micromagic.util.converter;

import java.text.ParseException;
import java.text.DateFormat;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.TypeManager;
import self.micromagic.util.FormatTool;
import self.micromagic.util.StringRef;
import self.micromagic.util.ObjectRef;
import self.micromagic.util.container.RequestParameterMap;

public class TimeConverter extends ObjectConverter
{
   private DateFormat dateFormat;

   public void setDateFormat(DateFormat dateFormat)
   {
      this.dateFormat = dateFormat;
   }

   public int getConvertType(StringRef typeName)
   {
      if (typeName != null)
      {
         typeName.setString("Time");
      }
      return TypeManager.TYPE_TIME;
   }

   public java.sql.Time getResult(Object result)
         throws ConfigurationException
   {
      try
      {
         return this.convertToTime(result);
      }
      catch (Exception ex)
      {
         throw getErrorTypeException(result, "Time");
      }
   }

   public java.sql.Time convertToTime(Object value)
   {
      return this.convertToTime(value, this.dateFormat);
   }

   public java.sql.Time convertToTime(Object value, DateFormat format)
   {
      if (value == null)
      {
         return null;
      }
      if (value instanceof java.sql.Time)
      {
         return (java.sql.Time) value;
      }
      if (value instanceof java.util.Date)
      {
         return new java.sql.Time(((java.util.Date) value).getTime());
      }
      if (value instanceof Number)
      {
         return new java.sql.Time(((Number) value).longValue());
      }
      if (value instanceof String)
      {
         return this.convertToTime((String) value, format);
      }
      if (value instanceof String[])
      {
         String str = RequestParameterMap.getFirstParam(value);
         return this.convertToTime(str, format);
      }
      if (value instanceof ObjectRef)
      {
         return this.convertToTime(((ObjectRef) value).getObject(), format);
      }
      throw new ClassCastException(getCastErrorMessage(value, "Time"));
   }

   public java.sql.Time convertToTime(String value)
   {
      return this.convertToTime(value, this.dateFormat);
   }

   public java.sql.Time convertToTime(String value, DateFormat format)
   {
      if (value == null)
      {
         return null;
      }
      try
      {
         if (format == null)
         {
            return new java.sql.Time(FormatTool.parseTime(value).getTime());
         }
         else
         {
            return new java.sql.Time(format.parse(value).getTime());
         }
      }
      catch (ParseException ex) {}
      throw new ClassCastException(getCastErrorMessage(value, "Time"));
   }

   public Object convert(Object value)
   {
      if (value instanceof java.sql.Time)
      {
         return (java.sql.Time) value;
      }
      try
      {
         return this.convertToTime(value);
      }
      catch (Exception ex)
      {
         if (this.needThrow)
         {
            if (ex instanceof RuntimeException)
            {
               throw (RuntimeException) ex;
            }
            throw new ClassCastException(getCastErrorMessage(value, "Time"));
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
         return this.convertToTime(value);
      }
      catch (Exception ex)
      {
         if (this.needThrow)
         {
            if (ex instanceof RuntimeException)
            {
               throw (RuntimeException) ex;
            }
            throw new ClassCastException(getCastErrorMessage(value, "Time"));
         }
         else
         {
            return null;
         }
      }
   }

}
