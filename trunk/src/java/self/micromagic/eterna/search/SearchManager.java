
package self.micromagic.eterna.search;

import java.util.List;
import java.io.Writer;
import java.io.IOException;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.sql.preparer.PreparerManager;
import self.micromagic.eterna.view.DataPrinter;

public interface SearchManager
{
   /**
    * ���ڱ�־�Ƿ�Ҫǿ��������е�����. <p>
    * �����Ƿ�Ҫ��ջ����������Ǹ���request�е�[queryTypeTag]��
    * ֵ��ȷ����, �����Ҫǿ�����, ����ڵ���ǰ�����·�������:
    * request.setAttribute(SearchManager.FORCE_CLEAR_CONDITION, "1");
    * ����, �����Ҫ�������õı�־ȥ��, ����ʹ�����·���:
    * request.removeAttribute(SearchManager.FORCE_CLEAR_CONDITION);
    */
   static final String FORCE_CLEAR_CONDITION = "ETERNA_FORCE_CLEAR_CONDITION";

   /**
    * ���ڱ�־�Ƿ�Ҫǿ�ƴ���request�е�����. <p>
    * ����Ϊ��Ч��, ����requestǰ�����жϴ˴ε�request�Ƿ���ǰ
    * һ����ͬ, ��ͬ�Ļ���᲻������. �����Ҫǿ�ƴ���request,
    * ����ڵ���ǰ�����·�������:
    * request.setAttribute(SearchManager.FORCE_DEAL_CONDITION, "1");
    * ����, �����Ҫ�������õı�־ȥ��, ����ʹ�����·���:
    * request.removeAttribute(SearchManager.FORCE_DEAL_CONDITION);
    */
   static final String FORCE_DEAL_CONDITION = "ETERNA_FORCE_DEAL_CONDITION";

   /**
    * ���ڱ�־�Ƿ�Ҫ�����е�Condition��������, �Ա�ʹ��. <p>
    * ���ڱ���Condition��Ҫ���ٿ���, ����Ĭ������ǲ�����.
    * �����Ҫ����Condition, ����ڵ���ǰ�����·�������:
    * request.setAttribute(SearchManager.SAVE_CONDITION, "1");
    * ����, �����Ҫ�������õı�־ȥ��, ����ʹ�����·���:
    * request.removeAttribute(SearchManager.SAVE_CONDITION);
    */
   static final String SAVE_CONDITION = "ETERNA_SAVE_CONDITION";

   /**
    * ��ʶ�Ƿ���ʹ�����ݼ��е�ָ��ֵ����Ϊ������Ĭ��ֵ.
    */
   static final String DATA_DEFAULT_VALUE_PREFIX = "$data.";

   /**
    * ��ҳʱ��ҳ����ʾ������¼��.
    */
   static final int MAX_PAGE_SIZE = 1024;

	/**
	 * Ĭ�ϵĲ�ѯ��ص���������.
	 */
   static final Attributes DEFAULT_PROPERTIES = new Attributes(
         null, null, null, null, null, null);

   /**
    * ��õ�ǰ���ڵ�ҳ��.
    */
   int getPageNum();

   /**
    * ��õ�ǰҳ����ʾ������.
    */
   int getPageSize(int defaultSize);

   /**
    * �Ƿ���ڲ�ѯ��־.
    */
   boolean hasQueryType(AppData data) throws ConfigurationException;

   /**
    * ��õ�ǰ�������汾, ÿ����һ�������汾�Զ���1, ��ʼ�汾��Ϊ1.
    */
   int getConditionVersion() throws ConfigurationException;

   /**
    * ����request�е���Ϣ, ����������ҳ��.
    */
   void setPageNumAndCondition(AppData data, SearchAdapter search)
         throws ConfigurationException;

   /**
    * ��ȡ���������PreparerManager.
    */
   PreparerManager getPreparerManager();

   /**
    * ��ȡ���������PreparerManager�Ӽ�.
    */
   PreparerManager getSpecialPreparerManager(SearchAdapter search)
         throws ConfigurationException;

   /**
    * ��ȡ���������Ϊ������sql�Ӿ�.
    */
   String getConditionPart();

   /**
    * ��ȡ���������Ϊ������sql�Ӿ�.
    *
    * @param needWrap   �Ƿ���Ҫ�����������������"(", ")".
    */
   String getConditionPart(boolean needWrap);

   /**
    * ��ȡ���������Ϊ������sql�Ӿ���Ӽ�.
    */
   String getSpecialConditionPart(SearchAdapter search) throws ConfigurationException;

   /**
    * ��ȡ���������Ϊ������sql�Ӿ���Ӽ�.
    *
    * @param needWrap   �Ƿ���Ҫ�����������������"(", ")".
    */
   String getSpecialConditionPart(SearchAdapter search, boolean needWrap) throws ConfigurationException;

   /**
    * ��ȡ��SearchManager����������.
    */
   Attributes getAttributes();

   /**
    * ���ñ�SearchManager����������.
    */
   void setAttributes(Attributes attributes);

   /**
    * �������������ƻ�ȡĳ������õ�����.
    * ע: ����ֻ��ȡ�������µĵ�һ������.
    */
   Condition getCondition(String name);

   /**
    * �������������ƻ�ȡ�����������й���õ�����.
    */
   List getConditions(String name);

   /**
    * ��ȡ���й���õ�����.
    */
   List getConditions();

	/**
	 * ����Ĳ�ѯ������Ԫ.
	 */
   static final class Condition
   {
      /**
       * ����������
       */
      public final String name;

      /**
       * �������������������
       */
      public final String group;

      /**
       * �����ڸ������ϵ�ֵ
       */
      public final String value;

      /**
       * ���ɸ�������Ҫʹ�õ�ConditionBuilder
       */
      public final ConditionBuilder builder;

      public Condition(String name, String group, String value, ConditionBuilder builder)
      {
         this.name = name;
         this.group = group;
         this.value = value;
         this.builder = builder;
      }

   }

	/**
	 * ��ѯ��ص���������.
	 */
   static final class Attributes
			implements DataPrinter.BeanPrinter
   {
      /**
       * �洢ҳ�ŵĿؼ�����
       */
      public final String pageNumTag;

      /**
       * �洢ÿҳ��ʾ�����Ŀؼ�����
       */
      public final String pageSizeTag;

      /**
       * �洢XML��ʽ��ѯ�����Ŀؼ�����
       */
      public final String querySettingTag;

      /**
       * �洢��ѯ��ʽ�Ŀؼ�����
       */
      public final String queryTypeTag;

      /**
       * ��������Ĳ�ѯ��ʽ
       */
      public final String queryTypeClear;

      /**
       * ���������Ĳ�ѯ��ʽ
       */
      public final String queryTypeReset;

      public Attributes(String pageNumTag, String pageSizeTag, String querySettingTag,
            String queryTypeTag, String queryTypeClear, String queryTypeReset)
      {
         this.pageNumTag = pageNumTag == null ? "pageNum" : pageNumTag;
         this.pageSizeTag = pageSizeTag == null ? "pageSize" : pageSizeTag;
         this.querySettingTag = querySettingTag == null ? "querySetting" : querySettingTag;
         this.queryTypeTag = queryTypeTag == null ? "queryType" : queryTypeTag;
         this.queryTypeClear = queryTypeClear == null ? "clear" : queryTypeClear;
         this.queryTypeReset = queryTypeReset == null ? "set" : queryTypeReset;
      }

		public void print(DataPrinter p, Writer out, Object bean)
				throws IOException, ConfigurationException
		{
			p.printObjectBegin(out);
			p.printPairWithoutCheck(out, "pageNumTag", this.pageNumTag, true);
			p.printPairWithoutCheck(out, "pageSizeTag", this.pageSizeTag, false);
			p.printPairWithoutCheck(out, "querySettingTag", this.querySettingTag, false);
			p.printPairWithoutCheck(out, "queryTypeClear", this.queryTypeClear, false);
			p.printPairWithoutCheck(out, "queryTypeReset", this.queryTypeReset, false);
			p.printPairWithoutCheck(out, "queryTypeTag", this.queryTypeTag, false);
			p.printObjectEnd(out);
		}

   }

}
