
package self.micromagic.eterna.sql.preparer;

import java.sql.SQLException;

import self.micromagic.eterna.sql.PreparedStatementWrap;

class NullPreparer extends AbstractValuePreparer
{
   private int type;

   public NullPreparer(ValuePreparerCreater vpc, int type)
   {
      super(vpc);
      this.type = type;
   }

   public void setValueToStatement(int index, PreparedStatementWrap stmtWrap)
         throws SQLException
   {
      stmtWrap.setNull(this.getName(), index, this.type);
   }

   static class Creater extends AbstractCreater
   {
      public Creater(ValuePreparerCreaterGenerator vpcg)
      {
         super(vpcg);
      }

      public ValuePreparer createPreparer(Object value)
      {
         return new NullPreparer(this, java.sql.Types.JAVA_OBJECT);
      }

      public ValuePreparer createPreparer(String value)
      {
         return new NullPreparer(this, java.sql.Types.VARCHAR);
      }

      public ValuePreparer createPreparer(int type)
      {
         return new NullPreparer(this, type);
      }

   }

}
