
package self.micromagic.util.converter;

import self.micromagic.util.StringRef;
import self.micromagic.eterna.share.TypeManager;
import self.micromagic.eterna.digester.ConfigurationException;

public class CharacterConverter extends ObjectConverter
{
	private static StringConverter stringConverter = new StringConverter();
	private static Character DEFAULT_VALUE = new Character((char) 0);

	public int getConvertType(StringRef typeName)
	{
		if (typeName != null)
		{
			typeName.setString("char");
		}
		return TypeManager.TYPE_STRING;
	}

	public int getResult(Object result)
			throws ConfigurationException
	{
		try
		{
			return this.convertToChar(result);
		}
		catch (Exception ex)
		{
			throw getErrorTypeException(result, "char");
		}
	}

	public char convertToChar(Object value)
	{
		if (value == null)
		{
			return DEFAULT_VALUE.charValue();
		}
		Object tmpObj = this.changeByPropertyEditor(value);
		if (tmpObj instanceof Character)
		{
			return ((Character) tmpObj).charValue();
		}
		String str = stringConverter.convertToString(value);
		if (str.length() > 0)
		{
			return str.charAt(0);
		}
		throw new ClassCastException(getCastErrorMessage(value, "char"));
	}

	public char convertToChar(String value)
	{
		if (value == null)
		{
			return DEFAULT_VALUE.charValue();
		}
		Object tmpObj = this.changeByPropertyEditor(value);
		if (tmpObj instanceof Character)
		{
			return ((Character) tmpObj).charValue();
		}
		if (value.length() > 0)
		{
			return value.charAt(0);
		}
		throw new ClassCastException(getCastErrorMessage(value, "char"));
	}

	public Object convert(Object value)
	{
		if (value instanceof Character)
		{
			return (Character) value;
		}
		try
		{
			return new Character(this.convertToChar(value));
		}
		catch (Exception ex)
		{
			if (this.needThrow)
			{
				if (ex instanceof RuntimeException)
				{
					throw (RuntimeException) ex;
				}
				throw new ClassCastException(getCastErrorMessage(value, "char"));
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
			return new Character(this.convertToChar(value));
		}
		catch (Exception ex)
		{
			if (this.needThrow)
			{
				if (ex instanceof RuntimeException)
				{
					throw (RuntimeException) ex;
				}
				throw new ClassCastException(getCastErrorMessage(value, "char"));
			}
			else
			{
				return DEFAULT_VALUE;
			}
		}
	}

}