# 两次查询的搜索 #
实现类为：`self.micromagic.eterna.search.DoubleQuerySearch`。<br>
其执行过程为：第一次查询，取出所有的主键；第二次查询，根据前一次查询结果中的主键值，取出正式的结果。<br>
在查询数据量非常大，且需要对某几个字段进行排序时，或者查询结果中存在数据量较大的字段时，使用这种方式可以大大加快查询的效率。当然，需要排序的话，要先对主键及排序字段建索引。<br>
<br>
需要在search节点中设置的attribute值如下：<br>
<table><thead><th>名称</th><th>必须</th><th>说明</th></thead><tbody>
<tr><td>nextQueryName</td><td>是   </td><td>第二次查询的query名称</td></tr>
<tr><td>keyConditionIndex</td><td>否   </td><td>上面这个query中设置key条件的索引值，默认值为1</td></tr>
<tr><td>keyNameList</td><td>是   </td><td>主键名称列表, 格式为  name1,name2,... 或 name1:col1,name2,...</td></tr>
<tr><td>assistSearchName</td><td>否   </td><td>辅助的search名称，用于设置其他的条件，可使用$same表示使用本search</td></tr>
<tr><td>needAssistCondition</td><td>否   </td><td>是否需要设置辅助search的条件，当assistSearchName为$same时默认值为false<br>当assistSearchName为其它search的名称时默认值为true</td></tr></tbody></table>


<h1>样例</h1>
假设有两个表，一个是人员信息表，id是此表的主键，userInfo是个大数据字段；一个是工作情况表，id是外键，id+createDate是主键，workInfo是个大数据字段。现在要做一个查询，查出所有人的工作情况，如果没有工作情况，仅列出人员信息。<br>
表数据如下：<br>
sample_a，人员信息<br>
<table><thead><th>id</th><th>name</th><th>userInfo</th></thead><tbody>
<tr><td>1 </td><td>测试1</td><td>测试1的用户信息</td></tr>
<tr><td>2 </td><td>测试2</td><td>测试2的用户信息</td></tr>
<tr><td>3 </td><td>其他1</td><td>其他1的用户信息</td></tr>
<tr><td>4 </td><td>其他2</td><td>其他2的用户信息</td></tr>
<tr><td>5 </td><td>测试3</td><td>测试3的用户信息</td></tr>
<tr><td>6 </td><td>其他3</td><td>其他3的用户信息</td></tr></tbody></table>

sample_b，工作情况<br>
<table><thead><th>id</th><th>createDate</th><th>workInfo</th></thead><tbody>
<tr><td>1 </td><td>2012-8-9  </td><td>测试1 2012年8月9日的工作日志</td></tr>
<tr><td>2 </td><td>2012-8-9  </td><td>测试2 2012年8月9日的工作日志</td></tr>
<tr><td>1 </td><td>2012-8-10 </td><td>测试1 2012年8月10日的工作日志</td></tr></tbody></table>

<br>
注，以上业务场景及相关数据纯属虚构，如有雷同，不甚荣幸。<br>
<br>
代码样例：<br>
<pre><code>&lt;query name="a.query"&gt;<br>
   &lt;prepared-sql&gt;<br>
      select a.id, b.createDate<br>
      from sample_a a left join sample_b b on a.id = b.id<br>
      #sub[where $]<br>
   &lt;/prepared-sql&gt;<br>
   &lt;readers&gt;<br>
      &lt;reader name="id" type="int"/&gt;<br>
      &lt;reader name="createDate" type="Date"/&gt;<br>
   &lt;/readers&gt;<br>
&lt;/query&gt;<br>
&lt;search name="sample.search" queryName="a.query" pageSize="5"<br>
      generator="self.micromagic.eterna.search.DoubleQuerySearch"&gt;<br>
   &lt;condition-propertys&gt;<br>
      &lt;condition-property name="name" colType="String"/&gt;<br>
   &lt;/condition-propertys&gt;<br>
   &lt;attribute name="nextQueryName" value="b.query"/&gt;<br>
   &lt;attribute name="keyNameList" value="id:a.id,createDate"/&gt;<br>
&lt;/search&gt;<br>
&lt;query name="b.query"&gt;<br>
   &lt;prepared-sql&gt;<br>
      select a.id, a.name, a.userInfo, b.createDate, b.workInfo<br>
      from sample_a a left join sample_b b on a.id = b.id<br>
      #sub[where $]<br>
   &lt;/prepared-sql&gt;<br>
   &lt;readers&gt;<br>
      &lt;reader name="id" type="int"/&gt;<br>
      &lt;reader name="name" type="String"/&gt;<br>
      &lt;reader name="userInfo" type="String"/&gt;<br>
      &lt;reader name="createDate" type="Date"/&gt;<br>
      &lt;reader name="workInfo" type="String"/&gt;<br>
   &lt;/readers&gt;<br>
&lt;/query&gt;<br>
</code></pre>
执行结果，传入的name条件值为“测试”：<br>
<pre><code>&lt;query name="a.query"&gt;<br>
   &lt;prepared-sql&gt;<br>
      select a.id, b.createDate<br>
      from sample_a a left join sample_b b on a.id = b.id<br>
      where (name LIKE ?)<br>
   &lt;/prepared-sql&gt;<br>
   &lt;parameters&gt;<br>
      &lt;parameter name="name" index="1" type="String"&gt;%测试%&lt;/parameter&gt;<br>
   &lt;/parameters&gt;<br>
&lt;/query&gt;<br>
&lt;result type="ResultIterator" rowCount="4"&gt;<br>
   &lt;value index="1" type="ResultRow" columnCount="2"&gt;<br>
      &lt;value columnName="id" type="int" value="1"/&gt;<br>
      &lt;value columnName="createDate" type="Date" value="2012-08-09"/&gt;<br>
   &lt;/value&gt;<br>
   &lt;value index="2" type="ResultRow" columnCount="2"&gt;<br>
      &lt;value columnName="id" type="int" value="2"/&gt;<br>
      &lt;value columnName="createDate" type="Date" value="2012-08-09"/&gt;<br>
   &lt;/value&gt;<br>
   &lt;value index="3" type="ResultRow" columnCount="2"&gt;<br>
      &lt;value columnName="id" type="int" value="1"/&gt;<br>
      &lt;value columnName="createDate" type="Date" value="2012-08-10"/&gt;<br>
   &lt;/value&gt;<br>
   &lt;value index="4" type="ResultRow" columnCount="2"&gt;<br>
      &lt;value columnName="id" type="int" value="5"/&gt;<br>
      &lt;value columnName="createDate" type="Date" value=""/&gt;<br>
   &lt;/value&gt;<br>
&lt;/result&gt;<br>
&lt;query name="b.query"&gt;<br>
   &lt;prepared-sql&gt;<br>
      select a.id, a.name, a.userInfo, b.createDate, b.workInfo<br>
      from sample_a a left join sample_b b on a.id = b.id<br>
      where ((a.id = ? AND createDate = ?) OR (a.id = ? AND createDate = ?)<br>
            OR (a.id = ? AND createDate = ?) OR (a.id = ? AND createDate is null))<br>
   &lt;/prepared-sql&gt;<br>
   &lt;parameters&gt;<br>
      &lt;parameter index="1" type="int"&gt;1&lt;/parameter&gt;<br>
      &lt;parameter index="2" type="Date"&gt;2012-08-09&lt;/parameter&gt;<br>
      &lt;parameter index="3" type="int"&gt;2&lt;/parameter&gt;<br>
      &lt;parameter index="4" type="Date"&gt;2012-08-09&lt;/parameter&gt;<br>
      &lt;parameter index="5" type="int"&gt;1&lt;/parameter&gt;<br>
      &lt;parameter index="6" type="Date"&gt;2012-08-10&lt;/parameter&gt;<br>
      &lt;parameter index="7" type="int"&gt;5&lt;/parameter&gt;<br>
   &lt;/parameters&gt;<br>
&lt;/query&gt;<br>
&lt;result type="ResultIterator" rowCount="4"&gt;<br>
   &lt;value index="1" type="ResultRow" columnCount="5"&gt;<br>
      &lt;value columnName="id" type="int" value="1"/&gt;<br>
      &lt;value columnName="name" type="String" value="测试1"/&gt;<br>
      &lt;value columnName="userInfo" type="String" value="测试1的用户信息"/&gt;<br>
      &lt;value columnName="createDate" type="Date" value="2012-08-09"/&gt;<br>
      &lt;value columnName="workInfo" type="String" value="测试1 2012年8月9日的工作日志"/&gt;<br>
   &lt;/value&gt;<br>
   &lt;value index="2" type="ResultRow" columnCount="5"&gt;<br>
      &lt;value columnName="id" type="int" value="2"/&gt;<br>
      &lt;value columnName="name" type="String" value="测试2"/&gt;<br>
      &lt;value columnName="userInfo" type="String" value="测试2的用户信息"/&gt;<br>
      &lt;value columnName="createDate" type="Date" value="2012-08-09"/&gt;<br>
      &lt;value columnName="workInfo" type="String" value="测试2 2012年8月9日的工作日志"/&gt;<br>
   &lt;/value&gt;<br>
   &lt;value index="3" type="ResultRow" columnCount="5"&gt;<br>
      &lt;value columnName="id" type="int" value="1"/&gt;<br>
      &lt;value columnName="name" type="String" value="测试1"/&gt;<br>
      &lt;value columnName="userInfo" type="String" value="测试1的用户信息"/&gt;<br>
      &lt;value columnName="createDate" type="Date" value="2012-08-10"/&gt;<br>
      &lt;value columnName="workInfo" type="String" value="测试1 2012年8月10日的工作日志"/&gt;<br>
   &lt;/value&gt;<br>
   &lt;value index="4" type="ResultRow" columnCount="5"&gt;<br>
      &lt;value columnName="id" type="int" value="5"/&gt;<br>
      &lt;value columnName="name" type="String" value="测试3"/&gt;<br>
      &lt;value columnName="userInfo" type="String" value="测试3的用户信息"/&gt;<br>
      &lt;value columnName="createDate" type="Date" value=""/&gt;<br>
      &lt;value columnName="workInfo" type="String" value=""/&gt;<br>
   &lt;/value&gt;<br>
&lt;/result&gt;<br>
</code></pre>