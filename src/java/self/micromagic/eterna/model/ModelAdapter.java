
package self.micromagic.eterna.model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.EternaFactory;

public interface ModelAdapter
{
   /**
    * model���������.
    *
    * @see    self.micromagic.eterna.model.AppData#getSpcialDataMap(String)
    */
   public static final String MODEL_CACHE = "model.cache";

   /**
    * model�����д洢��ǰ����ʹ�õ����ݿ����ӵ�����.
    */
   public static final String PRE_CONN = "preConn";

   /**
    * model�����д洢ҵ���Ƿ��ѽӹ������ݿ����ӵĿ���Ȩ. <p>
    * ��������:
    * data.addSpcialData(ModelAdapter.MODEL_CACHE, ModelAdapter.CONN_HOLDED, "1");
    *
    * @see    self.micromagic.eterna.model.AppData#addSpcialData(String, String, Object)
    */
   public static final String CONN_HOLDED = "connHolded";

   /**
    * ������ǰ��ִ�е�model.
    * ����һ��factory��arrtibute, ����ֵ���Ǹ�ǰ��model������.
    */
   public static final String FRONT_MODEL_ATTRIBUTE = "front.model.name";

   /**
    * ��Ҫ����һ����������������񣬼������е�����
    */
   public static final int T_REQUARED = 0;

   /**
    * ǿ�ƿ���һ�����������������񣬹�����������
    */
   public static final int T_NEW = 1;

   /**
    * ����Ҫ��������������񣬹�����������
    */
   public static final int T_NONE = 2;

   /**
    * ����Ҫ���񣬲��������ӣ���Ӧ���Լ��ͷ�
    */
   public static final int T_HOLD = 3;

   /**
    * �������ӣ������������ʹ�����е�����
    */
   public static final int T_NOTNEED  = 4;

   /**
    * ����������״̬��������Ҳ�������ύ��ع�
    */
   public static final int T_IDLE  = 5;

   /**
    * Ĭ�ϵĳ�����ڵ�����
    */
   public static final String DEFAULT_ERROR_EXPORT_NAME = "defaultErrorExport";


   String getName() throws ConfigurationException;

   EternaFactory getFactory() throws ConfigurationException;

   boolean isKeepCaches() throws ConfigurationException;

   boolean isNeedFrontModel() throws ConfigurationException;

   String getFrontModelName() throws ConfigurationException;

   int getTransactionType() throws ConfigurationException;

   String getDataSourceName() throws ConfigurationException;

   boolean checkPosition(AppData data) throws ConfigurationException;

   ModelExport getModelExport() throws ConfigurationException;

   ModelExport getErrorExport() throws ConfigurationException;

   ModelExport doModel(AppData data, Connection conn)
         throws ConfigurationException, SQLException, IOException;

}
