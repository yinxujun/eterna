
package self.micromagic.cg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import self.micromagic.util.StringTool;
import self.micromagic.util.Utility;
import self.micromagic.util.IntegerRef;
import self.micromagic.util.StringAppender;

/**
 * һ������Զ����뼰���ɹ���.
 */
public class ClassGenerator
{
   /**
    * ��ע��������ɹ��ߵĻ���.
    */
   private static final Map cgCache = new HashMap();

   /**
    * ע��һ�������ɹ���.
    *
    * @param name   ����
    * @param cg     �����ɹ��ߵ�ʵ��
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
    * ��ȡ�����������.
    */
   public String getClassName()
   {
      return this.className;
   }

   /**
    * ���ñ����������.
    */
   public void setClassName(String className)
   {
      if (!StringTool.isEmpty(className))
      {
         this.className = className;
      }
   }

   /**
    * �����������ʹ�õ�<code>ClassLoader</code>.
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
    * ������������ʹ�õ�<code>ClassLoader</code>.
    */
   public void setClassLoader(ClassLoader classLoader)
   {
      this.classLoader = classLoader;
   }

   /**
    * ���ñ�������Ҫ�̳е���.
    */
   public void setSuperClass(Class superClass)
   {
      this.superClass = superClass;
   }

   /**
    * ��ü̳е���.
    */
   public Class getSuperClass()
   {
      return superClass;
   }

   /**
    * ��ӱ�������Ҫʵ�ֵĽӿ�.
    */
   public void addInterface(Class anInterface)
   {
      if (anInterface != null)
      {
         this.interfaces.add(anInterface);
      }
   }

   /**
    * �����Ҫʵ�ֵĽӿ��б�.
    */
   public Class[] getInterfaces()
   {
      return (Class[]) this.interfaces.toArray(new Class[this.interfaces.size()]);
   }

   /**
    * �����Ҫ���õİ�.
    */
   public void importPackage(String packageName)
   {
      if (!StringTool.isEmpty(packageName))
      {
         this.importPackages.add(packageName);
      }
   }

   /**
    * �����Ҫ���õİ��б�.
    */
   public String[] getPackages()
   {
      String[] arr = new String[this.importPackages.size()];
      return (String[]) this.importPackages.toArray(arr);
   }

   /**
    * ���һ����ȡ�������·������.
    */
   public void addClassPath(Class pathClass)
   {
      if (pathClass != null && !this.classPathCache.containsKey(pathClass.getClassLoader()))
      {
         this.classPathCache.put(pathClass.getClassLoader(), pathClass);
      }
   }

   /**
    * �����Ҫ��ȡ�������·�������б�.
    */
   public Class[] getClassPaths()
   {
      Collection values = this.classPathCache.values();
      return (Class[]) values.toArray(new Class[values.size()]);
   }

   /**
    * ���һ�����Դ���.
    */
   public void addField(String field)
   {
      if (!StringTool.isEmpty(field))
      {
         this.fields.add(field);
      }
   }

   /**
    * ������Դ����б�.
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
         map.put(CG.THIS_NAME, getConstructorName(this.getClassName()));
         for (int i = 0; i < arr.length; i++)
         {
            arr[i] = Utility.resolveDynamicPropnames(arr[i], map, true);
         }
         return arr;
      }
   }

   /**
    * ���һ�����췽������.
    * �����й��캯�������ƿ�����"${thisName}"����.
    * @see CG#THIS_NAME
    */
   public void addConstructor(String constructor)
   {
      if (!StringTool.isEmpty(constructor))
      {
         this.constructors.add(constructor);
      }
   }

   /**
    * ��ù��췽�������б�.
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
      map.put(CG.THIS_NAME, getConstructorName(this.getClassName()));
      for (int i = 0; i < arr.length; i++)
      {
         arr[i] = Utility.resolveDynamicPropnames(arr[i], map, true);
      }
      return arr;
   }

   /**
    * ���һ����������.
    */
   public void addMethod(String methodCode)
   {
      if (!StringTool.isEmpty(methodCode))
      {
         this.methods.add(methodCode);
      }
   }

   /**
    * ��÷��������б�.
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
         map.put(CG.THIS_NAME, getConstructorName(this.getClassName()));
         for (int i = 0; i < arr.length; i++)
         {
            arr[i] = Utility.resolveDynamicPropnames(arr[i], map, true);
         }
         return arr;
      }
   }

   /**
    * ��öԱ�����ı��뷽ʽ.
    */
   public String getCompileType()
   {
      if (this.compileType == null)
      {
         return Utility.getProperty(CG.COMPILE_TYPE_PROPERTY, "javassist");
      }
      return this.compileType;
   }

   /**
    * ���öԱ�����ı��뷽ʽ.
    */
   public void setCompileType(String compileType)
   {
      this.compileType = compileType;
   }

   /**
    * �������õĴ�������һ����.
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
    * �������������ˮ��, ��ֹ�����ظ�.
    */
   private static volatile int CLASS_GENERATOR_ID = 1;

   /**
    * ����һ�������ɹ���.
    *
    * @param baseClass       ���ɵ�����ʹ�õĻ�����,
    *                        ��ʹ�ô����ClassLoader�����������ɵ���
    * @param interfaceClass  ��Ҫʵ�ֵĽӿ�
    * @param imports         ��Ҫ���õİ��б�
    * @return  <code>ClassGenerator</code>��ʵ��.
    */
   public static ClassGenerator createClassGenerator(Class baseClass, Class interfaceClass,
         String[] imports)
   {
      return createClassGenerator(null, baseClass, interfaceClass, imports);
   }

   /**
    * ����һ�������ɹ���. <p>
    * �µ�����Ϊ��eterna.[baseClass]$suffix$$EBP_[���к�]
    *
    * @param suffix          ���������ĺ�׺
    * @param baseClass       ���ɵ�����ʹ�õĻ�����,
    *                        ��ʹ�ô����ClassLoader�����������ɵ���
    * @param interfaceClass  ��Ҫʵ�ֵĽӿ�
    * @param imports         ��Ҫ���õİ��б�
    * @return  <code>ClassGenerator</code>��ʵ��.
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
            tmpSuffix = "$$ECG_" + (CLASS_GENERATOR_ID++);
         }
         else
         {
            tmpSuffix = "$" + suffix + "$$ECG_" + (CLASS_GENERATOR_ID++);
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
    * ��ȡ������İ�·���ַ���.
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
    * ��ȡ���������İ�·���ַ���.
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
    * ��ȡ���������Ĺ��캯������.
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
    * ��ȡ�����Ԫ������.
    *
    * @param arrayClass  ������
    * @param levelRef    ���������ά��
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
    * ���һ���������.
    * �˷�������ݲ�ͬ��JDK�汾���ò�ͬ�ķ���.
    * �磺1.5 ���ϵ���Ҫʹ��getCanonicalName.
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

   static int COMPILE_LOG_TYPE = 1;

   /**
    * ��ʼ��һ�������ķ�����.
    */
   private static NameAccessor nameAccessor;

   static
   {
      try
      {
         Class c = Class.forName("self.micromagic.cg.ClassGenerator$ClassCanonicalNameAccessor");
         nameAccessor = (NameAccessor) c.newInstance();
      }
      catch (Throwable ex)
      {
         if (!(ex instanceof UnsupportedClassVersionError))
         {
            if (COMPILE_LOG_TYPE > CG.COMPILE_LOG_TYPE_ERROR)
            {
               CG.log.error("init name accessor error.", ex);
            }
         }
         // ��������쳣, �������jdk�汾С��1.5, ʹ��getName��������ȡ����
         nameAccessor = new ClassNameAccessor();
      }
      try
      {
         Utility.addFieldPropertyManager(CG.COMPILE_LOG_PROPERTY, ClassGenerator.class, "COMPILE_LOG_TYPE");
         if (COMPILE_LOG_TYPE > CG.COMPILE_LOG_TYPE_INFO)
         {
            CG.log.info("map entry name:" + nameAccessor.getName(Map.Entry.class)
                  + ", accessor class:" + nameAccessor.getClass());
         }
      }
      catch (Throwable ex) {}
      try
      {
         registerCG("ant", new AntCG());
      }
      catch (Throwable ex)
      {
         if (COMPILE_LOG_TYPE > CG.COMPILE_LOG_TYPE_ERROR)
         {
            CG.log.error("AntCG init error.", ex);
         }
      }
      try
      {
         registerCG("javassist", new JavassistCG());
      }
      catch (Throwable ex)
      {
         if (COMPILE_LOG_TYPE > CG.COMPILE_LOG_TYPE_ERROR)
         {
            CG.log.error("JavassistCG init error.", ex);
         }
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
