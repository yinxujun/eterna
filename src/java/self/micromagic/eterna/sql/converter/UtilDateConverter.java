
package self.micromagic.eterna.sql.converter;

import java.text.DateFormat;
import java.text.ParseException;

import self.micromagic.util.StringRef;
import self.micromagic.util.FormatTool;
import self.micromagic.util.ObjectRef;
import self.micromagic.util.container.RequestParameterMap;
import self.micromagic.eterna.share.TypeManager;
import self.micromagic.eterna.digester.ConfigurationException;

public class UtilDateConverter extends ObjectConverter
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
         typeName.setString("UtilDate");
      }
      return TypeManager.TYPE_TIMPSTAMP;
   }

   public java.util.Date getResult(Object result)
         throws ConfigurationException
   {
      try
      {
         return this.convertToDate(result);
      }
      catch (Exception ex)
      {
         throw getErrorTypeException(result, "UtilDate");
      }
   }

   public java.util.Date convertToDate(Object value)
   {
      return this.convertToDate(value, this.dateFormat);
   }

   public java.util.Date convertToDate(Object value, DateFormat format)
   {
      if (value == null)
      {
         return null;
      }
      if (value instanceof java.util.Date)
      {
         return (java.util.Date) value;
      }
      if (value instanceof Number)
      {
         return new java.util.Date(((Number) value).longValue());
      }
      if (value instanceof String)
      {
         return this.convertToDate((String) value, format);
      }
      if (value instanceof String[])
      {
         String str = RequestParameterMap.getFirstParam(value);
         return this.convertToDate(str, format);
      }
      if (value instanceof ObjectRef)
      {
         return this.convertToDate(((ObjectRef) value).getObject(), format);
      }
      throw new ClassCastException(getCastErrorMessage(value, "UtilDate"));
   }

   public java.util.Date convertToDate(String value)
   {
      return this.convertToDate(value, this.dateFormat);
   }

   public java.util.Date convertToDate(String value, DateFormat format)
   {
      if (value == null)
      {
         return null;
      }
      try
      {
         if (format == null)
         {
            try
            {
               return FormatTool.parseDatetime(value);
            }
            catch (ParseException ex)
            {
               return FormatTool.parseDate(value);
            }
         }
         else
         {
            return format.parse(value);
         }
      }
      catch (ParseException ex) {}
      throw new ClassCastException(getCastErrorMessage(value, "UtilDate"));
   }

   public Object convert(Object value)
   {
      if (value instanceof java.util.Date)
      {
         return (java.util.Date) value;
      }
      try
      {
         return this.convertToDate(value);
      }
      catch (Exception ex)
      {
         if (this.needThrow)
         {
            if (ex instanceof RuntimeException)
            {
               throw (RuntimeException) ex;
            }
            throw new ClassCastException(getCastErrorMessage(value, "UtilDate"));
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
         return this.convertToDate(value);
      }
      catch (Exception ex)
      {
         if (this.needThrow)
         {
            if (ex instanceof RuntimeException)
            {
               throw (RuntimeException) ex;
            }
            throw new ClassCastException(getCastErrorMessage(value, "UtilDate"));
         }
         else
         {
            return null;
         }
      }
   }

}