
package self.micromagic.eterna.share;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import self.micromagic.util.Utility;

public class TypeManager
{
   public final static int TYPE_IGNORE = 0;
   public final static int TYPE_STRING = 1;
   public final static int TYPE_INTEGER = 2;
   public final static int TYPE_DOUBLE = 3;
   public final static int TYPE_BYTES = 4;
   public final static int TYPE_BOOLEAN = 5;
   public final static int TYPE_DATE = 6;
   public final static int TYPE_TIMPSTAMP = 7;
   public final static int TYPE_LONG = 8;
   public final static int TYPE_TIME = 9;
   public final static int TYPE_SHORT = 10;
   public final static int TYPE_BYTE = 11;
   public final static int TYPE_FLOAT = 12;
   public final static int TYPE_OBJECT = 13;
   public final static int TYPE_BIGSTRING = 14;
   public final static int TYPE_STREAM = 15;
   public final static int TYPE_READER = 16;
   public final static int TYPE_DECIMAL = 17;

   public final static int TYPES_COUNT = 18;

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
      paramMap.put("Datetime", Utility.INTEGER_7); // ºÊ»›Datetimeµƒ…Ë÷√
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
   }

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

   public static int getPureType(int id)
   {
      int realId = id & 0xff;
      if (realId < 0 || realId >= paramNames.length)
      {
         return TYPE_IGNORE;
      }
      return realId;
   }

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

   public static int getSQLType(int id)
   {
      int realId = id & 0xff;
      if (realId < 0 || realId >= paramNames.length)
      {
         return Types.NULL;
      }
      return SQL_TYPES[realId];
   }

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

   public static boolean isTypeDate(int id)
   {
      int realId = id & 0xff;
      if (realId < 0 || realId >= paramNames.length)
      {
         return false;
      }
      return realId == TYPE_DATE || realId == TYPE_TIME || realId == TYPE_TIMPSTAMP;
   }

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
