
package self.micromagic.eterna.sql.preparer;

import java.sql.Date;
import java.sql.SQLException;
import java.util.Calendar;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import self.micromagic.eterna.sql.PreparedStatementWrap;
import self.micromagic.util.converter.DateConverter;

class DatePreparer extends AbstractValuePreparer
{
	protected Date value;
	protected Calendar calendar;

	public DatePreparer(ValuePreparerCreater vpc, Date value)
	{
		this(vpc, value, null);
	}

	public DatePreparer(ValuePreparerCreater vpc, Date value, Calendar calendar)
	{
		super(vpc);
		this.value = value;
		this.calendar = calendar;
	}

	public void setValueToStatement(int index, PreparedStatementWrap stmtWrap)
			throws SQLException
	{
		if (this.calendar == null)
		{
			stmtWrap.setDate(this.getName(), index, this.value);
		}
		else
		{
			stmtWrap.setDate(this.getName(), index, this.value, this.calendar);
		}
	}

	static class Creater extends AbstractCreater
	{
		DateConverter convert = new DateConverter();
		DateFormat format = null;

		public Creater(ValuePreparerCreaterGenerator vpcg)
		{
			super(vpcg);
		}

		public void setFormat(String formatStr)
		{
			this.format = new SimpleDateFormat(formatStr);
		}

		public ValuePreparer createPreparer(Object value)
		{
			return new DatePreparer(this, this.convert.convertToDate(value, this.format));
		}

		public ValuePreparer createPreparer(String value)
		{
			return new DatePreparer(this, this.convert.convertToDate(value, this.format));
		}

		public ValuePreparer createPreparer(Date value)
		{
			return new DatePreparer(this, value);
		}

		public ValuePreparer createPreparer(Date value, Calendar calendar)
		{
			return new DatePreparer(this, value, calendar);
		}

	}

}