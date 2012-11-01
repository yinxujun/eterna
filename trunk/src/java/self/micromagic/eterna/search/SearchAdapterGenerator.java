
package self.micromagic.eterna.search;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.AdapterGenerator;

/**
 * @author micromagic@sina.com
 */
public interface SearchAdapterGenerator extends AdapterGenerator
{
   public static final String NONE_QUERY_NAME = "$none";

   void setName(String name) throws ConfigurationException;

   String getName() throws ConfigurationException;

   SearchAdapter createSearchAdapter() throws ConfigurationException;

   void setQueryName(String queryName) throws ConfigurationException;

   /**
    * ��ñ�searchʹ�õ�query������.
    */
   String getQueryName() throws ConfigurationException;

   /**
    * ����һ��ҳ�������ʾ�ļ�¼����.
    */
   void setPageSize(int pageSize);

   /**
    * �����Ƿ������������, ��Ҫ���¹�������������.
    */
   void setSpecialCondition(boolean special) throws ConfigurationException;

   /**
    * ���ü����ܼ�¼���ķ�ʽ. <p>
    * �ֱ�Ϊauto, count, none. Ĭ��ֵΪ: auto.
    * ����, �����԰�search:[searchName],[readerName]�ĸ�ʽ�������ڼ����ܼ�¼����search.
    */
   void setCountType(String countType) throws ConfigurationException;

   /**
    * �����Ƿ���Ҫ�����������������"(", ")".
    */
   void setNeedWrap(boolean needWrap) throws ConfigurationException;

   /**
    * ������Session�д��SearchManager������.
    */
   void setSearchManagerName(String name) throws ConfigurationException;

   void setConditionIndex(int index) throws ConfigurationException;

   void setOtherSearchManagerName(String otherName) throws ConfigurationException;

   void setConditionPropertyOrderWithOther(String order) throws ConfigurationException;

   void setParentConditionPropretyName(String parentName) throws ConfigurationException;

   void setConditionPropertyOrder(String order) throws ConfigurationException;

   void clearConditionPropertys() throws ConfigurationException;

   void addConditionProperty(ConditionProperty cp) throws ConfigurationException;

   /**
    * ����һ��ColumnSetting������, �������ֶ�ȡ�ĸ�ColumnSetting.
    */
   void setColumnSettingType(String type) throws ConfigurationException;

   /**
    * ����һ��ColumnSetting, SearchAdapter�����������ò�ѯ����.
    */
   void setColumnSetting(ColumnSetting setting) throws ConfigurationException;

   /**
    * ����һ��ParameterSetting, SearchAdapter�����������ò�ѯ����.
    */
   void setParameterSetting(ParameterSetting setting)  throws ConfigurationException;

}
