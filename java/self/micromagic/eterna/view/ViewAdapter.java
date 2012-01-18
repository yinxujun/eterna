
package self.micromagic.eterna.view;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.share.EternaFactory;

public interface ViewAdapter
{
   /**
    * 基本的debug等级, 这个等级之上的debug才会输出信息
    */
   public static final int ETERNA_VIEW_DEBUG_BASE = 0x10;

   /**
    * view缓存的名称.
    *
    * @see    self.micromagic.eterna.model.AppData#getSpcialDataMap(String)
    */
   public static final String VIEW_CACHE = "view.cache";

   /**
    * 是否是动态视图的标记. <p>
    * 如果是动态视图的话, 请在Component的实现类的print方法中,调用
    * data.addSpcialData(ViewAdapter.VIEW_CACHE, ViewAdapter.DYNAMIC_VIEW, "1")
    * 用于标识该视图是动态的不可缓存.
    *
    * @see    self.micromagic.eterna.model.AppData#addSpcialData(String, String, Object)
    */
   public static final String DYNAMIC_VIEW = "dynamic.view";

   /**
    * 动态方法map的标识. <p>
    * 如果是动态视图的话且有动态的方法调用, 那需要把这些动态方法添加到这个标识下的
    * map中. 可以调用如下方法进行添加:
    * BaseManager.addDynamicFunction(Map)
    *
    * @see    self.micromagic.eterna.model.AppData#addSpcialData(String, String, Object)
    * @see    self.micromagic.eterna.view.BaseManager#addDynamicFunction(Map)
    */
   public static final String DYNAMIC_FUNCTIONS = "dynamic.functions";

   /**
    * 动态资源文本set的标识. <p>
    * 如果是动态视图的话且有动态的资源文本引用, 那需要把这些动态资源文本的名称添加到
    * 这个标识下的set中. 可以调用如下方法进行添加:
    * BaseManager.addDynamicResourceName(String)
    *
    * @see    self.micromagic.eterna.model.AppData#addSpcialData(String, String, Object)
    * @see    self.micromagic.eterna.view.BaseManager#addDynamicResourceName(String)
    */
   public static final String DYNAMIC_RESOURCE_NAMES = "dynamic.resource.names";

   /**
    * 已使用的typical控件.
    */
   public static final String USED_TYPICAL_COMPONENTS = "used.TypicalComponents";

   /**
    * 当前的typical控件.
    */
   public static final String TYPICAL_COMPONENTS_MAP = "TypicalComponents_MAP";

   public static final String DATA_TYPE = "dataType";
   public static final String DATA_TYPE_ONLYRECORD = "onlyRecord";
   public static final String DATA_TYPE_ALL = "all";
   public static final String DATA_TYPE_WEB = "web";

   String getName() throws ConfigurationException;

   EternaFactory getFactory() throws ConfigurationException;

   Iterator getComponents() throws ConfigurationException;

   int getDebug() throws ConfigurationException;

   String getWidth() throws ConfigurationException;

   String getHeight() throws ConfigurationException;

   String getBeforeInit() throws ConfigurationException;

   String getInitScript() throws ConfigurationException;

   /**
    * 获取本view适配器某个设置的属性.
    */
   Object getAttribute(String name) throws ConfigurationException;

   /**
    * 获取本view适配器设置的所有属性的名称.
    */
   String[] getAttributeNames() throws ConfigurationException;

   ViewRes getViewRes() throws ConfigurationException;

   void printView(Writer out, AppData data) throws IOException, ConfigurationException;

   /**
    * 输出控件的事件定义.
    * 由于要根据debug的等级，在事件脚本中加入调试代码，所以此功能在ViewAdapter中实现.
    */
   void printEvent(Writer out, AppData data, Component.Event event) throws IOException, ConfigurationException;

   interface ViewRes
   {
      public Map getFunctionMap() throws ConfigurationException;

      public Set getTypicalComponentNames() throws ConfigurationException;

      public Set getResourceNames() throws ConfigurationException;

   }

}
