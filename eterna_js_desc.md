# Eterna对象中的方法及属性 #
Eterna对象是页面中的核心对象，在view模块的脚本中，此对象的引用名称为“`_eterna`”。下面一一介绍此对象中的属性及方法。

## eterna\_debug ##
```
类型：属性。
说明：当前Eterna对象的debug等级。
```

## eternaVersion ##
```
类型：属性。
说明：当前Eterna对象的版本号。
```

## rootWebObj ##
```
类型：属性。
说明：当前Eterna对象的作用域，是一个jQuery对象，view模块中生成的所有界面对象都在此对象中。
```

## egTemp ##
```
类型：方法。
参数列表
temp：一个代表上下文环境变量json对象，此参数为可选。
说明：
当给出了temp参数时，此方法是将temp中的数据复制到上下文环境变量“eg_temp”中。
当未给此temp参数时，此方法是将上下文环境变量“eg_temp”中的数据复制一份并返回。
注：
此方法只会复制第一层的属性，对于属性内的值不会复制。
如果需要复制对象类的属性可以使用下面提到的两个方法。
```
例子：
```
eg_temp.name = "1";
eg_temp.param = {a:10};
// 复制当前的上下文环境
var tmp = _eterna.eg_temp();
eg_temp.name = "2";
eg_temp.param.a = 20;
alert(eg_temp.name);    // <- 打印的值为2
alert(eg_temp.param.a); // <- 打印的值为20
// 恢复当前的上下文环境
_eterna.eg_temp(tmp);
alert(eg_temp.name);    // <- 打印的值为1
alert(eg_temp.param.a); // <- 打印的值仍然为20
```

## egTempParam ##
```
类型：方法。
参数列表
copy：可选参数，是否需要进行克隆。默认值为false，不进行克隆。
说明：
获取上下文环境变量eg_temp中的param属性，如果该属性不存在，则生成一个空的json对象。
如果该属性存在，不需要克隆时则直接返回，需要克隆时则克隆后再返回。
返回的值和eg_temp.param是同一个引用，即：_eterna.egTempParam() === eg_temp.param。
```
例子：
```
eg_temp.name = "1";
eg_temp.param = {a:10};
// 复制当前的上下文环境
var tmp = _eterna.eg_temp();
eg_temp.name = "2";
var param = _eterna.egTempParam(true);
param.a = 20;
alert(eg_temp.name);    // <- 打印的值为2
alert(eg_temp.param.a); // <- 打印的值为20
// 恢复当前的上下文环境
_eterna.eg_temp(tmp);
alert(eg_temp.name);    // <- 打印的值为1
alert(eg_temp.param.a); // <- 打印的值为10
```

## egTempData ##
```
类型：方法。
参数列表
copy：可选参数，是否需要进行克隆。默认值为false，不进行克隆。
说明：
获取上下文环境变量eg_temp中的tempData属性，如果该属性不存在，则生成一个空的json对象。
如果该属性存在，不需要克隆时则直接返回，需要克隆时则克隆后再返回。
返回的值和eg_temp.tempData是同一个引用，即：_eterna.egTempData() === eg_temp.tempData。
```

## cloneJSON ##
```
类型：方法。
参数列表
obj：需要被克隆的json对象，此对象的类型必须是object，即：typeof obj == "object"。
否则不进行克隆，直接返回原值，如：string、number这些类型。
说明：
克隆一个json对象，并返回。
```

## getRemoteJSON ##
```
类型：方法。
参数列表
url：获取json对象请求的url地址。
params：需要传递的参数，可以是个json对象，也可以是一个form元素的jQuery对象。
async：可选参数，是否异步调用，默认值为false，采用同步调用的方式。
successFunction：请求成功后的回调函数，详见后面的说明。
completeFunction：请求完成后的回调函数，详见后面的说明。
说明：
获取一个远程的json对象，即通过给出的url地址获取数据，并构造成一个json对象。
如果是同步调用，此方法将获取到的json对象，如果获取过程出现错误，则返回null。
如果是异步调用，此方式只是返回一个函数对象，json对象需要通过设置的回调函数获得。

回调函数successFunction
此函数会在获取数据成功后触发，其被调用时，传入的参数有两个：
data：获取的json对象，如果获取的数据无法转成json对象，则此参数的值为null。
status：状态信息。

回调函数completeFunction
此函数会在获取数据的请求结束后出发，无论获取数据成功还是失败，其被调用时，传入的参数有两个：
request：发起ajax请求的request对象。
status：状态信息。
eterna：调用当前方法的Eterna对象。
```

## queryWebObj ##
```
类型：方法。
参数列表
queryStr：查询字符串，和jQuery的查询字符串相同。
container：可选参数，在哪个节点的范围内查询，默认值为当前的rootWebObj。
说明：
根据查询字符串获取符合条件的界面元素。
```

## getWebObj ##
```
类型：方法。
参数列表
id：要获取的界面元素的id。
container：可选参数，在哪个节点的范围内查找，默认值为当前的rootWebObj。
index：可选参数，如果有多个同名id的元素，可通过此参数指定获取哪一个，如果未给出此参数，
       在有多个同名id元素的情况下，将返回一个jQuery的元素列表。
说明：
根据id获取界面元素。
```
例子：
```
_eterna.getWebObj("userId");
_eterna.getWebObj("userId", 0);
_eterna.getWebObj("userId", _eterna.getWebObj("usersDiv"));
_eterna.getWebObj("userId", _eterna.getWebObj("usersDiv"), 0);
```

## reloadWebObj ##
```
类型：方法。
参数列表
webObj：需要重新载入的界面元素，可以是一个jQuery的元素对象，也可以是界面元素的id。
说明：
重新载入一个界面元素，即重新生成这个界面元素，将原来的替换掉。
```

## openWindow ##
```
类型：方法。
参数列表
url：新窗口的url。
name：可选参数，新窗口的名称。
param：可选参数，新窗口的参数。
lock：可选参数，新窗口打开后是否要锁定当前窗口，默认值为false，不锁定。
closeFn：可选参数，新窗口关闭后的回调函数，只有在lock参数为true时，此值才有效。
说明：
打开一个新窗口，此方法和window.open的区别在于，在当前eterna对象所控制的区域被重新载入，
或当前窗口被关闭时，会同时将这些被打开的窗口关闭。
```

## newComponent ##
```
类型：方法。
参数列表
tName：控件模板的名称或控件模板对象。
parent：可选参数，新生成的控件放在哪个节点下，可以是界面元素的id，也可以是界面元素对象，
        也可以是界面元素的jQuery对象，默认值为rootWebObj。
说明：
创建一个界面元素，并将其返回。
```
例子：
```
var tObj = _eterna.newComponent({$typical:my_text});
tObj.val("test");
```

## doVisit ##
```
类型：方法。
参数列表
url：需要访问的url地址。
说明：
执行一次页面访问，会根据当前eterna对象中cache.useAJAX的设置，进行页面跳转或ajax方式访问。
```


# 其他方法 #
一些工具方法的介绍。

## ef\_formatNumber ##
```
说明：
对一个数字进行格式化输出。
参数列表
num：需要被格式化的数字，也可以是一个能被转成数字的字符串。
pattern：格式化的模式字符串。
```
例子：
```
ef_formatNumber(1000.267, "#,##0.00");     // 结果为：1,000.26
// 注：对小数的处理是舍去，如果需要四舍五入的话，可以采用如下方式
ef_formatNumber(1000.267 + 0.005, "#,##0.00"); 
```

## ef\_isEmpty ##
```
说明：
判断给出的字符串是否为null或是否为空字符串。
参数列表
str：被判断的字符串。
```
例子：
```
ef_isEmpty(null);   // 结果为：true
ef_isEmpty("");     // 结果为：true
ef_isEmpty(" ");    // 结果为：false
ef_isEmpty("a");    // 结果为：false
```

## eterna\_registerStaticInitFn ##
```
说明：
注册一个静态的初始化方法，在eterna对象所控制的区域中，有对象被重新加载时，会触发此方法，
并会将重新加载完成的对象作为第一个参数传入。
参数列表
fn：需要被注册的方法。
priority：可选参数，调用此方法的优先级，0最高，100（默认值）最低，
          如果为-1则表示删除已注册的方法。
```
例子：
```
function myInit(theObj)
{
   ...
}

eterna_registerStaticInitFn(myInit);

// 当调用用eterna对象的reloadWebObj(testObj)后，就会触发方法调用myInit(testObj)
```

## eterna\_addWillInitObj ##
```
说明：
注册一个需要等待初始化的对象，在eterna对象所控制的区域初始化完成后，会触发此对象的willInit事件。
参数列表
obj：需要被注册的对象。
priority：可选参数，触发事件的优先级，0最高，100（默认值）最低。
```
例子：
```
<component name="1" type="a" comParam="attr:{id:'a1'}">
   <init-script>var obj = _eterna.getWebObj("a2");</init-script>
</component>
<component name="1" type="a" comParam="attr:{id:'a2'}"/>
```
上面这段代码中，是无法获取a2这个控件的，因为这时候a2这个控件还没有生成，处理方式可改为：
```
<component name="1" type="a" comParam="attr:{id:'a1'}">
   <init-script>eterna_addWillInitObj(webObj, 0);</init-script>
   <events>
      <event name="willInit">
         var obj = _eterna.getWebObj("a2");
      </event>
   </events>
</component>
```
在不需要设置优先级的情况下，可以改为：
```
<component name="1" type="a" comParam="attr:{id:'a1'}">
   <events>
      <event name="willInit" scriptParam="autoInit">
         var obj = _eterna.getWebObj("a2");
      </event>
   </events>
</component>
```
可以不需要调用eterna\_addWillInitObj，但需要在event节点中加上`scriptParam="autoInit"`。