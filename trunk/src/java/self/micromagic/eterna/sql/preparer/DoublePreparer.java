
package self.micromagic.eterna.sql.preparer;

import java.sql.SQLException;
import java.sql.Types;
import java.text.NumberFormat;
import java.text.DecimalFormat;

import self.micromagic.eterna.sql.PreparedStatementWrap;
import self.micromagic.eterna.sql.converter.DoubleConverter;
import self.micromagic.eterna.digester.ConfigurationException;

class DoublePreparer extends AbstractValuePreparer
{
   private double value;

   public DoublePreparer(ValuePreparerCreater vpc, double value)
   {
      super(vpc);
      this.value = value;
   }

   public void setValueToStatement(int index, PreparedStatementWrap stmtWrap)
         throws SQLException
   {
      stmtWrap.setDouble(this.getName(), index, this.value);
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
            return this.vpcg.createNullPreparer(0, Types.DOUBLE);
         }
         return new DoublePreparer(this, this.convert.convertToDouble(value, this.format));
      }

      public ValuePreparer createPreparer(String value)
            throws ConfigurationException
      {
         if (value == null)
         {
            return this.vpcg.createNullPreparer(0, Types.DOUBLE);
         }
         return new DoublePreparer(this, this.convert.convertToDouble(value, this.format));
      }

      public ValuePreparer createPreparer(double value)
      {
         return new DoublePreparer(this, value);
      }

   }

}
