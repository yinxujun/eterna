
package self.micromagic.app;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.SQLException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.digester.FactoryManager;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.model.ModelCaller;
import self.micromagic.eterna.model.ModelExport;
import self.micromagic.eterna.view.ViewAdapter;
import self.micromagic.util.Utility;
import self.micromagic.util.StringAppender;
import self.micromagic.util.StringTool;
import self.micromagic.util.container.ValueContainerMap;
import self.micromagic.util.container.RequestParameterMap;

public class EternaServlet extends HttpServlet
      implements WebApp
{
   protected FactoryManager.Instance factoryManager = null;
   protected String defaultModel = "index";
   private String charset = "UTF-8";
   protected boolean initFactoryManager = true;

   public void init(ServletConfig config)
         throws ServletException
   {
      super.init(config);
      if (this.initFactoryManager)
      {
         String initFiles = config.getInitParameter(FactoryManager.CONFIG_INIT_FILES);
         String parentFiles = config.getInitParameter(FactoryManager.CONFIG_INIT_PARENTFILES);
         this.factoryManager = FactoryManager.createClassFactoryManager(
               this.getClass(), this, initFiles,
               parentFiles == null ? null : new String[]{parentFiles}, false);
      }
      String tmp = config.getInitParameter(DEFAULT_MODEL_TAG);
      if (tmp != null)
      {
         this.defaultModel = config.getInitParameter(DEFAULT_MODEL_TAG);
      }

      String temp = config.getInitParameter("charset");
      if (temp != null)
      {
         this.charset = temp;
      }
      else
      {
         temp = Utility.getProperty(Utility.CHARSET_TAG);
         if (temp != null && !temp.equals(this.charset))
         {
            this.charset = temp;
         }
      }
   }

   protected FactoryManager.Instance getFactoryManager()
         throws ConfigurationException
   {
      return this.factoryManager;
   }

   public static String getServerRoot(HttpServletRequest request)
   {
      HttpSession session = request.getSession();
      String serverRoot = (String) session.getAttribute(SERVER_ROOT_TAG);
      if (serverRoot == null)
      {
         serverRoot = request.getScheme() + "://" + request.getServerName()
               + ":" + request.getServerPort() + request.getContextPath();
         session.setAttribute(SERVER_ROOT_TAG, serverRoot);
      }
      return serverRoot;
   }

   protected void service(HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException
   {
      if (!this.charset.equals(request.getCharacterEncoding()))
      {
         request.setCharacterEncoding(this.charset);
      }
      response.setContentType("text/html;charset=" + this.charset);
      AppData data = AppData.getCurrentData();
      data.request = request;
      data.response = response;
      data.contextRoot = request.getContextPath();
      data.servletConfig = this.getServletConfig();
      data.position = AppData.POSITION_SERVLET;
      try
      {
         data.maps[AppData.REQUEST_PARAMETER_MAP] = RequestParameterMap.create(request);
         data.maps[AppData.REQUEST_ATTRIBUTE_MAP] = ValueContainerMap.createRequestAttributeMap(request);
         data.maps[AppData.SESSION_ATTRIBUTE_MAP] = ValueContainerMap.createSessionAttributeMap(request);

         String queryStr = request.getQueryString();
         if (queryStr != null)
         {
            String modelNameTag = this.getFactoryManager().getEternaFactory().getModelNameTag();
            int index;
            int plusCount = 2;
            if (queryStr.length() > 0 && queryStr.charAt(0) != '?')
            {
               if (queryStr.startsWith(modelNameTag + "="))
               {
                  index = 0;
                  plusCount = 1;
               }
               else
               {
                  index = -1;
               }
            }
            else
            {
               index = queryStr.indexOf("?" + modelNameTag + "=");
            }
            if (index == -1)
            {
               index = queryStr.indexOf("&" + modelNameTag + "=");
            }
            if (index != -1)
            {
               int endIndex = queryStr.indexOf('&', index + 1);
               if (endIndex != -1)
               {
                  data.modelName = queryStr.substring(index + modelNameTag.length() + plusCount, endIndex);
               }
               else
               {
                  data.modelName = queryStr.substring(index + modelNameTag.length() + plusCount);
               }
            }
         }

         if (this.defaultModel != null)
         {
            request.setAttribute(ModelCaller.DEFAULT_MODEL_TAG, this.defaultModel);
         }
         else
         {
            request.setAttribute(ModelCaller.DEFAULT_MODEL_TAG, ModelCaller.DEFAULT_MODEL_NAME);
         }

         ModelExport export = this.getFactoryManager().getEternaFactory().getModelCaller().callModel(data);
         if (export != null)
         {
            data.export = export;
         }
      }
      catch (ConfigurationException ex)
      {
         log.warn("Error in service.", ex);
      }
      catch (SQLException ex)
      {
         log.warn("SQL Error in service.", ex);
      }
      catch (Throwable ex)
      {
         log.error("Other Error in service.", ex);
      }
      finally
      {
         try
         {
            if (data.export != null)
            {
               this.doExport(data, request, response);
            }
         }
         catch (Throwable ex)
         {
            log.error("Error in doExport.", ex);
         }
         data.modelName = null;
         data.export = null;
         data.servletConfig = null;
         data.clearData();
      }
   }

   protected void doExport(AppData data, HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException, ConfigurationException
   {
      if (data.export == null)
      {
         return;
      }
      ModelExport export = data.export;
      if (export.isRedirect())
      {
         if (export.getModelName() != null)
         {
            StringAppender url = StringTool.createStringAppender(512);
            url.append(request.getScheme()).append("://").append(request.getHeader("host"));
            url.append(request.getContextPath()).append(request.getServletPath());
            url.append("?").append(this.getFactoryManager().getEternaFactory().getModelNameTag());
            url.append("=").append(URLEncoder.encode(export.getModelName(), this.charset));
            url.append("&");
            url.append(this.getFactoryManager().getEternaFactory().getModelCaller()
                  .prepareParam(data, this.charset));
            response.sendRedirect(url.toString());
         }
         else if (export.getPath() != null)
         {
            String path = export.getPath();
            StringAppender url = StringTool.createStringAppender(512);
            if (path.startsWith("/"))
            {
               url.append(request.getScheme()).append("://").append(request.getHeader("host"))
                     .append(request.getContextPath());
            }
            url.append(path);
            if (path.indexOf('?') != -1)
            {
               url.append("&");
            }
            else
            {
               url.append("?");
            }
            url.append(this.getFactoryManager().getEternaFactory().getModelCaller()
                  .prepareParam(data, this.charset));
            response.sendRedirect(url.toString());
         }
         return;
      }
      request.setAttribute(APPDATA_TAG, data);
      try
      {
         if (export.getViewName() != null)
         {
            ViewAdapter view = this.getFactoryManager().getEternaFactory().createViewAdapter(export.getViewName());
            request.setAttribute(VIEW_TAG, view);
         }
      }
      catch (ConfigurationException ex)
      {
         log.warn("Error in doExport.", ex);
      }
      data.dataMap.put("servletPath", request.getServletPath());
      request.getRequestDispatcher(export.getPath()).include(request, response);
   }

   public void destroy()
   {
      super.destroy();
      try
      {
         if (this.getFactoryManager() != null)
         {
            this.getFactoryManager().destroy();
         }
      } catch (ConfigurationException ex) {}
   }

}
