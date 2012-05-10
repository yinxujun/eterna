
package self.micromagic.eterna.sql.converter;

import java.io.UnsupportedEncodingException;
import java.io.InputStream;
import java.io.IOException;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.TypeManager;
import self.micromagic.util.MemoryStream;
import self.micromagic.util.StringRef;

public class BytesConverter extends ObjectConverter
{
   public static int MAX_BUFFER = 1024 * 1024 * 16;

   public int getConvertType(StringRef typeName)
   {
      if (typeName != null)
      {
         typeName.setString("Bytes");
      }
      return TypeManager.TYPE_BYTES;
   }

   public byte[] getResult(Object result)
         throws ConfigurationException
   {
      try
      {
         return this.convertToBytes(result);
      }
      catch (Exception ex)
      {
         throw getErrorTypeException(result, "Bytes");
      }
   }

   public byte[] convertToBytes(Object value)
   {
      return this.convertToBytes(value, null);
   }

   public byte[] convertToBytes(Object value, String charset)
   {
      if (value == null)
      {
         return null;
      }
      if (value instanceof MemoryStream)
      {
         MemoryStream ms = ((MemoryStream) value);
         if (ms.getUsedSize() < MAX_BUFFER)
         {
            InputStream in = ms.getInputStream();
            byte[] buf = new byte[(int) ms.getUsedSize()];
            try
            {
               in.read(buf);
            }
            catch (IOException ex) {}
            return buf;
         }
         else
         {
            throw new ClassCastException(getCastErrorMessage(value, "Bytes"));
         }
      }
      if (value instanceof byte[])
      {
         return (byte[]) value;
      }
      if (value instanceof String)
      {
         return this.convertToBytes((String) value, charset);
      }
      throw new ClassCastException(getCastErrorMessage(value, "Bytes"));
   }

   public byte[] convertToBytes(String value)
   {
      return this.convertToBytes(value, null);
   }

   public byte[] convertToBytes(String value, String charset)
   {
      if (value == null)
      {
         return null;
      }
      try
      {
         if (charset == null)
         {
            return value.getBytes("8859_1");
         }
         else
         {
            return value.getBytes(charset);
         }
      }
      catch (UnsupportedEncodingException ex)
      {
         throw new ClassCastException(getCastErrorMessage(value, "Bytes"));
      }
   }

   public Object convert(Object value)
   {
      if (value instanceof byte[])
      {
         return (byte[]) value;
      }
      try
      {
         return this.convertToBytes(value);
      }
      catch (Exception ex)
      {
         if (this.needThrow)
         {
            if (ex instanceof RuntimeException)
            {
               throw (RuntimeException) ex;
            }
            throw new ClassCastException(getCastErrorMessage(value, "Bytes"));
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
         return this.convertToBytes(value);
      }
      catch (Exception ex)
      {
         if (this.needThrow)
         {
            if (ex instanceof RuntimeException)
            {
               throw (RuntimeException) ex;
            }
            throw new ClassCastException(getCastErrorMessage(value, "Bytes"));
         }
         else
         {
            return null;
         }
      }
   }

}