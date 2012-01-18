
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
    * ������debug�ȼ�, ����ȼ�֮�ϵ�debug�Ż������Ϣ
    */
   public static final int ETERNA_VIEW_DEBUG_BASE = 0x10;

   /**
    * view���������.
    *
    * @see    self.micromagic.eterna.model.AppData#getSpcialDataMap(String)
    */
   public static final String VIEW_CACHE = "view.cache";

   /**
    * �Ƿ��Ƕ�̬��ͼ�ı��. <p>
    * ����Ƕ�̬��ͼ�Ļ�, ����Component��ʵ�����print������,����
    * data.addSpcialData(ViewAdapter.VIEW_CACHE, ViewAdapter.DYNAMIC_VIEW, "1")
    * ���ڱ�ʶ����ͼ�Ƕ�̬�Ĳ��ɻ���.
    *
    * @see    self.micromagic.eterna.model.AppData#addSpcialData(String, String, Object)
    */
   public static final String DYNAMIC_VIEW = "dynamic.view";

   /**
    * ��̬����map�ı�ʶ. <p>
    * ����Ƕ�̬��ͼ�Ļ����ж�̬�ķ�������, ����Ҫ����Щ��̬������ӵ������ʶ�µ�
    * map��. ���Ե������·����������:
    * BaseManager.addDynamicFunction(Map)
    *
    * @see    self.micromagic.eterna.model.AppData#addSpcialData(String, String, Object)
    * @see    self.micromagic.eterna.view.BaseManager#addDynamicFunction(Map)
    */
   public static final String DYNAMIC_FUNCTIONS = "dynamic.functions";

   /**
    * ��̬��Դ�ı�set�ı�ʶ. <p>
    * ����Ƕ�̬��ͼ�Ļ����ж�̬����Դ�ı�����, ����Ҫ����Щ��̬��Դ�ı���������ӵ�
    * �����ʶ�µ�set��. ���Ե������·����������:
    * BaseManager.addDynamicResourceName(String)
    *
    * @see    self.micromagic.eterna.model.AppData#addSpcialData(String, String, Object)
    * @see    self.micromagic.eterna.view.BaseManager#addDynamicResourceName(String)
    */
   public static final String DYNAMIC_RESOURCE_NAMES = "dynamic.resource.names";

   /**
    * ��ʹ�õ�typical�ؼ�.
    */
   public static final String USED_TYPICAL_COMPONENTS = "used.TypicalComponents";

   /**
    * ��ǰ��typical�ؼ�.
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
    * ��ȡ��view������ĳ�����õ�����.
    */
   Object getAttribute(String name) throws ConfigurationException;

   /**
    * ��ȡ��view���������õ��������Ե�����.
    */
   String[] getAttributeNames() throws ConfigurationException;

   ViewRes getViewRes() throws ConfigurationException;

   void printView(Writer out, AppData data) throws IOException, ConfigurationException;

   /**
    * ����ؼ����¼�����.
    * ����Ҫ����debug�ĵȼ������¼��ű��м�����Դ��룬���Դ˹�����ViewAdapter��ʵ��.
    */
   void printEvent(Writer out, AppData data, Component.Event event) throws IOException, ConfigurationException;

   interface ViewRes
   {
      public Map getFunctionMap() throws ConfigurationException;

      public Set getTypicalComponentNames() throws ConfigurationException;

      public Set getResourceNames() throws ConfigurationException;

   }

}
