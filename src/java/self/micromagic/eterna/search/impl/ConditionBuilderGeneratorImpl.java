
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
import self.micromagic.util.StringAppender;
import self.micromagic.util.StringTool;

public class ConditionBuilderGeneratorImpl extends AbstractGenerator
      implements ConditionBuilderGenerator
{
	private static final String EQUALS_OPT_TAG = "=";
	private static final String LIKE_OPT_TAG = "LIKE";
	private static final String CHECK_OPT_TAG = "CHECK";

   // 这里再定义一边, 防止ConditionBuilder接口中定义的被修改
   private static final String[] OPERATOR_NAMES = {
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
      builderMap.put(OPERATOR_NAMES[index++], new ConditionBuilderImpl(CHECK_OPT_TAG, -1));

      builderMap.put(OPERATOR_NAMES[index++], new ConditionBuilderImpl("=", 0));
      builderMap.put(OPERATOR_NAMES[index++], new ConditionBuilderImpl("<>", 0));
      builderMap.put(OPERATOR_NAMES[index++], new ConditionBuilderImpl(">", 0));
      builderMap.put(OPERATOR_NAMES[index++], new ConditionBuilderImpl("<", 0));
      builderMap.put(OPERATOR_NAMES[index++], new ConditionBuilderImpl("<=", 0));
      builderMap.put(OPERATOR_NAMES[index++], new ConditionBuilderImpl(">=", 0));
      builderMap.put(OPERATOR_NAMES[index++], new ConditionBuilderImpl(LIKE_OPT_TAG, 1));
      builderMap.put(OPERATOR_NAMES[index++], new ConditionBuilderImpl(LIKE_OPT_TAG, 2));
      builderMap.put(OPERATOR_NAMES[index++], new ConditionBuilderImpl(LIKE_OPT_TAG, 3));
      builderMap.put(OPERATOR_NAMES[index++], new ConditionBuilderImpl(LIKE_OPT_TAG, 0));
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

	/**
	 * 处理匹配查询的字符串中需要转义的字符. <p>
	 * 如果没有需要转义的字符串, 会直接返回原字符串, 因此可以
	 * 用<code>newStr == oldStr</code>判断是否有处理.
	 */
	public static String dealEscapeString(String str)
	{
		if (str == null)
		{
			return null;
		}
		StringAppender temp = null;
		int modifyCount = 0;
		for (int i = 0; i < str.length(); i++)
		{
			char c = str.charAt(i);
			String appendStr = null;
			if (c == '%' || c == '_' || c == '\\')
			{
				appendStr = "\\" + c;
				modifyCount++;
			}
			if (modifyCount == 1)
			{
				temp = StringTool.createStringAppender(str.length() + 16)
						.append(str.substring(0, i));
				//这里将modifyCount的个数增加, 防止下一次调用使他继续进入这个初始化
				modifyCount++;
			}
			if (modifyCount > 0)
			{
				if (appendStr == null)
				{
					temp.append(c);
				}
				else
				{
					temp.append(appendStr);
				}
			}
		}
		return temp == null ? str : temp.toString();
	}

   public static class ConditionBuilderImpl
         implements ConditionBuilder, Cloneable
   {
      private String name;
      private String caption;

      private String operator;

      /**
       *   -1 : 是否为空的判断
       *    0 : 一般情况
       *  0x1 : 针对字符串, 在后面加上通配符
       *  0x2 : 针对字符串, 在前面加上通配符
       *  0x3 : 针对字符串, 在两边加上通配符
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
            if (CHECK_OPT_TAG.equalsIgnoreCase(this.operator))
            {
               return "1".equals(value) ? new Condition(colName + " IS NULL")
							: new Condition(colName + " IS NOT NULL");
            }
            else
            {
               int count = colName.length() + this.operator.length() + 1;
               StringAppender temp = StringTool.createStringAppender(count);
               temp.append(colName).append(' ').append(this.operator);
               return new Condition(temp.toString());
            }
         }

         if (value == null || value.length() == 0)
         {
				return this.getNullCheckCondition(colName);
         }

         int count = colName.length() + this.operator.length() + 3;
         StringAppender sqlPart = StringTool.createStringAppender(count);
         sqlPart.append(colName).append(' ').append(this.operator).append(" ?");
         ValuePreparer[] preparers = new ValuePreparer[1];
         if (TypeManager.isTypeString(cp.getColumnType()))
         {
				if (LIKE_OPT_TAG.equalsIgnoreCase(this.operator))
				{
					if (this.optType == 0)
					{
						// 对于match, 默认加上escape
						sqlPart.append(" escape '\\'");
					}
					else
					{
                  String newStr = dealEscapeString(value);
						if (newStr != value)
						{
							value = newStr;
							sqlPart.append(" escape '\\'");
						}
					}
				}
            String strValue = "";
            if (this.optType == 0)
            {
               strValue = value;
            }
            else
            {
               StringAppender temp = StringTool.createStringAppender(value.length() + 2);
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
            if (LIKE_OPT_TAG.equalsIgnoreCase(opt))
            {
               opt = "=";
            }
            preparers[0] = cp.createValuePreparer(value);
         }
         return new Condition(sqlPart.toString(), preparers);
      }

		protected Condition getNullCheckCondition(String colName)
		{
			boolean equalsFlag = EQUALS_OPT_TAG.equalsIgnoreCase(this.operator)
					|| LIKE_OPT_TAG.equalsIgnoreCase(this.operator);
			String temp =  equalsFlag ? colName + " IS NULL" : colName + " IS NOT NULL";
			return new Condition(temp);
			/*
			对于字符串的情况, 这里不做是否为空字符串的判断, 如果需要可以重写这个方法,
			然后在配置中定义自己的builder
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
				else if (LIKE_OPT_TAG.equalsIgnoreCase(this.operator))
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
				StringAppender temp = StringTool.createStringAppender(count);
				temp.append('(').append(colName).append(tempOp1).append(linkOp);
				temp.append(colName).append(tempOp2).append(')');
				return new Condition(temp.toString());
			}
			*/
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
