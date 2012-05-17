
package self.micromagic.app;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.digester.FactoryManager;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.share.EternaInitialize;
import self.micromagic.eterna.share.ThreadCache;
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
   public final static Log log = Utility.createLog("app");

   public final static String DEFAULT_MODEL_TAG = "defaultModel";
   public final static String VIEW_TAG = "self.micromagic.view";
   public final static String APPDATA_TAG = "self.micromagic.appData";

   public final static QueryTool queryTool = new QueryTool();
   public final static AppTool appTool = new AppTool();

   public final static String SERVER_ROOT_TAG = "self.micromagic.server.contextRoot";

   public final static String SESSION_SECURITY_MANAGER = "self.micromagic.security.manager.";
   public final static String SECURITY_NAME = "security";
   public final static String NORIGHT_PAGE = "noright";
   public final static String ERROR_PAGE = "error";
   public final static String ERROR_404 = "error404";
   public final static String MESSAGE_PAGE = "message";
   public final static String LIST_PAGE = "list";
   public final static String EXCEL_PAGE = "excel";
   public final static String LIST_METHOD = "listMethod";

   public final static String PERFORM_IN_APPLICATION = Utility.getProperty(
         "self.micromagic.app_perform", "do");
   public final static String METHOD_NAME = Utility.getProperty(
         "self.micromagic.method_name", "method");

   public static class AppTool
   {
      /**
       * 使用一个Map对象中的数据作为请求的参数来构造一个AppData.
       *
       * @param param   存放请求的参数的Map对象
       */
      public AppData getAppData(Map param)
      {
         return this.getAppData(param, 0, null);
      }

      /**
       * 使用一个Map对象中的数据作为请求的参数来构造一个AppData.
       *
       * @param param            存放请求的参数的Map对象
       * @param appendPosition   添加额外的当前位置信息
       * @param oldAppData       一个出参, 如果传入将会把原来的AppData备份到里面
       *                         如果参数对象相同或原来没有AppData, 则不会备份
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
            tmpData.position = appendPosition;
            tmpData.maps[AppData.REQUEST_PARAMETER_MAP] = param == null ? new HashMap() : param;
            tmpData.maps[AppData.REQUEST_ATTRIBUTE_MAP] = new HashMap();
            tmpData.maps[AppData.SESSION_ATTRIBUTE_MAP] = new HashMap();
         }
         return tmpData;
      }

      /**
       * 使用一个HttpServletRequest对象来构造一个AppData.
       *
       * @param request   发起请求的HttpServletRequest对象
       */
      public AppData getAppData(HttpServletRequest request)
      {
         return this.getAppData(request, 0, null);
      }

      /**
       * 使用一个HttpServletRequest对象来构造一个AppData.
       *
       * @param request          发起请求的HttpServletRequest对象
       * @param appendPosition   添加额外的当前位置信息
       * @param oldAppData       一个出参, 如果传入将会把原来的AppData备份到里面
       *                         如果ServletRequest对象对象相同或原来没有AppData, 则不会备份
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
            tmpData.position = appendPosition;
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
       * 将传入的AppData恢复到线程的缓存中.
       *
       * @param data  要恢复的AppData对象
       */
      public void resumeAppData(AppData data)
      {
         if (data != null)
         {
            ThreadCache.getInstance().setProperty(AppData.CACHE_NAME, data);
         }
      }

   }

   public static class QueryTool
         implements EternaInitialize
   {
      private EternaFactory factory;

      public QueryTool()
      {
         try
         {
            FactoryManager.Instance instance = FactoryManager.getGlobalFactoryManager();
            instance.addInitializedListener(this);
            this.afterEternaInitialize(instance);
         }
         catch (ConfigurationException ex)
         {
            log.error("Error when create sql factory.", ex);
         }
      }

      private void afterEternaInitialize(FactoryManager.Instance instance)
            throws ConfigurationException
      {
         this.factory = instance.getEternaFactory();
      }

      public QueryTool(EternaFactory factory)
      {
         this.factory = factory;
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

