
package self.micromagic.eterna.sql.converter;

import java.io.Reader;
import java.io.IOException;

import self.micromagic.util.MemoryChars;
import self.micromagic.util.StringRef;
import self.micromagic.eterna.share.TypeManager;

public class StringConverter extends ObjectConverter
{
   public static int MAX_BUFFER = 10240;

   public int getConvertType(StringRef typeName)
   {
      if (typeName != null)
      {
         typeName.setString("String");
      }
      return TypeManager.TYPE_STRING;
   }

   public Object convert(Object value)
   {
      if (value != null && value instanceof MemoryChars)
      {
         MemoryChars mc = ((MemoryChars) value);
         if (mc.getUsedSize() < MAX_BUFFER)
         {
            Reader reader = mc.getReader();
            char[] buf = new char[(int) mc.getUsedSize()];
            try
            {
               reader.read(buf);
            }
            catch (IOException ex) {}
            return new String(buf);
         }
      }
      return this.convertToString(value);
   }

}
