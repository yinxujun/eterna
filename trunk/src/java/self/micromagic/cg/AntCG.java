
package self.micromagic.cg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Javac;
import org.apache.tools.ant.types.Path;
import self.micromagic.util.ResManager;
import self.micromagic.util.StringAppender;
import self.micromagic.util.StringTool;
import self.micromagic.util.Utility;

/**
 * 通过ant对类进行编译的工具.
 */
public class AntCG
      implements CG
{
   /**
    * 配置文件中对ant相关属性进行配置的前缀.
    */
   private static final String ANT_TOOL_CONFIG_PREFIX = "eterna.compile.ant.";

   /**
    * 使用ant生成一个类.
    */
   public Class createClass(ClassGenerator cg)
         throws IOException, ClassNotFoundException
   {
      synchronized (AntCG.class)
      {
         File destPath = new File(getDestPath());
         Project p = new Project();
         p.setName("eterna.ant");
         CompileLogger cl = new CompileLogger();
         p.addBuildListener(cl);
         MyJavac javac = new MyJavac();
         javac.setProject(p);
         javac.setDebug(getDebug());
         javac.setSourcepath(new Path(p, getSrcPath()));
         javac.setCompiler(getCompiler());
         setClassPath(p, javac, cg);
         javac.setDestdir(destPath);
         javac.setSrcFile(createSrcFile(cg));
         javac.setEncoding(getEncoding());
         try
         {
            javac.compile();
            CompileClassLoader ccl = new CompileClassLoader(
                  cg.getClassLoader(), destPath, cl.toString());
            return ccl.findClass(cg.getClassName());
         }
         catch (Exception ex)
         {
            throw new ClassNotFoundException("message:" + cl, ex);
         }
      }
   }

   private static File createSrcFile(ClassGenerator cg)
         throws IOException
   {
      String srcPath = getSrcPath();
      String destPath = getDestPath();
      String className = cg.getClassName();
      int index = className.lastIndexOf('.');
      String tmpPath = null;
      String cName = className;
      String pName = null;
      if (index != -1)
      {
         pName = className.substring(0, index);
         tmpPath = pName.replace('.', File.separatorChar);
         cName = className.substring(index + 1);
      }
      File srcDir = tmpPath == null ? new File(srcPath) : new File(srcPath, tmpPath);
      if (!srcDir.exists())
      {
         srcDir.mkdirs();
      }
      File destDir = tmpPath == null ? new File(destPath) : new File(destPath, tmpPath);
      if (!destDir.exists())
      {
         destDir.mkdirs();
      }
      StringAppender out = StringTool.createStringAppender(256);
      out.appendln();
      if (pName != null)
      {
         out.append("package ").append(pName).append(";").appendln().appendln();
      }
      String[] packages = cg.getPackages();
      for (int i = 0; i < packages.length; i++)
      {
         out.append("import ").append(packages[i]).append(".*").append(";").appendln();;
      }
      out.appendln().append("public class ").append(cName);
      Class baseClass = cg.getSuperClass();
      if (baseClass != null)
      {
         out.append(" extends ").append(ClassGenerator.getClassName(baseClass));
      }
      out.appendln();
      Class[] interfaces = cg.getInterfaces();
      for (int i = 0; i < interfaces.length; i++)
      {
         if (i == 0)
         {
            out.append("      ").append("implements ");
         }
         else
         {
            out.append(", ");
         }
         out.append(ClassGenerator.getClassName(interfaces[i]));
      }
      if (interfaces.length > 0)
      {
         out.appendln();
      }
      out.append("{").appendln();
      String[] fields = cg.getFields();
      for (int i = 0; i < fields.length; i++)
      {
         out.append(ResManager.indentCode(fields[i], 1)).appendln().appendln();
      }
      String[] constructors = cg.getConstructors();
      for (int i = 0; i < constructors.length; i++)
      {
         out.append(ResManager.indentCode(constructors[i], 1)).appendln().appendln();
      }
      String[] methods = cg.getMethods();
      for (int i = 0; i < methods.length; i++)
      {
         out.append(ResManager.indentCode(methods[i], 1)).appendln().appendln();
      }
      out.append("}");
      if (ClassGenerator.COMPILE_LOG_TYPE > 1)
      {
         ClassGenerator.log.info(out.toString());
      }
      File srcFile = new File(srcDir, cName + ".java");
      FileOutputStream fos = new FileOutputStream(srcFile);
      fos.write(out.toString().getBytes(getEncoding()));
      fos.close();
      return srcFile;
   }

   /**
    * 设置需要的classpath.
    */
   public static void setClassPath(Project p, Javac javac, ClassGenerator cg)
   {
      Set paths = new HashSet();
      parserClassPath(cg.getClassLoader(), paths);
      Class[] arr = cg.getClassPaths();
      for (int i = 0; i < arr.length; i++)
      {
         parserClassPath(arr[i].getClassLoader(), paths);
      }
      Iterator itr = paths.iterator();
      while (itr.hasNext())
      {
         String path = (String) itr.next();
         javac.setClasspath(new Path(p, path));
         if (ClassGenerator.COMPILE_LOG_TYPE > 1)
         {
            ClassGenerator.log.info("Added classpath:" + path);
         }
      }
   }

   /**
    * 解析</code>ClassLoader</code>中的路径, 放到结果集合中.
    */
   private static void parserClassPath(ClassLoader cl, Set result)
   {
      if (cl == null)
      {
         return;
      }
      if (cl instanceof URLClassLoader)
      {
         URLClassLoader ucl = (URLClassLoader) cl;
         URL[] urls = ucl.getURLs();
         for (int i = 0; i < urls.length; i++)
         {
            URL url = urls[i];
            if ("file".equals(url.getProtocol()))
            {
               result.add(url.getFile());
            }
            else
            {
               result.add(url.toString());
            }
         }
      }
      parserClassPath(cl.getParent(), result);
   }

   /**
    * 获得文件的编码格式.
    */
   public static String getEncoding()
   {
      String encoding = Utility.getProperty(ANT_TOOL_CONFIG_PREFIX + "encoding");
      if (encoding == null)
      {
         encoding = System.getProperty("file.encoding");
      }
      return encoding;
   }

   /**
    * 获得是否需要编译debug信息.
    */
   public static boolean getDebug()
   {
      String debug = Utility.getProperty(ANT_TOOL_CONFIG_PREFIX + "debug");
      if (debug == null)
      {
         return true;
      }
      return "true".equalsIgnoreCase(debug);
   }

   /**
    * 获得源文件的路径.
    */
   public static String getSrcPath()
   {
      String srcPath = Utility.getProperty(ANT_TOOL_CONFIG_PREFIX + "srcPath");
      if (srcPath == null)
      {
         srcPath = System.getProperty("java.io.tmpdir");
      }
      return srcPath;
   }

   /**
    * 获得编译文件的输出路径.
    */
   public static String getDestPath()
   {
      String destPath = Utility.getProperty(ANT_TOOL_CONFIG_PREFIX + "destPath");
      if (destPath == null)
      {
         destPath = System.getProperty("java.io.tmpdir");
      }
      return destPath;
   }

   /**
    * 获得使用的编译器名称.
    */
   public static String getCompiler()
   {
      String compiler = Utility.getProperty(ANT_TOOL_CONFIG_PREFIX + "compiler");
      if (compiler == null)
      {
         compiler = "extJavac";
      }
      return compiler;
   }

   private static class MyJavac extends Javac
   {
      public void setSrcFile(File file)
      {
         this.compileList = new File[]{file};
      }

      public void compile()
      {
         super.compile();
      }

   }

   private static class CompileClassLoader extends ClassLoader
   {
      private File basePath;
      private String msg;

      public CompileClassLoader(ClassLoader parent, File basePath, String msg)
      {
         super(parent);
         this.basePath = basePath;
         this.msg = msg;
      }

      protected Class findClass(String name)
            throws ClassNotFoundException
      {
         try
         {
            File f = new File(this.basePath, name.replace('.', File.separatorChar) + ".class");
            if (f.isFile())
            {
               FileInputStream fis = new FileInputStream(f);
               byte[] buf = new byte[(int) f.length()];
               fis.read(buf);
               return this.defineClass(name, buf, 0, buf.length);
            }
            else
            {
               Class c = super.findClass(name);
               if (c == null)
               {
                  throw new ClassNotFoundException("name:" + name + ", file:" + f
                        + ", message:" + this.msg);
               }
               return c;
            }
         }
         catch (Exception ex)
         {
            throw new ClassNotFoundException("message:" + this.msg, ex);
         }
      }

   }

   private static class CompileLogger
         implements BuildLogger
   {
      private StringAppender out = StringTool.createStringAppender();

      public synchronized void messageLogged(BuildEvent event)
      {
         Throwable ex = event.getException();
         int level = event.getPriority();
         if (level <= Project.MSG_WARN || ex != null)
         {
            if (level == Project.MSG_ERR || ex != null)
            {
               this.out.append("Error:").appendln();
            }
            else
            {
               this.out.append("Warn:").appendln();
            }
            this.out.append(event.getMessage());
            if (ex != null)
            {
               this.out.appendln().append("Exception:").appendln();
               StringTool.appendStackTrace(ex, this.out);
            }
            this.out.appendln();
         }
      }

      public void buildStarted(BuildEvent event)
      {
         this.messageLogged(event);
      }

      public void buildFinished(BuildEvent event)
      {
         this.messageLogged(event);
      }

      public void targetStarted(BuildEvent event)
      {
         this.messageLogged(event);
      }

      public void targetFinished(BuildEvent event)
      {
         this.messageLogged(event);
      }

      public void taskStarted(BuildEvent event)
      {
         this.messageLogged(event);
      }

      public void taskFinished(BuildEvent event)
      {
         this.messageLogged(event);
      }

      public String toString()
      {
         return this.out.toString();
      }

      public void setMessageOutputLevel(int level)
      {
      }

      public void setEmacsMode(boolean emacsMode)
      {
      }

      public void setOutputPrintStream(PrintStream output)
      {
      }

      public void setErrorPrintStream(PrintStream err)
      {
      }

   }

}
