
package self.micromagic.eterna.sql.preparer;

import java.sql.SQLException;
import java.sql.Types;
import java.text.NumberFormat;
import java.text.DecimalFormat;

import self.micromagic.eterna.sql.PreparedStatementWrap;
import self.micromagic.util.converter.DoubleConverter;
import self.micromagic.eterna.digester.ConfigurationException;

class FloatPreparer extends AbstractValuePreparer
{
   private float value;

   public FloatPreparer(ValuePreparerCreater vpc, float value)
   {
      super(vpc);
      this.value = value;
   }

   public void setValueToStatement(int index, PreparedStatementWrap stmtWrap)
         throws SQLException
   {
      stmtWrap.setFloat(this.getName(), index, this.value);
   }

   static class Creater extends AbstractCreater
   {
      DoubleConverter convert = new DoubleConverter();
      NumberFormat format = null;

      public Creater(ValuePreparerCreaterGenerator vpcg)
      {
         super(vpcg);
      }

      public void setFormat(String formatStr)
      {
         this.format = new DecimalFormat(formatStr);
      }

      public ValuePreparer createPreparer(Object value)
            throws ConfigurationException
      {
         if (value == null)
         {
            return this.vpcg.createNullPreparer(0, Types.FLOAT);
         }
         return new FloatPreparer(this, (float) this.convert.convertToDouble(value, this.format));
      }

      public ValuePreparer createPreparer(String value)
            throws ConfigurationException
      {
         if (value == null)
         {
            return this.vpcg.createNullPreparer(0, Types.FLOAT);
         }
         return new FloatPreparer(this, (float) this.convert.convertToDouble(value, this.format));
      }

      public ValuePreparer createPreparer(float value)
      {
         return new FloatPreparer(this, value);
      }

   }

}
