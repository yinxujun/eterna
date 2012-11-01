
package self.micromagic.eterna.search;

import java.sql.Connection;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.sql.QueryAdapter;

/**
 * @author micromagic@sina.com
 */
public interface ColumnSetting
{
   /**
    * 初始化, 该方法会在所属的search初始化时被调用.
    */
   void initColumnSetting(SearchAdapter search) throws ConfigurationException;

   /**
    * 返回一个数组, 该数组可以用来设置查询的列.
    *
    * @param first     表示是否为第一次获取列设置, 如果不是第一次, 则可根据情况,
    *                  或重新给出列设置, 或返回null表示使用前一次的列设置.
    * @param data      数据, 里面包含了request的parameter, request的attribute,
    *                  session的attritute
    *
    * @see    self.micromagic.eterna.sql.ResultReaderManager#setReaderList(String[])
    */
   String[] getColumnSetting(String columnType, QueryAdapter query, SearchAdapter search,
         boolean first, AppData data, Connection conn)
         throws ConfigurationException;
}
