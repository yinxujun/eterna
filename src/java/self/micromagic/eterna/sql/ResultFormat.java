
package self.micromagic.eterna.sql;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.security.Permission;
import self.micromagic.eterna.share.EternaFactory;

public interface ResultFormat
{
   /**
    * 初始化format.
    */
   void initialize(EternaFactory factory) throws ConfigurationException;

   /**
    * 获取这个format的名称.
    */
   String getName() throws ConfigurationException;

   /**
    * 对一个对象进行格式化输出. <p>
    * 注: 格式化的结果不能返回<code>null</code>, 如果无法格式化, 请抛出异常.
    *
    * @param obj         要进行格式化输出的对象
    * @param permission  相关的权限信息
    * @return   格式化后的字符串
    * @throws ConfigurationException     当相关配置出错时.
    */
   String format(Object obj, Permission permission) throws ConfigurationException;

   /**
    * 对一个对象进行格式化输出. <p>
    * 注: 格式化的结果不能返回<code>null</code>, 如果无法格式化, 请抛出异常.
    *
    * @param obj         要进行格式化输出的对象
    * @param row         当前格式化对象所在的行对象
    * @param permission  相关的权限信息
    * @return   格式化后的字符串
    * @throws ConfigurationException     当相关配置出错或无法格式化时.
    */
   String format(Object obj, ResultRow row, Permission permission) throws ConfigurationException;

}
