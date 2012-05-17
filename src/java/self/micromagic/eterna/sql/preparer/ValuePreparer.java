
package self.micromagic.eterna.sql.preparer;

import java.sql.SQLException;

import self.micromagic.eterna.sql.PreparedStatementWrap;
import self.micromagic.eterna.digester.ConfigurationException;

/**
 * ֵ׼����.
 */
public interface ValuePreparer
{
   /**
    * ������ֵ׼�����Ĵ�����.
    */
   ValuePreparerCreater getCreater() throws ConfigurationException;

   /**
    * ���������õ�PreparedStatementWrap��.
    *
    * @param index      Ҫ���õĲ���������ֵ
    * @param stmtWrap   PreparedStatement���⸽��
    */
   void setValueToStatement(int index, PreparedStatementWrap stmtWrap)
         throws SQLException;

   /**
    * ���ô˲���������.
    */
   public void setName(String name);

   /**
    * ��ȡ�˲���������.
    */
   public String getName();

   /**
    * ���ô˲������������ֵ, ��ֵ���ӦSQLAdapter���������õĲ�������ֵ.
    */
   public void setRelativeIndex(int index);

   /**
    * ��ȡ�˲������������ֵ, SQLAdapter����������, �������õ���Ӧλ��.
    */
   public int getRelativeIndex();

}
