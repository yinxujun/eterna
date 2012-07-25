
package self.micromagic.eterna.view;

import java.io.IOException;
import java.io.Writer;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.EternaFactory;

/**
 * 用于字符串格式转换的编码器.
 */
public interface StringCoder
{
	/**
	 * 初始化.
	 */
   void initStringCoder(EternaFactory factory) throws ConfigurationException;

   /**
    * 解析json使用属性的名称. <p>
    * 如果是个合法的名称, 则直接用<code>.name</code>.
    * 如果是有特殊字符的名称, 则直使用<code>["name"]</code>.
    */
   String parseJsonRefName(String str);

	/**
	 * 将字符串转换成HTML格式的字符串.
	 *
	 * @param str     要进行转换的字符串
	 */
   String toHTML(String str);

	/**
	 * 将字符串转换成HTML格式的字符串, 并直接写入到输出流out中.
	 *
	 * @param out     输出流
	 * @param str     要进行转换的字符串
	 */
   void toHTML(Writer out, String str) throws IOException;

	/**
	 * 将字符串转换成json格式的字符串.
	 *
	 * @param str     要进行转换的字符串
	 */
   String toJsonString(String str);

	/**
	 * 将字符串转换成json格式的字符串, 并直接写入到输出流out中.
	 *
	 * @param out     输出流
	 * @param str     要进行转换的字符串
	 */
   void toJsonString(Writer out, String str) throws IOException;

	/**
	 * 将字符串转换成json格式的字符串, 并直接写入到输出流out中,
	 * 不检查str是否为null.
	 *
	 * @param out     输出流
	 * @param str     要进行转换的字符串
	 */
   void toJsonStringWithoutCheck(Writer out, String str) throws IOException;

	/**
	 * 将字符转换成json格式的字符串, 并直接写入到输出流out中.
	 *
	 * @param out     输出流
	 * @param c       要进行转换的字符
	 */
   void toJsonString(Writer out, int c) throws IOException;

}
