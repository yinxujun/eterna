
package self.micromagic.eterna.view;

import java.io.IOException;
import java.io.Writer;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.EternaFactory;

/**
 * �����ַ�����ʽת���ı�����.
 */
public interface StringCoder
{
	/**
	 * ��ʼ��.
	 */
   void initStringCoder(EternaFactory factory) throws ConfigurationException;

   /**
    * ����jsonʹ�����Ե�����. <p>
    * ����Ǹ��Ϸ�������, ��ֱ����<code>.name</code>.
    * ������������ַ�������, ��ֱʹ��<code>["name"]</code>.
    */
   String parseJsonRefName(String str);

	/**
	 * ���ַ���ת����HTML��ʽ���ַ���.
	 *
	 * @param str     Ҫ����ת�����ַ���
	 */
   String toHTML(String str);

	/**
	 * ���ַ���ת����HTML��ʽ���ַ���, ��ֱ��д�뵽�����out��.
	 *
	 * @param out     �����
	 * @param str     Ҫ����ת�����ַ���
	 */
   void toHTML(Writer out, String str) throws IOException;

	/**
	 * ���ַ���ת����json��ʽ���ַ���.
	 *
	 * @param str     Ҫ����ת�����ַ���
	 */
   String toJsonString(String str);

	/**
	 * ���ַ���ת����json��ʽ���ַ���, ��ֱ��д�뵽�����out��.
	 *
	 * @param out     �����
	 * @param str     Ҫ����ת�����ַ���
	 */
   void toJsonString(Writer out, String str) throws IOException;

	/**
	 * ���ַ���ת����json��ʽ���ַ���, ��ֱ��д�뵽�����out��,
	 * �����str�Ƿ�Ϊnull.
	 *
	 * @param out     �����
	 * @param str     Ҫ����ת�����ַ���
	 */
   void toJsonStringWithoutCheck(Writer out, String str) throws IOException;

	/**
	 * ���ַ�ת����json��ʽ���ַ���, ��ֱ��д�뵽�����out��.
	 *
	 * @param out     �����
	 * @param c       Ҫ����ת�����ַ�
	 */
   void toJsonString(Writer out, int c) throws IOException;

}
