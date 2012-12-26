
package self.micromagic.cg;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import self.micromagic.util.StringAppender;
import self.micromagic.util.StringTool;
import self.micromagic.util.Utility;
import self.micromagic.util.converter.ValueConverter;
import self.micromagic.eterna.sql.ResultRow;

/**
 * 管理数组相关的自动处理代码.
 */
public class ArrayTool
{
   /**
	 * 将一个数组转换成指定类型的数组.
	 *
	 * @param arrayLevel 数组的维度等级
	 * @param cellType   需要转换成的目标类型
	 * @param array      数组对象
	 * @return  转换成的目标类型数组, 如果给出的数组对象的维度等级不正确, 则返回null
	 */
	public static Object convertArray(int arrayLevel, Class cellType, Object array)
	{
		return convertArray(arrayLevel, cellType, array, null, (Object) null);
	}

   /**
	 * 将一个数组转换成指定类型的数组.
	 *
	 * @param arrayLevel 数组的维度等级
	 * @param cellType   需要转换成的目标类型
	 * @param array      数组对象
	 * @param destArr    目标数组对象
	 * @return  转换成的目标类型数组, 如果给出的数组对象的维度等级不正确, 则返回null
	 */
	public static Object convertArray(int arrayLevel, Class cellType, Object array, Object destArr)
	{
		return convertArray(arrayLevel, cellType, array, destArr, (Object) null);
	}

   /**
	 * 将一个数组转换成指定类型的数组.
	 *
	 * @param arrayLevel 数组的维度等级
	 * @param cellType   需要转换成的目标类型
	 * @param array      数组对象
	 * @param converer   对数组元素进行类型转换的工具
	 * @return  转换成的目标类型数组, 如果给出的数组对象的维度等级不正确, 则返回null
	 */
	public static Object convertArray(int arrayLevel, Class cellType, Object array, ValueConverter converer)
	{
		return convertArray(arrayLevel, cellType, array, null, (Object) converer);
	}

   /**
	 * 将一个数组转换成指定类型的数组.
	 *
	 * @param arrayLevel 数组的维度等级
	 * @param cellType   需要转换成的目标类型
	 * @param array      数组对象
	 * @param destArr    目标数组对象
	 * @param converer   对数组元素进行类型转换的工具
	 * @return  转换成的目标类型数组, 如果给出的数组对象的维度等级不正确, 则返回null
	 */
	public static Object convertArray(int arrayLevel, Class cellType, Object array, Object destArr,
			ValueConverter converer)
	{
		return convertArray(arrayLevel, cellType, array, destArr, (Object) converer);
	}

   /**
	 * 将一个数组转换成指定类型的数组.
	 *
	 * @param arrayLevel 数组的维度等级
	 * @param cellType   需要转换成的目标类型
	 * @param array      数组对象
	 * @param destArr    目标数组对象
	 * @param beanMap    类型转换时需要的beanMap对象
	 * @return  转换成的目标类型数组, 如果给出的数组对象的维度等级不正确, 则返回null
	 */
	public static Object convertArray(int arrayLevel, Class cellType, Object array, Object destArr, BeanMap beanMap)
	{
		return convertArray(arrayLevel, cellType, array, destArr, (Object) beanMap);
	}

   /**
	 * 将一个数组转换成指定类型的数组.
	 *
	 * @param arrayLevel 数组的维度等级
	 * @param cellType   需要转换成的目标类型
	 * @param array      数组对象
	 * @param destArr    目标数组对象
	 * @param converter  类型转换器, 可以是BeanMap或ValueConverter
	 * @return  转换成的目标类型数组, 如果给出的数组对象的维度等级不正确, 则返回null
	 */
	private static Object convertArray(int arrayLevel, Class cellType, Object array, Object destArr, Object converter)
	{
		if (arrayLevel <= 0)
		{
			throw new IndexOutOfBoundsException("Error array level:" + arrayLevel + ".");
		}
		if (cellType == null)
		{
			return null;
		}
		ArrayConverter ac = null;
		Integer levelObj = Utility.createInteger(arrayLevel);
      Map acCache = (Map) arrayConverterCache.getProperty(cellType);
		if (acCache != null)
		{
			ac = (ArrayConverter) acCache.get(levelObj);
		}
      if (ac == null)
      {
			synchronized (arrayConverterCache)
			{
         	ac = getArrayConverter(levelObj, cellType);
			}
      }
      try
      {
			return ac == null ? null : destArr == null ?
					ac.convertArray(array, converter) : ac.convertArray(array, destArr, converter);
      }
      catch (Exception ex)
      {
         throw new RuntimeException(ex);
      }
	}
	private static ArrayConverter getArrayConverter(Integer arrayLevel, Class cellType)
	{
      Map acCache = (Map) arrayConverterCache.getProperty(cellType);
		if (acCache == null)
		{
			acCache = new HashMap(2);
			arrayConverterCache.setProperty(cellType, acCache);
		}
		ArrayConverter ac = (ArrayConverter) acCache.get(arrayLevel);
		if (ac == null)
		{
			String[] imports = {
				ClassGenerator.getPackageString(Map.class),
				ClassGenerator.getPackageString(ArrayTool.class),
				ClassGenerator.getPackageString(ValueConverter.class),
				ClassGenerator.getPackageString(ResultRow.class)
			};
			ClassGenerator cg = ClassGenerator.createClassGenerator("_ArrayConverter" + arrayLevel, cellType,
					ArrayConverter.class, imports);;
			cg.setClassLoader(cellType.getClassLoader());
			createConvertArrayFn(cellType, arrayLevel.intValue(), cg);
			try
			{
				ac = (ArrayConverter) cg.createClass().newInstance();
			}
			catch (Throwable ex)
			{
				if (ClassGenerator.COMPILE_LOG_TYPE > CG.COMPILE_LOG_TYPE_ERROR)
				{
					CG.log.error("Error in create PrimitiveArrayWrapper.", ex);
				}
			}
         acCache.put(arrayLevel, ac);
		}
		return ac;
	}


	/**
	 * 生成数组类型转换的处理方法.
	 */
	private static void createConvertArrayFn(Class cellType, int level, ClassGenerator cg)
	{
		// 准备代码片段的参数
		StringAppender arrVL = StringTool.createStringAppender();
		for (int i = 0; i < level; i++)
		{
			arrVL.append("[]");
		}
		String arrVLStr = arrVL.toString();
		Map tmpParam = new HashMap();
		tmpParam.put("arrayLevel", new Integer(level));
		tmpParam.put("srcType", "Object");
		tmpParam.put("destType", ClassGenerator.getClassName(cellType));
		tmpParam.put("cellType", ClassGenerator.getClassName(cellType));
		tmpParam.put("arrayObj", "array");
		tmpParam.put("destArr", "destArr");
		tmpParam.put("arrayDef", arrVLStr);
		tmpParam.put("converter", "converter");
		tmpParam.put("src", "array");
		tmpParam.put("dest", "destArr");
		StringAppender fnCode;

		String wrapName = BeanTool.getPrimitiveWrapClassName(ClassGenerator.getClassName(cellType));
		int vcIndex = BeanTool.converterManager.getConverterIndex(cellType);
		boolean beanType = BeanTool.checkBean(cellType);

		// 生成不带目标数组的转换方法
		fnCode = StringTool.createStringAppender();
		fnCode.append("private ").append(ClassGenerator.getClassName(cellType)).append(arrVLStr)
				.append(" convertArray(Object").append(arrVLStr).append(" array0, Object converter)").appendln()
            .append("      throws Exception").appendln().append('{').appendln();
      fnCode.append(ClassGenerator.getClassName(cellType)).append(arrVLStr).append(" destArr0 = new ")
				.append(ClassGenerator.getClassName(cellType)).append("[array0.length]")
				.append(arrVLStr.substring(2)).append(';').appendln();
		appendConvertArrayCode(fnCode, cellType, arrVLStr.substring(2), tmpParam, 0, level,
				wrapName, vcIndex, beanType, false);
      fnCode.append("return destArr0;").appendln().append('}');
      cg.addMethod(fnCode.toString());

		// 生成带目标数组的转换方法
		fnCode = StringTool.createStringAppender();
		fnCode.append("private ").append(ClassGenerator.getClassName(cellType)).append(arrVLStr)
				.append(" convertArray(Object").append(arrVLStr).append(" array0, ")
				.append(ClassGenerator.getClassName(cellType)).append(arrVLStr).append(" destArr0, Object converter)")
				.appendln()
            .append("      throws Exception").appendln().append('{').appendln();
		fnCode.append("if (destArr0 == null)").appendln().append('{').appendln()
				.append("return this.convertArray(array0, converter);").appendln().append('}')
				.appendln();
		fnCode.append("else if (destArr0.length < array0.length)").appendln().append('{').appendln()
				.append(ClassGenerator.getClassName(cellType)).append(arrVLStr).append(" tmpArr = new ")
				.append(ClassGenerator.getClassName(cellType)).append("[array0.length]")
				.append(arrVLStr.substring(2)).append(';').appendln()
				.append("System.arraycopy(tmpArr, 0, destArr0, 0, destArr0.length);").appendln()
				.append("destArr0 = tmpArr;").appendln().append('}').appendln();
		appendConvertArrayCode(fnCode, cellType, arrVLStr.substring(2), tmpParam, 0, level,
				wrapName, vcIndex, beanType, true);
      fnCode.append("return destArr0;").appendln().append('}');
      cg.addMethod(fnCode.toString());

		// 重设数组定义字符串
		tmpParam.put("arrayDef", arrVLStr);

		// 实现接口中不带目标数组的转换方法
		fnCode = StringTool.createStringAppender();
		fnCode.append("public Object convertArray(Object array, Object converter)").appendln()
				.append("      throws Exception").appendln().append('{').appendln();
		BeanTool.codeRes.printRes("convertArrayType", tmpParam, 0, fnCode)
				.appendln().append('}');
		cg.addMethod(fnCode.toString());

		// 实现接口中带目标数组的转换方法
		fnCode = StringTool.createStringAppender();
		fnCode.append("public Object convertArray(Object array, Object destArr, Object converter)").appendln()
				.append("      throws Exception").appendln().append('{').appendln();
		BeanTool.codeRes.printRes("convertArrayType.withDest", tmpParam, 0, fnCode)
				.appendln().append('}');
		cg.addMethod(fnCode.toString());
	}

	/**
	 * 生成数组类型转换的方法主体.
	 */
	private static void appendConvertArrayCode(StringAppender bodyCode, Class cellType, String arrVLStr, Map params,
			int levelIndex, int arrayLevel, String wrapName, int vcIndex, boolean beanType, boolean hasDest)
	{
		params.put("levelIndex", Integer.toString(levelIndex));
		params.put("nextIndex", Integer.toString(levelIndex + 1));
		BeanTool.codeRes.printRes("array2array_for", params, 0, bodyCode).appendln();
		bodyCode.append('{').appendln();
		BeanTool.codeRes.printRes("array2array_if", params, 0, bodyCode).appendln();
		bodyCode.append('{').appendln();
		if (levelIndex < arrayLevel - 1)
		{
			params.put("arrayDef", arrVLStr.substring(2));
			if (hasDest)
			{
				BeanTool.codeRes.printRes("array2array_def_withDest", params, 0, bodyCode).appendln();
			}
			else
			{
				BeanTool.codeRes.printRes("array2array_def", params, 0, bodyCode).appendln();
			}
			appendConvertArrayCode(bodyCode, cellType, arrVLStr.substring(2), params, levelIndex + 1, arrayLevel,
					wrapName, vcIndex, beanType, hasDest);
		}
		else
		{
			if (wrapName != null)
			{
				String typeName = ClassGenerator.getClassName(cellType);
      		params.put("converterType", "self.micromagic.util.converter." + wrapName + "Converter");
				params.put("vcIndex", new Integer(vcIndex));
				params.put("converterMethod",
						"convertTo" + Character.toUpperCase(typeName.charAt(0)) + typeName.substring(1));
				BeanTool.codeRes.printRes("arrayCell.convert.primitive", params, 0, bodyCode).appendln();
			}
			else if (vcIndex != -1)
			{
				params.put("vcIndex", new Integer(vcIndex));
				BeanTool.codeRes.printRes("arrayCell.convert.byTool", params, 0, bodyCode).appendln();
			}
			else if (beanType)
			{
				BeanTool.codeRes.printRes("arrayCell.convert.bean", params, 0, bodyCode).appendln();
			}
			else
			{
				BeanTool.codeRes.printRes("arrayCell.convert.other", params, 0, bodyCode).appendln();
			}
		}
		bodyCode.append('}').appendln().append('}').appendln();
	}

	/**
	 * 数组元素类型转换的缓存.
	 */
   private static ClassKeyCache arrayConverterCache = ClassKeyCache.getInstance();

   /**
	 * 将一个基本类型的数组转换成它的外覆类数组.
	 *
	 * @param arrayLevel 数组的维度等级
	 * @param array      基本类型数组对象
	 * @return  转换成的外覆类数组, 如果给出的数组对象不是基本类型数组或
	 *          数组的维度等级不正确, 则返回null
	 */
	public static Object wrapPrimitiveArray(int arrayLevel, Object array)
	{
		if (arrayLevel <= 0)
		{
			throw new IndexOutOfBoundsException("Error array level:" + arrayLevel + ".");
		}
		Integer levelObj = Utility.createInteger(arrayLevel);
      PrimitiveArrayWrapper paw = (PrimitiveArrayWrapper) pawCache.get(levelObj);
		if (paw == null)
		{
         synchronized (pawCache)
			{
            paw = getPrimitiveArrayWrapper(levelObj);
			}
		}
      return paw == null ? null : paw.doWrap(array);
	}
	private static PrimitiveArrayWrapper getPrimitiveArrayWrapper(Integer arrayLevel)
	{
		PrimitiveArrayWrapper paw = (PrimitiveArrayWrapper) pawCache.get(arrayLevel);
		if (paw == null)
		{
			StringAppender arrVL = StringTool.createStringAppender();
			for (int i = 0; i < arrayLevel.intValue(); i++)
			{
				arrVL.append("[]");
			}
			String arrVLStr = arrVL.toString();
			ClassGenerator cg = new ClassGenerator();
			cg.addClassPath(ArrayTool.class);
			cg.setClassName(ClassGenerator.getClassName(ArrayTool.class) + "_ArrayWrapper" + arrayLevel);
			cg.addInterface(PrimitiveArrayWrapper.class);
			cg.setClassLoader(ArrayTool.class.getClassLoader());
			StringAppender fnCode = StringTool.createStringAppender();
			fnCode.append("public Object doWrap(Object obj)").appendln()
					.append('{').appendln();
			Map tmpParam = new HashMap();
			tmpParam.put("arrayObj", "obj");
			tmpParam.put("arrayDef", arrVLStr);
			BeanTool.codeRes.printRes("checkAndConvertPrimitiveArrayType", tmpParam, 0, fnCode)
					.appendln().append('}');
			Iterator itr = BeanTool.primitiveWrapClass.keySet().iterator();
			while (itr.hasNext())
			{
				createPrimitiveArrayWrapFn((String) itr.next(), arrVLStr, arrayLevel.intValue(), cg);
			}
			cg.addMethod(fnCode.toString());
			try
			{
				paw = (PrimitiveArrayWrapper) cg.createClass().newInstance();
			}
			catch (Throwable ex)
			{
				if (ClassGenerator.COMPILE_LOG_TYPE > CG.COMPILE_LOG_TYPE_ERROR)
				{
					CG.log.error("Error in create PrimitiveArrayWrapper.", ex);
				}
			}
			pawCache.put(arrayLevel, paw);
		}
      return paw;
	}

	/**
	 * 生成基本类型数组转外覆类的处理方法.
	 */
	private static void createPrimitiveArrayWrapFn(String primitiveType, String arrVLStr, int level, ClassGenerator cg)
	{
		StringAppender fnCode = StringTool.createStringAppender();
		fnCode.append("private Object wrapArray(").append(primitiveType).append(arrVLStr).append(" array0)").appendln()
				.append('{').appendln();
		String wrapType = BeanTool.getPrimitiveWrapClassName(primitiveType);
		fnCode.append(wrapType).append(arrVLStr).append(" wrapArr0 = new ")
				.append(wrapType).append("[array0.length]")
				.append(arrVLStr.substring(2)).append(';').appendln();
		appendPrimitiveArrayWrapCode(fnCode, primitiveType, arrVLStr.substring(2), 0, level, wrapType);
		fnCode.appendln().append("return wrapArr0;").appendln().append('}');
		cg.addMethod(fnCode.toString());
	}

	/**
	 * 生成基本类型数组转外覆类的方法主体.
	 */
	private static void appendPrimitiveArrayWrapCode(StringAppender bodyCode, String primitiveType, String arrVLStr,
			int levelIndex, int arrayLevel, String wrapType)
	{
		Map tmpParam = new HashMap();
		tmpParam.put("levelIndex", Integer.toString(levelIndex));
		tmpParam.put("nextIndex", Integer.toString(levelIndex + 1));
		tmpParam.put("srcType", primitiveType);
		tmpParam.put("destType", wrapType);
		tmpParam.put("src", "array");
		tmpParam.put("dest", "wrapArr");
		BeanTool.codeRes.printRes("array2array_for", tmpParam, 0, bodyCode).appendln();
		bodyCode.append('{').appendln();
		if (levelIndex < arrayLevel - 1)
		{
			tmpParam.put("arrayDef", arrVLStr.substring(2));
			BeanTool.codeRes.printRes("array2array_def", tmpParam, 0, bodyCode).appendln();
			appendPrimitiveArrayWrapCode(bodyCode, primitiveType, arrVLStr.substring(2),
					levelIndex + 1, arrayLevel, wrapType);
		}
		else
		{
			tmpParam.put("wrapType", wrapType);
			BeanTool.codeRes.printRes("arrayCell.convert.wrap", tmpParam, 0, bodyCode).appendln();
		}
		bodyCode.append('}').appendln();
	}

	/**
	 * 基本类型数组外敷类包装的缓存.
	 */
	private static Map pawCache = new HashMap();

   /**
	 * 将一个bean数组转换成map数组.
	 *
	 * @param arrayLevel 数组的维度等级
	 * @param array      bean类型数组对象
	 * @return  转换成的目标类型数组, 如果给出的数组对象的维度等级不正确, 则返回null
	 */
	public static Object beanArray2Map(int arrayLevel, Object array)
	{
		if (arrayLevel <= 0)
		{
			throw new IndexOutOfBoundsException("Error array level:" + arrayLevel + ".");
		}
		Integer levelObj = Utility.createInteger(arrayLevel);
      ArrayConverter ac = (ArrayConverter) bean2MapCache.get(levelObj);
      if (ac == null)
      {
			synchronized (bean2MapCache)
			{
         	ac = getArrayConverter(levelObj);
			}
      }
      try
      {
			return ac == null ? null : ac.convertArray(array, null);
      }
      catch (Exception ex)
      {
         throw new RuntimeException(ex);
      }
	}
	private static ArrayConverter getArrayConverter(Integer arrayLevel)
	{
		ArrayConverter ac = (ArrayConverter) bean2MapCache.get(arrayLevel);
		if (ac == null)
		{
			ClassGenerator cg = new ClassGenerator();
			cg.importPackage(ClassGenerator.getPackageString(Map.class));
			cg.importPackage(ClassGenerator.getPackageString(ArrayTool.class));
			cg.addClassPath(ArrayTool.class);
			cg.setClassName(ClassGenerator.getClassName(ArrayTool.class) + "_BeanArray2Map" + arrayLevel);
			cg.addInterface(ArrayConverter.class);
			cg.setClassLoader(ArrayTool.class.getClassLoader());
			createBeanArray2MapFn(arrayLevel.intValue(), cg);
			try
			{
				ac = (ArrayConverter) cg.createClass().newInstance();
			}
			catch (Throwable ex)
			{
				if (ClassGenerator.COMPILE_LOG_TYPE > CG.COMPILE_LOG_TYPE_ERROR)
				{
					CG.log.error("Error in create PrimitiveArrayWrapper.", ex);
				}
			}
         bean2MapCache.put(arrayLevel, ac);
		}
		return ac;
	}


	/**
	 * 生成bean数组类型转换成map的处理方法.
	 */
	private static void createBeanArray2MapFn(int level, ClassGenerator cg)
	{
		// 准备代码片段的参数
		StringAppender arrVL = StringTool.createStringAppender();
		for (int i = 0; i < level; i++)
		{
			arrVL.append("[]");
		}
		String arrVLStr = arrVL.toString();
		Map tmpParam = new HashMap();
		tmpParam.put("arrayLevel", new Integer(level));
		tmpParam.put("srcType", "Object");
		tmpParam.put("destType", "Map");
		tmpParam.put("arrayObj", "array");
		tmpParam.put("destArr", "destArr");
		tmpParam.put("src", "array");
		tmpParam.put("dest", "destArr");
		StringAppender fnCode;

		// 生成不带目标数组的转换方法
		fnCode = StringTool.createStringAppender();
		fnCode.append("private Map").append(arrVLStr)
				.append(" convertArray(Object").append(arrVLStr).append(" array0, Object converter)").appendln()
            .append("      throws Exception").appendln().append('{').appendln();
      fnCode.append("Map").append(arrVLStr).append(" destArr0 = new Map[array0.length]")
				.append(arrVLStr.substring(2)).append(';').appendln();
		appendBeanArray2MapCode(fnCode, arrVLStr.substring(2), tmpParam, 0, level);
      fnCode.append("return destArr0;").appendln().append('}');
      cg.addMethod(fnCode.toString());

		// 实现接口中不带目标数组的转换方法
		fnCode = StringTool.createStringAppender();
		fnCode.append("public Object convertArray(Object array, Object converter)").appendln()
				.append("      throws Exception").appendln().append('{').appendln()
				.append("return this.convertArray((Object").append(arrVLStr).append(") array, converter);")
				.appendln().append('}');
		cg.addMethod(fnCode.toString());

		// 实现接口中带目标数组的转换方法
		fnCode = StringTool.createStringAppender();
		fnCode.append("public Object convertArray(Object array, Object destArr, Object converter)").appendln()
				.append("      throws Exception").appendln().append('{').appendln()
				.append("return null;").appendln().append('}');
		cg.addMethod(fnCode.toString());
	}

	/**
	 * 生成bean数组类型转换成map的方法主体.
	 */
	private static void appendBeanArray2MapCode(StringAppender bodyCode, String arrVLStr, Map params,
			int levelIndex, int arrayLevel)
	{
		params.put("levelIndex", Integer.toString(levelIndex));
		params.put("nextIndex", Integer.toString(levelIndex + 1));
		BeanTool.codeRes.printRes("array2array_for", params, 0, bodyCode).appendln();
		bodyCode.append('{').appendln();
		BeanTool.codeRes.printRes("array2array_if", params, 0, bodyCode).appendln();
		bodyCode.append('{').appendln();
		if (levelIndex < arrayLevel - 1)
		{
			params.put("arrayDef", arrVLStr.substring(2));
			BeanTool.codeRes.printRes("array2array_def", params, 0, bodyCode).appendln();
			appendBeanArray2MapCode(bodyCode, arrVLStr.substring(2), params, levelIndex + 1, arrayLevel);
		}
		else
		{
			BeanTool.codeRes.printRes("arrayCell.convert.map", params, 0, bodyCode).appendln();
		}
		bodyCode.append('}').appendln().append('}').appendln();
	}

	/**
	 * bean类型数组转map的缓存.
	 */
	private static Map bean2MapCache = new HashMap();

}
