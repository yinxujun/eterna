开始此部分之前，请先进行按《[如何配置](http://code.google.com/p/eterna/wiki/config)》进行初始设置。下面所有的代码都写在my.xml中。<br><br><br>

<h1>如何编写<code>HelloWorld</code></h1>
<pre><code>&lt;export name="helloWorld.export" path="{page.view}" viewName="helloWorld.view"/&gt;<br>
&lt;model name="helloWorld" transactionType="notNeed" modelExportName="helloWorld.export"/&gt;<br>
&lt;view name="helloWorld.view"&gt;<br>
   &lt;component name="text" type="center"&gt;<br>
      &lt;component-param&gt;text:"hello world!"&lt;/component-param&gt;<br>
   &lt;/component&gt;<br>
&lt;/view&gt;<br>
</code></pre>
然后我们在地址栏里输入<code>[起始url]</code>/test.do?model=helloWorld，就可以看到效果了。<br>
<br>
<h2>model</h2>
此对象为业务层的入口，其主要属性如下：<br>
<ul><li>name，该model的名称，也是请求此业务的入口名称，必须进行设置。<br>
</li><li>transactionType，事务的类型，notNeed表示不需要数据库连接，默认值为requare，表示加入现有事务，如果当前没有事务，则新建一个。<br>
</li><li>modelExportName，表示业务执行完后跳转的方向，可以不设置，那就不会发生跳转。<br>
</li><li>errorExportName，表示发生异常时跳转的方向，可以不设置。<br>
<br>
<h2>export</h2>
跳转的定义，表示跳转的方向，其主要属性如下：<br>
</li><li>name，跳转的名称，给model中的modelExportName及errorExportName属性使用。<br>
</li><li>path，对应jsp文件或其他页面路径<br>
</li><li>redirect，是否要重定向<br>
</li><li>modelName，转向下一个model的名称<br>
</li><li>viewName，用来展现界面的view的名称<br>
注：如果定义了modelName，则path和redirect就不能定义<br>
<br>
<h2>view</h2>
界面的定义，主要属性就一个：name，给export中的viewName使用。<br>
在view的子节点中，定义界面中的每一个元素。<br>
<br>
<h3>component</h3>
component为view中最常用的子节点，他和html中的标签对应。主要属性如下：<br>
</li><li>name，这个component的标识<br>
</li><li>type，这个component的类型，同html中各个标签的名称。如：div、span、center等。对于input标签，type的格式为：input-[类型]。如："input-text"，"input-checkbox"，"input-submit"等<br>
<br>
component还有以下几个子节点，用于定义其他的属性，"component-param"，"before-init"，"init-script"，"events"。<br>
<br>
<b>component-param</b> 节点中定义的是生成html对象的参数，格式为JSON类型。主要需设置的名称有：<br>
</li><li>attr是html对象的属性，格式为JSON类型，如：rowSpan:"2",id:"test"等。<br>
</li><li>css是html对象的style属下，格式为JSON类型，如："background-color":"red"，display:"none"等。<br>
</li><li>className是html对象的class，格式为字符串<br>
</li><li>objName是html对象的名称属性，格式为字符串<br>
</li><li>objValue是html对象的值属性，格式为字符串<br>
</li><li>text是html对象的内部文本，格式为字符串<br>
</li><li>html是html对象的内部超文本，格式为字符串<br>
在"objName"，"objValue"，"text"，"html"的定义中，可以用"<code>[html]:</code>"标记开始表示内容是html文本，还可以用"<code>[script]:</code>"标记开始表示内容是需要执行的脚本。<br>
<br>
<b>before-init</b> 是在生成html对象前执行的脚本，使用javascript语法，可以在脚本中设置checkResult（boolean类型，默认为true）的值，如果设置为false的话，该html对象就不会输出。<br>
<br>
<b>init-script</b> 是在生成html对象后执行的脚本(包括生成完所有的子对象之后)。使用javascript语法。<br>
<br>
<b>events</b> 是html对象的事件列表，每个事件定义在子节点event中，例子如下：<br>
<pre><code>&lt;events&gt;<br>
  &lt;event name="click"&gt;alert("我被点击了");&lt;/event&gt;<br>
  &lt;event name="dblclick"&gt;alert("我被双击了");&lt;/event&gt;<br>
&lt;/events&gt;<br>
</code></pre>
<br>
<h3>为什么要这样定义界面</h3>
这时，你应该会问这样定义界面有什么好处，写的代码反而比直接写html标签多。<br>
这里的回答是，规范化结构便于处理引擎的解析，也便于自己的属性的设置。如：name和id这两个属性，就会和html中的同名属性相冲突，这里将html的属性移到了component-param子节点的attr中，将html的style属性中的内容到了component-param子节点的ccs中，来避免这些冲突。<br>
另外，规范化的结构也便于自己定义界面对象，使界面对象能够重用，这样节省的工作量远比多写一些代码的量大。<br>
如何定义界面对象，见《<a href='http://code.google.com/p/eterna/wiki/SelfViewObj'>自定义界面对象</a>》。<br>
<br>
ps：界面是通过jQuery来生成的，所以你会发现上面的很多名称有jQuery的影子。