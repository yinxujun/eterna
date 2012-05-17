
package self.micromagic.eterna.sql.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.security.Permission;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.sql.QueryAdapter;
import self.micromagic.eterna.sql.QueryAdapterGenerator;
import self.micromagic.eterna.sql.ResultIterator;
import self.micromagic.eterna.sql.ResultMetaData;
import self.micromagic.eterna.sql.ResultReader;
import self.micromagic.eterna.sql.ResultReaderManager;
import self.micromagic.eterna.sql.ResultRow;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.model.AppDataLogExecute;
import self.micromagic.util.BooleanRef;
import self.micromagic.util.StringTool;
import org.dom4j.Element;

public abstract class AbstractQueryAdapter extends SQLAdapterImpl
      implements QueryAdapter, QueryAdapterGenerator
{
   private String readerOrder = null;
   private List tempResultReaders = new ArrayList();
   private Set otherReaderManagerSet = null;
   private Map tempNameToIndexMap = new HashMap();
   private ResultReaderManager readerManager = null;
   private boolean readerManagerSetted = false;
   private String readerManagerName = null;

   protected Permission permission = null;

   private int orderIndex = -1;
   private boolean forwardOnly = true;
   private String[] orderStrs = null;
   private String[] orderNames = null;
   private QueryAdapter countQuery = null;

   private int startRow = 1;
   private int maxRows = -1;
   private int totalCount = -1;

   public void initialize(EternaFactory factory)
         throws ConfigurationException
   {
      if (this.initialized)
      {
         return;
      }
      super.initialize(factory);
      Iterator itr = this.tempResultReaders.iterator();
      while (itr.hasNext())
      {
         ((ResultReader) itr.next()).initialize(this.getFactory());
      }
      this.readerManager = this.createTempReaderManager();
      this.tempResultReaders = null;
      this.tempNameToIndexMap = null;
      String tmp = (String) this.getAttribute(OTHER_READER_MANAGER_SET_FLAG);
      if (tmp != null)
      {
         String[] tmpArr = StringTool.separateString(tmp, ",", true);
         if (tmpArr.length > 0)
         {
            this.otherReaderManagerSet = new HashSet();
            for (int i = 0; i < tmpArr.length; i++)
            {
               this.otherReaderManagerSet.add(tmpArr[i]);
            }
         }
      }
   }

   public String getType()
   {
      return SQL_TYPE_QUERY;
   }

   private ResultReaderManager createTempReaderManager()
         throws ConfigurationException
   {
      ResultReaderManagerImpl temp = new ResultReaderManagerImpl();
      temp.setName("[query]+" + this.getName());
      temp.setParentName(this.readerManagerName);
      temp.setReaderOrder(this.readerOrder);
      Iterator itr = this.tempResultReaders.iterator();
      while (itr.hasNext())
      {
         temp.addReader((ResultReader) itr.next());
      }
      temp.initialize(this.getFactory());
      if (temp.getReaderCount() > 0)
      {
         temp.lock();
      }
      return temp;
   }

   public String getReaderOrder()
   {
      return this.readerOrder;
   }

   public void setReaderOrder(String readerOrder)
   {
      this.readerOrder = readerOrder;
   }

   public ResultReaderManager getReaderManager()
         throws ConfigurationException
   {
      if (this.readerManagerSetted)
      {
         // ��������ù�readerManager, ��Ͳ���Ҫ�ٸ���һ����, ��Ϊ�Ѿ����ƹ���.
         return this.readerManager;
      }
      return this.readerManager.copy(null);
   }

   public void setReaderManager(ResultReaderManager readerManager)
         throws ConfigurationException
   {
      if (this.readerManager != readerManager)
      {
         String name1 = readerManager.getName();
         String name2 = this.readerManager.getName();
         if (!name1.equals(name2))
         {
            if (this.otherReaderManagerSet != null)
            {
               if (!this.otherReaderManagerSet.contains(name1))
               {
                  throw new ConfigurationException(
                        "The setted readerManager name [" + name1 + "],  not same [" + name2
                        + "] in query[" + this.getName() + "], or in " + OTHER_READER_MANAGER_SET_FLAG
                        + " " + this.otherReaderManagerSet + ".");
               }
            }
            else
            {
               throw new ConfigurationException(
                     "The setted readerManager name [" + name1 + "],  not same [" + name2
                     + "] in query[" + this.getName() + "].");
            }
         }
         this.readerManager = readerManager;
         this.readerManagerSetted = true;
      }
      if (this.orderIndex != -1)
      {
         String orderStr = this.readerManager.getOrderByString();
         if (this.orderStrs != null)
         {
            orderStr = StringTool.linkStringArr(this.orderStrs, ", ") + ", " + orderStr;
         }
         if (log.isDebugEnabled())
         {
            log.debug("Set order at(" + this.orderIndex + "):" + orderStr);
         }
         this.setSubSQL(this.orderIndex, orderStr);
      }
   }

   public void addResultReader(ResultReader reader)
         throws ConfigurationException
   {
      if (this.tempNameToIndexMap.containsKey(reader.getName()))
      {
         throw new ConfigurationException(
               "Duplicate [ResultReader] name:" + reader.getName()
               + ", in query[" + this.getName() + "].");
      }
      this.tempResultReaders.add(reader);
      this.tempNameToIndexMap.put(reader.getName(), new Integer(this.tempResultReaders.size()));
   }

   protected Object clone()
   {
      AbstractQueryAdapter other = (AbstractQueryAdapter) super.clone();
      other.permission = null;
      other.orderStrs = null;
      other.orderNames = null;
      other.readerManagerSetted = false;
      other.startRow = 1;
      other.maxRows = -1;
      if (other.orderIndex != -1)
      {
         // ���������orderIndex, ������Ĭ�ϵ�ֵ
         try
         {
            other.setSubSQL(other.orderIndex, "");
         }
         catch (ConfigurationException ex) {}
      }
      return other;
   }

   public QueryAdapter createQueryAdapter()
         throws ConfigurationException
   {
      return (QueryAdapter) this.create();
   }

   public void setOrderIndex(int orderIndex)
   {
      this.orderIndex = orderIndex;
   }

   protected int getOrderIndex()
   {
      return this.orderIndex;
   }

   public boolean canOrder()
   {
      return this.orderIndex != -1;
   }

   public void setForwardOnly(boolean forwardOnly)
   {
      this.forwardOnly = forwardOnly;
   }

   public boolean isForwardOnly()
   {
      return this.forwardOnly;
   }

   protected Permission getPermission0()
   {
      return this.permission;
   }

   public void setPermission(Permission permission)
   {
      this.permission = permission;
   }

   public void setReaderManagerName(String name)
   {
      this.readerManagerName = name;
   }

   public void setSingleOrder(String readerName)
         throws ConfigurationException
   {
      if (this.orderIndex != -1)
      {
         this.setSingleOrder(readerName, 0);
      }
   }

   public void setSingleOrder(String readerName, int orderType)
         throws ConfigurationException
   {
      if (this.orderIndex != -1)
      {
         ResultReader reader = this.readerManager.getReader(readerName);
         if (reader == null)
         {
            log.error("Single order, not found the reader: [" + readerName
                  + "] in query[" + this.getName() + "].");
            return;
         }
         String orderStr = reader.getOrderName();
         if (orderType == 0)
         {
            if (this.orderStrs != null && orderStr.equals(this.orderStrs[0]))
            {
               orderStr = orderStr + " DESC";
               this.orderNames = new String[]{readerName + "D"};
            }
            else
            {
               this.orderNames = new String[]{readerName + "A"};
            }
         }
         else
         {
            orderStr = orderType < 0 ? orderStr + " DESC" : orderStr;
            this.orderNames = orderType < 0 ?
                  new String[]{readerName + "D"} : new String[]{readerName + "A"};
         }
         this.orderStrs = new String[]{orderStr};
         String settingOrder = this.readerManager.getOrderByString();
         if (settingOrder != null && settingOrder.length() > 0)
         {
            orderStr = orderStr + ", " + settingOrder;
         }
         if (log.isDebugEnabled())
         {
            log.debug("Set order at(" + this.orderIndex + "):" + orderStr);
         }
         this.setSubSQL(this.orderIndex, orderStr);
      }
   }

   public String getSingleOrder(BooleanRef desc)
   {
      if (this.orderNames == null)
      {
         return null;
      }
      int index = this.orderNames[0].length() - 1;
      if (desc != null)
      {
         desc.value = this.orderNames[0].charAt(index) == 'D';
      }
      return this.orderNames[0].substring(0, index);
   }

   public void setMultipleOrder(String[] orderNames)
         throws ConfigurationException
   {
      if (orderNames == null || orderNames.length == 0)
      {
         this.orderNames = null;
         this.orderStrs = null;
      }
      if (this.orderIndex != -1)
      {
         this.orderNames = new String[orderNames.length];
         this.orderStrs = new String[orderNames.length];
         for (int i = 0; i < orderNames.length; i++)
         {
            String readerName = orderNames[i].substring(0, orderNames[i].length() - 1);
            char orderType = orderNames[i].charAt(orderNames[i].length() - 1);
            ResultReader reader = this.readerManager.getReader(readerName);
            if (reader == null)
            {
               log.error("Multiple order, not found the reader: [" + readerName
                     + "] in query[" + this.getName() + "].");
               return;
            }
            String orderStr = reader.getOrderName();
            orderStr = orderType == 'D' ? orderStr + " DESC" : orderStr;
            this.orderNames[i] = orderNames[i];
            this.orderStrs[i] = orderStr;
         }

         String realOrderStr = StringTool.linkStringArr(this.orderStrs, ", ");
         String settingOrder = this.readerManager.getOrderByString();
         if (settingOrder != null && settingOrder.length() > 0)
         {
            realOrderStr = realOrderStr + ", " + settingOrder;
         }
         if (log.isDebugEnabled())
         {
            log.debug("Set order at(" + this.orderIndex + "):" + realOrderStr);
         }
         this.setSubSQL(this.orderIndex, realOrderStr);
      }
   }

   /**
    * @param rs   �������rs��Ϊ��, ��Ҫ�ж�readerManager�е�reader����
    *             �Ƿ�Ϊ0, Ϊ0�Ļ���Ҫ����rs��readerManager��ʼ��Ĭ�ϵ���
    */
   protected ResultReaderManager getReaderManager0(ResultSet rs)
   {
      if (rs == null)
      {
         return this.readerManager;
      }
      try
      {
         if (this.readerManager.getReaderCount() == 0)
         {
            this.readerManager.setColNameSensitive(false);
            this.initDefaultResultReaders(rs);
         }
      }
      catch (Exception ex)
      {
         log.warn("Init default ResultReaders error.", ex);
      }
      return this.readerManager;
   }

   public int getStartRow()
   {
      return this.startRow;
   }

   /**
    * ���ôӵڼ�����¼��ʼȡֵ����1��ʼ������
    */
   public void setStartRow(int startRow)
   {
      this.startRow = startRow < 1 ? 1 : startRow;
   }

   public int getMaxRows()
   {
      return this.maxRows;
   }

   /**
    * ����ȡ��������¼��-1��ʾȡ��Ϊֹ
    */
   public void setMaxRows(int maxRows)
   {
      this.maxRows = maxRows < -1 ? -1 : maxRows;
   }

   public int getTotalCount()
   {
      return this.totalCount;
   }

   public void setTotalCount(int totalCount)
         throws ConfigurationException
   {
      if (this.totalCount < -3)
      {
         throw new ConfigurationException("Error total count:" + totalCount + ".");
      }
      this.totalCount = totalCount;
   }

   public void execute(Connection conn)
         throws ConfigurationException, SQLException
   {
      this.executeQuery(conn);
   }

   public ResultIterator executeQuery(Connection conn)
         throws ConfigurationException, SQLException
   {
      long startTime = System.currentTimeMillis();
      Statement stmt = null;
      ResultSet rs = null;
      Throwable exception = null;
      ResultIterator result = null;
      try
      {
         if (this.hasActiveParam())
         {
            PreparedStatement temp;
            if (this.isForwardOnly())
            {
               temp = conn.prepareStatement(this.getPreparedSQL());
            }
            else
            {
               temp = conn.prepareStatement(this.getPreparedSQL(),
                     ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            }
            stmt = temp;
            this.prepareValues(temp);
            rs = temp.executeQuery();
         }
         else
         {
            if (this.isForwardOnly())
            {
               stmt = conn.createStatement();
            }
            else
            {
               stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                     ResultSet.CONCUR_READ_ONLY);
            }
            rs = stmt.executeQuery(this.getPreparedSQL());
         }
         ResultIteratorImpl ritr = this.executeQuery(rs);
         if (ritr.needCount)
         {
            rs.close();
            stmt.close();
            rs = null;
            stmt = null;
            if (this.countQuery == null)
            {
               this.countQuery = new CountQueryAdapter(this);
            }
            int count = this.countQuery.executeQuery(conn).nextRow().getInt(1);
            ritr.realRecordCount = count;
            ritr.realRecordCountAvailable = true;
         }
         result = ritr;
         return ritr;
      }
      catch (ConfigurationException ex)
      {
         exception = ex;
         throw ex;
      }
      catch (SQLException ex)
      {
         exception = ex;
         throw ex;
      }
      catch (RuntimeException ex)
      {
         exception = ex;
         throw ex;
      }
      catch (Error ex)
      {
         exception = ex;
         throw ex;
      }
      finally
      {
         if (this.logSQL("query", System.currentTimeMillis() - startTime, exception, conn))
         {
            if (result != null && AppData.getAppLogType() == 1)
            {
               Element nowNode = AppData.getCurrentData().getCurrentNode();
               if (nowNode != null)
               {
                  AppDataLogExecute.printObject(nowNode.addElement("result"), result);
               }
            }
         }
         if (rs != null)
         {
            rs.close();
         }
         if (stmt != null)
         {
            stmt.close();
         }
      }
   }

   private ResultIteratorImpl executeQuery(ResultSet rs)
         throws ConfigurationException, SQLException
   {
      int start = this.startRow - 1;
      int recordCount = 0;
      int realRecordCount = 0;
      boolean realRecordCountAvailable = false;
      boolean hasMoreRecord = false;
      boolean hasRecord = true;
      boolean isForwardOnly = rs.getType() == ResultSet.TYPE_FORWARD_ONLY;
      List readerList = this.getReaderManager0(rs).getReaderList(this.getPermission0());

      if (start > 0)
      {
         if (!isForwardOnly)
         {
            hasRecord = rs.absolute(start);
         }
         else
         {
            for (; recordCount < start && hasRecord; recordCount++, hasRecord = rs.next());
         }
      }
      ArrayList result;
      ResultIteratorImpl ritr;
      if (!hasRecord)
      {
         recordCount--;
         realRecordCountAvailable = true;
         hasMoreRecord = false;
         result = new ArrayList(0);
         ritr = new ResultIteratorImpl(readerList);
      }
      else
      {
         result = new ArrayList(this.maxRows == -1 ? 32 : this.maxRows);
         ritr = new ResultIteratorImpl(readerList);
         if (this.maxRows == -1)
         {
            while (rs.next())
            {
               recordCount++;
               result.add(this.readResults(readerList, rs, ritr));
            }
            realRecordCount = recordCount;
            realRecordCountAvailable = true;
            hasMoreRecord = false;
         }
         else
         {
            int i = 0;
            for (; i < this.maxRows && (hasMoreRecord = rs.next()); i++)
            {
               recordCount++;
               result.add(this.readResults(readerList, rs, ritr));
            }
            // ��ô�ж��Ƿ�ֹĳЩjdbc�ڵ�һ��nextΪfalse��, �����next�ֱ��true
            if (hasMoreRecord && (hasMoreRecord = rs.next()))
            {
               recordCount++;
               realRecordCountAvailable = false;
            }
            else
            {
               realRecordCountAvailable = true;
            }
         }
      }
      if (this.totalCount == TOTAL_COUNT_AUTO)
      {
         if (!isForwardOnly)
         {
            realRecordCountAvailable = rs.last();
            realRecordCount = rs.getRow();
         }
         else
         {
            if (hasMoreRecord)
            {
               for (; rs.next(); recordCount++);
            }
            realRecordCount = recordCount;
            realRecordCountAvailable = true;
         }
      }
      else if (this.totalCount == TOTAL_COUNT_NONE)
      {
         realRecordCount = recordCount;
      }
      else if (this.totalCount == TOTAL_COUNT_COUNT)
      {
         if (!realRecordCountAvailable)
         {
            ritr.needCount = true;
         }
         else
         {
            realRecordCount = recordCount;
         }
      }
      else if (this.totalCount >= 0)
      {
         if (!realRecordCountAvailable)
         {
            realRecordCount = this.totalCount;
            realRecordCountAvailable = true;
         }
         else
         {
            realRecordCount = recordCount;
         }
      }

      ritr.setResult(result);
      ritr.realRecordCount = realRecordCount;
      ritr.recordCount = result.size();
      ritr.realRecordCountAvailable = realRecordCountAvailable;
      ritr.hasMoreRecord = hasMoreRecord;
      return ritr;
   }

   private void initDefaultResultReaders(ResultSet rs)
         throws ConfigurationException, SQLException
   {
      Map temp = new HashMap();
      ResultSetMetaData meta = rs.getMetaData();
      int count = meta.getColumnCount();
      for (int i = 0; i < count; i++)
      {
         String colname = meta.getColumnName(i + 1);
         String name = colname;
         if (temp.get(name) != null)
         {
            name = colname + "+" + (i + 1);
         }
         temp.put(name, colname);
         ResultReaders.ObjectReader reader = new ResultReaders.ObjectReader(name);
         reader.setColumnIndex(i + 1);
         this.readerManager.addReader(reader);
      }
   }

   protected Object[] getResults(List readerList, ResultSet rs)
         throws ConfigurationException, SQLException
   {
      int count = readerList.size();
      Iterator itr = readerList.iterator();
      Object[] values = new Object[count];
      ResultReader reader = null;
      try
      {
         for (int i = 0; i < count; i++)
         {
            reader = (ResultReader) itr.next();
            values[i] = reader.readResult(rs);
         }
      }
      catch (Throwable ex)
      {
         if (reader != null)
         {
            log.error("Error when read result, reader[" + reader.getName()
                  + "], query[" + this.getName() + "].");
         }
         if (ex instanceof SQLException)
         {
            throw (SQLException) ex;
         }
         else if (ex instanceof ConfigurationException)
         {
            throw (ConfigurationException) ex;
         }
         else if (ex instanceof RuntimeException)
         {
            throw (RuntimeException) ex;
         }
         else
         {
            throw new ConfigurationException(ex);
         }
      }
      return values;
   }

   protected abstract ResultRow readResults(List readerList, ResultSet rs,
         ResultIterator resultIterator)
         throws ConfigurationException, SQLException;

   private class ResultIteratorImpl extends AbstractResultIterator
         implements ResultIterator
   {
      private int realRecordCount;
      private int recordCount;
      private boolean realRecordCountAvailable;
      private boolean hasMoreRecord;
      private boolean needCount = false;

      public ResultIteratorImpl(List readerList)
      {
         super(readerList);
      }

      public void setResult(List result)
      {
         this.result = result;
         this.resultItr = this.result.iterator();
      }

      public ResultMetaData getMetaData()
      {
         return new ResultMetaDataImpl(this.readerList, AbstractQueryAdapter.this);
      }

      public int getRealRecordCount()
      {
         return this.realRecordCount;
      }

      public int getRecordCount()
      {
         return this.recordCount;
      }

      public boolean isRealRecordCountAvailable()
      {
         return this.realRecordCountAvailable;
      }

      public boolean isHasMoreRecord()
      {
         return this.hasMoreRecord;
      }

   }

}
