# 全局配置文件 #
默认的全局配置文件有两个，一个是“self/micromagic/eterna/share/eterna\_share.xml”后面简称：eterna\_share，里面定义了一些基本的功能对象；另一个是“eterna\_global.xml”后面简称：eterna\_global,里面定义了一些常用的方法及控件。

## eterna\_global中方法介绍 ##

### invertSelect ###
```
说明：
对一组checkbox进行反选或全选操作。
参数列表
objs：通过jQuery筛选的checkbox列表，或者是checkbox的id。
container：当第一个参数为checkbox的id，此参数表示在哪个范围内筛选。
selectAll：是否为全选模式，ture为全选，false（默认值）为反选。
```
例子：
```
// 对id为tmp的一组checkbox进行反选
{$ef:invertSelect}("tmp");
// 对id为tmp的一组checkbox进行全选
{$ef:invertSelect}("tmp", true);
// 对optTable对象中，id为tmp的一组checkbox进行全选
{$ef:invertSelect}("tmp", _eterna.getWebObj("optTable"), true);
```

### checkSelected ###
```
说明：
判断一组checkbox或radion中是否有选中。
参数列表
id：需要被判断的一组checkbox或radio的id。
msg：当没有被选中的对象时，显示的提示信息。
container：在哪个范围内筛选。
returnSelected：是否返回被选中的列表而不是true或false。
```
例子：
```
// 对id为tmp的一组checkbox或radio进行判断是否选中
if ({$ef:checkSelected}("tmp", "请至少选择一条记录！"))
{
   ...
}
// 对id为tmp的一组checkbox或radio进行判断是否选中，并返回选中的列表
var objs = {$ef:checkSelected}("tmp", "请先选择！", true);
if (objs != null)
{
   ...
}
```

### triggerEvents ###
```
说明：
触发某个对象下的所有指定事件。
注：被触发的事件必须是定义过的事件。如a的click事件，虽然a有click事件，但必须有自定义的click事件，否则不会触发。
参数列表
rootObj：通过jQuery获取的某个对象，将触发此对象下所有控件的指定事件。
eventName：需要被触发的事件名称。
```
例子：
```
{$ef:triggerEvents}(_eterna.getWebObj("checkboxDiv"), "click");
```

### checkForm ###
```
说明：
触发一个form对象中的所有控件的check事件，并返回校验是否通过。如果未通过且有出错信息，则会通过alert方法提示这些信息。
参数列表
formObj：通过jQuery获取的表单对象。
```
例子：
```
if ({$ef:checkForm}(_eterna.getWebObj("formId")))
{
   // 执行submit
}
```

### getData\_value ###
```
说明：
获取结果集中的值。
参数列表
dataName：结果集名称或结果集对象。
srcName：需要获取结果集中的哪个属性。
index：需要获取结果集中的第几条记录的值(第一行为0 第二行为1 ...)，如果为单行结果集不需要此参数。
```
例子：
```
// 获取单行结果集
var v = {$ef:getData_value}("userInfo", "id");
// 获取多行结果集
var v = {$ef:getData_value}("userList", "name", 0);
```

### findData\_value ###
```
说明：
获取结果集中的值。此方法和getData_value效果相同，但这个方法的参数可动态选择，其他参数会取环境变量(eg_temp)中的值
参数列表
dataName：结果集名称或结果集对象。
srcName：需要获取结果集中的哪个属性。
index：需要获取结果集中的第几条记录的值(第一行为0 第二行为1 ...)，如果为单行结果集不需要此参数。
```
例子：
```
如：环境变量中 dataName = "userList", srcName = "sex", index = 1
// 当前行id的值
var v = {$ef:findData_value}("id");
// 当下一行sex的值
var v = {$ef:findData_value}(eg_temp.index + 1);
// 当下一行id的值
var v = {$ef:findData_value}("id", eg_temp.index + 1);
// 获取其他结果集的值
var v = {$ef:findData_value}("other", "name", 0);
```

### getData\_row ###
```
说明：
将多行结果集中的某一行转换为单行结果集。
参数列表
dataName：结果集名称或结果集对象。
index：需要将第几条行的值(第一行为0 第二行为1 ...)转换为单行结果集。
```
例子：
```
// 将第一行转换为单行结果集
var row = {$ef:getData_row}("userInfo", 0);
```

### findData\_row ###
```
说明：
将多行结果集中的某一行转换为单行结果集。此方法和getData_row效果相同，但这个方法的参数可动态选择，其他参数会取环境变量(eg_temp)中的值
参数列表
dataName：结果集名称或结果集对象。
index：需要将第几条行的值(第一行为0 第二行为1 ...)转换为单行结果集。
```
例子：
```
如：环境变量中 dataName = "userList", index = 1
// 将下一行转换为单行结果集
var row = {$ef:findData_row}(eg_temp.index + 1);
// 将其他结果集的当前行转换为单行结果集
var row = {$ef:findData_row}("other");
// 将其他结果集的下一行转换为单行结果集
var row = {$ef:findData_row}("other", eg_temp.index + 1);
```