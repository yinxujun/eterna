
package self.micromagic.cg;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Map;

/**
 * bean的描述类.
 */
public class BeanDescriptor
{
   private Map cells;
   private CellDescriptor initCell;

   /**
    * 这里使用<code>WeakReference</code>来引用单元的类型, 这样就不会影响其正常的释放.
    */
   private WeakReference beanType;

   BeanDescriptor(Class beanType, Map cells, CellDescriptor initCell)
   {
      this.beanType = new WeakReference(beanType);
      this.cells = cells;
      this.initCell = initCell;
   }

   /**
    * 获得BeanMap处理的bean的类型.
    */
   public Class getBeanType()
   {
      return (Class) beanType.get();
   }

   /**
    * 获得BeanMap中对bean的构造单元.
    */
   public CellDescriptor getInitCell()
   {
      return this.initCell;
   }

   /**
    * 获得BeanMap中对bean属性的操作单元.
    */
   public CellDescriptor getCell(String name)
   {
      return (CellDescriptor) this.cells.get(name);
   }

   /**
    * 以迭代器的方式获得BeanMap中对bean属性的所有操作单元.
    */
   public Iterator getCellIterator()
   {
      return this.cells.values().iterator();
   }

   /**
    * 在BeanMap中添加一个对bean属性的操作单元.
    *
    * @param cd  bean属性的操作单元
    * @return    <code>true</code>添加成功, <code>false</code>添加失败
    *            如已经存在同名的操作单元, cd参数为null或cd的name为null
    */
   public synchronized boolean addCell(CellDescriptor cd)
   {
      if (cd == null)
      {
         return false;
      }
      String name = cd.getName();
      if (name == null)
      {
         return false;
      }
      if (this.cells.containsKey(name))
      {
         return false;
      }
      this.cells.put(name, cd);
      return true;
   }

}
