
package self.micromagic.util;

import java.io.Serializable;

public class IntegerRef extends ObjectRef
      implements Serializable
{
   public int value;

   public IntegerRef()
   {
      this.value = 0;
   }

   public IntegerRef(int value)
   {
      this.value = value;
   }

   public IntegerRef(Integer value)
   {
      this.value = value.intValue();
   }

   public boolean isNumber()
   {
      return true;
   }

   public int intValue()
   {
      return this.value;
   }

   public long longValue()
   {
      return (long) this.value;
   }

   public double doubleValue()
   {
      return (double) this.value;
   }

   public static int getIntegerValue(Object obj)
   {
      if (obj == null)
      {
         return 0;
      }
      else if (obj instanceof Number)
      {
         return ((Number) obj).intValue();
      }
      else if (obj instanceof String)
      {
         try
         {
            return Integer.parseInt((String) obj);
         }
         catch (NumberFormatException e) {}
      }
      return 0;
   }

   public void setObject(Object obj)
   {
      this.value = IntegerRef.getIntegerValue(obj);
      super.setObject(null);
   }

   public Object getObject()
   {
      Object obj = super.getObject();
      if (obj == null || ((Integer) obj).intValue() != this.value)
      {
         obj = Utility.createInteger(this.value);
         super.setObject(obj);
      }
      return obj;
   }

   public void setInt(int value)
   {
      this.value = value;
      super.setObject(null);
   }

   public int getInt()
   {
      return this.value;
   }

   public String toString()
   {
      return String.valueOf(this.value);
   }

   public boolean equals(Object other)
   {
      int result = this.shareEqual(other, IntegerRef.class);
      if (result != ObjectRef.MORE_EQUAL)
      {
         return result == ObjectRef.TRUE_EQUAL;
      }
      return this.value == ((IntegerRef) other).value;
   }

}
