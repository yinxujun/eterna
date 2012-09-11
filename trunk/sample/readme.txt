例子中使用的是java开发的开源数据库H2，数据库文件在test\WebContent\WEB-INF\db下。
例子中需要的表都已经建好了，无需做任何改动，可在eclipse中直接运行。第一次运行时，需要选择一个应用服务器。


micromagic_config.properties文件中的几个配置的说明：

一、
dataSource.url=jdbc:h2:${h2.baseDir}/test
此配置为H2的数据库连接字符串，“${h2.baseDir}”为数据库文件所在的路径，此变量会在test/Test.java这个servlet初始化时设置进去。
如果你把数据库文件放在了别的目录，则可直接修改这个配置，或者添加一个“h2.baseDir”属性，如：
h2.baseDir=C:\\db
表示数据库文件在C盘的db目录下
h2.baseDir=~
表示数据库文件在当前用户路径下，具体可查看H2的说明文档

二、
self.micromagic.useEternaLog=true
表示加载eterna的日志，这样就可以在“[contextRoot]/eterna/setting.jsp”中的error日志中查看到所有的日志输出。

三、
self.micromagic.eterna.digester.checkGrammer=false
表示关闭页面脚本的语法结构检查，这样可以提高加载的效率。
但如果某个脚本有编写错误的话（如少了一个"}"等），初始化时就不会发现这个错误，此错误将会直接出现在页面中，造成页面无法显示。
