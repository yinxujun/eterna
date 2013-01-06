
package self.micromagic.grammer;

/**
 * 语法检查中出现异常.
 */
public class GrammerException extends Exception
{
	private Checker checker = null;

	/**
	 * @param message   出错信息
	 */
	public GrammerException(String message)
	{
		super(message);
	}

	public GrammerException(Checker checker)
	{
		this.checker = checker;
	}

	public GrammerException(Exception cause)
	{
		super(cause);
	}

	public Checker getChecker()
	{
		return this.checker;
	}

}