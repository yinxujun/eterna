
package self.micromagic.util.converter;

import java.beans.PropertyEditor;

import self.micromagic.util.StringRef;

/**
 * ֵת����.
 */
public interface ValueConverter extends Cloneable
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
    * ���<code>PropertyEditor</code>����, ֵת������ʹ����������ת��.
    */
   PropertyEditor getPropertyEditor();

   /**
    * ����<code>PropertyEditor</code>����, ֵת������ʹ����������ת��.
    */
   void setPropertyEditor(PropertyEditor propertyEditor);

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

   Object clone();

}
