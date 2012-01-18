
package self.micromagic.util;

import java.io.File;
import java.io.Writer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Iterator;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogConfigurationException;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.DocumentHelper;
import org.dom4j.io.XMLWriter;
import self.micromagic.eterna.model.AppData;

/**
 * 配置说明:
 * 默认的日志配置有
 * self.micromagic.defaultLogger.console.off            是否关闭控制台输出
 * self.micromagic.defaultLogger.console.delay_time     控制台输出的刷新间隔(毫秒)
 * self.micromagic.defaultLogger.name                   默认日志的名称
 * self.micromagic.defaultLogger.file                   日志的文件名
 * self.micromagic.defaultLogger.file.size              日志的大小限制(单位KB)
 * self.micromagic.defaultLogger.file.count             日志的文件个数
 * self.micromagic.defaultLogger.level                  日志的屏蔽等级
 *
 * 其它的日志配置有
 * self.micromagic.logger.names
 * 其它日志的名称, 多个名称之间用","分割, 名称只能是: 字母 数字 "_" "-", 且名称不能为"default"
 *
 * self.micromagic.[name]Logger.file                   日志的文件名
 * self.micromagic.[name]Logger.file.size              日志的大小限制(单位KB)
 * self.micromagic.[name]Logger.file.count             日志的文件个数
 * self.micromagic.[name]Logger.level                  日志的屏蔽等级
 */
public class Jdk14Factory extends LogFactory
{
   public static final String USE_ETERNA_LOG = "self.micromagic.useEternaLog";

   private static ConsoleFlushTimer consoleFlushTimer = new ConsoleFlushTimer();
   private static int consoleFlushDelay = 5000;
   private static StreamHandler consoleLoggerHander;
   private static Logger defaultLogger;
   private static Map otherLoggerMap = new HashMap();

   private Map attributes = new HashMap();
   private Map instances = new HashMap();

   public static final String EXCEPTION_LOG_PROPERTY = "self.micromagic.eterna.exception.logType";
   protected static int EXCEPTION_LOG_TYPE = 0;

   private static Document logDocument = null;
   private static Element logNodes = null;

   protected static void setExceptionLogType(String type)
   {
      try
      {
         EXCEPTION_LOG_TYPE = Integer.parseInt(type);
      }
      catch (Exception ex)
      {
         System.err.println("Error in set exception log type.");
         ex.printStackTrace();
      }
   }

   private static void checkNodeInit()
   {
      if (logDocument == null)
      {
         logDocument = DocumentHelper.createDocument();
         Element root = logDocument.addElement("eterna");
         logNodes = root.addElement("logs");
      }

      if (logNodes.elements().size() > 2048)
      {
         // 当节点过多时, 清除最先添加的几个节点
         Iterator itr = logNodes.elementIterator();
         try
         {
            for (int i = 0; i < 1536; i++)
            {
               itr.next();
               itr.remove();
            }
         }
         catch (Exception ex)
         {
            // 当去除节点出错时, 则清空日志
            logDocument = null;
            checkNodeInit();
         }
      }
   }

   private static synchronized void addException(String msg, Throwable ex, boolean isCause, String level,
         String className, String methodName, Element logNode)
   {
      if (!isCause)
      {
         logNode.addAttribute("level", level);
         logNode.addAttribute("class", className);
         logNode.addAttribute("method", methodName);
         logNode.addAttribute("message", msg);
      }
      logNode.addAttribute("time", Formater.formatDatetime(new java.util.Date(System.currentTimeMillis())));
      logNode.addAttribute("exClass", ex.getClass().getName());
      logNode.addAttribute("exMessage", ex.getMessage());
      Element stacks = logNode.addElement("stacks");
      StackTraceElement[] trace = ex.getStackTrace();
      for (int i = 0; i < trace.length; i++)
      {
         Element stack = stacks.addElement("stack");
         stack.setText(trace[i].toString());
      }
   }

   private static synchronized void addException(String msg, Throwable ex, boolean isCause, String level,
         String className, String methodName)
   {
      checkNodeInit();
      Element expNode;
      if (isCause)
      {
         expNode = logNodes.addElement("cause_by");
      }
      else
      {
         expNode = logNodes.addElement("exception");
      }
      addException(msg, ex, isCause, level, className, methodName, expNode);

      if (AppData.getAppLogType() == 1)
      {
         Element nowNode = AppData.getCurrentData().getCurrentNode();
         if (nowNode != null)
         {
            if (isCause)
            {
               expNode = nowNode.addElement("cause_by");
            }
            else
            {
               expNode = nowNode.addElement("exception");
            }
            addException(msg, ex, isCause, level, className, methodName, expNode);
         }
      }

      if (ex.getCause() != null)
      {
         addException(null, ex.getCause(), true, null, null, null);
      }
   }

   private static synchronized void addMessage(String msg, String level, String className, String methodName)
   {
      checkNodeInit();
      Element expNode = logNodes.addElement("message");
      expNode.addAttribute("level", level);
      expNode.addAttribute("time", Formater.formatDatetime(new java.util.Date(System.currentTimeMillis())));
      expNode.addAttribute("class", className);
      expNode.addAttribute("method", methodName);
      expNode.addAttribute("message", msg);
      if (AppData.getAppLogType() == 1)
      {
         Element nowNode = AppData.getCurrentData().getCurrentNode();
         if (nowNode != null)
         {
            expNode = nowNode.addElement("message");
            expNode.addAttribute("level", level);
            expNode.addAttribute("time", Formater.formatDatetime(new java.util.Date(System.currentTimeMillis())));
            expNode.addAttribute("class", className);
            expNode.addAttribute("method", methodName);
            expNode.addAttribute("message", msg);
         }
      }
   }

   public static synchronized void printException(Writer out, boolean clear)
         throws IOException
   {
      if (logDocument == null)
      {
         return;
      }
      XMLWriter writer = new XMLWriter(out);
      writer.write(logDocument);
      writer.flush();
      if (clear)
      {
         logDocument = null;
         logNodes = null;
      }
   }

   static
   {
      try
      {
         Utility.addMethodPropertyManager(EXCEPTION_LOG_PROPERTY, Jdk14Factory.class, "setExceptionLogType");
      }
      catch (Throwable ex)
      {
         System.err.println("Error in init exception log type.");
         ex.printStackTrace();
      }

      initLog(null, true);
      String lognames = Utility.getProperty("self.micromagic.logger.names");
      if (lognames != null)
      {
         StringTokenizer token = new StringTokenizer(lognames, ",");
         while (token.hasMoreTokens())
         {
            String temp = token.nextToken().trim();
            if (temp.length() == 0)
            {
               continue;
            }
            Jdk14Factory.initLog(temp, false);
         }
      }
   }

   private static void initLog(String name, boolean isDefault)
   {
      if (!isDefault)
      {
         // 检查名称的合法性
         if (name == null || name.length() == 0)
         {
            System.err.println("Error log name, null or empty.");
            return;
         }
         for (int i = 0; i < name.length(); i++)
         {
            char c = name.charAt(i);
            if (c >= 'a' && c <= 'z')
            {
               continue;
            }
            if (c >= '0' && c <= '9')
            {
               continue;
            }
            if (c >= 'A' && c <= 'Z')
            {
               continue;
            }
            if (c == '_' || c == '-')
            {
               continue;
            }
            System.err.println("Error log name: " + name);
            return;
         }
         if ("default".equals(name))
         {
            System.err.println("Error log name can not be [default].");
            return;
         }
      }

      if (isDefault)
      {
         Jdk14Factory.consoleLoggerHander = new StreamHandler(System.out, new SimpleFormatter());
         //是否关闭控制台的log
         if ("true".equalsIgnoreCase(Utility.getProperty("self.micromagic.defaultLogger.console.off")))
         {
            Jdk14Factory.consoleLoggerHander.setLevel(Level.OFF);
         }
         else
         {
            try
            {
               int delay = Integer.parseInt(
                     Utility.getProperty("self.micromagic.defaultLogger.console.delay_time"));
               Jdk14Factory.consoleFlushDelay = delay >= 0 && delay < 500 ? 500 : delay;
            }
            catch (Throwable ex) {}
            Jdk14Factory.consoleLoggerHander.setLevel(Level.ALL);
            if (Jdk14Factory.consoleFlushDelay != -1)
            {
               Jdk14Factory.consoleFlushTimer.start();
            }
         }
      }

      //创建文件日志的handler
      String propertyName;
      String logFile = null;
      int filesize = 1024 * 1024;
      int filecount = 5;
      FileHandler fileHander = null;
      try
      {
         propertyName = isDefault ? "self.micromagic.defaultLogger.file" :
               "self.micromagic." + name + "Logger.file";
         logFile = Utility.getProperty(propertyName);
         if (logFile != null)
         {
            File f = new File(Utility.resolveDynamicPropnames(logFile));
            f = f.getParentFile();
            if (!f.isDirectory())
            {
               f.mkdirs();
            }
            try
            {
               propertyName = isDefault ? "self.micromagic.defaultLogger.file.size" :
                     "self.micromagic." + name + "Logger.file.size";
               filesize = (int) (Double.parseDouble(Utility.getProperty(propertyName)) * 1024);
               filesize = filesize < 1024 ? 1024 : filesize;
            }
            catch (Throwable ex) {}
            try
            {
               propertyName = isDefault ? "self.micromagic.defaultLogger.file.count" :
                     "self.micromagic." + name + "Logger.file.count";
               filecount = Integer.parseInt(Utility.getProperty(propertyName));
               filecount = filecount < 1 ? 1 : filecount;
            }
            catch (Throwable ex) {}
            fileHander = new FileHandler(logFile, filesize, filecount, true);
            fileHander.setFormatter(new SimpleFormatter());
            fileHander.setLevel(Level.ALL);
         }
      }
      catch (Throwable ex)
      {
         System.err.println(Formater.getCurrentDatetimeString()
               + ": Error when create file log handler.");
         ex.printStackTrace(System.err);
      }

      //创建日志logger
      try
      {
         String logName;
         if (isDefault)
         {
            logName = Utility.getProperty("self.micromagic.defaultLogger.name");
            if (logName == null)
            {
               logName = "default:sid_" + Thread.currentThread().getName()
                     + "." + System.currentTimeMillis();
            }
            else
            {
               logName = "default:" + logName;
            }
         }
         else
         {
            logName = name;
         }
         Logger logger = Logger.getLogger(logName);
         logger.setUseParentHandlers(false);
         if (isDefault)
         {
            logger.addHandler(Jdk14Factory.consoleLoggerHander);
         }
         if (fileHander != null)
         {
            logger.addHandler(fileHander);
         }
         //设置log的level
         propertyName = isDefault ? "self.micromagic.defaultLogger.level" :
               "self.micromagic." + name + "Logger.level";
         String levelName = Utility.getProperty(propertyName, "INFO");
         try
         {
            Level level = Level.parse(levelName);
            StringBuffer temp = new StringBuffer(128);
            temp.append("Jdk14 log  name:").append(logName).append(", Level:").append(level);
            if (logFile != null)
            {
               temp.append(", file(").append(logFile).append(",");
               int tempI = ((filesize % 1024) * 10 + 512) / 1024;
               if (tempI == 10)
               {
                  temp.append(filesize / 1024 + 1);
               }
               else
               {
                  temp.append(filesize / 1024);
                  if (tempI > 0)
                  {
                     temp.append(".").append(tempI);
                  }
               }
               temp.append("k").append(",").append(filecount).append(")");
            }
            System.out.println(temp);
            logger.setLevel(level);
         }
         catch (Throwable ex) {}
         if (isDefault)
         {
            Jdk14Factory.defaultLogger = logger;
         }
         else
         {
            registerLogger(name, logger);
         }
      }
      catch (Throwable ex)
      {
         System.err.println(Formater.getCurrentDatetimeString()
               + ": Error when create log.");
         ex.printStackTrace(System.err);
      }
   }

   public static Logger registerLogger(String name, Logger logger)
   {
      if (logger == null)
      {
         return (Logger) Jdk14Factory.otherLoggerMap.remove(name);
      }
      return (Logger) Jdk14Factory.otherLoggerMap.put(name, logger);
   }

   public static void stopFlushConsale()
   {
      Jdk14Factory.consoleFlushTimer.consoleFlushOver = true;
   }

   public static void startFlushConsale()
   {
      if (Jdk14Factory.consoleLoggerHander.getLevel() != Level.OFF
            && Jdk14Factory.consoleFlushTimer.consoleFlushOver)
      {
         Jdk14Factory.consoleFlushTimer = new ConsoleFlushTimer();
         Jdk14Factory.consoleFlushTimer.start();
      }
   }

   public Object getAttribute(String name)
   {
      return this.attributes.get(name);
   }

   public String[] getAttributeNames()
   {
      Set keys = attributes.keySet();
      return (String[]) keys.toArray(new String[0]);
   }

   public void setAttribute(String name, Object value)
   {
      this.attributes.put(name, value);
   }

   public void removeAttribute(String name)
   {
      this.attributes.remove(name);
   }

   public Log getInstance(Class clazz)
         throws LogConfigurationException
   {
      return this.getInstance(clazz.getName());
   }

   public Log getInstance(String name)
         throws LogConfigurationException
   {
      Log instance = (Log) this.instances.get(name);
      if (instance == null)
      {
         Logger tempLogger = (Logger) Jdk14Factory.otherLoggerMap.get(name);
         if (tempLogger == null)
         {
            tempLogger = Jdk14Factory.defaultLogger;
         }
         instance = new MyJdk14Logger(name, tempLogger);
         this.instances.put(name, instance);
      }
      return instance;
   }

   public void release()
   {
      this.instances.clear();
   }

   private static class MyJdk14Logger
         implements Log
   {
      protected Logger logger;
      protected String name;

      public MyJdk14Logger(String name, Logger logger)
      {
         this.name = name;
         this.logger = logger;
      }

      public Logger getLogger()
      {
         return this.logger;
      }

      public boolean isDebugEnabled()
      {
        return (this.getLogger().isLoggable(Level.FINE));
      }

      public boolean isErrorEnabled()
      {
        return (this.getLogger().isLoggable(Level.SEVERE));
      }

      public boolean isFatalEnabled()
      {
        return (this.getLogger().isLoggable(Level.SEVERE));
      }

      public boolean isInfoEnabled()
      {
        return (this.getLogger().isLoggable(Level.INFO));
      }

      public boolean isTraceEnabled()
      {
        return (this.getLogger().isLoggable(Level.FINEST));
      }

      public boolean isWarnEnabled()
      {
        return (this.getLogger().isLoggable(Level.WARNING));
      }

      public void debug(Object message, Throwable exception)
      {
         this.log(Level.FINE, String.valueOf(message), exception);
      }

      public void error(Object message, Throwable exception)
      {
         this.log(Level.SEVERE, String.valueOf(message), exception);
      }

      public void fatal(Object message, Throwable exception)
      {
         this.log(Level.SEVERE, String.valueOf(message), exception);
      }

      public void info(Object message, Throwable exception)
      {
         this.log(Level.INFO, String.valueOf(message), exception);
      }

      public void trace(Object message, Throwable exception)
      {
         this.log(Level.FINEST, String.valueOf(message), exception);
      }

      public void warn(Object message, Throwable exception)
      {
         this.log(Level.WARNING, String.valueOf(message), exception);
      }

      public void trace(Object message)
      {
         this.log(Level.FINEST, String.valueOf(message), null);
      }

      public void debug(Object message)
      {
         this.log(Level.FINE, String.valueOf(message), null);
      }

      public void info(Object message)
      {
         this.log(Level.INFO, String.valueOf(message), null);
      }

      public void warn(Object message)
      {
         this.log(Level.WARNING, String.valueOf(message), null);
      }

      public void error(Object message)
      {
         this.log(Level.SEVERE, String.valueOf(message), null);
      }

      public void fatal(Object message)
      {
         this.log(Level.SEVERE, String.valueOf(message), null);
      }

      protected void log(Level level, String msg, Throwable ex)
      {
         Logger logger = getLogger();
         if (logger.isLoggable(level))
         {
            // Hack (?) to get the stack trace.
            Throwable dummyException = new Throwable();
            StackTraceElement locations[] = dummyException.getStackTrace();
            // Caller will be the third element
            String cname = "unknown";
            String method = "unknown";
            if (locations != null && locations.length > 2)
            {
               StackTraceElement caller = locations[2];
               cname = caller.getClassName();
               method = caller.getMethodName();
            }
            if (ex == null)
            {
               if (EXCEPTION_LOG_TYPE == 1)
               {
                  addMessage(msg, level.getName(), cname, method);
               }
               logger.logp(level, cname, method, msg);
            }
            else
            {
               if (EXCEPTION_LOG_TYPE == 1)
               {
                  addException(msg, ex, false, level.getName(), cname, method);
               }
               logger.logp(level, cname, method, msg, ex);
            }
         }
      }

   }

   private static class ConsoleFlushTimer extends Thread
   {
      private boolean consoleFlushOver = false;

      public void run()
      {
         while (!this.consoleFlushOver)
         {
            try
            {
               Thread.sleep(Jdk14Factory.consoleFlushDelay);
               Jdk14Factory.consoleLoggerHander.flush();
            }
            catch (InterruptedException ex)
            {
               System.err.println(Formater.getCurrentDatetimeString()
                     + ": Error when flush log console.");
            }
         }
         System.out.println("Jdk14 log  console timer end.");
      }

   }

}
