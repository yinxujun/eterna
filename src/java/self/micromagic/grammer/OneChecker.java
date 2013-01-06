
package self.micromagic.grammer;

/**
 * 单个的字符检查器.
 */
public abstract class OneChecker
		implements Checker
{

	public boolean verify(ParserData pd)
			throws GrammerException
	{
		pd.pushChecker(this);
		boolean result = this.verify(pd.getCurrentChar());
		pd.popChecker(result);
		return result;
	}

	protected abstract boolean verify(char c);

	public static class SetChecker extends OneChecker
			implements Checker
	{
		private String chars;

		public SetChecker(String chars)
		{
			this.chars = chars == null ? "" : chars;
		}

		public boolean verify(char c)
		{
			return this.chars.indexOf(c) != -1;
		}

		public String toString()
		{
			return this.chars;
		}

	}

	public static class RangeChecker extends OneChecker
			implements Checker
	{
		private char beginChar;
		private char endChar;

		public RangeChecker(char beginChar, char endChar)
		{
			this.beginChar = beginChar;
			this.endChar = endChar;
		}

		public boolean verify(char c)
		{
			return c >= this.beginChar && c <= this.endChar;
		}

		public String toString()
		{
			return "[" + this.beginChar + "-" + this.endChar + "]";
		}

	}
}