# 属性名的对应关系说明 #
界面中的`table-from`和`table-list`可以通过`baseName`属性来指定引用`query`、`reader-manager`或`search`的相关配置。而节点中的特殊属性也可设置在这些配置的**`attribute`**中。<br>
关于baseName的使用：<br>
<code>query:XXX</code>：表示使用<code>query</code>-><code>readers</code>中的<code>reader</code>列表。<br>
<code>reader:XXX</code>：表示使用<code>reader-manager</code>中的<code>reader</code>列表。<br>
<code>search:XXX</code>：表示使用<code>search</code>-><code>condition-propertys</code>中的<code>condition-property</code>列。<br>
注：<code>search:XXX</code>只能在<b><code>table-form</code></b>中使用<br>
下面这段代码为引用的样例:<br>
<pre><code>&lt;reader-manager name="r"&gt;<br>
   &lt;reader name="col1" caption="test1"/&gt; <br>
   &lt;reader name="col2" caption="test2"/&gt; <br>
&lt;/reader-manager&gt;			<br>
&lt;view name="v"&gt;<br>
   &lt;table-list name="t" baseName="reader:r"&gt;<br>
      &lt;columns&gt;<br>
         &lt;column name="col2"&gt;<br>
            &lt;init-script&gt;if () ...&lt;/init-script&gt;<br>
         &lt;/column&gt;<br>
      &lt;/columns&gt;<br>
   &lt;/table-list&gt; <br>
&lt;/view&gt;<br>
</code></pre>
上面这段代码中“reader:r”表示引用名称为“r”的reader-manager，使用它的<code>reader</code>列表来构造<code>cloumns</code>列表。其中，重写了名称为“col2”的column，添加了<code>init-script</code>子节点。为了简化，<b><code>init-script</code></b>节点可以直接设置到<code>reader</code>的<code>attribute</code>中。<br>
<pre><code>&lt;reader-manager name="r"&gt;<br>
   &lt;reader name="col1" caption="test1"/&gt; <br>
   &lt;reader name="col2" caption="test2"&gt;<br>
      &lt;attribute name="initScript" value="if () ..."/&gt;<br>
   &lt;/reader&gt; <br>
&lt;/reader-manager&gt;			<br>
&lt;view name="v"&gt;<br>
   &lt;table-list name="t" baseName="reader:r"&gt;<br>
   &lt;/table-list&gt; <br>
&lt;/view&gt;<br>
</code></pre>
通过将一些常用的属性定义在被应用的配置中，就无需在界面模块中重复定义，减少代码量。<br>
<br><br>

<h1>attribute名称的对应表</h1>

<table><thead><th>attribute名称</th><th>R</th><th>Q</th><th>S</th><th>table-form</th><th>table-list</th></thead><tbody>
<tr><td>initScript     </td><td>有效</td><td>有效</td><td>有效</td><td>子节点：init-script或属性：initScript</td><td>子节点：init-script或属性：initScript</td></tr>
<tr><td>beforeInit     </td><td>有效</td><td>有效</td><td>有效</td><td>子节点：before-init或属性：beforeInit</td><td>子节点：before-init或属性：beforeInit</td></tr>
<tr><td>initParam      </td><td>有效</td><td>有效</td><td>有效</td><td>子节点：init-param或属性：initParam</td><td>子节点：init-param或属性：initParam</td></tr>
<tr><td>titleParam     </td><td>有效</td><td>有效</td><td>有效</td><td>子节点：title-param或属性：titleParam</td><td>子节点：title-param或属性：titleParam</td></tr>
<tr><td>containerParam </td><td>有效</td><td>有效</td><td>有效</td><td>子节点：component-param或属性：comParam</td><td>子节点：component-param或属性：comParam</td></tr>
<tr><td>cellSize       </td><td>有效</td><td>有效</td><td>有效</td><td>属性：titleSize和containerSize</td><td>无效    </td></tr>
<tr><td>required       </td><td>有效</td><td>有效</td><td>有效</td><td>属性：required</td><td>无效    </td></tr>
<tr><td>newRow         </td><td>有效</td><td>有效</td><td>有效</td><td>属性：newRow</td><td>无效    </td></tr>
<tr><td>dataSrc        </td><td>有效</td><td>有效</td><td>有效</td><td>属性：srcName</td><td>属性：srcName</td></tr>
<tr><td>inputType      </td><td>有效</td><td>有效</td><td>无效</td><td>属性：typicalComponentName</td><td>属性：typicalComponentName</td></tr></tbody></table>

<br>
表中各字段的说明：<br>
attribute名称：可定义在配置中的属性名称。<br>
R：属性定义在<code>query</code>-><code>readers</code>中的<code>reader</code>列表中是否有效。<br>
Q：属性定义在<code>reader-manager</code>中的<code>reader</code>列表中是否有效。<br>
S：属性定义在<code>search</code>-><code>condition-propertys</code>中的<code>condition-property</code>列表中是否有效。<br>
table-form：对应<code>table-form</code>-><code>cells</code>中的<code>cell</code>列表的属性。<br>
table-list：对应<code>table-list</code>-><code>columns</code>中的<code>column</code>列表的属性。<br>
<br>
几个特殊的attribute名称的说明：<br>
cellSize：格式为“titleSize,containerSize”，如“0,2”表示标题占0格内容占2格。<br>
inputType：在<code>search</code>-><code>condition-propertys</code>中的<code>condition-property</code>节点本身就有<code>inputType</code>属性。<br>
<br>
从以上表中，可以看出，<code>cell</code>及<code>column</code>中有些值可以设在子节点中，也可以设置在属性中。但是如果两个都设置了的话，只取属性中设置的值，此时子节点中设置的值是无效的。