
package self.micromagic.eterna.share;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;

import self.micromagic.util.Utility;
import self.micromagic.util.converter.ValueConverter;
import self.micromagic.util.converter.ConverterFinder;

public class TypeManager
{
   public static final int TYPE_IGNORE = 0;
   public static final int TYPE_STRING = 1;
   public static final int TYPE_INTEGER = 2;
   public static final int TYPE_DOUBLE = 3;
   public static final int TYPE_BYTES = 4;
   public static final int TYPE_BOOLEAN = 5;
   public static final int TYPE_DATE = 6;
   public static final int TYPE_TIMPSTAMP = 7;
   public static final int TYPE_LONG = 8;
   public static final int TYPE_TIME = 9;
   public static final int TYPE_SHORT = 10;
   public static final int TYPE_BYTE = 11;
   public static final int TYPE_FLOAT = 12;
   public static final int TYPE_OBJECT = 13;
   public static final int TYPE_BIGSTRING = 14;
   public static final int TYPE_STREAM = 15;
   public static final int TYPE_READER = 16;
   public static final int TYPE_DECIMAL = 17;

   public static final int TYPES_COUNT = 18;

   private static Map paramMap = new HashMap();

   private static int[] SQL_TYPES = new int[]{
      Types.NULL,           //TYPE_IGNORE
      Types.VARCHAR,        //TYPE_STRING
      Types.INTEGER,        //TYPE_INTEGER
      Types.DOUBLE,         //TYPE_DOUBLE
      Types.BINARY,         //TYPE_BYTES
      Types.BIT,            //TYPE_BOOLEAN
      Types.DATE,           //TYPE_DATE
      Types.TIMESTAMP,      //TYPE_TIMPSTAMP
      Types.BIGINT,         //TYPE_LONG
      Types.TIME,           //TYPE_TIME
      Types.SMALLINT,       //TYPE_SHORT
      Types.TINYINT,        //TYPE_BYTE
      Types.FLOAT,          //TYPE_FLOAT
      Types.OTHER,          //TYPE_OBJECT
      Types.LONGVARCHAR,    //TYPE_BIGSTRING
      Types.LONGVARBINARY,  //TYPE_STREAM
      Types.LONGVARCHAR,    //TYPE_READER
      Types.DECIMAL         //TYPE_DECIMAL
   };

   private static String[] paramNames = {
      "ignore",
      "String",
      "int",
      "double",
      "Bytes",
      "boolean",
      "Date",
      "Timestamp",
      "long",
      "Time",
      "short",
      "byte",
      "float",
      "Object",
      "BigString",
      "Stream",
      "Reader",
      "Decimal"
   };

   private static ValueConverter[] converters;
   private static Class[] javaTypes = {
      null,
      String.class,
      int.class,
      double.class,
      byte[].class,
      boolean.class,
      java.sql.Date.class,
      java.sql.Timestamp.class,
      long.class,
      java.sql.Time.class,
      short.class,
      byte.class,
      float.class,
      Object.class,
      String.class,
      InputStream.class,
      Reader.class,
      BigDecimal.class
   };

   static
   {
      paramMap.put("ignore", Utility.INTEGER_0);
      paramMap.put("String", Utility.INTEGER_1);
      paramMap.put("int", Utility.INTEGER_2);
      paramMap.put("double", Utility.INTEGER_3);
      paramMap.put("Bytes", Utility.INTEGER_4);
      paramMap.put("boolean", Utility.INTEGER_5);
      paramMap.put("Date", Utility.INTEGER_6);
      paramMap.put("Timestamp", Utility.INTEGER_7);
      paramMap.put("Datetime", Utility.INTEGER_7); // 兼容Datetime的设置
      paramMap.put("long", Utility.INTEGER_8);
      paramMap.put("Time", Utility.INTEGER_9);
      paramMap.put("short", Utility.INTEGER_10);
      paramMap.put("byte", Utility.INTEGER_11);
      paramMap.put("float", Utility.INTEGER_12);
      paramMap.put("Object", Utility.INTEGER_13);
      paramMap.put("BigString", Utility.INTEGER_14);
      paramMap.put("Stream", Utility.INTEGER_15);
      paramMap.put("Reader", new Integer(16));
      paramMap.put("Decimal", new Integer(17));

		converters = new ValueConverter[javaTypes.length];
		for (int i = 0; i < javaTypes.length; i++)
		{
			Class type = javaTypes[i];
			if (type != null)
			{
				converters[i] = ConverterFinder.findConverter(type, true);
				converters[i].setNeedThrow(false);
			}
		}
   }

	/**
	 * 根据类型的名称获得类型的id.
	 *
	 * @param name   类型的名称
	 * @return    与类型名称对应的id, 如果该类型名称没有对应的类型id则
	 *            返回TYPE_IGNORE
	 */
   public static int getTypeId(String name)
   {
      if (name == null)
      {
         return TYPE_IGNORE;
      }
      int param = 0;
      name = name.trim();
      if (name.endsWith(")"))
      {
         int index = name.lastIndexOf('(');
         String temp = name.substring(index + 1, name.length() - 1);
         name = name.substring(0, index);
         index = temp.indexOf(',');
         try
         {
            if (index == -1)
            {
               param = Integer.parseInt(temp.trim()) << 8;
            }
            else
            {
               param = (Integer.parseInt(temp.substring(0, index).trim()) << 8)
                     | (Integer.parseInt(temp.substring(index + 1).trim()) << 24);
            }
         }
         catch (Exception ex){}
      }
      Integer i = (Integer) paramMap.get(name);
      return i == null ? TYPE_IGNORE : i.intValue() | param;
   }

	/**
	 * 获取纯类型id.
	 * 纯类型id保存在类型id的低8位, 有效类型id还包括其它参数,
	 * 如: 长度 精度等.
	 *
	 * @param id  类型的id
	 * @return   纯类型id, 如果给出的类型id无效则返回TYPE_IGNORE
	 */
   public static int getPureType(int id)
   {
      int realId = id & 0xff;
      if (realId < 0 || realId >= paramNames.length)
      {
         return TYPE_IGNORE;
      }
      return realId;
   }

	/**
	 * 根据类型id获取类型的名称.
	 *
	 * @param id  类型的id
	 * @return  与此类型id对应的类型名称, 如果给出的类型id无效
	 *          则返回null
	 */
   public static String getTypeName(int id)
   {
      int realId = id & 0xff;
      if (realId < 0 || realId >= paramNames.length)
      {
         return null;
      }
      String temp = paramNames[realId];
      if (id > 0xffffff || id < 0)
      {
         temp += "(" + ((id & 0xffff00) >> 8) + "," + ((id & 0xff000000) >>> 24) + ")";
      }
      else if (id > 0xff)
      {
         temp += "(" + ((id & 0xffff00) >> 8) + ")";
      }
      return temp;
   }

	/**
	 * 获取类型id对应的SQL的类型.
	 *
	 * @param id  类型的id
	 * @return  SQL的类型, 如果给出的类型id无效则返回Types.NULL
	 */
   public static int getSQLType(int id)
   {
      int realId = id & 0xff;
      if (realId < 0 || realId >= paramNames.length)
      {
         return Types.NULL;
      }
      return SQL_TYPES[realId];
   }

	/**
	 * 获取类型id对应的Java的类型.
	 *
	 * @param id  类型的id
	 * @return  Java的类型, 如果给出的类型id无效则返回null
	 */
   public static Class getJavaType(int id)
   {
      int realId = id & 0xff;
      if (realId < 0 || realId >= javaTypes.length)
      {
         return null;
      }
      return javaTypes[realId];
   }

	/**
	 * 获取类型id对应的类型转换器.
	 *
	 * @param id  类型的id
	 * @return  类型转换器, 如果给出的类型id无效则返回null
	 */
   public static ValueConverter getConverter(int id)
   {
      int realId = id & 0xff;
      if (realId < 0 || realId >= converters.length)
      {
         return null;
      }
      return converters[realId];
   }

	/**
	 * 判断给出的类型id是否是一个数字类型.
	 *
	 * @param id  类型的id
	 * @return  true表示此类型id为一个数字类型
	 */
   public static boolean isTypeNumber(int id)
   {
      int realId = id & 0xff;
      if (realId < 0 || realId >= paramNames.length)
      {
         return false;
      }
      return realId == TYPE_INTEGER || realId == TYPE_DOUBLE || realId == TYPE_LONG
            || realId == TYPE_SHORT || realId == TYPE_BYTE || realId == TYPE_FLOAT
            || realId == TYPE_DECIMAL;
   }

	/**
	 * 判断给出的类型id是否是一个日期类型.
	 *
	 * @param id  类型的id
	 * @return  true表示此类型id为一个日期类型
	 */
   public static boolean isTypeDate(int id)
   {
      int realId = id & 0xff;
      if (realId < 0 || realId >= paramNames.length)
      {
         return false;
      }
      return realId == TYPE_DATE || realId == TYPE_TIME || realId == TYPE_TIMPSTAMP;
   }

	/**
	 * 判断给出的类型id是否是一个字符串类型.
	 *
	 * @param id  类型的id
	 * @return  true表示此类型id为一个字符串类型
	 */
   public static boolean isTypeString(int id)
   {
      int realId = id & 0xff;
      if (realId < 0 || realId >= paramNames.length)
      {
         return false;
      }
      return realId == TYPE_STRING || realId == TYPE_BIGSTRING;
   }

}
