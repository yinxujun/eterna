
package self.micromagic.eterna.view.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.view.BaseManager;
import self.micromagic.eterna.view.Function;
import self.micromagic.eterna.view.ViewAdapter;
import self.micromagic.eterna.view.ViewAdapterGenerator;

public class ModifiableViewResImpl
      implements ViewAdapterGenerator.ModifiableViewRes
{
   protected Map functionMap = null;
   protected Set typicalComponentNames = null;
   protected Set resourceNames = null;

   public String addFunction(Function fn)
         throws ConfigurationException
   {
      if (this.functionMap == null) this.functionMap = new HashMap(2);
      EternaFactory shareFactory = fn.getFactory().getShareFactory();
      String fName = fn.getName();
      if (shareFactory != null)
      {
         try
         {
            boolean sameFn = true;
            EternaFactory tmp = shareFactory;
            while (tmp != null)
            {
               // ѭ���жϴ˷����Ƿ��ض����
               Function otherFn = tmp.getFunction(fName);
               if (otherFn != fn)
               {
                  sameFn = false;
                  break;
               }
               tmp = tmp.getShareFactory();
            }
            if (!sameFn)
            {
               // �ض�����ķ�����Ҫ�����ֺ�Ӻ�׺������
               int fnId = 1;
               tmp = shareFactory.getShareFactory();
               while (tmp != null)
               {
                  tmp = tmp.getShareFactory();
                  fnId++;
               }
               // ��׺�������ڵ�EternaFactory��share�㼶����
               fName += "_EFID_" + fnId;
            }
         }
         catch (ConfigurationException ex)
         {
            // �����쳣, ˵��shareFactory��û���ҵ�ͬ���ķ���
         }
      }
      Function oldFn = (Function) this.functionMap.get(fName);
      if (oldFn != null && oldFn != fn)
      {
         ViewTool.log.error("Duplicate function name:[" + fName + "] when add it.");
      }
      if (oldFn == null)
      {
         this.functionMap.put(fName, fn);
         this.addAll(fn.getViewRes());
      }
      return fName;
   }

   /**
    * �򷽷���map�����һ�鷽��.
    */
   public void addAllFunction(Map fnMap)
   {
      if (this.functionMap == null)
      {
         if (fnMap == null || fnMap.size() == 0)
         {
            return;
         }
         this.functionMap = new HashMap(2);
      }
      ViewTool.putAllFunction(this.functionMap, fnMap);
   }

   public void addTypicalComponentNames(String name)
   {
      if (this.typicalComponentNames == null) this.typicalComponentNames = new HashSet(1);
      this.typicalComponentNames.add(name);
   }

   public void addResourceNames(String name)
   {
      if (this.resourceNames == null) this.resourceNames = new HashSet(1);
      this.resourceNames.add(name);
   }

   public void addAll(ViewAdapter.ViewRes res)
         throws ConfigurationException
   {
      this.addAllFunction(res.getFunctionMap());
      if (this.typicalComponentNames == null)
      {
         Set temp = res.getTypicalComponentNames();
         if (temp.size() > 0)
         {
            this.typicalComponentNames = new HashSet(2);
            this.typicalComponentNames.addAll(temp);
         }
      }
      else
      {
         this.typicalComponentNames.addAll(res.getTypicalComponentNames());
      }
      if (this.resourceNames == null)
      {
         Set temp = res.getResourceNames();
         if (temp.size() > 0)
         {
            this.resourceNames = new HashSet(2);
            this.resourceNames.addAll(temp);
         }
      }
      else
      {
         this.resourceNames.addAll(res.getResourceNames());
      }
   }

   public Map getFunctionMap()
   {
      if (this.functionMap == null) return Collections.EMPTY_MAP;
      return Collections.unmodifiableMap(this.functionMap);
   }

   public Set getTypicalComponentNames()
   {
      if (this.typicalComponentNames == null) return Collections.EMPTY_SET;
      return Collections.unmodifiableSet(this.typicalComponentNames);
   }

   public Set getResourceNames()
   {
      if (this.resourceNames == null) return Collections.EMPTY_SET;
      return Collections.unmodifiableSet(this.resourceNames);
   }

}
