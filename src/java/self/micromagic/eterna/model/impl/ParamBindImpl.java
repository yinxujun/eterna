
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
import self.micromagic.util.StringTool;

public class ParamBindImpl extends AbstractGenerator
      implements ParamBind, ParamBindGenerator
{
   protected ParamSetManager.Name[] names = null;
   protected String paramSrc;

   protected boolean mapBind = false;
   protected int mapIndex = -1;
   protected boolean popStack = false;
   protected int peekIndex = -1;
   protected int cacheIndex = -1;

   protected boolean loop = false;
   protected boolean subSQL = false;

   public void initialize(ModelAdapter model, Execute execute) {}

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
      this.paramSrc = theSrc;

      for (int i = 0; i < AppData.MAP_NAMES.length; i++)
      {
         if (AppData.MAP_NAMES[i].equals(theSrc))
         {
            this.mapIndex = i;
            this.mapBind = true;
            return;
         }
      }
      for (int i = 0; i < AppData.MAP_SHORT_NAMES.length; i++)
      {
         if (AppData.MAP_SHORT_NAMES[i].equals(theSrc))
         {
            this.mapIndex = i;
            this.mapBind = true;
            return;
         }
      }
      if (theSrc != null)
      {
         if (theSrc.startsWith("stack"))
         {
            this.mapIndex = -1;
            this.mapBind = false;
            this.popStack = true;
            if (theSrc.length() > 5)
            {
               if (theSrc.charAt(5) == ':')
               {
                  String tmp = theSrc.substring(6);
                  if ("pop".equals(tmp))
                  {
                     return;
                  }
                  if (tmp.startsWith("peek"))
                  {
                     this.popStack = false;
                     this.peekIndex = 0;
                     if (tmp.length() > 4)
                     {
                        if (tmp.charAt(4) == '-')
                        {
                           try
                           {
                              this.peekIndex = Integer.parseInt(tmp.substring(5));
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
            }
            else
            {
               return;
            }
         }
         else if (theSrc.startsWith("cache"))
         {
            this.mapIndex = -1;
            this.mapBind = false;
            this.cacheIndex = 0;
            if (theSrc.length() > 5)
            {
               if (theSrc.charAt(5) == ':')
               {
                  String tmp = theSrc.substring(6);
                  try
                  {
                     this.cacheIndex = Integer.parseInt(tmp);
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
      throw new ConfigurationException("Error bind src:" + theSrc + ".");
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
            this.names[i] = new ParamSetManager.Name(tmp, tmp);
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
         if (this.mapBind)
         {
            tempValue = data.maps[this.mapIndex];
         }
         else if (this.cacheIndex != -1)
         {
            tempValue = data.caches[this.cacheIndex];
         }
         else
         {
            if (this.popStack)
            {
               tempValue = data.pop();
            }
            else
            {
               tempValue = data.peek(this.peekIndex);
            }
         }
      }

      if (this.subSQL)
      {
         if (tempValue instanceof String)
         {
            for (int i = 0; i < this.names.length; i++)
            {
               psm.setSubSQL(Integer.parseInt(this.names[i].sqlName), (String) tempValue);
            }
         }
         else if (tempValue instanceof SearchManager)
         {
            SearchManager sm = (SearchManager) tempValue;
            for (int i = 0; i < this.names.length; i++)
            {
               psm.setSubSQL(Integer.parseInt(this.names[i].sqlName),
                     sm.getConditionPart(), sm.getPreparerManager());
            }
         }
         else if (tempValue instanceof SearchAdapter)
         {
            SearchAdapter sa = (SearchAdapter) tempValue;
            SearchManager sm = sa.getSearchManager(data);
            for (int i = 0; i < this.names.length; i++)
            {
               psm.setSubSQL(Integer.parseInt(this.names[i].sqlName),
                     sm.getSpecialConditionPart(sa), sm.getSpecialPreparerManager(sa));
            }
         }
      }
      else
      {
         if (loopIndex == 0 && tempValue == null)
         {
            log.warn("Not found the src:" + this.paramSrc + ".");
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
         throws ConfigurationException
   {
      return this.createParamBind();
   }

   public ParamBind createParamBind()
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
            this.names = new ParamSetManager.Name[]{new ParamSetManager.Name("1", "1")};
         }
         else
         {
            for (int i = 0; i < this.names.length; i++)
            {
               try
               {
                  Integer.parseInt(this.names[i].sqlName);
               }
               catch (Exception ex)
               {
                  throw new ConfigurationException("When set sub SQL, names must be number. but the "
                        + (i + 1) + " name is [" + this.names[i].sqlName +  "].");
               }
            }
         }
      }
      return this;
   }

}

