
package self.micromagic.eterna.share;

import java.util.Map;
import java.util.HashMap;

public class DatabaseType
{
   public static DatabaseType ORACLE = new DatabaseType(0);
   public static DatabaseType SQL_SERVER = new DatabaseType(1);
   public static DatabaseType MYSQL = new DatabaseType(2);
   public static DatabaseType DB2 = new DatabaseType(3);

   private static final String[] databaseNames = {
      "oracle", "sql_server", "mysql", "db2"
   };
   private static final Map databaseMap = new HashMap();

   static
   {
      databaseMap.put("oracle", ORACLE);
      databaseMap.put("sql_server", SQL_SERVER);
      databaseMap.put("mysql", MYSQL);
      databaseMap.put("db2", DB2);
   }

   private int typeId;

   private DatabaseType(int type)
   {
      this.typeId = type;
   }

   public String getTypeName()
   {
      return databaseNames[this.typeId];
   }

   public int getTypeId()
   {
      return this.typeId;
   }

   public static DatabaseType getDatabaseType(String name)
   {
      return (DatabaseType) databaseMap.get(name);
   }

}
