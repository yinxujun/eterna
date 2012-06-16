
package self.micromagic.cg;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Map;

/**
 * bean��������.
 */
public class BeanDescriptor
{
   private Map cells;
   private CellDescriptor initCell;

   /**
    * ����ʹ��<code>WeakReference</code>�����õ�Ԫ������, �����Ͳ���Ӱ�����������ͷ�.
    */
   private WeakReference beanType;

   BeanDescriptor(Class beanType, Map cells, CellDescriptor initCell)
   {
      this.beanType = new WeakReference(beanType);
      this.cells = cells;
      this.initCell = initCell;
   }

   /**
    * ���BeanMap�����bean������.
    */
   public Class getBeanType()
   {
      return (Class) beanType.get();
   }

   /**
    * ���BeanMap�ж�bean�Ĺ��쵥Ԫ.
    */
   public CellDescriptor getInitCell()
   {
      return this.initCell;
   }

   /**
    * ���BeanMap�ж�bean���ԵĲ�����Ԫ.
    */
   public CellDescriptor getCell(String name)
   {
      return (CellDescriptor) this.cells.get(name);
   }

   /**
    * �Ե������ķ�ʽ���BeanMap�ж�bean���Ե����в�����Ԫ.
    */
   public Iterator getCellIterator()
   {
      return this.cells.values().iterator();
   }

   /**
    * ��BeanMap�����һ����bean���ԵĲ�����Ԫ.
    *
    * @param cd  bean���ԵĲ�����Ԫ
    * @return    <code>true</code>��ӳɹ�, <code>false</code>���ʧ��
    *            ���Ѿ�����ͬ���Ĳ�����Ԫ, cd����Ϊnull��cd��nameΪnull
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
