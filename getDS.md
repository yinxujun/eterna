# 1. 配置文件 #
在`micromagic_config.properties`文件中配置数据库的链接，配置方式如下：
```
dataSource.autoCommit=[是否自动提交]
dataSource.description=[数据源的说明]
dataSource.driverClass=[数据库的驱动类]
dataSource.maxCount=[最大连接数]
dataSource.url=[数据库连接字符串]
dataSource.user=[数据库用户名]
dataSource.password=[数据库密码]
```
通过这样的配置，可通过如下方式获取数据源：
```
self.micromagic.util.Utility.getDataSource();
```
<br>
<h1>2. 通过<code>data-source-manager</code>配置</h1>
此对象定义在eterna-config/factory节点下，可定义通过jndi方式获取的数据源<br>
<pre><code>&lt;data-source-manager defaultName="testDS"&gt;<br>
   &lt;data-source&gt;<br>
      testDS=jdbc/ds01;<br>
      otherDS=jdbc/ds02;<br>
   &lt;/data-source&gt;<br>
&lt;/data-source-manager&gt;<br>
</code></pre>
上面代码中，定义了两个数据源，<code>data-source-manager</code>的<code>defaultName</code>属性用于指定默认使用的数据源，<code>data-source</code>节点内配置数据源的名称对应的jndi，格式为<code>数据源名称1=JNDI1;[数据源名称2=JNDI2;...]</code>，系统中就可通过“数据源名称”获得对应的数据源。<br>
获取的代码如下：<br>
<pre><code>FactoryManager.Instance instance = FactoryManager.createClassFactoryManager(this.getClass());<br>
// 获得默认的数据源<br>
instance.getEternaFactory().getDataSourceManager().getDefaultDataSource();<br>
// 根据数据源名称获得对应的数据源<br>
instance.getEternaFactory().getDataSourceManager().getDataSource("otherDS");<br>
</code></pre>
对于一些特定的应用服务器，需要通过<code>data-source</code>节点的<code>java.naming.factory.initial</code>属性来指定jndi工厂<br>
<pre><code>// WebSphere<br>
&lt;data-source-manager defaultName="testDS"&gt;<br>
   &lt;data-source java.naming.factory.initial="com.ibm.websphere.naming.WsnInitialContextFactory"&gt;<br>
      ...<br>
   &lt;/data-source&gt;<br>
&lt;/data-source-manager&gt;<br>
// weblogic<br>
&lt;data-source-manager defaultName="testDS"&gt;<br>
   &lt;data-source java.naming.factory.initial="weblogic.jndi.WLInitialContextFactory"&gt;<br>
      ...<br>
   &lt;/data-source&gt;<br>
&lt;/data-source-manager&gt;<br>
</code></pre>
如果不是通过jndi方式获取的数据源，那还可以用下面一种方式。<br>
<br>
<h1>3. 通过初始化变量配置</h1>
初始化前，将相关的数据源对象设置好<br>
<pre><code>Map initMap = new HashMap();<br>
Map dsMap = new HashMap();<br>
// ds1和ds2为通过其他方式获得的数据源<br>
dsMap.put("testDS", ds1);<br>
dsMap.put("otherDS", ds2);<br>
initMap.put(ModelCaller.DATA_SOURCE_MAP, dsMap);<br>
initMap.put(ModelCaller.DEFAULT_DATA_SOURCE_NAME, "testDS");<br>
FactoryManager.setInitCache(initMap);<br>
</code></pre>
此段代码要在eterna初始化之前执行，然后就可以像第二种方法一样获得相应的数据源了。<br>
不过有一点需要注意，如果已经按第二种方法配置了数据源，那这样的配置就没有效果了。