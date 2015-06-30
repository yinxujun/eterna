# 如何定义自己的界面对象 #

```
<typical-component name="boolean_component" type="div">
   <before-init>if (eg_temp.name == null) eg_temp.name = "yn";</before-init>
   <component name="yes" type="input-radio" comParam="objValue:'1'"/>
   <component name="yes_lable" type="span" comParam="text:'是'"/>
   <component name="no" type="input-radio" comParam="objValue:'0'"/>
   <component name="no_lable" type="span" comParam="text:'否'"/>
</typical-component>
```
以上代码我们定义了一个选择“是”和“否”的单选框，这样我们在需要的地方，用 **replacement** 标签就可以使用它。具体代码如下：
```
<replacement name="yn" baseComponentName="boolean_component"/>
```
<br>

<h2>replacement</h2>

引用已定义的一个已定义的界面对象，主要属性如下：<br>
<ul><li>name，这个replacement的标识<br>
</li><li>baseComponentName，引用的界面对象的名称</li></ul>

其他的定义同<a href='http://code.google.com/p/eterna/wiki/HelloWorld#component'>component</a>。<br>
<br>

<h2>typical-component</h2>

一个可以被引用的自定义对象，他是直接定义在objs节点下，而不是定义在view节点下。<br>
其他的属性定义同<a href='http://code.google.com/p/eterna/wiki/HelloWorld#component'>component</a>。<br>
<br>
<br>

<h1>完善界面对象的定义</h1>
如果要使自己定义的界面对象能在较多的地方使用，有许多情况需要考虑，比如：控件的名称、控件的值等。如果是通过参数传递，那在使用时需要传递很多参数，这里使用环境变量来处理。<br>
<br>
<h2>eg_temp</h2>
这是最重要的一个环境变量，你可以通过它知道当前要取哪个数据集、哪个数据列、哪个数据行，当前的控件应设为什么名称，还需要设置哪些属性等。<br>
此属性可以在"init-script"、"before-init"、"event"中直接使用。<br>
下面介绍两个常用的属性：<br>
<ul><li>name，当前控件应设置的名称，框架对input、select等控件的名称是这么处理的，先判断objName有没有设置，然后在判断有没有eg_temp.name。<br>
</li><li>param，此属性一般是json对象，用于配置需要设置的控件属性。<br>
</li><li>dataName：指目前上下文环境中的数据对象的名称，如：{$dataV:eg_temp.dataName}。<br>
</li><li>srcName：指目前上下文环境中的数据对象的源数据名称，如：{$dataV:eg_temp.dataName}[eg_temp.srcName]。<br>
</li><li>rowNum：指目前上下文环境中所在表格的行数索引。<br>
</li><li>columnCount：指目前上下文环境中所在表格的列数。<br>
</li><li>index：指目前上下文环境中的数据对象所在的索引值，如：{$ef:getData_value}(eg_temp.dataName, eg_temp.srcName, eg_temp.index)。<br>
</li><li>valueObj：目前上下文环境中需要使用的value对象，里面有exists，value，dataName，srcName等属性。<br>
</li><li>rowType：生成表格时标识当前行的类型，如 beforeTable，afterTable，row，title，beforeTitle，afterTitle，beforeRow，afterRow。<br>
<br>
<h2>webObj</h2>
这是另一个重要的环境变量，是当前控件的jQuery对象，如：jQuery(this)。<br>
此属性可以在"init-script"、"event"中直接使用。<br>
<br>
<h2>开始完善</h2>
<pre><code>&lt;typical-component name="boolean_component" type="div"&gt;<br>
   &lt;before-init&gt;if (eg_temp.name == null) eg_temp.name = "yn";&lt;/before-init&gt;<br>
   &lt;component name="yes" type="input-radio" comParam="objValue:'1'"&gt;<br>
      &lt;init-script&gt;&lt;![CDATA[<br>
         var param = eg_temp.param;<br>
         if (param != null)<br>
         {<br>
            if (param.yesValue != null)<br>
            {<br>
               webObj.val(param.yesValue);<br>
            }<br>
            if (param.checkedValue != null &amp;&amp; webObj.val() == param.checkedValue)<br>
            {<br>
               webObj.prop("checked", true);<br>
            }<br>
         }<br>
      ]]&gt;&lt;/init-script&gt;<br>
   &lt;/component&gt;<br>
   &lt;component name="yes_lable" type="span" comParam="text:'是'"&gt;<br>
      &lt;init-script&gt;&lt;![CDATA[<br>
         var param = eg_temp.param;<br>
         if (param != null &amp;&amp; param.yesLabel != null)<br>
         {<br>
            webObj.text(param.yesLabel);<br>
         }<br>
      ]]&gt;&lt;/init-script&gt;<br>
   &lt;/component&gt;<br>
   &lt;component name="no" type="input-radio" comParam="objValue:'0'"&gt;<br>
      &lt;init-script&gt;&lt;![CDATA[<br>
         var param = eg_temp.param;<br>
         if (param != null)<br>
         {<br>
            if (param.noValue != null)<br>
            {<br>
               webObj.val(param.noValue);<br>
            }<br>
            if (param.checkedValue != null &amp;&amp; webObj.val() == param.checkedValue)<br>
            {<br>
               webObj.prop("checked", true);<br>
            }<br>
         }<br>
      ]]&gt;&lt;/init-script&gt;<br>
   &lt;/component&gt;<br>
   &lt;component name="no_lable" type="span" comParam="text:'否'"&gt;<br>
      &lt;init-script&gt;&lt;![CDATA[<br>
         var param = eg_temp.param;<br>
         if (param != null &amp;&amp; param.noLabel != null)<br>
         {<br>
            webObj.text(param.noLabel);<br>
         }<br>
      ]]&gt;&lt;/init-script&gt;<br>
   &lt;/component&gt;<br>
&lt;/typical-component&gt;<br>
</code></pre>
以上代码，“是”和“否”的默认值为“1”和“0”，控件的默认名称为：“yn”，可以通过设置：eg_temp.name、eg_temp.param下的yesValue、noValue、yesLable、noLable、checkedValue这些值来控制单选框的值，以及后面显示的标签，选中的单选框。<br>
<br>
也许你会认为这样定义一个界面对象也太麻烦了，但使用时就能省很多工作，不是吗？<br>
例子中eterna_globe.xml文件里已经定义了一些常用的界面对象。<br>
<br>
<h2>子节点替换</h2>
如果只定义的界面对象只能引用，那功能还是有限的，更强的功能是子节点替换。<br>
何为子节点替换？就是在引用自定义的界面对象时，你可以换掉其中的任意子节点，来满足特殊的效果。<br>
<pre><code>&lt;typical-component name="sample" type="div"&gt;<br>
   &lt;component-param&gt;css:{position:'relative',border:'4px #b9002c solid',padding:'24px'}&lt;/component-param&gt;<br>
   &lt;component name='lt' type="span"&gt;<br>
      &lt;component-param&gt;css:{position:'absolute',width:'23px',height:'23px','background-image':'url(border.gif)',top:'-4px',left:'-4px','background-position':'left top'}&lt;/component-param&gt;<br>
   &lt;/component&gt;<br>
   &lt;component name='lb' type="span"&gt;<br>
      &lt;component-param&gt;css:{position:'absolute',width:'23px',height:'23px','background-image':'url(border.gif)',bottom:'-5px',left:'-4px','background-position':'left bottom'}&lt;/component-param&gt;<br>
   &lt;/component&gt;<br>
   &lt;component name='rt' type="span"&gt;<br>
      &lt;component-param&gt;css:{position:'absolute',width:'23px',height:'23px','background-image':'url(border.gif)',top:'-4px',right:'-4px','background-position':'right top'}&lt;/component-param&gt;<br>
   &lt;/component&gt;<br>
   &lt;component name='rb' type="span"&gt;<br>
      &lt;component-param&gt;css:{position:'absolute',width:'23px',height:'23px','background-image':'url(border.gif)',bottom:'-5px',right:'-4px','background-position':'right bottom'}&lt;/component-param&gt;<br>
   &lt;/component&gt;<br>
   &lt;component name="title" type="none"/&gt;<br>
   &lt;component name="body" type="none"/&gt;<br>
&lt;/typical-component&gt;<br>
</code></pre>
如这段代码，定义了一个四角为圆弧的边框，<b>border.gif</b> 为一个空心圆的图片。title和body为待替换的节点，下面为引用这个自定义界面对象的代码。<br>
<pre><code>&lt;replacement name="border" baseComponentName="sample;title,body"&gt;<br>
   &lt;component name="title" type="span" comParam="text:'标题'"/&gt;<br>
   &lt;component name="body" type="div"&gt;<br>
      ... <br>
   &lt;/component&gt;<br>
&lt;/replacement&gt;<br>
</code></pre>
上面这段代码中，baseComponentName的值除了设置了引用的自定义界面对象的名称，还指定了要替换的子节点的名称，“;”后面的部分，多个名称之间用“,”分隔。里面名称为title和body的两个子节点即为要替换的子节点，他们会代替原先界面对象中的同名节点在界面上出现。<br>
这样自己定义的界面对象重用的方式是不是灵活多了，你不用考虑如何提供接口和参数，只需要把界面对象定义出来，并且给每个节点赋上唯一的名称就行了。使用时可以根据自己的需要，将不合适的节点换成自己重新定义的，你有多大的想象空间，这框架就能给你多大的发挥空间，还不快自己试一下。