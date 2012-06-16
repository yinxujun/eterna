
package self.micromagic.eterna.sql.impl;

import java.io.InputStream;
import java.io.Reader;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.SQLException;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.security.Permission;
import self.micromagic.eterna.sql.ResultIterator;
import self.micromagic.eterna.sql.ResultReader;
import self.micromagic.eterna.sql.ResultReaderManager;
import self.micromagic.eterna.sql.ResultRow;

public class ResultRowImpl implements ResultRow
{
   private Object[] values;
   private Permission permission;
   private ResultReaderManager readerManager;
   private ResultIterator resultIterator;

   private String[] formateds;
   private boolean lastReadWasNull = false;

   public ResultRowImpl(Object[] values, ResultIterator resultIterator,
         ResultReaderManager readerManager, Permission permission)
   {
      this.values = values;
      this.formateds = new String[values.length];
      this.readerManager = readerManager;
      this.permission = permission;
      this.resultIterator = resultIterator;
   }

   public ResultIterator getResultIterator()
   {
      return this.resultIterator;
   }

   public String getFormated(int columnIndex)
         throws ConfigurationException
   {
      this.lastReadWasNull = this.values[columnIndex - 1] == null;
      if (this.values[columnIndex - 1] == null)
      {
         return "";
      }
      if (this.formateds[columnIndex - 1] != null)
      {
         return this.formateds[columnIndex - 1];
      }
      ResultReader reader = this.readerManager.getReaderInList(columnIndex - 1);
      if (reader.getFormat() == null)
      {
         this.formateds[columnIndex - 1] = QueryAdapterImpl.strConvert.convertToString(this.values[columnIndex - 1]);
         return this.formateds[columnIndex - 1];
      }
      try
      {
         this.formateds[columnIndex - 1] = reader.getFormat().format(
               this.values[columnIndex - 1], this, this.permission);
      }
      catch (Exception ex)
      {
        SQLManager.log.error("When format the column [" + columnIndex
              + "], value ["  + this.values[columnIndex - 1] + "]", ex);
      }
      return this.formateds[columnIndex - 1];
   }

   public String getFormated(String columnName)
         throws ConfigurationException
   {
      int index = this.readerManager.getIndexByName(columnName);
      return this.getFormated(index);
   }

   public boolean wasNull()
   {
      return this.lastReadWasNull;
   }

   public String getString(int columnIndex)
         throws ConfigurationException
   {
      this.lastReadWasNull = this.values[columnIndex - 1] == null;
      return QueryAdapterImpl.strConvert.convertToString(this.values[columnIndex - 1]);
   }

   public boolean getBoolean(int columnIndex)
         throws ConfigurationException
   {
      this.lastReadWasNull = this.values[columnIndex - 1] == null;
      return QueryAdapterImpl.boolConvert.getResult(this.values[columnIndex - 1]);
   }

   public byte getByte(int columnIndex)
         throws ConfigurationException
   {
      this.lastReadWasNull = this.values[columnIndex - 1] == null;
      return QueryAdapterImpl.byteConvert.getResult(this.values[columnIndex - 1]);
   }

   public short getShort(int columnIndex)
         throws ConfigurationException
   {
      this.lastReadWasNull = this.values[columnIndex - 1] == null;
      return QueryAdapterImpl.shortConvert.getResult(this.values[columnIndex - 1]);
   }

   public int getInt(int columnIndex)
         throws ConfigurationException
   {
      this.lastReadWasNull = this.values[columnIndex - 1] == null;
      return QueryAdapterImpl.intConvert.getResult(this.values[columnIndex - 1]);
   }

   public long getLong(int columnIndex)
         throws ConfigurationException
   {
      this.lastReadWasNull = this.values[columnIndex - 1] == null;
      return QueryAdapterImpl.longConvert.getResult(this.values[columnIndex - 1]);
   }

   public float getFloat(int columnIndex)
         throws ConfigurationException
   {
      this.lastReadWasNull = this.values[columnIndex - 1] == null;
      return QueryAdapterImpl.floatConvert.getResult(this.values[columnIndex - 1]);
   }

   public double getDouble(int columnIndex)
         throws ConfigurationException
   {
      this.lastReadWasNull = this.values[columnIndex - 1] == null;
      return QueryAdapterImpl.doubleConvert.getResult(this.values[columnIndex - 1]);
   }

   public byte[] getBytes(int columnIndex)
         throws ConfigurationException
   {
      this.lastReadWasNull = this.values[columnIndex - 1] == null;
      return QueryAdapterImpl.bytesConvert.getResult(this.values[columnIndex - 1]);
   }

   public Date getDate(int columnIndex)
         throws ConfigurationException
   {
      this.lastReadWasNull = this.values[columnIndex - 1] == null;
      return QueryAdapterImpl.dateConvert.getResult(this.values[columnIndex - 1]);
   }

   public Time getTime(int columnIndex)
         throws ConfigurationException
   {
      this.lastReadWasNull = this.values[columnIndex - 1] == null;
      return QueryAdapterImpl.timeConvert.getResult(this.values[columnIndex - 1]);
   }

   public Timestamp getTimestamp(int columnIndex)
         throws ConfigurationException
   {
      this.lastReadWasNull = this.values[columnIndex - 1] == null;
      return QueryAdapterImpl.timestampConvert.getResult(this.values[columnIndex - 1]);
   }

   public String getString(String columnName)
         throws ConfigurationException
   {
      int index = this.readerManager.getIndexByName(columnName);
      return this.getString(index);
   }

   public boolean getBoolean(String columnName)
         throws ConfigurationException
   {
      int index = this.readerManager.getIndexByName(columnName);
      return this.getBoolean(index);
   }

   public byte getByte(String columnName)
         throws ConfigurationException
   {
      int index = this.readerManager.getIndexByName(columnName);
      return this.getByte(index);
   }

   public short getShort(String columnName)
         throws ConfigurationException
   {
      int index = this.readerManager.getIndexByName(columnName);
      return this.getShort(index);
   }

   public int getInt(String columnName)
         throws ConfigurationException
   {
      int index = this.readerManager.getIndexByName(columnName);
      return this.getInt(index);
   }

   public long getLong(String columnName)
         throws ConfigurationException
   {
      int index = this.readerManager.getIndexByName(columnName);
      return this.getLong(index);
   }

   public float getFloat(String columnName)
         throws ConfigurationException
   {
      int index = this.readerManager.getIndexByName(columnName);
      return this.getFloat(index);
   }

   public double getDouble(String columnName)
         throws ConfigurationException
   {
      int index = this.readerManager.getIndexByName(columnName);
      return this.getDouble(index);
   }

   public byte[] getBytes(String columnName)
         throws ConfigurationException
   {
      int index = this.readerManager.getIndexByName(columnName);
      return this.getBytes(index);
   }

   public Date getDate(String columnName)
         throws ConfigurationException
   {
      int index = this.readerManager.getIndexByName(columnName);
      return this.getDate(index);
   }

   public Time getTime(String columnName)
         throws ConfigurationException
   {
      int index = this.readerManager.getIndexByName(columnName);
      return this.getTime(index);
   }

   public Timestamp getTimestamp(String columnName)
         throws ConfigurationException
   {
      int index = this.readerManager.getIndexByName(columnName);
      return this.getTimestamp(index);
   }

   public Object getObject(int readerIndex)
   {
      this.lastReadWasNull = this.values[readerIndex - 1] == null;
      return this.values[readerIndex - 1];
   }

   public Object getObject(String readerName)
         throws ConfigurationException
   {
      int index = this.readerManager.getIndexByName(readerName);
      return this.getObject(index);
   }

   public Object getObject(String readerName, boolean notThrow)
         throws SQLException, ConfigurationException
   {
      int index = this.readerManager.getIndexByName(readerName, notThrow);
      if (index == -1)
      {
         return null;
      }
      return this.getObject(index);
   }

   public InputStream getBinaryStream(int columnIndex)
         throws ConfigurationException
   {
      this.lastReadWasNull = this.values[columnIndex - 1] == null;
      return QueryAdapterImpl.streamConvert.getResult(this.values[columnIndex - 1]);
   }

   public InputStream getBinaryStream(String columnName)
         throws ConfigurationException
   {
      int index = this.readerManager.getIndexByName(columnName);
      return this.getBinaryStream(index);
   }

   public Reader getCharacterStream(int columnIndex)
         throws ConfigurationException
   {
      this.lastReadWasNull = this.values[columnIndex - 1] == null;
      return QueryAdapterImpl.readerConvert.getResult(this.values[columnIndex - 1]);
   }

   public Reader getCharacterStream(String columnName)
         throws ConfigurationException
   {
      int index = this.readerManager.getIndexByName(columnName);
      return this.getCharacterStream(index);
   }

   public int findColumn(String columnName)
         throws ConfigurationException
   {
      return this.readerManager.getIndexByName(columnName);
   }

   public int findColumn(String columnName, boolean notThrow)
         throws ConfigurationException
   {
      return this.readerManager.getIndexByName(columnName, notThrow);
   }

}
