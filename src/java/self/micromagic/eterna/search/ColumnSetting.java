
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
    * ��ʼ��, �÷�������������search��ʼ��ʱ������.
    */
   void initColumnSetting(SearchAdapter search) throws ConfigurationException;

   /**
    * ����һ������, ����������������ò�ѯ����.
    *
    * @param first     ��ʾ�Ƿ�Ϊ��һ�λ�ȡ������, ������ǵ�һ��, ��ɸ������,
    *                  �����¸���������, �򷵻�null��ʾʹ��ǰһ�ε�������.
    * @param data      ����, ���������request��parameter, request��attribute,
    *                  session��attritute
    *
    * @see    self.micromagic.eterna.sql.ResultReaderManager#setReaderList(String[])
    */
   String[] getColumnSetting(String columnType, QueryAdapter query, SearchAdapter search,
         boolean first, AppData data, Connection conn)
         throws ConfigurationException;
}
