
package self.micromagic.util;

/**
 * �ַ���������, ���ڶ��ַ���������Ӳ����Ľӿ�.
 */
public interface StringAppender extends CharSequence, Appendable
{
    /**
     * ���һ�������ַ���֮��.
     */
    StringAppender append(Object obj);

    /**
     * ���һ���ַ������ַ���֮��.
     */
    StringAppender append(String str);

    /**
     * ���һ���ַ������ַ���֮��, ���趨��ֹλ��.
     */
    StringAppender append(String str, int startIndex, int length);

    /**
     * ���һ���ַ����鵽�ַ���֮��.
     */
    StringAppender append(char[] chars);

    /**
     * ���һ���ַ����鵽�ַ���֮��, ���趨��ֹλ��.
     */
    StringAppender append(char[] chars, int startIndex, int length);

    /**
     * ���һ������ֵ���ַ���֮��.
     */
    StringAppender append(boolean value);

    /**
     * ���һ���ַ����ַ���֮��.
     */
    StringAppender append(char ch);

    /**
     * ���һ������ֵ���ַ���֮��.
     */
    StringAppender append(int value);

    /**
     * ���һ��������ֵ���ַ���֮��.
     */
    StringAppender append(long value);

    /**
     * ���һ��������ֵ���ַ���֮��.
     */
    StringAppender append(float value);

    /**
     * ���һ��˫���ȸ�����ֵ���ַ���֮��.
     */
    StringAppender append(double value);

    /**
     * ���һ�����з�.
     */
    StringAppender appendln();

    /**
     * ��ȡ���ַ���.
     */
    String substring(int beginIndex, int endIndex);

}
