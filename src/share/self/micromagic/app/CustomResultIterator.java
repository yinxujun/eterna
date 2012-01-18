
package self.micromagic.app;

import java.util.List;
import java.util.LinkedList;

import self.micromagic.eterna.sql.ResultIterator;
import self.micromagic.eterna.sql.ResultRow;
import self.micromagic.eterna.sql.ResultMetaData;
import self.micromagic.eterna.sql.impl.ResultMetaDataImpl;
import self.micromagic.eterna.sql.impl.AbstractResultIterator;

public class CustomResultIterator extends AbstractResultIterator
      implements ResultIterator
{
   private int recordCount = 0;

   public CustomResultIterator(List readerList)
   {
      super(readerList);
      this.result = new LinkedList();
   }

   public void addRow(ResultRow row)
   {
      this.result.add(row);
   }

   public void addedOver()
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

}
