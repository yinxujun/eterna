# 参数绑定(`param-bind`)中的names属性说明 #
在`query-execute`或`update-execute`中，是通过`param-bind`子节点来绑定参数的，可以通过设置多个`param-bind`子节点来设置多个参数来源或多种绑定方式。<br>
<code>param-bind</code>节点有两个最常用的属性：src和names。src为必须的属性，用于指定参数的来源。names为可选属性，用于指定设置哪些参数及参数名的对应关系。当没有指定names属性时，将根据query或update的参数列表自动寻找参数。<br>

<h2>names属性设置与不设置的区别</h2>
如：当前传入的参数有a1, b2, c3。当这3个参数都存在时，下面两种设置方式没有区别。<br>
方式1：<br>
<pre><code>&lt;param-bind src="RP"/&gt;<br>
</code></pre>
方式2：<br>
<pre><code>&lt;param-bind src="RP" names="a1,b2,c3"/&gt;<br>
</code></pre>
但是当这3个参数中，可能有不存在时。第一种方式就会发生有参数未设置的错误，而第二种方式就能正常执行。<br>
这里的区别就在于，自动寻找参数时，当某个参数不存在时，就不会进行设置。而指定了名称的话，当参数不存在时，会设置为忽略（当对应的parameter为动态参数时）或null。<br>
如果对于参数比较多的情况，只有某几个参数会有不存在的可能，其他大部分参数必定存在，则可以使用组合的方式，先指定可能会不存在的参数，再使用自动绑定。<br>
<pre><code>&lt;param-bind src="RP" names="maybe1,maybe2,maybe3"/&gt;<br>
&lt;param-bind src="RP"/&gt;<br>
</code></pre>

<h2>参数名称不匹配时的处理</h2>
当来源中的参数名称与参数列表的中名称不一致时，可以使用“<code>name1:name2</code>”的格式来指定名称的对应。如，传过来的参数名称为：id，name，参数列表中的名称为：code，text。则可以这么写：<br>
<pre><code>&lt;param-bind src="RP" names="id:code,name:text"/&gt;<br>
</code></pre>
使用这种方式，还可以将同一个参数，指定到参数列表中的多个值。<br>
<pre><code>&lt;param-bind src="RP" names="id:id1,id:id2,id:id3"/&gt;<br>
</code></pre>