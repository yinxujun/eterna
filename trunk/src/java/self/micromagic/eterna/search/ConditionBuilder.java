
package self.micromagic.eterna.search;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.sql.preparer.ValuePreparer;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.util.StringAppender;
import self.micromagic.util.StringTool;

/**
 * SQL���������������. <p>
 * {@link self.micromagic.eterna.search.ConditionBuilder.Condition}��ͨ����������������SQL������
 *
 * @author  micromagic
 * @version 0.8, 2004-11-4
 */
public interface ConditionBuilder
{
   public static final String[] OPERATOR_NAMES = {
      "isNull", "notNull", "checkNull",
      "equal", "notEqual", "large", "below", "notLarge", "notBelow",
      "beginWith", "endWith", "include", "match"
   };

   public static ValuePreparer[] EMPTY_PREPARERS = new ValuePreparer[0];

   void initialize(EternaFactory factory) throws ConfigurationException;

   public String getName() throws ConfigurationException;

   public String getCaption() throws ConfigurationException;

   /**
    * ����һ��SQL����.
    *
    * @param colName  Ҫ���ɵ�����������.
    * @param value    Ҫ���ɵ�������ֵ.
    * @param cp       ������������������Ӧ��ConditionProperty.
    * @return         �����ɵ�����, ����ز���.
    */
   public Condition buildeCondition(String colName, String value, ConditionProperty cp)
         throws ConfigurationException;

   public static class Condition
   {
      public final String sqlPart;
      public final ValuePreparer[] preparers;

      private String toStrBuf = null;

      public Condition(String sqlPart)
      {
         this(sqlPart, null);
      }

      public Condition(String sqlPart, ValuePreparer[] preparers)
      {
         this.sqlPart = sqlPart;
         this.preparers = preparers == null ? EMPTY_PREPARERS : preparers;
      }

      public String toString()
      {
         if (this.toStrBuf == null)
         {
            int count = this.sqlPart.length() + 39;
            StringAppender buf = StringTool.createStringAppender(count);
            buf.append("Condition[sqlPart:(").append(this.sqlPart);
            buf.append("),preparerCount:").append(this.preparers.length).append(']');
            this.toStrBuf = buf.toString();
         }
         return this.toStrBuf;
      }

   }

}
