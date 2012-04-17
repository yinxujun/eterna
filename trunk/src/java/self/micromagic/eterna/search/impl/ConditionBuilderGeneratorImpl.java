
package self.micromagic.eterna.search.impl;

import java.util.Map;
import java.util.HashMap;

import self.micromagic.eterna.share.AbstractGenerator;
import self.micromagic.eterna.share.TypeManager;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.search.ConditionBuilderGenerator;
import self.micromagic.eterna.search.ConditionBuilder;
import self.micromagic.eterna.search.ConditionProperty;
import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.sql.preparer.ValuePreparer;
import self.micromagic.eterna.sql.preparer.ValuePreparerCreaterGeneratorImpl;

public class ConditionBuilderGeneratorImpl extends AbstractGenerator
      implements ConditionBuilderGenerator
{
   // �����ٶ���һ��, ��ֹConditionBuilder�ӿ��ж���ı��޸�
   private final static String[] OPERATOR_NAMES = {
      "isNull", "notNull", "checkNull",
      "equal", "notEqual", "large", "below", "notLarge", "notBelow",
      "beginWith", "endWith", "include", "match"
   };

   private static Map builderMap = new HashMap();

   private String caption;
   private String operator;

   static
   {
      int index = 0;
      builderMap.put(OPERATOR_NAMES[index++], new ConditionBuilderImpl("IS NULL", -1));
      builderMap.put(OPERATOR_NAMES[index++], new ConditionBuilderImpl("IS NOT NULL", -1));
      builderMap.put(OPERATOR_NAMES[index++], new ConditionBuilderImpl("CHECK", -1));

      builderMap.put(OPERATOR_NAMES[index++], new ConditionBuilderImpl("=", 0));
      builderMap.put(OPERATOR_NAMES[index++], new ConditionBuilderImpl("<>", 0));
      builderMap.put(OPERATOR_NAMES[index++], new ConditionBuilderImpl(">", 0));
      builderMap.put(OPERATOR_NAMES[index++], new ConditionBuilderImpl("<", 0));
      builderMap.put(OPERATOR_NAMES[index++], new ConditionBuilderImpl("<=", 0));
      builderMap.put(OPERATOR_NAMES[index++], new ConditionBuilderImpl(">=", 0));
      builderMap.put(OPERATOR_NAMES[index++], new ConditionBuilderImpl("LIKE", 1));
      builderMap.put(OPERATOR_NAMES[index++], new ConditionBuilderImpl("LIKE", 2));
      builderMap.put(OPERATOR_NAMES[index++], new ConditionBuilderImpl("LIKE", 3));
      builderMap.put(OPERATOR_NAMES[index++], new ConditionBuilderImpl("LIKE", 0));
   }

   public void setCaption(String caption)
   {
      this.caption = caption;
   }

   public void setOperator(String operator)
   {
      this.operator = operator;
   }

   public Object create()
         throws ConfigurationException
   {
      return this.createConditionBuilder();
   }

   public ConditionBuilder createConditionBuilder()
         throws ConfigurationException
   {
      ConditionBuilderImpl cb = (ConditionBuilderImpl) builderMap.get(this.operator);
      if (cb == null)
      {
         cb = new ConditionBuilderImpl("=", 0);
      }
      else
      {
         cb = (ConditionBuilderImpl) cb.clone();
      }
      cb.name = this.name;
      cb.caption = this.caption == null ? this.name : this.caption;
      return cb;
   }

   private static class ConditionBuilderImpl
         implements ConditionBuilder, Cloneable
   {
      private String name;
      private String caption;

      private String operator;

      /**
       *   -1 : �Ƿ�Ϊ�յ��ж�
       *    0 : һ�����
       *  0x1 : ����ַ���, �ں������ͨ���
       *  0x2 : ����ַ���, ��ǰ�����ͨ���
       *  0x3 : ����ַ���, �����߼���ͨ���
       */
      private int optType;

      public ConditionBuilderImpl(String operator, int optType)
      {
         this.operator = operator;
         this.optType = optType;
      }

      public void initialize(EternaFactory factory)
            throws ConfigurationException
      {
      }

      public String getName()
      {
         return this.name;
      }

      public String getCaption() throws ConfigurationException
      {
         return this.caption;
      }

      public Condition buildeCondition(String colName, String value,
            ConditionProperty cp)
            throws ConfigurationException
      {
         if (this.optType == -1)
         {
            if ("CHECK".equals(this.operator))
            {
               return "1".equals(value) ?
                     new Condition(colName + " IS NULL") : new Condition(colName + " IS NOT NULL");

            }
            else
            {
               int count = colName.length() + this.operator.length() + 1;
               StringBuffer temp = new StringBuffer(count);
               temp.append(colName).append(" ").append(this.operator);
               return new Condition(temp.toString());
            }
         }

         if (value == null || value.length() == 0)
         {
            if (TypeManager.isTypeString(cp.getColumnType()))
            {
               String tempOp1, tempOp2;
               String linkOp;
               if ("=".equals(this.operator))
               {
                  tempOp1 = " IS NULL";
                  tempOp2 = " = ''";
                  linkOp = " OR ";
               }
               else if ("LIKE".equals(this.operator))
               {
                  tempOp1 = " LIKE '%'";
                  tempOp2 = " IS NULL";
                  linkOp = " OR ";
               }
               else
               {
                  tempOp1 = " IS NOT NULL";
                  tempOp2 = " <> ''";
                  linkOp = " AND ";
               }
               int count = colName.length() + tempOp1.length() + tempOp2.length() + 8;
               count = count * 2;
               StringBuffer temp = new StringBuffer(count);
               temp.append("(").append(colName).append(tempOp1).append(linkOp);
               temp.append(colName).append(tempOp2).append(")");
               return new Condition(temp.toString());
            }
            else
            {
               String temp = "=".equals(this.operator) || "LIKE".equals(this.operator) ?
                     colName + " IS NULL" : colName + " IS NOT NULL";
               return new Condition(temp);
            }
         }

         int count = colName.length() + this.operator.length() + 3;
         StringBuffer sqlPart = new StringBuffer(count);
         sqlPart.append(colName).append(" ").append(this.operator).append(" ?");
         ValuePreparer[] preparers = new ValuePreparer[1];
         if (TypeManager.isTypeString(cp.getColumnType()))
         {
            String strValue = "";
            if (this.optType == 0)
            {
               strValue = value;
            }
            else
            {
               StringBuffer temp = new StringBuffer(value.length() + 2);
               if ((this.optType & 0x2) != 0)
               {
                  temp.append('%');
               }
               temp.append(value);
               if ((this.optType & 0x1) != 0)
               {
                  temp.append('%');
               }
               strValue = temp.toString();
            }
            preparers[0] = cp.createValuePreparer(strValue);
         }
         else
         {
            String opt = this.operator;
            if ("LIKE".equals(opt))
            {
               opt = "=";
            }
            preparers[0] = cp.createValuePreparer(value);
         }
         return new Condition(sqlPart.toString(), preparers);
      }

      protected Object clone()
      {
         Object obj = null;
         try
         {
            obj = super.clone();
         }
         catch (CloneNotSupportedException ex) {}
         return obj;
      }

   }

}