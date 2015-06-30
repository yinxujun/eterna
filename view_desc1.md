# 定义即参数 #
框架中定义的每一个界面元素都可以被看作是一个参数，都可以在使用时被替换。如我们定义了一个控件，“textList”：
```
<typical-component name="textList" type="div">
   <component name="div1" type="div">
      <component name="t1_1" type="span" comParam="text:'1'"/>
      <component name="t1_2" type="span" comParam="text:'2'"/>
      <component name="t1_3" type="span" comParam="text:'3'"/>
   </component>
   <component name="div2" type="div">
      <component name="t2_1" type="span" comParam="text:'a'"/>
      <component name="t2_2" type="span" comParam="text:'b'"/>
      <component name="t2_3" type="span" comParam="text:'c'"/>
   </component>
</typical-component>
```
此控件的显示效果如下：
```
123
abc
```
如果需要将b换成大写的，则可在使用时作如下修改：
```
<replacement name="list" baseComponentName="textList;t2_2">
   <component name="t2_2" type="span" comParam="text:'B'"/>
</replacement>
```
如果还需要让第二行居中，则可再对div2进行修改：
```
<replacement name="list" baseComponentName="textList;t2_2,div2">
   <component name="t2_2" type="span" comParam="text:'B'"/>
   <replacement name="div2" comParam="css:{'text-align':'center'}"/>
</replacement>
```
修改后的显示效果如下：
```
123
                                           aBc
```
如果需要将第二行完全换掉，改成一个超链接，则可对div2重新定：
```
<replacement name="list" baseComponentName="textList;div2">
   <component name="div2" type="a" comParam="text:'abc'"/>
</replacement>
```

# 定义即控件 #
定义的任何部分，都可以单独作为控件提取出来。还是上面定义的那个控件，如果需要将其第一行（即：div1）单独提取出来，则可按如下方式使用：
```
<replacement name="list" baseComponentName="textList:div1"/>
```
同样，如果还需要将第一个数字改成中文，可以继续对其替换，代码如下：
```
<replacement name="list" baseComponentName="textList:div1;t1_1">
   <component name="t1_1" type="span" comParam="text:'一'"/>
</replacement>
```
如果你需要使用在视图中定义的一个元素，可以使用如下方式：
```
<view name="view1">
   ...
   <component name="div" type="div">
      <component name="needCom" type="div">...</component>
   </component>
   ...
</view>
```
需要使用“view1”中，名称为“needCom”的元素：
```
<replacement name="list" baseComponentName="view:view1:needCom"/>
```