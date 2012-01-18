package self.micromagic.util;

import java.io.Serializable;

public class DoubleRef extends ObjectRef
      implements Serializable
{
   public double value;

   public DoubleRef()
   {
      this.value = 0.0;
   }

   public DoubleRef(double value)
   {
      this.value = value;
   }

   public DoubleRef(Double value)
   {
      this.value = value.doubleValue();
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
      return (long) this.value;
   }

   public double doubleValue()
   {
      return this.value;
   }

   public static double getDoubleValue(Object obj)
   {
      if (obj == null)
      {
         return 0.0;
      }
      else if (obj instanceof Number)
      {
         return ((Number) obj).doubleValue();
      }
      else if (obj instanceof String)
      {
         try
         {
            return Double.parseDouble((String) obj);
         }
         catch (NumberFormatException e) {}
      }
      return 0.0;
   }

   public void setObject(Object obj)
   {
      this.value = DoubleRef.getDoubleValue(obj);
      super.setObject(null);
   }

   public Object getObject()
   {
      Object obj = super.getObject();
      if (obj == null || ((Double) obj).doubleValue() != this.value)
      {
         obj = new Double(this.value);
         super.setObject(obj);
      }
      return obj;
   }

   public void setDouble(double value)
   {
      this.value = value;
      super.setObject(null);
   }

   public double getDouble()
   {
      return this.value;
   }

   public String toString()
   {
      return String.valueOf(this.value);
   }

   public boolean equals(Object other)
   {
      int result = this.shareEqual(other, DoubleRef.class);
      if (result != MORE_EQUAL)
      {
         return result == TRUE_EQUAL;
      }
      return this.value == ((DoubleRef) other).value;
   }

}
