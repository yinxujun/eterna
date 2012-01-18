
package self.micromagic.eterna.share;

import java.util.List;

import self.micromagic.eterna.sql.ResultFormat;
import self.micromagic.eterna.sql.ResultReaderManager;
import self.micromagic.eterna.sql.QueryAdapter;
import self.micromagic.eterna.sql.QueryAdapterGenerator;
import self.micromagic.eterna.sql.UpdateAdapter;
import self.micromagic.eterna.sql.UpdateAdapterGenerator;
import self.micromagic.eterna.sql.SpecialLog;
import self.micromagic.eterna.sql.SQLParameterGroup;
import self.micromagic.eterna.sql.preparer.ValuePreparerCreaterGenerator;
import self.micromagic.eterna.sql.preparer.ValuePreparerCreater;
import self.micromagic.eterna.search.ConditionBuilder;
import self.micromagic.eterna.search.SearchAdapter;
import self.micromagic.eterna.search.SearchAdapterGenerator;
import self.micromagic.eterna.search.SearchManagerGenerator;
import self.micromagic.eterna.search.SearchManager;
import self.micromagic.eterna.view.Component;
import self.micromagic.eterna.view.ViewAdapter;
import self.micromagic.eterna.view.ViewAdapterGenerator;
import self.micromagic.eterna.view.StringCoder;
import self.micromagic.eterna.view.Function;
import self.micromagic.eterna.view.Resource;
import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.model.ModelExport;
import self.micromagic.eterna.model.ModelAdapter;
import self.micromagic.eterna.model.ModelAdapterGenerator;
import self.micromagic.eterna.model.ModelCaller;
import self.micromagic.eterna.security.UserManager;

public interface EternaFactory extends Factory
{

   /**
    * ��ñ�factory�ĸ�factory.
    */
   EternaFactory getShareFactory() throws ConfigurationException;

   /**
    * ���һ��UserManager����.
    *
    * @return  ���UserManager����δ���õĻ�, �򷵻�null.
    */
   UserManager getUserManager() throws ConfigurationException;

   /**
    * ����һ��UserManager����.
    */
   void setUserManager(UserManager um) throws ConfigurationException;

   /**
    * ���һ��DataSourceManager����.
    *
    * @return  ���DataSourceManager����δ���õĻ�, �򷵻�null.
    */
   DataSourceManager getDataSourceManager() throws ConfigurationException;

   /**
    * ����һ��UserManager����.
    */
   void setDataSourceManager(DataSourceManager dsm) throws ConfigurationException;


   //----------------------------------  SQLFactory  --------------------------------------

   /**
    * ���һ��������ֵ.
    *
    * @param name       ����������.
    */
   String getConstantValue(String name) throws ConfigurationException;

   /**
    * ����һ��������ֵ.
    *
    * @param name       ����������.
    * @param value      ������ֵ.
    */
   void addConstantValue(String name, String value) throws ConfigurationException;

   /**
    * �����־��¼��<code>SpecialLog</code>.
    */
   SpecialLog getSpecialLog() throws ConfigurationException;

   /**
    * ������־��¼��<code>SpecialLog</code>.
    */
   void setSpecialLog(SpecialLog sl)throws ConfigurationException;

   /**
    * ���һ��ResultFormat��. ���ڸ�ʽ����ѯ�Ľ��.
    *
    * @param name       format����.
    */
   ResultFormat getFormat(String name) throws ConfigurationException;

   /**
    * ����һ��ResultFormat��. ���ڸ�ʽ����ѯ�Ľ��.
    *
    * @param name       format����.
    * @param format     Ҫ���õ�ResultFormat��.
    */
   void addFormat(String name, ResultFormat format) throws ConfigurationException;

   /**
    * ���һ��ResultReaderManager��. ���ڹ����ѯ��ʾ����.
    *
    * @param name       ResultReaderManager����.
    */
   ResultReaderManager getReaderManager(String name) throws ConfigurationException;

   /**
    * ����һ��ResultReaderManager��. ���ڹ����ѯ��ʾ����.
    *
    * @param name        ResultReaderManager����.
    * @param manager     Ҫ���õ�ResultReaderManagerʵ��.
    */
   void addReaderManager(String name, ResultReaderManager manager) throws ConfigurationException;

   /**
    * ���һ��SQLParameterGroup��.
    *
    * @param name       SQLParameterGroup����.
    */
   SQLParameterGroup getParameterGroup(String name) throws ConfigurationException;

   /**
    * ����һ��SQLParameterGroup��.
    *
    * @param name        SQLParameterGroup����.
    * @param group       Ҫ���õ�SQLParameterGroupʵ��.
    */
   void addParameterGroup(String name, SQLParameterGroup group) throws ConfigurationException;

   /**
    * ����һ��<code>QueryAdapter</code>��ʵ��.
    *
    * @param name       <code>QueryAdapter</code>������.
    * @return           <code>QueryAdapter</code>��ʵ��.
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   QueryAdapter createQueryAdapter(String name) throws ConfigurationException;

   /**
    * ����һ��<code>QueryAdapter</code>��ʵ��.
    *
    * @param id         <code>QueryAdapter</code>��id.
    * @return           <code>QueryAdapter</code>��ʵ��.
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   QueryAdapter createQueryAdapter(int id) throws ConfigurationException;

   /**
    * ͨ��<code>QueryAdapter</code>�����ƻ������id.
    *
    * @param name       <code>QueryAdapter</code>�ĵ�����.
    * @return           <code>QueryAdapter</code>��id.
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   int getQueryAdapterId(String name) throws ConfigurationException;

   /**
    * ע��һ��<code>QueryAdapter</code>.
    *
    * @param generator   ��Ҫע���<code>QueryAdapter</code>������.
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   void registerQueryAdapter(QueryAdapterGenerator generator) throws ConfigurationException;;

   /**
    * ����һ��<code>QueryAdapter</code>��ע��.
    *
    * @param name       ��Ҫ����ע���<code>QueryAdapter</code>������.
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   void deregisterQueryAdapter(String name) throws ConfigurationException;

   /**
    * ����һ��<code>UpdateAdapter</code>��ʵ��.
    *
    * @param name       <code>UpdateAdapter</code>������.
    * @return           <code>UpdateAdapter</code>��ʵ��.
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   UpdateAdapter createUpdateAdapter(String name) throws ConfigurationException;

   /**
    * ����һ��<code>UpdateAdapter</code>��ʵ��.
    *
    * @param id         <code>UpdateAdapter</code>��id.
    * @return           <code>UpdateAdapter</code>��ʵ��.
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   UpdateAdapter createUpdateAdapter(int id) throws ConfigurationException;

   /**
    * ͨ��<code>UpdateAdapter</code>�����ƻ������id.
    *
    * @param name       <code>UpdateAdapter</code>�ĵ�����.
    * @return           <code>UpdateAdapter</code>��id.
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   int getUpdateAdapterId(String name) throws ConfigurationException;

   /**
    * ע��һ��<code>UpdateAdapter</code>.
    *
    * @param generator   ��Ҫע���<code>UpdateAdapter</code>������.
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   void registerUpdateAdapter(UpdateAdapterGenerator generator) throws ConfigurationException;

   /**
    * ����һ��<code>UpdateAdapter</code>��ע��.
    *
    * @param name       ��Ҫ����ע���<code>UpdateAdapter</code>������.
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   void deregisterUpdateAdapter(String name) throws ConfigurationException;

   /**
    * ע��һ��<code>ValuePreparerCreaterGenerator</code>.
    */
   public void registerValuePreparerGenerator(ValuePreparerCreaterGenerator generator)
         throws ConfigurationException;

   /**
    * ����һ��<code>VPGenerator</code>��ʵ��.
    * ���nameΪnull, ��ʹ��Ĭ�ϵ�ValuePreparerGenerator.
    *
    * @param name       ValuePreparerCreater������.
    * @param type       value������.
    */
   public ValuePreparerCreater createValuePreparerCreater(String name, int type)
         throws ConfigurationException;

   /**
    * ����һ��<code>VPGenerator</code>Ĭ�ϵ�ʵ��.
    *
    * @param type       value������.
    */
   public ValuePreparerCreater createValuePreparerCreater(int type)
         throws ConfigurationException;


   //----------------------------------  SearchFactory  --------------------------------------

   public static final String SEARCH_MANAGER_ATTRIBUTE_PREFIX = "search-manager.attribute.";

   /**
    * ���һ��ConditionBuilder��. ���ڹ���һ����ѯ����.
    *
    * @param name       ConditionBuilder����.
    */
   ConditionBuilder getConditionBuilder(String name) throws ConfigurationException;

   /**
    * ����һ��ConditionBuilder��. ���ڹ���һ����ѯ����.
    *
    * @param name        ConditionBuilder����.
    * @param builder     Ҫ���õ�ConditionBuilder��.
    */
   void addConditionBuilder(String name, ConditionBuilder builder) throws ConfigurationException;

   /**
    * ���һ��ConditionBuilder���б�. ��ConditionProperty�л��õĸ��б�,
    * ����ȷ���������Ŀ�ѡ�����ķ�Χ.
    *
    * @param name       ConditionBuilder�б������.
    */
   List getConditionBuilderList(String name) throws ConfigurationException;

   /**
    * ����һ��ConditionBuilder���б�.
    *
    * @param name              �б�����.
    * @param builderNames      Ҫ���õ�ConditionBuilder���б�.
    */
   void addConditionBuilderList(String name, List builderNames) throws ConfigurationException;

   SearchAdapter createSearchAdapter(String name) throws ConfigurationException;

   SearchAdapter createSearchAdapter(int id) throws ConfigurationException;

   int getSearchAdapterId(String name) throws ConfigurationException;

   void registerSearchAdapter(SearchAdapterGenerator generator)
         throws ConfigurationException;

   void deregisterSearchAdapter(String name) throws ConfigurationException;

   void registerSearchManager(SearchManagerGenerator generator)
         throws ConfigurationException;

   SearchManager createSearchManager() throws ConfigurationException;

   SearchManager.Attributes getSearchManagerAttributes()
         throws ConfigurationException;


   //----------------------------------  ModelFactory  --------------------------------------

   public static final String MODEL_NAME_TAG = "model.name.tag";

   String getModelNameTag() throws ConfigurationException;

   ModelCaller getModelCaller() throws ConfigurationException;

   void setModelCaller(ModelCaller mc)throws ConfigurationException;

   void addModelExport(String exportName, ModelExport modelExport) throws ConfigurationException;

   ModelExport getModelExport(String exportName) throws ConfigurationException;

   ModelAdapter createModelAdapter(String name) throws ConfigurationException;

   ModelAdapter createModelAdapter(int id) throws ConfigurationException;

   int getModelAdapterId(String name) throws ConfigurationException;

   void registerModelAdapter(ModelAdapterGenerator generator) throws ConfigurationException;

   void deregisterModelAdapter(String name) throws ConfigurationException;


   //----------------------------------  ViewFactory  --------------------------------------

   public static final String VIEW_GLOBAL_SETTING = "view.global.setting";

   String getViewGlobalSetting() throws ConfigurationException;

   Function getFunction(String name) throws ConfigurationException;

   void addFunction(String name, Function fun)throws ConfigurationException;

   Component getTypicalComponent(String name) throws ConfigurationException;

   void addTypicalComponent(String name, Component com)throws ConfigurationException;

   StringCoder getStringCoder() throws ConfigurationException;

   void setStringCoder(StringCoder sc)throws ConfigurationException;

   ViewAdapter createViewAdapter(String name) throws ConfigurationException;

   ViewAdapter createViewAdapter(int id) throws ConfigurationException;

   int getViewAdapterId(String name) throws ConfigurationException;

   void registerViewAdapter(ViewAdapterGenerator generator) throws ConfigurationException;

   void deregisterViewAdapter(String name) throws ConfigurationException;

   Resource getResource(String name) throws ConfigurationException;

   void addResource(String name, Resource resource)throws ConfigurationException;

}
