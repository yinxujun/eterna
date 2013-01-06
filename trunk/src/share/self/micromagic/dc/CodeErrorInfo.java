
package self.micromagic.dc;

import self.micromagic.util.StringAppender;
import self.micromagic.util.StringTool;

/**
 * 动态生成类时的出错信息.
 *
 * @author micromagic@sina.com
 */
public class CodeErrorInfo
{
	/**
	 * 需要动态编译的代码.
	 */
	public final String code;

	/**
	 * 代码所在的位置信息.
	 */
	public final String position;

	/**
	 * 出错的异常信息.
	 */
	public final Exception error;

	public CodeErrorInfo(String code, String position, Exception error)
	{
		this.code = code;
		this.position = position;
		this.error = error;
	}

	private String message;

	public String toString()
	{
		if (this.message == null)
		{
			StringAppender buf = StringTool.createStringAppender(256);
			buf.append("CodeErrorInfo:[").appendln()
					.append("   position:").append(this.position).appendln()
					.append("   error:").append(this.error.getMessage()).appendln()
					.append("   code:").appendln().append(this.code).appendln()
					.append(" ]");
			this.message = buf.toString();
		}
		return this.message;
	}

}