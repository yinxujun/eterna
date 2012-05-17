
package self.micromagic.eterna.sql.preparer;

import java.sql.SQLException;
import java.sql.Types;
import java.text.NumberFormat;
import java.text.DecimalFormat;

import self.micromagic.eterna.sql.PreparedStatementWrap;
import self.micromagic.eterna.sql.converter.IntegerConverter;
import self.micromagic.eterna.digester.ConfigurationException;

class BytePreparer extends AbstractValuePreparer
{
   private byte value;

   public BytePreparer(ValuePreparerCreater vpc, byte value)
   {
      super(vpc);
      this.value = value;
   }

   public void setValueToStatement(int index, PreparedStatementWrap stmtWrap)
         throws SQLException
   {
      stmtWrap.setByte(this.getName(), index, this.value);
   }

   static class Creater extends AbstractCreater
   {
      IntegerConverter convert = new IntegerConverter();
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
            return this.vpcg.createNullPreparer(0, Types.TINYINT);
         }
         return new BytePreparer(this, (byte) this.convert.convertToInt(value, this.format));
      }

      public ValuePreparer createPreparer(String value)
            throws ConfigurationException
      {
         if (value == null)
         {
            return this.vpcg.createNullPreparer(0, Types.TINYINT);
         }
         return new BytePreparer(this, (byte) this.convert.convertToInt(value, this.format));
      }

      public ValuePreparer createPreparer(byte value)
      {
         return new BytePreparer(this, value);
      }

   }

}