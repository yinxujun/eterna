
package self.micromagic.eterna.sql.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.security.Permission;
import self.micromagic.eterna.security.PermissionSet;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.share.OrderManager;
import self.micromagic.eterna.sql.NullResultReader;
import self.micromagic.eterna.sql.ResultReader;
import self.micromagic.eterna.sql.ResultReaderManager;
import self.micromagic.util.Utility;

public class ResultReaderManagerImpl
      implements ResultReaderManager
{
   private boolean initialized;
   private boolean locked = false;

   /**
    * 锁住 readerMap 的标志, 比如在调用query的getReaderManager方法时
    * 就不需要复制 readerMap.
    */
   private boolean locked2 = false;

   private boolean nonePermission;
   private boolean colNameSensitive = true;

   private String name = null;
   private String parentName = null;
   private ResultReaderManager[] parents = null;
   private EternaFactory factory;

   /**
    * 定义reader顺序的字符串
    */
   private String readerOrder = null;

   private Map readerMap;
   private Map nameToIndexMap;

   private List readerList;
   private List allReaderList;

   private List orderList;

   /**
    * 生成的sql语句中的"order by"子句
    */
   private String orderStr;

   public ResultReaderManagerImpl()
   {
      this.initialized = false;
      this.nonePermission = true;

      this.readerMap = new HashMap();
      this.nameToIndexMap = new HashMap();
      this.readerList = new ArrayList();
      this.orderList = new ArrayList(0);
      this.orderStr = null;
   }

   public ResultReaderManagerImpl(boolean nonePomission, boolean lock2,
         String readerOrder, Map readerMap, Map nameToIndexMap, List allReaderList,
         List readerList, List orderList, String orderStr)
   {
      this.initialized = true;
      this.nonePermission = nonePomission;

      this.locked = false;
      this.locked2 = lock2;

      this.readerOrder = readerOrder;
      this.readerMap = lock2 ? readerMap : new HashMap(readerMap);
      this.nameToIndexMap = lock2 ? nameToIndexMap : new HashMap(nameToIndexMap);
      this.allReaderList = lock2 ? allReaderList : null;
      this.readerList = lock2 ? readerList : new ArrayList(readerList);
      this.orderList = orderList;
      this.orderStr = orderStr;
   }

   public ResultReaderManagerImpl(boolean nonePomission, String readerOrder, Map readerMap,
         Map nameToIndexMap, List readerList, List orderList, String orderStr)
   {
      this.initialized = false;
      this.nonePermission = nonePomission;

      this.locked = false;
      this.locked2 = false;

      this.readerOrder = readerOrder;
      this.readerMap = new HashMap(readerMap);
      this.nameToIndexMap = new HashMap(nameToIndexMap);
      this.allReaderList = null;
      this.readerList = new ArrayList(readerList);
      this.orderList = new ArrayList(orderList);
      this.orderStr = orderStr;
   }

   public void initialize(EternaFactory factory)
         throws ConfigurationException
   {
      if (!this.initialized)
      {
         this.initialized = true;
         this.factory = factory;

         Iterator itr = this.readerMap.values().iterator();
         while (itr.hasNext())
         {
            ((ResultReader) itr.next()).initialize(factory);
         }

         if (this.parentName != null)
         {
            if (this.parentName.indexOf(',') == -1)
            {
               this.parents = new ResultReaderManager[1];
               this.parents[0] = factory.getReaderManager(this.parentName);
               if (this.parents[0] == null)
               {
                  SQLManager.log.warn(
                        "The ResultReaderManager [" + this.parentName + "] not found.");
               }
            }
            else
            {
               StringTokenizer token = new StringTokenizer(this.parentName, ",");
               this.parents = new ResultReaderManager[token.countTokens()];
               for (int i = 0; i < this.parents.length; i++)
               {
                  String temp = token.nextToken().trim();
                  this.parents[i] = factory.getReaderManager(temp);
                  if (this.parents[i] == null)
                  {
                     SQLManager.log.warn("The ResultReaderManager [" + temp + "] not found.");
                  }
               }
            }
         }
      }
   }

   public void setName(String name)
         throws ConfigurationException
   {
      if (this.initialized)
      {
         throw new ConfigurationException("You can't set name at initialized ResultReaderManager.");
      }
      this.name = name;
   }

   public String getName()
   {
      return this.name;
   }

   public void setParentName(String name)
         throws ConfigurationException
   {
      if (this.initialized)
      {
         throw new ConfigurationException("You can't set parent name at initialized ResultReaderManager.");
      }
      this.parentName = name;
   }

   public String getParentName()
   {
      return this.parentName;
   }

   public ResultReaderManager getParent()
   {
      if (this.parents != null && this.parents.length > 0)
      {
         return this.parents[0];
      }
      return null;
   }

   public EternaFactory getFactory()
   {
      return this.factory;
   }

   public String getReaderOrder()
   {
      return this.readerOrder;
   }

   public void setReaderOrder(String readerOrder)
   {
      this.readerOrder = readerOrder;
   }

   public int getReaderCount()
         throws ConfigurationException
   {
      this.getReaderList0();
      return this.readerMap.size();
   }

   public ResultReader getReader(String name)
         throws ConfigurationException
   {
      this.getReaderList0();
      return (ResultReader) this.readerMap.get(this.colNameSensitive ? name : name.toUpperCase());
   }

   public ResultReader addReader(ResultReader reader)
         throws ConfigurationException
   {
      if (this.locked2)
      {
         throw new ConfigurationException("You can't add reader at initialized ResultReaderManager.");
      }
      if (this.locked)
      {
         throw new ConfigurationException("You can't invoke addReader when ResultReaderManager locked.");
      }

      this.allReaderList = null;
      if (reader.getPermissionSet() != null)
      {
         this.nonePermission = false;
      }
      String readerName = this.colNameSensitive ? reader.getName() : reader.getName().toUpperCase();
      ResultReader temp = (ResultReader) this.readerMap.put(readerName, reader);

      if (temp != null)
      {
         throw new ConfigurationException(
               "Duplicate [ResultReader] name:" + reader.getName() + ".");
      }
      this.readerList.add(reader);
      this.nameToIndexMap.put(readerName, Utility.createInteger(this.readerList.size()));
      return temp;
   }

   public void setColNameSensitive(boolean colNameSensitive)
         throws ConfigurationException
   {
      if (this.getReaderCount() == 0)
      {
         this.colNameSensitive = colNameSensitive;
      }
      else
      {
         throw new ConfigurationException("You can't set column name sensitive when ResultReaderManager has readers.");
      }
   }

   public void setReaderList(String[] names)
         throws ConfigurationException
   {
      this.getReaderList0();
      if (this.locked)
      {
         throw new ConfigurationException("You can't invoke setReaderList when ResultReaderManager locked.");
      }

      this.readerList = new ArrayList(names.length);
      this.orderList = new ArrayList(5);
      this.nameToIndexMap = new HashMap(names.length * 2);
      this.orderStr = null;
      for (int i = 0; i < names.length; i++)
      {
         String name = names[i];
         char orderType = name.charAt(name.length() - 1);
         name = name.substring(0, name.length() - 1);
         ResultReader reader = this.getReader(name);
         if (reader == null)
         {
            throw new ConfigurationException(
                  "Invalid ResultReader name:" + name + " at ResultReaderManager "
                  + this.getName() + ".");
         }
         if (orderType != '-')
         {
            this.orderList.add(reader.getOrderName() + (orderType == 'D' ? " DESC" : "" ));
         }
         this.readerList.add(reader);
         if (this.colNameSensitive)
         {
            this.nameToIndexMap.put(reader.getName(),
                  Utility.createInteger(this.readerList.size()));
         }
         else
         {
            this.nameToIndexMap.put(reader.getName().toUpperCase(),
                  Utility.createInteger(this.readerList.size()));
         }
      }
   }

   public int getIndexByName(String name)
         throws ConfigurationException
   {
      this.getReaderList0();
      Integer i = (Integer) this.nameToIndexMap.get(
            this.colNameSensitive ? name : name.toUpperCase());
      if (i == null)
      {
         throw new ConfigurationException(
               "Invalid ResultReader name:" + name + " at ResultReaderManager "
               + this.getName() + ".");
      }
      return i.intValue();
   }

   public String getOrderByString()
   {
      if (this.orderStr == null)
      {
         StringBuffer temp = new StringBuffer(this.orderList.size() * 16);
         Iterator itr = this.orderList.iterator();
         if (itr.hasNext())
         {
            temp.append(itr.next());
         }
         while (itr.hasNext())
         {
            temp.append(", ").append(itr.next());
         }
         this.orderStr = temp.toString();
      }
      return this.orderStr;
   }

   public List getReaderList()
         throws ConfigurationException
   {
      return this.getReaderList0();
   }

   public List getReaderList(Permission permission)
         throws ConfigurationException
   {
      this.getReaderList0();
      if (this.nonePermission)
      {
         return Collections.unmodifiableList(this.readerList);
      }
      if (permission == null)
      {
         return Collections.unmodifiableList(this.readerList);
      }

      int count = 0;
      Iterator srcItr = this.readerList.iterator();
      ArrayList temp = null;
      while (srcItr.hasNext())
      {
         ResultReader reader = (ResultReader) srcItr.next();
         if (!checkPermission(reader, permission))
         {
            if (temp == null)
            {
               temp = new ArrayList(this.readerList.size());
               srcItr = this.readerList.iterator();
               for (int i = 0; i < count; i++)
               {
                  temp.add(srcItr.next());
               }
               srcItr.next();
            }
            temp.add(new NullResultReader(reader.getName()));
         }
         else
         {
            if (temp != null)
            {
               temp.add(reader);
            }
         }
         count++;
      }
      return Collections.unmodifiableList(temp == null ? this.readerList : temp);
   }

   private boolean checkPermission(ResultReader reader, Permission permission)
         throws ConfigurationException
   {
      PermissionSet ps = reader.getPermissionSet();
      if (ps == null)
      {
         return true;
      }
      return ps.checkPermission(permission);
   }

   public ResultReader getReaderInList(int index)
         throws ConfigurationException
   {
      this.getReaderList0();
      try
      {
         return (ResultReader) this.readerList.get(index);
      }
      catch (Exception ex)
      {
         throw new ConfigurationException(ex.getMessage());
      }
   }

   public void lock()
   {
      this.locked = true;
   }

   public ResultReaderManager copy(String copyName)
         throws ConfigurationException
   {
      ResultReaderManagerImpl other;
      if (this.initialized)
      {
         this.getReaderList0();
         other = new ResultReaderManagerImpl(this.nonePermission, copyName == null,
               this.readerOrder, this.readerMap, this.nameToIndexMap, this.allReaderList,
               this.readerList, this.orderList, this.orderStr);
      }
      else
      {
         other = new ResultReaderManagerImpl(this.nonePermission, this.readerOrder, this.readerMap,
               this.nameToIndexMap, this.readerList, this.orderList, this.orderStr);
      }
      other.name = copyName == null ? this.name : this.name + "+" + copyName;
      other.parentName = this.parentName;
      other.parents = this.parents;
      return other;
   }

   private List getReaderList0()
         throws ConfigurationException
   {
      if (this.allReaderList != null)
      {
         return this.allReaderList;
      }
      OrderManager om = new OrderManager();
      List resultList = om.getOrder(new MyOrderItem(), this.parents, this.readerOrder,
            this.readerList, this.readerMap);
      Iterator itr = resultList.iterator();
      int index = 1;
      while (itr.hasNext())
      {
         ResultReader reader = (ResultReader) itr.next();
         if (this.colNameSensitive)
         {
            this.nameToIndexMap.put(reader.getName(), Utility.createInteger(index));
         }
         else
         {
            this.nameToIndexMap.put(reader.getName().toUpperCase(), Utility.createInteger(index));
         }
         index++;
      }
      this.readerList = new ArrayList(resultList.size());
      this.readerList.addAll(resultList);
      this.allReaderList = Collections.unmodifiableList(resultList);
      return this.allReaderList;
   }

   private static class MyOrderItem extends OrderManager.OrderItem
   {
      private ResultReader reader;

      public MyOrderItem()
      {
         super("", null);
      }

      protected MyOrderItem(String name, Object obj)
      {
         super(name, obj);
         this.reader = (ResultReader) obj;
      }

      public boolean isIgnore()
            throws ConfigurationException
      {
         return this.reader.isIgnore();
      }

      public OrderManager.OrderItem create(Object obj)
            throws ConfigurationException
      {
         if (obj == null)
         {
            return null;
         }
         ResultReader reader = (ResultReader) obj;
         return new MyOrderItem(reader.getName(), reader);
      }

      public Iterator getOrderItemIterator(Object container)
            throws ConfigurationException
      {
         ResultReaderManager rm = (ResultReaderManager) container;
         return rm.getReaderList().iterator();
      }

   }

}
