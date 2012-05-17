
package self.micromagic.eterna.model;

import self.micromagic.eterna.digester.ConfigurationException;

public interface SearchExecuteGenerator
{
   /**
    * search��صĿ��Ʊ�ʶ�������ݼ�������.
    */
   public static final String SEARCH_MANAGER_ATTRIBUTES = "searchManager_attributes";

   /**
    * ���ö�ȡsearch���Ƶı�ǩ��
    */
   void setSearchNameTag(String tag) throws ConfigurationException;

   void setSearchName(String name) throws ConfigurationException;

   void setQueryResultName(String name) throws ConfigurationException;

   void setSearchManagerName(String name) throws ConfigurationException;

   void setSearchCountName(String name) throws ConfigurationException;

   void setSaveCondition(boolean saveCondition) throws ConfigurationException;

   void setStart(int start) throws ConfigurationException;

   void setCount(int count) throws ConfigurationException;

   void setDoExecute(boolean execute) throws ConfigurationException;

   /**
    * �����Ƿ��Ա������ݿ����ӵķ�ʽ��ѯ. <p>
    * ���������������true, ��ô��SearchExecute����������ʽΪhold��model��ִ�С�
    * ������������Ϊtrue�󣬻����saveCondition, start, count��3�����ԡ�
    */
   void setHoldConnection(boolean hold) throws ConfigurationException;

   Execute createExecute() throws ConfigurationException;

}
