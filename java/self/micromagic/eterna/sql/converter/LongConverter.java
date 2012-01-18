
package self.micromagic.eterna.sql.converter;

import java.text.NumberFormat;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.util.ObjectRef;

public class LongConverter extends ObjectConverter
{
   private Long DEFAULT_VALUE = new Long(0L);

   public long getResult(Object result)
         throws ConfigurationException
   {
      try
      {
         return this.convertToLong(result);
      }
      catch (Exception ex)
      {
         throw getErrorTypeException(result, "long");
      }
   }

   public long convertToLong(Object value)
   {
      return this.convertToLong(value, null);
   }

   public long convertToLong(Object value, NumberFormat format)
   {
      if (value == null)
      {
         return 0;
      }
      if (value instanceof Number)
      {
         return ((Number) value).longValue();
      }
      if (value instanceof String)
      {
         return this.convertToLong((String) value, format);
      }
      if (value instanceof ObjectRef)
      {
         ObjectRef ref = (ObjectRef) value;
         if (ref.isNumber())
         {
            return ref.longValue();
         }
         else if (ref.isString())
         {
            return this.convertToLong(ref.toString(), format);
         }
      }
      throw new ClassCastException(getCastErrorMessage(value, "long"));
   }

   public long convertToLong(String value)
   {
      return this.convertToLong(value, null);
   }

   public long convertToLong(String value, NumberFormat format)
   {
      if (value == null)
      {
         return 0;
      }
      try
      {
         if (format == null)
         {
            return Long.parseLong(value);
         }
         else
         {
            return format.parse(value).longValue();
         }
      }
      catch (Exception ex) {}
      throw new ClassCastException(getCastErrorMessage(value, "long"));
   }

   public Object convert(Object value)
   {
      if (value instanceof Long)
      {
         return (Long) value;
      }
      try
      {
         return new Long(this.convertToLong(value));
      }
      catch (Exception ex)
      {
         if (this.needThrow)
         {
            if (ex instanceof RuntimeException)
            {
               throw (RuntimeException) ex;
            }
            throw new ClassCastException(getCastErrorMessage(value, "long"));
         }
         else
         {
            return DEFAULT_VALUE;
         }
      }
   }

   public Object convert(String value)
   {
      try
      {
         return new Long(this.convertToLong(value));
      }
      catch (Exception ex)
      {
         if (this.needThrow)
         {
            if (ex instanceof RuntimeException)
            {
               throw (RuntimeException) ex;
            }
            throw new ClassCastException(getCastErrorMessage(value, "long"));
         }
         else
         {
            return DEFAULT_VALUE;
         }
      }
   }

}
