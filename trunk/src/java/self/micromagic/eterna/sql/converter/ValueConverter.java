
package self.micromagic.eterna.sql.converter;

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
