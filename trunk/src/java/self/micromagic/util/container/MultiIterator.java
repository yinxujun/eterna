
package self.micromagic.util.container;

import java.util.Iterator;

import self.micromagic.util.container.UnmodifiableIterator;

public class MultiIterator
      implements Iterator
{
   private Iterator firstItr;
   private Iterator secondItr;

   private boolean readOnly;

   public MultiIterator(Iterator firstItr, Iterator secondItr)
   {
      this(firstItr, secondItr, false);
   }

   public MultiIterator(Iterator firstItr, Iterator secondItr, boolean readOnly)
   {
      this.firstItr = firstItr == null ? UnmodifiableIterator.EMPTY_ITERATOR : firstItr;
      this.secondItr = secondItr == null ? UnmodifiableIterator.EMPTY_ITERATOR : secondItr;
      this.readOnly = readOnly;
   }

   public boolean hasNext()
   {
      return this.firstItr.hasNext() ? true : this.secondItr.hasNext();
   }

   public Object next()
   {
      return this.firstItr.hasNext() ? this.firstItr.next() : this.secondItr.next();
   }

   public void remove()
   {
      if (this.readOnly)
      {
         throw new UnsupportedOperationException();
      }
      if (this.firstItr.hasNext())
      {
         this.firstItr.remove();
      }
      else
      {
         this.secondItr.remove();
      }
   }

}

