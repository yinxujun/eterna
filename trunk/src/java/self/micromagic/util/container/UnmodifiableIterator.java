
package self.micromagic.util.container;

import java.util.Iterator;
import java.util.Enumeration;

import org.apache.commons.collections.iterators.EmptyIterator;
import org.apache.commons.collections.iterators.IteratorEnumeration;

/**
 * @author micromagic@sina.com
 */
public class UnmodifiableIterator
		implements Iterator
{
	public static final Iterator EMPTY_ITERATOR = EmptyIterator.INSTANCE;
	public static final Enumeration EMPTY_ENUMERATION = new IteratorEnumeration(EmptyIterator.INSTANCE);

	private Iterator itr;

	public UnmodifiableIterator(Iterator itr)
	{
		this.itr = itr == null ? EMPTY_ITERATOR : itr;
	}

	public boolean hasNext()
	{
		return this.itr.hasNext();
	}

	public Object next()
	{
		return this.itr.next();
	}

	public void remove()
	{
		throw new UnsupportedOperationException();
	}

}