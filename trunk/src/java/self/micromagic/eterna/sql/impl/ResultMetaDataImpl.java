
package self.micromagic.eterna.sql.impl;

import java.util.Iterator;
import java.util.List;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.sql.ResultMetaData;
import self.micromagic.eterna.sql.ResultReader;
import self.micromagic.eterna.sql.QueryAdapter;

public class ResultMetaDataImpl
      implements ResultMetaData
{
   private ResultReader[] readers;
   private QueryAdapter query;

   public ResultMetaDataImpl(List readerList, QueryAdapter query)
   {
      this.query = query;
      int count = readerList.size();
      readers = new ResultReader[count];
      Iterator itr = readerList.iterator();
      for (int i = 0; i < readers.length; i++)
      {
         readers[i] = (ResultReader) itr.next();
      }
   }

   public QueryAdapter getQuery()
   {
      return this.query;
   }

   public int getColumnCount()
   {
      return this.readers.length;
   }

   public int getColumnWidth(int column)
         throws ConfigurationException
   {
      return this.readers[column - 1].getWidth();
   }

   public String getColumnCaption(int column)
         throws ConfigurationException
   {
      return this.readers[column - 1].getCaption();
   }

   public String getColumnName(int column)
         throws ConfigurationException
   {
      return this.readers[column - 1].getName();
   }

   public ResultReader getColumnReader(int column)
   {
      return this.readers[column - 1];
   }

}
