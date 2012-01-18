
package self.micromagic.eterna.sql;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.Generator;

public interface ResultFormatGenerator extends Generator
{
   /**
    * 设置这个format的名称.
    */
   void setName(String name) throws ConfigurationException;

   /**
    * 获取这个format的名称.
    */
   String getName() throws ConfigurationException;

   /**
    * 设置这个format要格式化的对象的类型.
    */
   void setType(String type) throws ConfigurationException;

   /**
    * 设置格式化输出的模板.
    * 可以设置在pattern属性中, 也可以设置在pattern子节点的body中.
    * 如果两个都设置, 那取pattern属性中的设置.
    */
   void setPattern(String pattern) throws ConfigurationException;

   /**
    * 创建一个<code>ResultFormat</code>的实例. <p>
    *
    * @return <code>ResultFormat</code>的实例.
    * @throws ConfigurationException     当相关配置出错时.
    */
   ResultFormat createFormat() throws ConfigurationException;

}
