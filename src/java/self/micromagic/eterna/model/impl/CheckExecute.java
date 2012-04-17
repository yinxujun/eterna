
package self.micromagic.eterna.model.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.io.IOException;
import java.util.Iterator;

import self.micromagic.eterna.model.CheckExecuteGenerator;
import self.micromagic.eterna.model.Execute;
import self.micromagic.eterna.model.ModelExport;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.model.ModelAdapter;
import self.micromagic.eterna.model.CheckOperator;
import self.micromagic.eterna.model.AppDataLogExecute;
import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.digester.ObjectCreateRule;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.util.StringTool;
import self.micromagic.util.ObjectRef;
import self.micromagic.util.Utility;
import org.dom4j.Element;

public class CheckExecute extends AbstractExecute
      implements Execute, CheckExecuteGenerator
{
   public static int MAX_LOOP_COUNT = 1024 * 32;

   static
   {
      try
      {
         Utility.addMethodPropertyManager(MAX_LOOP_COUNT_PROPERTY, CheckExecute.class, "setMaxLoopCount");
      }
      catch (Throwable ex)
      {
         log.warn("Error in init CheckExecute max loop count.", ex);
      }
   }

   protected static void setMaxLoopCount(String count)
   {
      try
      {
         MAX_LOOP_COUNT = Integer.parseInt(count);
      }
      catch (Exception ex) {}
   }

   private String trueExportName = null;
   private ModelExport trueExport = null;
   private String trueModelName = null;
   private int trueModelIndex = -1;
   private int trueTransactionType = -1;

   private String falseExportName = null;
   private ModelExport falseExport = null;
   private String falseModelName = null;
   private int falseModelIndex = -1;
   private int falseTransactionType = -1;

   private int loopType = 0;

   private int obj1Index = -1;
   private int obj2Index = -1;
   private CheckOperator checkOpt = null;

   public void initialize(ModelAdapter model)
         throws ConfigurationException
   {
      if (this.initialized)
      {
         return;
      }
      super.initialize(model);

      if (this.trueExportName != null)
      {
         this.trueExport = model.getFactory().getModelExport(this.trueExportName);
         if (this.trueExport == null)
         {
            log.warn("The Model Export [" + this.trueExportName + "] not found.");
         }
      }
      if (this.trueModelName != null)
      {
         this.trueModelIndex = model.getFactory().getModelAdapterId(this.trueModelName);
      }

      if (this.falseExportName != null)
      {
         this.falseExport = model.getFactory().getModelExport(this.falseExportName);
         if (this.falseExport == null)
         {
            log.warn("The Model Export [" + this.falseExportName + "] not found.");
         }
      }
      if (this.falseModelName != null)
      {
         this.falseModelIndex = model.getFactory().getModelAdapterId(this.falseModelName);
      }

      if (this.checkOpt == null)
      {
         throw new ConfigurationException("Check operator must be setted.");
      }
   }

   public String getExecuteType()
         throws ConfigurationException
   {
      return "check";
   }

   public ModelExport execute(AppData data, Connection conn)
         throws ConfigurationException, SQLException, IOException
   {
      boolean checkResult;
      ModelExport export;
      ObjectRef preConn = (ObjectRef) data.getSpcialData(ModelAdapter.MODEL_CACHE, ModelAdapter.PRE_CONN);
      int loopCount = 0;
      EternaFactory f = this.getModelAdapter().getFactory();
      do
      {
         Object obj1 = this.obj1Index != -1 ? data.caches[this.obj1Index] : null;
         Object obj2 = this.obj2Index != -1 ? data.caches[this.obj2Index] : null;
         checkResult = this.checkOpt.check(obj1, obj2);
         if (AppData.getAppLogType() != 0)
         {
            Element nowNode = data.getCurrentNode();
            Element rNode = nowNode.addElement("check-result");
            rNode.addAttribute("value", String.valueOf(checkResult));
            if (obj1 != null)
            {
               Element vNode = rNode.addElement("obj1");
               AppDataLogExecute.printObject(vNode, obj1);
            }
            if (obj2 != null)
            {
               Element vNode = rNode.addElement("obj2");
               AppDataLogExecute.printObject(vNode, obj2);
            }
         }
         if (checkResult)
         {
            if (this.trueModelIndex != -1)
            {
               ModelAdapter tmpModel = f.createModelAdapter(this.trueModelIndex);
               int tType = this.trueTransactionType == -1 ? tmpModel.getTransactionType() : this.trueTransactionType;
               export = f.getModelCaller().callModel(data, tmpModel, this.trueExport, tType, preConn);
            }
            else
            {
               export = this.trueExport;
            }
         }
         else
         {
            if (this.falseModelIndex != -1)
            {
               ModelAdapter tmpModel = f.createModelAdapter(this.falseModelIndex);
               int tType = this.falseTransactionType == -1 ? tmpModel.getTransactionType() : this.falseTransactionType;
               export = f.getModelCaller().callModel(data, tmpModel, this.falseExport, tType, preConn);
            }
            else
            {
               export = this.falseExport;
            }
         }

         // �����ж�ѭ���������, ��ֹ��ѭ��
         loopCount++;
         if (loopCount > MAX_LOOP_COUNT)
         {
            log.warn("The execute [" + this.getName() + "] is breaked.");
            break;
         }

         if (export != null)
         {
            return export;
         }

      } while (checkResult ? this.loopType > 0 : this.loopType < 0);
      return export;
   }

   public void setCheckPattern(String pattern)
         throws ConfigurationException
   {
      String[] params = StringTool.separateString(pattern, ";", true);
      if (params.length > 3)
      {
         throw new ConfigurationException("Too many type params:[" + pattern + "].");
      }
      if (params.length > 0)
      {
         try
         {
            this.obj1Index = Integer.parseInt(params[0]);
         }
         catch (NumberFormatException ex)
         {
            throw new ConfigurationException("Error check obj1:" + params[0] + ".");
         }
      }
      if (params.length == 3)
      {
         try
         {
            this.obj2Index = Integer.parseInt(params[2]);
         }
         catch (NumberFormatException ex)
         {
            throw new ConfigurationException("Error check obj2:" + params[2] + ".");
         }
      }
      if (params.length > 1)
      {
         if ("null".equals(params[1]))
         {
            this.checkOpt = NullCheck.instance;
         }
         else if ("array".equals(params[1]))
         {
            this.checkOpt = ArrayCheck.instance;
         }
         else if ("hasNext".equals(params[1]))
         {
            this.checkOpt = HasNextCheck.instance;
         }
         else if ("<".equals(params[1]))
         {
            this.checkOpt = CompareCheck.instance[0];
         }
         else if ("=".equals(params[1]))
         {
            this.checkOpt = CompareCheck.instance[1];
         }
         else if (">".equals(params[1]))
         {
            this.checkOpt = CompareCheck.instance[2];
         }
         else if (params[1].startsWith("class:"))
         {
            try
            {
               this.checkOpt = (CheckOperator) ObjectCreateRule.createObject(
                     params[1].substring(6));
            }
            catch (Exception ex)
            {
               throw new ConfigurationException(ex);
            }
         }
         else
         {
            try
            {
               this.checkOpt = new ClassTypeCheck(Class.forName(params[1]));
            }
            catch (Exception ex)
            {
               throw new ConfigurationException(ex);
            }
         }
      }
   }

   public void setLoopType(int type)
   {
      this.loopType = type;
   }

   public void setTrueExportName(String name)
   {
      this.trueExportName = name;
   }

   public void setFalseExportName(String name)
   {
      this.falseExportName = name;
   }

   public void setTrueTransactionType(String tType)
         throws ConfigurationException
   {
      this.trueTransactionType = ModelAdapterImpl.parseTransactionType(tType);
   }

   public void setFalseTransactionType(String tType)
         throws ConfigurationException
   {
      this.falseTransactionType = ModelAdapterImpl.parseTransactionType(tType);
   }

   public void setTrueModelName(String name)
         throws ConfigurationException
   {
      this.trueModelName = name;
      this.setName(name);
   }

   public void setFalseModelName(String name)
         throws ConfigurationException
   {
      this.falseModelName = name;
      if (this.getName() == null)
      {
         this.setName(name);
      }
   }

   private static class HasNextCheck
         implements CheckOperator
   {
      static HasNextCheck instance = new HasNextCheck();

      public boolean check(Object value1, Object value2)
            throws ConfigurationException
      {
         if (value1 != null && value1 instanceof Iterator)
         {
            return ((Iterator) value1).hasNext();
         }
         return false;
      }
   }

   private static class NullCheck
         implements CheckOperator
   {
      static NullCheck instance = new NullCheck();

      public boolean check(Object value1, Object value2)
            throws ConfigurationException
      {
         return value1 == null;
      }
   }

   private static class ArrayCheck
         implements CheckOperator
   {
      static ArrayCheck instance = new ArrayCheck();

      public boolean check(Object value1, Object value2)
            throws ConfigurationException
      {
         if (value1 == null)
         {
            return false;
         }
         return value1.getClass().isArray();
      }
   }

   private static class ClassTypeCheck
         implements CheckOperator
   {
      private Class checkClass;

      public ClassTypeCheck(Class checkClass)
      {
         this.checkClass = checkClass;
      }

      public boolean check(Object value1, Object value2)
            throws ConfigurationException
      {
         if (value1 == null)
         {
            return false;
         }
         return this.checkClass.isInstance(value1);
      }
   }

   private static class CompareCheck
         implements CheckOperator
   {
      static CompareCheck[] instance = {
         new CompareCheck(-1), new CompareCheck(0), new CompareCheck(1)
      };

      private int compareResult;

      public CompareCheck(int compareResult)
      {
         this.compareResult = compareResult;
      }

      public boolean check(Object value1, Object value2)
            throws ConfigurationException
      {
         if (value1 == null)
         {
            // ����ǵ��ڱȽϣ���value2Ϊ����Ϊtrue
            return value2 == null && this.compareResult == 0;
         }
         else if (value2 == null)
         {
            // ����Ǵ��ڱȽϣ���value2Ϊ����Ϊtrue
            return this.compareResult == 1;
         }
         if (value1 instanceof Comparable && value2 instanceof Comparable)
         {
            int tmpResult = ((Comparable) value1).compareTo(value2);
            tmpResult = tmpResult < 0 ? -1 : tmpResult > 0 ? 1 : 0;
            return tmpResult == this.compareResult;
         }
         else
         {
            throw new ConfigurationException("The two value can not compare, 1:"
                  + value1.getClass() + ", 2:" + value2.getClass() + ".");
         }
      }
   }

}