
package self.micromagic.eterna.model.impl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.model.Execute;
import self.micromagic.eterna.model.ModelAdapter;
import self.micromagic.eterna.model.ModelExport;
import self.micromagic.eterna.model.ParamSetManager;
import self.micromagic.eterna.model.QueryExecuteGenerator;
import self.micromagic.eterna.security.EmptyPermission;
import self.micromagic.eterna.security.User;
import self.micromagic.eterna.security.UserManager;
import self.micromagic.eterna.sql.QueryAdapter;
import self.micromagic.eterna.sql.ResultIterator;
import org.dom4j.Element;

public class QueryExecute extends SQLExecute
      implements Execute, QueryExecuteGenerator
{
   private int start = 1;
   private int count = -1;
   private int countType = QueryAdapter.TOTAL_COUNT_NONE;
   protected int queryAdapterIndex;

   public void initialize(ModelAdapter model)
         throws ConfigurationException
   {
      if (this.initialized)
      {
         return;
      }
      super.initialize(model);

      this.queryAdapterIndex = this.getModelAdapter().getFactory().getQueryAdapterId(this.getName());
   }

   public String getExecuteType()
   {
      return "query";
   }

   public void setStart(int start)
   {
      this.start = start;
   }

   public void setCount(int count)
   {
      this.count = count;
   }

   public void setCountType(String countType)
         throws ConfigurationException
   {
      if ("auto".equals(countType))
      {
         this.countType = QueryAdapter.TOTAL_COUNT_AUTO;
      }
      else if ("count".equals(countType))
      {
         this.countType = QueryAdapter.TOTAL_COUNT_COUNT;
      }
      else if ("none".equals(countType))
      {
         this.countType = QueryAdapter.TOTAL_COUNT_NONE;
      }
      else
      {
         throw new ConfigurationException("Error count type:[" + countType + "].");
      }
   }

   public ModelExport execute(AppData data, Connection conn)
         throws ConfigurationException, SQLException, IOException
   {
      boolean inCache = false;
      QueryAdapter query = null;
      if (this.sqlCacheIndex != -1)
      {
         Object temp = data.caches[this.sqlCacheIndex];
         if (temp instanceof QueryAdapter)
         {
            query = (QueryAdapter) temp;
            if (!query.getName().equals(this.getName()))
            {
               query = null;
            }
            else
            {
               inCache = true;
            }
         }
      }
      if (query == null)
      {
         query = this.getModelAdapter().getFactory().createQueryAdapter(this.queryAdapterIndex);
         if (this.sqlCacheIndex != -1)
         {
            data.caches[this.sqlCacheIndex] = query;
         }
      }

      if (this.start != 1)
      {
         query.setStartRow(this.start);
      }
      if (this.count != -1)
      {
         query.setMaxRows(this.count);
      }
      query.setTotalCount(this.countType);
      UserManager um = this.getModelAdapter().getFactory().getUserManager();
      if (um != null)
      {
         User user = um.getUser(data);
         if (user != null)
         {
            query.setPermission(user.getPermission());
         }
         else
         {
            query.setPermission(EmptyPermission.getInstance());
         }
      }

      if (AppData.getAppLogType() != 0)
      {
         Element nowNode = data.getCurrentNode();
         nowNode.addAttribute("queryName", query.getName());
         if (this.start != 1)
         {
            nowNode.addAttribute("start", String.valueOf(this.start));
         }
         if (this.count != -1)
         {
            nowNode.addAttribute("count", String.valueOf(this.count));
         }
         if (this.sqlCacheIndex != -1)
         {
            nowNode.addAttribute("sqlCacheIndex", String.valueOf(this.sqlCacheIndex));
            if (inCache)
            {
               nowNode.addAttribute("inCache", "true");
            }
         }
         if (!this.doExecute)
         {
            nowNode.addAttribute("doExecute", String.valueOf(this.doExecute));
         }
      }
      ParamSetManager psm = new ParamSetManager(query);
      int count = this.setParams(data, psm, 0);
      if (this.doExecute)
      {
         if (count != 0)
         {
            ResultIterator ritr = query.executeQuery(conn);
            ResultIterator[] results = null;
            if (this.pushResult)
            {
               if (count > 1)
               {
                  results = new ResultIterator[count];
                  results[0] = ritr;
                  data.push(results);
               }
               else
               {
                  data.push(ritr);
               }
            }
            for (int i = 1; i < count; i++)
            {
               this.setParams(data, psm, i);
               ritr = query.executeQuery(conn);
               if (this.pushResult)
               {
                  results[i] = ritr;
               }
            }
         }
         else if (this.pushResult)
         {
            data.push(new ResultIterator[0]);
         }
      }
      return null;
   }

}
