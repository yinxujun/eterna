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

package self.micromagic.eterna.model;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.AdapterGenerator;

public interface ModelAdapterGenerator extends AdapterGenerator
{
	void setName(String name) throws ConfigurationException;

	void setKeepCaches(boolean keep) throws ConfigurationException;

	void setNeedFrontModel(boolean needFrontModel) throws ConfigurationException;

	void setFrontModelName(String frontModelName) throws ConfigurationException;

	void setModelExportName(String name) throws ConfigurationException;

	void setErrorExportName(String name) throws ConfigurationException;

	void addExecute(Execute execute) throws ConfigurationException;

	void setTransactionType(String tType) throws ConfigurationException;

	void setDataSourceName(String dsName) throws ConfigurationException;

	void setAllowPosition(String positions) throws ConfigurationException;

	ModelAdapter createModelAdapter() throws ConfigurationException;

}