
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
 * ��̬������ɹ���.
 */
public class CodeClassTool
{
   /**
    * ���ɵĴ���ı������.
    */
   private static int CODE_ID = 1;

   /**
    * ���ɵĴ���Ļ���. <p>
    * ��baseClassΪ�������л���, ֵΪһ��map, Ϊ�̳������Ĵ��뼯.
    */
   private static Map codeCache = new ReferenceMap(ReferenceMap.WEAK, ReferenceMap.HARD);

   /**
    * ���ݶ�̬��������һ����.
    *
    * @param baseClass       ���ɵ������̳е���
    * @param interfaceClass  ���ɵ�����ʵ�ֵĽӿ�
    * @param methodHead      Ҫ���ɵķ�����ͷ��, ���������� �����б� �׳����쳣
    * @param bodyCode        �����Ĵ���
    * @param imports         ��Ҫ���õ���·���б�
    * @return                ���ɳ�������
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
    * ·����Ļ���, ����Ϊ·����, ֵΪ��·�����ClassLoader.
    */
   private static Map pathClassCache = new ReferenceMap(ReferenceMap.WEAK, ReferenceMap.HARD);

   /**
    * ע��һ�����ڶ�ȡ����Ϣ��·����. <p>
    * ����ͨ�����·��������ȡ������ʹ�õ������ļ�.
    *
    * @param pathClass   ��ȡ����Ϣ��·����
    */
   public static void registerPathClass(Class pathClass)
   {
      if (pathClass != null)
      {
         pathClassCache.put(pathClass, pathClass.getClassLoader());
      }
   }

   /**
    * ��ȡ��Ҫ��̬���ɵĴ���.
    *
    * @param g              ��̬�������ڵĹ�����
    * @param factory        ����������Factory��ʵ��
    * @param codeFlag       �������д�Ŵ������������
    * @param attrCondeFlag  �������д�����õĴ������������
    * @param codeParamFlag  �������д�����ɴ���Ĳ�������������
    * @return �����ַ���
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
