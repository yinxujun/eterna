# web.xml中servlet的定义 #
```
<servlet>
   <servlet-name>test</servlet-name>
   <servlet-class>self.micromagic.app.EternaServlet</servlet-class>
   <init-param>
      <param-name>initFiles</param-name>
      <param-value>cp:test/my.xml</param-value>
   </init-param>
</servlet>
<servlet-mapping>
   <servlet-name>test</servlet-name>
   <url-pattern>/test.do</url-pattern>
</servlet-mapping>
```

参数initFiles，指配置文件，多个文件可以用";"分割。如果是以"cp:"开头的，表示是classpath中的路径。

如果想要xml修改后能够自动加载，那可以自己定义一个类继承`self.micromagic.app.EternaServlet`，并且实现`self.micromagic.eterna.share.EternaInitialize`接口。然后定义如下方法：
```
private static long autoReloadTime()
{
   return 10000L;
}
```
此方法必须是静态的，返回类型为long，表示两次判断文件是否有更新的最新间隔时间，单位为毫秒，上面例子中最小间隔时间为10秒。<br>
将web.xml中，相关的servlet类换成你自己定义的类，这样就会在xml文件更新后自动加载了。<br>
如例子中的test.Test类就是这个作用。<br>
<br><br>


<h1>如何使用dtd</h1>
有人说xml配置起来麻烦，但如果xml编写的时候有提示呢，那是不是就和写java代码一样。<br>
下图为如何在eclipse中配置dtd文件。<br>
<img src='http://eterna.googlecode.com/files/eclipse_dtd.gif' /><br>
此功能在eclipse的windows——>preferences——>Files and Editors菜单中，点击add后就能出现下半部分，按图中的配置进行设置。<br>
这样，你的xml起始部分按如下代码书写就能有自动提示了。<br>
<pre><code>&lt;?xml version="1.0" encoding="GBK"?&gt;<br>
&lt;!DOCTYPE eterna-config PUBLIC "eterna" "http://eterna.googlecode.com/files/eterna_1_3.dtd"&gt;<br>
</code></pre>
这就是为什么用xml来配置，而不用json的原因。大部分的xml编辑工具，在有dtd或schema的时候，写xml就会变得很容易，而json还没有什么好的编辑工具。<br>
<br><br>


<h1>通用显示界面</h1>
最终的页面是通过jsp来显示了，但这里只需要一个jsp页面就行了，如例子中的view.jsp。<br>
<pre><code>&lt;%@ page session="false" contentType="text/html;charset=UTF-8" pageEncoding="GBK"%&gt;<br>
&lt;%@ page import ="self.micromagic.eterna.model.AppData,<br>
                  self.micromagic.app.WebApp,<br>
                  self.micromagic.eterna.view.ViewAdapter,<br>
                  self.micromagic.eterna.view.BaseManager"%&gt;<br>
&lt;%<br>
   String dataType = request.getParameter(ViewAdapter.DATA_TYPE);<br>
   if (ViewAdapter.DATA_TYPE_ONLYRECORD.equals(dataType)<br>
         || ViewAdapter.DATA_TYPE_ALL.equals(dataType))<br>
   {<br>
      ViewAdapter view = (ViewAdapter) request.getAttribute(WebApp.VIEW_TAG);<br>
      AppData data = (AppData) request.getAttribute(WebApp.APPDATA_TAG);<br>
      if (view != null)<br>
      {<br>
         view.printView(out, data);<br>
      }<br>
   }<br>
   else<br>
   {<br>
      String root = request.getContextPath();<br>
      int eternaId = BaseManager.createEternaId();<br>
%&gt;<br>
<br>
&lt;html&gt;<br>
&lt;head&gt;<br>
&lt;meta http-equiv="content-type" content="text/html; charset=utf-8"&gt;<br>
&lt;meta http-equiv="pragma" content="no-cache"&gt;<br>
&lt;script language="javascript"&gt;<br>
if (typeof _page_init == "undefined")<br>
{<br>
   window._page_init = {};<br>
   window._loadScript = function (src, scriptFlag, recall)<br>
   {<br>
      (function() {<br>
         var scriptObj = document.createElement('script');<br>
         scriptObj.type = 'text/javascript';<br>
         scriptObj.async = true;<br>
         scriptObj.src = src;<br>
         scriptObj.scriptFlag = scriptFlag;<br>
         var s = document.getElementsByTagName('script')[0];<br>
         s.parentNode.insertBefore(scriptObj, s);<br>
         if (scriptObj.readyState) //IE<br>
         {<br>
            scriptObj.onreadystatechange = function()<br>
            {<br>
               if (scriptObj.readyState == "complete" || scriptObj.readyState == "loaded")<br>
               {<br>
                   window._page_init[scriptObj.scriptFlag] = true;<br>
                   if (recall != null) recall();<br>
               }<br>
           };<br>
         }<br>
         else //Others<br>
         {<br>
            scriptObj.onload = function()<br>
            {<br>
                window._page_init[scriptObj.scriptFlag] = true;<br>
                if (recall != null) recall();<br>
            };<br>
         }<br>
      })();<br>
   };<br>
   window._loadEterna = function () {<br>
      jQuery.noConflict();<br>
      window._loadScript('&lt;%= root %&gt;/eterna/eterna.js', 'eterna');<br>
   };<br>
   (function() {<br>
      var styleObj = document.createElement('link');<br>
      styleObj.type = 'text/css';<br>
      styleObj.rel = 'stylesheet';<br>
      styleObj.href = '&lt;%= root %&gt;/res/sample.css';<br>
      var s = document.getElementsByTagName('script')[0];<br>
      s.parentNode.insertBefore(styleObj, s);<br>
   })();<br>
   window._loadScript('&lt;%= root %&gt;/eterna/jquery.js', 'jQuery', window._loadEterna);<br>
   window._loadScript('&lt;%= root %&gt;/My97DatePicker/WdatePicker.js', 'WdatePicker');<br>
}<br>
&lt;/script&gt;<br>
&lt;script language="javascript"&gt;<br>
<br>
function page_eterna_&lt;%= eternaId %&gt;()<br>
{<br>
<br>
&lt;%<br>
      ViewAdapter view = (ViewAdapter) request.getAttribute(WebApp.VIEW_TAG);<br>
      AppData data = (AppData) request.getAttribute(WebApp.APPDATA_TAG);<br>
      String width = null;<br>
      String height = null;<br>
      if (view != null)<br>
      {<br>
         width = view.getWidth();<br>
         height = view.getHeight();<br>
         out.print("var eternaData=");<br>
         view.printView(out, data);<br>
         out.print(";");<br>
      }<br>
<br>
      String pDebug = request.getParameter("debug");<br>
%&gt;<br>
<br>
var eterna_debug = &lt;%= pDebug != null ? pDebug : view != null ? view.getDebug() + "" : "0" %&gt;;<br>
var _eterna;<br>
<br>
this.initView = function()<br>
{<br>
&lt;%<br>
      if (view != null)<br>
      {<br>
%&gt;<br>
   var divObj = jQuery("#eternaShow_&lt;%= eternaId %&gt;");<br>
&lt;%<br>
         if (width != null)<br>
         {<br>
%&gt;<br>
   divObj.css("width", "&lt;%= width %&gt;");<br>
&lt;%<br>
         }<br>
         if (height != null)<br>
         {<br>
%&gt;<br>
   divObj.css("height", "&lt;%= height %&gt;");<br>
&lt;%<br>
         }<br>
%&gt;<br>
   _eterna = new Eterna(eternaData, eterna_debug, divObj)<br>
   _eterna.reInit();<br>
&lt;%<br>
      }<br>
%&gt;<br>
}<br>
<br>
} &lt;% /* end function page_eterna_XXX */ %&gt;<br>
&lt;/script&gt;<br>
&lt;style type="text/css"&gt;<br>
body {<br>
}<br>
&lt;/style&gt;<br>
&lt;/head&gt;<br>
&lt;body&gt;<br>
&lt;div id="eternaShow_&lt;%= eternaId %&gt;" class="eternaFrame"&gt;&lt;/div&gt;<br>
&lt;script language="javascript"&gt;<br>
function init_page_eterna_&lt;%= eternaId %&gt;()<br>
{<br>
   if (!window._page_init["jQuery"] || !window._page_init["eterna"])<br>
   {<br>
      setTimeout(init_page_eterna_&lt;%= eternaId %&gt;, 100);<br>
      return;<br>
   }<br>
   new page_eterna_&lt;%= eternaId %&gt;().initView();<br>
}<br>
setTimeout(init_page_eterna_&lt;%= eternaId %&gt;, 100);<br>
&lt;/script&gt;<br>
&lt;/body&gt;<br>
&lt;/html&gt;<br>
&lt;%<br>
   }<br>
%&gt;<br>
</code></pre>
此页面的功能有：<br>
<ol><li>根据view对象，生成json<br>
</li><li>根据json对象，生成界面<br>
</li><li>判断js文件是否已引用，不重复引用<br>
</li><li>给界面生成一个独立的命名空间，这样在portlet中就不会发生冲突<br>
<br><br>
如果你使用taglib，那这个页面代码能够更简单：<br>
<pre><code>&lt;%@ page session="false" contentType="text/html;charset=UTF-8"%&gt;&lt;%@<br>
    page import ="self.micromagic.eterna.view.impl.ViewTool"%&gt;&lt;%@<br>
    taglib prefix="e" uri="http://code.google.com/p/eterna" %&gt;&lt;%<br>
   String id = "_" + ViewTool.createEternaId();<br>
%&gt;&lt;e:init parentElement="eternaShow" divClass="eternaFrame" printHTML="2" suffixId="&lt;%= id %&gt;"&gt;<br>
&lt;e:res url="/eterna/jquery.js"/&gt;<br>
&lt;e:res url="/eterna/eterna.js"/&gt;<br>
&lt;e:res url="/My97DatePicker/WdatePicker.js"/&gt;<br>
&lt;e:res url="/res/sample.css" jsResource="false" charset="UTF-8"/&gt;<br>
&lt;/e:init&gt;<br>
</code></pre>
<code>&lt;%@ taglib prefix="e" uri="http://code.google.com/p/eterna" %&gt;</code>这段代码为对tablib的引用(当然需要先在web.xml中定义好uri)，“e:init”标签的功能同上面的那段jsp代码所实现的功能。<br>
<br><br></li></ol>


<h1>公共配置</h1>
公共配置文件为：micromagic_config.properties，此文件一般放在WEB-INF\classes目录下。<br>
里面的主要属性有以下几个：<br>
<ol><li>self.micromagic.eterna.digester.initfiles，全局配置文件，这些文件中的配置对象在各个子配置中都可以使用。<br>
</li><li>page.view，通用视图的jsp文件，一般设为/view.jsp，这样export的path属性就可以设为${page.view}，如果文件有变动就只需改变page.view的值，而不用改动所有export的path属性。