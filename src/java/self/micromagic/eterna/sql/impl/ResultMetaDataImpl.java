
package self.micromagic.eterna.sql.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.sql.ResultMetaData;
import self.micromagic.eterna.sql.ResultReader;
import self.micromagic.eterna.sql.QueryAdapter;
import self.micromagic.eterna.sql.ResultReaderManager;
import self.micromagic.util.Utility;

public class ResultMetaDataImpl
      implements ResultMetaData
{
   private String name;
   private ResultReader[] readers;
   private QueryAdapter query;
   private ResultReaderManager readerManager;
   private Map nameToIndexMap;
	private boolean colNameSensitive = true;

   public ResultMetaDataImpl(List readerList, ResultReaderManager readerManager, QueryAdapter query)
			throws ConfigurationException
	{
      this.query = query;
      int count = readerList.size();
      this.readers = new ResultReader[count];
      Iterator itr = readerList.iterator();
      for (int i = 0; i < this.readers.length; i++)
      {
         this.readers[i] = (ResultReader) itr.next();
      }
		this.readerManager = readerManager;
		if (readerManager == null || !readerManager.isLocked())
		{
			this.colNameSensitive = readerManager == null ? true : readerManager.isColNameSensitive();
			// ��readerManagerΪnull��δ����ʱ, ��Ҫ�����Լ�������ֵ��Ӧ��
			this.nameToIndexMap = new HashMap((int) (count * 1.5));
			for (int i = 0; i < this.readers.length; i++)
			{
				ResultReader r = this.readers[i];
				if (this.colNameSensitive)
				{
					this.nameToIndexMap.put(r.getName(), Utility.createInteger(i + 1));
				}
				else
				{
					this.nameToIndexMap.put(r.getName().toUpperCase(), Utility.createInteger(i + 1));
				}
			}
		}
		if (this.query != null)
		{
			this.name = "query [" + this.query.getName() + "]";
		}
		else if (this.readerManager != null)
		{
			this.name = "reader manager [" + this.readerManager.getName() + "]";
		}
		else
		{
			this.name = "unknow";
		}
   }

   public QueryAdapter getQuery()
   {
      return this.query;
   }

	public ResultReaderManager getReaderManager()
	{
		return this.readerManager;
	}

	public String getName()
	{
		return this.name;
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

	public int findColumn(String columnName)
			throws ConfigurationException
	{
		return this.findColumn(columnName, false);
	}

	public int findColumn(String columnName, boolean notThrow)
			throws ConfigurationException
	{
		if (this.nameToIndexMap == null)
		{
			return this.readerManager.getIndexByName(columnName, notThrow);
		}
		else
		{
			Integer i = (Integer) this.nameToIndexMap.get(
					this.colNameSensitive ? columnName : columnName.toUpperCase());
			if (i == null)
			{
				if (notThrow)
				{
					return -1;
				}
				else
				{
					throw new ConfigurationException(
							"Invalid column name:[" + columnName + "] at " + this.getName() + ".");
				}
			}
			return i.intValue();
		}
	}

}
