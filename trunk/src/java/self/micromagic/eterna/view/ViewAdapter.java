
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
    * Ĭ�ϵ����ݼ������������
    */
   public static final String DEFAULT_DATA_PRINTER_NAME = "dataPrinter";

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
    * ����Ƕ�̬��ͼ�Ļ����ж�̬�ķ�������, ����Ҫ����Щ��̬�������ӵ������ʶ�µ�
    * map��. ���Ե������·�����������:
    * BaseManager.addDynamicFunction(Map)
    *
    * @see    self.micromagic.eterna.model.AppData#addSpcialData(String, String, Object)
    * @see    self.micromagic.eterna.view.impl.ViewTool#addDynamicFunction(Map)
    */
   public static final String DYNAMIC_FUNCTIONS = "dynamic.functions";

   /**
    * ��̬��Դ�ı�set�ı�ʶ. <p>
    * ����Ƕ�̬��ͼ�Ļ����ж�̬����Դ�ı�����, ����Ҫ����Щ��̬��Դ�ı����������ӵ�
    * �����ʶ�µ�set��. ���Ե������·�����������:
    * BaseManager.addDynamicResourceName(String)
    *
    * @see    self.micromagic.eterna.model.AppData#addSpcialData(String, String, Object)
    * @see    self.micromagic.eterna.view.impl.ViewTool#addDynamicResourceName(String)
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


   /**
    * ͨ����������dataTypeֵʹ�õ�����.
    */
   public static final String DATA_TYPE = "___dataType";

   /**
    * ����Ҫ������ݲ��ֵ���������.
    * @deprecated
    */
   public static final String DATA_TYPE_ONLYRECORD = "data";

   /**
    * ����Ҫ������ݲ��ֵ���������.
    */
   public static final String DATA_TYPE_DATA = "data";

   /**
    * ȥ����ܵ����ݽṹ, ���������ݲ���, ������ΪREST�ķ���ֵ.
    */
   public static final String DATA_TYPE_REST = "REST";

   /**
    * �����ܽṹ����������.
    */
   public static final String DATA_TYPE_ALL = "all";

   /**
    * ���չ�ֵ�ҳ��.
    */
   public static final String DATA_TYPE_WEB = "web";

   /**
    * �ָ�json���ݺͺ����html���ݵı�ǩ.
    */
   public static final String JSON_SPLIT_FLAG = "<!-- eterna json data split -->";

   String getName() throws ConfigurationException;

   DataPrinter getDataPrinter() throws ConfigurationException;

   String getDefaultDataType() throws ConfigurationException;

   String getDataType(AppData data) throws ConfigurationException;

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

   /**
    * �����漰���ݼ���Ϣд�뵽�������.
    * �����<code>DATA_TYPE</code>�������������.
    *
    * @param out    ��Ϣд���������
    * @param data   ���ݼ����ڵ�<code>AppData</code>
    */
   void printView(Writer out, AppData data) throws IOException, ConfigurationException;

   /**
    * �����漰���ݼ���Ϣд�뵽�������.
    * �����<code>DATA_TYPE</code>�������������.
    *
    * @param out       ��Ϣд���������
    * @param data      ���ݼ����ڵ�<code>AppData</code>
    * @param cache     ��Ҫ��ʼ����_eterna.cache�е�ֵ
    */
   void printView(Writer out, AppData data, Map cache)
         throws IOException, ConfigurationException;

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