
package self.micromagic.eterna.sql.preparer;

import java.sql.SQLException;

import self.micromagic.eterna.sql.PreparedStatementWrap;

class ObjectPreparer extends AbstractValuePreparer
{
	protected Object value;
	protected Integer targetSqlType;
	protected Integer scale;

	public ObjectPreparer(ValuePreparerCreater vpc, Object value)
	{
		this(vpc, value, null, null);
	}

	public ObjectPreparer(ValuePreparerCreater vpc, Object value, Integer targetSqlType)
	{
		this(vpc, value, targetSqlType, null);
	}

	public ObjectPreparer(ValuePreparerCreater vpc, Object value, Integer targetSqlType, Integer scale)
	{
		super(vpc);
		this.value = value;
		this.targetSqlType = targetSqlType;
		this.scale = scale;
	}

	public void setValueToStatement(int index, PreparedStatementWrap stmtWrap)
			throws SQLException
	{
		if (this.targetSqlType == null)
		{
			stmtWrap.setObject(this.getName(), index, this.value);
		}
		else if (this.scale == null)
		{
			stmtWrap.setObject(this.getName(), index, this.value, this.targetSqlType.intValue());
		}
		else
		{
			stmtWrap.setObject(this.getName(), index, this.value,
					this.targetSqlType.intValue(), this.scale.intValue());
		}
	}

	static class Creater extends AbstractCreater
	{
		public Creater(ValuePreparerCreaterGenerator vpcg)
		{
			super(vpcg);
		}

		public ValuePreparer createPreparer(Object value)
		{
			return new ObjectPreparer(this, value);
		}

		public ValuePreparer createPreparer(String value)
		{
			return new ObjectPreparer(this, value);
		}

		public ValuePreparer createPreparer(Object value, int targetSqlType)
		{
			return new ObjectPreparer(this, value, new Integer(targetSqlType));
		}

		public ValuePreparer createPreparer(Object value, int targetSqlType, int scale)
		{
			return new ObjectPreparer(this, value, new Integer(targetSqlType),
					new Integer(scale));
		}

	}

}