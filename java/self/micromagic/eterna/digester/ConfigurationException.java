
package self.micromagic.eterna.digester;

import self.micromagic.eterna.share.EternaException;
import self.micromagic.eterna.share.ThreadCache;

/**
 * 当适配器<code>Adapter</code>相关的配置文件不正确，或不正确的
 * 使用适配器的时候就会抛出该异常.
 *
 * @author  micromagic
 * @version 1.0, 2002-10-12
 */
public class ConfigurationException extends EternaException
{
   static String IN_INITIALIZE = "eterna.in_initialize";

   /**
    * 在解析xml时设置正在解析的文件
    */
   static String config = null;

   /**
    * 在解析xml时设置正在解析的对象名
    */
   static String objName = null;

   /**
    * 构造一个<code>ConfigurationException</code>.
    */
   public ConfigurationException()
   {
      super();
   }

   /**
    * 通过参数<code>message</code>来构造一个<code>ConfigurationException</code>.
    *
    * @param message   出错信息
    */
   public ConfigurationException(String message)
   {
      super(message);
   }

   /**
    * 通过通过一个抛出的对象来构造一个<code>ConfigurationException</code>.
    *
    * @param origin    异常或错误
    */
   public ConfigurationException(Throwable origin)
   {
      super(origin);
   }

   public String getMessage()
   {
      String msg = super.getMessage();
      msg = msg == null ? "" : msg;
      if (config == null && objName == null)
      {
         return msg;
      }
      if ("1".equals(ThreadCache.getInstance().getProperty(IN_INITIALIZE)))
      {
         StringBuffer temp = new StringBuffer(msg.length());
         if (config != null)
         {
            temp.append("Config:").append(config).append("; ");
         }
         if (objName != null)
         {
            temp.append("Object:").append(objName).append("; ");
         }
         temp.append("Message:").append(msg);
         return temp.toString();
      }
      return msg;
   }

}
