
package self.micromagic.eterna.model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.EternaFactory;

public interface ModelAdapter
{
   /**
    * model缓存的名称.
    *
    * @see    self.micromagic.eterna.model.AppData#getSpcialDataMap(String)
    */
   public static final String MODEL_CACHE = "model.cache";

   /**
    * model缓存中存储当前正在使用的数据库链接的名称.
    */
   public static final String PRE_CONN = "preConn";

   /**
    * model缓存中存储业务是否已接管了数据库链接的控制权. <p>
    * 设置样例:
    * data.addSpcialData(ModelAdapter.MODEL_CACHE, ModelAdapter.CONN_HOLDED, "1");
    *
    * @see    self.micromagic.eterna.model.AppData#addSpcialData(String, String, Object)
    */
   public static final String CONN_HOLDED = "connHolded";

   /**
    * 设置需前置执行的model.
    * 这是一个factory的arrtibute, 它的值是那个前置model的名称.
    */
   public static final String FRONT_MODEL_ATTRIBUTE = "front.model.name";

   /**
    * 需要开启一个事务，如果已有事务，加入现有的事务
    */
   public static final int T_REQUARED = 0;

   /**
    * 强制开启一个新事物，如果已有事务，挂起现有事务
    */
   public static final int T_NEW = 1;

   /**
    * 不需要事务，如果已有事务，挂起现有事务
    */
   public static final int T_NONE = 2;

   /**
    * 不需要事务，并保持连接，由应用自己释放
    */
   public static final int T_HOLD = 3;

   /**
    * 不需连接，如果已有连接使用现有的连接
    */
   public static final int T_NOTNEED  = 4;

   /**
    * 不设置事务状态，结束后也不进行提交或回滚
    */
   public static final int T_IDLE  = 5;

   /**
    * 默认的出错出口的名称
    */
   public static final String DEFAULT_ERROR_EXPORT_NAME = "defaultErrorExport";


   String getName() throws ConfigurationException;

   EternaFactory getFactory() throws ConfigurationException;

   boolean isKeepCaches() throws ConfigurationException;

   boolean isNeedFrontModel() throws ConfigurationException;

   String getFrontModelName() throws ConfigurationException;

   int getTransactionType() throws ConfigurationException;

   String getDataSourceName() throws ConfigurationException;

   boolean checkPosition(AppData data) throws ConfigurationException;

   ModelExport getModelExport() throws ConfigurationException;

   ModelExport getErrorExport() throws ConfigurationException;

   ModelExport doModel(AppData data, Connection conn)
         throws ConfigurationException, SQLException, IOException;

}
