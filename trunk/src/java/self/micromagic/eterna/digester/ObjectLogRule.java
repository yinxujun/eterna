
package self.micromagic.eterna.digester;

import org.xml.sax.Attributes;

/**
 * 进行初始化情况记录的初始化规则.
 * 主要记录当前正在生成的是什么对象, 这样在发生异常时可以准确地报告
 * 哪个对象出错了.
 */
public class ObjectLogRule extends MyRule
{
   private String attributeName;
   private String objType;

   /**
    * @param attributeName   配置中指定对象名称的属性
    * @param objType         对象类型
    */
   public ObjectLogRule(String attributeName, String objType)
   {
      this.attributeName = attributeName;
      this.objType = objType;
   }

   public void myBegin(String namespace, String name, Attributes attributes)
         throws Exception
   {
      String theName = attributes.getValue(this.attributeName);
      if (theName == null)
      {
         theName = "null";
      }
      StringBuffer temp = new StringBuffer(
            this.objType.length() + theName.length() + 2);
      temp.append(this.objType).append("[").append(theName).append("]");
      ConfigurationException.objName = temp.toString();
   }

   /**
    * 设置当前正在初始化的配置.
    */
   public static void setConfigName(String name)
   {
      ConfigurationException.config = name;
   }

   /**
    * 设置当前正在初始化的对象名称.
    */
   public static void setObjName(String name)
   {
      ConfigurationException.objName = name;
   }

   /**
    * 设置当前正在初始化的对象的类型及名称.
    */
   public static void setObjName(String type, String name)
   {
      if (name == null)
      {
         setObjName(type);
      }
      StringBuffer temp = new StringBuffer(type.length() + name.length() + 2);
      temp.append(type).append("[").append(name).append("]");
      ConfigurationException.objName = temp.toString();
   }

}
