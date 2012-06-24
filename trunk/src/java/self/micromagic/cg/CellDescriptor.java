
package self.micromagic.cg;

import java.lang.ref.WeakReference;

/**
 * bean属性单元的描述类.
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
    * 这里使用<code>WeakReference</code>来引用单元的类型, 这样就不会影响其正常的释放.
    */
   private WeakReference cellType;
   private WeakReference arrayElementType;

   /**
    * 获取属性的名称.
    */
   public String getName()
   {
      return name;
   }

   /**
    * 设置属性的名称.
    */
   void setName(String name)
   {
      this.name = name;
   }

   /**
    * 获取写属性时是否要读取原来的值.
    */
   public boolean isReadOldValue()
   {
      return readOldValue;
   }

   /**
    * 设置写属性时是否要读取原来的值.
    */
   public void setReadOldValue(boolean readOldValue)
   {
      this.readOldValue = readOldValue;
   }

   /**
    * 获取属性单元的类型.
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
    * 设置属性单元的类型.
    */
   public void setCellType(Class cellType)
   {
      this.cellType = new WeakReference(cellType);
   }

   /**
    * 获取属性单元的类型是否是一个数组.
    */
   public boolean isArrayType()
   {
      return arrayType;
   }

   /**
    * 设置属性单元的类型是否是一个数组.
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
    * 如果属性单元是一个数组, 获取该数组的元素类型是否是一个bean.
    */
   public boolean isArrayBeanType()
   {
      return arrayBeanType;
   }

   /**
    * 如果属性单元是一个数组, 设置该数组的元素类型是否是一个bean.
    */
   public void setArrayBeanType(boolean arrayBeanType)
   {
      if (this.isArrayType())
      {
         this.arrayBeanType = arrayBeanType;
      }
   }

   /**
    * 如果属性单元是一个数组, 获取该数组的元素类型.
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
    * 如果属性单元是一个数组, 设置该数组的元素类型.
    */
   public void setArrayElementType(Class arrayElementType)
   {
      if (this.isArrayType())
      {
         this.arrayElementType = new WeakReference(arrayElementType);
      }
   }

   /**
    * 获取属性单元的类型是否是一个bean.
    */
   public boolean isBeanType()
   {
      return this.beanType;
   }

   /**
    * 设置属性单元的类型是否是一个bean.
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
    * 获取对bean属性的读处理者.
    */
   public BeanPropertyReader getReadProcesser()
   {
      return this.readProcesser;
   }

   /**
    * 设置对bean属性的读处理者.
    */
   public void setReadProcesser(BeanPropertyReader readProcesser)
   {
      this.readProcesser = readProcesser;
   }

   /**
    * 获取对bean属性的写处理者.
    */
   public BeanPropertyWriter getWriteProcesser()
   {
      return this.writeProcesser;
   }

   /**
    * 设置对bean属性的写处理者.
    */
   public void setWriteProcesser(BeanPropertyWriter writeProcesser)
   {
      this.writeProcesser = writeProcesser;
   }

}
