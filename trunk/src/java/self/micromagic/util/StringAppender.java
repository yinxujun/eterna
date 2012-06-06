
package self.micromagic.util;

/**
 * 字符串连接器, 用于对字符串进行添加操作的接口.
 */
public interface StringAppender extends CharSequence
{
    /**
     * 添加一个对象到字符串之后.
     */
    StringAppender append(Object obj);

    /**
     * 添加一个字符串到字符串之后.
     */
    StringAppender append(String str);

    /**
     * 添加一个字符串到字符串之后, 并设定起止位置.
     */
    StringAppender append(String str, int startIndex, int length);

    /**
     * 添加一个字符数组到字符串之后.
     */
    StringAppender append(char[] chars);

    /**
     * 添加一个字符数组到字符串之后, 并设定起止位置.
     */
    StringAppender append(char[] chars, int startIndex, int length);

    /**
     * 添加一个布尔值到字符串之后.
     */
    StringAppender append(boolean value);

    /**
     * 添加一个字符到字符串之后.
     */
    StringAppender append(char ch);

    /**
     * 添加一个整型值到字符串之后.
     */
    StringAppender append(int value);

    /**
     * 添加一个长整型值到字符串之后.
     */
    StringAppender append(long value);

    /**
     * 添加一个浮点型值到字符串之后.
     */
    StringAppender append(float value);

    /**
     * 添加一个双精度浮点型值到字符串之后.
     */
    StringAppender append(double value);

    /**
     * 添加一个换行符.
     */
    StringAppender appendln();

    /**
     * 获取子字符串.
     */
    String substring(int beginIndex, int endIndex);

}
