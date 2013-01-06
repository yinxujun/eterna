
package self.micromagic.grammer;

import java.util.Map;

public interface GrammerElement extends Checker
{
	public static final int TYPE_NONE = 0;
	public static final int TYPE_NAME = 1;
	public static final int TYPE_BLANK = 2;
	public static final int TYPE_TEXT = 3;
	public static final int TYPE_CHAR = 4;
	public static final int TYPE_ESCAPE = 5;
	public static final int TYPE_STRING = 6;
	public static final int TYPE_OPERATOR = 7;
	public static final int TYPE_EXPRESSION = 8;
	public static final int TYPE_NOTE = 9;
	public static final int TYPE_INT = 10;
	public static final int TYPE_INT8 = 11;
	public static final int TYPE_INT16 = 12;
	public static final int TYPE_FLOAT = 13;
	public static final int TYPE_ARRAY = 14;
	public static final int TYPE_OBJECT = 15;
	public static final int TYPE_EGROUP = 16;
	public static final int TYPE_EE = 17;

	public static final String[] TYPE_NAMES = {
		"NONE",
		"NAME",
		"BLANK",
		"TEXT",
		"CHAR",
		"ESCAPE",
		"STRING",
		"OPERATOR",
		"EXPRESSION",
		"NOTE",
		"INT",
		"INT8",
		"INT16",
		"FLOAT",
		"ARRAY",
		"OBJECT",
		"EGROUP",
		"EE"
	};

	void initialize(Map elements) throws GrammerException;

	String getName();

	int getType();

	/**
	 * 判断此节点是否为TYPE_NONE类型.
	 *
	 * @throws GrammerException   如果还没初始化完成, 则抛出此异常.
	 */
	boolean isTypeNone() throws GrammerException;

	boolean isNot();

}