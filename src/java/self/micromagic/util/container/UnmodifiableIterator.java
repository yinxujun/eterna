
package self.micromagic.util.container;

import java.util.Iterator;
import java.util.Enumeration;

import org.apache.commons.collections.iterators.EmptyIterator;
import org.apache.commons.collections.iterators.IteratorEnumeration;

public class UnmodifiableIterator
      implements Iterator
{
   public final static Iterator EMPTY_ITERATOR = EmptyIterator.INSTANCE;
   public final static Enumeration EMPTY_ENUMERATION = new IteratorEnumeration(EmptyIterator.INSTANCE);

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
