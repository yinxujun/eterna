# 从request-parameter中取值 #
因为request中的参数都是以数组形式存放的，程序中使用的getRequestParameter方法是获取数组的第一个值。但如果你使用trans-execute时，如果不做任何处理，取出来的就是一个数组。
```
<trans-execute from="request-parameter:XXX" to="cache:0"/>
```
如上面这段代码，放入cache:0中的是个字符串数组，而不是一个字符串。所以，在从request-parameter中取值时，要增加getFirstString操作，代码如下：
```
<trans-execute from="request-parameter:XXX" opt="getFirstString" to="cache:0"/>
```
这是刚使用的人经常犯的错误，当然如果你确实要以数组的方式取出多个参数，那就不必加这个操作了。
<br><br>

<h1>多行结果集与单行结果集</h1>
并不是只有一条记录的结果集就是单行结果集，这两类数据的结构及使用的场景是不同的。多行结果集用在<code>table-list</code>上，单行结果集用在<code>table-form</code>上。<br>
一般，直接查询出的结果是个多行结果集的结构，要获得单行结果集的结构，需要通过<code>getFirstRow</code>操作来获取。<br>
<pre><code>&lt;!-- 获取多行结果集 --&gt;<br>
&lt;query-execute queryName="XXXX"/&gt;<br>
&lt;trans-execute from="stack" to="data:multiRow"/&gt;<br>
<br>
&lt;!-- 获取单行结果集 --&gt;<br>
&lt;query-execute queryName="XXXX"/&gt;<br>
&lt;trans-execute from="stack" opt="getFirstRow" to="data:oneRow"/&gt;<br>
</code></pre>
<br>

<h1>获取数据时报数据不存在</h1>
在使用<code>trans-execute</code>时，有时候会报<code>from</code>中不存在数据，这是因为在数据移动时，会检查数据是否存在，如果不存在的话就会报这个错。解决办法是使用<code>mustExists</code>属性，它的默认值是<code>true</code>，表示必须存在数据，将其设为<code>false</code>在数据移动时就不会要求<code>from</code>中必须存在数据。<br>
注：当<code>mustExists</code>设为<code>false</code>，<code>from</code>不存在数据的情况下，数据移动完毕后，<code>to</code>中的内容也会被置空。