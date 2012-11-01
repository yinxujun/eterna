
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
    * ��ʼ��, �÷�������������search��ʼ��ʱ������.
    */
   void initParameterSetting(SearchAdapter search) throws ConfigurationException;

   /**
    * �������ڲ�ѯ��QueryAdapter�Ĳ���.
    *
    * @param first     ��ʾ�Ƿ�Ϊ��һ��ִ�в�������, ������ǵ�һ��, ��ɸ������,
    *                  ���������ò���, �򷵻�ʲô������ʹ��ǰһ�ε�����.
    * @param data      ����, ���������request��parameter, request��attribute,
    *                  session��attritute
    */
   void setParameter(QueryAdapter query, SearchAdapter search, boolean first,
         AppData data, Connection conn)
         throws ConfigurationException;

}
