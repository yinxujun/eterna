
package self.micromagic.eterna.model;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.search.SearchManager;
import self.micromagic.eterna.share.TypeManager;
import self.micromagic.eterna.sql.ResultIterator;
import self.micromagic.eterna.sql.ResultRow;
import self.micromagic.eterna.sql.SQLAdapter;
import self.micromagic.eterna.sql.SQLParameter;
import self.micromagic.eterna.sql.preparer.PreparerManager;

public class ParamSetManager
{
   private SQLAdapter sql;
   private SQLParameter[] params;
   private boolean[] paramsSetted;
   private Object[] paramsValues;
   private Map paramsCacheValues;

   public ParamSetManager(SQLAdapter sql)
         throws ConfigurationException
   {
      this.sql = sql;
      Iterator itr = sql.getParameterIterator();
      int count = sql.getParameterCount();
      this.params = new SQLParameter[count];
      this.paramsSetted = new boolean[count];
      this.paramsValues = new Object[count];
      for (int i = 0; i < count; i++)
      {
         SQLParameter param = (SQLParameter) itr.next();
         // 因为parameter的index从1开始，所以要减1
         this.params[param.getIndex() - 1] = param;
         this.paramsSetted[param.getIndex() - 1] = false;
         this.paramsValues[param.getIndex() - 1] = null;
      }
   }

   private Object getValues(Name[] names)
   {
      if (this.paramsCacheValues == null)
      {
         return null;
      }
      return this.paramsCacheValues.get(names);
   }

   private void setValues(Name[] names, Object values)
   {
      if (this.paramsCacheValues == null)
      {
         this.paramsCacheValues = new HashMap();
      }
      this.paramsCacheValues.put(names, values);
   }

   public static void preparerValue(SQLAdapter sql, SQLParameter param, String value)
         throws ConfigurationException
   {
      try
      {
         sql.setValuePreparer(param.createValuePreparer(value));
      }
      catch (Exception ex)
      {
         if (ex instanceof ConfigurationException)
         {
            throw (ConfigurationException) ex;
         }
         doPreparerError(sql, param, value, ex);
      }
   }

   public static void preparerValue(SQLAdapter sql, SQLParameter param, Object value)
         throws ConfigurationException
   {
      try
      {
         sql.setValuePreparer(param.createValuePreparer(value));
      }
      catch (Exception ex)
      {
         if (ex instanceof ConfigurationException)
         {
            throw (ConfigurationException) ex;
         }
         doPreparerError(sql, param, value, ex);
      }
   }

   private static void doPreparerError(SQLAdapter sql, SQLParameter param, Object value,
         Exception ex)
         throws ConfigurationException
   {
      if (!"".equals(value))
      {
         // 如果因为是空字符串而产生的类型转换错误，则不记录警告日志.
         StringBuffer str = new StringBuffer(64);
         str.append("SQL:[").append(sql.getName()).append("] ");
         str.append("param:[").append(param.getName()).append("] ");
         str.append("value:[").append(value).append("] preparer error.");
         AppData.log.warn(str, ex);
      }
      if (sql.isDynamicParameter(param.getIndex()))
      {
         sql.setIgnore(param.getIndex());
      }
      else
      {
         sql.setValuePreparer(sql.getFactory().getDefaultValuePreparerCreaterGenerator()
               .createNullPreparer(param.getIndex(), TypeManager.getSQLType(param.getType())));
      }
   }


   public void setSubSQL(int index, String subSQL, PreparerManager pm)
         throws ConfigurationException
   {
      this.sql.setSubSQL(index, subSQL, pm);
   }

   public void setSubSQL(int index, String subSQL)
         throws ConfigurationException
   {
      this.sql.setSubSQL(index, subSQL);
   }

   public void setParam(int index, Object value)
         throws ConfigurationException
   {
      // 因为parameter的index从1开始，所以要减1
      SQLParameter param = this.params[index - 1];
      preparerValue(this.sql, param, value);
      this.paramsSetted[index - 1] = true;
      this.paramsValues[index - 1] = value;
   }

   public void setIgnore(int index)
         throws ConfigurationException
   {
      // 因为parameter的index从1开始，所以要减1
      SQLParameter param = this.params[index - 1];
      this.sql.setIgnore(param.getIndex());
      this.paramsSetted[index - 1] = true;
      this.paramsValues[index - 1] = null;
   }

   public Object getParamValue(int index)
         throws ConfigurationException
   {
      return this.paramsValues[index - 1];
   }

   public boolean isParamSetted(int index)
         throws ConfigurationException
   {
      return this.paramsSetted[index - 1];
   }

   public void setParam(String name, Object value)
         throws ConfigurationException
   {
      SQLParameter param = this.sql.getParameter(name);
      preparerValue(this.sql, param, value);
      // 因为parameter的index从1开始，所以要减1
      this.paramsSetted[param.getIndex() - 1] = true;
      this.paramsValues[param.getIndex() - 1] = value;
   }

   public void setIgnore(String name)
         throws ConfigurationException
   {
      SQLParameter param = this.sql.getParameter(name);
      if (this.sql.isDynamicParameter(param.getIndex()))
      {
         this.sql.setIgnore(param.getIndex());
      }
      else
      {
         this.sql.setValuePreparer(this.sql.getFactory().getDefaultValuePreparerCreaterGenerator()
               .createNullPreparer(param.getIndex(), TypeManager.getSQLType(param.getType())));
      }
      // 因为parameter的index从1开始，所以要减1
      this.paramsSetted[param.getIndex() - 1] = true;
      this.paramsValues[param.getIndex() - 1] = null;
   }

   public Object getParamValue(String name)
         throws ConfigurationException
   {
      SQLParameter param = this.sql.getParameter(name);
      return this.paramsValues[param.getIndex() - 1];
   }

   /**
    * 将所有的动态参数设为忽略非动态参数设为null. <p>
    *
    * @param settedFlag   设置完后是否要置上已设置标志
    */
   public void setIgnores(boolean settedFlag)
         throws ConfigurationException
   {
      for (int i = 0; i < this.params.length; i++)
      {
         if (!this.paramsSetted[i])
         {
            SQLParameter param = this.params[i];
            if (this.sql.isDynamicParameter(param.getIndex()))
            {
               this.sql.setIgnore(param.getIndex());
            }
            else
            {
               this.sql.setValuePreparer(this.sql.getFactory().getDefaultValuePreparerCreaterGenerator()
                     .createNullPreparer(param.getIndex(), TypeManager.getSQLType(param.getType())));
            }
            this.paramsSetted[i] = settedFlag;
            if (settedFlag)
            {
               this.paramsValues[i] = null;
            }
         }
      }
   }

   public void setParams(Map values)
         throws ConfigurationException
   {
      for (int i = 0; i < this.params.length; i++)
      {
         if (!this.paramsSetted[i])
         {
            SQLParameter param = this.params[i];
            Object value = values.get(param.getName());
            if (value != null)
            {
               if (value instanceof String[])
               {
                  String[] strs = (String[]) value;
                  if (strs.length == 0)
                  {
                     continue;
                  }
                  preparerValue(this.sql, param, strs[0]);
               }
               else
               {
                  preparerValue(this.sql, param, value);
               }
               this.paramsSetted[i] = true;
               this.paramsValues[i] = value;
            }
         }
      }
   }

   public int setParams(Map values, Name[] names, int index)
         throws ConfigurationException
   {
      Object[][] arrays;
      if (index == 0)
      {
         arrays = new Object[names.length][];
         this.setValues(names, arrays);
      }
      else
      {
         arrays = (Object[][]) this.getValues(names);
      }

      int loopCount = -1;
      for (int i = 0; i < names.length; i++)
      {
         SQLParameter param = this.sql.getParameter(names[i].sqlName);
         Object[] array = null;
         if (index == 0)
         {
            Object value = values.get(names[i].srcName);
            if (value != null)
            {
               if (value instanceof Object[])
               {
                  array = (Object[]) value;
                  arrays[i] = array;

                  if (loopCount == -1)
                  {
                     loopCount = array.length;
                  }
                  if (loopCount != array.length)
                  {
                     throw new ConfigurationException("The param count not same, "
                           + loopCount + " and " + array.length + ".");
                  }
               }
            }
         }
         else
         {
            array = arrays[i];
         }
         if (array != null)
         {
            loopCount = array.length;
            preparerValue(this.sql, param, array[index]);
            this.paramsSetted[param.getIndex() - 1] = true;
            this.paramsValues[param.getIndex() - 1] = array[index];
         }
         else
         {
            this.dealNull(param);
         }
      }
      return loopCount;
   }

   public void setParams(Map values, Name[] names)
         throws ConfigurationException
   {
      for (int i = 0; i < names.length; i++)
      {
         SQLParameter param = this.sql.getParameter(names[i].sqlName);
         Object value = values.get(names[i].srcName);
         if (value != null)
         {
            if (value instanceof String[])
            {
               String[] strs = (String[]) value;
               if (strs.length == 0)
               {
                  continue;
               }
               preparerValue(this.sql, param, strs[0]);
            }
            else
            {
               preparerValue(this.sql, param, value);
            }
            this.paramsSetted[param.getIndex() - 1] = true;
            this.paramsValues[param.getIndex() - 1] = value;
         }
         else
         {
            this.dealNull(param);
         }
      }
   }

   public void setParams(ResultRow values)
         throws ConfigurationException, SQLException
   {
      for (int i = 0; i < this.params.length; i++)
      {
         if (!this.paramsSetted[i])
         {
            SQLParameter param = this.params[i];
            int colIndex = -1;
            try
            {
               colIndex = values.findColumn(param.getName());
            }
            catch (SQLException ex) {}
            catch (ConfigurationException ex) {}
            if (colIndex != -1)
            {
               Object value = values.getObject(colIndex);
               preparerValue(this.sql, param, value);
               this.paramsSetted[i] = true;
               this.paramsValues[i] = value;
            }
         }
      }
   }

   public int setParams(ResultIterator values, Name[] names, int index)
         throws ConfigurationException, SQLException
   {
      ResultIterator ritr;
      if (index == 0)
      {
         ritr = values;
         this.setValues(names, ritr);
      }
      else
      {
         ritr = (ResultIterator) this.getValues(names);
      }

      int loopCount = ritr.getRecordCount();
      if (loopCount > 0)
      {
         ResultRow row = ritr.nextRow();
         for (int i = 0; i < names.length; i++)
         {
            SQLParameter param = this.sql.getParameter(names[i].sqlName);
            int colIndex = -1;
            try
            {
               colIndex = row.findColumn(names[i].srcName);
            }
            catch (SQLException ex) {}
            catch (ConfigurationException ex) {}
            if (colIndex != -1)
            {
               Object value = row.getObject(colIndex);
               preparerValue(this.sql, param, value);
               this.paramsSetted[param.getIndex() - 1] = true;
               this.paramsValues[param.getIndex() - 1] = value;
            }
            else
            {
               this.dealNull(param);
            }
         }
      }
      else
      {
         loopCount = -1;
      }
      return loopCount;
   }

   public void setParams(ResultRow values, Name[] names)
         throws ConfigurationException, SQLException
   {
      for (int i = 0; i < names.length; i++)
      {
         SQLParameter param = this.sql.getParameter(names[i].sqlName);
         int colIndex = -1;
         try
         {
            colIndex = values.findColumn(names[i].srcName);
         }
         catch (SQLException ex) {}
         catch (ConfigurationException ex) {}
         if (colIndex != -1)
         {
            Object value = values.getObject(colIndex);
            preparerValue(this.sql, param, value);
            this.paramsSetted[param.getIndex() - 1] = true;
            this.paramsValues[param.getIndex() - 1] = value;
         }
         else
         {
            this.dealNull(param);
         }
      }
   }

   public void setParams(SearchManager searchManager)
         throws ConfigurationException, SQLException
   {
      for (int i = 0; i < this.params.length; i++)
      {
         if (!this.paramsSetted[i])
         {
            SQLParameter param = this.params[i];
            SearchManager.Condition con = searchManager.getCondition(param.getName());
            if (con != null)
            {
               preparerValue(this.sql, param, con.value);
               this.paramsSetted[i] = true;
               this.paramsValues[i] = con.value;
            }
         }
      }
   }

   public void setParams(SearchManager searchManager, Name[] names)
         throws ConfigurationException, SQLException
   {
      for (int i = 0; i < names.length; i++)
      {
         SQLParameter param = this.sql.getParameter(names[i].sqlName);
         SearchManager.Condition con = searchManager.getCondition(names[i].srcName);
         if (con != null)
         {
            preparerValue(this.sql, param, con.value);
            this.paramsSetted[i] = true;
            this.paramsValues[i] = con.value;
         }
         else
         {
            this.dealNull(param);
         }
      }
   }

   private void dealNull(SQLParameter param)
         throws ConfigurationException
   {
      if (this.sql.isDynamicParameter(param.getIndex()))
      {
         this.sql.setIgnore(param.getIndex());
      }
      else
      {
         this.sql.setValuePreparer(this.sql.getFactory().getDefaultValuePreparerCreaterGenerator()
               .createNullPreparer(param.getIndex(), TypeManager.getSQLType(param.getType())));
      }
      this.paramsSetted[param.getIndex() - 1] = true;
      this.paramsValues[param.getIndex() - 1] = null;
   }

   public int setParams(Name[] names, int index)
         throws ConfigurationException, SQLException
   {
      if (index < 1)
      {
         throw new ConfigurationException("In this method:setParams(String[], int), the second parameter mustn't below than 1.");
      }
      Object obj = this.getValues(names);
      if (obj != null)
      {
         if (obj instanceof Object[][])
         {
            return this.setParams((Map) null, names, index);
         }
         if (obj instanceof ResultIterator)
         {
            return this.setParams((ResultIterator) null, names, index);
         }
      }
      throw new ConfigurationException("Not found cached values.");
   }

   public static class Name
   {
      public final String srcName;
      public final String sqlName;

      public Name(String srcName, String sqlName)
      {
         this.srcName = srcName;
         this.sqlName = sqlName;
      }

   }

}
