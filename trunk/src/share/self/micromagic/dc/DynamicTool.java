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

package self.micromagic.dc;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import self.micromagic.util.Utility;
import self.micromagic.util.ResManager;
import self.micromagic.util.StringAppender;
import self.micromagic.util.StringTool;
import self.micromagic.cg.ClassKeyCache;
import self.micromagic.cg.ClassGenerator;
import self.micromagic.cg.BeanTool;

/**
 * 动态代码生成的工具.
 * 如生成MethodProxy等.
 */
public class DynamicTool
{
	/**
	 * 创建一个方法调用的代理.
	 *
	 * @param method      需要创建方法调用代理的目标方法
	 * @return  方法调用的代理
	 */
   public static MethodProxy createMethodProxy(Method method)
			throws DynamicCodeException
	{
		return createMethodProxy(method, true);
	}

	/**
	 * 创建一个方法调用的代理.
	 *
	 * @param method      需要创建方法调用代理的目标方法
	 * @param paramCheck  是否需要检查参数类型
	 * @return  方法调用的代理
	 */
   public static MethodProxy createMethodProxy(Method method, boolean paramCheck)
			throws DynamicCodeException
	{
		if (!Modifier.isPublic(method.getModifiers()))
		{
			throw new DynamicCodeException("The method [" + method + "] isn't public.");
		}
      Class c = method.getDeclaringClass();
		if (!Modifier.isPublic(c.getModifiers()))
		{
			throw new DynamicCodeException("The method [" + method + "]'s declaring type [" + c + "] isn't public.");
		}
		Class[] params = method.getParameterTypes();
		for (int i = 0; i < params.length; i++)
		{
			if (!Modifier.isPublic(params[i].getModifiers()))
			{
				throw new DynamicCodeException("The method [" + method + "]'s param(" + i + ") [" + c + "] isn't public.");
			}
		}
      MethodProxy proxy = getCachedMethodProxy(c, method, paramCheck);
		if (proxy == null)
		{
			synchronized (methodProxyCache)
			{
				proxy = createMethodProxy0(c, method, paramCheck);
			}
		}
		return proxy;
	}
	private static MethodProxy createMethodProxy0(Class c, Method method, boolean paramCheck)
	{
		MethodProxy proxy = getCachedMethodProxy(c, method, paramCheck);
		if (proxy != null)
		{
			return proxy;
		}
		boolean staticMethod = Modifier.isStatic(method.getModifiers());
		Class[] params = method.getParameterTypes();

      ClassGenerator cg = new ClassGenerator();
		cg.setClassLoader(c.getClassLoader());
		cg.setClassName(c.getName() + "$$" + method.getName() + "$Proxy" + METHOD_PROXY_ID++);
		cg.addInterface(MethodProxy.class);
		cg.addClassPath(c);
		cg.addClassPath(MethodProxy.class);
		StringAppender buf = StringTool.createStringAppender(128);
		codeRes.printRes("methodProxy.invoke.declare", null, 0, buf).appendln().append('{');

		// 生成参数的转换
		String paramCode, paramCodePrimitive;
		Map paramCodeParams = new HashMap();
		if (paramCheck)
		{
			if (!staticMethod)
			{
				paramCodeParams.put("type", ClassGenerator.getClassName(c));
				codeRes.printRes("methodProxy.check.target", paramCodeParams, 1, buf).appendln();
			}
			if (params.length > 0)
			{
				paramCodeParams.put("paramCount", Integer.toString(params.length));
				codeRes.printRes("methodProxy.check.args", paramCodeParams, 1, buf).appendln();
			}
			paramCode = "methodProxy.param.cast.withCheck";
			paramCodePrimitive = "methodProxy.param.cast.primitive.withCheck";
		}
		else
		{
			paramCode = "methodProxy.param.cast.withDeclare";
			paramCodePrimitive = "methodProxy.param.cast.primitive.withDeclare";
		}
		for (int i = 0; i < params.length; i++)
		{
			Class type = params[i];
			paramCodeParams.put("type", ClassGenerator.getClassName(type));
			paramCodeParams.put("index", Integer.toString(i));
			if (type.isPrimitive())
			{
				paramCodeParams.put("wrapType", BeanTool.getPrimitiveWrapClassName(type.getName()));
				codeRes.printRes(paramCodePrimitive, paramCodeParams, 1, buf).appendln();
			}
			else
			{
				codeRes.printRes(paramCode, paramCodeParams, 1, buf).appendln();
			}
		}

		// 生成调用及返回的代码
		Class rType = method.getReturnType();
		Map returnCodeParams = new HashMap();
		if (staticMethod)
		{
         returnCodeParams.put("target", ClassGenerator.getClassName(c));
		}
		else
		{
         returnCodeParams.put("target", "((" + ClassGenerator.getClassName(c) + ") target)");
		}
		returnCodeParams.put("method", method.getName());
		StringAppender paramsBuf = StringTool.createStringAppender(params.length * 8);
		for (int i = 0; i < params.length; i++)
		{
			if (i > 0)
			{
				paramsBuf.append(", ");
			}
			paramsBuf.append("param").append(i);
		}
		returnCodeParams.put("params", paramsBuf.toString());
		if (rType == void.class)
		{
			codeRes.printRes("methodProxy.doInvoke.void", returnCodeParams, 1, buf).appendln();
		}
		else if (rType.isPrimitive())
		{
			returnCodeParams.put("wrapType", BeanTool.getPrimitiveWrapClassName(rType.getName()));
			codeRes.printRes("methodProxy.doInvoke.primitive", returnCodeParams, 1, buf).appendln();
		}
		else
		{
			codeRes.printRes("methodProxy.doInvoke", returnCodeParams, 1, buf).appendln();
		}

		buf.append('}');
		cg.addMethod(buf.toString());
		try
		{
			proxy = (MethodProxy) cg.createClass().newInstance();
			putMethodProxy(c, method, paramCheck, proxy);
		}
		catch (Exception ex)
		{
			throw new DynamicCodeException(ex);
		}
		return proxy;
	}
	private static int METHOD_PROXY_ID = 1;

	/**
	 * 获取缓存的方法调用代理.
	 */
	private static MethodProxy getCachedMethodProxy(Class c, Method method, boolean paramCheck)
	{
      Map methodCache = (Map) methodProxyCache.getProperty(c);
		if (methodCache == null)
		{
			return null;
		}
      MethodProxyKey key = new MethodProxyKey(method.getName(), paramCheck, method.getParameterTypes());
		return (MethodProxy) methodCache.get(key);
	}

	/**
	 * 将方法调用代理放入缓存.
	 */
	private static void putMethodProxy(Class c, Method method, boolean paramCheck, MethodProxy proxy)
	{
      Map methodCache = (Map) methodProxyCache.getProperty(c);
		if (methodCache == null)
		{
			methodCache = new HashMap();
			methodProxyCache.setProperty(c, methodCache);
		}
      MethodProxyKey key = new MethodProxyKey(method.getName(), paramCheck, method.getParameterTypes());
		methodCache.put(key, proxy);
	}

	/**
	 * 存放方法调用代理的缓存.
	 */
	private static ClassKeyCache methodProxyCache = ClassKeyCache.getInstance();

	/**
	 * 方法调用代理存放的键值.
	 */
   static class MethodProxyKey
	{
      private String methodName;
      private boolean paramCheck;
		private Class[] params;
		public MethodProxyKey(String methodName, boolean paramCheck, Class[] params)
		{
			this.methodName = methodName;
			this.paramCheck = paramCheck;
			this.params = params;
		}

		public boolean equals(Object obj)
		{
			if (obj instanceof MethodProxyKey)
			{
				MethodProxyKey key = (MethodProxyKey) obj;
				if (this.methodName.equals(key.methodName) && this.paramCheck == key.paramCheck)
				{
               Class[] params1 = this.params;
               Class[] params2 = key.params;
					if (params1.length == params2.length)
					{
						for (int i = 0; i < params1.length; i++)
						{
							if (params1[i] != params2[i])
							{
								return false;
							}
						}
						return true;
					}
				}
			}
			return false;
		}

		public int hashCode()
		{
			return this.methodName.hashCode() ^ this.params.length ^ (this.paramCheck ? 1231 : 1237);
		}

	}

	/**
	 * 用于记录日志.
	 */
	static final Log log = Utility.createLog("eterna.dc");

	/**
	 * 设置动态代码生成时, 对代码编译的类型.
	 */
	public static final String COMPILE_TYPE_PROPERTY = "self.micromagic.dc.compile.type";

	/**
	 * 动态代码生成时, 对代码编译的类型.
	 */
	static String DC_COMPILE_TYPE = null;

	/**
	 * 代码段资源.
	 */
	static ResManager codeRes = new ResManager();

	/**
	 * 初始化代码资源及各种类型对应的转换器.
	 */
	static
	{
		try
		{
			codeRes.load(DynamicTool.class.getResourceAsStream("DynamicTool.res"));
		}
		catch (Exception ex)
		{
			log.error("Error in get code res.", ex);
		}
		try
		{
			Utility.addFieldPropertyManager(COMPILE_TYPE_PROPERTY, CodeClassTool.class, "DC_COMPILE_TYPE");
		}
		catch (Throwable ex) {}
	}

}