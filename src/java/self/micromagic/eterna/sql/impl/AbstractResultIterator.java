
package self.micromagic.eterna.sql.impl;

import java.util.List;
import java.util.Iterator;
import java.util.LinkedList;

import self.micromagic.eterna.sql.ResultIterator;
import self.micromagic.eterna.sql.ResultRow;
import self.micromagic.eterna.digester.ConfigurationException;

abstract public class AbstractResultIterator
      implements ResultIterator, Cloneable
{
   protected List result;
   protected Iterator resultItr = null;
   protected List readerList;
   protected List preFetchList = null;
   protected ResultRow currentRow = null;

   public AbstractResultIterator(List readerList)
   {
      this.readerList = readerList;
   }

   public boolean hasMoreRow()
   {
      if (this.preFetchList != null && this.preFetchList.size() > 0)
      {
         return true;
      }
      return this.resultItr.hasNext();
   }

   protected boolean hasMoreRow0()
   {
      return this.resultItr.hasNext();
   }

   public ResultRow preFetch()
   {
      return this.preFetch(1);
   }

   public ResultRow preFetch(int index)
   {
      if (this.preFetchList != null && this.preFetchList.size() >= index)
      {
         return (ResultRow) this.preFetchList.get(index - 1);
      }
      if (this.preFetchList == null)
      {
         this.preFetchList = new LinkedList();
      }
      for (int i = this.preFetchList.size(); i < index; i++)
      {
         if (this.hasMoreRow0())
         {
            this.preFetchList.add(this.nextRow0());
         }
         else
         {
            return null;
         }
      }
      return (ResultRow) this.preFetchList.get(index - 1);
   }

   public ResultRow getCurrentRow()
   {
      return this.currentRow;
   }

   public ResultRow nextRow()
   {
      if (this.preFetchList != null && this.preFetchList.size() > 0)
      {
         this.currentRow  = (ResultRow) this.preFetchList.remove(0);
         return this.currentRow ;
      }
      this.currentRow = (ResultRow) this.resultItr.next();
      return this.currentRow;
   }

   protected ResultRow nextRow0()
   {
      return (ResultRow) this.resultItr.next();
   }

   public boolean beforeFirst()
   {
      this.resultItr = this.result.iterator();
      this.preFetchList = null;
      this.currentRow = null;
      return true;
   }

   public void close()
   {
   }

   public ResultIterator copy()
         throws ConfigurationException
   {
      try
      {
         AbstractResultIterator newRitr = (AbstractResultIterator) this.clone();
         newRitr.beforeFirst();
         return newRitr;
      }
      catch (Exception ex)
      {
         throw new ConfigurationException(ex);
      }
   }

   public boolean hasNext()
   {
      return this.hasMoreRow();
   }

   public Object next()
   {
      return this.nextRow();
   }

   public void remove()
   {
      throw new UnsupportedOperationException();
   }

}
