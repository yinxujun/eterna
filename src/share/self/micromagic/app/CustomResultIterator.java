
package self.micromagic.app;

import java.util.LinkedList;
import java.util.List;
import java.sql.SQLException;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.security.Permission;
import self.micromagic.eterna.sql.ResultIterator;
import self.micromagic.eterna.sql.ResultReaderManager;
import self.micromagic.eterna.sql.ResultRow;
import self.micromagic.eterna.sql.impl.AbstractResultIterator;
import self.micromagic.eterna.sql.impl.ResultRowImpl;

/**
 * @deprecated
 * @see self.micromagic.util.CustomResultIterator
 */
public class CustomResultIterator extends AbstractResultIterator
      implements ResultIterator
{
   private int recordCount = 0;
   private Permission permission;

   /**
    * @deprecated
    * @see #CustomResultIterator(ResultReaderManager, Permission)
    */
   public CustomResultIterator(List readerList)
   {
      super(readerList);
      this.result = new LinkedList();
   }

   public CustomResultIterator(ResultReaderManager rrm, Permission permission)
         throws ConfigurationException
   {
      super(rrm, permission);
      this.permission = permission;
      this.result = new LinkedList();
   }

	private CustomResultIterator(Permission permission)
	{
		this.permission = permission;
	}

   public ResultRow createRow(Object[] values)
         throws ConfigurationException
   {
      return this.createRow(values, true);
   }

   public ResultRow createRow(Object[] values, boolean autoAdd)
         throws ConfigurationException
   {
      if (this.readerManager == null)
      {
         throw new ConfigurationException("Must use [CustomResultIterator(ResultReaderManager, Permission)] "
               + "to constructor this object.");
      }
		try
		{
      	ResultRow row = new ResultRowImpl(values, this, this.permission);
			if (autoAdd)
			{
				this.result.add(row);
			}
			return row;
		}
		catch (SQLException ex)
		{
			throw new ConfigurationException(ex);
		}
   }

   /**
    * @deprecated
    * @see #createRow(Object[])
    */
   public void addRow(ResultRow row)
   {
      this.result.add(row);
   }

   public void addedOver()
   {
      this.resultItr = this.result.iterator();
      this.recordCount = this.result.size();
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
		CustomResultIterator ritr = new CustomResultIterator(this.permission);
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
