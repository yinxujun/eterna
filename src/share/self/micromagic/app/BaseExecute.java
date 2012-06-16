
package self.micromagic.app;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.model.Execute;
import self.micromagic.eterna.model.ModelAdapter;
import self.micromagic.eterna.model.ModelExport;
import self.micromagic.eterna.model.impl.AbstractExecute;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.share.Generator;
import self.micromagic.cg.ClassGenerator;
import self.micromagic.util.ObjectRef;
import org.apache.commons.logging.Log;

/**
 * ����ִ����. <p>
 * ʵ����<code>self.micromagic.eterna.model.Execute</code>�ӿ�.
 * �����ʼ������, ����дplusInit����.
 * �����ҵ���߼�, ����дdealProcess����.
 */
public class BaseExecute extends AbstractExecute
      implements Execute, Generator
{
   /**
    * ��ʶ�Ƿ���ʹ�����ݼ��е�ָ��ֵ����Ϊ����ֵ.
    */
   public final static String DATA_ATTRIBUTE_NAME_PREFIX = "$data.";

   /**
    * �����Ի��ڳ�ʼ��ʱ��ֵ, ������Ҫʱʹ��.
    */
   protected EternaFactory factory;

   /**
    * Ĭ��ֵ�ڳ�ʼ��ʱ����, Ϊ��ǰ������.
    */
   protected String executeType;

   /**
    * ��־��¼.
    */
   protected final static Log log = WebApp.log;

   /**
    * ʵ����<code>self.micromagic.eterna.model.Execute</code>�ӿڵĳ�ʼ������.
    * ������������ʼ������, ����дplusInit����.
    *
    * @see #plusInit
    */
   public void initialize(ModelAdapter model)
         throws ConfigurationException
   {
      if (this.initialized)
      {
         return;
      }
      super.initialize(model);
      this.factory = model.getFactory();
      this.executeType = "class:" + ClassGenerator.getClassName(this.getClass());
      this.plusInit();
   }

   /**
    * �����ʼ�����ݿ���д�˷���ʵ��.
    */
   protected void plusInit()
         throws ConfigurationException
   {
   }

   /**
    * ʵ����<code>self.micromagic.eterna.model.Execute</code>�ӿڵķ���,
    * ���ص�ǰִ����������.
    */
   public String getExecuteType()
         throws ConfigurationException
   {
      return this.executeType;
   }

   /**
    * ʵ����<code>self.micromagic.eterna.model.Execute</code>�ӿڵ�ִ�з���,
    * �����ҵ���߼�, ����дdealProcess����.
    *
    * @see #plusInit
    */
   public ModelExport execute(AppData data, Connection conn)
         throws ConfigurationException, SQLException, IOException
   {
      try
      {
         return this.dealProcess(data, conn);
      }
      catch (InnerExport e)
      {
         return e.export;
      }
   }

   /**
    * �����ҵ���߼�����д�˷���ʵ��.
    */
   protected ModelExport dealProcess(AppData data, Connection conn)
         throws ConfigurationException, SQLException, IOException, InnerExport
   {
      return null;
   }

   /**
    * ͨ��ָ��export��������ִ����ת.
    *
    * @param exportName    ִ����ת��export������
    */
   protected ModelExport doExport(String exportName)
         throws ConfigurationException, SQLException, IOException, InnerExport
   {
      ModelExport export = this.factory.getModelExport(exportName);
      if (export == null)
      {
            log.warn("The ModelExport [" + exportName + "] not found.");
      }
      else
      {
         throw new InnerExport(export);
      }
      return export;
   }

   /**
    * ͨ��ָ������������һ��model.
    *
    * @param modelName    Ҫ���õ�model������
    */
   protected ModelExport callModel(AppData data, Connection conn, String modelName)
         throws ConfigurationException, SQLException, IOException, InnerExport
   {
      return this.callModel(data, conn, modelName, false);
   }

   /**
    * ͨ��ָ������������һ��model.
    *
    * @param modelName    Ҫ���õ�model������
    * @param noJump       ��Ϊ<code>true</code>, ���κ��������������
    */
   protected ModelExport callModel(AppData data, Connection conn, String modelName, boolean noJump)
         throws ConfigurationException, SQLException, IOException, InnerExport
   {
      ObjectRef preConn = (ObjectRef) data.getSpcialData(ModelAdapter.MODEL_CACHE, ModelAdapter.PRE_CONN);
      ModelAdapter tmpModel = this.factory.createModelAdapter(modelName);
      int tType = tmpModel.getTransactionType();
      if (noJump)
      {
         try
         {
            return this.factory.getModelCaller().callModel(data, tmpModel, null, tType, preConn);
         }
         catch (Throwable ex)
         {
            log.error("Error in call model", ex);
            return null;
         }
      }
      else
      {
         ModelExport export = this.factory.getModelCaller().callModel(data, tmpModel, null, tType, preConn);
         if (export != null)
         {
            throw new InnerExport(export);
         }
         return null;
      }
   }

   /**
    * �������model����Ҫ����ת��һ��<code>ModelExport</code>, �����׳����쳣.
    */
   protected static class InnerExport extends RuntimeException
   {
      /**
       * Ҫ����ת���<code>ModelExport</code>.
       */
      public final ModelExport export;

      public InnerExport(ModelExport export)
      {
         this.export = export;
      }

   }

}
