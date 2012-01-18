
package self.micromagic.eterna.sql.converter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.io.IOException;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.util.MemoryStream;
import org.apache.commons.fileupload.FileItem;

public class StreamConverter extends ObjectConverter
{
   public InputStream getResult(Object result)
         throws ConfigurationException
   {
      try
      {
         return this.convertToStream(result);
      }
      catch (Exception ex)
      {
         throw getErrorTypeException(result, "InputStream");
      }
   }

   public InputStream convertToStream(Object value)
   {
      return this.convertToStream(value, null);
   }

   public InputStream convertToStream(Object value, String charset)
   {
      if (value == null)
      {
         return null;
      }
      if (value instanceof InputStream)
      {
         return (InputStream) value;
      }
      if (value instanceof MemoryStream)
      {
         return ((MemoryStream) value).getInputStream();
      }
      if (value instanceof FileItem)
      {
         try
         {
            return ((FileItem) value).getInputStream();
         }
         catch (IOException ex)
         {
            throw new RuntimeException(ex);
         }
      }
      if (value instanceof byte[])
      {
         return new ByteArrayInputStream((byte[]) value);
      }
      if (value instanceof String)
      {
         return this.convertToStream((String) value, charset);
      }
      throw new ClassCastException(getCastErrorMessage(value, "InputStream"));
   }

   public InputStream convertToStream(String value)
   {
      return this.convertToStream(value, null);
   }

   public InputStream convertToStream(String value, String charset)
   {
      if (value == null)
      {
         return null;
      }
      try
      {
         if (charset == null)
         {
            return new ByteArrayInputStream(value.getBytes("8859_1"));
         }
         else
         {
            return new ByteArrayInputStream(value.getBytes(charset));
         }
      }
      catch (UnsupportedEncodingException ex)
      {
         throw new RuntimeException(ex);
      }
   }

   public Object convert(Object value)
   {
      if (value instanceof InputStream)
      {
         return (InputStream) value;
      }
      try
      {
         return this.convertToStream(value);
      }
      catch (Exception ex)
      {
         if (this.needThrow)
         {
            if (ex instanceof RuntimeException)
            {
               throw (RuntimeException) ex;
            }
            throw new ClassCastException(getCastErrorMessage(value, "InputStream"));
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
         return this.convertToStream(value);
      }
      catch (Exception ex)
      {
         if (this.needThrow)
         {
            if (ex instanceof RuntimeException)
            {
               throw (RuntimeException) ex;
            }
            throw new ClassCastException(getCastErrorMessage(value, "InputStream"));
         }
         else
         {
            return null;
         }
      }
   }

}