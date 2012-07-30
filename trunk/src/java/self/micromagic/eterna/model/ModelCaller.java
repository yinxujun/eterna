
package self.micromagic.eterna.model;

import java.sql.SQLException;
import java.sql.Connection;
import java.io.IOException;

import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.share.DataSourceManager;
import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.util.ObjectRef;

public interface ModelCaller
{
   public static final String DEFAULT_MODEL_TAG = "self.micromagic.default.model";
   public static final String DEFAULT_MODEL_NAME = "index";

   /**
    * @deprecated
    * @see DataSourceManager#DATA_SOURCE_MAP
    */
   public static final String DATA_SOURCE_MAP = DataSourceManager.DATA_SOURCE_MAP;

   /**
    * @deprecated
    * @see DataSourceManager#DEFAULT_DATA_SOURCE_NAME
    */
   public static final String DEFAULT_DATA_SOURCE_NAME = DataSourceManager.DEFAULT_DATA_SOURCE_NAME;

   void initModelCaller(EternaFactory factory) throws ConfigurationException;

   Connection getConnection(ModelAdapter model) throws SQLException, ConfigurationException;

   void closeConnection(Connection conn);

   EternaFactory getFactory() throws ConfigurationException;

   ModelExport callModel(AppData data)
         throws ConfigurationException, SQLException, IOException;

   ModelExport callModel(AppData data, ObjectRef preConn)
         throws ConfigurationException, SQLException, IOException;

   ModelExport callModel(AppData data, ModelAdapter model, ModelExport export, int tType, ObjectRef preConn)
         throws ConfigurationException, SQLException, IOException;

   /**
    * 根据数据集<code>data</code>生成重定向的参数
    */
   String prepareParam(AppData data, String charset) throws ConfigurationException, IOException;

}
