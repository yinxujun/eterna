# 概述 #
框架中的预备SQL语句主要出现在query和update中的prepared-sql节点的内部文本中，和JDBC中的PreparedStatement一样，"?"表示需要设置参数。而"#"则是框架中添加的特殊标识，用于处理特殊的语法。<br>
第一个用处就是对单个字符的转义，如：<br>
##：表示一个"#"<br>
#?：表示这个"?"是一个字符，不会被认为是参数<br>
下面会分别介绍一些其他的用法。<br>
<br>
<h1>sub 动态子语句</h1>
动态子句就是一段SQL语句片段，它可以和主语句构成一个完整的SQL。<br>
完整的动态子句写法如下：<br>
<pre><code>#sub[...$...]<br>
或<br>
#sub<br>
</code></pre>
以"#sub"开始，后面"<a href='.md'>.md</a>"内的部分为子句的模板（如果没有模板则可去掉"<a href='.md'>.md</a>"部分），"$"为动态子句出现的位置。如果模板部分需要出现"<a href='.md'>.md</a>$"这些字符，可以用"#["，"#]"，"#$"代替。<br>
模板的作用是这样的，当插入的内容为空时，则动态子语句就变为空字符串，当插入的内容不为空时，则将插入的内容套入模板后再插入。<br>
例子如下：<br>
<pre><code>预备SQL语句如下<br>
SELECT * FROM TEST_TABLE<br>
#sub[WHERE $]<br>
<br>
当执行了<br>
query.setSubSQL(1, "");<br>
后，此段语句就变为<br>
SELECT * FROM TEST_TABLE<br>
<br>
当执行了<br>
query.setSubSQL(1, "status = 0");<br>
后，此段语句就变为<br>
SELECT * FROM TEST_TABLE<br>
WHERE status = 0<br>
</code></pre>
有了子句模板之后，代码中就不必关心这部分SQL片段插入后需要在之前或之后添加什么了。<br>
setSubSQL方法的说明：<br>
第一个参数为要设置的子句的索引值（即第几个子句），从1开始。<br>
第二个参数为要设置的子句片段。<br>
第三个参数为一个参数管理者，可以用于绑定子句中出现的参数。<br>
下面的例子为一个带参数的子句。<br>
<pre><code>PreparerManager pm = new PreparerManager(2);<br>
// 绑定第一个整型参数<br>
ValuePreparer preparer = factory.getDefaultValuePreparerCreaterGenerator().createIntPreparer(1, 0);<br>
// 绑定第二个字符串型参数<br>
ValuePreparer preparer = factory.getDefaultValuePreparerCreaterGenerator().createStringPreparer(2, "用户名");<br>
query.setSubSQL(1, "status = ? and userId = ?", pm);<br>
</code></pre>
<br>
<h1>param 动态参数</h1>
动态参数就是一段可以出现也可以不出现的，用于参数设置的语句片段。<br>
完整的动态参数写法如下：<br>
<pre><code>#param(name)[...?...]<br>
</code></pre>
以"#param"开始，后面"(name)"内的"name"表示动态参数模板段分组的名称，同一名称的模板，要么在设置参数时同时出现，要么没有设置参数时都不出现。再后面"<a href='.md'>.md</a>"内的部分为动态参数的模板部分，"?"为绑定的参数的位置。<b>一个动态参数的所有模板段中只能出现一个"?"</b>，如果模板部分需要出现"<a href='.md'>.md</a>?"这些字符，可以用"#["，"#]"，"#?"代替。<br>
例子如下：<br>
<pre><code>预备SQL语句如下<br>
update TEST_TABLE set status = ? #param(id)[, userId = ?]<br>
<br>
当执行了<br>
set.setString(2, "用户名");<br>
后，此段语句就变为<br>
update TEST_TABLE set status = ? , userId = ?<br>
<br>
当执行了<br>
set.setIgnore(2);<br>
后，此段语句就变为<br>
update TEST_TABLE set status = ? <br>
</code></pre>
关于模板段分组的名称的作用，看下下面这句插入语句<br>
<pre><code>INSERT INTO TEST_TABLE (id #param(v)[, value] #param(s)[, status])<br>
VALUES (? #param(v)[, ?] #param(s)[, ?])<br>
</code></pre>
这句语句中有两个动态参数，每个动态参数的模板分为了两段。<br>
<br>
<h1>auto 动态语句生成</h1>
动态语句生成是根据参数列表"parameters"来生成语句片段。<br>
完整的动态语句生成写法如下：<br>
<pre><code>#auto[type,num1,num2]<br>
</code></pre>
以"#auto"开始，"type"为生成语句的模板，"num1"为重第几个参数开始动态生成语句片段，"num2"为生成到第几个参数。<br>
"num1"和"num2"为正数时表示从第一个开始，如1表示第一个、2表示第二个...<br>
"num1"和"num2"为负数时表示从最后一个开始，如-1表示最后一个、-2表示倒数第二个...<br>
"num1"和"num2"为名称格式时，i+XXX表示到XXX之后，i-XXX表示到XXX之前，i=XXX表示到XXX。<br>
"num2"的最终结果值必需大于"num1"，如参数列表的总数为5，num1设为-2，num2设为3，这样是非法的，因为-2的最终结果值为4，大于3。<br>
type模板的说明如下：<br>
<pre><code>如果parameters的配置有name、sex，age这3个参数<br>
<br>
query<br>
各语句where语句部分的模板，例子如下：<br>
动态语句生成的配置为：#auto[query,1,3]<br>
生成的结果为：name = ? and sex = ? and age = ?<br>
<br>
queryD<br>
各语句where部分的动态参数模板，例子如下：<br>
动态语句生成的配置为：#auto[queryD,1,2]<br>
生成的结果为：#param(dAuto_1)[ and name = ?]#param(dAuto_2)[ and sex = ?]<br>
<br>
update<br>
update语句set部分的模板，例子如下：<br>
动态语句生成的配置为：#auto[update,1,3]<br>
生成的结果为：name = ?, sex = ?, age = ?<br>
<br>
updateD<br>
update语句set部分的动态参数模板，例子如下：<br>
动态语句生成的配置为：#auto[updateD,1,2]<br>
生成的结果为：#param(dAuto_1)[, name = ?]#param(dAuto_2)[, sex = ?]<br>
<br>
insertN<br>
insert语句列名部分的模板，例子如下：<br>
动态语句生成的配置为：#auto[insertN,1,3]<br>
生成的结果为：name, sex, age<br>
<br>
insertND<br>
insert语句列名部分的动态参数模板，例子如下：<br>
动态语句生成的配置为：#auto[insertND,1,2]<br>
生成的结果为：#param(dAuto_1)[, name]#param(dAuto_2)[, sex]<br>
<br>
insertV<br>
insert语句值部分的模板，例子如下：<br>
#auto[insertV,1,3]<br>
生成的结果为：?, ?, ?<br>
<br>
insertVD<br>
insert语句值部分的动态参数模板，例子如下：<br>
#auto[insertVD,1,2]<br>
生成的结果为：#param(dAuto_1)[, ?]#param(dAuto_2)[, ?]<br>
</code></pre>
名称格式的样例如下：<br>
<pre><code>如果parameters的配置有name、sex，age这3个参数<br>
<br>
动态语句生成的配置为：#auto[update,1,i-sex]<br>
生成的结果为：name = ?<br>
<br>
动态语句生成的配置为：#auto[update,1,i=sex]<br>
生成的结果为：name = ?, sex = ?<br>
<br>
动态语句生成的配置为：#auto[update,1,i+sex]<br>
生成的结果为：name = ?, sex = ?, age = ?<br>
</code></pre>
<br>
<h1>const 常量</h1>
在语句中引用一个设置的常量。<br>
完整的常量写法如下：<br>
<pre><code>#const(name)<br>
</code></pre>
以"#const"开始，后面"()"中的"name"为所引用的常量的名称。<br>
常量的用处在于简化语句，增加语句的可读性。尤其在语句中有较长的一段子查询的时候，将其定义成常量，在主语句中引用进来，这样主语句就不会显得很臃肿。<br>
常量的另一个用处就是兼容不同的数据库，如对"null"的判断，在oracle下为"nvl"，在mysql下为"ifnull"。这里我们就可以定义一个常量"null"，在语句中使用"#const(null)"，在项目中将常量"null"的值设为对应数据库的具体实现。<br>
<br>
<h1>例子中的变量说明</h1>
query为<code>self.micromagic.eterna.sql.QueryAdapter</code>的实例<br>
update为<code>self.micromagic.eterna.sql.UpdateAdapter</code>的实例<br>
factory为<code>self.micromagic.eterna.share.EternaFactory</code>的实例