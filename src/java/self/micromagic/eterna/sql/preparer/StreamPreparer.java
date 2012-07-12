
package self.micromagic.eterna.sql.preparer;

import java.io.InputStream;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import self.micromagic.eterna.sql.PreparedStatementWrap;
import self.micromagic.util.converter.StreamConverter;
import self.micromagic.util.Utility;

class StreamPreparer extends AbstractValuePreparer
{
   private InputStream value;
   private int length;

   public StreamPreparer(ValuePreparerCreater vpc, InputStream value, int length)
   {
      super(vpc);
      this.length = length;
      this.value = value;
   }
   public StreamPreparer(ValuePreparerCreater vpc, InputStream value)
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
         stmtWrap.setObject(this.getName(), index, this.value, java.sql.Types.LONGVARBINARY);
      }
      else
      {
         stmtWrap.setBinaryStream(this.getName(), index, this.value, this.length);
      }
   }

   static class Creater extends AbstractCreater
   {
      StreamConverter convert = new StreamConverter();
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
         return new StreamPreparer(this, this.convert.convertToStream(value, this.charset));
      }

      public ValuePreparer createPreparer(String value)
      {
         return new StreamPreparer(this, this.convert.convertToStream(value, this.charset));
      }

      public ValuePreparer createPreparer(InputStream value)
      {
         return new StreamPreparer(this, value);
      }

      public ValuePreparer createPreparer(InputStream value, int length)
      {
         return new StreamPreparer(this, value, length);
      }

   }

}