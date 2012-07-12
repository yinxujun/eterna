
package self.micromagic.cg;

import java.beans.PropertyEditor;
import java.util.HashMap;

import self.micromagic.util.converter.ObjectConverter;
import self.micromagic.util.converter.ValueConverter;
import self.micromagic.util.Utility;

/**
 * ��������ʱ��ת����������.
 */
class ConverterManager
      implements Cloneable
{
   private ValueConverter[] converters = new ValueConverter[16];
   private int usedCount;
   private HashMap converterIndexMap = new HashMap();

   /**
    * ����ת����������ֵ��ȡ��Ӧ��ת����.
    *
    * @param index  ת����������ֵ
    */
   ValueConverter getConverter(int index)
   {
      return this.converters[index];
   }

   /**
    * ����ֵ�����ͻ��ת����������ֵ.
    *
    * @param type    ֵ������
    * @return   -1δ�ҵ���Ӧ��ת����, ������ڵ���0��Ϊת������Ӧ������ֵ
    * @see #getConverter(int)
    */
   int getConverterIndex(Class type)
   {
      Integer i = (Integer) this.converterIndexMap.get(type);
      if (i == null)
      {
         return -1;
      }
      return i.intValue();
   }

   /**
    * ��һ������ע��һ��ת����.
    */
   synchronized void registerConverter(Class type, ValueConverter converter)
   {
      if (converter == null)
      {
         return;
      }
      Integer i = (Integer) this.converterIndexMap.get(type);
      if (i == null)
      {
         if (this.converters.length <= this.usedCount + 1)
         {
            int newCapacity = this.usedCount + 16;
            ValueConverter[] newConverters = new ValueConverter[newCapacity];
            System.arraycopy(this.converters, 0, newConverters, 0, this.converters.length);
            this.converters = newConverters;
         }
         i = Utility.createInteger(++this.usedCount);
         this.converterIndexMap.put(type, i);
      }
      if (this.converters[i.intValue()] != null && type.isPrimitive())
      {
         if (this.converters[i.intValue()].getClass() != converter.getClass())
         {
            throw new IllegalArgumentException("For the primitive [" + type
                  + "], the ValueConverter class must same as the old.");
         }
      }
      this.converters[i.intValue()] = converter;
   }

   /**
    * ��һ������ע��һ��<code>PropertyEditor</code>, ת������ʹ����������ת��.
    */
   synchronized void registerPropertyEditor(Class type, PropertyEditor pe)
   {
      if (pe == null)
      {
         return;
      }
      if (type.isPrimitive())
      {
         int tmpI = this.getConverterIndex(type);
         ValueConverter vc = (ValueConverter) this.converters[tmpI].clone();
         vc.setPropertyEditor(pe);
         this.converters[tmpI] = vc;
      }
      else
      {
         ValueConverter vc = new ObjectConverter();
         vc.setPropertyEditor(pe);
         this.registerConverter(type, vc);
      }
   }

   /**
    * ��¡ת����������.
    */
   public Object clone()
   {
      ConverterManager result = null;
      try
      {
         result = (ConverterManager) super.clone();
      }
      catch (CloneNotSupportedException e)
      {
         // assert false;
      }
      result.converters = (ValueConverter[]) this.converters.clone();
      result.converterIndexMap = (HashMap) this.converterIndexMap.clone();
      return result;
   }

}
