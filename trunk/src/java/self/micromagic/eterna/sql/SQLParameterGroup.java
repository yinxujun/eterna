
package self.micromagic.eterna.sql;

import java.util.Iterator;

import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.digester.ConfigurationException;

/**
 * 参数组, 多个参数的组合
 */
public interface SQLParameterGroup
{
   /**
    * ignoreList列表中, 加上此标记名表示忽略参数组中同名的参数
    */
   public final static String IGNORE_SAME_NAME = "$ignoreSame";

   /**
    * 初始化本SQLParameterGroup对象, 系统会在初始化时调用此方法. <p>
    * 该方法的主要作用是初始化每个SQLParameter对象, 并根据父对象来组成自己
    * 自己的reader列表.
    *
    * @param factory  SQLAdapterFactory的实例, 可以从中获得父对象
    */
   void initialize(EternaFactory factory) throws ConfigurationException;

   /**
    * 设置本SQLParameterGroup的名称.
    */
   void setName(String name) throws ConfigurationException;

   /**
    * 获取本SQLParameterGroup的名称.
    */
   String getName() throws ConfigurationException;

   /**
    * 获取SQLParameterGenerator列表的迭代器.
    */
   Iterator getParameterGeneratorIterator() throws ConfigurationException;

   /**
    * 添加一个参数. <p>
    *
    * @param paramGenerator     参数构造器.
    * @throws ConfigurationException     当相关配置出错时.
    */
   void addParameter(SQLParameterGenerator paramGenerator) throws ConfigurationException;

   /**
    * 添加一个参数组. <p>
    *
    * @param groupName     参数组名称.
    * @param ignoreList    忽略的参数列表.
    * @throws ConfigurationException     当相关配置出错时.
    */
   void addParameterRef(String groupName, String ignoreList) throws ConfigurationException;

}
