## 1.5.7 ##
在使用jdk1.5及以上版本时，运行日志中记录的时间可精确到千分之一毫秒。<br>
self.micromagic.eterna.sql.SpecialLog:中的logSQL方法的usedTime参数进行的调整，此参数将会根据当前的jdk版本表示毫秒值或纳秒值，可使用self.micromagic.util.logging.TimeLogger.getPassTime(long)方法获取毫秒值。<br>
修复了select控件在局部更新时无法加载选项的问题。<br>
修复了在初始化过程中执行了reloadWebObj而造成eterna_doInitObjs提前执行的bug。<br>
<hr />
<h2>1.5.6</h2>
修复了对java.util.Iterator或java.util.Enumeration进行app日志记录时造成游标移到最后的bug。<br>
增加了对自适应网页设计（Responsive Web Design）的处理。<br>
可在reader节点中设置print.caption属性，用于控制导出时使用的标题。<br>
<hr />
<h2>1.5.5</h2>
修复了eterna_global.xml中定义的list_operator控件，在需要确认时点击取消仍然继续执行的bug。<br>
script代码中增加了{$caption:name}，用于获取标题的翻译值。<br>
修复了self.micromagic.util.StringAppend实现类中append(String, int, int)方法的一个bug。<br>
<hr />
<h2>1.5.4</h2>
将self.micromagic.eterna.sql.impl包下一些类的clone方法改为copy方法。<br>
修复了data-source-manager节点设置className属性时没有效果的错误。<br>
将样例做成了eclipse的项目包，可以在eclipse中直接运行。<br>
<hr />
<h2>1.5.3</h2>
修复了使用init标签时，访问类型不为web时仍然会有页面代码输出的bug。<br>
修复了在界面中直接对eg_temp赋值后，造成事件中获取的值不正常的问题。<br>
self.micromagic.eterna.sql.SpecialLog:中的logSQL方法做了调整。<br>
self.micromagic.eterna.sql.impl.SQLAdapterImpl:中的logSQL方法做了调整。<br>
增加了self.micromagic.eterna.search.<code>DoubleQuerySearch</code>，进行两次查询的搜索，详见<a href='http://code.google.com/p/eterna/wiki/DoubleQuerySearch'>两次查询的搜索</a>。<br>
self.micromagic.eterna.view.Replacement:中的initReplace方法做了调整。<br>
<hr />
<h2>1.5.2</h2>
修复了一个会造成框架无法初始化的bug。<br>
<hr />
<h2>1.5.1</h2>
修复了针对session优化后出现的一个问题。<br>
在参数绑定时(self.micromagic.eterna.model.ParamSetManager)，因为是空字符串而产生的类型转换错误，不记录警告日志。<br>
修复了self.micromagic.app.WebApp中的QueryTool获取的EternaFactory在重新初始化后没有释放的问题。<br>
增加了self.micromagic.dc包，里面为各类动态代码的生成工具。<br>
将包self.micromagic.eterna.sql.converter移至了self.micromagic.util包下。<br>
将类self.micromagic.app.JavaCodeExecute移至了self.micromagic.dc包下。<br>
调整了self.micromagic.eterna.sql.preparer包下类的结构。<br>
增加了从初始化cache中构造self.micromagic.eterna.share.DataSourceManager。<br>
在self.micromagic.eterna.digester.FactoryManager.Instance接口中增加了setAttribute、removeAttribute及getAttribute方法。<br>
增加了self.micromagic.util.ext.SimpleDataSource类，用于处理一些不支持事务的数据库。<br>
eterna_global.xml中增加了getData_row方法。作用为：读取多行结果集中的一行, 并以单行结果集的形式返回。<br>
增加了在页面中直接生成页面脚本的标签，见web/eterna.tld。<br>
<hr />
<h2>1.5.0</h2>
request-parameter、request-attribute、session-attribute可以使用缩写：RP、RA、SA。<br>
修复了self.micromagic.eterna.model.impl.ModelCallerImpl中，transactionType为new时的一个错误。<br>
修复了当transactionType设为hold后，业务未成功接管数据库链接时，造成链接无法释放的问题。<br>
增加了data-printer节点。<br>
在view节点中增加了dataPrinterName属性和defaultDataType属性。<br>
将类self.micromagic.app.CustomResultIterator移至了self.micromagic.util包下。<br>
增加了self.micromagic.app.JavaCodeExecute类，可以直接执行java代码(需要javassist)。<br>
<hr />
<h2>1.4.1</h2>
完善了数据库日志，在参数列表中增加了参数的名称。<br>
修复了reader-manager节点设置readerOrder属性时发生的错误。<br>
修复了多层重用typical-replacement时的一个问题。<br>
eterna.js中修复了ef_formatNumber方法的一个错误。<br>
<hr />
<h2>1.4.0</h2>
对一些长期保存的字符串将其放入字符串池, 这样可节省30%以上的内存空间。<br>
将update-execute节点下错误的属性名mutiType，改为正确的multiType。<br>
增加了resource节点，用于配置可在view中引用的文本资源。<br>
将错误的类名self.micromagic.util.MutiConditionBuilder，改为正确的MultiConditionBuilder。<br>
将错误的类名self.micromagic.util.MutiOutputStream，改为正确的MultiOutputStream。<br>
将错误的类名self.micromagic.util.MutiTemplateFormat，改为正确的MultiTemplateFormat。<br>
script代码中增加了{$ef:name}、{$efV:name}、{$data:name}、{$dataV:name}、{$res:name}、{$resV:name}、{$typical:name}、{$typicalV:name}、{$res:name}、{$resV:name}等扩展语法。<br>
增加了self.micromagic.app.CallQuery类，可以调用带返回参数的存储过程。<br>
export的redirect设为true后，在Servlet下有效。<br>
使用的jQuery版本更新到1.6.2。<br>
eterna.js中调整了弹出窗口的锁定方式。<br>
<hr />
<br>
<h2>1.3.2</h2>
eterna.js中调整了弹出窗口对parentEterna的初始化过程。<br>
调整self.micromagic.app.NoParamQueryExecute类对数据库链接的获取, 可以对transactionType设为notNeed, 此类会在需要的时候自己来获取数据库链接。<br>
增加了self.micromagic.app.SpellCoderExecute类，作用为将中文转换为拼音编码。<br>
micromagic_config.properties配置文件中增加了self.micromagic.parent.properties属性，指向要读取的父配置文件，父配置文件中的同名属性不会被读取进来。<br>
micromagic_config.properties配置文件中增加了self.micromagic.eterna.digester.subinitfiles属性，指定要进行全局初始化的子文件列表，子文件列表中的对象会覆盖掉全局初始化的文件列表中的同名对象。<br>
<hr />
<br>
<h2>1.3.1</h2>
app运行日志中增加了信息日志和sql日志的显示。<br>
self.micromagic.eterna.model.AppData:中增加了addAppMessage方法。<br>
修复了多层重用typical-replacement时的一个问题。<br>
修正了self.micromagic.app.NoParamQueryExecute类中的一个错误。<br>
增加了self.micromagic.app.ExportCSV类，作用为导出csv格式的数据流。<br>
<hr />
<br>
<h2>1.3.0</h2>
self.micromagic.eterna.search.ConditionPropertyGenerator:中的多个方法做了调整。<br>
self.micromagic.eterna.search.ConditionProperty:中的多个方法做了调整。<br>
self.micromagic.eterna.sql.SQLParameterGenerator:中的多个方法做了调整。<br>
self.micromagic.eterna.sql.SQLParameter:中的多个方法做了调整。<br>
self.micromagic.eterna.model.ModelAdapterGenerator:中增加了setKeepCaches方法。<br>
self.micromagic.eterna.model.ModelAdapter:中增加了isKeepCaches和getFrontModelName方法。<br>
增加了一个接口self.micromagic.eterna.sql.SQLParameterGroup，管理一组参数。<br>
model节点中增加了keepCaches属性。<br>
增加了parameter-group节点。<br>
query和update节点下，parameters节点下，增加了parameter-ref节点。<br>
search节点下，condition-propertys节点下，condition-property节点增加了defaultValue属性。<br>
修正了app运行日志记录时的一个严重错误。<br>
增加了一个默认的builder: checkNull，用于判断是否为空。值为1检查是否为空，值为0检查是否不为空。<br>
self.micromagic.eterna.share.DataSourceManager:中调整了addDataSource方法的参数。<br>
<hr />
<br>
<h2>1.2.1</h2>
修正了当因position设置model没有执行时，app运行日志记录错误的问题。<br>
更新了self.micromagic.util.AttributeComponent类，增加attrName中值的动态处理。<br>
修正了init-param节点中定义了调用方法的元素无效的问题。<br>
补充了app运行日志显示的内容。<br>
<hr />
<br>
<h2>1.2.0</h2>
修正了cell节点当设置了typicalComponentName又将titleSize设为0后出错的问题。<br>
修正了param-bind节点中generator属性设置无效等问题。<br>
修正了配置文件继承时出错的问题。<br>
self.micromagic.eterna.sql.ResultIterator:中增加了preFetch和getCurrentRow方法。<br>
增加了self.micromagic.util.AttributeComponent类，作用为输出静态的html。<br>
增加了self.micromagic.eterna.model.AppDataLogExecute类，作用为输出当前运行时的变量值。<br>
增加了app运行日志，可以查询系统运行的过程。<br>
<hr />
<br>
<h2>1.1.0</h2>
为了性能将query的forwardOnly属性的默认值改为"true"。<br>
修复了在query节点的readers节点中设置readerOrder时出错的问题。<br>
search-execute节点中增加了searchCountName属性，用于设置总记录数查询结果放入data中使用的名称。<br>
query-execute节点中增加了countType属性，用于设置计算总记录数的方式。<br>
search节点中增加了countType属性，用于设置计算总记录数的方式。<br>
去掉了self.micromagic.eterna.model.DataErrorException类，因为没有使用。<br>
去掉了table-form和table-list中的parent属性，因为无法使用。<br>
调整了eterna.js中真的loop类型控件，增加了只设置循环次数的方式。<br>
<hr />
<br>
<h2>1.0.3</h2>
调整了在event中初始化eg_temp的方式。<br>
修复了replacement节点生成中的一个问题。<br>
<hr />
<br>
<h2>1.0.2</h2>
修复了重用typical-replacement时的一个问题。<br>
在event中也可以使用eg_temp、webObj、objConfig这几个和当前节点环境相关的变量了。<br>
修复了body文本设置trimLine和noLine属性时会影响后面的同类配置的问题。<br>
增加了self.micromagic.app.ExportExcelExecute类，作用为导出excel。<br>
<hr />
<br>
<h2>1.0.1</h2>
修复了在update和query节点中设置logType及attribute时出错的问题。<br>
self.micromagic.eterna.sql.SpecialLog:中更新了logSQL方法。<br>
self.micromagic.eterna.sql.SQLAdapter:中增加了getAttributeh和getAttributeNames方法。<br>
micromagic_config.properties文件中增加了self.micromagic.useEternaLog属性，设置为"true"的话表示使用框架自带的日志，默认为"false"。<br>
增加了一个执行器self.micromagic.app.EditParameterMapExecute，可以将data中的requestPamaneterMap变为可编辑的。<br>
调整了eterna.js中真的loop类型控件的生成，每次循环后会将数据集社会初值, 这样即使循环体里改变了数据集也不会有影响。<br>
修改了dtd文件：<br>
在model节点中增加了frontModelName属性，用于设置只对此model有效的frontModel。<br>
修改了例子中的相关文件。<br>
<hr />
<br>
<h2>1.0.0</h2>
self.micromagic.eterna.search.ColumnSetting:中的getColumnSetting方法的参数做了调整。<br>
如果程序中实现的以上接口，需要重新实现。<br>
self.micromagic.eterna.sql.QueryAdapter中增加了setMultipleOrder方法，可设置多列排序。<br>
修复的eterna.js中table-form有rowSpan时的一个bug。<br>
修复了search节点设置doExecute和forceSetParam属性无效的问题。<br>
search节点的conditionIndex可设为0，表示没有条件要设置，仅需分页。<br>
search节点的queryName属性可设为$none，表示此search不需要执行，仅需获得条件。<br>
query和update节点的logType属性可设为none。<br>
self.micromagic.util.Formater类中，parserDatetime等方法名改为parseDatetime等。<br>
在初始化script元素节点时增加了语法检查。<br>
是否要语法检查可通过micromagic_config.properties文件的self.micromagic.eterna.digester.checkGrammer属性来设置，默认为"true"。<br>
修改了dtd文件：<br>
prepared-sql节点中增加了noLine属性，这样获得的sql语句就不会有换行符。<br>
将component、typical-component、typical-replacement节点的type或baseComponentName属性改为必填。<br>
query-execute节点中增加了start和count属性。<br>
factory下增加了user-manager节点，用于定义管理用户的实现类。<br>
factory下增加了data-source-manager节点，用于定义数据源的管理器(目前只能通过jndi绑定数据源)。<br>
一些子节点的定义去掉了顺序的要求。<br>
修改了例子中的相关文件。<br>
<hr />
<br>
<h2>0.9.0</h2>
修复了使用replacement的一个bug。<br>
增加了self.micromagic.util包中MemoryStream和MemoryChars的磁盘缓存功能。<br>
修改了dtd文件：<br>
将search节点中的subCondition属性，改为specialCondition。<br>
节点before-init、init-script、component-param、title-param、init-param中的值可以设置到属性中<br>
名称分别为beforeInit、initScript、comParam、titleParam、initParam。<br>
修改了例子中的相关文件。<br>
<hr />
<br>
<h2>0.8.1</h2>
修复了使用replacement的一个bug。<br>
修改了dtd文件，tr节点可以设置name属性。<br>
增加了内存日志自动清理的功能。<br>
增加了前置model的设置。<br>
修改了例子中的相关文件。<br>
<hr />
<br>
<h2>0.8.0</h2>
self.micromagic.eterna.model.ModelCaller:中增加了initModelCaller方法。<br>
self.micromagic.eterna.view.StringCoder:中增加了initStringCoder方法。<br>
self.micromagic.eterna.search.ParameterSetting:中增加了initParameterSetting方法。<br>
self.micromagic.eterna.search.ColumnSetting:中增加了initColumnSetting方法。<br>
self.micromagic.eterna.model.ModelCaller:中的某些方法做了调整。<br>
如果程序中实现的以上接口，需要重新实现。<br>
调整了query和update节点中sql日志记录的方式，增加了错误标记。<br>
replacement可以设置以"view:"开始的baseComponentName，表示基于某个view来替换。<br>
table-form和table-list中的cell和column中增加了可将名称设为$typical单元，作用为当其他单元的某个属性没有设置时会初始化为这个单元的属性。<br>
修改了dtd文件:<br>
在search-manager节点中增加了attribute子节点。<br>
cell节点中增加了rowSpan和newRow属性。<br>
model节点中增加了dataSourceName属性。<br>
增加了special-log节点，用于配置特殊的sql日志的记录。<br>
调整了eterna.js中生成table-form的方法。<br>
修改了例子中的相关文件。<br>
<hr />
<br>
<h2>0.7.0</h2>
增加了配置文件的继承关系及相关的获取方法。<br>
在reader和condition-property的attribute中增加了containerParam和titleParam的设置。<br>
修复的eterna.js中table-list当某行不生成时eg_temp.rowNum不正确的问题。<br>
修改了trans-execute中当值不存在时，没有将目标清空的问题。<br>
增加了view模块载入时根路径的判断。<br>
在component-param节点中增加了creater(构造函数)的设置。<br>
在update-execute中增加了mutiType属性。<br>
增强了param-bind的子语句设置的功能。<br>
调整了配置文件的继承模式。<br>
修改了dtd文件中的一些错误。<br>
修改了例子中的相关文件。<br>
<hr />
<br>
<h2>0.6.0</h2>
增加了query和update节点中单独设置sql日志记录方式的属性logType。<br>
在search-execute节点中增加了保持链接的查询方式将holdConnection设为true。<br>
修改了例子中的相关文件。<br>
<hr />
<br>
<h2>0.5.3</h2>
修复的eterna.js中table-form的一个bug。<br>
修改了例子中的相关文件。<br>
<hr />
<br>
<h2>0.5.2</h2>
self.micromagic.app.ReadExcelExecute:修改了单元格读取的方式。<br>
self.micromagic.eterna.model.impl.ModelCallerImpl:修改了返回错误状态的export后未回滚的错误。<br>
将self.micromagic.eterna.model.ModelFilter改为self.micromagic.app.EternaFilter<br>
修改了例子中的相关文件。<br>
<hr />
<br>
<h2>0.5.1</h2>
self.micromagic.app.ReadExcelExecute:增加了needRowIndex的设置, 标识是否需要行号列; 增加了错误数据的记录。<br>
self.micromagic.eterna.model.impl.ModelCallerImpl:将是否执行完的标志设置代码后移，这样在返回错误状态的export后也会回滚。<br>
修改了例子中的相关文件。