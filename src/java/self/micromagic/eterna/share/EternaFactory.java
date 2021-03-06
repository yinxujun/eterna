/*
 * Copyright 2009-2015 xinjunli (micromagic@sina.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
import self.micromagic.eterna.view.DataPrinter;
import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.model.ModelExport;
import self.micromagic.eterna.model.ModelAdapter;
import self.micromagic.eterna.model.ModelAdapterGenerator;
import self.micromagic.eterna.model.ModelCaller;
import self.micromagic.eterna.security.UserManager;

public interface EternaFactory extends Factory
{

	/**
	 * 获得本factory的父factory.
	 */
	EternaFactory getShareFactory() throws ConfigurationException;

	/**
	 * 获得一个UserManager对象.
	 *
	 * @return  如果UserManager对象未设置的话, 则返回null.
	 */
	UserManager getUserManager() throws ConfigurationException;

	/**
	 * 设置一个UserManager对象.
	 */
	void setUserManager(UserManager um) throws ConfigurationException;

	/**
	 * 获得一个DataSourceManager对象.
	 *
	 * @return  如果DataSourceManager对象未设置的话, 则返回null.
	 */
	DataSourceManager getDataSourceManager() throws ConfigurationException;

	/**
	 * 设置一个UserManager对象.
	 */
	void setDataSourceManager(DataSourceManager dsm) throws ConfigurationException;


	//----------------------------------  SQLFactory  --------------------------------------

	/**
	 * 获得一个常量的值.
	 *
	 * @param name       常量的名称.
	 */
	String getConstantValue(String name) throws ConfigurationException;

	/**
	 * 设置一个常量的值.
	 *
	 * @param name       常量的名称.
	 * @param value      常量的值.
	 */
	void addConstantValue(String name, String value) throws ConfigurationException;

	/**
	 * 获得日志记录器<code>SpecialLog</code>.
	 */
	SpecialLog getSpecialLog() throws ConfigurationException;

	/**
	 * 设置日志记录器<code>SpecialLog</code>.
	 */
	void setSpecialLog(SpecialLog sl)throws ConfigurationException;

	/**
	 * 获得一个ResultFormat类. 用于格式化查询的结果.
	 *
	 * @param name       format名称.
	 */
	ResultFormat getFormat(String name) throws ConfigurationException;

	/**
	 * 设置一个ResultFormat类. 用于格式化查询的结果.
	 *
	 * @param name       format名称.
	 * @param format     要设置的ResultFormat类.
	 */
	void addFormat(String name, ResultFormat format) throws ConfigurationException;

	/**
	 * 获得一个ResultReaderManager类. 用于管理查询显示的列.
	 *
	 * @param name       ResultReaderManager名称.
	 */
	ResultReaderManager getReaderManager(String name) throws ConfigurationException;

	/**
	 * 设置一个ResultReaderManager类. 用于管理查询显示的列.
	 *
	 * @param name        ResultReaderManager名称.
	 * @param manager     要设置的ResultReaderManager实例.
	 */
	void addReaderManager(String name, ResultReaderManager manager) throws ConfigurationException;

	/**
	 * 获得一个SQLParameterGroup类.
	 *
	 * @param name       SQLParameterGroup名称.
	 */
	SQLParameterGroup getParameterGroup(String name) throws ConfigurationException;

	/**
	 * 设置一个SQLParameterGroup类.
	 *
	 * @param name        SQLParameterGroup名称.
	 * @param group       要设置的SQLParameterGroup实例.
	 */
	void addParameterGroup(String name, SQLParameterGroup group) throws ConfigurationException;

	/**
	 * 生成一个<code>QueryAdapter</code>的实例.
	 *
	 * @param name       <code>QueryAdapter</code>的名称.
	 * @return           <code>QueryAdapter</code>的实例.
	 * @throws ConfigurationException     当相关配置出错时.
	 */
	QueryAdapter createQueryAdapter(String name) throws ConfigurationException;

	/**
	 * 生成一个<code>QueryAdapter</code>的实例.
	 *
	 * @param id         <code>QueryAdapter</code>的id.
	 * @return           <code>QueryAdapter</code>的实例.
	 * @throws ConfigurationException     当相关配置出错时.
	 */
	QueryAdapter createQueryAdapter(int id) throws ConfigurationException;

	/**
	 * 通过<code>QueryAdapter</code>的名称获得它的id.
	 *
	 * @param name       <code>QueryAdapter</code>的的名称.
	 * @return           <code>QueryAdapter</code>的id.
	 * @throws ConfigurationException     当相关配置出错时.
	 */
	int getQueryAdapterId(String name) throws ConfigurationException;

	/**
	 * 注册一个<code>QueryAdapter</code>.
	 *
	 * @param generator   需要注册的<code>QueryAdapter</code>的样本.
	 * @throws ConfigurationException     当相关配置出错时.
	 */
	void registerQueryAdapter(QueryAdapterGenerator generator) throws ConfigurationException;;

	/**
	 * 撤销一个<code>QueryAdapter</code>的注册.
	 *
	 * @param name       需要撤销注册的<code>QueryAdapter</code>的名称.
	 * @throws ConfigurationException     当相关配置出错时.
	 */
	void deregisterQueryAdapter(String name) throws ConfigurationException;

	/**
	 * 生成一个<code>UpdateAdapter</code>的实例.
	 *
	 * @param name       <code>UpdateAdapter</code>的名称.
	 * @return           <code>UpdateAdapter</code>的实例.
	 * @throws ConfigurationException     当相关配置出错时.
	 */
	UpdateAdapter createUpdateAdapter(String name) throws ConfigurationException;

	/**
	 * 生成一个<code>UpdateAdapter</code>的实例.
	 *
	 * @param id         <code>UpdateAdapter</code>的id.
	 * @return           <code>UpdateAdapter</code>的实例.
	 * @throws ConfigurationException     当相关配置出错时.
	 */
	UpdateAdapter createUpdateAdapter(int id) throws ConfigurationException;

	/**
	 * 通过<code>UpdateAdapter</code>的名称获得它的id.
	 *
	 * @param name       <code>UpdateAdapter</code>的的名称.
	 * @return           <code>UpdateAdapter</code>的id.
	 * @throws ConfigurationException     当相关配置出错时.
	 */
	int getUpdateAdapterId(String name) throws ConfigurationException;

	/**
	 * 注册一个<code>UpdateAdapter</code>.
	 *
	 * @param generator   需要注册的<code>UpdateAdapter</code>的样本.
	 * @throws ConfigurationException     当相关配置出错时.
	 */
	void registerUpdateAdapter(UpdateAdapterGenerator generator) throws ConfigurationException;

	/**
	 * 撤销一个<code>UpdateAdapter</code>的注册.
	 *
	 * @param name       需要撤销注册的<code>UpdateAdapter</code>的名称.
	 * @throws ConfigurationException     当相关配置出错时.
	 */
	void deregisterUpdateAdapter(String name) throws ConfigurationException;

	/**
	 * 注册一个<code>ValuePreparerCreaterGenerator</code>.
	 */
	void registerValuePreparerGenerator(ValuePreparerCreaterGenerator generator)
			throws ConfigurationException;

	/**
	 * 获得一个默认的<code>ValuePreparerCreaterGenerator</code>.
	 */
	ValuePreparerCreaterGenerator getDefaultValuePreparerCreaterGenerator();

	/**
	 * 获得一个指定名称<code>ValuePreparerCreaterGenerator</code>.
	 *
	 * @param name       ValuePreparerCreaterGenerator的名称.
	 */
	ValuePreparerCreaterGenerator getValuePreparerCreaterGenerator(String name)
			throws ConfigurationException;

	/**
	 * 生成一个<code>VPGenerator</code>的实例.
	 * 如果name为null, 则使用默认的ValuePreparerGenerator.
	 *
	 * @param name       ValuePreparerCreater的名称.
	 * @param type       value的类型.
	 */
	ValuePreparerCreater createValuePreparerCreater(String name, int type)
			throws ConfigurationException;

	/**
	 * 生成一个<code>VPGenerator</code>默认的实例.
	 *
	 * @param type       value的类型.
	 */
	ValuePreparerCreater createValuePreparerCreater(int type)
			throws ConfigurationException;


	//----------------------------------  SearchFactory  --------------------------------------

	public static final String SEARCH_MANAGER_ATTRIBUTE_PREFIX = "search-manager.attribute.";

	/**
	 * 获得一个ConditionBuilder类. 用于构成一个查询条件.
	 *
	 * @param name       ConditionBuilder名称.
	 */
	ConditionBuilder getConditionBuilder(String name) throws ConfigurationException;

	/**
	 * 设置一个ConditionBuilder类. 用于构成一个查询条件.
	 *
	 * @param name        ConditionBuilder名称.
	 * @param builder     要设置的ConditionBuilder类.
	 */
	void addConditionBuilder(String name, ConditionBuilder builder) throws ConfigurationException;

	/**
	 * 获得一个ConditionBuilder的列表. 在ConditionProperty中会用的该列表,
	 * 用于确定该条件的可选操作的范围.
	 *
	 * @param name       ConditionBuilder列表的名称.
	 */
	List getConditionBuilderList(String name) throws ConfigurationException;

	/**
	 * 设置一个ConditionBuilder的列表.
	 *
	 * @param name              列表名称.
	 * @param builderNames      要设置的ConditionBuilder的列表.
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

	/**
	 * 在factory中设置存放model名称的标签的属性名称.
	 */
	public static final String MODEL_NAME_TAG_FLAG = "model.name.tag";

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

	/**
	 * 在factory中设置视图全局配置的属性名称.
	 */
	public static final String VIEW_GLOBAL_SETTING_FLAG = "view.global.setting";

	String getViewGlobalSetting() throws ConfigurationException;

	/**
	 * 获得一个数据集输出器.
	 *
	 * @param name       数据集输出器的名称.
	 */
	DataPrinter getDataPrinter(String name) throws ConfigurationException;

	/**
	 * 设置一个数据集输出器.
	 *
	 * @param name            数据集输出器的名称.
	 * @param dataPrinter     要设置的数据集输出器.
	 */
	void addDataPrinter(String name, DataPrinter dataPrinter) throws ConfigurationException;

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