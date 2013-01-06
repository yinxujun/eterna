
package self.micromagic.util.converter;

import java.text.NumberFormat;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.TypeManager;
import self.micromagic.util.ObjectRef;
import self.micromagic.util.Utility;
import self.micromagic.util.StringRef;
import self.micromagic.util.container.RequestParameterMap;

public class IntegerConverter extends ObjectConverter
{
	private static Integer DEFAULT_VALUE = Utility.INTEGER_0;

	private NumberFormat numberFormat;

	public void setNumberFormat(NumberFormat numberFormat)
	{
		this.numberFormat = numberFormat;
	}

	public int getConvertType(StringRef typeName)
	{
		if (typeName != null)
		{
			typeName.setString("int");
		}
		return TypeManager.TYPE_INTEGER;
	}

	public int getResult(Object result)
			throws ConfigurationException
	{
		try
		{
			return this.convertToInt(result);
		}
		catch (Exception ex)
		{
			throw getErrorTypeException(result, "int");
		}
	}

	public int convertToInt(Object value)
	{
		return this.convertToInt(value, this.numberFormat);
	}

	public int convertToInt(Object value, NumberFormat format)
	{
		if (value == null)
		{
			return 0;
		}
		if (value instanceof Number)
		{
			return ((Number) value).intValue();
		}
		if (value instanceof String)
		{
			return this.convertToInt((String) value, format);
		}
		Object tmpObj = this.changeByPropertyEditor(value);
		if (tmpObj instanceof Integer)
		{
			return ((Integer) tmpObj).intValue();
		}
		if (value instanceof String[])
		{
			String str = RequestParameterMap.getFirstParam(value);
			return this.convertToInt(str, format);
		}
		if (value instanceof ObjectRef)
		{
			ObjectRef ref = (ObjectRef) value;
			if (ref.isNumber())
			{
				return ref.intValue();
			}
			else if (ref.isString())
			{
				return this.convertToInt(ref.toString(), format);
			}
			else
			{
				return this.convertToInt(ref.getObject(), format);
			}
		}
		throw new ClassCastException(getCastErrorMessage(value, "int"));
	}

	public int convertToInt(String value)
	{
		return this.convertToInt(value, this.numberFormat);
	}

	public int convertToInt(String value, NumberFormat format)
	{
		if (value == null)
		{
			return 0;
		}
		Object tmpObj = this.changeByPropertyEditor(value);
		if (tmpObj instanceof Integer)
		{
			return ((Integer) tmpObj).intValue();
		}
		try
		{
			if (format == null)
			{
				return Integer.parseInt(value);
			}
			else
			{
				return format.parse(value).intValue();
			}
		}
		catch (Exception ex) {}
		throw new ClassCastException(getCastErrorMessage(value, "int"));
	}

	public Object convert(Object value)
	{
		if (value instanceof Integer)
		{
			return (Integer) value;
		}
		try
		{
			return new Integer(this.convertToInt(value));
		}
		catch (Exception ex)
		{
			if (this.needThrow)
			{
				if (ex instanceof RuntimeException)
				{
					throw (RuntimeException) ex;
				}
				throw new ClassCastException(getCastErrorMessage(value, "int"));
			}
			else
			{
				return DEFAULT_VALUE;
			}
		}
	}

	public Object convert(String value)
	{
		try
		{
			return new Integer(this.convertToInt(value));
		}
		catch (Exception ex)
		{
			if (this.needThrow)
			{
				if (ex instanceof RuntimeException)
				{
					throw (RuntimeException) ex;
				}
				throw new ClassCastException(getCastErrorMessage(value, "int"));
			}
			else
			{
				return DEFAULT_VALUE;
			}
		}
	}

}