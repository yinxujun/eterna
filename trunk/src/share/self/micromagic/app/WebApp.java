
package self.micromagic.app;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.digester.FactoryManager;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.model.ModelExport;
import self.micromagic.eterna.model.ModelAdapter;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.share.EternaInitialize;
import self.micromagic.util.container.ThreadCache;
import self.micromagic.eterna.sql.QueryAdapter;
import self.micromagic.eterna.sql.ResultIterator;
import self.micromagic.eterna.sql.ResultRow;
import self.micromagic.eterna.sql.SQLParameter;
import self.micromagic.eterna.sql.SQLParameterGenerator;
import self.micromagic.eterna.sql.impl.QueryAdapterImpl;
import self.micromagic.eterna.sql.impl.ResultReaderGeneratorImpl;
import self.micromagic.eterna.sql.impl.SQLParameterGeneratorImpl;
import self.micromagic.util.ObjectRef;
import self.micromagic.util.Utility;
import self.micromagic.util.container.ValueContainerMap;

public interface WebApp
{
   public static final Log log = Utility.createLog("app");

   public static final String DEFAULT_MODEL_TAG = "defaultModel";
   public static final String VIEW_TAG = "self.micromagic.view";
   public static final String APPDATA_TAG = "self.micromagic.appData";

   public static final QueryTool queryTool = new QueryTool();
   public static final AppTool appTool = new AppTool();

   public static final String SERVER_ROOT_TAG = "self.micromagic.server.contextRoot";

   public static final String SESSION_SECURITY_MANAGER = "self.micromagic.security.manager.";
   public static final String SECURITY_NAME = "security";
   public static final String NORIGHT_PAGE = "noright";
   public static final String ERROR_PAGE = "error";
   public static final String ERROR_404 = "error404";
   public static final String MESSAGE_PAGE = "message";
   public static final String LIST_PAGE = "list";
   public static final String EXCEL_PAGE = "excel";
   public static final String LIST_METHOD = "listMethod";

   public static final String PERFORM_IN_APPLICATION = Utility.getProperty(
         "self.micromagic.app_perform", "do");
   public static final String METHOD_NAME = Utility.getProperty(
         "self.micromagic.method_name", "method");

   public static class AppTool
   {
      /**
       * ʹ��һ��Map�����е�������Ϊ����Ĳ���������һ��AppData.
       *
       * @param param   �������Ĳ�����Map����
       */
      public AppData getAppData(Map param)
      {
         return this.getAppData(param, 0, null);
      }

      /**
       * ʹ��һ��Map�����е�������Ϊ����Ĳ���������һ��AppData.
       *
       * @param param            �������Ĳ�����Map����
       * @param appendPosition   ��Ӷ���ĵ�ǰλ����Ϣ
       * @param oldAppData       һ������, ������뽫���ԭ����AppData���ݵ�����
       *                         �������������ͬ��ԭ��û��AppData, �򲻻ᱸ��
       */
      public AppData getAppData(Map param, int appendPosition, ObjectRef oldAppData)
      {
         AppData tmpData = AppData.getCurrentData();
         if (oldAppData != null)
         {
            if (tmpData.maps[AppData.REQUEST_PARAMETER_MAP] != null
                  && tmpData.maps[AppData.REQUEST_PARAMETER_MAP] != param)
            {
               oldAppData.setObject(tmpData);
               tmpData = new AppData();
               ThreadCache.getInstance().setProperty(AppData.CACHE_NAME, tmpData);
            }
         }
         if (tmpData.maps[AppData.REQUEST_PARAMETER_MAP] != param)
         {
            tmpData.clearData();
            tmpData.position |= appendPosition;
            tmpData.maps[AppData.REQUEST_PARAMETER_MAP] = param == null ? new HashMap() : param;
            tmpData.maps[AppData.REQUEST_ATTRIBUTE_MAP] = new HashMap();
            tmpData.maps[AppData.SESSION_ATTRIBUTE_MAP] = new HashMap();
         }
         return tmpData;
      }

      /**
       * ʹ��һ��HttpServletRequest����������һ��AppData.
       *
       * @param request   ���������HttpServletRequest����
       */
      public AppData getAppData(HttpServletRequest request)
      {
         return this.getAppData(request, 0, null);
      }

      /**
       * ʹ��һ��HttpServletRequest����������һ��AppData.
       *
       * @param request          ���������HttpServletRequest����
       * @param appendPosition   ��Ӷ���ĵ�ǰλ����Ϣ
       * @param oldAppData       һ������, ������뽫���ԭ����AppData���ݵ�����
       *                         ���ServletRequest���������ͬ��ԭ��û��AppData, �򲻻ᱸ��
       */
      public AppData getAppData(HttpServletRequest request, int appendPosition, ObjectRef oldAppData)
      {
         AppData tmpData = AppData.getCurrentData();
         if (oldAppData != null)
         {
            if (tmpData.maps[AppData.REQUEST_PARAMETER_MAP] != null && tmpData.request != request)
            {
               oldAppData.setObject(tmpData);
               tmpData = new AppData();
               ThreadCache.getInstance().setProperty(AppData.CACHE_NAME, tmpData);
            }
         }
         if (tmpData.request != request)
         {
            tmpData.clearData();
            tmpData.position |= appendPosition;
            tmpData.contextRoot = request.getContextPath();
            tmpData.request = request;
            tmpData.maps[AppData.REQUEST_PARAMETER_MAP] = request.getParameterMap();
            tmpData.maps[AppData.REQUEST_ATTRIBUTE_MAP]
                  = ValueContainerMap.createRequestAttributeMap(request);
            tmpData.maps[AppData.SESSION_ATTRIBUTE_MAP]
                  = ValueContainerMap.createSessionAttributeMap(request);
         }
         return tmpData;
      }

      /**
       * �������AppData�ָ����̵߳Ļ�����.
       *
       * @param data  Ҫ�ָ���AppData����
       */
      public void resumeAppData(AppData data)
      {
         if (data != null)
         {
            ThreadCache.getInstance().setProperty(AppData.CACHE_NAME, data);
         }
      }

      /**
       * ִ��һ��model.
       *
       * @param data         ִ��modelʱ��Ҫ�õ������ݶ���
       * @param conn         ��ǰ���ڵ�һ��Ĭ������Դ�����ݿ�����, �����ǰ��������Ӧ
       *                     �����ݿ�����, ����Ը�<code>null</code>
       * @param factory      Ҫִ�е�model���ڵĹ���
       * @param modelName    Ҫִ�е�model������
       * @return    modelִ�����, ��Ҫת���export, ���ִ�е�modelδ������Ӧ��export
       *            �򷵻�<code>null</code>
       */
      public ModelExport callModel(AppData data, Connection conn, EternaFactory factory,
            String modelName)
            throws ConfigurationException, SQLException, IOException
      {
         return this.callModel(data, conn, factory, modelName, false);
      }

      /**
       * ִ��һ��model.
       *
       * @param data         ִ��modelʱ��Ҫ�õ������ݶ���
       * @param conn         ��ǰ���ڵ�һ��Ĭ������Դ�����ݿ�����, �����ǰ��������Ӧ
       *                     �����ݿ�����, ����Ը�<code>null</code>
       * @param factory      Ҫִ�е�model���ڵĹ���
       * @param modelName    Ҫִ�е�model������
       * @param noJump       ���ִ���ʱ�Ƿ�������(��: �׳��쳣), ��Ϊ<code>true</code>
       *                     ʱ, �κ��������������
       * @return    modelִ�����, ��Ҫת���export, ���ִ�е�modelδ������Ӧ��export
       *            �򷵻�<code>null</code>
       */
      public ModelExport callModel(AppData data, Connection conn, EternaFactory factory,
            String modelName, boolean noJump)
            throws ConfigurationException, SQLException, IOException
      {
         ObjectRef preConn = (ObjectRef) data.getSpcialData(ModelAdapter.MODEL_CACHE, ModelAdapter.PRE_CONN);
         if (preConn == null)
         {
            preConn = new ObjectRef(conn);
            data.addSpcialData(ModelAdapter.MODEL_CACHE, ModelAdapter.PRE_CONN, preConn);
         }
         ModelAdapter tmpModel = factory.createModelAdapter(modelName);
         int tType = tmpModel.getTransactionType();
         if (noJump)
         {
            try
            {
               return factory.getModelCaller().callModel(data, tmpModel, null, tType, preConn);
            }
            catch (Throwable ex)
            {
               Utility.createLog("pay").error("Error in call model", ex);
               return tmpModel.getErrorExport();
            }
         }
         else
         {
            return factory.getModelCaller().callModel(data, tmpModel, null, tType, preConn);
         }
      }

   }

   public static class QueryTool
         implements EternaInitialize
   {
      private EternaFactory factory;

      public QueryTool()
      {
         FactoryManager.Instance instance = FactoryManager.getGlobalFactoryManager();
         instance.addInitializedListener(this);
      }

      public QueryTool(EternaFactory factory)
      {
         try
         {
            FactoryManager.Instance instance;
            if (factory == null)
            {
               instance = FactoryManager.getGlobalFactoryManager();
            }
            else
            {
               instance = factory.getFactoryManager();
            }
            instance.addInitializedListener(this);
         }
         catch (ConfigurationException ex)
         {
            log.error("Error when create QueryTool.", ex);
         }
      }

      private void afterEternaInitialize(FactoryManager.Instance instance)
            throws ConfigurationException
      {
         this.factory = instance.getEternaFactory();
      }

      public QueryAdapter getQueryAdapter(String name, String sql, String[] paramTypes,
            String readerManager, String[] readerTypes)
            throws ConfigurationException
      {
         try
         {
            QueryAdapter query = factory.createQueryAdapter(name);
            return query;
         }
         catch (ConfigurationException ex)
         {
            QueryAdapterImpl impl = new QueryAdapterImpl();
            impl.setName(name);
            impl.setPreparedSQL(sql);
            if (paramTypes != null)
            {
               for (int i = 0; i < paramTypes.length; i++)
               {
                  SQLParameterGenerator spg = new SQLParameterGeneratorImpl();
                  spg.setName("param" + (i + 1));
                  spg.setParamType(paramTypes[i]);
                  impl.addParameter(spg);
               }
            }
            if (readerManager != null)
            {
               impl.setReaderManagerName(readerManager);
            }
            if (readerTypes != null)
            {
               ResultReaderGeneratorImpl rg = new ResultReaderGeneratorImpl();
               for (int i = 0; i < readerTypes.length; i++)
               {
                  rg.setName("col" + (i + 1));
                  rg.setColumnIndex(i + 1);
                  rg.setType(readerTypes[i]);
                  impl.addResultReader(rg.createReader());
               }
            }
            this.factory.registerQueryAdapter(impl);
            return impl.createQueryAdapter();
         }
      }

      public ResultIterator executeQuery(QueryAdapter query, String[] params, Connection conn)
            throws ConfigurationException, SQLException
      {
         if (params != null && params.length > 0)
         {
            Iterator itr = query.getParameterIterator();
            for (int i = 0; i < params.length; i++)
            {
               SQLParameter param = (SQLParameter) itr.next();
               query.setValuePreparer(param.createValuePreparer(params[i]));
            }
         }
         return query.executeQuery(conn);
      }

      public ResultIterator executeQuery(String name, String sql, String readerManager, String[] readerTypes,
            Connection conn)
            throws ConfigurationException, SQLException
      {
         QueryAdapter query = this.getQueryAdapter(name, sql, null,
               readerManager, readerTypes);
         return this.executeQuery(query, null, conn);
      }

      public ResultIterator executeQuery(String name, String sql, Connection conn)
            throws ConfigurationException, SQLException
      {
         return this.executeQuery(name, sql, null, null,conn);
      }

      public ResultRow getFirstRow(ResultIterator ritr)
            throws SQLException, ConfigurationException
      {
         if (ritr == null)
         {
            return null;
         }
         if (ritr.hasMoreRow())
         {
            return ritr.nextRow();
         }
         return null;
      }

      public ResultRow getFirstRow(QueryAdapter query, Connection conn)
            throws ConfigurationException, SQLException
      {
         return this.getFirstRow(this.executeQuery(query, null, conn));
      }

      public ResultRow getFirstRow(QueryAdapter query, String[] params, Connection conn)
            throws ConfigurationException, SQLException
      {
         return this.getFirstRow(this.executeQuery(query, params, conn));
      }

      public ResultRow getFirstRow(String name, String sql, String[] paramTypes, String[] params,
            String readerManager, String[] readerTypes, Connection conn)
            throws ConfigurationException, SQLException
      {
         QueryAdapter query = this.getQueryAdapter(name, sql, paramTypes,
               readerManager, readerTypes);
         ResultIterator ritr = this.executeQuery(query, params, conn);
         return this.getFirstRow(ritr);
      }

      public ResultRow getFirstRow(String name, String sql, String[] paramTypes, String[] params,
            Connection conn)
            throws ConfigurationException, SQLException
      {
         return this.getFirstRow(name, sql, paramTypes, params, null, null, conn);
      }

   }

}

