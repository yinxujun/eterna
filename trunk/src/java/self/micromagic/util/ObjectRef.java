package self.micromagic.util;

import java.io.Serializable;

public class ObjectRef
		implements Serializable
{
	protected static final int MORE_EQUAL = -1;
	protected static final int TRUE_EQUAL = 1;
	protected static final int FALSE_EQUAL = 0;

	private Object obj;

	public ObjectRef()
	{
		this.obj = null;
	}

	public ObjectRef(Object obj)
	{
		this.obj = obj;
	}

	public void setObject(Object obj)
	{
		this.obj = obj;
	}

	public Object getObject()
	{
		return this.obj;
	}

	public String toString()
	{
		return String.valueOf(this.obj);
	}

	public boolean isBoolean()
	{
		return false;
	}

	public boolean isNumber()
	{
		return false;
	}

	public boolean isString()
	{
		return false;
	}

	public boolean booleanValue()
	{
		return false;
	}

	public int intValue()
	{
		return 0;
	}

	public long longValue()
	{
		return 0L;
	}

	public double doubleValue()
	{
		return 0.0;
	}

	protected int shareEqual(Object other, Class type)
	{
		if (other == null)
		{
			return FALSE_EQUAL;
		}
		if (this == other)
		{
			return TRUE_EQUAL;
		}
		if (!type.isInstance(other))
		{
			return FALSE_EQUAL;
		}
		return MORE_EQUAL;
	}

	public boolean equals(Object other)
	{
		int result = this.shareEqual(other, ObjectRef.class);
		if (result != MORE_EQUAL)
		{
			return result == TRUE_EQUAL;
		}

		if (this.obj == null && ((ObjectRef) other).obj != null)
		{
			return false;
		}
		return this.obj.equals(((ObjectRef) other).obj);
	}

	public int hashCode()
	{
		Object temp = this.getObject();
		return temp == null ? 0 : temp.hashCode();
	}

}