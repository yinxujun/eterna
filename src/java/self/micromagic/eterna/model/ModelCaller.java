
package self.micromagic.eterna.model;

import java.sql.SQLException;
import java.sql.Connection;
import java.io.IOException;

import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.util.ObjectRef;

public interface ModelCaller
{
   public final static String DEFAULT_MODEL_TAG = "self.micromagic.default.model";
   public final static String DEFAULT_MODEL_NAME = "index";
   public final static String DATA_SOURCE_MAP = "dataSourceMap";
   public final static String DEFAULT_DATA_SOURCE_NAME = "defaultDataSourceName";

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
    * �������ݼ�<code>data</code>�����ض���Ĳ���
    */
   String prepareParam(AppData data, String charset) throws ConfigurationException, IOException;

}
