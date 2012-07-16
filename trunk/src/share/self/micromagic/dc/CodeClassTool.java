
package self.micromagic.dc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.DeflaterOutputStream;

import self.micromagic.cg.ClassGenerator;
import self.micromagic.cg.ClassKeyCache;
import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.digester.FactoryManager;
import self.micromagic.eterna.share.Factory;
import self.micromagic.eterna.share.Generator;
import self.micromagic.util.StringAppender;
import self.micromagic.util.StringTool;
import self.micromagic.util.Utility;
import self.micromagic.util.container.SynHashMap;
import self.micromagic.util.container.ThreadCache;

/**
 * ��̬������ɹ���.
 */
public class CodeClassTool
{
   /**
    * ���̻߳����д洢�Ĵ�����Ϣ�б������, ����ΪList.
    */
   public static final String CODE_ERRORS_FLAG = "codeErrors";

   /**
    * ���ö�̬��������ʱ, �Դ�����������.
    */
   public static final String COMPILE_TYPE_PROPERTY = "self.micromagic.dc.compile.type";

   /**
    * ��̬��������ʱ, �Դ�����������.
    */
   private static String DC_COMPILE_TYPE = null;

   /**
    * ���ɵĴ���ı������.
    */
   private static volatile int CODE_ID = 1;

   /**
    * ���ɵĴ���Ļ���. <p>
    * ��baseClassΪ�������л���, ֵΪһ��map, Ϊ�̳������Ĵ��뼯.
    */
   private static ClassKeyCache codeCache = ClassKeyCache.getInstance();

   static
   {
      try
      {
         Utility.addFieldPropertyManager(COMPILE_TYPE_PROPERTY, CodeClassTool.class, "DC_COMPILE_TYPE");
      }
      catch (Throwable ex) {}
   }

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
      cache = (Map) codeCache.getProperty(baseClass);
      if (cache == null)
      {
         cache = new HashMap();
         codeCache.setProperty(baseClass, cache);
      }
      CodeKey key = new CodeKey(methodHead, bodyCode);
      Class codeClass = (Class) cache.get(key);
      if (codeClass != null)
      {
         return codeClass;
      }

      ClassGenerator cg = new ClassGenerator();
      Iterator itr = pathClassCache.entrySet().iterator();
      while (itr.hasNext())
      {
         Map.Entry entry = (Map.Entry) itr.next();
         cg.addClassPath((Class) entry.getKey());
      }
      cg.addClassPath(baseClass);
      cg.addClassPath(interfaceClass);
      cg.addClassPath(CodeClassTool.class);
      String nameSuffix = "$$EDC_" + (CODE_ID++);
      cg.setClassName("dc." + baseClass.getName() + nameSuffix);
      cg.addInterface(interfaceClass);
      cg.setSuperClass(baseClass);
      cg.importPackage("self.micromagic.util");
      cg.importPackage("self.micromagic.eterna.sql");
      cg.importPackage("self.micromagic.eterna.model");
      cg.importPackage("self.micromagic.eterna.search");
      cg.importPackage("self.micromagic.eterna.security");
      cg.importPackage("java.util");
      cg.importPackage("java.sql");
      if (imports != null)
      {
         for (int i = 0; i < imports.length; i++)
         {
            cg.importPackage(imports[i]);
         }
      }
      cg.setClassLoader(baseClass.getClassLoader());
      StringAppender tmpCode = StringTool.createStringAppender(bodyCode.length() + 32);
      tmpCode.append(methodHead).appendln().append("{").appendln();
      tmpCode.append(bodyCode);
      tmpCode.appendln().append("}");
      cg.addMethod(tmpCode.toString());
      if (DC_COMPILE_TYPE != null)
      {
         cg.setCompileType(DC_COMPILE_TYPE);
      }
      codeClass = cg.createClass();
      cache.put(key, codeClass);
      return codeClass;
   }

   /**
    * ·����Ļ���, ����Ϊ·����, ֵΪ��·�����ClassLoader.
    */
   private static Map pathClassCache = new SynHashMap(8, SynHashMap.WEAK);

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
         pathClassCache.put(pathClass, Boolean.TRUE);
      }
   }

   /**
    * ��ȡ��Ҫ��̬���ɵĴ���.
    *
    * @param g              ��̬�������ڵĹ�����
    * @param factory        ����������Factory��ʵ��
    * @param codeFlag       �������д�Ŵ������������
    * @param attrCondeFlag  �������д�����õĴ������������
    * @return �����ַ���
    */
   public static String getCode(Generator g, Factory factory, String codeFlag, String attrCondeFlag)
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
      Map paramMap = new HashMap();
      String[] names = g.getAttributeNames();
      for (int i = 0; i < names.length; i++)
      {
         String name = names[i];
         if (codeFlag.equals(name) || attrCondeFlag.equals(name))
         {
            continue;
         }
         paramMap.put(name, g.getAttribute(name));
      }
      if (paramMap.size() == 0)
      {
         paramMap = null;
      }
      if (paramMap != null)
      {
         code = Utility.resolveDynamicPropnames(code, paramMap);
      }
      else
      {
         code = Utility.resolveDynamicPropnames(code);
      }
      return code;
   }

   /**
    * ��¼��̬������ʱ�ĳ�����Ϣ.
    *
    * @param code         ��Ҫ��̬����Ĵ���
    * @param position     �������ڵ�λ����Ϣ
    * @param error        ������쳣��Ϣ
    */
   public static void logCodeError(String code, String position, Exception error)
   {
      String msg = "Error in compile java code at " + position + ". the code is:\n" + code;
      FactoryManager.log.error(msg, error);
      Object obj = ThreadCache.getInstance().getProperty(CODE_ERRORS_FLAG);
      if (obj instanceof List)
      {
         ((List) obj).add(new CodeErrorInfo(code, position, error));
      }
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
