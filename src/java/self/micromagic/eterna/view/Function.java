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

package self.micromagic.eterna.view;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.EternaFactory;

public interface Function
{
	public static final String ETERNA_FUNCTION_BEGIN = "{$eternaFunction:";
	public static final String ETERNA_FUNCTION_END = "}";

	String getName() throws ConfigurationException;

	String getParam() throws ConfigurationException;

	String getBody() throws ConfigurationException;

	EternaFactory getFactory() throws ConfigurationException;

	ViewAdapter.ViewRes getViewRes() throws ConfigurationException;

}