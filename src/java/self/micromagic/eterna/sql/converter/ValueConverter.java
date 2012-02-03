
package self.micromagic.eterna.sql.converter;

import self.micromagic.util.StringRef;

public interface ValueConverter
{
   /**
    * 在无法转换时是否需要抛出异常.
    * 默认为不抛出异常.
    */
   boolean isNeedThrow();

   /**
    * 设置是否要抛出异常, 当无法转换成目标类型时.
    */
   void setNeedThrow(boolean need);

   /**
    * 获得要转换的目标类型代码.
    *
    * @param typeName   将会被设为目标类型的名称, 如果不需要可设为null
    */
   int getConvertType(StringRef typeName);

   /**
    * 对一个对象进行类型转换, 转换成所要求的类型.
    */
   Object convert(Object value);

   /**
    * 对一个字符串进行类型转换, 转换成所要求的类型.
    */
   Object convert(String value);

   /**
    * 将一个对象转换成字符串.
    */
   String convertToString(Object value);

   /**
    * 将一个对象转换成字符串.
    *
    * @param changeNullToEmpty   是否要将null转换为空字符串
    */
   String convertToString(Object value, boolean changeNullToEmpty);

}
