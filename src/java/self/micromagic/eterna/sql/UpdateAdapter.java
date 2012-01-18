
package self.micromagic.eterna.sql;

import java.sql.Connection;
import java.sql.SQLException;

import self.micromagic.eterna.digester.ConfigurationException;

public interface UpdateAdapter extends SQLAdapter
{
   /**
    * ִ�б�Updater������.
    *
    * @return    ���µļ�¼��.
    */
   int executeUpdate(Connection conn) throws ConfigurationException, SQLException;

}
