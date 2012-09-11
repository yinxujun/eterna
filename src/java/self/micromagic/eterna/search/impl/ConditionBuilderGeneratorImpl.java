
package self.micromagic.eterna.search.impl;

import java.util.HashMap;
import java.util.Map;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.search.ConditionBuilder;
import self.micromagic.eterna.search.ConditionBuilderGenerator;
import self.micromagic.eterna.share.AbstractGenerator;
import self.micromagic.util.StringAppender;
import self.micromagic.util.StringTool;

public class ConditionBuilderGeneratorImpl extends AbstractGenerator
      implements ConditionBuilderGenerator
{
   // �����ٶ���һ��, ��ֹConditionBuilder�ӿ��ж���ı��޸�
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
      builderMap.put(OPERATOR_NAMES[index++],
				new ConditionBuilderImpl(ConditionBuilderImpl.CHECK_OPT_TAG, -1));

      builderMap.put(OPERATOR_NAMES[index++], new ConditionBuilderImpl("=", 0));
      builderMap.put(OPERATOR_NAMES[index++], new ConditionBuilderImpl("<>", 0));
      builderMap.put(OPERATOR_NAMES[index++], new ConditionBuilderImpl(">", 0));
      builderMap.put(OPERATOR_NAMES[index++], new ConditionBuilderImpl("<", 0));
      builderMap.put(OPERATOR_NAMES[index++], new ConditionBuilderImpl("<=", 0));
      builderMap.put(OPERATOR_NAMES[index++], new ConditionBuilderImpl(">=", 0));
      builderMap.put(OPERATOR_NAMES[index++],
				new ConditionBuilderImpl(ConditionBuilderImpl.LIKE_OPT_TAG, 1));
      builderMap.put(OPERATOR_NAMES[index++],
				new ConditionBuilderImpl(ConditionBuilderImpl.LIKE_OPT_TAG, 2));
      builderMap.put(OPERATOR_NAMES[index++],
				new ConditionBuilderImpl(ConditionBuilderImpl.LIKE_OPT_TAG, 3));
      builderMap.put(OPERATOR_NAMES[index++],
				new ConditionBuilderImpl(ConditionBuilderImpl.LIKE_OPT_TAG, 0));
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
         cb = cb.copy();
      }
      cb.name = this.name;
      cb.caption = this.caption == null ? this.name : this.caption;
      return cb;
   }

	/**
	 * ����ƥ���ѯ���ַ�������Ҫת����ַ�. <p>
	 * ���û����Ҫת����ַ���, ��ֱ�ӷ���ԭ�ַ���, ��˿���
	 * ��<code>newStr == oldStr</code>�ж��Ƿ��д���.
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
				//���ｫmodifyCount�ĸ�������, ��ֹ��һ�ε���ʹ���������������ʼ��
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

}