
package self.micromagic.eterna.tag;

import java.util.Map;
import java.util.HashMap;
import java.sql.SQLException;

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletRequest;

import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.model.ModelExport;
import self.micromagic.eterna.digester.FactoryManager;
import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.view.ViewAdapter;
import self.micromagic.util.container.ThreadCache;
import self.micromagic.util.container.ValueContainerMap;
import self.micromagic.util.container.RequestParameterMap;

/**
 * 在JSP中, 可通过此标签在页面中定义一个eterna对象.
 */
public class EternaDefine extends TagSupport
{
   /**
    * 默认的空界面的名称.
    */
   public static final String EMPTY_VIEW_FLAG = "empty.view";

   /**
    * 在上下文环境中获得工厂实例的名称前缀.
    */
   public static final String CONTEXT_FMI_PREFIX = "$context.";

   /**
    * 在_eterna.cache中存放区分多个同名控件时使用的后缀的名称.
    */
   public static final String SUFFIX_ID_FLAG = "eSuffixId";

   /**
    * 在_eterna.cache中存放根控件的名称.
    */
   public static final String ROOT_OBJ_ID_FLAG = "eRootObjId";

   /**
    * 在_eterna.cache中存放模板根控件标记的名称.
    */
   public static final String SCATTER_FLAG = "scatterFlag";

   private String name;
   private String instanceName;
   private String modelName;
   private String param;
   private String viewName;
   private String data;
   private String parentElement;
   private String suffixId;
   private boolean useAJAX;
   private String scatterFlag;

   public int doStartTag()
         throws JspException
   {
      AppData oldData = AppData.getCurrentData();
      try
      {
         ServletRequest req = this.pageContext.getRequest();
         HttpServletRequest request = null;
         if (req instanceof HttpServletRequest)
         {
            request = (HttpServletRequest) req;
         }
         AppData nowData = new AppData();
         ThreadCache.getInstance().setProperty(AppData.CACHE_NAME, nowData);
         nowData.position = AppData.POSITION_SERVLET;
         nowData.response = this.pageContext.getResponse();
         nowData.request = req;
         nowData.servletConfig = this.pageContext.getServletConfig();
         nowData.maps[AppData.REQUEST_ATTRIBUTE_MAP] = ValueContainerMap.createRequestAttributeMap(req);
         Map paramMap = null;
         if (this.param != null)
         {
            Object obj = this.pageContext.findAttribute(this.param);
            if (obj == null)
            {
               DefaultFinder.log.warn("Not found param:[" + this.param + "].");
            }
            else
            {
               if (obj instanceof Map)
               {
                  paramMap = (Map) obj;
               }
               else
               {
                  DefaultFinder.log.warn("Error param type:[" + obj.getClass() + "].");
               }
            }
         }
         if (paramMap == null)
         {
            nowData.maps[AppData.REQUEST_PARAMETER_MAP] = RequestParameterMap.create(request);
         }
         else
         {
            nowData.maps[AppData.REQUEST_PARAMETER_MAP] = RequestParameterMap.create(paramMap);
         }
         if (request != null)
         {
            nowData.contextRoot = request.getContextPath();
            nowData.dataMap.put("servletPath", request.getServletPath());
            nowData.maps[AppData.SESSION_ATTRIBUTE_MAP] = ValueContainerMap.createSessionAttributeMap(request);
         }
         else
         {
            nowData.maps[AppData.SESSION_ATTRIBUTE_MAP] = new HashMap();
         }


         EternaFactory f = this.getEternaFactory();
         String tmpViewName = this.viewName;
         if (this.modelName != null)
         {
            nowData.modelName = this.modelName;
            ModelExport export = f.getModelCaller().callModel(nowData);
            while (export != null && export.getModelName() != null)
            {
               nowData.modelName = export.getModelName();
               export = f.getModelCaller().callModel(nowData);
               nowData.export = export;
            }
            if (export != null && tmpViewName == null)
            {
               tmpViewName = export.getViewName();
            }
         }
         if (tmpViewName == null)
         {
            tmpViewName = EMPTY_VIEW_FLAG;
         }
         if (this.data != null)
         {
            Object obj = this.pageContext.findAttribute(this.data);
            if (obj == null)
            {
               DefaultFinder.log.warn("Not found data:[" + this.data + "].");
            }
            else
            {
               if (obj instanceof Map)
               {
                  nowData.dataMap.putAll((Map) obj);
               }
               else
               {
                  DefaultFinder.log.warn("Error data type:[" + obj.getClass() + "].");
               }
            }
         }
         ViewAdapter view = f.createViewAdapter(tmpViewName);
         JspWriter out = this.pageContext.getOut();
         String dataType = view.getDataType(nowData);
         if (!ViewAdapter.DATA_TYPE_WEB.equals(dataType))
         {
            view.printView(out, nowData, this.getCacheMap(view));
         }
         else
         {
            out.println("<script language=\"javascript\">");
            out.println("(function() {");
            out.print("var $E = ");
            view.printView(out, nowData, this.getCacheMap(view));
            out.println(";");
            out.println("var eternaData = $E;");
            out.println("var eterna_debug = " + view.getDebug() + ";");
            out.println("var _eterna = new Eterna(eternaData, eterna_debug, null);");
            if (this.useAJAX)
            {
               out.println("_eterna.cache.useAJAX = true;");
            }
            if (this.parentElement != null)
            {
               out.println("jQuery(document).ready(function(){");
               out.println("_eterna.reInit();");
               out.println("});");
            }
            out.println("window." + this.name + " = _eterna;");
            out.println("})();");
            out.println("</script>");
         }
      }
      catch (ConfigurationException ex)
      {
         DefaultFinder.log.warn("Error in service.", ex);
      }
      catch (SQLException ex)
      {
         DefaultFinder.log.warn("SQL Error in service.", ex);
      }
      catch (Throwable ex)
      {
         DefaultFinder.log.error("Other Error in service.", ex);
      }
      finally
      {
         ThreadCache.getInstance().setProperty(AppData.CACHE_NAME, oldData);
      }
      return SKIP_BODY;
   }

   private Map getCacheMap(ViewAdapter view)
         throws ConfigurationException
   {
      Map cache = new HashMap();
      if (this.suffixId != null)
      {
         cache.put(SUFFIX_ID_FLAG, this.suffixId);
      }
      if (this.parentElement != null)
      {
         cache.put(ROOT_OBJ_ID_FLAG, this.parentElement);
      }
      if (this.scatterFlag != null)
      {
         cache.put(SCATTER_FLAG, this.scatterFlag);
      }
      String width = view.getWidth();
      String height = view.getHeight();
      if (width != null)
      {
         cache.put(ROOT_OBJ_ID_FLAG + ".width", width);
      }
      if (height != null)
      {
         cache.put(ROOT_OBJ_ID_FLAG + ".height", height);
      }
      return cache.size() > 0 ? cache : null;
   }

   private EternaFactory getEternaFactory()
         throws ConfigurationException
   {
      if (this.instanceName != null)
      {
         if (this.instanceName.startsWith(CONTEXT_FMI_PREFIX))
         {
            FactoryManager.Instance instance = (FactoryManager.Instance) this.pageContext.findAttribute(
                  this.instanceName.substring(CONTEXT_FMI_PREFIX.length()));
            if (instance != null)
            {
               return instance.getEternaFactory();
            }
            else
            {
               DefaultFinder.log.error("Not found factory [" + this.instanceName + "] in context.");
            }
         }
         else
         {
            FactoryManager.Instance instance = DefaultFinder.finder.findInstance(this.instanceName);
            if (instance != null)
            {
               return instance.getEternaFactory();
            }
            else
            {
               DefaultFinder.log.error("Not found factory [" + this.instanceName + "].");
            }
         }
      }
      return FactoryManager.getEternaFactory();
   }

   public void release()
   {
      this.name = null;
      this.instanceName = null;
      this.modelName = null;
      this.param = null;
      this.viewName = null;
      this.data = null;
      this.parentElement = null;
      this.suffixId = null;
      this.useAJAX = false;
      this.scatterFlag = null;
      super.release();
   }

   public String getName()
   {
      return this.name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public String getInstanceName()
   {
      return this.instanceName;
   }

   public void setInstanceName(String instanceName)
   {
      this.instanceName = instanceName;
   }

   public String getModelName()
   {
      return this.modelName;
   }

   public void setModelName(String modelName)
   {
      this.modelName = modelName;
   }

   public String getParam()
   {
      return this.param;
   }

   public void setParam(String param)
   {
      this.param = param;
   }

   public String getViewName()
   {
      return this.viewName;
   }

   public void setViewName(String viewName)
   {
      this.viewName = viewName;
   }

   public String getData()
   {
      return this.data;
   }

   public void setData(String data)
   {
      this.data = data;
   }

   public String getParentElement()
   {
      return this.parentElement;
   }

   public void setParentElement(String parentElement)
   {
      this.parentElement = parentElement;
   }

   public String getSuffixId()
   {
      return this.suffixId;
   }

   public void setSuffixId(String suffixId)
   {
      this.suffixId = suffixId;
   }

   public boolean isUseAJAX()
   {
      return this.useAJAX;
   }

   public void setUseAJAX(boolean useAJAX)
   {
      this.useAJAX = useAJAX;
   }

   public String getScatterFlag()
   {
      return this.scatterFlag;
   }

   public void setScatterFlag(String scatterFlag)
   {
      this.scatterFlag = scatterFlag;
   }

}
