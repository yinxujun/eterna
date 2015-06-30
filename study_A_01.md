# 编写第一段代码 #
第一步配置编译环境<br>
下载<a href='http://code.google.com/p/eterna/downloads/detail?name=etetna-libs.zip'>etetna-libs.zip</a>，将里面的jar文件添加到lib中。<br>
下载eterna-X.X.X.zip，将里面的eterna-X.X.X.jar添加的lib中。<br>
这样编译环境就设置完了。<br>
<br>
第二步新建java文件和xml配置文件，结构如下：<br>
<pre><code>---<br>
 |-Test.java<br>
 |-Text.xml<br>
</code></pre>
xml配置文件和java文件在相同位置，文件名相同即可。<br>
这样就可通过如下代码获取工厂对象的实例。<br>
<pre><code>FactoryManager.Instance instance = FactoryManager.createClassFactoryManager(this.getClass());<br>
</code></pre>
接下来就可以编写配置文件了，配置文件的结构如下：<br>
<pre><code>&lt;?xml version="1.0" encoding="utf-8"?&gt;<br>
&lt;!DOCTYPE eterna-config PUBLIC "eterna" "http://eterna.googlecode.com/files/eterna_1_5.dtd"&gt;<br>
&lt;!-- 上面这段配置指定了dtd文件，如果你的编辑器支持dtd文件的提示的话，那编写起来会方便很多 --&gt;<br>
&lt;eterna-config&gt;<br>
   &lt;factory&gt;<br>
      &lt;objs&gt;<br>
         &lt;!-- 在objs节点中定义需要的对象 --&gt;<br>
      &lt;/objs&gt;<br>
   &lt;/factory&gt;<br>
&lt;/eterna-config&gt;<br>
</code></pre>
数据库中有个表 T_TABLE，数据如下：<br>
<table><thead><th><b>id</b></th><th><b>strValue</b></th><th><b>intValue</b></th><th><b>dateValue</b></th></thead><tbody>
<tr><td>1        </td><td>test01         </td><td>100            </td><td>2012-01-01      </td></tr>
<tr><td>2        </td><td>test02         </td><td>200            </td><td>2012-02-02      </td></tr></tbody></table>

先针对这张表在配置中写一个简单的查询<br>
<pre><code>&lt;query name="get.t_table"&gt;<br>
   &lt;prepared-sql&gt;<br>
      select * from T_TABLE<br>
      where id = ?<br>
   &lt;/prepared-sql&gt;<br>
   &lt;parameters&gt;<br>
      &lt;parameter name="id" type="int"/&gt;<br>
   &lt;/parameters&gt;<br>
   &lt;readers&gt;<br>
      &lt;reader name="id" type="int"/&gt;<br>
      &lt;reader name="strValue" type="String"/&gt;<br>
      &lt;reader name="intValue" type="int"/&gt;<br>
      &lt;reader name="dateValue" type="Date"/&gt;<br>
   &lt;/readers&gt;<br>
&lt;/query&gt;<br>
</code></pre>
查询的配置定义好了，接着我们可以编写执行的代码<br>
<pre><code>Connection conn = ...; // 这里的...为获取数据库链接的代码<br>
EternaFactory factory = instance.getEternaFactory();<br>
// get.t_table 为配置中定义的query的名称<br>
QueryAdapter query = factory.createQueryAdapter("get.t_table");<br>
// 设置查询的条件id<br>
query.setInt("id", 1);<br>
// 执行查询<br>
ResultIterator ritr = query.executeQuery(conn);<br>
if (ritr.hasMoreRow())<br>
{<br>
   ResultRow row = ritr.nextRow();<br>
   System.out.println("strValue:" + row.getString("strValue"));<br>
   System.out.println("intValue:" + row.getInt("intValue"));<br>
   System.out.println("dateValue:" + row.getDate("dateValue"));<br>
}<br>
else<br>
{<br>
   System.out.println("未找到记录！");<br>
}<br>
--------------------------------------------<br>
需要import的类如下<br>
import self.micromagic.eterna.digester.FactoryManager;<br>
import self.micromagic.eterna.share.EternaFactory;<br>
import self.micromagic.eterna.sql.QueryAdapter;<br>
import self.micromagic.eterna.sql.ResultIterator;<br>
import self.micromagic.eterna.sql.ResultRow;<br>
</code></pre>
以上代码中<code>ResultIterator</code>和<code>ResultRow</code>在数据库链接关闭后仍然是可以使用的。<br>
关于获取数据库链接，如果你是按<a href='http://code.google.com/p/eterna/wiki/initDB'>数据库初始化</a>的说明进行的配置，可通过如下代码获得数据库链接<br>
<pre><code>Connection conn = self.micromagic.util.Utility.getDataSource().getConnection();<br>
</code></pre>
如果这样的获取数据库链接的方法不能满足要求，还可根据<a href='http://code.google.com/p/eterna/wiki/getDS'>如何获得数据源</a>中的说明来获取数据库的链接。<br>
接下来我们在配置中写一个更新<br>
<pre><code>&lt;update name="modify.t_table"&gt;<br>
   &lt;prepared-sql&gt;<br>
      update T_TABLE set strValue = ?, intValue = ?, dateValue = ?<br>
      where id = ?<br>
   &lt;/prepared-sql&gt;<br>
   &lt;parameters&gt;<br>
      &lt;parameter name="strValue" type="String"/&gt;<br>
      &lt;parameter name="intValue" type="int"/&gt;<br>
      &lt;parameter name="myName" type="Date"/&gt;<br>
      &lt;parameter name="id" type="int"/&gt;<br>
   &lt;/parameters&gt;<br>
&lt;/update&gt;<br>
</code></pre>
通过上面的配置，我们可以看到参数的绑定是根据SQL语句中“<code>?</code>”出现的顺序一一绑定的，参数配置里name属性只是个别名，在设置参数的时候使用。<br>
执行这个更新的代码如下：<br>
<pre><code>// modify.t_table 为配置中定义的update的名称<br>
UpdateAdapter update = factory.createUpdateAdapter("modify.t_table");<br>
// 设置需要修改的数据<br>
update.setInt("id", 2);<br>
update.setString("strValue", "modify02");  <br>
update.setInt("intValue", 2000);  <br>
// 因为配置中定义的这个参数名为myName<br>
update.setDate("myName", new java.sql.Date(System.currentTimeMillis()));<br>
// 执行更新<br>
int modifiedCount = update.executeUpdate(conn);<br>
System.out.println("更新记录数:" + modifiedCount); <br>
--------------------------------------------<br>
还需要import的类如下<br>
import self.micromagic.eterna.sql.UpdateAdapter;<br>
</code></pre>
<br>
<h1>可以写得简单些吗</h1>
当然能够进行裁剪，我们可以先对查询进行调整，可以把查询需要读取的内容单独定义出来，这样如果需要在多个地方使用就不用重复写了。<br>
<pre><code>&lt;reader-manager name="t_table.readers"&gt;<br>
   &lt;reader name="id" type="int"/&gt;<br>
   &lt;reader name="strValue" type="String"/&gt;<br>
   &lt;reader name="intValue" type="int"/&gt;<br>
   &lt;reader name="dateValue" type="Date"/&gt;<br>
&lt;/reader-manager&gt;<br>
&lt;query name="get.t_table"&gt;<br>
   &lt;prepared-sql&gt;<br>
      select * from T_TABLE<br>
      where id = ?<br>
   &lt;/prepared-sql&gt;<br>
   &lt;parameters&gt;<br>
      &lt;parameter name="id" type="int"/&gt;<br>
   &lt;/parameters&gt;<br>
   &lt;readers baseReaderManager="t_table.readers"/&gt;<br>
&lt;/query&gt;<br>
</code></pre>
这里定义了一个<code>reader-manager</code>“<code>t_table.readers</code>”，在<code>query</code>中通过<code>baseReaderManager</code>属性来指定使用已定义好的<code>reader-manager</code>。<br>
同样，也可以把更新中使用的参数列表单独定义出来<br>
<pre><code>&lt;parameter-group name="t_table.param"&gt;   <br>
   &lt;parameter name="strValue" type="String"/&gt;<br>
   &lt;parameter name="intValue" type="int"/&gt;<br>
   &lt;parameter name="dateValue" type="Date"/&gt;<br>
   &lt;parameter name="id" type="int"/&gt;<br>
&lt;/parameter-group&gt;<br>
&lt;update name="modify.t_table"&gt;<br>
   &lt;prepared-sql&gt;<br>
      update T_TABLE set strValue = ?, intValue = ?, dateValue = ?<br>
      where id = ?<br>
   &lt;/prepared-sql&gt;<br>
   &lt;parameters&gt;<br>
      &lt;parameter-ref groupName="t_table.param"/&gt;<br>
   &lt;/parameters&gt;<br>
&lt;/update&gt;<br>
</code></pre>
这里定义了一个<code>parameter-group</code>“<code>t_table.param</code>”，在<code>update</code>中通过<code>parameter-ref</code>节点的属性<code>groupName</code>来指定使用已定义好的<code>parameter-group</code>。<br>
这样似乎没简化多少，<code>parameter-group</code>和<code>reader-manager</code>里的内容似乎有些重复，所以这两个内容可以合成一个，就像这样<br>
<pre><code>&lt;update name="modify.t_table"&gt;<br>
   &lt;prepared-sql&gt;<br>
      update T_TABLE set strValue = ?, intValue = ?, dateValue = ?<br>
      where id = ?<br>
   &lt;/prepared-sql&gt;<br>
   &lt;parameters&gt;<br>
      &lt;parameter-ref groupName="reader:t_table.readers"/&gt;<br>
   &lt;/parameters&gt;<br>
&lt;/update&gt;<br>
</code></pre>
通过<code>reader:</code>前缀表示将使用一个<code>reader-manager</code>来作为参数列表，不过这样似乎还有个问题，前面说过*参数的绑定是根据SQL语句中“<code>?</code>”出现的顺序一一绑定的<b>，这个<code>reader-manager</code>中的顺序是id, strValue, intValue, dateValue，和SQL语句的顺序不一致。这没关系，可以做如下修改<br>
<pre><code>&lt;update name="modify.t_table"&gt;<br>
   &lt;prepared-sql&gt;<br>
      update T_TABLE set strValue = ?, intValue = ?, dateValue = ?<br>
      where id = ?<br>
   &lt;/prepared-sql&gt;<br>
   &lt;parameters&gt;<br>
      &lt;parameter-ref groupName="reader:t_table.readers" ignoreList="$ignoreSame"/&gt;<br>
      &lt;parameter name="id" type="int"/&gt;<br>
   &lt;/parameters&gt;<br>
&lt;/update&gt;<br>
</code></pre>
在<code>parameter-ref</code>节点中增加一个属性“ignoreList="$ignoreSame"”，表示忽略已出现的同名参数，在这里已经定义了<code>id</code>这个参数了，那么<code>t_table.readers</code>中出现在第一个的'id'参数就会被忽略，参数列表中的顺序就和SQL语句中的一致了。</b><br>
对于这个更新语句，如果列比较多的话，即要写参数列表，又要写SQL语句，这个量也不小。其实语句是可以部分生成的，看下面的修改<br>
<pre><code>&lt;update name="modify.t_table"&gt;<br>
   &lt;prepared-sql&gt;<br>
      update T_TABLE set #auto[update,1,-2]<br>
      where id = ?<br>
   &lt;/prepared-sql&gt;<br>
   &lt;parameters&gt;<br>
      &lt;parameter-ref groupName="reader:t_table.readers" ignoreList="$ignoreSame"/&gt;<br>
      &lt;parameter name="id" type="int"/&gt;<br>
   &lt;/parameters&gt;<br>
&lt;/update&gt;<br>
</code></pre>
这段配置中，将列赋值的部分变成了一个表达式<code>#auto[update,1,-2]</code>，表示根据参数列表中的第一个参数至倒数第二个参数并根据<code>update</code>的格式来生成语句，其生成的语句就是“<code>strValue = ?, intValue = ?, dateValue = ?</code>”。关于动态语句生成的语法可详见<a href='http://code.google.com/p/eterna/wiki/prepared_sql?ts=1337004188&updated=prepared_sql#auto_动态语句生成'>预备SQL语句的特殊语法</a>。<br>
这样简化后的配置就是如下效果<br>
<pre><code>&lt;reader-manager name="t_table.readers"&gt;<br>
   &lt;reader name="id" type="int"/&gt;<br>
   &lt;reader name="strValue" type="String"/&gt;<br>
   &lt;reader name="intValue" type="int"/&gt;<br>
   &lt;reader name="dateValue" type="Date"/&gt;<br>
&lt;/reader-manager&gt;<br>
<br>
&lt;query name="get.t_table"&gt;<br>
   &lt;prepared-sql&gt;<br>
      select * from T_TABLE<br>
      where id = ?<br>
   &lt;/prepared-sql&gt;<br>
   &lt;parameters&gt;<br>
      &lt;parameter name="id" type="int"/&gt;<br>
   &lt;/parameters&gt;<br>
   &lt;readers baseReaderManager="t_table.readers"/&gt;<br>
&lt;/query&gt;<br>
<br>
&lt;update name="modify.t_table"&gt;<br>
   &lt;prepared-sql&gt;<br>
      update T_TABLE set #auto[update,1,-2]<br>
      where id = ?<br>
   &lt;/prepared-sql&gt;<br>
   &lt;parameters&gt;<br>
      &lt;parameter-ref groupName="reader:t_table.readers" ignoreList="$ignoreSame"/&gt;<br>
      &lt;parameter name="id" type="int"/&gt;<br>
   &lt;/parameters&gt;<br>
&lt;/update&gt;<br>
</code></pre>
配置简化好了，如果参数比较多的话，一个个设置也是比较费工作的，所以代码中设置参数的部分也可以简化<br>
<pre><code>// modify.t_table 为配置中定义的update的名称<br>
UpdateAdapter update = factory.createUpdateAdapter("modify.t_table");<br>
ParamSetManager psm = new ParamSetManager(update);<br>
// request为javax.servlet.http.HttpServletRequest对象<br>
// 此段代码会根据参数列表到request中寻找同名的参数自动进行设置      <br>
psm.setParams(request.getParameterMap());   <br>
// 执行更新<br>
int modifiedCount = update.executeUpdate(conn);<br>
System.out.println("更新记录数:" + modifiedCount);<br>
--------------------------------------------<br>
还需要import的类如下<br>
import self.micromagic.eterna.model.ParamSetManager;<br>
</code></pre>
这里通过<code>ParamSetManager</code>进行参数的自动设置，这样就无需一行一行的写设置参数的代码了。<br>
<br><br><br>
<a href='http://code.google.com/p/eterna/wiki/study_A_02'>下一篇_搜索</a>