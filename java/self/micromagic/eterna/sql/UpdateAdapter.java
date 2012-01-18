
package self.micromagic.eterna.sql;

import java.sql.Connection;
import java.sql.SQLException;

import self.micromagic.eterna.digester.ConfigurationException;

public interface UpdateAdapter extends SQLAdapter
{
   /**
    * 执行本Updater适配器.
    *
    * @return    更新的记录数.
    */
   int executeUpdate(Connection conn) throws ConfigurationException, SQLException;

}
