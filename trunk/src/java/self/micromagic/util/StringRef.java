package self.micromagic.util;

import java.io.Serializable;

public class StringRef extends ObjectRef
		implements Serializable
{
	public StringRef()
	{
		super("");
	}

	public StringRef(String str)
	{
		super(str);
	}

	public boolean isString()
	{
		return true;
	}

	public static String getStringValue(Object obj)
	{
		return obj == null ? null : obj.toString();
	}

	public void setObject(Object obj)
	{
		super.setObject(StringRef.getStringValue(obj));
	}

	public void setString(String str)
	{
		this.setObject(str);
	}

	public String getString()
	{
		return (String) this.getObject();
	}

}