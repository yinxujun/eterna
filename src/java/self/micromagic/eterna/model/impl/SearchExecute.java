
package self.micromagic.eterna.model.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.io.IOException;
import java.util.Map;

import self.micromagic.eterna.model.SearchExecuteGenerator;
import self.micromagic.eterna.model.Execute;
import self.micromagic.eterna.model.ModelAdapter;
import self.micromagic.eterna.model.ModelExport;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.search.SearchAdapter;
import self.micromagic.eterna.search.SearchManager;
import org.dom4j.Element;


public class SearchExecute extends AbstractExecute
      implements Execute, SearchExecuteGenerator
{
   private String searchNameTag = "searchName";
   private String searchName = null;

   private int searchCacheIdnex = -1;
   private String queryResultName = "queryResult";
   private String searchManagerName = "searchManager";
   private String searchCountName = "searchCount";

   private boolean saveCondition = true;
   private boolean forceSetParam = false;
   private int start = -1;
   private int count = -1;

   private boolean holdConnection = false;
   protected boolean doExecute = true;

   public void initialize(ModelAdapter model)
         throws ConfigurationException
   {
      if (this.initialized)
      {
         return;
      }
      super.initialize(model);
   }

   public String getName()
   {
      return this.searchName == null ? this.searchNameTag : this.searchName;
   }

   public String getExecuteType()
         throws ConfigurationException
   {
      return "search";
   }

   public void setSearchNameTag(String tag)
   {
      this.searchNameTag = tag;
   }

   public void setSearchName(String name)
   {
      this.searchName = name;
   }

   public void setCache(int cacheIndex)
   {
      this.searchCacheIdnex = cacheIndex;
   }

   public void setQueryResultName(String name)
   {
      this.queryResultName = name;
   }

   public void setSearchManagerName(String name)
   {
      this.searchManagerName = name;
   }

   public void setSearchCountName(String name)
   {
      this.searchCountName = name;
   }

   public void setSaveCondition(boolean saveCondition)
   {
      this.saveCondition = saveCondition;
   }

   public void setForceSetParam(boolean forceSetParam)
   {
      this.forceSetParam = forceSetParam;
   }

   public void setStart(int start)
   {
      this.start = start;
   }

   public void setCount(int count)
   {
      this.count = count;
   }

   public void setHoldConnection(boolean hold)
   {
      this.holdConnection = hold;
   }

   public void setDoExecute(boolean execute)
   {
      this.doExecute = execute;
   }

   public ModelExport execute(AppData data, Connection conn)
         throws ConfigurationException, SQLException, IOException
   {
      String searchName = this.searchName != null ?
            this.searchName : data.getRequestParameter(this.searchNameTag);
      if (searchName == null)
      {
         searchName = (String) data.getRequestAttributeMap().get(this.searchNameTag);
      }
      if (searchName != null)
      {
         if (AppData.getAppLogType() != 0)
         {
            Element nowNode = data.getCurrentNode();
            nowNode.addAttribute("searchName", searchName);
            if (this.start != 1)
            {
               nowNode.addAttribute("start", String.valueOf(this.start));
            }
            if (this.count != -1)
            {
               nowNode.addAttribute("count", String.valueOf(this.count));
            }
            if (this.searchCacheIdnex != -1)
            {
               nowNode.addAttribute("searchCacheIdnex", String.valueOf(this.searchCacheIdnex));
            }
            if (this.forceSetParam)
            {
               nowNode.addAttribute("forceSetParam", String.valueOf(this.forceSetParam));
            }
            if (this.holdConnection)
            {
               nowNode.addAttribute("holdConnection", String.valueOf(this.holdConnection));
            }
            if (!this.doExecute)
            {
               nowNode.addAttribute("doExecute", String.valueOf(this.doExecute));
            }
         }
         SearchAdapter search = this.model.getFactory().createSearchAdapter(searchName);
         if (this.searchCacheIdnex != -1)
         {
            data.caches[this.searchCacheIdnex] = search;
         }
         Map raMap = data.getRequestAttributeMap();
         if (this.forceSetParam)
         {
            raMap.put(SearchManager.FORCE_DEAL_CONDITION, "1");
         }
         if (this.holdConnection)
         {
            if (this.doExecute)
            {
               raMap.put(SearchAdapter.READ_ALL_ROW, "1");
               raMap.put(SearchAdapter.HOLD_CONNECTION, "1");
               SearchAdapter.Result sr = search.doSearch(data, conn);
               data.dataMap.put(this.queryResultName, sr);
            }
         }
         else
         {
            if (this.start != -1)
            {
               raMap.put(SearchAdapter.READ_ROW_START_AND_COUNT,
                     new SearchAdapter.StartAndCount(this.start, this.count));
            }
            if (this.saveCondition)
            {
               raMap.put(SearchManager.SAVE_CONDITION, "1");
               SearchManager sm = search.getSearchManager(data);
               data.dataMap.put(this.searchManagerName, sm);
            }
            else
            {
               raMap.remove(SearchManager.SAVE_CONDITION);
            }
            if (this.doExecute)
            {
               SearchAdapter.Result sr = search.doSearch(data, conn);
               data.dataMap.put(this.queryResultName, sr);
               if (sr.searchCount != null)
               {
                  data.dataMap.put(this.searchCountName, sr.searchCount);
               }
            }
            if (this.start != -1)
            {
               raMap.remove(SearchAdapter.READ_ROW_START_AND_COUNT);
            }
         }
         if (this.forceSetParam)
         {
            raMap.remove(SearchManager.FORCE_DEAL_CONDITION);
         }
      }
      data.dataMap.put(SEARCH_MANAGER_ATTRIBUTES, this.model.getFactory().getSearchManagerAttributes());
      return null;
   }

}
