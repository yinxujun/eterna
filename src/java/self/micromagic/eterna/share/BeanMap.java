
package self.micromagic.eterna.share;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.beans.PropertyEditor;
import java.lang.ref.WeakReference;

import self.micromagic.eterna.sql.ResultRow;
import self.micromagic.eterna.sql.converter.ValueConverter;
import self.micromagic.util.ObjectRef;
import self.micromagic.util.StringTool;

/**
 * bean��map��ת������. <p>
 * ���Կ��ٵؽ�beanת��Ϊmap.
 * Ҳ���Կ��ٵؽ�map�е�ֵ���õ�bean��.
 */
public class BeanMap extends AbstractMap
      implements Map
{
   private Object beanObj;
   private Class beanType;
   private String namePrefix;
   private BeanTool.BeanDescriptor beanDescriptor;
   private BeanTool.ConverterManager converterManager;
   private boolean converterManagerCopied = false;
   private List entryList = null;

   BeanMap(Object beanObj, String namePrefix, BeanTool.BeanDescriptor beanDescriptor,
         BeanTool.ConverterManager converterManager)
   {
      this.beanObj = beanObj;
      this.beanType = beanObj.getClass();
      this.namePrefix = StringTool.isEmpty(namePrefix) ? "" : namePrefix;
      this.beanDescriptor = beanDescriptor;
      this.converterManager = converterManager;
   }

   BeanMap(Class beanType, String namePrefix, BeanTool.BeanDescriptor beanDescriptor,
         BeanTool.ConverterManager converterManager)
   {
      this.beanType = beanType;
      this.namePrefix = StringTool.isEmpty(namePrefix) ? "" : namePrefix;
      this.beanDescriptor = beanDescriptor;
      this.converterManager = converterManager;
   }

   /**
    * ע��һ������ת����.
    */
   public void registerConverter(Class type, ValueConverter converter)
   {
      if (!this.converterManagerCopied)
      {
         this.converterManager = (BeanTool.ConverterManager) this.converterManager.clone();
         this.converterManagerCopied = true;
      }
      this.converterManager.registerConverter(type, converter);
   }

   /**
    * ע��һ������ת��ʱʹ�õ�<code>PropertyEditor</code>.
    */
   public void registerPropertyEditor(Class type, PropertyEditor pe)
   {
      if (!this.converterManagerCopied)
      {
         this.converterManager = (BeanTool.ConverterManager) this.converterManager.clone();
         this.converterManagerCopied = true;
      }
      this.converterManager.registerPropertyEditor(type, pe);
   }

   /**
    * ����ת����������ֵ��ȡ��Ӧ��ת����.
    *
    * @param index  ת����������ֵ
    */
   public ValueConverter getConverter(int index)
   {
      return this.converterManager.getConverter(index);
   }

   /**
    * ����һ���µ�bean����, �˶���Ḳ��ԭ���󶨵�bean����.
    */
   public Object createBean()
   {
      try
      {
         this.beanObj = this.beanDescriptor.initCell.readProcesser.getBeanValue(
               null, this.getPrefix(), this);
      }
      catch (Exception ex) {}
      return this.beanObj;
   }

   /**
    * ��ȡbean�����Ӧ������.
    */
   public Class getBeanType()
   {
      return this.beanType;
   }

   /**
    * ��ȡ��Ӧ��bean����.
    */
   public Object getBean()
   {
      return this.beanObj;
   }

   /**
    * ��ȡ�������Ƶ�ǰ׺.
    */
   public String getPrefix()
   {
      return this.namePrefix;
   }

   /**
    * ͨ��һ��Map����bean�����ж�Ӧ������ֵ.
    */
   public int setValues(Map t)
   {
      if (this.getBean() == null)
      {
         this.createBean();
      }
      int settedCount = 0;
      Iterator eItr = this.beanDescriptor.cells.entrySet().iterator();
      Object bean = this.getBean();
      String prefix = this.getPrefix();
      Object value;
      while (eItr.hasNext())
      {
         Map.Entry bpEntry = (Map.Entry) eItr.next();
         value = t.get(prefix.length() == 0 ? bpEntry.getKey() : prefix + bpEntry.getKey());
         CellDescriptor cd = (CellDescriptor) bpEntry.getValue();
         if (cd.writeProcesser != null)
         {
            if (cd.isBeanType() || value != null)
            {
               try
               {
                  Object oldValue = null;
                  if (cd.readProcesser != null && cd.isReadOldValue())
                  {
                     oldValue = cd.readProcesser.getBeanValue(beanObj, prefix, this);
                  }
                  settedCount += cd.writeProcesser.setBeanValue(bean, value, prefix, this, t, oldValue);
               }
               catch (Exception ex)
               {
                  if (Tool.BP_CREATE_LOG_TYPE > 0)
                  {
                     Tool.log.info("Write bean value error.", ex);
                  }
               }
            }
         }
      }
      return settedCount;
   }

   /**
    * ͨ��һ��ResultRow����bean�����ж�Ӧ������ֵ.
    */
   public int setValues(ResultRow t)
   {
      if (this.getBean() == null)
      {
         this.createBean();
      }
      int settedCount = 0;
      Iterator eItr = this.beanDescriptor.cells.entrySet().iterator();
      Object bean = this.getBean();
      String prefix = this.getPrefix();
      Object value;
      while (eItr.hasNext())
      {
         Map.Entry bpEntry = (Map.Entry) eItr.next();
         String name = prefix.length() == 0 ? (String) bpEntry.getKey() : prefix + bpEntry.getKey();
         try
         {
            value = t.getObject(name, false);
            CellDescriptor cd = (CellDescriptor) bpEntry.getValue();
            if (cd.writeProcesser != null)
            {
               if (cd.isBeanType() || value != null)
               {
                  Object oldValue = null;
                  if (cd.readProcesser != null && cd.isReadOldValue())
                  {
                     oldValue = cd.readProcesser.getBeanValue(beanObj, prefix, this);
                  }
                  settedCount += cd.writeProcesser.setBeanValue(bean, value, prefix, this, t, oldValue);
               }
            }
         }
         catch (Exception ex)
         {
            if (Tool.BP_CREATE_LOG_TYPE > 0)
            {
               Tool.log.info("Write bean value error.", ex);
            }
         }
      }
      return settedCount;
   }

   /**
    * ���ݼ�ֵ��ȡ���Ե�Ԫ��������.
    */
   private CellDescriptor getCell(String key, ObjectRef beanMapRef, boolean needCreate)
   {
      int index = key.indexOf('.');
      if (index != -1)
      {
         String tmpName = key.substring(0, index);
         CellDescriptor cd = (CellDescriptor) this.beanDescriptor.cells.get(tmpName);
         if (cd != null && cd.isBeanType())
         {
            BeanMap sub = null;
            Object thisObj = this.getBean();
            if (thisObj == null && needCreate)
            {
               thisObj = this.createBean();
            }
            String prefix = this.getPrefix();
            if (thisObj != null && cd.readProcesser != null)
            {
               try
               {
                  Object tmpObj = cd.readProcesser.getBeanValue(thisObj, prefix, this);
                  if (tmpObj != null)
                  {
                     sub = BeanTool.getBeanMap(tmpObj, prefix + tmpName + ".");
                  }
               }
               catch (Exception ex) {}
            }
            if (sub == null)
            {
               sub = BeanTool.getBeanMap(cd.getCellType(), prefix + tmpName + ".");
               if (needCreate && cd.writeProcesser != null)
               {
                  try
                  {
                     cd.writeProcesser.setBeanValue(thisObj, sub.createBean(), prefix, this, null, null);
                  }
                  catch (Exception ex)
                  {
                     if (Tool.BP_CREATE_LOG_TYPE > 0)
                     {
                        Tool.log.info("Write bean value error.", ex);
                     }
                  }
               }
            }
            if (sub != null)
            {
               return sub.getCell(key.substring(index + 1), beanMapRef, needCreate);
            }
         }
         return null;
      }
      if (beanMapRef != null)
      {
         beanMapRef.setObject(this);
      }
      return (CellDescriptor) this.beanDescriptor.cells.get(key);
   }

   /**
    * ��ȡ���Ե�Ԫ��������б�.
    */
   private synchronized List getEntryList(Class[] beanTypeStack)
   {
      if (this.entryList != null)
      {
         return this.entryList;
      }
      if (beanTypeStack == null)
      {
         beanTypeStack = new Class[]{this.getBeanType()};
      }
      else
      {
         Class[] types = new Class[beanTypeStack.length + 1];
         System.arraycopy(beanTypeStack, 0, types, 0, beanTypeStack.length);
         types[beanTypeStack.length] = this.getBeanType();
      }
      List result = new LinkedList();
      Iterator itr = this.beanDescriptor.cells.entrySet().iterator();
      while (itr.hasNext())
      {
         Map.Entry entry = (Map.Entry) itr.next();
         CellDescriptor cd = (CellDescriptor) entry.getValue();
         BeanMap sub = null;
         if (cd.isBeanType())
         {
            Object thisObj = this.getBean();
            String prefix = this.getPrefix();
            if (thisObj != null && cd.readProcesser != null)
            {
               try
               {
                  Object tmpObj = cd.readProcesser.getBeanValue(thisObj, prefix, this);
                  if (tmpObj != null)
                  {
                     sub = BeanTool.getBeanMap(tmpObj, prefix + entry.getKey() + ".");
                  }
               }
               catch (Exception ex) {}
            }
            if (sub == null)
            {
               sub = BeanTool.getBeanMap(cd.getCellType(), prefix + entry.getKey() + ".");
            }
         }
         if (sub != null)
         {
            if (!this.isTypeInStack(beanTypeStack, sub.getBeanType()))
            {
               result.addAll(sub.getEntryList(beanTypeStack));
            }
         }
         else
         {
            result.add(new BeanMapEntry(this, entry.getKey(), cd));
         }
      }
      this.entryList = result;
      return result;
   }

   /**
    * �жϸ����������Ƿ������Ͷ�ջ��, ��ֹ�ݹ�Ľ���
    */
   private boolean isTypeInStack(Class[] beanTypeStack, Class type)
   {
      if (beanTypeStack == null)
      {
         return false;
      }
      for (int i = 0; i < beanTypeStack.length; i++)
      {
         if (type == beanTypeStack[i])
         {
            return true;
         }
      }
      return false;
   }

   public void putAll(Map t)
   {
      this.setValues(t);
   }

   public boolean containsKey(Object key)
   {
      if (key == null)
      {
         return false;
      }
      return this.getCell(String.valueOf(key), null, false) != null;
   }

   public Object get(Object key)
   {
      if (key == null)
      {
         return null;
      }
      ObjectRef ref = new ObjectRef();
      CellDescriptor cd = this.getCell(String.valueOf(key), ref, false);
      if (cd == null)
      {
         return null;
      }
      return BeanMapEntry.getBeanValue(cd, (BeanMap) ref.getObject());
   }

   public Object put(Object key, Object value)
   {
      if (key == null)
      {
         return null;
      }
      ObjectRef ref = new ObjectRef();
      CellDescriptor cd = this.getCell(String.valueOf(key), ref, true);
      if (cd == null)
      {
         return null;
      }
      return BeanMapEntry.setBeanValue(cd, (BeanMap) ref.getObject(), value);
   }

   public Object remove(Object key)
   {
      return this.put(key, null);
   }

   public void clear()
   {
      this.entrySet().clear();
   }

   public Set keySet()
   {
      return new BeanMapEntrySet(this, BEANMAP_SET_TYPE_KEY);
   }

   public Collection values()
   {
      return new BeanMapEntrySet(this, BEANMAP_SET_TYPE_VALUE);
   }

   public Set entrySet()
   {
      return new BeanMapEntrySet(this, BEANMAP_SET_TYPE_ENTRY);
   }

   public int hashCode()
   {
      Object myObj = this.getBean();
      if (myObj == null)
      {
         return 0;
      }
      return myObj.hashCode();
   }

   private final static int BEANMAP_SET_TYPE_ENTRY = 1;
   private final static int BEANMAP_SET_TYPE_VALUE = 2;
   private final static int BEANMAP_SET_TYPE_KEY = 3;

   private static class BeanMapEntrySet extends AbstractSet
         implements Set
   {
      private BeanMap beanMap;
      private int beanMapSetType;
      private List entryList;

      public BeanMapEntrySet(BeanMap beanMap, int beanMapSetType)
      {
         this.beanMap = beanMap;
         this.beanMapSetType = beanMapSetType;
         this.entryList = beanMap.getEntryList(null);
      }

      public int size()
      {
         return this.entryList.size();
      }

      public Iterator iterator()
      {
         return new BeanMapIterator(this.beanMapSetType, this.entryList.iterator());
      }

      public boolean add(Object o)
      {
         return false;
      }

      public boolean addAll(Collection c)
      {
         return false;
      }

      public boolean retainAll(Collection c)
      {
         return false;
      }

      public boolean remove(Object o)
      {
         return false;
      }

      public boolean removeAll(Collection c)
      {
         return false;
      }

      public int hashCode()
      {
         return this.beanMap.hashCode();
      }

   }

   private static class BeanMapIterator
         implements Iterator
   {
      private int beanMapSetType;
      private Iterator entrySetIterator;
      BeanMapEntry nowEntry = null;

      public BeanMapIterator(int beanMapSetType, Iterator entrySetIterator)
      {
         this.beanMapSetType = beanMapSetType;
         this.entrySetIterator = entrySetIterator;
      }

      public boolean hasNext()
      {
         return this.entrySetIterator.hasNext();
      }

      public Object next()
      {
         this.nowEntry = (BeanMapEntry) this.entrySetIterator.next();
         if (this.beanMapSetType == BEANMAP_SET_TYPE_VALUE)
         {
            return this.nowEntry.getValue();
         }
         else if (this.beanMapSetType == BEANMAP_SET_TYPE_KEY)
         {
            return this.nowEntry.getKey();
         }
         return this.nowEntry;
      }

      public void remove()
      {
         if (this.nowEntry != null)
         {
            this.nowEntry.setValue(null);
         }
      }

   }

   private static class BeanMapEntry
         implements Map.Entry
   {
      private BeanMap beanMap;
      private Object key;
      private CellDescriptor cellDescriptor;

      public BeanMapEntry(BeanMap beanMap, Object key, CellDescriptor cellDescriptor)
      {
         this.beanMap = beanMap;
         this.key = key;
         this.cellDescriptor = cellDescriptor;
      }

      static Object getBeanValue(CellDescriptor cd, BeanMap beanMap)
      {
         if (cd.readProcesser != null)
         {
            try
            {
               Object beanObj = beanMap.getBean();
               String prefix = beanMap.getPrefix();
               if (beanObj != null)
               {
                  return cd.readProcesser.getBeanValue(beanObj, prefix, beanMap);
               }
            }
            catch (Exception ex)
            {
               if (Tool.BP_CREATE_LOG_TYPE > 0)
               {
                  Tool.log.info("Read bean value error.", ex);
               }
            }
         }
         return null;
      }

      static Object setBeanValue(CellDescriptor cd, BeanMap beanMap, Object value)
      {
         if (cd.writeProcesser != null)
         {
            try
            {
               Object oldValue = null;
               Object beanObj = beanMap.getBean();
               String prefix = beanMap.getPrefix();
               if (beanObj != null)
               {
                  if (cd.readProcesser != null)
                  {
                     oldValue = cd.readProcesser.getBeanValue(beanObj, prefix, beanMap);
                  }
               }
               else
               {
                  beanObj = beanMap.createBean();
               }
               cd.writeProcesser.setBeanValue(beanObj, value, prefix, beanMap, null, oldValue);
               return oldValue;
            }
            catch (Exception ex)
            {
               if (Tool.BP_CREATE_LOG_TYPE > 0)
               {
                  Tool.log.info("Write bean value error.", ex);
               }
            }
         }
         return null;
      }

      public Object getKey()
      {
         String prefix = this.beanMap.getPrefix();
         return prefix.length() == 0 ? this.key : prefix + this.key;
      }

      public Object getValue()
      {
         return getBeanValue(this.cellDescriptor, this.beanMap);
      }

      public Object setValue(Object value)
      {
         return setBeanValue(this.cellDescriptor, this.beanMap, value);
      }

      public int hashCode()
      {
         Object key = this.getKey();
         Object value = this.getValue();
         return (key == null ? 0 : key.hashCode()) ^ (value == null ? 0 : value.hashCode());
      }

      public boolean equals(Object obj)
      {
         if (obj instanceof Map.Entry)
         {
            Map.Entry e = (Map.Entry) obj;
            Object k1 = this.getKey();
            Object k2 = e.getKey();
            if (k1 == k2 || (k1 != null && k1.equals(k2)))
            {
               Object v1 = this.getValue();
               Object v2 = e.getValue();
               if (v1 == v2 || (v1 != null && v1.equals(v2)))
                  return true;
            }
         }
         return false;
      }

      public String toString()
      {
         return this.getKey() + "=" + this.getValue();
      }

   }

   /**
    * bean���Ե�Ԫ��������.
    */
   public static class CellDescriptor
   {
      private String name;
      private boolean readOldValue;
      private BeanTool.BeanPropertyReader readProcesser;
      private BeanTool.BeanPropertyWriter writeProcesser;
      private boolean beanType;

      /**
       * ����ʹ��<code>WeakReference</code>�����õ�Ԫ������, �����Ͳ���Ӱ�����������ͷ�.
       */
      private WeakReference cellType;

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
      public BeanTool.BeanPropertyReader getReadProcesser()
      {
         return this.readProcesser;
      }

      /**
       * ���ö�bean���ԵĶ�������.
       */
      public void setReadProcesser(BeanTool.BeanPropertyReader readProcesser)
      {
         this.readProcesser = readProcesser;
      }

      /**
       * ��ȡ��bean���Ե�д������.
       */
      public BeanTool.BeanPropertyWriter getWriteProcesser()
      {
         return this.writeProcesser;
      }

      /**
       * ���ö�bean���Ե�д������.
       */
      public void setWriteProcesser(BeanTool.BeanPropertyWriter writeProcesser)
      {
         this.writeProcesser = writeProcesser;
      }

   }

}
