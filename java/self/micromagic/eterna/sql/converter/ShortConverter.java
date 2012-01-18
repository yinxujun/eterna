
package self.micromagic.eterna.sql.converter;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.util.ObjectRef;

public class ShortConverter extends ObjectConverter
{
   private Short DEFAULT_VALUE = new Short((short) 0);

   public short getResult(Object result)
         throws ConfigurationException
   {
      try
      {
         return this.convertToShort(result);
      }
      catch (Exception ex)
      {
         throw getErrorTypeException(result, "short");
      }
   }

   public short convertToShort(Object value)
   {
      if (value == null)
      {
         return 0;
      }
      if (value instanceof Number)
      {
         return ((Number) value).shortValue();
      }
      if (value instanceof String)
      {
         try
         {
            return Short.parseShort((String) value);
         }
         catch (NumberFormatException ex) {}
      }
      if (value instanceof ObjectRef)
      {
         ObjectRef ref = (ObjectRef) value;
         if (ref.isNumber())
         {
            return (short) ref.longValue();
         }
         else if (ref.isString())
         {
            try
            {
               return Short.parseShort(ref.toString());
            }
            catch (NumberFormatException ex) {}
         }
      }
      throw new ClassCastException(getCastErrorMessage(value, "short"));
   }

   public short convertToShort(String value)
   {
      if (value == null)
      {
         return 0;
      }
      try
      {
         return Short.parseShort(value);
      }
      catch (NumberFormatException ex) {}
      throw new ClassCastException(getCastErrorMessage(value, "short"));
   }

   public Object convert(Object value)
   {
      if (value instanceof Short)
      {
         return (Short) value;
      }
      try
      {
         return new Short(this.convertToShort(value));
      }
      catch (Exception ex)
      {
         if (this.needThrow)
         {
            if (ex instanceof RuntimeException)
            {
               throw (RuntimeException) ex;
            }
            throw new ClassCastException(getCastErrorMessage(value, "short"));
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
         return new Short(this.convertToShort(value));
      }
      catch (Exception ex)
      {
         if (this.needThrow)
         {
            if (ex instanceof RuntimeException)
            {
               throw (RuntimeException) ex;
            }
            throw new ClassCastException(getCastErrorMessage(value, "short"));
         }
         else
         {
            return DEFAULT_VALUE;
         }
      }
   }
}
