
package self.micromagic.app;

import java.io.IOException;
import java.sql.SQLException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletException;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.digester.FactoryManager;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.model.ModelCaller;
import self.micromagic.eterna.model.ModelExport;
import self.micromagic.eterna.view.ViewAdapter;

public class EternaPortlet extends GenericPortlet
      implements WebApp
{
   public static final String NEXT_MODEL_TAG = "nextModel";

   protected FactoryManager.Instance factoryManager = null;
   protected String defaultModel = "index";
   protected boolean initFactoryManager = true;

   public void init(PortletConfig config)
         throws PortletException
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
   }

   protected FactoryManager.Instance getFactoryManager()
         throws ConfigurationException
   {
      return this.factoryManager;
   }

   public static String getServerRoot(PortletRequest request)
   {
      PortletSession session = request.getPortletSession();
      String serverRoot = (String) session.getAttribute(SERVER_ROOT_TAG);
      if (serverRoot == null)
      {
         serverRoot = request.getScheme() + "://" + request.getServerName()
               + ":" + request.getServerPort() + request.getContextPath();
         session.setAttribute(SERVER_ROOT_TAG, serverRoot);
      }
      return serverRoot;
   }

   public void processAction(ActionRequest request, ActionResponse response)
         throws PortletException, IOException
   {
      AppData data = AppData.getCurrentData();
      data.contextRoot = request.getContextPath();
      data.actionRequest = request;
      data.actionResponse = response;
      data.portletConfig = this.getPortletConfig();
      data.position = AppData.POSITION_PORTLET_ACTION;

      if (this.defaultModel != null)
      {
         request.setAttribute(ModelCaller.DEFAULT_MODEL_TAG, this.defaultModel);
      }
      else
      {
         request.setAttribute(ModelCaller.DEFAULT_MODEL_TAG, ModelCaller.DEFAULT_MODEL_NAME);
      }
      try
      {
         data.maps[AppData.REQUEST_PARAMETER_MAP] = request.getParameterMap();
         data.maps[AppData.REQUEST_ATTRIBUTE_MAP] = PortletValueMap.createRequestAttributeMap(request);
         data.maps[AppData.SESSION_ATTRIBUTE_MAP] = PortletValueMap.createSessionAttributeMap(
               request, PortletSession.APPLICATION_SCOPE);
         data.export = this.getFactoryManager().getEternaFactory().getModelCaller().callModel(data);
         String nextModel = (String) request.getAttribute(NEXT_MODEL_TAG);
         if (nextModel != null)
         {
            data.modelName = nextModel;
         }
      }
      catch (ConfigurationException ex)
      {
         log.warn("Error in processAction.", ex);
      }
      catch (SQLException ex)
      {
         log.warn("SQL Error in processAction.", ex);
      }
      catch (Throwable ex)
      {
         log.error("Other Error in processAction.", ex);
      }
   }

   public void render(RenderRequest request, RenderResponse response)
         throws PortletException, IOException
   {
      response.setTitle(this.getTitle(request));
      AppData data = AppData.getCurrentData();
      try
      {
         data.contextRoot = request.getContextPath();
         data.renderRequest = request;
         data.renderResponse = response;
         data.portletConfig = this.getPortletConfig();
         data.position = AppData.POSITION_PORTLET_RENDER;
         if (data.export == null)
         {
            if (this.defaultModel != null)
            {
               request.setAttribute(ModelCaller.DEFAULT_MODEL_TAG, this.defaultModel);
            }
            else
            {
               request.setAttribute(ModelCaller.DEFAULT_MODEL_TAG, ModelCaller.DEFAULT_MODEL_NAME);
            }
            try
            {
               data.maps[AppData.REQUEST_PARAMETER_MAP] = request.getParameterMap();
               data.maps[AppData.REQUEST_ATTRIBUTE_MAP] = PortletValueMap.createRequestAttributeMap(request);
               data.maps[AppData.SESSION_ATTRIBUTE_MAP] = PortletValueMap.createSessionAttributeMap(
                     request, PortletSession.APPLICATION_SCOPE);
               ModelExport export = this.getFactoryManager().getEternaFactory().getModelCaller().callModel(data);
               if (export != null)
               {
                  data.export = export;
               }
            }
            catch (ConfigurationException ex)
            {
               log.warn("Error in render.", ex);
            }
            catch (SQLException ex)
            {
               log.warn("SQL Error in render.", ex);
            }
            catch (Throwable ex)
            {
               log.error("Other Error in render.", ex);
            }
         }
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
         data.portletConfig = null;
         data.clearData();
      }
   }

   protected void doExport(AppData data, RenderRequest request, RenderResponse response)
         throws PortletException, IOException
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
            // portlet ���޷��ض���, ����Export��modelʱ, ͨ��render��ʽ�ٵ������model
            data.modelName = export.getModelName();
            data.export = null;
            this.render(request, response);
            return;
         }
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
      data.dataMap.put("actionURL", response.createActionURL().toString());
      data.dataMap.put("renderURL", response.createRenderURL().toString());
      data.dataMap.put("portletRoot", response.encodeURL(""));
      this.getPortletContext().getRequestDispatcher(export.getPath()).include(request, response);
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