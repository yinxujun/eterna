/*
 * Copyright 2009-2015 xinjunli (micromagic@sina.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package self.micromagic.eterna.sql.impl;

import java.io.InputStream;
import java.io.Reader;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.security.Permission;
import self.micromagic.eterna.sql.ResultIterator;
import self.micromagic.eterna.sql.ResultMetaData;
import self.micromagic.eterna.sql.ResultReader;
import self.micromagic.eterna.sql.ResultRow;
import self.micromagic.util.StringAppender;
import self.micromagic.util.StringTool;
import self.micromagic.util.converter.BooleanConverter;
import self.micromagic.util.converter.ByteConverter;
import self.micromagic.util.converter.BytesConverter;
import self.micromagic.util.converter.DateConverter;
import self.micromagic.util.converter.DoubleConverter;
import self.micromagic.util.converter.FloatConverter;
import self.micromagic.util.converter.IntegerConverter;
import self.micromagic.util.converter.LongConverter;
import self.micromagic.util.converter.ReaderConverter;
import self.micromagic.util.converter.ShortConverter;
import self.micromagic.util.converter.StreamConverter;
import self.micromagic.util.converter.StringConverter;
import self.micromagic.util.converter.TimeConverter;
import self.micromagic.util.converter.TimestampConverter;

public class ResultRowImpl implements ResultRow
{
	private Object[] values;
	private Permission permission;
	private ResultMetaData metaData;
	private ResultIterator resultIterator;

	private String[] formateds;
	private boolean wasNull;

	public ResultRowImpl(Object[] values, ResultIterator resultIterator, Permission permission)
			throws ConfigurationException, SQLException
	{
		this.values = values;
		this.formateds = new String[values.length];
		this.permission = permission;
		this.resultIterator = resultIterator;
		this.metaData = resultIterator.getMetaData();
	}

	public ResultIterator getResultIterator()
	{
		return this.resultIterator;
	}

	public String getFormated(int columnIndex)
			throws ConfigurationException
	{
		int cIndex = columnIndex - 1;
		if (this.values[cIndex] == null)
		{
			this.wasNull = true;
			if (this.formateds[cIndex] != null)
			{
				return this.formateds[cIndex];
			}
			ResultReader reader = this.metaData.getColumnReader(columnIndex);
			if (reader.getFormat() != null)
			{
				String tmp = null;
				try
				{
					tmp = reader.getFormat().format(null, this, this.permission);
				}
				catch (Exception ex)
				{
					  SQLManager.log.warn(this.getFormatErrMsg(columnIndex), ex);
				}
				return tmp == null ? (this.formateds[cIndex] = "") : (this.formateds[cIndex] = tmp);
			}
			return this.formateds[cIndex] = "";
		}
		this.wasNull = false;
		if (this.formateds[cIndex] != null)
		{
			return this.formateds[cIndex];
		}
		ResultReader reader = this.metaData.getColumnReader(columnIndex);
		if (reader.getFormat() == null)
		{
			this.formateds[cIndex] = strConvert.convertToString(this.values[cIndex]);
			return this.formateds[cIndex];
		}
		String tmp = null;
		try
		{
			tmp = reader.getFormat().format(this.values[cIndex], this, this.permission);
		}
		catch (Exception ex)
		{
			  SQLManager.log.warn(this.getFormatErrMsg(columnIndex), ex);
		}
		return tmp == null ? (this.formateds[cIndex] = "") : (this.formateds[cIndex] = tmp);
	}

	public String getFormated(String columnName)
			throws ConfigurationException
	{
		int index = this.metaData.findColumn(columnName, false);
		return this.getFormated(index);
	}

	private String getFormatErrMsg(int columnIndex)
			throws ConfigurationException
	{
		StringAppender buf = StringTool.createStringAppender(128);
		buf.append("When format the column [").append(columnIndex).append(':')
				.append(this.metaData.getColumnReader(columnIndex).getName())
				.append("] in ").append(this.metaData.getName())
				.append(", value [").append(this.values[columnIndex - 1]).append("].");
		return buf.toString();
	}

	public boolean wasNull()
	{
		return this.wasNull;
	}

	public String getString(int columnIndex)
			throws ConfigurationException
	{
		this.wasNull = this.values[columnIndex - 1] == null;
		return strConvert.convertToString(this.values[columnIndex - 1]);
	}

	public boolean getBoolean(int columnIndex)
			throws ConfigurationException
	{
		this.wasNull = this.values[columnIndex - 1] == null;
		return boolConvert.getResult(this.values[columnIndex - 1]);
	}

	public byte getByte(int columnIndex)
			throws ConfigurationException
	{
		this.wasNull = this.values[columnIndex - 1] == null;
		return byteConvert.getResult(this.values[columnIndex - 1]);
	}

	public short getShort(int columnIndex)
			throws ConfigurationException
	{
		this.wasNull = this.values[columnIndex - 1] == null;
		return shortConvert.getResult(this.values[columnIndex - 1]);
	}

	public int getInt(int columnIndex)
			throws ConfigurationException
	{
		this.wasNull = this.values[columnIndex - 1] == null;
		return intConvert.getResult(this.values[columnIndex - 1]);
	}

	public long getLong(int columnIndex)
			throws ConfigurationException
	{
		this.wasNull = this.values[columnIndex - 1] == null;
		return longConvert.getResult(this.values[columnIndex - 1]);
	}

	public float getFloat(int columnIndex)
			throws ConfigurationException
	{
		this.wasNull = this.values[columnIndex - 1] == null;
		return floatConvert.getResult(this.values[columnIndex - 1]);
	}

	public double getDouble(int columnIndex)
			throws ConfigurationException
	{
		this.wasNull = this.values[columnIndex - 1] == null;
		return doubleConvert.getResult(this.values[columnIndex - 1]);
	}

	public byte[] getBytes(int columnIndex)
			throws ConfigurationException
	{
		this.wasNull = this.values[columnIndex - 1] == null;
		return bytesConvert.getResult(this.values[columnIndex - 1]);
	}

	public Date getDate(int columnIndex)
			throws ConfigurationException
	{
		this.wasNull = this.values[columnIndex - 1] == null;
		return dateConvert.getResult(this.values[columnIndex - 1]);
	}

	public Time getTime(int columnIndex)
			throws ConfigurationException
	{
		this.wasNull = this.values[columnIndex - 1] == null;
		return timeConvert.getResult(this.values[columnIndex - 1]);
	}

	public Timestamp getTimestamp(int columnIndex)
			throws ConfigurationException
	{
		this.wasNull = this.values[columnIndex - 1] == null;
		return timestampConvert.getResult(this.values[columnIndex - 1]);
	}

	public String getString(String columnName)
			throws ConfigurationException
	{
		int index = this.metaData.findColumn(columnName, false);
		return this.getString(index);
	}

	public boolean getBoolean(String columnName)
			throws ConfigurationException
	{
		int index = this.metaData.findColumn(columnName, false);
		return this.getBoolean(index);
	}

	public byte getByte(String columnName)
			throws ConfigurationException
	{
		int index = this.metaData.findColumn(columnName, false);
		return this.getByte(index);
	}

	public short getShort(String columnName)
			throws ConfigurationException
	{
		int index = this.metaData.findColumn(columnName, false);
		return this.getShort(index);
	}

	public int getInt(String columnName)
			throws ConfigurationException
	{
		int index = this.metaData.findColumn(columnName, false);
		return this.getInt(index);
	}

	public long getLong(String columnName)
			throws ConfigurationException
	{
		int index = this.metaData.findColumn(columnName, false);
		return this.getLong(index);
	}

	public float getFloat(String columnName)
			throws ConfigurationException
	{
		int index = this.metaData.findColumn(columnName, false);
		return this.getFloat(index);
	}

	public double getDouble(String columnName)
			throws ConfigurationException
	{
		int index = this.metaData.findColumn(columnName, false);
		return this.getDouble(index);
	}

	public byte[] getBytes(String columnName)
			throws ConfigurationException
	{
		int index = this.metaData.findColumn(columnName, false);
		return this.getBytes(index);
	}

	public Date getDate(String columnName)
			throws ConfigurationException
	{
		int index = this.metaData.findColumn(columnName, false);
		return this.getDate(index);
	}

	public Time getTime(String columnName)
			throws ConfigurationException
	{
		int index = this.metaData.findColumn(columnName, false);
		return this.getTime(index);
	}

	public Timestamp getTimestamp(String columnName)
			throws ConfigurationException
	{
		int index = this.metaData.findColumn(columnName, false);
		return this.getTimestamp(index);
	}

	public Object getObject(int columnIndex)
	{
		this.wasNull = this.values[columnIndex - 1] == null;
		return this.values[columnIndex - 1];
	}

	public Object getObject(String columnName)
			throws ConfigurationException
	{
		int index = this.metaData.findColumn(columnName, false);
		return this.getObject(index);
	}

	public Object getObject(String columnName, boolean notThrow)
			throws SQLException, ConfigurationException
	{
		int index = this.metaData.findColumn(columnName, notThrow);
		if (index == -1)
		{
			return null;
		}
		return this.getObject(index);
	}

	public InputStream getBinaryStream(int columnIndex)
			throws ConfigurationException
	{
		this.wasNull = this.values[columnIndex - 1] == null;
		return streamConvert.getResult(this.values[columnIndex - 1]);
	}

	public InputStream getBinaryStream(String columnName)
			throws ConfigurationException
	{
		int index = this.metaData.findColumn(columnName, false);
		return this.getBinaryStream(index);
	}

	public Reader getCharacterStream(int columnIndex)
			throws ConfigurationException
	{
		this.wasNull = this.values[columnIndex - 1] == null;
		return readerConvert.getResult(this.values[columnIndex - 1]);
	}

	public Reader getCharacterStream(String columnName)
			throws ConfigurationException
	{
		int index = this.metaData.findColumn(columnName, false);
		return this.getCharacterStream(index);
	}

	public int findColumn(String columnName)
			throws ConfigurationException
	{
		return this.metaData.findColumn(columnName, false);
	}

	public int findColumn(String columnName, boolean notThrow)
			throws ConfigurationException
	{
		return this.metaData.findColumn(columnName, notThrow);
	}

	static StringConverter strConvert = new StringConverter();
	static BooleanConverter boolConvert = new BooleanConverter();
	static ByteConverter byteConvert = new ByteConverter();
	static ShortConverter shortConvert = new ShortConverter();
	static IntegerConverter intConvert = new IntegerConverter();
	static LongConverter longConvert = new LongConverter();
	static FloatConverter floatConvert = new FloatConverter();
	static DoubleConverter doubleConvert = new DoubleConverter();
	static BytesConverter bytesConvert = new BytesConverter();
	static DateConverter dateConvert = new DateConverter();
	static TimeConverter timeConvert = new TimeConverter();
	static TimestampConverter timestampConvert = new TimestampConverter();
	static StreamConverter streamConvert = new StreamConverter();
	static ReaderConverter readerConvert = new ReaderConverter();

}