
package self.micromagic.util;

import java.util.LinkedList;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.security.Permission;
import self.micromagic.eterna.sql.ResultIterator;
import self.micromagic.eterna.sql.ResultMetaData;
import self.micromagic.eterna.sql.ResultReaderManager;
import self.micromagic.eterna.sql.ResultRow;
import self.micromagic.eterna.sql.impl.AbstractResultIterator;
import self.micromagic.eterna.sql.impl.ResultMetaDataImpl;
import self.micromagic.eterna.sql.impl.ResultRowImpl;

public class CustomResultIterator extends AbstractResultIterator
      implements ResultIterator
{
   private int recordCount = 0;
   private ResultReaderManager rrm;
   private Permission permission;

   public CustomResultIterator(ResultReaderManager rrm, Permission permission)
         throws ConfigurationException
   {
      super(rrm.getReaderList());
      this.rrm = rrm;
      this.permission = permission;
      this.result = new LinkedList();
   }

   public ResultRow createRow(Object[] values)
         throws ConfigurationException
   {
      if (values.length != this.rrm.getReaderCount())
      {
         throw new ConfigurationException("The values count must same as the ResultReaderManager's readers count.");
      }
      ResultRow row = new ResultRowImpl(values, this, this.rrm, this.permission);
      this.result.add(row);
      return row;
   }

   public void finishCreateRow()
   {
      this.resultItr = this.result.iterator();
      this.recordCount = this.result.size();
   }

   public ResultMetaData getMetaData()
   {
      return new ResultMetaDataImpl(this.readerList, null);
   }

   public int getRealRecordCount()
   {
      return this.recordCount;
   }

   public int getRecordCount()
   {
      return this.recordCount;
   }

   public boolean isRealRecordCountAvailable()
   {
      return true;
   }

   public boolean isHasMoreRecord()
   {
      return false;
   }

	public ResultIterator copy()
			throws ConfigurationException
	{
		CustomResultIterator ritr = new CustomResultIterator(this.rrm, this.permission);
		this.copy(ritr);
		return ritr;
	}

	protected void copy(ResultIterator copyObj)
			throws ConfigurationException
	{
		super.copy(copyObj);
		CustomResultIterator ritr = (CustomResultIterator) copyObj;
		ritr.recordCount = this.recordCount;
	}

}
