
package self.micromagic.eterna.sql.preparer;

import java.sql.SQLException;

import self.micromagic.eterna.sql.PreparedStatementWrap;

/**
 * 参数准备者.
 */
public interface ValuePreparer
{
   /**
    * 将参数设置到PreparedStatementWrap中.
    *
    * @param index      要设置的参数的索引值
    * @param stmtWrap   PreparedStatement的外附类
    */
   void setValueToStatement(int index, PreparedStatementWrap stmtWrap)
         throws SQLException;

   /**
    * 设置此参数的名称.
    */
   public void setName(String name);

   /**
    * 获取此参数的名称.
    */
   public String getName();

   /**
    * 设置此参数的相对索引值, 此值需对应SQLAdapter对象中配置的参数索引值.
    */
   public void setRelativeIndex(int index);

   /**
    * 获取此参数的相对索引值, SQLAdapter对象会调用它, 将其设置到相应位置.
    */
   public int getRelativeIndex();

}
