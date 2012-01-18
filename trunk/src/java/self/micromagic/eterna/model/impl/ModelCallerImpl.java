
package self.micromagic.eterna.model.impl;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.sql.DataSource;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.digester.FactoryManager;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.model.AppDataLogExecute;
import self.micromagic.eterna.model.ModelAdapter;
import self.micromagic.eterna.model.ModelCaller;
import self.micromagic.eterna.model.ModelExport;
import self.micromagic.eterna.share.DataSourceManager;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.util.ObjectRef;
import self.micromagic.util.Utility;

public class ModelCallerImpl
      implements ModelCaller
{
   private EternaFactory factory;
   private Map dataSourceMap = null;
   private DataSource defaultDataSource = null;
   private String defaultDataSourceName = null;

   public ModelCallerImpl()
   {
   }

   public void initModelCaller(EternaFactory factory)
         throws ConfigurationException
   {
      this.factory = factory;
      DataSourceManager dsm = factory.getDataSourceManager();
      if (dsm != null)
      {
         this.dataSourceMap = dsm.getDataSourceMap();
         this.defaultDataSource = dsm.getDefaultDataSource();
         this.defaultDataSourceName = dsm.getDefaultDataSourceName();
         return;
      }
      Map cache = FactoryManager.getInitCache();
      if (cache != null)
      {
         this.dataSourceMap = (Map) cache.get(DATA_SOURCE_MAP);
         if (this.dataSourceMap != null && this.dataSourceMap.size() > 0)
         {
            this.defaultDataSourceName = (String) cache.get(DEFAULT_DATA_SOURCE_NAME);
            if (defaultDataSourceName == null)
            {
               Map.Entry entry = (Map.Entry) this.dataSourceMap.entrySet().iterator().next();
               this.defaultDataSource = (DataSource) entry.getValue();
               this.defaultDataSourceName = (String) entry.getKey();
            }
            else
            {
               this.defaultDataSource = (DataSource) this.dataSourceMap.get(this.defaultDataSourceName);
            }
         }
         else
         {
            this.dataSourceMap = null;
         }
      }
   }

   public Connection getConnection(ModelAdapter model)
         throws SQLException, ConfigurationException
   {
      DataSource ds;
      if (this.dataSourceMap != null)
      {
         String name;
         name = model.getDataSourceName();
         if (name == null || name.equals(this.defaultDataSourceName))
         {
            ds = this.defaultDataSource;
         }
         else
         {
            ds = (DataSource) this.dataSourceMap.get(name);
            if (ds == null)
            {
               throw new ConfigurationException("Can't find the data source:" + name + ".");
            }
         }
      }
      else
      {
         ds = Utility.getDataSource();
      }
      return ds.getConnection();
   }

   public void closeConnection(Connection conn)
   {
      try
      {
         if (conn != null)
         {
            //conn.rollback();
            conn.close();
         }
      }
      catch (SQLException ex) {}
   }

   public EternaFactory getFactory()
   {
      return this.factory;
   }

   public ModelExport callModel(AppData data)
         throws ConfigurationException, SQLException, IOException
   {
      ObjectRef preConn = (ObjectRef) data.getSpcialData(ModelAdapter.MODEL_CACHE, ModelAdapter.PRE_CONN);
      if (preConn == null)
      {
         preConn = new ObjectRef();
         data.addSpcialData(ModelAdapter.MODEL_CACHE, ModelAdapter.PRE_CONN, preConn);
      }

      boolean needLogApp = AppData.getAppLogType() != 0;
      if (needLogApp)
      {
         data.beginNode("call", data.modelName, null);
         AppDataLogExecute.printAppData(data);
      }
      boolean commited = false;
      try
      {
         ModelExport export = this.callModel(data, preConn);
         if (preConn.getObject() != null)
         {
            if (export == null || !export.isErrorExport())
            {
               commited = true;
            }
         }
         return export;
      }
      finally
      {
         if (needLogApp)
         {
            try
            {
               AppDataLogExecute.printAppData(data);
               data.endNode(null, null);
            }
            catch (Throwable ex) {}
         }
         if (preConn.getObject() != null)
         {
            if (preConn.getObject() instanceof Connection)
            {
               Connection conn = (Connection) preConn.getObject();
               if (!commited) conn.rollback();
               else conn.commit();
               conn.close();
            }
            else if (preConn.getObject() instanceof Map)
            {
               Map map = (Map) preConn.getObject();
               Iterator values = map.values().iterator();
               while (values.hasNext())
               {
                  Connection conn = (Connection) values.next();
                  try
                  {
                     if (!commited) conn.rollback();
                     else conn.commit();
                     conn.close();
                  }
                  catch (SQLException sex)
                  {
                     AppData.log.error("*** Error in close connection.", sex);
                  }
               }
            }
            else
            {
               AppData.log.error("Error preConn type:" + preConn.getObject().getClass() + ".");
            }
            preConn.setObject(null);
         }
      }
   }

   public ModelExport callModel(AppData data, ObjectRef preConn)
         throws ConfigurationException, SQLException, IOException
   {
      if (data.modelName == null)
      {
         data.modelName = data.getRequestParameter(factory.getModelNameTag());
         if (data.modelName == null)
         {
            data.modelName = (String) data.getRequestAttributeMap().get(DEFAULT_MODEL_TAG);
         }
      }

      ModelAdapter model = null;
      try
      {
         model = factory.createModelAdapter(data.modelName);
      }
      catch (ConfigurationException ex)
      {
         if (AppData.log.isInfoEnabled())
         {
            StringBuffer buf = new StringBuffer(128);
            buf.append("Not found the model:").append(data.modelName).append(".");
            AppData.log.warn(buf, ex);
         }
         return null;
      }
      return this.callModel(data, model, null, model.getTransactionType(), preConn);
   }

   private void setPreConnection(Connection conn, ObjectRef preConn, String dsName)
   {
      if (preConn.getObject() != null)
      {
         Map map;
         if (preConn.getObject() instanceof Connection)
         {
            Connection tmpConn = (Connection) preConn.getObject();
            map = new HashMap();
            map.put(this.defaultDataSourceName, tmpConn);
            preConn.setObject(map);
            map.put(dsName, conn);
         }
         else if (preConn.getObject() instanceof Map)
         {
            map = (Map) preConn.getObject();
            map.put(dsName, conn);
         }
         else
         {
            AppData.log.error("Error preConn type:" + preConn.getObject().getClass() + ".");
         }
      }
      else
      {
         if (dsName == null || dsName.equals(this.defaultDataSourceName))
         {
            preConn.setObject(conn);
         }
         else
         {
            Map map = new HashMap();
            map.put(dsName, conn);
            preConn.setObject(map);
         }
      }
   }

   public ModelExport callModel(AppData data, ModelAdapter model, ModelExport export, int tType, ObjectRef preConn)
         throws ConfigurationException, SQLException, IOException
   {
      String dsName = model.getDataSourceName();
      Connection oldConn = null;
      if (preConn.getObject() != null)
      {
         if (dsName == null || dsName.equals(this.defaultDataSourceName))
         {
            if (preConn.getObject() instanceof Connection)
            {
               oldConn = (Connection) preConn.getObject();
            }
            else if (preConn.getObject() instanceof Map)
            {
               oldConn = (Connection) ((Map) preConn.getObject()).get(this.defaultDataSourceName);
            }
            else
            {
               AppData.log.error("Error preConn type:" + preConn.getObject().getClass() + ".");
            }
         }
         else
         {
            if (preConn.getObject() instanceof Map)
            {
               oldConn = (Connection) ((Map) preConn.getObject()).get(dsName);
            }
            else if (!(preConn.getObject() instanceof Connection))
            {
               AppData.log.error("Error preConn type:" + preConn.getObject().getClass() + ".");
            }
         }
      }
      Connection myConn = null;
      boolean executed = false;
      switch (tType)
      {
         case ModelAdapter.T_REQUARED:
            if (oldConn == null)
            {
               oldConn = this.getConnection(model);
               oldConn.setAutoCommit(false);
               this.setPreConnection(oldConn, preConn, dsName);
            }
            myConn = oldConn;
            break;
         case ModelAdapter.T_NEW:
            myConn = this.getConnection(model);
            myConn.setAutoCommit(false);
            // 这里不需要判断oldConn, 后面的model如果是requared的话, 将使用这个conn
            this.setPreConnection(myConn, preConn, dsName);
            break;
         case ModelAdapter.T_NONE:
            myConn = this.getConnection(model);
            myConn.setAutoCommit(true);
            break;
         case ModelAdapter.T_HOLD:
            myConn = this.getConnection(model);
            myConn.setAutoCommit(true);
            break;
         case ModelAdapter.T_IDLE:
            myConn = this.getConnection(model);
            break;
         case ModelAdapter.T_NOTNEED:
            myConn = oldConn;
      }

      try
      {
         ModelExport tmpExport = model.doModel(data, myConn);
         if (tmpExport != null)
         {
            if (tmpExport.isRedirect() || (!tmpExport.isNextModel() && tmpExport.isErrorExport()))
            {
               executed = !tmpExport.isErrorExport();
               return tmpExport;
            }
         }
         if (export != null)
         {
            tmpExport = export;
            if (tmpExport.isRedirect() || (!tmpExport.isNextModel() && tmpExport.isErrorExport()))
            {
               executed = !export.isErrorExport();
               return export;
            }
         }
         boolean hasError = false;
         if (tmpExport != null && tmpExport.isNextModel())
         {
            if (tmpExport.isErrorExport())
            {
               // 如果是错误的出口, 并且有下一个要执行的model, 则回滚
               if (tType == ModelAdapter.T_REQUARED || tType == ModelAdapter.T_NEW)
               {
                  myConn.rollback();
               }
               hasError = true;
            }
            data.modelName = tmpExport.getModelName();
            tmpExport = this.callModel(data, preConn);
         }
         executed = !hasError && (tmpExport == null || !tmpExport.isErrorExport());
         return tmpExport;
      }
      finally
      {
         switch (tType)
         {
            case ModelAdapter.T_REQUARED:
               if (!executed)
               {
                  myConn.rollback();
               }
               break;
            case ModelAdapter.T_NEW:
               if (!executed)
               {
                  myConn.rollback();
               }
               if (oldConn != null)
               {
                  if (executed)
                  {
                     myConn.commit();
                  }
                  this.closeConnection(myConn);
                  // oldConn不为空, 则设回原来的conn
                  this.setPreConnection(myConn, preConn, dsName);
               }
               break;
            case ModelAdapter.T_NONE:
               this.closeConnection(myConn);
               break;
            case ModelAdapter.T_IDLE:
               this.closeConnection(myConn);
               break;
            case ModelAdapter.T_HOLD:
            case ModelAdapter.T_NOTNEED:
               break;
         }
      }
   }

   public String prepareParam(AppData data, String charset)
         throws ConfigurationException, IOException
   {
      StringBuffer buf = new StringBuffer(512);
      Iterator params = data.dataMap.entrySet().iterator();
      while (params.hasNext())
      {
         Map.Entry entry = (Map.Entry) params.next();
         if (entry.getValue() != null)
         {
            if (buf.length() > 0)
            {
               buf.append("&");
            }
            String name = URLEncoder.encode(String.valueOf(entry.getKey()), charset);
            Object value = entry.getValue();
            if (value instanceof Collection)
            {
               Iterator itr = ((Collection) value).iterator();
               boolean hasValue = false;
               while (itr.hasNext())
               {
                  Object tmp = itr.next();
                  if (tmp != null)
                  {
                     if (hasValue)
                     {
                        buf.append("&");
                     }
                     hasValue = true;
                     buf.append(name).append("=").append(URLEncoder.encode(String.valueOf(tmp), charset));
                  }
               }
               if (!hasValue)
               {
                  buf.append(name).append("=");
               }
            }
            else if (value.getClass().isArray())
            {
               boolean hasValue = false;
               int length = Array.getLength(value);
               for (int i = 0; i < length; i++)
               {
                  Object tmp = Array.get(value, i);
                  if (tmp != null)
                  {
                     if (hasValue)
                     {
                        buf.append("&");
                     }
                     hasValue = true;
                     buf.append(name).append("=").append(URLEncoder.encode(String.valueOf(tmp), charset));
                  }
               }
               if (!hasValue)
               {
                  buf.append(name).append("=");
               }
            }
            else
            {
               buf.append(name).append("=").append(URLEncoder.encode(String.valueOf(value), charset));
            }
         }
      }
      return buf.toString();
   }

}
