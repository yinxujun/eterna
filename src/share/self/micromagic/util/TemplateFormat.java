
package self.micromagic.util;

import java.util.ArrayList;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.security.Permission;
import self.micromagic.eterna.share.AbstractGenerator;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.sql.ResultFormat;
import self.micromagic.eterna.sql.ResultFormatGenerator;
import self.micromagic.eterna.sql.ResultRow;

/**
 * 增加一个format的例子：
 * <format name="link.format" type="String" generator="self.micromagic.util.TemplateFormat"/>
 *    <pattern> <![CDATA[
 *       <a href="/ky/projectManager.dow?projectId=[v]&action=load">[v]</a>
 *    ]]> </pattern>
 * </format>
 */
public class TemplateFormat extends AbstractGenerator
		implements ResultFormat, ResultFormatGenerator
{
	public static final String DEFAULT_INSERT_VALUE_TAG = "[v]";

	protected String pattern;
	protected String[] patterns;
	protected boolean htmlFilter = true;

	/**
	 * 进行格式化输出需要的权限, 如果没有权限, 则不格式化, 直接输出
	 */
	protected String needPermission = null;

	public void initialize(EternaFactory factory)
			throws ConfigurationException
	{
	}

	public String format(Object obj, Permission permission)
			throws ConfigurationException
	{
		if (this.needPermission != null && permission != null)
		{
			if (!permission.hasPermission(this.needPermission))
			{
				return obj == null ? "" :
						this.htmlFilter ? Utils.dealString2HTML(obj.toString(), true) : obj.toString();
			}
		}
		String temp = obj == null ? "" :
				this.htmlFilter ? Utils.dealString2HTML(obj.toString(), true) : obj.toString();
		int count = this.pattern.length() + (this.patterns.length - 1) * temp.length();
		StringAppender buf = StringTool.createStringAppender(count);
		for (int i = 0; i < this.patterns.length; i++)
		{
			if (i > 0)
			{
				buf.append(temp);
			}
			buf.append(this.patterns[i]);
		}
		return buf.toString();
	}

	public String format(Object obj, ResultRow row, Permission permission)
			throws ConfigurationException
	{
		return this.format(obj, permission);
	}

	protected void parseTemplate()
	{
		if (this.pattern == null)
		{
			this.pattern = "";
			this.patterns = StringTool.EMPTY_STRING_ARRAY;
		}
		String valueTag = (String) this.getAttribute("insert_value_tag");
		valueTag = valueTag == null ? DEFAULT_INSERT_VALUE_TAG : valueTag;
		this.needPermission = (String) this.getAttribute("format_permission");
		String filter = (String) this.getAttribute("html_filter");
		if (filter != null)
		{
			this.htmlFilter = "true".equalsIgnoreCase(filter);
		}
		filter = (String) this.getAttribute("root_format");
		ArrayList temp = new ArrayList();
		int vtLength = valueTag.length();
		String str = this.pattern;
		int index = str.indexOf(valueTag);
		while (index != -1)
		{
			temp.add(str.substring(0, index));
			str = str.substring(index + vtLength);
			index = str.indexOf(valueTag);
		}
		temp.add(str);
		this.patterns = (String[]) temp.toArray(new String[temp.size()]);
	}

	public Object create()
			throws ConfigurationException
	{
		return this.createFormat();
	}

	public void setType(String type)
	{
	}

	public void setPattern(String pattern)
	{
		this.pattern = pattern;
	}

	public ResultFormat createFormat()
	{
		this.parseTemplate();
		return this;
	}

	public static void main(String[] args)
			throws ConfigurationException
	{
		TemplateFormat t = new TemplateFormat();
		t.setPattern("s[v]dfdfds[v]df[v]");
		t.parseTemplate();
		System.out.println(java.util.Arrays.asList(t.patterns));
		System.out.println(t.format("--", null));
	}

}