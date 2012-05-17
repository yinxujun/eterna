
package self.micromagic.eterna.sql.preparer;

import java.io.Reader;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import self.micromagic.eterna.sql.PreparedStatementWrap;
import self.micromagic.eterna.sql.converter.ReaderConverter;
import self.micromagic.util.Utility;

class ReaderPreparer extends AbstractValuePreparer
{
   private Reader value;
   private int length;

   public ReaderPreparer(ValuePreparerCreater vpc, Reader value, int length)
   {
      super(vpc);
      this.length = length;
      this.value = value;
   }

   public ReaderPreparer(ValuePreparerCreater vpc, Reader value)
   {
      super(vpc);
      this.length = -1;
      this.value = value;
   }

   public void setValueToStatement(int index, PreparedStatementWrap stmtWrap)
         throws SQLException
   {
      if (this.length == -1)
      {
         stmtWrap.setObject(this.getName(), index, this.value, java.sql.Types.LONGVARCHAR);
      }
      else
      {
         stmtWrap.setCharacterStream(this.getName(), index, this.value, this.length);
      }
   }

   static class Creater extends AbstractCreater
   {
      ReaderConverter convert = new ReaderConverter();

      public Creater(ValuePreparerCreaterGenerator vpcg)
      {
         super(vpcg);
      }

      public ValuePreparer createPreparer(Object value)
      {
         return new ReaderPreparer(this, this.convert.convertToReader(value));
      }

      public ValuePreparer createPreparer(String value)
      {
         return new ReaderPreparer(this, this.convert.convertToReader(value));
      }

      public ValuePreparer createPreparer(Reader value)
      {
         return new ReaderPreparer(this, value);
      }

      public ValuePreparer createPreparer(Reader value, int length)
      {
         return new ReaderPreparer(this, value, length);
      }

   }

}