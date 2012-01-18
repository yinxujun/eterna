
package self.micromagic.util;

import java.io.Serializable;

public class LongRef extends ObjectRef
      implements Serializable
{
   public long value;

   public LongRef()
   {
      this.value = 0L;
   }

   public LongRef(long value)
   {
      this.value = value;
   }

   public LongRef(Long value)
   {
      this.value = value.intValue();
   }

   public boolean isNumber()
   {
      return true;
   }

   public int intValue()
   {
      return (int) this.value;
   }

   public long longValue()
   {
      return this.value;
   }

   public double doubleValue()
   {
      return (double) this.value;
   }

   public static long getLongValue(Object obj)
   {
      if (obj == null)
      {
         return 0L;
      }
      else if (obj instanceof Number)
      {
         return ((Number) obj).longValue();
      }
      else if (obj instanceof String)
      {
         try
         {
            return Long.parseLong((String) obj);
         }
         catch (NumberFormatException e) {}
      }
      return 0L;
   }

   public void setObject(Object obj)
   {
      this.value = LongRef.getLongValue(obj);
      super.setObject(null);
   }

   public Object getObject()
   {
      Object obj = super.getObject();
      if (obj == null || ((Long) obj).longValue() != this.value)
      {
         obj = new Long(this.value);
         super.setObject(obj);
      }
      return obj;
   }

   public void setLong(long value)
   {
      this.value = value;
      super.setObject(null);
   }

   public long getLong()
   {
      return this.value;
   }

   public String toString()
   {
      return String.valueOf(this.value);
   }

   public boolean equals(Object other)
   {
      int result = this.shareEqual(other, LongRef.class);
      if (result != MORE_EQUAL)
      {
         return result == TRUE_EQUAL;
      }
      return this.value == ((LongRef) other).value;
   }

}
