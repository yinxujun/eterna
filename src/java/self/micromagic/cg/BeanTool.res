# 获取map中的数据
## mapSet.getMapValue
${tmpStr} = ${prefixName}.length() == 0 ? "${pName}" : ${prefixName} + "${pName}";
${tmpObjName} = ${mapName}.get(${tmpStr});

# 检查数据是否为字符串数组且只有一个元素, 是的话取第一个字符串
## getFirstValue
if (${tmpObjName} != null && ${tmpObjName} instanceof String[])
{
   String[] arr = (String[]) ${tmpObjName};
   if (arr.length == 1)
   {
      ${tmpObjName} = arr[0];
   }
}


# 下面是设置属性值的代码

# 对基本类型通过属性进行设置
## mapSet.primitiveFieldSet
if (${tmpObjName} != null)
{
   try
   {
      ${declareType} v = ((${converterType}) ${converterName}).${converterMethod}(${tmpObjName});
      ${beanName}.${fieldName} = v;
      ${settedCountName}++;
   }
   catch (Throwable ex) {}
}

# 对基本类型通过方法进行设置
## mapSet.primitiveMethodSet
if (${tmpObjName} != null)
{
   try
   {
      ${declareType} v = ((${converterType}) ${converterName}).${converterMethod}(${tmpObjName});
      ${beanName}.${methodName}(v);
      ${settedCountName}++;
   }
   catch (Throwable ex) {}
}

# 对bean类型通过属性进行设置
## mapSet.beanTypeFieldSet
if (${tmpObjName} != null)
{
   if (${tmpObjName} instanceof ${className})
   {
      ${beanName}.${fieldName} = (${className}) ${tmpObjName};
      ${settedCountName}++;
   }
   else if (${tmpObjName} instanceof Map)
   {
      ${className} tb = new ${className}();
      int tempCount = BeanTool.setBeanValues(tb, (Map) ${tmpObjName});
      if (tempCount > 0)
      {
         ${beanName}.${fieldName} = tb;
         ${settedCountName} += tempCount;
      }
   }
}
else
{
   ${className} tb = new ${className}();
   int tempCount = BeanTool.setBeanValues(tb, ${mapName}, ${prefixName} + "${pName}.");
   if (tempCount > 0)
   {
      ${beanName}.${fieldName} = tb;
      ${settedCountName} += tempCount;
   }
}

# 对bean类型通过方法进行设置
## mapSet.beanTypeMethodSet
if (${tmpObjName} != null)
{
   if (${tmpObjName} instanceof ${className})
   {
      ${beanName}.${methodName}((${className}) ${tmpObjName});
      ${settedCountName}++;
   }
   else if (${tmpObjName} instanceof Map)
   {
      ${className} tb = new ${className}();
      int tempCount = BeanTool.setBeanValues(tb, (Map) ${tmpObjName});
      if (tempCount > 0)
      {
         ${beanName}.${methodName}(tb);
         ${settedCountName} += tempCount;
      }
   }
}
else
{
   ${className} tb = new ${className}();
   int tempCount = BeanTool.setBeanValues(tb, ${mapName}, ${prefixName} + "${pName}.");
   if (tempCount > 0)
   {
      ${beanName}.${methodName}(tb);
      ${settedCountName} += tempCount;
   }
}

# 对可转换的类型通过属性进行设置
## convertTypeFieldSet
if (${tmpObjName} != null)
{
   try
   {
      Object tObj = ${converterName}.convert(${tmpObjName});
      if (tObj != null)
      {
         ${beanName}.${fieldName} = (${className}) tObj;
         ${settedCountName}++;
      }
   }
   catch (Throwable ex) {}
}

# 对可转换的类型通过方法进行设置
## convertTypeMethodSet
if (${tmpObjName} != null)
{
   try
   {
      Object tObj = ${converterName}.convert(${tmpObjName});
      if (tObj != null)
      {
         ${beanName}.${methodName}((${className}) tObj);
         ${settedCountName}++;
      }
   }
   catch (Throwable ex) {}
}

# 对其他无法转换的类型通过属性进行设置
## otherTypeFieldSet
if (${tmpObjName} != null)
{
   if (${tmpObjName} instanceof ${className})
   {
      ${beanName}.${fieldName} = (${className}) ${tmpObjName};
      ${settedCountName}++;
   }
}

# 对其他无法转换的类型通过方法进行设置
## otherTypeMethodSet
if (${tmpObjName} != null)
{
   if (${tmpObjName} instanceof ${className})
   {
      ${beanName}.${methodName}((${className}) ${tmpObjName});
      ${settedCountName}++;
   }
}


# BeanMap对bean类型通过属性进行设置
## beanMap.beanTypeFieldSet
if (${tmpObjName} instanceof ${className})
{
   ${beanName}.${fieldName} = (${className}) ${tmpObjName};
   ${settedCountName}++;
}
else if (${tmpObjName} instanceof Map)
{
   ${className} tb;
   if (${oldValueName} != null && ${oldValueName} instanceof ${className})
   {
      tb = (${className}) ${oldValueName};
   }
   else
   {
      tb = new ${className}();
   }
   int tempCount = BeanTool.getBeanMap(tb).setValues((Map) ${tmpObjName});
   if (tempCount > 0)
   {
      ${beanName}.${fieldName} = tb;
      ${settedCountName} += tempCount;
   }
}
else if (${originObjName} instanceof Map)
{
   ${className} tb;
   if (${oldValueName} != null && ${oldValueName} instanceof ${className})
   {
      tb = (${className}) ${oldValueName};
   }
   else
   {
      tb = new ${className}();
   }
   int tempCount = BeanTool.getBeanMap(tb, ${prefixName} + "${pName}.")
         .setValues((Map) ${originObjName});
   if (tempCount > 0)
   {
      ${beanName}.${fieldName} = tb;
      ${settedCountName} += tempCount;
   }
}
else if (${originObjName} instanceof ResultRow)
{
   ${className} tb;
   if (${oldValueName} != null && ${oldValueName} instanceof ${className})
   {
      tb = (${className}) ${oldValueName};
   }
   else
   {
      tb = new ${className}();
   }
   int tempCount = BeanTool.getBeanMap(tb, ${prefixName} + "${pName}.")
         .setValues((ResultRow) ${originObjName});
   if (tempCount > 0)
   {
      ${beanName}.${fieldName} = tb;
      ${settedCountName} += tempCount;
   }
}

# BeanMap中对bean类型通过方法进行设置
## beanMap.beanTypeMethodSet
if (${tmpObjName} instanceof ${className})
{
   ${beanName}.${methodName}((${className}) ${tmpObjName});
   ${settedCountName}++;
}
else if (${tmpObjName} instanceof Map)
{
   ${className} tb;
   if (${oldValueName} != null && ${oldValueName} instanceof ${className})
   {
      tb = (${className}) ${oldValueName};
   }
   else
   {
      tb = new ${className}();
   }
   int tempCount = BeanTool.getBeanMap(tb).setValues((Map) ${tmpObjName});
   if (tempCount > 0)
   {
      ${beanName}.${methodName}(tb);
      ${settedCountName} += tempCount;
   }
}
else if (${originObjName} instanceof Map)
{
   ${className} tb;
   if (${oldValueName} != null && ${oldValueName} instanceof ${className})
   {
      tb = (${className}) ${oldValueName};
   }
   else
   {
      tb = new ${className}();
   }
   int tempCount = BeanTool.getBeanMap(tb, ${prefixName} + "${pName}.")
         .setValues((Map) ${originObjName});
   if (tempCount > 0)
   {
      ${beanName}.${methodName}(tb);
      ${settedCountName} += tempCount;
   }
}
else if (${originObjName} instanceof ResultRow)
{
   ${className} tb;
   if (${oldValueName} != null && ${oldValueName} instanceof ${className})
   {
      tb = (${className}) ${oldValueName};
   }
   else
   {
      tb = new ${className}();
   }
   int tempCount = BeanTool.getBeanMap(tb, ${prefixName} + "${pName}.")
         .setValues((ResultRow) ${originObjName});
   if (tempCount > 0)
   {
      ${beanName}.${methodName}(tb);
      ${settedCountName} += tempCount;
   }
}

# BeanMap中对基本类型通过属性进行设置
## beanMap.primitiveFieldSet
try
{
   ${declareType} v = ((${converterType}) ${converterName}).${converterMethod}(${tmpObjName});
   ${beanName}.${fieldName} = v;
   ${settedCountName}++;
}
catch (Throwable ex) {}

# BeanMap中对基本类型通过方法进行设置
## beanMap.primitiveMethodSet
try
{
   ${declareType} v = ((${converterType}) ${converterName}).${converterMethod}(${tmpObjName});
   ${beanName}.${methodName}(v);
   ${settedCountName}++;
}
catch (Throwable ex) {}



# 下面是获取属性值的代码

# 对基本类型通过属性进行获取
## primitiveFieldGet
return new ${wrapName}(${beanName}.${fieldName});

# 对基本类型通过方法进行获取
## primitiveMethodGet
return new ${wrapName}(${beanName}.${methodName}());

# 对其他类型通过属性进行获取
## otherTypeFieldGet
return ${beanName}.${fieldName};

# 对其他类型通过方法进行获取
## otherTypeMethodGet
return ${beanName}.${methodName}();

# 对数组类型通过属性进行获取
## arrayTypeFieldGet
if (${indexs} == null || ${indexs}.length == 0)
{
   return ${beanName}.${fieldName};
}
else
{
   return this.processerArr[${indexs}.length - 1].getBeanValue(${cellDescriptor}, ${indexs},
         ${beanName}.${fieldName}, ${prefixName}, ${beanMap});
}

# 对数组类型通过方法进行获取
## arrayTypeMethodGet
if (${indexs} == null || ${indexs}.length == 0)
{
   return ${beanName}.${methodName}();
}
else
{
   return this.processerArr[${indexs}.length - 1].getBeanValue(${cellDescriptor}, ${indexs},
         ${beanName}.${methodName}(), ${prefixName}, ${beanMap});
}


# 对集合容器类型通过属性进行获取
## collectionTypeFieldGet
if (${indexs} == null || ${indexs}.length == 0)
{
   return ${beanName}.${fieldName};
}
else
{
   Collection c = ${beanName}.${fieldName};
   ${getCollectionIndexCode}
   return null;
}

# 对集合容器类型通过方法进行获取
## collectionTypeMethodGet
if (${indexs} == null || ${indexs}.length == 0)
{
   return ${beanName}.${methodName}();
}
else
{
   Collection c = ${beanName}.${methodName}();
   ${getCollectionIndexCode}
   return null;
}

# 对集合容器类型中的值进行获取(集合容器的变量名为 c)
## collectionTypeIndexGet
if (c == null)
{
   return null;
}
if (c instanceof List)
{
   return ((List) c).get(${indexs}[0]);
}
else
{
   Iterator itr = c.iterator();
   int tmpI = 0;
   for (; itr.hasNext() && tmpI < ${indexs}[0]; tmpI++, itr.next());
   if (tmpI == ${indexs}[0] && itr.hasNext())
   {
      return itr.next();
   }
}

# 对基本类型的数组进行获取
## arrayTypePrimitiveGet
return new ${wrapName}(${arrayName}${arrayVisitList});

# 对其他类型的数组进行获取
## arrayTypeOtherGet
return ${arrayName}${arrayVisitList};

