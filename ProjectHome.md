eterna框架可以让你在不用写任何java代码的情况下，仅通过一些xml的配置来开发你的Java应用。<br><br><br>
<h2>一个超轻量级的框架</h2>
eterna框架包括了数据库层、控制层、视图层的配置功能，对于90%以上（微量业务）的开发场景，您可以不用书写任何Java代码。对于另外10%较复杂的业务场景，你也可以通过实现框架中提供的相应接口，将特殊的业务配置到整个执行流程中。<br>
关于框架的设计理念可看<a href='http://code.google.com/p/eterna/wiki/introduce'>这里</a>。<br>
<br>
在例子中，提供了一些基本的界面元素，包括了查询界面的模板，输入元素的检查等。<br>
<br>
<h2>日志</h2>
eterna框架提供了完善的日志功能，你可以在web界面中直接查看日志，并且可以在web界面中直接控制日志的开启或关闭。日志的内容也十分完善，记录了每个请求执行的流程，每个请求开始及结束时的数据情况，每个流程中执行的sql及其结果。<br>
<br>
<h2>主要功能</h2>
可以将这个框架看作是管理类系统的定义语言，对于一些基本的CRUD操作，可以立刻开发出来，而无需写任何代码。对于一些特殊的应用，也可以在这上面开发，当然，也可以引入一些处理特定业务的框架，加快开发的效率。<br>
此框架分为以下3个部分：<br>
<b>数据库</b><br>
eterna框架是通过预先定义动态的SQL语句来处理数据库的操作。对于普通的处理，可以定义静态的SQL，绑定上参数就可以了。在特殊的场景中，可通过子语句#sub、动态参数#param等，形成一个动态SQL，进行一些特殊功能的处理。<br>
<b>业务</b><br>
eterna框架提供了一种组装业务模型的方式，即将多个执行单元的组合起来，构成一个执行流程。实现不同的业务，就是构造不同的执行流程的过程。框架提供了一些基本的执行单元，如：update-execute(更新)、query-execute(查询)、trans-execute(数据移动)、check-execute(检查+分支)等。<br>
这些执行单元可以组合出85%以上的业务模型，其他特殊的业务需求可以定义自己的执行单元(execute类)，并将其配置到需要的业务模型中。<br>
<b>页面</b><br>
eterna框架提供了一种动态页面语法。通过XML的语义结构定义页面中的对象，每个对象的显示逻辑、初始化逻辑、事件等都可以和对象一起定义。定义语义还可以重用已定义的对象，这样可以大大加快页面的开发效率，具体说明见<a href='http://code.google.com/p/eterna/wiki/view_desc1'>《eterna框架界面定义结构的好处》</a>。除此之外，对于控件初始化时的参数传递，框架提供了一个临时环境变量。其特点是里面的值可以从父对象传递到子对象，子对象中改变了这个值，不会影响到父对象及平级对象。<br>
<br>
以上三块功能，可以只单独使用其中的某一个或某几个，并不是一定要所有的功能一起使用。<br>
<br>
相关文档请点击<a href='http://eterna.googlecode.com/files/eternadoc.rtf'>这里</a>下载。<br>
<br>
<h2>框架的结构图</h2>
<img src='http://eterna.googlecode.com/files/eterna.jpg' /><br>
Query和Update为后端与数据库交互的组件。<br>
Search为执行查询及分页管理的组件，需要通过Query来运行。<br>
Model为业务执行组件Execute集合。<br>
Execute可调用Query、Update及Search，也可以自己定义执行方式。<br>
View为视图表现层，辅助JSP来显示页面，最终客户端展现是通过jQuery来处理。<br>
Export为转向的控制，可以控制转向另一个Model或转向某个JSP。<br>
AppData为数据的载体，贯穿整个过程。<br>
<br>
执行的流程：<br>
首先http请求被分发到一个Model来处理相关的业务流程。<br>
Model会顺序执行其下面的每个Execute，执行完后会返回一个Export。<br>
Export指向一个JSP，这个JSP可使用Export中指定的View来构成客户端的显示页面。<br>
另外Export还可指向另一个Model来继续处理业务流程。<br>
<br>
此框架的另一个特点就是保证大部分改动只要修改一个地方，你不需要既改jsp又改代码又改配置。<br>
如下图所示：<br>
<img src='http://eterna.googlecode.com/files/eterna2.jpg' /><br>
query中的reader元素可以通过reader-manager来生成。<br>
table-form中的cell元素可以通过query、search、reader来生成。<br>
table-list中的column元素可以通过query、reader来生成。<br>
这样如果有修改，你只需改动query、search或reader，那相应的其它元素也会随之更新。<br>
<br>
<h2>最近更新</h2>
<br>
1.5.7<br>
在使用jdk1.5及以上版本时，运行日志中记录的时间可精确到千分之一毫秒。<br>
<code>self.micromagic.eterna.sql.SpecialLog</code>:中的logSQL方法的usedTime参数进行的调整，此参数将会根据当前的jdk版本表示毫秒值或纳秒值，可使用<code>self.micromagic.util.logging.TimeLogger</code>.getPassTime(long)方法获取毫秒值。<br>
修复了select控件在局部更新时无法加载选项的问题。<br>
修复了在初始化过程中执行了reloadWebObj而造成eterna_doInitObjs提前执行的bug。<br>
<br>
1.5.6<br>
修复了对java.util.Iterator或java.util.Enumeration进行app日志记录时造成游标移到最后的bug。<br>
增加了对自适应网页设计（Responsive Web Design）的处理。<br>
可在reader节点中设置print.caption属性，用于控制导出时使用的标题。<br>
<br><br>
更多更新日志请查看：<a href='http://code.google.com/p/eterna/wiki/VersionInfo'>版本更新日志</a>