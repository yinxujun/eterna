
package self.micromagic.util.container;

import java.util.AbstractSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import self.micromagic.util.Utility;

public class ValueContainerMapEntrySet extends AbstractSet
      implements Set
{
   private ValueContainer vContainer;
   private Map entryMap = null;

   public ValueContainerMapEntrySet(ValueContainer vContainer)
   {
      if (vContainer == null)
      {
         throw new NullPointerException();
      }
      this.vContainer = vContainer;
   }

   private synchronized void initEntryMap()
   {
      if (this.entryMap == null)
      {
         this.entryMap = new HashMap();
         MapEntry entry;
         Enumeration e = this.vContainer.getKeys();
         while (e.hasMoreElements())
         {
            entry = new MapEntry(e.nextElement());
            this.entryMap.put(entry.getKey(), entry);
         }
      }
   }

   protected boolean isEntryMapInitialized()
   {
      return this.entryMap != null;
   }

   public int size()
   {
      if (this.entryMap == null)
      {
         this.initEntryMap();
      }
      return this.entryMap.size();
   }

   public boolean contains(Object o)
   {
      if (o != null && (o instanceof Map.Entry))
      {
         Map.Entry entry = (Map.Entry) o;
         if (this.entryMap != null)
         {
            Object value = this.entryMap.get(entry.getKey());
            if (value != null)
            {
               return Utility.objectEquals(
                     ((Map.Entry) value).getValue(), entry.getValue());
            }
         }
         else
         {
            Object value = this.vContainer.getValue(entry.getKey());
            return Utility.objectEquals(value, entry.getValue());
         }
      }
      return false;
   }

   public Iterator iterator()
   {
      if (this.entryMap == null)
      {
         this.initEntryMap();
      }
      return new MapEntrySetIterator(this.entryMap.values().iterator());
   }

   public boolean add(Object o)
   {
      if (o != null && (o instanceof Map.Entry))
      {
         Map.Entry entry = (Map.Entry) o;
         this.vContainer.setValue(entry.getKey(), entry.getValue());
         return this.addEntry(entry) == null;
      }
      return false;
   }

   protected synchronized Object addEntry(Map.Entry entry)
   {
      if (this.entryMap != null)
      {
         return this.entryMap.put(entry.getKey(), entry);
      }
      return null;
   }

   public boolean remove(Object o)
   {
      if (o != null && (o instanceof Map.Entry))
      {
         Map.Entry entry = (Map.Entry) o;
         this.vContainer.removeValue(entry.getKey());
         return this.removeEntry(entry) != null;
      }
      return false;
   }

   protected synchronized Object removeEntry(Map.Entry entry)
   {
      if (this.entryMap != null)
      {
         return this.entryMap.remove(entry.getKey());
      }
      return entry;
   }

   private void removeByIterator(Map.Entry entry)
   {
      this.vContainer.removeValue(entry.getKey());
   }

   public synchronized Object addValue(Object key, Object value)
   {
      Object oldValue = this.vContainer.getValue(key);
      if (this.isEntryMapInitialized())
      {
         MapEntry entry = new MapEntry(key, value);
         this.add(entry);
      }
      else
      {
         this.vContainer.setValue(key, value);
      }
      return oldValue;
   }

   public synchronized Object removeValue(Object key)
   {
      Object oldValue = this.vContainer.getValue(key);
      if (this.isEntryMapInitialized())
      {
         Map.Entry entry = new MapEntry(key);
         this.remove(entry);
      }
      else
      {
         this.vContainer.removeValue(key);
      }
      return oldValue;
   }

   private class MapEntrySetIterator
         implements Iterator
   {
      private Iterator itr;
      private MapEntry current = null;

      public MapEntrySetIterator(Iterator itr)
      {
         this.itr = itr;
      }

      public boolean hasNext()
      {
         return this.itr.hasNext();
      }

      public Object next()
      {
         this.current = (MapEntry) this.itr.next();
         return this.current;
      }

      public void remove()
      {
         if (this.current == null)
         {
            throw new IllegalStateException();
         }
         ValueContainerMapEntrySet.this.removeByIterator(this.current);
         this.current = null;
         this.itr.remove();
      }

   }

   private class MapEntry
         implements Map.Entry
   {
      private Object key;
      private Object value = null;

      private String toStringValue = null;

      public MapEntry(Object key)
      {
         this.key = key;
      }

      public MapEntry(Object key, Object value)
      {
         this(key);
         this.value = value;
      }

      public Object getKey()
      {
         return this.key;
      }

      public Object getValue()
      {
         if (this.value == null)
         {
            this.value = ValueContainerMapEntrySet.this.vContainer.getValue(this.key);
         }
         return this.value;
      }

      public Object setValue(Object value)
      {
         Object oldValue = this.getValue();
         this.value = value;
         ValueContainerMapEntrySet.this.vContainer.setValue(this.key, value);
         return oldValue;
      }

      public int hashCode()
      {
         if (this.key == null)
         {
            return 0;
         }
         return this.key.hashCode();
      }

      public boolean equals(Object obj)
      {
         if (obj instanceof Map.Entry)
         {
            Object otherKey = ((Map.Entry) obj).getKey();
            Object otherValue = ((Map.Entry) obj).getValue();
            return Utility.objectEquals(this.key, otherKey)
                  && Utility.objectEquals(this.value, otherValue);
         }
         return false;
      }

      public String toString()
      {
         if (this.toStringValue == null)
         {
            StringBuffer buf = new StringBuffer(32);
            buf.append("Entry[key:").append(this.key)
                  .append(",value:").append(this.value).append(']');
            this.toStringValue = buf.toString();
         }
         return this.toStringValue;
      }

   }

}