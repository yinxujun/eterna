
package self.micromagic.cg;

import java.beans.PropertyEditor;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import self.micromagic.eterna.sql.ResultRow;
import self.micromagic.eterna.sql.converter.ValueConverter;
import self.micromagic.util.StringRef;
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
   private BeanDescriptor beanDescriptor;
   private ConverterManager converterManager;
   private boolean converterManagerCopied = false;
   private List entryList = null;

   BeanMap(Object beanObj, String namePrefix, BeanDescriptor beanDescriptor,
         ConverterManager converterManager)
   {
      this.beanObj = beanObj;
      this.beanType = beanObj.getClass();
      this.namePrefix = StringTool.isEmpty(namePrefix) ? "" : namePrefix;
      this.beanDescriptor = beanDescriptor;
      this.converterManager = converterManager;
   }

   BeanMap(Class beanType, String namePrefix, BeanDescriptor beanDescriptor,
         ConverterManager converterManager)
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
         this.converterManager = (ConverterManager) this.converterManager.clone();
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
         this.converterManager = (ConverterManager) this.converterManager.clone();
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
         this.beanObj = this.beanDescriptor.getInitCell().readProcesser.getBeanValue(
               null, null, null, this.getPrefix(), this);
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
    * �˷�������bean�ĽṹΪ����, ��map�л�ȡ��Ӧ��ֵ����������.
    */
   public int setValues(Map map)
   {
      if (this.getBean() == null)
      {
         this.createBean();
      }
      int settedCount = 0;
      Iterator cdItr = this.beanDescriptor.getCellIterator();
      Object bean = this.getBean();
      String prefix = this.getPrefix();
      Object value;
      while (cdItr.hasNext())
      {
         CellDescriptor cd = (CellDescriptor) cdItr.next();
         String pName = cd.getName();
         value = map.get(prefix.length() == 0 ? pName : prefix + pName);
         if (cd.writeProcesser != null)
         {
            if (cd.isBeanType() || value != null)
            {
               try
               {
                  Object oldValue = null;
                  if (cd.readProcesser != null && cd.isReadOldValue())
                  {
                     oldValue = cd.readProcesser.getBeanValue(cd, null, beanObj, prefix, this);
                  }
                  settedCount += cd.writeProcesser.setBeanValue(
                        cd, null, bean, value, prefix, this, map, oldValue);
               }
               catch (Exception ex)
               {
                  if (ClassGenerator.COMPILE_LOG_TYPE > CG.COMPILE_LOG_TYPE_ERROR)
                  {
                     CG.log.info("Write bean value error.", ex);
                  }
               }
            }
         }
      }
      return settedCount;
   }

   /**
    * ͨ��һ��ResultRow����bean�����ж�Ӧ������ֵ.
    * �˷�������bean�ĽṹΪ����, ��ResultRow�л�ȡ��Ӧ��ֵ����������.
    */
   public int setValues(ResultRow row)
   {
      if (this.getBean() == null)
      {
         this.createBean();
      }
      int settedCount = 0;
      Iterator cdItr = this.beanDescriptor.getCellIterator();
      Object bean = this.getBean();
      String prefix = this.getPrefix();
      Object value;
      while (cdItr.hasNext())
      {
         CellDescriptor cd = (CellDescriptor) cdItr.next();
         String pName = cd.getName();
         String name = prefix.length() == 0 ? pName : prefix + pName;
         try
         {
            value = row.getObject(name, true);
            if (cd.writeProcesser != null)
            {
               if (cd.isBeanType() || value != null)
               {
                  Object oldValue = null;
                  if (cd.readProcesser != null && cd.isReadOldValue())
                  {
                     oldValue = cd.readProcesser.getBeanValue(cd, null, beanObj, prefix, this);
                  }
                  settedCount += cd.writeProcesser.setBeanValue(
                        cd, null, bean, value, prefix, this, row, oldValue);
               }
            }
         }
         catch (Exception ex)
         {
            if (ClassGenerator.COMPILE_LOG_TYPE > CG.COMPILE_LOG_TYPE_ERROR)
            {
               CG.log.info("Write bean value error.", ex);
            }
         }
      }
      return settedCount;
   }

   /**
    * ���ݼ�ֵ��ȡ���Ե�Ԫ�ķ�����Ϣ.
    *
    * @param key          ���ڻ�ȡ���Ե�Ԫ������Ϣ�ļ�ֵ
    */
   public CellAccessInfo getCellAccessInfo(String key)
   {
      return this.getCellAccessInfo(key, false);
   }

   /**
    * ���ݼ�ֵ��ȡ���Ե�Ԫ�ķ�����Ϣ.
    *
    * @param key          ���ڻ�ȡ���Ե�Ԫ������Ϣ�ļ�ֵ
    * @param needCreate   �����Ӧ��bean������ʱ�Ƿ�Ҫ�Զ�����
    */
   public CellAccessInfo getCellAccessInfo(String key, boolean needCreate)
   {
      int index = key.indexOf('.');
      if (index != -1)
      {
         String tmpName;
         StringRef refName = new StringRef();
         int[] indexs = this.parseArrayName(key.substring(0, index), refName);
         tmpName = refName.getString();
         CellDescriptor cd =  this.beanDescriptor.getCell(tmpName);
         if (cd != null)
         {
            if (cd.isBeanType())
            {
               if (indexs != null)
               {
                  // bean���͵��޷������鷽ʽ��ȡ
                  return null;
               }
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
                     Object tmpObj = cd.readProcesser.getBeanValue(cd, null, thisObj, prefix, this);
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
                        cd.writeProcesser.setBeanValue(cd, null, thisObj, sub.createBean(),
                              prefix, this, null, null);
                     }
                     catch (Exception ex)
                     {
                        if (ClassGenerator.COMPILE_LOG_TYPE > CG.COMPILE_LOG_TYPE_ERROR)
                        {
                           CG.log.info("Write bean value error.", ex);
                        }
                     }
                  }
               }
               if (sub != null)
               {
                  return sub.getCellAccessInfo(key.substring(index + 1), needCreate);
               }
            }
            else if (cd.isArrayType())
            {
               if (!cd.isArrayBeanType() || indexs == null)
               {
                  // �����Ԫ�����Ͳ���bean��û����������, �޷�����������
                  return null;
               }
               Object thisObj = this.getBean();
               if (thisObj == null)
               {
                  // ��ǰ���󲻴���, �޷���������������
                  return null;
               }
               String prefix = this.getPrefix();
               if (cd.readProcesser != null)
               {
                  try
                  {
                     Object tmpObj = cd.readProcesser.getBeanValue(cd, indexs, thisObj, prefix, this);
                     if (tmpObj == null || tmpObj.getClass().isArray())
                     {
                        // ���û��õ�����, �������һ������, �޷�����������
                        return null;
                     }
                     BeanMap sub = BeanTool.getBeanMap(tmpObj, prefix + tmpName + ".");
                     return sub.getCellAccessInfo(key.substring(index + 1), needCreate);
                  }
                  catch (Exception ex) {}
               }
            }
            else if (Collection.class.isAssignableFrom(cd.getCellType()))
            {
               if (indexs == null && indexs.length != 1)
               {
                  // ��������������Ԫ������������Ϊ1, �޷�����������
                  return null;
               }
               Object thisObj = this.getBean();
               if (thisObj == null)
               {
                  // ��ǰ���󲻴���, �޷���������������
                  return null;
               }
               String prefix = this.getPrefix();
               if (cd.readProcesser != null)
               {
                  try
                  {
                     Object tmpObj = cd.readProcesser.getBeanValue(cd, indexs, thisObj, prefix, this);
                     if (tmpObj == null || !BeanTool.checkBean(tmpObj.getClass()))
                     {
                        // ���û��õ�����, �������һ��bean
                        return null;
                     }
                     BeanMap sub = BeanTool.getBeanMap(tmpObj, prefix + tmpName + ".");
                     return sub.getCellAccessInfo(key.substring(index + 1), needCreate);
                  }
                  catch (Exception ex) {}
               }
            }
         }
         return null;
      }
      StringRef refName = new StringRef();
      int[] indexs = this.parseArrayName(key, refName);
      CellDescriptor cd = this.beanDescriptor.getCell(refName.toString());
      if (cd == null)
      {
         return null;
      }
      return new CellAccessInfo(this, cd, indexs);
   }

   /**
    * ���������е�������Ϣ. <p>
    * ��:
    * ���������   ����          ����ֵ
    * tmpName      tmpName       null
    * tArr[1]      tArr          [1]
    * arrs[2][3]   arrs          [2, 3]
    *
    * @param name      ���������
    * @param pureName  ����, ������������Ϣ�����ƶ���
    * @return   ������ʵ�����ֵ�б�
    */
   public static int[] parseArrayName(String name, StringRef pureName)
   {
      if (name.charAt(name.length() - 1) == ']')
      {
         int index = name.indexOf('[');
         if (index == -1 || index == 0)
         {
            throw new IllegalArgumentException("Error array visit name:\"" + name + "\".");
         }
         pureName.setString(name.substring(0, index));
         int endIndex = name.indexOf(']', index + 1);
         if (endIndex == name.length() - 1)
         {
            return new int[]{Integer.parseInt(name.substring(index + 1, endIndex))};
         }
         else
         {
            List indexList = new LinkedList();
            indexList.add(new Integer(name.substring(index + 1, endIndex)));
            while (endIndex < name.length() - 1)
            {
               index = name.indexOf('[', endIndex + 1);
               if (index == -1)
               {
                  throw new IllegalArgumentException("Error array visit name:\"" + name + "\".");
               }
               endIndex = name.indexOf(']', index + 1);
               indexList.add(new Integer(name.substring(index + 1, endIndex)));
            }
            int tmpI = 0;
            int[] indexs = new int[indexList.size()];
            Iterator itr = indexList.iterator();
            while (itr.hasNext())
            {
               indexs[tmpI++] = ((Integer) itr.next()).intValue();
            }
            return indexs;
         }
      }
      pureName.setString(name);
      return null;
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
      Iterator cdItr = this.beanDescriptor.getCellIterator();
      while (cdItr.hasNext())
      {
         CellDescriptor cd = (CellDescriptor) cdItr.next();
         String pName = cd.getName();
         BeanMap sub = null;
         if (cd.isBeanType())
         {
            Object thisObj = this.getBean();
            String prefix = this.getPrefix();
            if (thisObj != null && cd.readProcesser != null)
            {
               try
               {
                  Object tmpObj = cd.readProcesser.getBeanValue(cd, null, thisObj, prefix, this);
                  if (tmpObj != null)
                  {
                     sub = BeanTool.getBeanMap(tmpObj, prefix + pName + ".");
                  }
               }
               catch (Exception ex) {}
            }
            if (sub == null)
            {
               sub = BeanTool.getBeanMap(cd.getCellType(), prefix + pName + ".");
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
            result.add(new BeanMapEntry(this, pName, cd));
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
      return this.getCellAccessInfo(String.valueOf(key), false) != null;
   }

   public Object get(Object key)
   {
      if (key == null)
      {
         return null;
      }
      CellAccessInfo cai = this.getCellAccessInfo(String.valueOf(key), false);
      if (cai == null)
      {
         return null;
      }
      return cai.getValue();
   }

   public Object put(Object key, Object value)
   {
      if (key == null)
      {
         return null;
      }
      CellAccessInfo cai = this.getCellAccessInfo(String.valueOf(key), true);
      if (cai == null)
      {
         return null;
      }
      return cai.setValue(value);
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

   private final static int BEANMAP_SET_TYPE_ENTRY = 1;
   private final static int BEANMAP_SET_TYPE_VALUE = 2;
   private final static int BEANMAP_SET_TYPE_KEY = 3;

   private static class BeanMapEntrySet extends AbstractSet
         implements Set
   {
      private int beanMapSetType;
      private List entryList;

      public BeanMapEntrySet(BeanMap beanMap, int beanMapSetType)
      {
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
      private CellAccessInfo cellAccessInfo;

      public BeanMapEntry(BeanMap beanMap, Object key, CellDescriptor cellDescriptor)
      {
         this.beanMap = beanMap;
         this.key = key;
         this.cellAccessInfo = new CellAccessInfo(beanMap, cellDescriptor, null);
      }

      public Object getKey()
      {
         String prefix = this.beanMap.getPrefix();
         return prefix.length() == 0 ? this.key : prefix + this.key;
      }

      public Object getValue()
      {
         return this.cellAccessInfo.getValue();
      }

      public Object setValue(Object value)
      {
         return this.cellAccessInfo.setValue(value);
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

}
