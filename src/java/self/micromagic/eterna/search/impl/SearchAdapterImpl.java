
package self.micromagic.eterna.search.impl;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.XMLWriter;
import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.search.ColumnSetting;
import self.micromagic.eterna.search.ConditionBuilder;
import self.micromagic.eterna.search.ConditionProperty;
import self.micromagic.eterna.search.ParameterSetting;
import self.micromagic.eterna.search.SearchAdapter;
import self.micromagic.eterna.search.SearchAdapterGenerator;
import self.micromagic.eterna.search.SearchManager;
import self.micromagic.eterna.security.EmptyPermission;
import self.micromagic.eterna.security.Permission;
import self.micromagic.eterna.security.PermissionSet;
import self.micromagic.eterna.security.User;
import self.micromagic.eterna.security.UserManager;
import self.micromagic.eterna.share.AbstractGenerator;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.share.OrderManager;
import self.micromagic.eterna.share.SessionCache;
import self.micromagic.eterna.share.TypeManager;
import self.micromagic.eterna.sql.QueryAdapter;
import self.micromagic.eterna.sql.ResultIterator;
import self.micromagic.eterna.sql.ResultReaderManager;
import self.micromagic.eterna.sql.preparer.PreparerManager;
import self.micromagic.util.BooleanRef;
import self.micromagic.util.MemoryChars;

public class SearchAdapterImpl extends AbstractGenerator
      implements SearchAdapter, SearchAdapterGenerator
{
   private final static int[] conditionDocumentCounts = {1, 3, 7};

   private int maxPageSize = -1;

   private String parentName;
   private SearchAdapter[] parents;

   private String sessionQueryTag;
   private String searchManagerName = null;
   private String queryName;
   private int queryIndex;
   private String columnType;
   private ColumnSetting columnSetting = null;
   private ParameterSetting parameterSetting = null;
   private int countType = 0;
   private String countReaderName = null;
   private String countSearchName = null;
   private int countSearchIndex = -1;

   private String otherName;
   private SearchAdapter[] others;
   private String conditionPropertyOrderWithOther = null;

   private boolean needWrap = true;
   private boolean specialCondition = false;
   private int conditionIndex;

   private String conditionPropertyOrder = null;
   private PermissionSet[] permissionSets = null;
   private Map conditionPropertyMap =  new HashMap();
   private List conditionProperties = new LinkedList();
   private ConditionProperty[] allConditionProperties = null;
   private ConditionProperty[] allConditionPropertiesWithOther = null;
   private int conditionDocumentCount = 1;
   private MemoryChars conditionDocument = null;
   private MemoryChars[] conditionDocuments = null;

   private boolean initialized = false;

   public void initialize(EternaFactory factory)
         throws ConfigurationException
   {
      if (this.initialized)
      {
         return;
      }
      this.initialized = true;
      this.sessionQueryTag = "s:" + this.getName() + ":" + factory.getFactoryManager().getId();
      if (this.parentName != null)
      {
         if (this.parentName.indexOf(',') == -1)
         {
            this.parents = new SearchAdapter[1];
            this.parents[0] = factory.createSearchAdapter(this.parentName);
            if (this.parents[0] == null)
            {
               log.warn("The search parent [" + this.parentName + "] not found.");
            }
         }
         else
         {
            StringTokenizer token = new StringTokenizer(this.parentName, ",");
            this.parents = new SearchAdapter[token.countTokens()];
            for (int i = 0; i < this.parents.length; i++)
            {
               String temp = token.nextToken().trim();
               this.parents[i] = factory.createSearchAdapter(temp);
               if (this.parents[i] == null)
               {
                  log.warn("The search parent [" + temp + "] not found.");
               }
            }
         }
      }

      if (this.otherName != null)
      {
         if (this.otherName.indexOf(',') == -1)
         {
            this.others = new SearchAdapter[1];
            this.others[0] = factory.createSearchAdapter(this.otherName);
            if (this.others[0] == null)
            {
               log.warn("The search parent [" + this.otherName + "] not found.");
            }
         }
         else
         {
            StringTokenizer token = new StringTokenizer(this.otherName, ",");
            this.others = new SearchAdapter[token.countTokens()];
            for (int i = 0; i < this.others.length; i++)
            {
               String temp = token.nextToken().trim();
               this.others[i] = factory.createSearchAdapter(temp);
               if (this.others[i] == null)
               {
                  log.warn("The search parent [" + temp + "] not found.");
               }
            }
         }
      }

      if (this.queryName != null && !NONE_QUERY_NAME.equals(this.queryName))
      {
         this.queryIndex = this.getFactory().getQueryAdapterId(this.queryName);
      }
      else
      {
         this.queryName = NONE_QUERY_NAME;
         this.queryIndex = -1;
      }

      if (this.searchManagerName == null)
      {
         this.searchManagerName = this.sessionQueryTag;
      }
      if (this.countSearchName != null)
      {
         this.countSearchIndex = this.getFactory().getSearchAdapterId(this.countSearchName);
      }

      Iterator cps = this.conditionProperties.iterator();
      while (cps.hasNext())
      {
         ConditionProperty cp = (ConditionProperty) cps.next();
         cp.initialize(factory);
      }

      if (this.parameterSetting != null)
      {
         this.parameterSetting.initParameterSetting(this);
      }
      if (this.columnSetting != null)
      {
         this.columnSetting.initColumnSetting(this);
      }

      if (this.conditionIndex == 0)
      {
         if (this.getConditionPropertyCount() > 0)
         {
            throw new ConfigurationException("Can't set conditionIndex 0 in a search witch has conditionProperty.");
         }
      }
   }

   public Object create()
         throws ConfigurationException
   {
      return this.createSearchAdapter();
   }

   public SearchAdapter createSearchAdapter()
         throws ConfigurationException
   {
      return this;
   }

   public EternaFactory getFactory()
   {
      return (EternaFactory) this.factory;
   }

   public void setQueryName(String queryName)
   {
      this.queryName = queryName;
   }

   public String getQueryName()
   {
      return this.queryName;
   }

   public boolean isSpecialCondition()
   {
      return this.specialCondition;
   }

   public void setSpecialCondition(boolean special)
   {
      this.specialCondition = special;
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
      else if (countType != null && countType.startsWith("search:"))
      {
         int index = countType.indexOf(',');
         if (index == -1)
         {
            throw new ConfigurationException("Error count type:[" + countType + "].");
         }
         this.countSearchName = countType.substring(7, index).trim();
         this.countReaderName = countType.substring(index + 1).trim();
      }
      else
      {
         throw new ConfigurationException("Error count type:[" + countType + "].");
      }
   }

   public boolean isNeedWrap()
   {
      return this.needWrap;
   }

   public void setNeedWrap(boolean needWrap)
   {
      this.needWrap = needWrap;
   }

   public int getConditionIndex()
   {
      return this.conditionIndex;
   }

   public void setConditionIndex(int index)
   {
      this.conditionIndex = index;
   }

   public void setColumnSettingType(String type)
   {
      this.columnType = type;
   }

   public void setColumnSetting(ColumnSetting setting)
   {
      this.columnSetting = setting;
   }

   public void setParameterSetting(ParameterSetting setting)
   {
      this.parameterSetting = setting;
   }

   public String getOtherSearchManagerName()
   {
      return this.otherName;
   }

   public void setOtherSearchManagerName(String otherName)
   {
      this.otherName = otherName;
   }

   public String getConditionPropertyOrderWithOther()
   {
      return this.conditionPropertyOrderWithOther;
   }

   public void setConditionPropertyOrderWithOther(String order)
   {
      this.conditionPropertyOrderWithOther = order;
   }

   private PermissionSet[] getPermissionSet(ConditionProperty[] cps)
         throws ConfigurationException
   {
      Set psSet = null;
      for (int i = 0; i < cps.length; i++)
      {
         PermissionSet ps = cps[i].getPermissionSet();
         if (ps != null)
         {
            if (psSet == null)
            {
               psSet = new HashSet();
            }
            psSet.add(ps);
         }
      }
      if (psSet == null)
      {
         this.conditionDocuments = null;
         this.conditionDocumentCount = 1;
         return new PermissionSet[0];
      }
      else
      {
         PermissionSet[] result = new PermissionSet[psSet.size()];
         psSet.toArray(result);
         if (result.length <= conditionDocumentCounts.length)
         {
            this.conditionDocuments = new MemoryChars[conditionDocumentCounts[result.length - 1]];
         }
         else
         {
            this.conditionDocuments = null;
         }
         this.conditionDocumentCount = (int) Math.pow(2, result.length);
         return result;
      }
   }

   private MemoryChars getConditionDocument0(Permission permission)
         throws ConfigurationException
   {
      int cdId = 0;
      int addInt = 1;
      for (int i = 0; i < this.permissionSets.length; i++)
      {
         if (this.permissionSets[i].checkPermission(permission))
         {
            cdId += addInt;
         }
         addInt *= 2;
      }
      if (cdId == this.conditionDocumentCount - 1)
      {
         if (this.conditionDocument == null)
         {
            this.conditionDocument = this.createConditionDocument(permission);
         }
         return this.conditionDocument;
      }
      if (this.conditionDocuments != null)
      {
         if (this.conditionDocuments[cdId] == null)
         {
            this.conditionDocuments[cdId] = this.createConditionDocument(permission);
         }
         return this.conditionDocuments[cdId];
      }
      return this.createConditionDocument(permission);
   }

   private MemoryChars createConditionDocument(Permission permission)
         throws ConfigurationException
   {
      Document document = DocumentHelper.createDocument();
      Element root = document.addElement("eterna");
      Element el_cps = root.addElement("condition-propertys");
      if (this.others != null && this.others.length > 0)
      {
         el_cps.addAttribute("noGroup", "1");
      }
      Element el_cbls = root.addElement("condition-builder-lists");

      this.list2Document(this.getConditionPropertysWithOther(),
            el_cps, el_cbls, permission);
      MemoryChars mcs = new MemoryChars(2, 256);
      XMLWriter writer = new XMLWriter(mcs.getWriter());
      try
      {
         writer.write(document);
         writer.close();
      }
      catch (IOException ex)
      {
         //use MemoryChars, so not IOException
      }
      return mcs;
   }

   public Reader getConditionDocument(Permission permission)
         throws ConfigurationException
   {
      if (this.permissionSets == null)
      {
         synchronized (this)
         {
            if (this.permissionSets == null)
            {
               this.permissionSets = this.getPermissionSet(this.getConditionPropertysWithOther());
            }
         }
      }
      return this.getConditionDocument0(permission).getReader();
   }

   private void list2Document(ConditionProperty[] cps, Element el_conditionPropertys,
         Element el_conditionBuilserLists, Permission permission)
         throws ConfigurationException
   {
      Set addedBuilders = new HashSet();

      for (int i = 0; i < cps.length; i++)
      {
         ConditionProperty cp = cps[i];
         if (checkPermission(cp, permission))
         {
            Element el_cp = el_conditionPropertys.addElement("condition-property");
            el_cp.addAttribute("name", cp.getName());
            el_cp.addAttribute("colId", i + "");
            el_cp.addAttribute("caption", cp.getColumnCaption());
            el_cp.addAttribute("inputType", cp.getConditionInputType());
            el_cp.addAttribute("type",
                  TypeManager.getTypeName(TypeManager.getPureType(cp.getColumnType())));
            el_cp.addAttribute("builderList", cp.getConditionBuilderListName());

            String[] pNames = cp.getAttributeNames();
            if (pNames.length > 0)
            {
               for (int j = 0; j < pNames.length; j++)
               {
                  el_cp.addElement("parameter").addAttribute("name", pNames[j])
                        .addAttribute("value", cp.getAttribute(pNames[j]));
               }
            }

            if (!addedBuilders.contains(cp.getConditionBuilderListName()))
            {
               addedBuilders.add(cp.getConditionBuilderListName());
               Element el_cbl = el_conditionBuilserLists.addElement("builder-list");
               el_cbl.addAttribute("name", cp.getConditionBuilderListName());
               Iterator cbl = cp.getConditionBuilderList().iterator();
               while (cbl.hasNext())
               {
                  ConditionBuilder cb = (ConditionBuilder) cbl.next();
                  el_cbl.addElement("builder").addAttribute("name", cb.getName())
                        .addAttribute("caption", cb.getCaption());
               }
            }
         }
      }
   }

   private boolean checkPermission(ConditionProperty cp, Permission permission)
         throws ConfigurationException
   {
      if (permission == null)
      {
         return true;
      }
      PermissionSet ps = cp.getPermissionSet();
      if (ps == null)
      {
         return true;
      }
      return ps.checkPermission(permission);
   }

   public String getParentConditionPropretyName()
   {
      return this.parentName;
   }

   public void setParentConditionPropretyName(String parentName)
   {
      this.parentName = parentName;
   }

   public String getConditionPropertyOrder()
   {
      return this.conditionPropertyOrder;
   }

   public void setConditionPropertyOrder(String order)
   {
      this.conditionPropertyOrder = order;
   }

   public void clearConditionPropertys() throws ConfigurationException
   {
      this.allConditionProperties = null;
      this.conditionProperties.clear();
   }

   public void addConditionProperty(ConditionProperty cp) throws ConfigurationException
   {
      this.allConditionProperties = null;
      if (this.conditionPropertyMap.containsKey(cp.getName()))
      {
         throw new ConfigurationException(
               "Duplicate [ConditionProperty] name:" + cp.getName() + ".");
      }
      this.conditionProperties.add(cp);
      this.conditionPropertyMap.put(cp.getName(), cp);
   }

   public int getConditionPropertyCount()
         throws ConfigurationException
   {
      return this.getConditionPropertys0().length;
   }

   public ConditionProperty getConditionProperty(int colId)
         throws ConfigurationException
   {
      ConditionProperty[] temp = this.getConditionPropertys0();
      return temp[colId];
   }

   public ConditionProperty getConditionProperty(String name)
         throws ConfigurationException
   {
      this.getConditionPropertys0();
      return (ConditionProperty) this.conditionPropertyMap.get(name);
   }

   public int getPageSize()
         throws ConfigurationException
   {
      if (this.maxPageSize == -1)
      {
         int tempSize = -1;
         Object size = this.getFactory().getAttribute(PAGE_SIZE_ATTRIBUTE);
         if (size != null)
         {
            try
            {
               tempSize = Integer.parseInt((String) size);
            }
            catch (NumberFormatException ex) {}
         }
         this.maxPageSize = tempSize < 1 ? 10 : tempSize;
      }
      return this.maxPageSize;
   }

   public void setPageSize(int pageSize)
   {
      if (pageSize > 0)
      {
         this.maxPageSize = pageSize;
      }
   }

   public String getSearchManagerName()
   {
      return this.searchManagerName;
   }

   public void setSearchManagerName(String name)
   {
      this.searchManagerName = name;
   }

   public SearchManager getSearchManager(AppData data)
         throws ConfigurationException
   {
      SearchManager manager = this.getSearchManager0(data.getSessionAttributeMap());
      manager.setPageNumAndCondition(data, this);
      return manager;
   }

   public synchronized SearchAdapter.Result doSearch(AppData data, Connection conn)
         throws ConfigurationException, SQLException
   {
      if (log.isDebugEnabled())
      {
         log.debug("Start prepare query:" + System.currentTimeMillis());
      }

      Map raMap = data.getRequestAttributeMap();
      BooleanRef isFirst = new BooleanRef();
      SearchManager manager = this.getSearchManager0(data.getSessionAttributeMap());
      QueryAdapter query = this.getQueryAdapter(data, conn, isFirst, manager);
      manager.setPageNumAndCondition(data, this);

      if (query == null)
      {
         log.warn("The search [" + this.getName() + "] can't execute!");
         return null;
      }
      int maxRow = manager.getPageSize(this.getPageSize());
      int pageNum = manager.getPageNum();
      int startRow = pageNum * maxRow;
      if ("1".equals(raMap.get(READ_ALL_ROW)))
      {
         startRow = 0;
         pageNum = -1;
         maxRow = -1;
      }
      else
      {
         Object start_and_count = raMap.get(READ_ROW_START_AND_COUNT);
         if (start_and_count != null & start_and_count instanceof StartAndCount)
         {
            StartAndCount temp = (StartAndCount) start_and_count;
            maxRow = temp.count;
            if (temp.start >= 0)
            {
               startRow = temp.start - 1;
               pageNum = -1;
            }
            else
            {
               startRow = pageNum * maxRow;
            }
         }
      }
      query.setMaxRows(maxRow);
      query.setStartRow(startRow + 1);
      if (this.others != null)
      {
         for (int i = 0; i < this.others.length; i++)
         {
            SearchAdapter other = this.others[i];
            if (other.getConditionIndex() > 0)
            {
               SearchManager om = other.getSearchManager(data);
               if (other.isSpecialCondition())
               {
                  String subConSQL = om.getSpecialConditionPart(other, other.isNeedWrap());
                  PreparerManager spm = om.getSpecialPreparerManager(other);
                  query.setSubSQL(other.getConditionIndex(), subConSQL, spm);
               }
               else
               {
                  query.setSubSQL(other.getConditionIndex(), om.getConditionPart(other.isNeedWrap()),
                        om.getPreparerManager());
               }
            }
         }
      }
      if (this.conditionIndex > 0)
      {
         if (this.specialCondition)
         {
            String subConSQL = manager.getSpecialConditionPart(this, this.needWrap);
            PreparerManager spm = manager.getSpecialPreparerManager(this);
            query.setSubSQL(this.conditionIndex, subConSQL, spm);
         }
         else
         {
            query.setSubSQL(this.conditionIndex, manager.getConditionPart(this.needWrap),
                  manager.getPreparerManager());
         }
      }
      if (this.parameterSetting != null)
      {
         this.parameterSetting.setParameter(query, this, isFirst.value, data, conn);
      }
      String singleOrederName = null;
      boolean singleOrederDesc = false;
      if (query.canOrder())
      {
         String orderStr = data.getRequestParameter(this.getName() + SINGLE_ORDER_SUFIX);
         if (orderStr != null)
         {
            query.setSingleOrder(orderStr);
            BooleanRef tmp = new BooleanRef();
            singleOrederName = query.getSingleOrder(tmp);
            singleOrederDesc = tmp.value;
         }
      }

      if (log.isDebugEnabled())
      {
         log.debug("Search SQL:" + query.getPreparedSQL());
         log.debug("End prepare query:" + System.currentTimeMillis());
      }
      //System.out.println("Search SQL:" + query.getPreparedSQL());
      ResultIterator countRitr = null;
      ResultIterator ritr;
      if ("1".equals(raMap.get(HOLD_CONNECTION)))
      {
         ritr = query.executeQueryHoldConnection(conn);
      }
      else
      {
         if (this.countSearchIndex != -1)
         {
            SearchAdapter tmpSearch = this.getFactory().createSearchAdapter(this.countSearchIndex);
            Object oldObj = raMap.get(READ_ROW_START_AND_COUNT);
            raMap.put(READ_ROW_START_AND_COUNT, new StartAndCount(1, 1));
            countRitr = tmpSearch.doSearch(data, conn).queryResult;
            int count = countRitr.nextRow().getInt(this.countReaderName);
            query.setTotalCount(count);
            countRitr.beforeFirst();
            if (oldObj == null)
            {
               raMap.remove(READ_ROW_START_AND_COUNT);
            }
            else
            {
               raMap.put(READ_ROW_START_AND_COUNT, oldObj);
            }
         }
         else if (this.countType != 0)
         {
            query.setTotalCount(this.countType);
         }
         ritr = query.executeQuery(conn);
      }
      Result result = new Result(this.name, this.queryName, ritr, countRitr, maxRow, pageNum,
            singleOrederName, singleOrederDesc);

      if (log.isDebugEnabled())
      {
         log.debug("End execute query:" + System.currentTimeMillis());
      }

      return result;
   }

   private QueryAdapter getQueryAdapter(AppData data, Connection conn, BooleanRef first,
         SearchManager searchManager)
         throws ConfigurationException
   {
      if (this.queryIndex == -1)
      {
         return null;
      }
      QueryAdapter query = null;
      if (data.getRequestAttributeMap().get(FORCE_LOAD_COLUMN_SETTING) != null)
      {
         query = this.getFactory().createQueryAdapter(this.queryIndex);
         if (this.columnSetting != null)
         {
            String[] colSetting = this.columnSetting.getColumnSetting(
                  this.columnType, query, this, true, data, conn);
            if (colSetting != null)
            {
               ResultReaderManager readerManager = query.getReaderManager();
               readerManager.setReaderList(colSetting);
               query.setReaderManager(readerManager);
            }
         }
         UserManager um = this.getFactory().getUserManager();
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
         first.value = true;
         return query;
      }

      Map queryMap = (Map) SessionCache.getInstance().getProperty(
            data.getSessionAttributeMap(), SESSION_SEARCH_QUERY);
      if (queryMap == null)
      {
         queryMap = new HashMap();
         SessionCache.getInstance().setProperty(data.getSessionAttributeMap(), SESSION_SEARCH_QUERY, queryMap);
      }

      QueryContainer qc = (QueryContainer) queryMap.get(this.sessionQueryTag);
      int qcVersion;
      if (qc == null)
      {
         qcVersion = 0;
      }
      else
      {
         qcVersion = qc.conditionVersion;
      }
      boolean isFirst = searchManager.hasQueryType(data) || searchManager.getConditionVersion() > qcVersion;
      qcVersion = searchManager.getConditionVersion();
      if (isFirst || qc == null)
      {
         isFirst = true;
         query = this.getFactory().createQueryAdapter(this.queryIndex);
         UserManager um = this.getFactory().getUserManager();
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
         queryMap.put(this.sessionQueryTag, new QueryContainer(query, qcVersion));
      }
      else
      {
         query = qc.query;
      }
      if (this.columnSetting != null)
      {
         String[] colSetting = this.columnSetting.getColumnSetting(
               this.columnType, query, this, isFirst, data, conn);
         if (colSetting != null)
         {
            ResultReaderManager readerManager = query.getReaderManager();
            readerManager.setReaderList(colSetting);
            query.setReaderManager(readerManager);
         }
      }
      first.value = isFirst;
      return query;
   }

   private SearchManager getSearchManager0(Map saMap)
         throws ConfigurationException
   {
      Map managerMap = (Map) SessionCache.getInstance().getProperty(saMap, SESSION_SEARCH_MANAGER);
      if (managerMap == null)
      {
         managerMap = new HashMap();
         SessionCache.getInstance().setProperty(saMap, SESSION_SEARCH_MANAGER, managerMap);
      }
      SearchManager manager = (SearchManager) managerMap.get(this.searchManagerName);
      if (manager == null)
      {
         manager = this.getFactory().createSearchManager();
         managerMap.put(this.searchManagerName, manager);
      }
      return manager;
   }

   private ConditionProperty[] getConditionPropertys0()
         throws ConfigurationException
   {
      if (this.allConditionProperties != null)
      {
         return this.allConditionProperties;
      }
      OrderManager om = new OrderManager();
      List resultList = om.getOrder(new MyOrderItem(), this.parents, this.conditionPropertyOrder,
            this.conditionProperties, this.conditionPropertyMap);
      this.allConditionProperties = (ConditionProperty[]) resultList.toArray(new ConditionProperty[0]);
      return this.allConditionProperties;
   }

   private ConditionProperty[] getConditionPropertysWithOther()
         throws ConfigurationException
   {
      if (this.allConditionPropertiesWithOther != null)
      {
         return this.allConditionPropertiesWithOther;
      }
      OrderManager om = new OrderManager("other");
      Map temp = new HashMap(this.conditionPropertyMap);
      List resultList = om.getOrder(new MyOrderItem(), this.others, this.conditionPropertyOrderWithOther,
            this.conditionProperties, temp);
      this.allConditionPropertiesWithOther = (ConditionProperty[]) resultList.toArray(new ConditionProperty[0]);
      return this.allConditionPropertiesWithOther;
   }

   private static class MyOrderItem extends OrderManager.OrderItem
   {
      private ConditionProperty cp;

      public MyOrderItem()
      {
         super("", null);
      }

      protected MyOrderItem(String name, Object obj)
      {
         super(name, obj);
         this.cp = (ConditionProperty) obj;
      }

      public boolean isIgnore()
            throws ConfigurationException
      {
         return this.cp.isIgnore();
      }

      public OrderManager.OrderItem create(Object obj)
            throws ConfigurationException
      {
         if (obj == null)
         {
            return null;
         }
         ConditionProperty cp = (ConditionProperty) obj;
         return new MyOrderItem(cp.getName(), cp);
      }

      public Iterator getOrderItemIterator(Object container)
            throws ConfigurationException
      {
         SearchAdapter search = (SearchAdapter) container;
         return new MyIterator(search);
      }

   }

   private static class MyIterator
         implements Iterator
   {
      private int index = 0;
      private int count;
      SearchAdapter search;

      public MyIterator(SearchAdapter search)
            throws ConfigurationException
      {
         this.search = search;
         this.count = search.getConditionPropertyCount();
      }

      public boolean hasNext()
      {
         return this.index < this.count;
      }

      public Object next()
      {
         try
         {
            return this.search.getConditionProperty(this.index++);
         }
         catch (ConfigurationException ex)
         {
            log.error("Search my iterator next.", ex);
            throw new UnsupportedOperationException(ex.getMessage());
         }
      }

      public void remove()
      {
         throw new UnsupportedOperationException();
      }

   }

   private class QueryContainer
   {
      public final QueryAdapter query;
      public final int conditionVersion;

      public QueryContainer(QueryAdapter query, int conditionVersion)
      {
         this.query = query;
         this.conditionVersion = conditionVersion;
      }

   }

}
