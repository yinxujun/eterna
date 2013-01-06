
package self.micromagic.eterna.search;

import java.sql.Connection;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.sql.QueryAdapter;
import self.micromagic.eterna.model.AppData;

/**
 * @author micromagic@sina.com
 */
public interface ParameterSetting
{
	/**
	 * 初始化, 该方法会在所属的search初始化时被调用.
	 */
	void initParameterSetting(SearchAdapter search) throws ConfigurationException;

	/**
	 * 设置用于查询的QueryAdapter的参数.
	 *
	 * @param first     表示是否为第一次执行参数设置, 如果不是第一次, 则可根据情况,
	 *                  或重新设置参数, 或返回什么都不做使用前一次的设置.
	 * @param data      数据, 里面包含了request的parameter, request的attribute,
	 *                  session的attritute
	 */
	void setParameter(QueryAdapter query, SearchAdapter search, boolean first,
			AppData data, Connection conn)
			throws ConfigurationException;

}