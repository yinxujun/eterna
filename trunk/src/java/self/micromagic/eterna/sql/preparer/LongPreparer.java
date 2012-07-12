
package self.micromagic.eterna.sql.preparer;

import java.sql.SQLException;
import java.sql.Types;
import java.text.NumberFormat;
import java.text.DecimalFormat;

import self.micromagic.eterna.sql.PreparedStatementWrap;
import self.micromagic.util.converter.LongConverter;
import self.micromagic.eterna.digester.ConfigurationException;

class LongPreparer extends AbstractValuePreparer
{
   private long value;

   public LongPreparer(ValuePreparerCreater vpc, long value)
   {
      super(vpc);
      this.value = value;
   }

   public void setValueToStatement(int index, PreparedStatementWrap stmtWrap)
         throws SQLException
   {
      stmtWrap.setLong(this.getName(), index, this.value);
   }

   static class Creater extends AbstractCreater
   {
      LongConverter convert = new LongConverter();
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
            return this.vpcg.createNullPreparer(0, Types.BIGINT);
         }
         return new LongPreparer(this, this.convert.convertToLong(value, this.format));
      }

      public ValuePreparer createPreparer(String value)
            throws ConfigurationException
      {
         if (value == null)
         {
            return this.vpcg.createNullPreparer(0, Types.BIGINT);
         }
         return new LongPreparer(this, this.convert.convertToLong(value, this.format));
      }

      public ValuePreparer createPreparer(long value)
      {
         return new LongPreparer(this, value);
      }

   }

}