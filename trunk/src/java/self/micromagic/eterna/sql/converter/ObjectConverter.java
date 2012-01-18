
package self.micromagic.eterna.sql.converter;

import self.micromagic.eterna.digester.ConfigurationException;

public class ObjectConverter
      implements ValueConverter
{
   protected boolean needThrow = false;

   public boolean isNeedThrow()
   {
      return this.needThrow;
   }

   public void setNeedThrow(boolean need)
   {
      this.needThrow = need;
   }

   public Object convert(Object value)
   {
      return value;
   }

   public Object convert(String value)
   {
      return value;
   }

   public String convertToString(Object value)
   {
      return value == null ? null : value.toString();
   }

   public String convertToString(Object value, boolean changeNullToEmpty)
   {
      return value == null ? changeNullToEmpty ? "" : null
            : this.convertToString(value);
   }

   public static String getCastErrorMessage(Object obj, String needType)
   {
      return "Can't cast [" + obj + "](" + obj.getClass() + ") to " + needType + ".";
   }

   public static ConfigurationException getErrorTypeException(Object obj, String needType)
   {
      return new ConfigurationException(getCastErrorMessage(obj, needType));
   }

}
