
package self.micromagic.eterna.sql.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.AbstractGenerator;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.sql.PreparedStatementWrap;
import self.micromagic.eterna.sql.SQLAdapter;
import self.micromagic.eterna.sql.SQLAdapterGenerator;
import self.micromagic.eterna.sql.SQLParameter;
import self.micromagic.eterna.sql.SQLParameterGenerator;
import self.micromagic.eterna.sql.SQLParameterGroup;
import self.micromagic.eterna.sql.preparer.PreparedStatementWrapImpl;
import self.micromagic.eterna.sql.preparer.PreparerManager;
import self.micromagic.eterna.sql.preparer.ValuePreparer;
import self.micromagic.util.container.UnmodifiableIterator;

public abstract class AbstractSQLAdapter extends AbstractGenerator
      implements Cloneable, SQLAdapter, SQLAdapterGenerator
{
   private String preparedSQL = null;
   private SQLManager sqlManager = null;
   private PreparerManager preparerManager = null;
   private SQLParameterGroup paramGroup = new SQLParameterGroupImpl();

   private Map parameterNameMap = new HashMap();
   private SQLParameter[] parameterArray;

   protected boolean initialized = false;

   public EternaFactory getFactory()
   {
      return (EternaFactory) this.factory;
   }

   public void initialize(EternaFactory factory)
         throws ConfigurationException
   {
      if (!this.initialized)
      {
         if (this.preparedSQL == null)
         {
            throw new ConfigurationException( "Can't initialize without preparedSQL.");
         }

         this.initialized = true;

         this.paramGroup.initialize(factory);
         Iterator itr = this.paramGroup.getParameterGeneratorIterator();
         List paramList = new ArrayList();
         int paramIndex = 1;
         while (itr.hasNext())
         {
            SQLParameterGenerator spg = (SQLParameterGenerator) itr.next();
            SQLParameter param = spg.createParameter(paramIndex++);
            param.initialize(this.getFactory());
            paramList.add(param);
         }
         SQLParameter[] paramArray = new SQLParameter[paramList.size()];
         paramList.toArray(paramArray);
         this.parameterArray = paramArray;

         this.sqlManager = new SQLManager();
         String tmpSQL = this.sqlManager.frontParse(this.preparedSQL, paramArray);
         this.sqlManager.parse(tmpSQL);
         this.preparerManager = new PreparerManager(paramArray);

         if (this.sqlManager != null)
         {
            this.sqlManager.initialize(this.getFactory());
         }
         for (int i = 0; i < paramArray.length; i++)
         {
            SQLParameter param = paramArray[i];
            this.addParameterNameMap(param);
         }
         if (this.sqlManager.getParameterCount() > paramArray.length)
         {
            throw new ConfigurationException(
                  "Not all parameter has been bound in [" + this.getName() + "].");
         }
      }
   }

   public SQLAdapter createSQLAdapter()
         throws ConfigurationException
   {
      return (SQLAdapter) this.clone();
   }

   public Object create()
         throws ConfigurationException
   {
      return this.createSQLAdapter();
   }

   protected Object clone()
   {
      try
      {
         AbstractSQLAdapter other = (AbstractSQLAdapter) super.clone();
         if (this.preparedSQL != null)
         {
            other.preparerManager = new PreparerManager(this.parameterArray);
            other.sqlManager = this.sqlManager.copy(true);
         }
         return other;
      }
      catch (CloneNotSupportedException e)
      {
         // this shouldn't happen, since we are Cloneable
         throw new InternalError();
      }
   }

   public int getParameterCount()
         throws ConfigurationException
   {
      if (this.sqlManager == null)
      {
         throw new ConfigurationException("SQL not initialized.");
      }
      return this.sqlManager.getParameterCount();
   }

   public boolean hasActiveParam()
         throws ConfigurationException
   {
      if (this.preparerManager == null)
      {
         throw new ConfigurationException("SQL not initialized.");
      }
      return this.preparerManager.hasActiveParam();
   }

   public int getActiveParamCount()
         throws ConfigurationException
   {
      if (this.preparerManager == null)
      {
         throw new ConfigurationException("SQL not initialized.");
      }
      return this.preparerManager.getParamCount();
   }

   public int getSubSQLCount()
         throws ConfigurationException
   {
      if (this.sqlManager == null)
      {
         throw new ConfigurationException("SQL not initialized.");
      }
      return this.sqlManager.getSubPartCount();
   }

   public String getPreparedSQL()
         throws ConfigurationException
   {
      if (this.sqlManager == null)
      {
         throw new ConfigurationException("SQL not initialized.");
      }
      return this.sqlManager.getPreparedSQL();
   }

   /**
    * ������ʱ���õ��Ӿ��ȡԤ��SQL.
    */
   String getTempPreparedSQL(int[] indexs, String[] subParts)
         throws ConfigurationException
   {
      if (this.sqlManager == null)
      {
         throw new ConfigurationException("SQL not initialized.");
      }
      return this.sqlManager.getTempPreparedSQL(indexs, subParts);
   }

   public void setPreparedSQL(String sql)
         throws ConfigurationException
   {
      if (this.preparedSQL != null)
      {
         throw new ConfigurationException("You can't set prepared sql twice.");
      }
      if (sql == null)
      {
         throw new NullPointerException();
      }
      this.preparedSQL = sql;
   }

   protected void clear()
         throws ConfigurationException
   {
      this.preparedSQL = null;
      this.sqlManager = null;
      this.preparerManager = null;
      this.clearParameters();
   }

   public void setSubSQL(int index, String subPart)
         throws ConfigurationException
   {
      this.setSubSQL(index, subPart, null);
   }

   public void setSubSQL(int index, String subPart, PreparerManager pm)
         throws ConfigurationException
   {
      if (this.sqlManager == null)
      {
         throw new ConfigurationException("SQL not initialized.");
      }
      int tempI = this.sqlManager.setSubPart(index - 1, subPart);
      this.preparerManager.inserPreparerManager(pm, tempI, index);
   }

   public PreparerManager getPreparerManager()
   {
      return this.preparerManager;
   }

   public boolean isDynamicParameter(int index)
         throws ConfigurationException
   {
      if (this.sqlManager == null)
      {
         throw new ConfigurationException("SQL not initialized.");
      }
      return this.sqlManager.isDynamicParameter(index - 1);
   }

   public boolean isDynamicParameter(String name)
         throws ConfigurationException
   {
      return this.isDynamicParameter(this.getIndexByParameterName(name));
   }

   public void setIgnore(int parameterIndex)
         throws ConfigurationException
   {
      if (this.sqlManager == null)
      {
         throw new ConfigurationException("SQL not initialized.");
      }
      this.preparerManager.setIgnore(parameterIndex);
      this.sqlManager.setParamSetted(parameterIndex - 1, false);
   }

   public void setIgnore(String parameterName)
         throws ConfigurationException
   {
      this.setIgnore(this.getIndexByParameterName(parameterName));
   }

   public void setValuePreparer(ValuePreparer preparer)
         throws ConfigurationException
   {
      if (this.sqlManager == null)
      {
         throw new ConfigurationException("SQL not initialized.");
      }
      this.preparerManager.setValuePreparer(preparer);
      preparer.setName(this.parameterArray[preparer.getRelativeIndex() - 1].getName());
      this.sqlManager.setParamSetted(preparer.getRelativeIndex() - 1, true);
   }

   public void prepareValues(PreparedStatement stmt)
         throws ConfigurationException, SQLException
   {
      if (this.sqlManager == null)
      {
         throw new ConfigurationException("SQL not initialized.");
      }
      this.preparerManager.prepareValues(new PreparedStatementWrapImpl(stmt));
   }

   public void prepareValues(PreparedStatementWrap stmtWrap)
         throws ConfigurationException, SQLException
   {
      if (this.sqlManager == null)
      {
         throw new ConfigurationException("SQL not initialized.");
      }
      this.preparerManager.prepareValues(stmtWrap);
   }

   public Iterator getParameterIterator()
         throws ConfigurationException
   {
      return new UnmodifiableIterator(Arrays.asList(this.parameterArray).iterator());
   }

   private void addParameterNameMap(SQLParameter param)
         throws ConfigurationException
   {
      int index = param.getIndex();
      if (index < 1 || index > this.getParameterCount())
      {
         throw new ConfigurationException(
               "Invalid parameter index:" + index + " at SQLAdapter "
               + this.getName() + ".");
      }
      Object obj = this.parameterNameMap.put(param.getName(), param);
      if (obj != null)
      {
         throw new ConfigurationException(
               "Duplicate parameter name:" + param.getName() + " at SQLAdapter "
               + this.getName() + ".");
      }
      ParameterManager pm = this.sqlManager.getParameterManager(index - 1);
      pm.setParam(param);
   }

   public void clearParameters()
         throws ConfigurationException
   {
      if (this.sqlManager != null)
      {
         int count = this.sqlManager.getParameterCount();
         for (int i = 0; i < count; i++)
         {
            ParameterManager pm = this.sqlManager.getParameterManager(i);
            pm.clearParam();
         }
         this.parameterNameMap.clear();
      }
      this.paramGroup = new SQLParameterGroupImpl();
   }

   public void addParameter(SQLParameterGenerator paramGenerator)
         throws ConfigurationException
   {
      this.paramGroup.addParameter(paramGenerator);
   }

   public void addParameterRef(String groupName, String ignoreList)
         throws ConfigurationException
   {
      this.paramGroup.addParameterRef(groupName, ignoreList);
   }

   protected int getIndexByParameterName(String name)
         throws ConfigurationException
   {
      return this.getParameter(name).getIndex();
   }

   public SQLParameter getParameter(String paramName)
         throws ConfigurationException
   {
      SQLParameter p = (SQLParameter) this.parameterNameMap.get(paramName);
      if (p == null)
      {
         throw new ConfigurationException(
               "Invalid parameter name:" + paramName + ".");
      }
      return p;
   }

}

