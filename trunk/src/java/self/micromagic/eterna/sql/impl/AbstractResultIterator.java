
package self.micromagic.eterna.sql.impl;

import java.util.List;
import java.util.Iterator;
import java.util.LinkedList;
import java.sql.SQLException;

import self.micromagic.eterna.sql.ResultIterator;
import self.micromagic.eterna.sql.ResultRow;
import self.micromagic.eterna.sql.ResultMetaData;
import self.micromagic.eterna.sql.ResultReaderManager;
import self.micromagic.eterna.sql.QueryAdapter;
import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.security.Permission;

/**
 * @author micromagic@sina.com
 */
public abstract class AbstractResultIterator
      implements ResultIterator
{
   protected List result;
   protected Iterator resultItr;
   protected List readerList;
   protected ResultReaderManager readerManager;
   protected List preFetchList;
   protected ResultRow currentRow;
   protected ResultMetaData metaData;
   protected QueryAdapter query;

   public AbstractResultIterator(List readerList)
   {
      this.readerList = readerList;
   }

	public AbstractResultIterator(ResultReaderManager readerManager, Permission permission)
			throws ConfigurationException
	{
		this.readerManager = readerManager;
		this.readerList = readerManager.getReaderList(permission);
	}

	protected AbstractResultIterator()
	{
	}

	public ResultMetaData getMetaData()
			throws SQLException, ConfigurationException
	{
		if (this.metaData == null)
		{
			this.metaData = new ResultMetaDataImpl(this.readerList, this.readerManager, this.query);
		}
		return this.metaData;
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

   protected void copy(ResultIterator copyObj)
         throws ConfigurationException
   {
		AbstractResultIterator other = (AbstractResultIterator) copyObj;
		other.result = this.result;
		other.resultItr = this.resultItr;
		other.readerList = this.readerList;
		other.readerManager = this.readerManager;
		other.preFetchList = this.preFetchList;
		other.currentRow = this.currentRow;
		other.metaData = this.metaData;
		other.query = this.query;
		other.beforeFirst();
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
