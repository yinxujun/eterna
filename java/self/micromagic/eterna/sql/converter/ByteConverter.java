
package self.micromagic.eterna.sql.converter;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.util.ObjectRef;

public class ByteConverter extends ObjectConverter
{
   private Byte DEFAULT_VALUE = new Byte((byte) 0);

   public byte getResult(Object result)
         throws ConfigurationException
   {
      try
      {
         return this.convertToByte(result);
      }
      catch (Exception ex)
      {
         throw getErrorTypeException(result, "byte");
      }
   }

   public byte convertToByte(Object value)
   {
      if (value == null)
      {
         return 0;
      }
      if (value instanceof Number)
      {
         return ((Number) value).byteValue();
      }
      if (value instanceof String)
      {
         try
         {
            return Byte.parseByte((String) value);
         }
         catch (NumberFormatException ex) {}
      }
      if (value instanceof ObjectRef)
      {
         ObjectRef ref = (ObjectRef) value;
         if (ref.isNumber())
         {
            return (byte) ref.intValue();
         }
         else if (ref.isString())
         {
            try
            {
               return Byte.parseByte(ref.toString());
            }
            catch (NumberFormatException ex) {}
         }
      }
      throw new ClassCastException(getCastErrorMessage(value, "byte"));
   }

   public byte convertToByte(String value)
   {
      if (value == null)
      {
         return 0;
      }
      try
      {
         return Byte.parseByte(value);
      }
      catch (NumberFormatException ex) {}
      throw new ClassCastException(getCastErrorMessage(value, "byte"));
   }

   public Object convert(Object value)
   {
      if (value instanceof Byte)
      {
         return (Byte) value;
      }
      try
      {
         return new Byte(this.convertToByte(value));
      }
      catch (Exception ex)
      {
         if (this.needThrow)
         {
            if (ex instanceof RuntimeException)
            {
               throw (RuntimeException) ex;
            }
            throw new ClassCastException(getCastErrorMessage(value, "byte"));
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
         return new Byte(this.convertToByte(value));
      }
      catch (Exception ex)
      {
         if (this.needThrow)
         {
            if (ex instanceof RuntimeException)
            {
               throw (RuntimeException) ex;
            }
            throw new ClassCastException(getCastErrorMessage(value, "byte"));
         }
         else
         {
            return DEFAULT_VALUE;
         }
      }
   }

}
