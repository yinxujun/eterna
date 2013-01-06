
package self.micromagic.util.converter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;

import self.micromagic.util.StringRef;
import self.micromagic.util.ObjectRef;
import self.micromagic.util.container.RequestParameterMap;
import self.micromagic.eterna.share.TypeManager;
import self.micromagic.eterna.digester.ConfigurationException;

public class BigIntegerConverter extends ObjectConverter
{
	private NumberFormat numberFormat;

	public void setNumberFormat(NumberFormat numberFormat)
	{
		this.numberFormat = numberFormat;
	}

	public int getConvertType(StringRef typeName)
	{
		if (typeName != null)
		{
			typeName.setString("BigInteger");
		}
		return TypeManager.TYPE_DECIMAL;
	}

	public BigInteger getResult(Object result)
			throws ConfigurationException
	{
		try
		{
			return this.convertToBigInteger(result);
		}
		catch (Exception ex)
		{
			throw getErrorTypeException(result, "Decimal");
		}
	}

	public BigInteger convertToBigInteger(Object value)
	{
		return this.convertToBigInteger(value, this.numberFormat);
	}

	public BigInteger convertToBigInteger(Object value, NumberFormat format)
	{
		if (value == null)
		{
			return null;
		}
		if (value instanceof BigInteger)
		{
			return (BigInteger) value;
		}
		if (value instanceof BigDecimal)
		{
			return ((BigDecimal) value).toBigInteger();
		}
		if (value instanceof Number)
		{
			return BigInteger.valueOf(((Number) value).longValue());
		}
		if (value instanceof String)
		{
			return this.convertToBigInteger((String) value, format);
		}
		if (value instanceof String[])
		{
			try
			{
				String str = RequestParameterMap.getFirstParam(value);
				return this.convertToBigInteger(str, format);
			}
			catch (NumberFormatException ex) {}
		}
		if (value instanceof ObjectRef)
		{
			ObjectRef ref = (ObjectRef) value;
			if (ref.isNumber())
			{
				return BigInteger.valueOf(ref.longValue());
			}
			else if (ref.isString())
			{
				return this.convertToBigInteger(ref.toString(), format);
			}
			else
			{
				return this.convertToBigInteger(ref.getObject(), format);
			}
		}
		throw new ClassCastException(getCastErrorMessage(value, "Decimal"));
	}

	public BigInteger convertToBigInteger(String value)
	{
		return this.convertToBigInteger(value, this.numberFormat);
	}

	public BigInteger convertToBigInteger(String value, NumberFormat format)
	{
		if (value == null)
		{
			return null;
		}
		try
		{
			if (format == null)
			{
				return new BigInteger(value);
			}
			else
			{
				return this.convertToBigInteger(format.parse(value));
			}
		}
		catch (Exception ex) {}
		throw new ClassCastException(getCastErrorMessage(value, "BigInteger"));
	}

	public Object convert(Object value)
	{
		if (value instanceof BigInteger)
		{
			return (BigInteger) value;
		}
		try
		{
			return this.convertToBigInteger(value);
		}
		catch (Exception ex)
		{
			if (this.needThrow)
			{
				if (ex instanceof RuntimeException)
				{
					throw (RuntimeException) ex;
				}
				throw new ClassCastException(getCastErrorMessage(value, "BigInteger"));
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
			return this.convertToBigInteger(value);
		}
		catch (Exception ex)
		{
			if (this.needThrow)
			{
				if (ex instanceof RuntimeException)
				{
					throw (RuntimeException) ex;
				}
				throw new ClassCastException(getCastErrorMessage(value, "BigInteger"));
			}
			else
			{
				return null;
			}
		}
	}

}

