package self.micromagic.util;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

public class DateRef extends ObjectRef
		implements Serializable
{
	private static DateFormat format = DateFormat.getDateTimeInstance();

	public DateRef()
	{
		super(new Date());
	}

	public DateRef(Date date)
	{
		super(date);
	}

	public static Date getDateValue(Object obj)
	{
		if (obj == null)
		{
			return null;
		}
		else if (obj instanceof Date)
		{
			return (Date) obj;
		}
		else if (obj instanceof String)
		{
			try
			{
				return DateRef.format.parse((String) obj);
			}
			catch (ParseException e) {}
		}
		return null;
	}

	public void setObject(Object obj)
	{
		super.setObject(DateRef.getDateValue(obj));
	}

	public void setDate(Date date)
	{
		this.setObject(date);
	}

	public Date getDate()
	{
		return (Date) this.getObject();
	}

}