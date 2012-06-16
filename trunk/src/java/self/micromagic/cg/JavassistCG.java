
package self.micromagic.cg;

import java.util.Map;
import java.util.WeakHashMap;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.NotFoundException;

/**
 * 通过javassist对类进行编译的工具.
 */
public class JavassistCG
      implements CG
{
   /**
    * 使用javassist生成一个类.
    */
   public Class createClass(ClassGenerator cg)
         throws NotFoundException, CannotCompileException
   {
      ClassLoader cl = cg.getClassLoader();
      ClassPool pool = getClassPool(cl);
      Class[] classPaths = cg.getClassPaths();
      for (int i = 0; i < classPaths.length; i++)
      {
         pool.appendClassPath(new ClassClassPath(classPaths[i]));
      }
      String[] packages = cg.getPackages();
      for (int i = 0; i < packages.length; i++)
      {
         pool.importPackage(packages[i]);
      }
      pool.importPackage(ClassGenerator.getPackageString(cg.getClassName()));
      CtClass cc = pool.makeClass(cg.getClassName());
      Class[] interfaces = cg.getInterfaces();
      for (int i = 0; i < interfaces.length; i++)
      {
         cc.addInterface(pool.get(interfaces[i].getName()));
      }
      Class baseClass = cg.getSuperClass();
      if (baseClass != null)
      {
         cc.setSuperclass(pool.get(baseClass.getName()));
      }
      if (ClassGenerator.COMPILE_LOG_TYPE > 1)
      {
         ClassGenerator.log.info(cg.getClassName()
               + (baseClass != null ? ":" + baseClass.getName() : ""));
      }
      String[] fields = cg.getFields();
      for (int i = 0; i < fields.length; i++)
      {
         if (ClassGenerator.COMPILE_LOG_TYPE > 1)
         {
            ClassGenerator.log.info(cg.getClassName() + ", field:" + fields[i]);
         }
         cc.addField(CtField.make(fields[i], cc));
      }
      String[] constructors = cg.getConstructors();
      for (int i = 0; i < constructors.length; i++)
      {
         if (ClassGenerator.COMPILE_LOG_TYPE > 1)
         {
            ClassGenerator.log.info(cg.getClassName() + ", constructor:" + constructors[i]);
         }
         cc.addConstructor(CtNewConstructor.make(constructors[i], cc));
      }
      String[] methods = cg.getMethods();
      for (int i = 0; i < methods.length; i++)
      {
         if (ClassGenerator.COMPILE_LOG_TYPE > 1)
         {
            ClassGenerator.log.info(cg.getClassName() + ", method:" + methods[i]);
         }
         cc.addMethod(CtNewMethod.make(methods[i], cc));
      }
      Class c = cc.toClass(cl);
      return c;
   }

   private static Map classPoolCache = new WeakHashMap();

   /**
    * 根据使用的ClassLoader获得一个ClassPool.
    */
   private static ClassPool getClassPool(ClassLoader cl)
   {
      ClassPool pool = (ClassPool) classPoolCache.get(cl);
      if (pool == null)
      {
         synchronized (classPoolCache)
         {
            pool = (ClassPool) classPoolCache.get(cl);
            if (pool == null)
            {
               pool = new ClassPool();
               pool.appendSystemPath();
               classPoolCache.put(cl, pool);
            }
         }
      }
      return pool;
   }

}
