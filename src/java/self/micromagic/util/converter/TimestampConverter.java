
package self.micromagic.util.converter;

import java.text.ParseException;
import java.text.DateFormat;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.TypeManager;
import self.micromagic.util.FormatTool;
import self.micromagic.util.StringRef;
import self.micromagic.util.ObjectRef;
import self.micromagic.util.container.RequestParameterMap;

public class TimestampConverter extends ObjectConverter
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
         typeName.setString("Timestamp");
      }
      return TypeManager.TYPE_TIMPSTAMP;
   }

   public java.sql.Timestamp getResult(Object result)
         throws ConfigurationException
   {
      try
      {
         return this.convertToTimestamp(result);
      }
      catch (Exception ex)
      {
         throw getErrorTypeException(result, "Timestamp");
      }
   }

   public java.sql.Timestamp convertToTimestamp(Object value)
   {
      return this.convertToTimestamp(value, this.dateFormat);
   }

   public java.sql.Timestamp convertToTimestamp(Object value, DateFormat format)
   {
      if (value == null)
      {
         return null;
      }
      if (value instanceof java.sql.Timestamp)
      {
         return (java.sql.Timestamp) value;
      }
      if (value instanceof java.util.Date)
      {
         return new java.sql.Timestamp(((java.util.Date) value).getTime());
      }
      if (value instanceof Number)
      {
         return new java.sql.Timestamp(((Number) value).longValue());
      }
      if (value instanceof String)
      {
         return this.convertToTimestamp((String) value, format);
      }
      if (value instanceof String[])
      {
         String str = RequestParameterMap.getFirstParam(value);
         return this.convertToTimestamp(str, format);
      }
      if (value instanceof ObjectRef)
      {
         return this.convertToTimestamp(((ObjectRef) value).getObject(), format);
      }
      throw new ClassCastException(getCastErrorMessage(value, "Timestamp"));
   }

   public java.sql.Timestamp convertToTimestamp(String value)
   {
      return this.convertToTimestamp(value, this.dateFormat);
   }

   public java.sql.Timestamp convertToTimestamp(String value, DateFormat format)
   {
      if (value == null)
      {
         return null;
      }
      try
      {
         if (format == null)
         {
            return new java.sql.Timestamp(FormatTool.parseDatetime(value).getTime());
         }
         else
         {
            return new java.sql.Timestamp(format.parse(value).getTime());
         }
      }
      catch (ParseException ex) {}
      throw new ClassCastException(getCastErrorMessage(value, "Timestamp"));
   }

   public Object convert(Object value)
   {
      if (value instanceof java.sql.Timestamp)
      {
         return (java.sql.Timestamp) value;
      }
      try
      {
         return this.convertToTimestamp(value);
      }
      catch (Exception ex)
      {
         if (this.needThrow)
         {
            if (ex instanceof RuntimeException)
            {
               throw (RuntimeException) ex;
            }
            throw new ClassCastException(getCastErrorMessage(value, "Timestamp"));
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
         return this.convertToTimestamp(value);
      }
      catch (Exception ex)
      {
         if (this.needThrow)
         {
            if (ex instanceof RuntimeException)
            {
               throw (RuntimeException) ex;
            }
            throw new ClassCastException(getCastErrorMessage(value, "Timestamp"));
         }
         else
         {
            return null;
         }
      }
   }

}