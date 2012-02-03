
package self.micromagic.eterna.sql.converter;

import self.micromagic.util.StringRef;

public interface ValueConverter
{
   /**
    * ���޷�ת��ʱ�Ƿ���Ҫ�׳��쳣.
    * Ĭ��Ϊ���׳��쳣.
    */
   boolean isNeedThrow();

   /**
    * �����Ƿ�Ҫ�׳��쳣, ���޷�ת����Ŀ������ʱ.
    */
   void setNeedThrow(boolean need);

   /**
    * ���Ҫת����Ŀ�����ʹ���.
    *
    * @param typeName   ���ᱻ��ΪĿ�����͵�����, �������Ҫ����Ϊnull
    */
   int getConvertType(StringRef typeName);

   /**
    * ��һ�������������ת��, ת������Ҫ�������.
    */
   Object convert(Object value);

   /**
    * ��һ���ַ�����������ת��, ת������Ҫ�������.
    */
   Object convert(String value);

   /**
    * ��һ������ת�����ַ���.
    */
   String convertToString(Object value);

   /**
    * ��һ������ת�����ַ���.
    *
    * @param changeNullToEmpty   �Ƿ�Ҫ��nullת��Ϊ���ַ���
    */
   String convertToString(Object value, boolean changeNullToEmpty);

}
