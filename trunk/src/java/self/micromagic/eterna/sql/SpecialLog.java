
package self.micromagic.eterna.sql;

import java.sql.SQLException;
import java.sql.Connection;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.EternaFactory;
import org.dom4j.Element;

/**
 * 用于记录特殊的SQL日志.
 */
public interface SpecialLog
{
   /**
    * 初始化这个日志记录器.
    */
   void initSpecialLog(EternaFactory factory) throws ConfigurationException;

   /**
    * 记录日志.
    *
    * @param sql           发生日志的<code>SQLAdapter</code>
    * @param xmlLog        已记录日志信息的xml节点
    * @param usedTime      sql执行用时
    * @param exception     出错时抛出的异常
    * @param conn          执行<code>SQLAdapter</code>所使用的数据库连接
    */
   void logSQL(SQLAdapter sql, Element xmlLog, long usedTime, Throwable exception, Connection conn)
         throws ConfigurationException, SQLException;

}
