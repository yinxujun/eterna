
package self.micromagic.eterna.sql.preparer;

import java.sql.SQLException;
import java.sql.Types;
import java.text.NumberFormat;
import java.text.DecimalFormat;

import self.micromagic.eterna.sql.PreparedStatementWrap;
import self.micromagic.util.converter.IntegerConverter;
import self.micromagic.eterna.digester.ConfigurationException;

class ShortPreparer extends AbstractValuePreparer
{
   private short value;

   public ShortPreparer(ValuePreparerCreater vpc, short value)
   {
      super(vpc);
      this.value = value;
   }

   public void setValueToStatement(int index, PreparedStatementWrap stmtWrap)
         throws SQLException
   {
      stmtWrap.setShort(this.getName(), index, this.value);
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
            return this.vpcg.createNullPreparer(0, Types.SMALLINT);
         }
         return new IntegerPreparer(this, this.convert.convertToInt(value, this.format));
      }

      public ValuePreparer createPreparer(String value)
            throws ConfigurationException
      {
         if (value == null)
         {
            return this.vpcg.createNullPreparer(0, Types.SMALLINT);
         }
         return new IntegerPreparer(this, this.convert.convertToInt(value, this.format));
      }

      public ValuePreparer createPreparer(short value)
      {
         return new ShortPreparer(this, value);
      }

   }

}