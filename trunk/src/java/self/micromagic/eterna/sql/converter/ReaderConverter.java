
package self.micromagic.eterna.sql.converter;

import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.TypeManager;
import self.micromagic.util.MemoryChars;
import self.micromagic.util.StringRef;

public class ReaderConverter extends ObjectConverter
{
   public int getConvertType(StringRef typeName)
   {
      if (typeName != null)
      {
         typeName.setString("Reader");
      }
      return TypeManager.TYPE_READER;
   }

   public Reader getResult(Object result)
         throws ConfigurationException
   {
      try
      {
         return this.convertToReader(result);
      }
      catch (Exception ex)
      {
         throw getErrorTypeException(result, "Reader");
      }
   }

   public Reader convertToReader(Object value)
   {
      if (value == null)
      {
         return null;
      }
      if (value instanceof Reader)
      {
         return (Reader) value;
      }
      if (value instanceof MemoryChars)
      {
         return ((MemoryChars) value).getReader();
      }
      if (value instanceof char[])
      {
         return new StringReader(new String((char[]) value));
      }
      if (value instanceof String)
      {
         return new StringReader((String) value);
      }
      throw new ClassCastException(getCastErrorMessage(value, "Reader"));
   }

   public Reader convertToReader(String value)
   {
      if (value == null)
      {
         return null;
      }
      return new StringReader(value);
   }

   public Object convert(Object value)
   {
      if (value instanceof InputStream)
      {
         return (InputStream) value;
      }
      try
      {
         return this.convertToReader(value);
      }
      catch (Exception ex)
      {
         if (this.needThrow)
         {
            if (ex instanceof RuntimeException)
            {
               throw (RuntimeException) ex;
            }
            throw new ClassCastException(getCastErrorMessage(value, "Reader"));
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
         return this.convertToReader(value);
      }
      catch (Exception ex)
      {
         if (this.needThrow)
         {
            if (ex instanceof RuntimeException)
            {
               throw (RuntimeException) ex;
            }
            throw new ClassCastException(getCastErrorMessage(value, "Reader"));
         }
         else
         {
            return null;
         }
      }
   }

}