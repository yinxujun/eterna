
package self.micromagic.cg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import self.micromagic.util.StringTool;
import self.micromagic.util.Utility;
import self.micromagic.util.IntegerRef;
import self.micromagic.util.StringAppender;

/**
 * 一个类的自动编译及生成工具.
 */
public class ClassGenerator
{
   /**
    * 类的名称变量名. 在代码内部, 需要构造类本身或写构造函数时, 需要用到的类名. <p>
    * 如：
    * 构造函数   public ${thisName}()
    * 定义本类   ${thisName} value = new ${thisName}();
    */
   public static final String THIS_NAME = "thisName";

   /**
    * 用于记录日志.
    */
   public static final Log log = Utility.createLog("cg");

   /**
    * 设置对代码编译的类型.
    */
   public static final String COMPILE_TYPE_PROPERTY = "eterna.compile.type";

   /**
    * 设置是否要输出代码动态编译相关的日志信息.
    * 可设置的值如下:
    * 1. 只记录出错信息
    * 2. 记录生成的代码信息
    */
   public static final String COMPILE_LOG_PROPERTY = "eterna.compile.log";

   /**
    * 已注册的类生成工具的缓存.
    */
   private static final Map cgCache = new HashMap();

   static int COMPILE_LOG_TYPE = 1;
   static
   {
      try
      {
         Utility.addFieldPropertyManager(COMPILE_LOG_PROPERTY, ClassGenerator.class, "COMPILE_LOG_TYPE", "1");
      }
      catch (Throwable ex) {}
      registerCG("ant", new AntCG());
      registerCG("javassist", new JavassistCG());
   }

   /**
    * 注册一个类生成工具.
    *
    * @param name   名称
    * @param cg     类生成工具的实现
    */
   public static void registerCG(String name, CG cg)
   {
      cgCache.put(name, cg);
   }

   private String className;
   private ClassLoader classLoader;
   private Class superClass;
   private List interfaces = new ArrayList();
   private Set importPackages = new HashSet();
   private Map classPathCache = new HashMap();
   private List fields = new ArrayList();
   private List constructors = new ArrayList();
   private List methods = new ArrayList();
   private String compileType;

   /**
    * 获取本代码的类名.
    */
   public String getClassName()
   {
      return this.className;
   }

   /**
    * 设置本代码的类名.
    */
   public void setClassName(String className)
   {
      if (!StringTool.isEmpty(className))
      {
         this.className = className;
      }
   }

   /**
    * 获得生成类是使用的<code>ClassLoader</code>.
    */
   public ClassLoader getClassLoader()
   {
      if (this.classLoader == null)
      {
         return this.getClass().getClassLoader();
      }
      return this.classLoader;
   }

   /**
    * 设置生成类是使用的<code>ClassLoader</code>.
    */
   public void setClassLoader(ClassLoader classLoader)
   {
      this.classLoader = classLoader;
   }

   /**
    * 设置本代码需要继承的类.
    */
   public void setSuperClass(Class superClass)
   {
      this.superClass = superClass;
   }

   /**
    * 获得继承的类.
    */
   public Class getSuperClass()
   {
      return superClass;
   }

   /**
    * 添加本代码需要实现的接口.
    */
   public void addInterface(Class anInterface)
   {
      if (anInterface != null)
      {
         this.interfaces.add(anInterface);
      }
   }

   /**
    * 获得需要实现的接口列表.
    */
   public Class[] getInterfaces()
   {
      return (Class[]) this.interfaces.toArray(new Class[this.interfaces.size()]);
   }

   /**
    * 添加需要引用的包.
    */
   public void importPackage(String packageName)
   {
      if (!StringTool.isEmpty(packageName))
      {
         this.importPackages.add(packageName);
      }
   }

   /**
    * 获得需要引用的包列表.
    */
   public String[] getPackages()
   {
      String[] arr = new String[this.importPackages.size()];
      return (String[]) this.importPackages.toArray(arr);
   }

   /**
    * 添加一个读取类的路径.
    */
   public void addClassPath(Class pathClass)
   {
      if (pathClass != null && !this.classPathCache.containsKey(pathClass.getClassLoader()))
      {
         this.classPathCache.put(pathClass.getClassLoader(), pathClass);
      }
   }

   /**
    * 获得需要读取类的路径列表.
    */
   public Class[] getClassPaths()
   {
      Collection values = this.classPathCache.values();
      return (Class[]) values.toArray(new Class[values.size()]);
   }

   /**
    * 添加一个属性代码.
    */
   public void addField(String field)
   {
      if (!StringTool.isEmpty(field))
      {
         this.fields.add(field);
      }
   }

   /**
    * 获得属性代码列表.
    */
   public String[] getFields()
   {
      if (this.getClassName() == null)
      {
         return (String[]) this.fields.toArray(new String[this.fields.size()]);
      }
      else
      {
         String[] arr = new String[this.fields.size()];
         this.fields.toArray(arr);
         Map map = new HashMap(2);
         map.put(THIS_NAME, getConstructorName(this.getClassName()));
         for (int i = 0; i < arr.length; i++)
         {
            arr[i] = Utility.resolveDynamicPropnames(arr[i], map, true);
         }
         return arr;
      }
   }

   /**
    * 添加一个构造方法代码.
    * 代码中构造函数的名称可以用"${thisName}"代替.
    * @see #THIS_NAME
    */
   public void addConstructor(String constructor)
   {
      if (!StringTool.isEmpty(constructor))
      {
         this.constructors.add(constructor);
      }
   }

   /**
    * 获得构造方法代码列表.
    */
   public String[] getConstructors()
   {
      if (this.getClassName() == null)
      {
         throw new IllegalArgumentException("The class name hasn't bean setted .");
      }
      String[] arr = new String[this.constructors.size()];
      this.constructors.toArray(arr);
      Map map = new HashMap(2);
      map.put(THIS_NAME, getConstructorName(this.getClassName()));
      for (int i = 0; i < arr.length; i++)
      {
         arr[i] = Utility.resolveDynamicPropnames(arr[i], map, true);
      }
      return arr;
   }

   /**
    * 添加一个方法代码.
    */
   public void addMethod(String methodCode)
   {
      if (!StringTool.isEmpty(methodCode))
      {
         this.methods.add(methodCode);
      }
   }

   /**
    * 获得方法代码列表.
    */
   public String[] getMethods()
   {
      if (this.getClassName() == null)
      {
         return (String[]) this.methods.toArray(new String[this.methods.size()]);
      }
      else
      {
         String[] arr = new String[this.methods.size()];
         this.methods.toArray(arr);
         Map map = new HashMap(2);
         map.put(THIS_NAME, getConstructorName(this.getClassName()));
         for (int i = 0; i < arr.length; i++)
         {
            arr[i] = Utility.resolveDynamicPropnames(arr[i], map, true);
         }
         return arr;
      }
   }

   /**
    * 获得对本代码的编译方式.
    */
   public String getCompileType()
   {
      if (this.compileType == null)
      {
         return Utility.getProperty(COMPILE_TYPE_PROPERTY, "javassist");
      }
      return this.compileType;
   }

   /**
    * 设置对本代码的编译方式.
    */
   public void setCompileType(String compileType)
   {
      this.compileType = compileType;
   }

   /**
    * 根据设置的代码生成一个类.
    */
   public Class createClass()
   {
      if (this.getClassName() == null)
      {
         throw new IllegalArgumentException("The class name hasn't bean setted .");
      }
      String type = this.getCompileType();
      try
      {
         CG cg = (CG) cgCache.get(type.toLowerCase());
         if (cg != null)
         {
            return cg.createClass(this);
         }
      }
      catch (Exception ex)
      {
         throw new RuntimeException(ex);
      }
      throw new IllegalArgumentException("Error compile type:" + type + ".");
   }

   /**
    * bean处理类的id
    */
   private static volatile int BEAN_PROCESSER_ID = 1;

   /**
    * 创建一个类生成工具.
    *
    * @param baseClass       生成的类所使用的基础类,
    *                        会使用此类的ClassLoader来载入新生成的类
    * @param interfaceClass  需要实现的接口
    * @param imports         需要引用的包列表
    * @return  <code>ClassGenerator</code>的实例.
    */
   public static ClassGenerator createClassGenerator(Class baseClass, Class interfaceClass,
         String[] imports)
   {
      return createClassGenerator(null, baseClass, interfaceClass, imports);
   }

   /**
    * 创建一个类生成工具. <p>
    * 新的类名为：eterna.[baseClass]$suffix$$EBP_[序列号]
    *
    * @param suffix          生成类名的后缀
    * @param baseClass       生成的类所使用的基础类,
    *                        会使用此类的ClassLoader来载入新生成的类
    * @param interfaceClass  需要实现的接口
    * @param imports         需要引用的包列表
    * @return  <code>ClassGenerator</code>的实例.
    */
   public static ClassGenerator createClassGenerator(String suffix, Class baseClass, Class interfaceClass,
         String[] imports)
   {
      ClassGenerator cg = new ClassGenerator();
      cg.addClassPath(baseClass);
      cg.addClassPath(interfaceClass);
      cg.addClassPath(ClassGenerator.class);
      String tmpSuffix;
      synchronized (ClassGenerator.class)
      {
         if (StringTool.isEmpty(suffix))
         {
            tmpSuffix = "$$EBP_" + (BEAN_PROCESSER_ID++);
         }
         else
         {
            tmpSuffix = "$" + suffix + "$$EBP_" + (BEAN_PROCESSER_ID++);
         }
      }
      cg.setClassName("eterna." + baseClass.getName() + tmpSuffix);
      cg.addInterface(interfaceClass);
      if (imports != null)
      {
         for (int i = 0; i < imports.length; i++)
         {
            if (!StringTool.isEmpty(imports[i]))
            {
               cg.importPackage(imports[i]);
            }
         }
      }
      return cg;
   }

   /**
    * 获取给定类的包路径字符串.
    */
   public static String getPackageString(Class c)
   {
      String cName = c.getName();
      int lastIndex = cName.lastIndexOf('.');
      if (lastIndex == -1)
      {
         return "";
      }
      return cName.substring(0, lastIndex);
   }

   /**
    * 获取给定类名的包路径字符串.
    */
   public static String getPackageString(String className)
   {
      if (className == null)
      {
         return "";
      }
      int lastIndex = className.lastIndexOf('.');
      if (lastIndex == -1)
      {
         return "";
      }
      return className.substring(0, lastIndex);
   }

   /**
    * 获取给定类名的构造函数名称.
    */
   public static String getConstructorName(String className)
   {
      int lastIndex = className.lastIndexOf('.');
      if (lastIndex == -1)
      {
         return className;
      }
      return className.substring(lastIndex + 1);
   }

   /**
    * 获取数组的元素类型.
    *
    * @param arrayClass  数组类
    * @param levelRef    返回数组的维度
    */
   public static Class getArrayElementType(Class arrayClass, IntegerRef levelRef)
   {
      if (arrayClass == null || !arrayClass.isArray())
      {
         return null;
      }
      int level = 0;
      Class tmpClass = arrayClass;
      while (tmpClass.isArray())
      {
         tmpClass = tmpClass.getComponentType();
         level++;
      }
      if (levelRef != null)
      {
         levelRef.value = level;
      }
      return tmpClass;
   }

   /**
    * 获得一个类的类名.
    * 此方法会根据不同的JDK版本调用不同的方法.
    * 如：1.5 以上的需要使用getCanonicalName.
    */
   public static String getClassName(Class c)
   {
      if (c.isArray())
      {
         IntegerRef level = new IntegerRef();
         Class type = getArrayElementType(c, level);
         String nameStr = nameAccessor.getName(type);
         StringAppender arrVL = StringTool.createStringAppender(nameStr.length() + level.value * 2);
         arrVL.append(nameStr);
         for (int i = 0; i < level.value; i++)
         {
            arrVL.append("[]");
         }
         return arrVL.toString();
      }
      return nameAccessor.getName(c);
   }

   /**
    * 初始化一个类名的访问者.
    */
   private static NameAccessor nameAccessor;
   static
   {
      try
      {
         Class c = Class.forName("self.micromagic.cg.ClassGenerator$ClassCanonicalNameAccessor");
         nameAccessor = (NameAccessor) c.newInstance();
         nameAccessor.getName(Map.Entry.class);
      }
      catch (Throwable ex)
      {
         // 如果出现异常, 这可能是jdk版本小于1.5, 使用getName方法来获取类名
         nameAccessor = new ClassNameAccessor();
      }
   }

   interface NameAccessor
   {
      String getName(Class c);

   }

   static class ClassNameAccessor
         implements NameAccessor
   {
      public String getName(Class c)
      {
         return c.getName();
      }

   }

}
