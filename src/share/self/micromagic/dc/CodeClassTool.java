
package self.micromagic.dc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.commons.collections.ReferenceMap;
import javassist.ClassPool;
import javassist.ClassClassPath;
import javassist.CtClass;
import javassist.CtNewMethod;
import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.Generator;
import self.micromagic.eterna.share.Factory;
import self.micromagic.util.StringTool;
import self.micromagic.util.Utility;

/**
 * 动态类的生成工具.
 */
public class CodeClassTool
{
   /**
    * 生成的代码的编号序列.
    */
   private static int CODE_ID = 1;

   /**
    * 生成的代码的缓存. <p>
    * 以baseClass为主键进行缓存, 值为一个map, 为继承这个类的代码集.
    */
   private static Map codeCache = new ReferenceMap(ReferenceMap.WEAK, ReferenceMap.HARD);

   /**
    * 根据动态代码生成一个类.
    *
    * @param baseClass       生成的类所继承的类
    * @param interfaceClass  生成的类所实现的接口
    * @param methodHead      要生成的方法的头部, 包括方法名 参数列表 抛出的异常
    * @param bodyCode        方法的代码
    * @param imports         需要引用的类路径列表
    * @return                生成出来的类
    */
   public static synchronized Class createJavaCodeClass(Class baseClass, Class interfaceClass,
         String methodHead, String bodyCode, String[] imports)
         throws Exception
   {
      if (baseClass == null)
      {
         baseClass = CodeClassTool.class;
      }
      Map cache;
      cache = (Map) codeCache.get(baseClass);
      if (cache == null)
      {
         cache = new HashMap();
         codeCache.put(baseClass, cache);
      }
      CodeKey key = new CodeKey(methodHead, bodyCode);
      Class codeClass = (Class) cache.get(key);
      if (codeClass != null)
      {
         return codeClass;
      }

      ClassPool pool = new ClassPool();
      pool.appendSystemPath();
      HashSet clSet = new HashSet();
      Iterator itr = pathClassCache.entrySet().iterator();
      while (itr.hasNext())
      {
         Map.Entry entry = (Map.Entry) itr.next();
         if (!clSet.contains(entry.getValue()))
         {
            pool.appendClassPath(new ClassClassPath((Class) entry.getKey()));
            clSet.add(entry.getValue());
         }
      }
      if (!clSet.contains(baseClass.getClassLoader()))
      {
         pool.appendClassPath(new ClassClassPath(baseClass));
         clSet.add(baseClass.getClassLoader());
      }
      if (interfaceClass != null && !clSet.contains(interfaceClass.getClassLoader()))
      {
         pool.appendClassPath(new ClassClassPath(interfaceClass));
         clSet.add(interfaceClass.getClassLoader());
      }
      if (!clSet.contains(CodeClassTool.class.getClassLoader()))
      {
         pool.appendClassPath(new ClassClassPath(CodeClassTool.class));
      }
      String namePrefix = "eterna.code_" + (CODE_ID++) + ".";
      CtClass cc = pool.makeClass(namePrefix + baseClass.getName());
      if (interfaceClass != null)
      {
         cc.addInterface(pool.get(interfaceClass.getName()));
      }
      cc.setSuperclass(pool.get(baseClass.getName()));
      pool.importPackage("self.micromagic.util");
      pool.importPackage("self.micromagic.eterna.sql");
      pool.importPackage("self.micromagic.eterna.model");
      pool.importPackage("self.micromagic.eterna.search");
      pool.importPackage("self.micromagic.eterna.security");
      pool.importPackage("java.util");
      pool.importPackage("java.sql");
      if (imports != null)
      {
         for (int i = 0; i < imports.length; i++)
         {
            pool.importPackage(imports[i]);
         }
      }
      ClassLoader cl = baseClass.getClassLoader();
      StringBuffer tmpCode = new StringBuffer(bodyCode.length() + 32);
      tmpCode.append(methodHead).append("\n{\n");
      tmpCode.append(bodyCode);
      tmpCode.append("\n}");
      cc.addMethod(CtNewMethod.make(tmpCode.toString(), cc));
      codeClass = cc.toClass(cl);
      cache.put(key, codeClass);
      return codeClass;
   }

   /**
    * 路径类的缓存, 主键为路径类, 值为此路径类的ClassLoader.
    */
   private static Map pathClassCache = new ReferenceMap(ReferenceMap.WEAK, ReferenceMap.HARD);

   /**
    * 注册一个用于读取类信息的路径类. <p>
    * 可以通过这个路径类来获取代码中使用到的类文件.
    *
    * @param pathClass   读取类信息的路径类
    */
   public static void registerPathClass(Class pathClass)
   {
      if (pathClass != null)
      {
         pathClassCache.put(pathClass, pathClass.getClassLoader());
      }
   }

   /**
    * 获取需要动态生成的代码.
    *
    * @param g              动态代码所在的构造器
    * @param factory        构造器所在Factory的实例
    * @param codeFlag       构造器中存放代码的属性名称
    * @param attrCondeFlag  构造器中存放引用的代码的属性名称
    * @param codeParamFlag  构造器中存放生成代码的参数的属性名称
    * @return 代码字符串
    */
   public static String getCode(Generator g, Factory factory, String codeFlag, String attrCondeFlag,
         String codeParamFlag)
         throws ConfigurationException
   {
      String code = (String) g.getAttribute(codeFlag);
      String attrCode = (String) g.getAttribute(attrCondeFlag);
      if (code == null && attrCode == null)
      {
         throw new ConfigurationException("Not found the [" + codeFlag + "] or ["
               + attrCondeFlag + "] attribute.");
      }
      if (code == null)
      {
         code = (String) factory.getAttribute(attrCode);
         if (code == null)
         {
            throw new ConfigurationException("Not found the [" + attrCode + "] in factory attribute.");
         }
      }
      String codeParam = (String) g.getAttribute(codeParamFlag);
      if (codeParam != null)
      {
         Map paramMap = StringTool.string2Map(codeParam, ";", '=');
         code = Utility.resolveDynamicPropnames(code, paramMap);
      }
      else
      {
         code = Utility.resolveDynamicPropnames(code);
      }
      return code;
   }

   private static class CodeKey
   {
      private byte[] compressedCode;
      private int strCodeHash;

      public CodeKey(String methodHead, String bodyCode)
            throws IOException
      {
         String code = methodHead + "#" + bodyCode;
         this.strCodeHash = code.hashCode();
         ByteArrayOutputStream byteOut = new ByteArrayOutputStream(128);
         DeflaterOutputStream out = new DeflaterOutputStream(byteOut);
         byte[] buf = code.getBytes("UTF-8");
         out.write(buf);
         out.close();
         this.compressedCode = byteOut.toByteArray();
      }

      public int hashCode()
      {
         return this.strCodeHash;
      }

      public boolean equals(Object obj)
      {
         if (obj instanceof CodeKey)
         {
            CodeKey other = (CodeKey) obj;
            if (other.compressedCode.length == this.compressedCode.length
                  && other.strCodeHash == this.strCodeHash)
            {
               for (int i = 0; i < this.compressedCode.length; i++)
               {
                  if (other.compressedCode[i] != this.compressedCode[i])
                  {
                     return false;
                  }
               }
               return true;
            }
         }
         return false;
      }

   }

}
