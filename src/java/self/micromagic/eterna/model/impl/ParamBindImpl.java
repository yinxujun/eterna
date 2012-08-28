
package self.micromagic.eterna.model.impl;

import java.sql.SQLException;
import java.util.Map;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.model.Execute;
import self.micromagic.eterna.model.ModelAdapter;
import self.micromagic.eterna.model.ParamBind;
import self.micromagic.eterna.model.ParamBindGenerator;
import self.micromagic.eterna.model.ParamSetManager;
import self.micromagic.eterna.search.SearchAdapter;
import self.micromagic.eterna.search.SearchManager;
import self.micromagic.eterna.share.AbstractGenerator;
import self.micromagic.eterna.sql.ResultIterator;
import self.micromagic.eterna.sql.ResultRow;
import self.micromagic.eterna.sql.SQLAdapter;
import self.micromagic.eterna.sql.SQLParameter;
import self.micromagic.util.StringTool;

public class ParamBindImpl extends AbstractGenerator
      implements ParamBind, ParamBindGenerator
{
   protected ParamSetManager.Name[] names = null;
   protected DataHandler srcHandler = new DataHandler("src", false, true);

   protected boolean loop = false;
   protected boolean subSQL = false;

   public void initialize(ModelAdapter model, Execute execute)
         throws ConfigurationException
   {
      if (this.loop && this.names == null)
      {
         log.info("Because not give the attribute names, so set loop = false.");
         this.loop = false;
      }
      if (this.subSQL)
      {
         if (this.names == null || this.names.length == 0)
         {
            log.warn("Because not give the sub index at attribute names, so set names = 1.");
            this.names = new ParamSetManager.Name[]{new ParamSetManager.Name(null, 1)};
         }
         else
         {
            for (int i = 0; i < this.names.length; i++)
            {
               try
               {
						this.names[i] = new ParamSetManager.Name(
								this.names[i], Integer.parseInt(this.names[i].sqlName));
               }
               catch (Exception ex)
               {
                  throw new ConfigurationException("When set sub SQL, names must be number. but the "
                        + (i + 1) + " name is [" + this.names[i].sqlName +  "].");
               }
            }
         }
      }
		else
		{
			if (this.names != null && execute instanceof SQLExecute)
			{
				try
				{
					SQLAdapter sql = ((SQLExecute) execute).getSQL();
					for (int i = 0; i < this.names.length; i++)
					{
						SQLParameter param = sql.getParameter(this.names[i].sqlName);
						this.names[i] = new ParamSetManager.Name(this.names[i], param.getIndex());
					}
				}
				catch (ConfigurationException ex)
				{
					log.error("Error in parse name index.", ex);
				}
			}
		}
   }

   public boolean isLoop()
   {
      return this.loop;
   }

   public void setLoop(boolean loop)
   {
      this.loop = loop;
   }

   public boolean isSubSQL()
   {
      return this.subSQL;
   }

   public void setSubSQL(boolean subSQL)
   {
      this.subSQL = subSQL;
   }

   public void setSrc(String theSrc)
         throws ConfigurationException
   {
      this.srcHandler.setConfig(theSrc);
   }

   public void setNames(String names)
   {
      if (names == null || names.length() == 0)
      {
         this.names = null;
      }
      String[] tmps = StringTool.separateString(names, ",", true);
      this.names = new ParamSetManager.Name[tmps.length];
      for (int i = 0; i < tmps.length; i++)
      {
         String tmp = tmps[i];
         int index = tmp.indexOf(':');
         if (index == -1)
         {
            if (tmp.endsWith("[]"))
            {
               // 如果名称是以[]结尾, 则说明是以数组方式获取值
               // sqlName只取"[]"前面的部分
               this.names[i] = new ParamSetManager.Name(tmp, tmp.substring(0, tmp.length() - 2));
            }
            else
            {
               this.names[i] = new ParamSetManager.Name(tmp, tmp);
            }
         }
         else
         {
            this.names[i] = new ParamSetManager.Name(tmp.substring(0, index),
                  tmp.substring(index + 1));
         }
      }
   }

   public int setParam(AppData data, ParamSetManager psm, int loopIndex)
         throws ConfigurationException, SQLException
   {
      int loopCount = -1;
      Object tempValue = null;
      if (loopIndex == 0)
      {
         tempValue = this.srcHandler.getData(data, false);
      }

      if (this.subSQL)
      {
         if (tempValue instanceof String)
         {
            for (int i = 0; i < this.names.length; i++)
            {
               psm.setSubSQL(this.names[i].sqlIndex, (String) tempValue);
            }
         }
         else if (tempValue instanceof SearchManager)
         {
            SearchManager sm = (SearchManager) tempValue;
            for (int i = 0; i < this.names.length; i++)
            {
               psm.setSubSQL(this.names[i].sqlIndex, sm.getConditionPart(), sm.getPreparerManager());
            }
         }
         else if (tempValue instanceof SearchAdapter)
         {
            SearchAdapter sa = (SearchAdapter) tempValue;
            SearchManager sm = sa.getSearchManager(data);
            for (int i = 0; i < this.names.length; i++)
            {
               psm.setSubSQL(this.names[i].sqlIndex, sm.getSpecialConditionPart(sa),
							sm.getSpecialPreparerManager(sa));
            }
         }
      }
      else
      {
         if (loopIndex == 0 && tempValue == null)
         {
            log.warn("Not found the src:" + this.srcHandler.getConfig() + ".");
         }
         if (this.names != null)
         {
            if (this.loop)
            {
               if (tempValue == null)
               {
                  loopCount = psm.setParams(this.names, loopIndex);
               }
               else if (tempValue instanceof Map)
               {
                  loopCount = psm.setParams((Map) tempValue, this.names, loopIndex);
               }
               else if (tempValue instanceof ResultIterator)
               {
                  ResultIterator ritr = (ResultIterator) tempValue;
                  loopCount = psm.setParams(ritr, this.names, loopIndex);
               }
               else if (tempValue instanceof SearchAdapter.Result)
               {
                  ResultIterator ritr = ((SearchAdapter.Result) tempValue).queryResult;
                  loopCount = psm.setParams(ritr, this.names, loopIndex);
               }
               else
               {
                  throw new ConfigurationException("Error src type:" + tempValue.getClass() + ".");
               }
            }
            else
            {
               if (tempValue == null)
               {
                  for (int i = 0; i < this.names.length; i++)
                  {
                     psm.setParam(this.names[i].sqlName, tempValue);
                  }
               }
               else if (tempValue instanceof Map)
               {
                  psm.setParams((Map) tempValue, this.names);
               }
               else if (tempValue instanceof ResultRow)
               {
                  psm.setParams((ResultRow) tempValue, this.names);
               }
               else if (tempValue instanceof SearchManager)
               {
                  psm.setParams((SearchManager) tempValue, this.names);
               }
               else if (tempValue instanceof ResultIterator)
               {
                  ResultIterator ritr = (ResultIterator) tempValue;
                  if (ritr.hasMoreRow())
                  {
                     psm.setParams(ritr.nextRow(), this.names);
                  }
               }
               else if (tempValue instanceof SearchAdapter.Result)
               {
                  ResultIterator ritr = ((SearchAdapter.Result) tempValue).queryResult;
                  if (ritr.hasMoreRow())
                  {
                     psm.setParams(ritr.nextRow(), this.names);
                  }
               }
               else if (tempValue instanceof Object[])
               {
                  Object[] objs = (Object[]) tempValue;
                  if (objs.length == this.names.length)
                  {
                     for (int i = 0; i < objs.length; i++)
                     {
                        psm.setParam(this.names[i].sqlName, objs[i]);
                     }
                  }
                  else if (objs.length == 1)
                  {
                     for (int i = 0; i < this.names.length; i++)
                     {
                        psm.setParam(this.names[i].sqlName, objs[0]);
                     }
                  }
                  else
                  {
                     throw new ConfigurationException("Error src length:" + objs.length + ", reqared:" + this.names.length + ".");
                  }
               }
               else
               {
                  for (int i = 0; i < this.names.length; i++)
                  {
                     psm.setParam(this.names[i].sqlName, tempValue);
                  }
               }
            }
         }
         else
         {
            if (tempValue == null)
            {
               throw new ConfigurationException("Error src type:[null].");
            }
            else if (tempValue instanceof Map)
            {
               psm.setParams((Map) tempValue);
            }
            else if (tempValue instanceof ResultRow)
            {
               psm.setParams((ResultRow) tempValue);
            }
            else if (tempValue instanceof SearchManager)
            {
               psm.setParams((SearchManager) tempValue);
            }
            else if (tempValue instanceof ResultIterator)
            {
               ResultIterator ritr = (ResultIterator) tempValue;
               if (ritr.hasMoreRow())
               {
                  psm.setParams(ritr.nextRow());
               }
            }
            else if (tempValue instanceof SearchAdapter.Result)
            {
               ResultIterator ritr = ((SearchAdapter.Result) tempValue).queryResult;
               if (ritr.hasMoreRow())
               {
                  psm.setParams(ritr.nextRow());
               }
            }
            else
            {
               throw new ConfigurationException("Error src type:" + tempValue.getClass() + ".");
            }
         }
      }
      return loopCount;
   }

   public Object create()
   {
      return this.createParamBind();
   }

   public ParamBind createParamBind()
   {
      return this;
   }

}

