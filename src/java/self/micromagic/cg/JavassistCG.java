
package self.micromagic.cg;

import java.util.Map;
import java.util.WeakHashMap;
import java.lang.ref.WeakReference;
import java.io.InputStream;
import java.net.URL;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtNewConstructor;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import javassist.ClassPath;
import self.micromagic.util.StringAppender;
import self.micromagic.util.StringTool;

/**
 * 通过javassist对类进行编译的工具.
 */
public class JavassistCG
      implements CG
{
   /**
    * 用javassist作为编译类型时使用的名称.
    */
   public static final String COMPILE_TYPE = "javassist";

   /**
    * 使用javassist生成一个类.
    */
   public Class createClass(ClassGenerator cg)
         throws NotFoundException, CannotCompileException
   {
      return createClass0(cg);
   }

   private static synchronized Class createClass0(ClassGenerator cg)
         throws NotFoundException, CannotCompileException
   {
      ClassLoader cl = cg.getClassLoader();
      ClassPool pool = getClassPool(cl);
      Class[] classPaths = cg.getClassPaths();
      WeakClassPath[] cpArr = new WeakClassPath[classPaths.length];
      for (int i = 0; i < classPaths.length; i++)
      {
         cpArr[i] = new WeakClassPath(classPaths[i]);
         pool.appendClassPath(cpArr[i]);
      }
      try
      {
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
         if (ClassGenerator.COMPILE_LOG_TYPE > COMPILE_LOG_TYPE_DEBUG)
         {
            log.info(cg.getClassName()
                  + (baseClass != null ? ":" + ClassGenerator.getClassName(baseClass) : ""));
         }
         String[] fields = cg.getFields();
         for (int i = 0; i < fields.length; i++)
         {
            if (ClassGenerator.COMPILE_LOG_TYPE > COMPILE_LOG_TYPE_DEBUG)
            {
               log.info(cg.getClassName() + ", field:" + fields[i]);
            }
            cc.addField(CtField.make(fields[i], cc));
         }
         String[] constructors = cg.getConstructors();
         for (int i = 0; i < constructors.length; i++)
         {
            if (ClassGenerator.COMPILE_LOG_TYPE > COMPILE_LOG_TYPE_DEBUG)
            {
               log.info(cg.getClassName() + ", constructor:" + constructors[i]);
            }
            cc.addConstructor(CtNewConstructor.make(constructors[i], cc));
         }
         String[] methods = cg.getMethods();
         for (int i = 0; i < methods.length; i++)
         {
            if (ClassGenerator.COMPILE_LOG_TYPE > COMPILE_LOG_TYPE_DEBUG)
            {
               log.info(cg.getClassName() + ", method:" + methods[i]);
            }
            cc.addMethod(CtNewMethod.make(methods[i], cc));
         }
         Class c = cc.toClass(cl);
         return c;
      }
      finally
      {
         // 清理importPackages和classPaths
         pool.clearImportedPackages();
         for (int i = 0; i < cpArr.length; i++)
         {
            pool.removeClassPath(cpArr[i]);
         }
      }
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

   private static class WeakClassPath
         implements ClassPath
   {
      /**
       * 这里使用<code>WeakReference</code>来引用类, 这样就不会影响其正常的释放.
       */
      private WeakReference baseCL;
      private String className;

      public WeakClassPath(Class c)
      {
         this.className = c.getName();
         this.baseCL = new WeakReference(c.getClassLoader());
      }

      public InputStream openClassfile(String classname)
      {
         ClassLoader cl = (ClassLoader) this.baseCL.get();
         if (cl == null)
         {
            if (ClassGenerator.COMPILE_LOG_TYPE > COMPILE_LOG_TYPE_ERROR)
            {
               log.warn(this);
            }
            return null;
         }
         String jarname = classname.replace('.', '/') + ".class";
         InputStream is = cl.getResourceAsStream(jarname);
         if (ClassGenerator.COMPILE_LOG_TYPE > COMPILE_LOG_TYPE_INFO)
         {
            if (log.isInfoEnabled())
            {
               StringAppender buf = StringTool.createStringAppender();
               buf.append(is == null ? "Not found " : "Open ").append("class file:")
                     .append(jarname).append(", base class:").append(this.className);
               log.info(buf);
            }
         }
         return is;
      }

      public URL find(String classname)
      {
         ClassLoader cl = (ClassLoader) this.baseCL.get();
         if (cl == null)
         {
            if (ClassGenerator.COMPILE_LOG_TYPE > COMPILE_LOG_TYPE_ERROR)
            {
               log.warn(this);
            }
            return null;
         }
         String jarname = classname.replace('.', '/') + ".class";
         URL url = cl.getResource(jarname);
         if (ClassGenerator.COMPILE_LOG_TYPE > COMPILE_LOG_TYPE_INFO)
         {
            if (log.isInfoEnabled())
            {
               StringAppender buf = StringTool.createStringAppender();
               buf.append(url == null ? "Not found " : "The ").append("res file:").append(jarname);
               if (url != null)
               {
                  buf.append(", locate:").append(url);
               }
               buf.append(", base class:").append(this.className);
               log.info(buf);
            }
         }
         return url;
      }

      public void close()
      {
      }

      public String toString()
      {
         ClassLoader cl = (ClassLoader) this.baseCL.get();
         if (cl == null)
         {
            return this.className + " released.";
         }
         return this.className + ".class";
      }

   }

}
