
package self.micromagic.eterna.digester;

import self.micromagic.eterna.share.EternaException;
import self.micromagic.eterna.share.ThreadCache;

/**
 * ��������<code>Adapter</code>��ص������ļ�����ȷ������ȷ��
 * ʹ����������ʱ��ͻ��׳����쳣.
 *
 * @author  micromagic
 * @version 1.0, 2002-10-12
 */
public class ConfigurationException extends EternaException
{
   static String IN_INITIALIZE = "eterna.in_initialize";

   /**
    * �ڽ���xmlʱ�������ڽ������ļ�
    */
   static String config = null;

   /**
    * �ڽ���xmlʱ�������ڽ����Ķ�����
    */
   static String objName = null;

   /**
    * ����һ��<code>ConfigurationException</code>.
    */
   public ConfigurationException()
   {
      super();
   }

   /**
    * ͨ������<code>message</code>������һ��<code>ConfigurationException</code>.
    *
    * @param message   ������Ϣ
    */
   public ConfigurationException(String message)
   {
      super(message);
   }

   /**
    * ͨ��ͨ��һ���׳��Ķ���������һ��<code>ConfigurationException</code>.
    *
    * @param origin    �쳣�����
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
