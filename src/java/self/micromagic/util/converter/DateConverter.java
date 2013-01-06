
package self.micromagic.util.converter;

import java.text.ParseException;
import java.text.DateFormat;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.TypeManager;
import self.micromagic.util.FormatTool;
import self.micromagic.util.StringRef;
import self.micromagic.util.ObjectRef;
import self.micromagic.util.container.RequestParameterMap;

public class DateConverter extends ObjectConverter
{
	private DateFormat dateFormat;

	public void setDateFormat(DateFormat dateFormat)
	{
		this.dateFormat = dateFormat;
	}

	public int getConvertType(StringRef typeName)
	{
		if (typeName != null)
		{
			typeName.setString("Date");
		}
		return TypeManager.TYPE_DATE;
	}

	public java.sql.Date getResult(Object result)
			throws ConfigurationException
	{
		try
		{
			return this.convertToDate(result);
		}
		catch (Exception ex)
		{
			throw getErrorTypeException(result, "Date");
		}
	}

	public java.sql.Date convertToDate(Object value)
	{
		return this.convertToDate(value, this.dateFormat);
	}

	public java.sql.Date convertToDate(Object value, DateFormat format)
	{
		if (value == null)
		{
			return null;
		}
		if (value instanceof java.sql.Date)
		{
			return (java.sql.Date) value;
		}
		if (value instanceof java.util.Date)
		{
			return new java.sql.Date(((java.util.Date) value).getTime());
		}
		if (value instanceof Number)
		{
			return new java.sql.Date(((Number) value).longValue());
		}
		if (value instanceof String)
		{
			return this.convertToDate((String) value, format);
		}
		if (value instanceof String[])
		{
			String str = RequestParameterMap.getFirstParam(value);
			return this.convertToDate(str, format);
		}
		if (value instanceof ObjectRef)
		{
			return this.convertToDate(((ObjectRef) value).getObject(), format);
		}
		throw new ClassCastException(getCastErrorMessage(value, "Date"));
	}

	public java.sql.Date convertToDate(String value)
	{
		return this.convertToDate(value, this.dateFormat);
	}

	public java.sql.Date convertToDate(String value, DateFormat format)
	{
		if (value == null)
		{
			return null;
		}
		try
		{
			if (format == null)
			{
				return new java.sql.Date(FormatTool.parseDate(value).getTime());
			}
			else
			{
				return new java.sql.Date(format.parse(value).getTime());
			}
		}
		catch (ParseException ex) {}
		throw new ClassCastException(getCastErrorMessage(value, "Date"));
	}

	public Object convert(Object value)
	{
		if (value instanceof java.sql.Date)
		{
			return (java.sql.Date) value;
		}
		try
		{
			return this.convertToDate(value);
		}
		catch (Exception ex)
		{
			if (this.needThrow)
			{
				if (ex instanceof RuntimeException)
				{
					throw (RuntimeException) ex;
				}
				throw new ClassCastException(getCastErrorMessage(value, "Date"));
			}
			else
			{
				return null;
			}
		}
	}

	public Object convert(String value)
	{
		try
		{
			return this.convertToDate(value);
		}
		catch (Exception ex)
		{
			if (this.needThrow)
			{
				if (ex instanceof RuntimeException)
				{
					throw (RuntimeException) ex;
				}
				throw new ClassCastException(getCastErrorMessage(value, "Date"));
			}
			else
			{
				return null;
			}
		}
	}

}