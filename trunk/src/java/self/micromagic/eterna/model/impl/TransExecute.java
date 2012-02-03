
package self.micromagic.eterna.model.impl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Map;
import java.util.Iterator;
import java.util.Collection;
import java.util.Arrays;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.digester.ObjectCreateRule;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.model.Execute;
import self.micromagic.eterna.model.ModelAdapter;
import self.micromagic.eterna.model.ModelExport;
import self.micromagic.eterna.model.TransExecuteGenerator;
import self.micromagic.eterna.model.TransOperator;
import self.micromagic.eterna.model.AppDataLogExecute;
import self.micromagic.eterna.sql.ResultIterator;
import self.micromagic.eterna.sql.ResultRow;
import self.micromagic.eterna.sql.converter.StringConverter;
import self.micromagic.eterna.sql.converter.ValueConverter;
import self.micromagic.eterna.sql.converter.IntegerConverter;
import self.micromagic.eterna.sql.converter.LongConverter;
import self.micromagic.eterna.sql.converter.DoubleConverter;
import self.micromagic.eterna.sql.converter.TimeConverter;
import self.micromagic.eterna.sql.converter.DateConverter;
import self.micromagic.eterna.sql.converter.TimestampConverter;
import self.micromagic.eterna.search.SearchAdapter;
import self.micromagic.eterna.search.SearchManager;
import self.micromagic.util.StringTool;
import self.micromagic.util.StringRef;
import org.dom4j.Element;

public class TransExecute extends AbstractExecute
      implements Execute, TransExecuteGenerator
{
   protected String fromName;
   protected boolean popStack = false;
   protected int peekIndex = -1;
   protected int fromMapIndex = -1;
   protected int fromCacheIndex = -1;
   protected String fromValue = null;
   protected boolean fromMap = false;
   protected boolean removeFrom = false;
   protected boolean mustExist = true;

   protected String toName;
   protected int toMapIndex = -1;
   protected int toCacheIndex = -1;

   protected TransOperator opt;
   protected boolean pushResult = false;

   public void initialize(ModelAdapter model)
         throws ConfigurationException
   {
      if (this.initialized)
      {
         return;
      }
      super.initialize(model);
   }

   public String getExecuteType()
   {
      return "trans";
   }

   public String getName()
         throws ConfigurationException
   {
      return this.toName == null ? this.pushResult ? "#stack" : "#none" : this.toName;
   }

   public boolean isPushResult()
   {
      return this.pushResult;
   }

   public void setPushResult(boolean push)
   {
      this.pushResult = push;
   }

   public void setFrom(String theFrom)
         throws ConfigurationException
   {
      int index = theFrom.indexOf(':');
      String fromParam = null;
      if (index != -1)
      {
         fromParam = theFrom.substring(index + 1);
         theFrom = theFrom.substring(0, index);
      }

      for (int i = 0; i < AppData.MAP_NAMES.length; i++)
      {
         if (AppData.MAP_NAMES[i].equals(theFrom))
         {
            this.fromMapIndex = i;
            this.fromMap = true;
            break;
         }
      }
      if (!this.fromMap)
      {
         for (int i = 0; i < AppData.MAP_SHORT_NAMES.length; i++)
         {
            if (AppData.MAP_SHORT_NAMES[i].equals(theFrom))
            {
               this.fromMapIndex = i;
               this.fromMap = true;
               break;
            }
         }
      }
      if (this.fromMap)
      {
         if (fromParam != null)
         {
            this.fromName = fromParam;
            return;
         }
      }
      else if ("stack".equals(theFrom))
      {
         this.popStack = true;
         if ("pop".equals(fromParam) || fromParam == null)
         {
            return;
         }
         if (fromParam != null && fromParam.startsWith("peek"))
         {
            this.popStack = false;
            this.peekIndex = 0;
            if (fromParam.length() > 4)
            {
               if (fromParam.charAt(4) == '-')
               {
                  try
                  {
                     this.peekIndex = Integer.parseInt(fromParam.substring(5));
                     return;
                  }
                  catch (NumberFormatException ex) {}
               }
            }
            else
            {
               return;
            }
         }
      }
      else if ("cache".equals(theFrom))
      {
         this.fromCacheIndex = 0;
         if (fromParam != null)
         {
            try
            {
               this.fromCacheIndex = Integer.parseInt(fromParam);
               return;
            }
            catch (NumberFormatException ex) {}
         }
         else
         {
            return;
         }
      }
      else if ("value".equals(theFrom))
      {
         if (fromParam != null)
         {
            this.fromValue = fromParam;
            return;
         }
      }

      throw new ConfigurationException("Error from:" + theFrom + ".");
   }

   public void setRemoveFrom(boolean remove)
   {
      this.removeFrom = remove;
   }

   public void setMustExist(boolean mustExist)
   {
      this.mustExist = mustExist;
   }

   public void setOpt(String opt)
         throws ConfigurationException
   {
      if (opt.startsWith("class:"))
      {
         try
         {
            this.opt = (TransOperator) ObjectCreateRule.createObject(opt.substring(6));
         }
         catch (Exception ex)
         {
            throw new ConfigurationException(ex);
         }
      }
      else
      {
         int index = opt.indexOf(':');
         String optName = opt;
         String optParam = null;
         if (index != -1)
         {
            optName = opt.substring(0, index);
            optParam = opt.substring(index + 1);
         }
         if ("getFirstRow".equals(optName))
         {
            this.opt = TransResultRow.instance;
         }
         else if ("getNext".equals(optName))
         {
            this.opt = TransNext.instance;
         }
         else if ("getFirstString".equals(optName))
         {
            this.opt = TransStrings.instance;
         }
         else if ("getFormated".equals(optName))
         {
            this.opt = new TransResultValue(optParam);
         }
         else if ("getObject".equals(optName))
         {
            this.opt = new TransResultValue(optParam, false);
         }
         else if ("getMapValue".equals(optName))
         {
            this.opt = new TransMapValue(optParam);
         }
         else if ("toResultIterator".equals(optName))
         {
            this.opt = TransToResultIterator.instance;
         }
         else if ("toIterator".equals(optName))
         {
            this.opt = TransToIterator.instance;
         }
         else if ("beforeFirst".equals(optName))
         {
            this.opt = TransBeforeFirst.instance;
         }
         else if ("toArray".equals(optName))
         {
            this.opt = optParam != null ? new TransToArray(optParam) : TransToArray.instance;
         }
         else if ("toString".equals(optName))
         {
            this.opt = new TransToWatendType(new StringConverter());
         }
         else if ("toInteger".equals(optName))
         {
            this.opt = new TransToWatendType(new IntegerConverter());
         }
         else if ("toLong".equals(optName))
         {
            this.opt = new TransToWatendType(new LongConverter());
         }
         else if ("toDouble".equals(optName))
         {
            this.opt = new TransToWatendType(new DoubleConverter());
         }
         else if ("toTime".equals(optName))
         {
            this.opt = new TransToWatendType(new TimeConverter());
         }
         else if ("toDate".equals(optName))
         {
            this.opt = new TransToWatendType(new DateConverter());
         }
         else if ("toDatetime".equals(optName))
         {
            this.opt = new TransToWatendType(new TimestampConverter());
         }
         else
         {
            throw new ConfigurationException("Error opt:[" + opt + "].");
         }
      }
   }

   public void setTo(String theTo)
         throws ConfigurationException
   {
      int index = theTo.indexOf(':');
      String toParam = null;
      if (index != -1)
      {
         toParam = theTo.substring(index + 1);
         theTo = theTo.substring(0, index);
      }

      for (int i = 0; i < AppData.MAP_NAMES.length; i++)
      {
         if (AppData.MAP_NAMES[i].equals(theTo))
         {
            this.toMapIndex = i;
            break;
         }
      }
      if (this.toMapIndex == -1)
      {
         for (int i = 0; i < AppData.MAP_SHORT_NAMES.length; i++)
         {
            if (AppData.MAP_SHORT_NAMES[i].equals(theTo))
            {
               this.toMapIndex = i;
               break;
            }
         }
      }
      if (this.toMapIndex != -1)
      {
         if (toParam != null)
         {
            this.toName = toParam;
            return;
         }
      }
      else if ("cache".equals(theTo))
      {
         this.toCacheIndex = 0;
         if (toParam != null)
         {
            try
            {
               this.toCacheIndex = Integer.parseInt(toParam);
               return;
            }
            catch (NumberFormatException ex) {}
         }
         else
         {
            return;
         }
      }

      throw new ConfigurationException("Error to:" + theTo + ".");
   }

   public ModelExport execute(AppData data, Connection conn)
         throws ConfigurationException, SQLException, IOException
   {
      Object value = null;
      if (this.fromValue != null)
      {
         value = this.fromValue;
      }
      else if (this.fromMap)
      {
         Map tmpMap = data.maps[this.fromMapIndex];
         value = tmpMap.get(this.fromName);
         if (this.removeFrom)
         {
            tmpMap.remove(this.fromName);
         }
      }
      else if (this.fromCacheIndex != -1)
      {
         value = data.caches[this.fromCacheIndex];
         if (this.removeFrom)
         {
            data.caches[this.fromCacheIndex] = null;
         }
      }
      else
      {
         if (this.popStack)
         {
            value = data.pop();
         }
         else if (this.peekIndex != -1)
         {
            value = data.peek(this.peekIndex);
         }
      }
      if (value == null)
      {
         if (this.mustExist)
         {
            String msg;
            if (this.fromMap)
            {
               msg = "There is no value in map [" + AppData.MAP_NAMES[this.fromMapIndex] + "] name ["
                     + this.fromName + "].";
            }
            else if (this.fromCacheIndex != -1)
            {
               msg = "There is no value in cache.";
            }
            else
            {
               msg = "There is no value in stack.";
            }
            throw new ConfigurationException(msg);
         }
      }
      Element nowNode = null;
      Element vToNode = null;
      if (AppData.getAppLogType() != 0)
      {
         nowNode = data.getCurrentNode();
      }
      if (nowNode != null)
      {
         Element vNode = nowNode.addElement("value-from");
         if (this.removeFrom)
         {
            vNode.addAttribute("removeFrom", "true");
         }
         if (this.fromValue != null)
         {
            vNode.addAttribute("fromValue", this.fromValue);
         }
         else if (this.fromMap)
         {
            vNode.addAttribute("fromMap", AppData.MAP_NAMES[this.fromMapIndex] + ":" + this.fromName);
         }
         else if (this.fromCacheIndex != -1)
         {
            vNode.addAttribute("fromCache", String.valueOf(this.fromCacheIndex));
         }
         else
         {
            vNode.addAttribute("fromStack", this.peekIndex != -1 ? "pop" : "peek:" + this.peekIndex);
         }
         AppDataLogExecute.printObject(vNode, value);
         vToNode = nowNode.addElement("value-to");
         if (this.isPushResult())
         {
            vToNode.addAttribute("pushResult", "true");
         }
         if (this.toMapIndex != -1)
         {
            vToNode.addAttribute("toMap", AppData.MAP_NAMES[this.toMapIndex] + ":" + this.toName);
         }
         else if (this.toCacheIndex != -1)
         {
            vToNode.addAttribute("toCache", String.valueOf(this.toCacheIndex));
         }
      }
      if (this.opt != null && value != null)
      {
         value = this.opt.change(value);
         if (vToNode != null)
         {
            vToNode.addAttribute("opt", this.opt.toString());
            AppDataLogExecute.printObject(vToNode, value);
         }
      }

      if (this.toMapIndex != -1)
      {
         Map tmpMap = data.maps[this.toMapIndex];
         if (value == null)
         {
            tmpMap.remove(this.toName);
         }
         else
         {
            tmpMap.put(this.toName, value);
         }
      }
      else if (this.toCacheIndex != -1)
      {
         data.caches[this.toCacheIndex] = value;
      }
      if (this.isPushResult())
      {
         data.push(value);
      }
      return null;
   }

   private static class TransResultValue
         implements TransOperator
   {
      private String param;
      private boolean useFormated = true;

      public TransResultValue(String param)
      {
         this.param = param;
      }

      public TransResultValue(String param, boolean useFormated)
      {
         this.param = param;
         this.useFormated = useFormated;
      }

      public Object change(Object value)
            throws ConfigurationException
      {
         if (value == null)
         {
            return null;
         }
         ResultRow row = (ResultRow) TransResultRow.instance.change(value);
         if (row != null)
         {
            try
            {
               return this.useFormated ? row.getFormated(this.param) : row.getObject(this.param);
            }
            catch (SQLException ex)
            {
               throw new ConfigurationException(ex);
            }
         }
         throw new ConfigurationException("Error Object type:" + value.getClass() + ".");
      }

      public String toString()
      {
         return (this.useFormated ? "getFormated:" : "getObject:") + this.param;
      }

   }

   private static class TransMapValue
         implements TransOperator
   {
      private String param;

      public TransMapValue(String param)
      {
         this.param = param;
      }

      public Object change(Object value)
            throws ConfigurationException
      {
         if (value == null)
         {
            return null;
         }
         if (value instanceof Map)
         {
            Map map = (Map) value;
            return map.get(this.param);
         }
         else if (value instanceof SearchManager)
         {
            SearchManager sm = (SearchManager) value;
            SearchManager.Condition condition = sm.getCondition(this.param);
            return condition != null ? condition.value : null;
         }
         throw new ConfigurationException("Error Object type:" + value.getClass() + ".");
      }

      public String toString()
      {
         return "getMapValue:" + this.param;
      }

   }

   private static class TransStrings
         implements TransOperator
   {
      static TransStrings instance = new TransStrings();

      public Object change(Object value)
            throws ConfigurationException
      {
         if (value == null)
         {
            return null;
         }
         if (value instanceof String[])
         {
            String[] strs = (String[]) value;
            if (strs.length > 0)
            {
               return strs[0];
            }
            return null;
         }
         if (value instanceof String)
         {
            return value;
         }
         throw new ConfigurationException("Error Object type:" + value.getClass() + ".");
      }

      public String toString()
      {
         return "getFirstString";
      }

   }

   private static class TransToIterator
         implements TransOperator
   {
      static TransToIterator instance = new TransToIterator();

      public Object change(Object value)
            throws ConfigurationException
      {
         if (value == null)
         {
            return null;
         }
         if (value instanceof Iterator)
         {
            return value;
         }
         else if (value instanceof Collection)
         {
            return ((Collection) value).iterator();
         }
         else if (value instanceof Object[])
         {
            return Arrays.asList((Object[]) value).iterator();
         }
         else if (value instanceof Map)
         {
            return ((Map) value).entrySet().iterator();
         }
         throw new ConfigurationException("Error Object type:" + value.getClass() + ".");
      }

      public String toString()
      {
         return "getFirstString";
      }

   }

   private static class TransNext
         implements TransOperator
   {
      static TransNext instance = new TransNext();

      public Object change(Object value)
            throws ConfigurationException
      {
         if (value == null)
         {
            return null;
         }
         if (value instanceof Iterator)
         {
            Iterator itr = (Iterator) value;
            if (itr.hasNext())
            {
               return itr.next();
            }
            return null;
         }
         throw new ConfigurationException("Error Object type:" + value.getClass() + ".");
      }

      public String toString()
      {
         return "getNext";
      }

   }

   private static class TransToResultIterator
         implements TransOperator
   {
      static TransToResultIterator instance = new TransToResultIterator();

      public Object change(Object value)
            throws ConfigurationException
      {
         if (value == null)
         {
            return null;
         }
         if (value instanceof ResultIterator)
         {
            return value;
         }
         if (value instanceof SearchAdapter.Result)
         {
            return ((SearchAdapter.Result) value).queryResult;
         }
         throw new ConfigurationException("Error Object type:" + value.getClass() + ".");
      }

      public String toString()
      {
         return "toResultIterator";
      }

   }

   private static class TransBeforeFirst
         implements TransOperator
   {
      static TransBeforeFirst instance = new TransBeforeFirst();

      public Object change(Object value)
            throws ConfigurationException
      {
         if (value == null)
         {
            return null;
         }
         if (value instanceof ResultIterator)
         {
            ResultIterator ritr = (ResultIterator) value;
            try
            {
               ritr.beforeFirst();
            }
            catch (SQLException ex)
            {
               throw new ConfigurationException(ex);
            }
            return ritr;
         }
         if (value instanceof SearchAdapter.Result)
         {
            ResultIterator ritr = ((SearchAdapter.Result) value).queryResult;
            try
            {
               ritr.beforeFirst();
            }
            catch (SQLException ex)
            {
               throw new ConfigurationException(ex);
            }
            return value;
         }
         throw new ConfigurationException("Error Object type:" + value.getClass() + ".");
      }

      public String toString()
      {
         return "beforeFirst";
      }

   }

   private static class TransResultRow
         implements TransOperator
   {
      static TransResultRow instance = new TransResultRow();

      public Object change(Object value)
            throws ConfigurationException
      {
         if (value == null)
         {
            return null;
         }
         if (value instanceof ResultIterator)
         {
            ResultIterator ritr = (ResultIterator) value;
            if (ritr.hasNext())
            {
               try
               {
                  return ritr.nextRow();
               }
               catch (SQLException ex)
               {
                  throw new ConfigurationException(ex);
               }
            }
            return null;
         }
         if (value instanceof ResultRow)
         {
            return value;
         }
         if (value instanceof SearchAdapter.Result)
         {
            ResultIterator ritr = ((SearchAdapter.Result) value).queryResult;
            if (ritr.hasNext())
            {
               try
               {
                  return ritr.nextRow();
               }
               catch (SQLException ex)
               {
                  throw new ConfigurationException(ex);
               }
            }
            return null;
         }
         throw new ConfigurationException("Error Object type:" + value.getClass() + ".");
      }

      public String toString()
      {
         return "getFirstRow";
      }

   }

   private static class TransToArray
         implements TransOperator
   {
      static TransToArray instance = new TransToArray();

      private String param;

      public TransToArray(String param)
      {
         this.param = param;
      }

      public TransToArray()
      {
         this.param = ",";
      }

      public Object change(Object value)
            throws ConfigurationException
      {
         if (value == null)
         {
            return null;
         }
         if (value instanceof ResultIterator)
         {
            LinkedList list = new LinkedList();
            ResultIterator ritr = (ResultIterator) value;
            try
            {
               while (ritr.hasNext())
               {
                  list.add(ritr.nextRow().getFormated(this.param));
               }
            }
            catch (SQLException ex)
            {
               throw new ConfigurationException(ex);
            }
            String[] temp = new String[list.size()];
            list.toArray(temp);
            return temp;
         }
         else if (value instanceof String)
         {
            return StringTool.separateString((String) value, this.param, true);
         }
         throw new ConfigurationException("Error Object type:" + value.getClass() + ".");
      }

      public String toString()
      {
         return "toArray:" + this.param;
      }

   }

   private static class TransToWatendType
         implements TransOperator
   {
      private ValueConverter converter;

      public TransToWatendType(ValueConverter converter)
      {
         this.converter = converter;
      }

      public Object change(Object value)
            throws ConfigurationException
      {
         return this.converter.convert(value);
      }

      public String toString()
      {
         StringRef type = new StringRef();
         this.converter.getConvertType(type);
         String typeName = "Unkown";
         if (type.getString() != null && type.getString().length() > 0)
         {
            typeName = type.getString();
         }
         return "convert:" + typeName;
      }

   }

}
