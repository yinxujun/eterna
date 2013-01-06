
package self.micromagic.grammer;

import self.micromagic.util.Utils;

public abstract class AbstractElement
		implements GrammerElement
{
	protected String name;
	protected int type;
	protected boolean not = false;

	public boolean verify(ParserData pd)
			throws GrammerException
	{
		int startIndex = pd.getCurrentIndex();
		pd.addResetPoint();
		pd.pushChecker(this);
		boolean result = this.not ^ this.doVerify(pd);
		if (this.not)
		{
			if (startIndex == pd.getCurrentIndex())
			{
				pd.getNextChar();
			}
			/*System.out.println("n{" + this + "}:" + result
					+ ":[" + Utils.dealString2EditCode(pd.getCurrentSubBuf())
					+ "]:" + Utils.dealString2EditCode(pd.getCurrentBuf()));*/
		}
		pd.popChecker(result);
		if (result)
		{
			pd.storeElement(this);
		}
		else
		{
			pd.reset();
		}
		return result;
	}

	public abstract boolean doVerify(ParserData pd) throws GrammerException;

	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getType()
	{
		return this.type;
	}

	public void setType(int type)
	{
		this.type = type;
	}

	public boolean isNot()
	{
		return this.not;
	}

	public void setNot(boolean not)
	{
		this.not = not;
	}

}