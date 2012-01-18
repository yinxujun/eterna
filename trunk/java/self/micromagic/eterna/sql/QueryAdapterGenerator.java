
package self.micromagic.eterna.sql;

import self.micromagic.eterna.digester.ConfigurationException;

public interface QueryAdapterGenerator extends SQLAdapterGenerator
{
   /**
    * 设置排序的子sql语句所在的位置.
    */
   void setOrderIndex(int index) throws ConfigurationException;

   /**
    * 设置这句查询是否只能用forwardOnly模式来执行.
    */
   void setForwardOnly(boolean forwardOnly) throws ConfigurationException;

   /**
    * 设置ResultReader的排序方式字符串.
    */
   void setReaderOrder(String readerOrder) throws ConfigurationException;

   /**
    * 设置继承的ResultReaderManager的名称.
    */
   void setReaderManagerName(String name) throws ConfigurationException;

   /**
    * 在继承的ResultReaderManager的基础上添加一个ResultReader, 如果
    * 名称与ResultReaderManager中的重复, 则覆盖原来的.
    */
   void addResultReader(ResultReader reader) throws ConfigurationException;

   /**
    * 获得一个<code>QueryAdapter</code>的实例. <p>
    *
    * @return <code>QueryAdapter</code>的实例.
    * @throws ConfigurationException     当相关配置出错时.
    */
   QueryAdapter createQueryAdapter() throws ConfigurationException;

}
