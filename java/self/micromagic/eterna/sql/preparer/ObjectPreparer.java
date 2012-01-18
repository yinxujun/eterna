
package self.micromagic.eterna.sql.preparer;

import java.sql.SQLException;

import self.micromagic.eterna.sql.PreparedStatementWrap;

public class ObjectPreparer extends AbstractValuePreparer
{
   protected Object value;
   protected Integer targetSqlType;
   protected Integer scale;

   public ObjectPreparer(int index, Object value)
   {
      this(index, value, null, null);
   }

   public ObjectPreparer(int index, Object value, Integer targetSqlType)
   {
      this(index, value, targetSqlType, null);
   }

   public ObjectPreparer(int index, Object value, Integer targetSqlType, Integer scale)
   {
      this.setRelativeIndex(index);
      this.value = value;
      this.targetSqlType = targetSqlType;
      this.scale = scale;
   }

   public void setValueToStatement(int index, PreparedStatementWrap stmtWrap)
         throws SQLException
   {
      if (this.targetSqlType == null)
      {
         stmtWrap.setObject(this.getName(), index, this.value);
      }
      else if (this.scale == null)
      {
         stmtWrap.setObject(this.getName(), index, this.value, this.targetSqlType.intValue());
      }
      else
      {
         stmtWrap.setObject(this.getName(), index, this.value,
               this.targetSqlType.intValue(), this.scale.intValue());
      }
   }

}