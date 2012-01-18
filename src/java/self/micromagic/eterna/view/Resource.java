
package self.micromagic.eterna.view;

import java.util.Iterator;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.EternaFactory;

/**
 * 文本资源
 */
public interface Resource
{
   /**
    * 初始化此文本资源.
    */
   void initialize(EternaFactory factory) throws ConfigurationException;

   /**
    * 获取此文本资源的名称. <p>
    */
   String getName() throws ConfigurationException;

   /**
    * 获得解析后的资源迭代列表.
    */
   Iterator getParsedRessource() throws ConfigurationException;

   /**
    * 获取没有参数的文本资源的值. <p>
    * 如果该文本资源有参数, 则默认为空字符串.
    */
   String getValue() throws ConfigurationException;

   /**
    * 获取带有参数的文本资源的值. <p>
    * 如果该文本资源的参数超出了设置的参数, 则
    * 多出的参数默认为空字符串.
    */
   String getValue(Object[] params) throws ConfigurationException;

}
