
package self.micromagic.eterna.sql.converter;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.TypeManager;
import self.micromagic.util.ObjectRef;
import self.micromagic.util.StringRef;
import self.micromagic.util.container.RequestParameterMap;

public class ByteConverter extends ObjectConverter
{
   private static Byte DEFAULT_VALUE = new Byte((byte) 0);

   public int getConvertType(StringRef typeName)
   {
      if (typeName != null)
      {
         typeName.setString("byte");
      }
      return TypeManager.TYPE_BYTE;
   }

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
         return this.convertToByte((String) value);
      }
      Object tmpObj = this.changeByPropertyEditor(value);
      if (tmpObj instanceof Byte)
      {
         return ((Byte) tmpObj).byteValue();
      }
      if (value instanceof String[])
      {
         String str = RequestParameterMap.getFirstParam(value);
         return this.convertToByte(str);
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
         else
         {
            return this.convertToByte(ref.getObject());
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
      Object tmpObj = this.changeByPropertyEditor(value);
      if (tmpObj instanceof Byte)
      {
         return ((Byte) tmpObj).byteValue();
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
