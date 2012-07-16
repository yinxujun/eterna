
package self.micromagic.util.container;

import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * һ��ͬ����HashMap, �󲿷ִ���ο�jdk1.6��HashMap����д��. <p>
 * �����ʵ�ַ�ʽ��, �Ի��޸�size�ķ�������ͬ��, ��: put remove��, �Զ�ȡ�ķ���
 * ������ͬ��, ��: get containsKey.
 *
 * һ���HashMap���Զ�ȡ��������ͬ���ǻ������¼�������:
 *
 * 1. ��hash��ı�ʱ, ����ɻ�ȡ���ڵ�keyʱȴ����null.
 * ����Ĵ�����Ϊ, �����еĶ�ȡ������, ��ֱ������ʵ��hash��, ͨ��getTable�������
 * ��hash�������, ������ʵ����hash��ı�ʱҲ����Ӱ�쵱ǰ������ʹ�õ�hash��.
 * ����, �ع�hash��ʱ, ÿһ��EntryҲ��clone��, ��֤���ı�ԭ��������ṹ.
 *
 * 2. ����ͬ���߳̽��л�ȡget��putʱ, get�����Ľ�����������̵߳�Ӱ��.
 * ��: getʱ�����, ��ʱ��һ���߳�put��һ��key, ���������get����Ӧ�÷���null��ȴ��
 * ���˷���ֵ.
 * ���ﲢ������������, ������������Կ���put������ִ����, ֮����ִ����get.
 *
 * �������Ҫ������Ҫ��������get, put�������ٷ��������. ��Ŀ���Ǳ�֤���̻߳�����,
 * get����ִ�е��㹻��, ������put���������get�Ľ����map�Ľṹ����Ӱ��.
 * ����, �ڳ�ʼ��ʱ��������keyʹ�õ����÷�ʽ, Ӳ���� ������ ������.
 */
public class SynHashMap extends AbstractMap
		implements Map, Cloneable, Serializable
{
	/**
	 * keyֵʹ��Ӳ����.
	 */
	public static final int HARD = 0;

	/**
	 * keyֵʹ��������.
	 */
	public static final int SOFT = 1;

	/**
	 * keyֵʹ��������.
	 */
	public static final int WEAK = 2;

	/**
	 * Ĭ�ϵĳ�ʼ���� - ������2�ĳ˷�.
	 */
	public static final int DEFAULT_INITIAL_CAPACITY = 16;

	/**
	 * �������.
	 */
	protected static final int MAXIMUM_CAPACITY = 1 << 30;

	/**
	 * Ĭ�ϵĸ�������.
	 */
	protected static final float DEFAULT_LOAD_FACTOR = 0.75f;


   /**
    * keyֵʹ�õ���������.
    */
   protected final int keyRefType;

	/**
	 * hash��, ���ȱ�����2�ĳ˷�.
	 */
	protected transient SynEntry[] table;

	/**
	 * ��������ݵĸ���.
	 */
	protected transient int size;

	/**
	 * ��һ����Ҫ�ع�hash�������.
	 */
	protected int threshold;

	/**
	 * hash��ĸ�������.
	 */
	protected final float loadFactor;

	/**
	 * SynHashMap���޸ĵĴ���.
	 */
	protected transient volatile int modCount;

   /**
    * ������������õĶ���.
    */
	private final ReferenceQueue queue = new ReferenceQueue();

	/**
	 * ����һ���յ�<tt>SynHashMap</tt>.
	 */
	public SynHashMap(int initialCapacity, int keyRefType, float loadFactor)
	{
		if (initialCapacity < 0)
		{
			throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
		}
		if (initialCapacity > MAXIMUM_CAPACITY)
		{
			initialCapacity = MAXIMUM_CAPACITY;
		}
		if (loadFactor <= 0 || Float.isNaN(loadFactor))
		{
			throw new IllegalArgumentException("Illegal load factor: " + loadFactor);
		}
		this.keyRefType = keyRefType < 0 ? HARD : keyRefType;

		// Find a power of 2 >= initialCapacity
		int capacity = 1;
		while (capacity < initialCapacity)
		{
			capacity <<= 1;
		}

		this.loadFactor = loadFactor;
		threshold = (int) (capacity * loadFactor);
		table = new SynEntry[capacity];
		init();
	}

	/**
	 * ����һ���յ�<tt>SynHashMap</tt>.
	 */
	public SynHashMap(int initialCapacity, int keyRefType)
	{
		this(initialCapacity, keyRefType, DEFAULT_LOAD_FACTOR);
	}

	/**
	 * ����һ���յ�<tt>SynHashMap</tt>.
	 */
	public SynHashMap(int initialCapacity)
	{
		this(initialCapacity, HARD, DEFAULT_LOAD_FACTOR);
	}

	/**
	 * ����һ���յ�<tt>SynHashMap</tt>.
	 */
	public SynHashMap()
	{
		this.keyRefType = HARD;
		this.loadFactor = DEFAULT_LOAD_FACTOR;
		threshold = (int) (DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
		table = new SynEntry[DEFAULT_INITIAL_CAPACITY];
		init();
	}

	/**
	 * ͨ��һ�����е�<tt>Map</tt>����һ���µ�<tt>SynHashMap</tt>.
	 */
	public SynHashMap(Map m, int keyRefType)
	{
		this(Math.max((int) (m.size() / DEFAULT_LOAD_FACTOR) + 1, DEFAULT_INITIAL_CAPACITY),
				keyRefType, DEFAULT_LOAD_FACTOR);
		putAllForCreate(m);
	}

	/**
	 * ͨ��һ�����е�<tt>Map</tt>����һ���µ�<tt>SynHashMap</tt>.
	 */
	public SynHashMap(Map m)
	{
		this(Math.max((int) (m.size() / DEFAULT_LOAD_FACTOR) + 1, DEFAULT_INITIAL_CAPACITY),
				HARD, DEFAULT_LOAD_FACTOR);
		putAllForCreate(m);
	}

	// internal utilities

	protected void init()
	{
	}

	protected SynEntry[] getTable()
	{
		this.expungeStaleEntries();
		return this.table;
	}

	/**
	 * ���key����Ӳ����, ����Ҫ�����ڵ�����.
	 */
   protected void expungeStaleEntries()
	{
		if (this.keyRefType > HARD)
		{
			this.expungeStaleEntries0();
		}
   }
   protected synchronized void expungeStaleEntries0()
	{
		SynEntry[] tmpTable = this.table;
		Object r;
		int hash;
		while ((r = this.queue.poll()) != null)
		{
			SynEntry entry = ((Ref) r).getEntry();
			hash = entry.hash;
			int i = indexFor(hash, tmpTable.length);
			SynEntry prev = tmpTable[i];
			SynEntry e = prev;
			while (e != null)
			{
				SynEntry next = e.next;
				if (entry.sameEntry(e))
				{
					this.size--;
					if (prev == e)
					{
						tmpTable[i] = next;
					}
					else
					{
						prev.next = next;
					}
					e.recordRemoval(this);
					break;
				}
				prev = e;
				e = next;
			}
		}
   }

	protected static int hash(int h)
	{
		h ^= (h >>> 20) ^ (h >>> 12);
		return h ^ (h >>> 7) ^ (h >>> 4);
	}

	protected static int indexFor(int h, int length)
	{
		return h & (length - 1);
	}

	/**
	 * �Żص�ǰmap������.
	 */
	public int size()
	{
		if (this.size == 0)
		{
			return this.size;
		}
		this.expungeStaleEntries();
		return this.size;
	}

	/**
	 * ���ص�ǰmap�Ƿ�Ϊ��.
	 */
	public boolean isEmpty()
	{
		return this.size() == 0;
	}

	/**
	 * ����key��ȡ��Ӧ��ֵ.
	 */
	public Object get(Object key)
	{
		if (key == null)
		{
			return getForNullKey();
		}
		int hash = hash(key.hashCode());
		SynEntry[] tmpTable = this.getTable();
		for (SynEntry e = tmpTable[indexFor(hash, tmpTable.length)]; e != null; e = e.next)
		{
			Object k;
			if (e.hash == hash && ((k = e.getKey()) == key || key.equals(k)))
			{
				return e.value;
			}
		}
		return null;
	}

	/**
	 * ��ȡkeyΪnull��ֵ.
	 */
	protected Object getForNullKey()
	{
		SynEntry[] tmpTable = this.getTable();
		for (SynEntry e = tmpTable[0]; e != null; e = e.next)
		{
			if (e.getKey() == null)
			{
				return e.value;
			}
		}
		return null;
	}

	/**
	 * �ж��Ƿ����ָ���Ŀ���ֵ.
	 */
	public boolean containsKey(Object key)
	{
		return this.getEntry(key) != null;
	}

	/**
	 * ����key��ȡEntry.
	 */
	protected SynEntry getEntry(Object key)
	{
		int hash = (key == null) ? 0 : hash(key.hashCode());
		SynEntry[] tmpTable = this.table;
		for (SynEntry e = tmpTable[indexFor(hash, tmpTable.length)]; e != null; e = e.next)
		{
			Object k;
			if (e.hash == hash && ((k = e.getKey()) == key || (key != null && key.equals(k))))
			{
				return e;
			}
		}
		return null;
	}


	/**
	 * ���һ��key value��.
	 */
	public synchronized Object put(Object key, Object value)
	{
		if (key == null)
		{
			return putForNullKey(value);
		}
		int hash = hash(key.hashCode());
		SynEntry[] tmpTable = this.getTable();
		int i = indexFor(hash, tmpTable.length);
		for (SynEntry e = tmpTable[i]; e != null; e = e.next)
		{
			Object k;
			if (e.hash == hash && ((k = e.getKey()) == key || key.equals(k)))
			{
				Object oldValue = e.value;
				e.value = value;
				e.recordAccess(this);
				return oldValue;
			}
		}
		modCount++;
		this.addEntry(hash, key, value, i);
		return null;
	}

	/**
	 * ���һ��keyΪnull��value.
	 */
	protected synchronized Object putForNullKey(Object value)
	{
		SynEntry[] tmpTable = this.getTable();
		for (SynEntry e = tmpTable[0]; e != null; e = e.next)
		{
			if (e.getKey() == null)
			{
				Object oldValue = e.value;
				e.value = value;
				e.recordAccess(this);
				return oldValue;
			}
		}
		modCount++;
		this.addEntry(0, null, value, 0);
		return null;
	}

	/**
	 * �����ڲ�ʹ�õ����key value��.
	 */
	protected synchronized void putForCreate(Object key, Object value)
	{
		int hash = (key == null) ? 0 : hash(key.hashCode());
		SynEntry[] tmpTable = this.table;
		int i = indexFor(hash, tmpTable.length);
		for (SynEntry e = tmpTable[i]; e != null; e = e.next)
		{
			Object k;
			if (e.hash == hash && ((k = e.getKey()) == key || (key != null && key.equals(k))))
			{
				e.value = value;
				return;
			}
		}
		this.createEntry(hash, key, value, i);
	}

	/**
	 * �����ڲ�ʹ��, ���map�е�key value��.
	 */
	protected void putAllForCreate(Map m)
	{
		for (Iterator i = m.entrySet().iterator(); i.hasNext();)
		{
			Map.Entry e = (Map.Entry) i.next();
			this.putForCreate(e.getKey(), e.getValue());
		}
	}

	/**
	 * �����µ�����, ���¹���hash��.
	 */
	protected synchronized void resize(int newCapacity)
	{
		SynEntry[] oldTable = this.table;
		int oldCapacity = oldTable.length;
		// ���������û�����ڵĴ�, ��ֱ���˳�
		if (newCapacity <= oldCapacity)
		{
			return;
		}
		if (oldCapacity == MAXIMUM_CAPACITY)
		{
			this.threshold = Integer.MAX_VALUE;
			return;
		}
		SynEntry[] newTable = new SynEntry[newCapacity];
		this.transfer(newTable);
		this.threshold = (int) (newCapacity * this.loadFactor);
		this.table = newTable;
	}

	/**
	 * �����е�Entry�Ƶ��µ�hash����.
	 */
	protected synchronized void transfer(SynEntry[] newTable)
	{
		SynEntry[] src = this.table;
		int newCapacity = newTable.length;
		for (int j = 0; j < src.length; j++)
		{
			SynEntry e = src[j];
			if (e != null)
			{
				do
				{
					e = (SynEntry) e.clone();
					SynEntry next = e.next;
					int i = indexFor(e.hash, newCapacity);
					e.next = newTable[i];
					newTable[i] = e;
					e = next;
				} while (e != null);
			}
		}
	}

	/**
	 * ���map�е�key value��.
	 */
	public void putAll(Map m)
	{
		int numKeysToBeAdded = m.size();
		if (numKeysToBeAdded == 0)
		{
			return;
		}
		/*
		 * �����µ�����
		 */
		if (numKeysToBeAdded > threshold)
		{
			int targetCapacity = (int) (numKeysToBeAdded / loadFactor + 1);
			if (targetCapacity > MAXIMUM_CAPACITY)
			{
				targetCapacity = MAXIMUM_CAPACITY;
			}
			int newCapacity = table.length;
			while (newCapacity < targetCapacity)
			{
				newCapacity <<= 1;
			}
			if (newCapacity > table.length)
			{
				resize(newCapacity);
			}
		}
		for (Iterator i = m.entrySet().iterator(); i.hasNext();)
		{
			Map.Entry e = (Map.Entry) i.next();
			this.put(e.getKey(), e.getValue());
		}
	}

	/**
	 * ����key�Ƴ�һ��value.
	 */
	public Object remove(Object key)
	{
		SynEntry e = this.removeEntryForKey(key);
		return (e == null ? null : e.value);
	}

	/**
	 * ����key�Ƴ�һ��Entry, �����䷵��.
	 */
	protected synchronized SynEntry removeEntryForKey(Object key)
	{
		int hash = (key == null) ? 0 : hash(key.hashCode());
		SynEntry[] tmpTable = this.getTable();
		int i = indexFor(hash, tmpTable.length);
		SynEntry prev = tmpTable[i];
		SynEntry e = prev;
		while (e != null)
		{
			SynEntry next = e.next;
			Object k;
			if (e.hash == hash && ((k = e.getKey()) == key || (key != null && key.equals(k))))
			{
				this.modCount++;
				this.size--;
				if (prev == e)
				{
					tmpTable[i] = next;
				}
				else
				{
					prev.next = next;
				}
				e.recordRemoval(this);
				return e;
			}
			prev = e;
			e = next;
		}
		return e;
	}

	/**
	 * ��EntrySetʹ�õ��Ƴ�.
	 */
	protected synchronized SynEntry removeMapping(Object o)
	{
		if (!(o instanceof Map.Entry))
		{
			return null;
		}

		Map.Entry entry = (Map.Entry) o;
		Object key = entry.getKey();
		int hash = (key == null) ? 0 : hash(key.hashCode());
		SynEntry[] tmpTable = this.getTable();
		int i = indexFor(hash, tmpTable.length);
		SynEntry prev = tmpTable[i];
		SynEntry e = prev;
		while (e != null)
		{
			SynEntry next = e.next;
			if (e.hash == hash && e.equals(entry))
			{
				this.modCount++;
				this.size--;
				if (prev == e)
				{
					tmpTable[i] = next;
				}
				else
				{
					prev.next = next;
				}
				e.recordRemoval(this);
				return e;
			}
			prev = e;
			e = next;
		}
		return e;
	}

	/**
	 * �Ƴ����е�����.
	 */
	public synchronized void clear()
	{
		this.modCount++;
		Map.Entry[] tab = this.table;
		for (int i = 0; i < tab.length; i++)
		{
			tab[i] = null;
		}
		this.size = 0;
		if (this.keyRefType > HARD)
		{
			// �Ƴ�����������ʧȥ���õĶ���
			while (this.queue.poll() != null);
		}
	}

	/**
	 * �Ƿ����ĳ��valueֵ.
	 */
	public boolean containsValue(Object value)
	{
		if (value == null)
		{
			return containsNullValue();
		}
		SynEntry[] tmpTable = this.getTable();
		for (int i = 0; i < tmpTable.length; i++)
		{
			for (SynEntry e = tmpTable[i]; e != null; e = e.next)
			{
				if (value.equals(e.value))
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * �ж��Ƿ����valueΪnull.
	 */
	protected boolean containsNullValue()
	{
		SynEntry[] tmpTable = this.getTable();
		for (int i = 0; i < tmpTable.length; i++)
		{
			for (SynEntry e = tmpTable[i]; e != null; e = e.next)
			{
				if (e.value == null)
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * clone��map.
	 */
	public Object clone()
	{
		SynHashMap result = null;
		try
		{
			result = (SynHashMap) super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			// assert false;
		}
		SynEntry[] tmpTable = this.getTable();
		result.table = new SynEntry[tmpTable.length];
		result.entrySet = null;
		result.keySet = null;
		result.values = null;
		result.modCount = 0;
		result.size = 0;
		result.init();
		result.putAllForCreate(this);
		return result;
	}


	protected static interface Ref
	{
	 	SynEntry getEntry();

		Object get();

	}

	private static class SoftRef extends SoftReference
			implements Ref
	{
		protected SynEntry entry;

		public SoftRef(Object r, SynEntry entry, ReferenceQueue q)
		{
			super(r, q);
			this.entry = entry;
		}

		public SynEntry getEntry()
		{
			return this.entry;
		}

	}

	private static class WeakRef extends WeakReference
			implements Ref
	{
		protected SynEntry entry;

		public WeakRef(Object r, SynEntry entry, ReferenceQueue q)
		{
			super(r, q);
			this.entry = entry;
		}

		public SynEntry getEntry()
		{
			return this.entry;
		}

	}

	protected static class SynEntry
			implements Map.Entry, Cloneable
	{
		protected final Object hardKey;
		protected final Ref refKey;
		protected Object value;
		protected SynEntry next;
		protected final int hash;

		/**
		 * Creates new entry.
		 */
		protected SynEntry(int h, Object k, Object v, SynEntry n, int keyRefType, ReferenceQueue q)
		{
			if (k == null || keyRefType == HARD)
			{
				this.hardKey = k;
				this.refKey = null;
			}
			else
			{
				this.hardKey = null;
				if (keyRefType == SOFT)
				{
					this.refKey = new SoftRef(k, this, q);
				}
				else
				{
					this.refKey = new WeakRef(k, this, q);
				}
			}
			this.value = v;
			this.next = n;
			this.hash = h;
		}

		public Object getKey()
		{
			return this.refKey == null ? this.hardKey : this.refKey.get();
		}

		public Object getValue()
		{
			return this.value;
		}

		public Object setValue(Object newValue)
		{
			Object oldValue = this.value;
			this.value = newValue;
			return oldValue;
		}

		public boolean sameEntry(SynEntry e)
		{
			return this.hardKey == e.hardKey && this.refKey == e.refKey && this.hash == e.hash;
		}

		public boolean equals(Object o)
		{
			if (!(o instanceof Map.Entry))
			{
				return false;
			}
			Map.Entry e = (Map.Entry) o;
			Object k1 = this.getKey();
			Object k2 = e.getKey();
			if (k1 == k2 || (k1 != null && k1.equals(k2)))
			{
				Object v1 = this.getValue();
				Object v2 = e.getValue();
				if (v1 == v2 || (v1 != null && v1.equals(v2)))
				{
					return true;
				}
			}
			return false;
		}

		public final int hashCode()
		{
			return (this.getKey() == null ? 0 : this.getKey().hashCode()) ^
					(this.getValue() == null ? 0 : this.getValue().hashCode());
		}

		public final String toString()
		{
			return this.getKey() + "=" + this.getValue();
		}

		protected Object clone()
		{
			try
			{
				return super.clone();
			}
			catch (CloneNotSupportedException ex)
			{
				// ��Ϊʵ����Cloneable, ���Բ����������쳣
				throw new Error(ex);
			}
		}

		/**
		 * ��ͨ��put(k,v)�޸���һ���Ѵ��ڵ�Entryʱ, ����ô˷���.
		 */
		protected void recordAccess(SynHashMap m)
		{
		}

		/**
		 * ��ͨ��remove�Ƴ���һ���Ѵ��ڵ�Entryʱ, ����ô˷���.
		 */
		protected void recordRemoval(SynHashMap m)
		{
		}

	}

	/**
	 * ���һ��Entry.
	 */
	protected synchronized void addEntry(int hash, Object key, Object value, int bucketIndex)
	{
		SynEntry e = this.table[bucketIndex];
		this.table[bucketIndex] = new SynEntry(hash, key, value, e, this.keyRefType, this.queue);
		if (this.size++ >= this.threshold)
		{
			this.resize(2 * this.table.length);
		}
	}

	/**
	 * ����һ��Entry, �ڳ�ʼ�� �����л� cloneʱʹ��.
	 */
	protected synchronized void createEntry(int hash, Object key, Object value, int bucketIndex)
	{
		SynEntry e = this.table[bucketIndex];
		this.table[bucketIndex] = new SynEntry(hash, key, value, e, this.keyRefType, this.queue);
		this.size++;
	}

	protected static class EntryIterator implements Iterator
	{
		protected SynEntry next;	          // next entry to return
		protected int expectedModCount;      // For fast-fail
		protected int index;                 // current slot
		protected SynEntry current;          // current entry
		protected SynHashMap map;            // map

		protected EntryIterator(SynHashMap map)
		{
			this.map = map;
			this.expectedModCount = this.map.modCount;
			SynEntry[] t = this.map.getTable();
			if (this.map.size > 0)
			{
				// advance to first entry
				while (this.index < t.length && (this.next = t[this.index++]) == null);
			}
		}

		public boolean hasNext()
		{
			return this.next != null;
		}

		protected SynEntry nextEntry()
		{
			if (this.map.modCount != this.expectedModCount)
			{
				throw new ConcurrentModificationException();
			}
			SynEntry e = this.next;
			if (e == null)
			{
				throw new NoSuchElementException();
			}

			if ((this.next = e.next) == null)
			{
				SynEntry[] t = this.map.getTable();
				while (this.index < t.length && (this.next = t[this.index++]) == null);
			}
			this.current = e;
			return e;
		}

		public Object next()
		{
			return this.nextEntry();
		}

		public void remove()
		{
			if (this.current == null)
			{
				throw new IllegalStateException();
			}
			if (this.map.modCount != expectedModCount)
			{
				throw new ConcurrentModificationException();
			}
			Object k = this.current.getKey();
			this.current = null;
			this.map.removeEntryForKey(k);
			this.expectedModCount = this.map.modCount;
		}

	}

	protected static class ValueIterator extends EntryIterator
	{
		public ValueIterator(SynHashMap map)
		{
			super(map);
		}

		public Object next()
		{
			return this.nextEntry().value;
		}
	}

	protected static class KeyIterator extends EntryIterator
	{
		public KeyIterator(SynHashMap map)
		{
			super(map);
		}

		public Object next()
		{
			return this.nextEntry().getKey();
		}
	}

	// Subclass overrides these to alter behavior of views' iterator() method
	protected Iterator newKeyIterator()
	{
		return new KeyIterator(this);
	}

	protected Iterator newValueIterator()
	{
		return new ValueIterator(this);
	}

	protected Iterator newEntryIterator()
	{
		return new EntryIterator(this);
	}


	// Views

	protected transient Set entrySet = null;
	protected transient Set keySet = null;
	protected transient Collection values = null;

	/**
	 * ��ȡkey�ļ���.
	 */
	public Set keySet()
	{
		Set ks = this.keySet;
		return (ks != null ? ks : (this.keySet = new KeySet(this)));
	}
	protected static class KeySet extends AbstractSet
	{
		protected SynHashMap map;            // map

		public KeySet(SynHashMap map)
		{
			this.map = map;
		}

		public Iterator iterator()
		{
			return this.map.newKeyIterator();
		}

		public int size()
		{
			return this.map.size();
		}

		public boolean contains(Object o)
		{
			return this.map.containsKey(o);
		}

		public boolean remove(Object o)
		{
			return this.map.removeEntryForKey(o) != null;
		}

		public void clear()
		{
			this.map.clear();
		}

	}

	/**
	 * ��ȡvalue�ļ���.
	 */
	public Collection values()
	{
		Collection vs = this.values;
		return (vs != null ? vs : (this.values = new Values(this)));
	}
	protected static class Values extends AbstractCollection
	{
		protected SynHashMap map;            // map

		public Values(SynHashMap map)
		{
			this.map = map;
		}

		public Iterator iterator()
		{
			return this.map.newValueIterator();
		}

		public int size()
		{
			return this.map.size();
		}

		public boolean contains(Object o)
		{
			return this.map.containsValue(o);
		}

		public void clear()
		{
			this.map.clear();
		}

	}

	/**
	 * ��ȡEntry����.
	 */
	public Set entrySet()
	{
		Set es = this.entrySet;
		return es != null ? es : (this.entrySet = new EntrySet(this));
	}
	protected static class EntrySet extends AbstractSet
	{
		protected SynHashMap map;            // map

		public EntrySet(SynHashMap map)
		{
			this.map = map;
		}

		public Iterator iterator()
		{
			return this.map.newEntryIterator();
		}

		public boolean contains(Object o)
		{
			if (!(o instanceof Map.Entry))
			{
				return false;
			}
			Map.Entry e = (Map.Entry) o;
			Map.Entry candidate = this.map.getEntry(e.getKey());
			return candidate != null && candidate.equals(e);
		}

		public boolean remove(Object o)
		{
			return this.map.removeMapping(o) != null;
		}

		public int size()
		{
			return this.map.size();
		}

		public void clear()
		{
			this.map.clear();
		}

	}

	/**
	 * ���˶������л�.
	 */
	private synchronized void writeObject(java.io.ObjectOutputStream s)
			throws IOException
	{
		this.expungeStaleEntries();
		Iterator i = (this.size > 0) ? this.entrySet().iterator() : null;
		// ���һЩĬ�ϵ�ֵ
		s.defaultWriteObject();
		// ���hash��Ĵ�С
		s.writeInt(this.table.length);
		// �����ǰ������
		s.writeInt(this.size);
		// ���ÿ��Entry
		if (i != null)
		{
			while (i.hasNext())
			{
				Map.Entry e = (Map.Entry) i.next();
				s.writeObject(e.getKey());
				s.writeObject(e.getValue());
			}
		}
	}

	/**
	 * ���˶������л�.
	 */
	private synchronized void readObject(java.io.ObjectInputStream s)
			throws IOException, ClassNotFoundException
	{
		// ����Ĭ��ֵ
		s.defaultReadObject();
		// ����hash��Ĵ�С
		int numBuckets = s.readInt();
		this.table = new SynEntry[numBuckets];
		init();   // ִ�г�ʼ��
		// ��������
		int size = s.readInt();
		// ����ÿ��Entry
		for (int i = 0; i < size; i++)
		{
			Object key = s.readObject();
			Object value = s.readObject();
			this.putForCreate(key, value);
		}
	}

}
