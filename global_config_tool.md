# 全局配置文件 #
默认的全局配置文件有两个，一个是“self/micromagic/eterna/share/eterna\_share.xml”后面简称：eterna\_share，里面定义了一些基本的功能对象；另一个是“eterna\_global.xml”后面简称：eterna\_global,里面定义了一些常用的方法及控件。

## eterna\_share中的builder介绍 ##
builder是为search在搜索时构造条件单元的工具，定义在condition-property节点中。使用方式如下：
```
<search name="searchName" queryName="queryName">
   <condition-propertys>
      <condition-property name="conditionName" colType="String" defaultBuilder="builderName"/>
      ...
   </condition-propertys>
</search>
```
上面代码中的`defaultBuilder="builderName"`部分就是设置条件项使用什么条件构造工具。

### isNull ###
构造判断是否为空的条件，构造的语句如下：
```
columnName IS NULL
```

### notNull ###
构造判断是否不为空的条件，构造的语句如下：
```
columnName IS NOT NULL
```

### checkNull ###
根据传入的参数值构造判断是否为空或不为空的条件，构造的语句如下：
```
当传入的值为“1”时
columnName IS NULL
当传入的值不为“1”时
columnName IS NOT NULL
```

### equal ###
构造一个判断相等的条件，构造的语句如下：
```
columnName = ?
```

### notEqual ###
构造一个判断不相等的条件，构造的语句如下：
```
columnName <> ?
```

### include ###
构造一个判断是否包含某个字符串的条件，构造的语句如下：
```
columnName LIKE %?%
或，当传入的字符串有通配符时
columnName LIKE %?% escape '\'
如：参数值为“15%”，会被改为“15\%”
```

### beginWith ###
构造一个判断是否以某个字符串起始的条件，构造的语句如下：
```
columnName LIKE ?%
或，当传入的字符串有通配符时
columnName LIKE ?% escape '\'
如：参数值为“15%”，会被改为“15\%”
```

### endWith ###
构造一个判断是否以某个字符串结束的条件，构造的语句如下：
```
columnName LIKE %?
或，当传入的字符串有通配符时
columnName LIKE %? escape '\'
如：参数值为“15%”，会被改为“15\%”
```

### match ###
构造一个可输入统配符的字符串匹配判断条件，构造的语句如下：
```
columnName LIKE ? escape '\'
当传入的字符串有通配符时不会进行修改
如：参数值为“15%”，会被改为“15\%”
```

### large ###
构造一个判断是否大于的条件，构造的语句如下：
```
columnName > ?
```

### below ###
构造一个判断是否小于的条件，构造的语句如下：
```
columnName < ?
```

### notBelow ###
构造一个判断是否大于等于的条件，构造的语句如下：
```
columnName >= ?
```

### notLarge ###
构造一个判断是否小于等于的条件，构造的语句如下：
```
columnName <= ?
```

## eterna\_global中的builder介绍 ##

### util.IN ###
构造一个判断是否存在于一个集合中的条件，构造的语句如下：
```
columnName in (?, ?, ?)
如传入的值为“a, b, c”
字符串的分割符可以是“,”或“;”，也可以是全角的“，”或“；”
```

### util.Save ###
不构造任何条件，仅仅只将当前的条件值保存下来。

## eterna\_share中的vpc介绍 ##
vpc即value preparer creater的简称，用于构造数据值的准备者，其最主要的工作就是数据类型转换，定义在parameter及condition-property节点中。使用方式如下：
```
<search name="searchName" queryName="queryName">
   <condition-propertys>
      <condition-property name="conditionName" colType="String" vpcName="vpcName"/>
      ...
   </condition-propertys>
</search>

<query name="queryName">
   ...
   <parameters>
      <parameter name="paramName" type="int" vpcName="vpcName"/>
      ...
   </parameters>
   ...
</query>

<update name="updateName">
   ...
   <parameters>
      <parameter name="paramName" type="int" vpcName="vpcName"/>
      ...
   </parameters>
</update>
```
上面代码中的`vpcName="vpcName"`部分就是设置数据值的准备者。

### str\_begin.vpc ###
构造一个判断是否以某个字符串起始的条件数据，即会在字符串的结束处加上一个统配符“%”。

### str\_end.vpc ###
构造一个判断是否以某个字符串结束的条件数据，即会在字符串的开始处加上一个统配符“%”。

### str\_include.vpc ###
构造一个判断是否包含某个字符串的条件数据，即会在字符串的开始及结束处各加上一个统配符“%”。

## eterna\_global中的format介绍 ##
format是为查询结果中的数据进行格式化输出的工具，定义在reader节点中。使用方式如下：
```
<query name="queryName">
   ...
   <readers>
      <reader name="readerName" type="double" format="formatName"/>
      ...
   </readers>
   ...
</query>
```
上面代码中的`format="formatName"`部分就是设置格式化输出的工具。

### currency ###
将一个数字格式化成保留2位小数的格式。
```
10 -> 10.00
0.618 -> 0.62
```

### currency2 ###
将一个数字格式化成保留2位小数并加上千分位的格式。
```
1000 -> 1,000.00
0.618 -> 0.62
```

### boolean.format ###
如果值为ture或非0，则格式化的值为“是”，反之则值为“否”。
```
1 -> 是
true -> 是
0 -> 否
false -> 否
```

### date.format ###
将一个日期或长整型数据格式化为4位年2位月日的格式，如：“2012-01-15”。

### datetime.format ###
将一个日期或长整型数据格式化为4位年2位月日+时分秒的格式，如：“2012-01-15 23:04:59”。