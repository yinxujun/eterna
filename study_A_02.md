# 定义一个搜索 search #
search的功能是自动设置查询条件及处理分页，下面就是定义一个search的例子
```
<search name="search.t_table" queryName="query.t_table" conditionIndex="1" pageSize="10">
   <condition-propertys>
      <condition-property name="strValue" colType="String"/>
      <condition-property name="intValue" colType="int"/>
      <condition-property name="dateValue" colType="Date"/>
   </condition-propertys>
</search>
```
上面的例子中`queryName`属性指定使用哪个`query`来执行查询，`conditionIndex`属性指定查询中设置条件的子句的索引值（默认值为：1），`pageSize`属性指定每页显示的记录数（默认值为：10）。<br>
<code>condition-propertys</code>子节点中用于定义搜索的条件，里面的每个<code>condition-property</code>节点代表一个条件。<br>
下面来看下对应的<code>query</code>需要怎么定义<br>
<pre><code>&lt;query name="query.t_table"&gt;<br>
   &lt;prepared-sql&gt;<br>
      select * from T_TABLE<br>
      #sub[where $]<br>
   &lt;/prepared-sql&gt;<br>
   &lt;readers&gt;<br>
      &lt;reader name="id" type="int"/&gt;<br>
      &lt;reader name="strValue" type="String"/&gt;<br>
      &lt;reader name="intValue" type="int"/&gt;<br>
      &lt;reader name="dateValue" type="Date"/&gt;<br>
   &lt;/readers&gt;<br>
&lt;/query&gt;<br>
</code></pre>
这个<code>query</code>中定义了一个子句，用于给search设置查询的条件。<br>
<br>
搜索定义完了，下面来执行它<br>
<pre><code>// search.t_table 为配置中定义的search的名称<br>
SearchAdapter search = factory.createSearchAdapter("search.t_table");<br>
// request为javax.servlet.http.HttpServletRequest对象<br>
SearchAdapter.Result result = search.doSearch(WebApp.appTool.getAppData(request), conn);<br>
System.out.println("查询结果:" + result.queryResult); // 这是个ResultIterator<br>
System.out.println("当前页数:" + result.pageNum);  // 第一页为"0", 第二页为"1", ...<br>
--------------------------------------------<br>
还需要import的类如下<br>
import self.micromagic.eterna.search.SearchAdapter;  <br>
import self.micromagic.app.WebApp;   <br>
</code></pre>
<br>
<h1>传入的参数对搜索的控制</h1>
下面为页面中可传入的控制参数的说明<br>
<pre><code>pageNum：用于控制显示第几页，第一页为"0"、第二页为"1"、...，如果没有传入此参数，则保持原来的页数<br>
pageSize：重新设置每页显示的记录数，如果没有传入此参数，则维持原来的值<br>
queryType：会根据不同的值来对搜索的条件进行处理<br>
   set：从参数中获取条件并进行设置<br>
   clear：清空已设置的搜索条件<br>
   不传值：维持原来的搜索条件不变<br>
</code></pre>
关于搜索的条件，就是前面<code>condition-propertys</code>子节点中配置的内容，每个<code>condition-property</code>节点的<code>name</code>属性就是需要传入的参数的名称。<br>
如：<br>
strValue=abc，就是查找strValue列中，包含abc的记录。<br>
intValue=1，就是查找intValue列的值等于1的记录。<br>
<br><br><br>
<a href='http://code.google.com/p/eterna/wiki/study_A_01'>上一篇_配置和代码编写</a>