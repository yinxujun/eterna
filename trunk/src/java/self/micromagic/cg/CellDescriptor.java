
package self.micromagic.cg;

import java.lang.ref.WeakReference;

/**
 * bean���Ե�Ԫ��������.
 */
public class CellDescriptor
{
   private String name;
   private boolean readOldValue = false;
   private boolean beanType = false;
   private boolean arrayType = false;
   private boolean arrayBeanType = false;

   BeanPropertyReader readProcesser;  // BeanPropertyReader
   BeanPropertyWriter writeProcesser; // BeanPropertyWriter

   /**
    * ����ʹ��<code>WeakReference</code>�����õ�Ԫ������, �����Ͳ���Ӱ�����������ͷ�.
    */
   private WeakReference cellType;
   private WeakReference arrayElementType;

   /**
    * ��ȡ���Ե�����.
    */
   public String getName()
   {
      return name;
   }

   /**
    * �������Ե�����.
    */
   void setName(String name)
   {
      this.name = name;
   }

   /**
    * ��ȡд����ʱ�Ƿ�Ҫ��ȡԭ����ֵ.
    */
   public boolean isReadOldValue()
   {
      return readOldValue;
   }

   /**
    * ����д����ʱ�Ƿ�Ҫ��ȡԭ����ֵ.
    */
   public void setReadOldValue(boolean readOldValue)
   {
      this.readOldValue = readOldValue;
   }

   /**
    * ��ȡ���Ե�Ԫ������.
    */
   public Class getCellType()
   {
      if (this.cellType == null)
      {
         return null;
      }
      return (Class) this.cellType.get();
   }

   /**
    * �������Ե�Ԫ������.
    */
   public void setCellType(Class cellType)
   {
      this.cellType = new WeakReference(cellType);
   }

   /**
    * ��ȡ���Ե�Ԫ�������Ƿ���һ������.
    */
   public boolean isArrayType()
   {
      return arrayType;
   }

   /**
    * �������Ե�Ԫ�������Ƿ���һ������.
    */
   public void setArrayType(boolean arrayType)
   {
      this.arrayType = arrayType;
      if (arrayType)
      {
         this.setReadOldValue(true);
         Class tmpClass = ClassGenerator.getArrayElementType(this.getCellType(), null);
         this.setArrayElementType(tmpClass);
         if (BeanTool.checkBean(tmpClass))
         {
            this.setArrayBeanType(true);
         }
      }
      else
      {
         this.arrayBeanType = false;
         this.arrayElementType = null;
      }
   }

   /**
    * ������Ե�Ԫ��һ������, ��ȡ�������Ԫ�������Ƿ���һ��bean.
    */
   public boolean isArrayBeanType()
   {
      return arrayBeanType;
   }

   /**
    * ������Ե�Ԫ��һ������, ���ø������Ԫ�������Ƿ���һ��bean.
    */
   public void setArrayBeanType(boolean arrayBeanType)
   {
      if (this.isArrayType())
      {
         this.arrayBeanType = arrayBeanType;
      }
   }

   /**
    * ������Ե�Ԫ��һ������, ��ȡ�������Ԫ������.
    */
   public Class getArrayElementType()
   {
      if (this.arrayElementType == null)
      {
         return null;
      }
      return (Class) this.arrayElementType.get();
   }

   /**
    * ������Ե�Ԫ��һ������, ���ø������Ԫ������.
    */
   public void setArrayElementType(Class arrayElementType)
   {
      if (this.isArrayType())
      {
         this.arrayElementType = new WeakReference(arrayElementType);
      }
   }

   /**
    * ��ȡ���Ե�Ԫ�������Ƿ���һ��bean.
    */
   public boolean isBeanType()
   {
      return this.beanType;
   }

   /**
    * �������Ե�Ԫ�������Ƿ���һ��bean.
    */
   public void setBeanType(boolean beanType)
   {
      if (beanType)
      {
         this.setReadOldValue(true);
      }
      this.beanType = beanType;
   }

   /**
    * ��ȡ��bean���ԵĶ�������.
    */
   public BeanPropertyReader getReadProcesser()
   {
      return this.readProcesser;
   }

   /**
    * ���ö�bean���ԵĶ�������.
    */
   public void setReadProcesser(BeanPropertyReader readProcesser)
   {
      this.readProcesser = readProcesser;
   }

   /**
    * ��ȡ��bean���Ե�д������.
    */
   public BeanPropertyWriter getWriteProcesser()
   {
      return this.writeProcesser;
   }

   /**
    * ���ö�bean���Ե�д������.
    */
   public void setWriteProcesser(BeanPropertyWriter writeProcesser)
   {
      this.writeProcesser = writeProcesser;
   }

}
