
package self.micromagic.eterna.sql;

import java.sql.SQLException;
import java.sql.Connection;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.EternaFactory;
import org.dom4j.Element;

/**
 * ���ڼ�¼�����SQL��־.
 */
public interface SpecialLog
{
   /**
    * ��ʼ�������־��¼��.
    */
   void initSpecialLog(EternaFactory factory) throws ConfigurationException;

   /**
    * ��¼��־.
    *
    * @param sql           ������־��<code>SQLAdapter</code>
    * @param xmlLog        �Ѽ�¼��־��Ϣ��xml�ڵ�
    * @param usedTime      sqlִ����ʱ
    * @param exception     ����ʱ�׳����쳣
    * @param conn          ִ��<code>SQLAdapter</code>��ʹ�õ����ݿ�����
    */
   void logSQL(SQLAdapter sql, Element xmlLog, long usedTime, Throwable exception, Connection conn)
         throws ConfigurationException, SQLException;

}
