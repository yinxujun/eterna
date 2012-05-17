
package self.micromagic.eterna.sql.preparer;

import java.sql.SQLException;

import self.micromagic.eterna.sql.PreparedStatementWrap;
import self.micromagic.eterna.sql.converter.BytesConverter;

class BytesPreparer extends AbstractValuePreparer
{
   private byte[] value;

   public BytesPreparer(ValuePreparerCreater vpc, byte[] value)
   {
      super(vpc);
      this.value = value;
   }

   public void setValueToStatement(int index, PreparedStatementWrap stmtWrap)
         throws SQLException
   {
      stmtWrap.setBytes(this.getName(), index, this.value);
   }

   static class Creater extends AbstractCreater
   {
      BytesConverter convert = new BytesConverter();
      String charset = "UTF-8";

      public Creater(ValuePreparerCreaterGenerator vpcg)
      {
         super(vpcg);
      }

      public void setCharset(String charset)
      {
         this.charset = charset;
      }

      public ValuePreparer createPreparer(Object value)
      {
         return new BytesPreparer(this, this.convert.convertToBytes(value, this.charset));
      }

      public ValuePreparer createPreparer(String value)
      {
         return new BytesPreparer(this, this.convert.convertToBytes(value, this.charset));
      }

      public ValuePreparer createPreparer(byte[] value)
      {
         return new BytesPreparer(this, value);
      }

   }

}