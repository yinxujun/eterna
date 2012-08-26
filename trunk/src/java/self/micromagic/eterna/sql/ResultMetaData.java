
package self.micromagic.eterna.sql;

import self.micromagic.eterna.digester.ConfigurationException;

public interface ResultMetaData
{
   /**
    * 获取<code>ResultIterator</code>对应的<code>QueryAdapter</code>.
    */
   QueryAdapter getQuery() throws ConfigurationException;

   /**
    * 获取<code>ResultIterator</code>中列的个数.
    */
   int getColumnCount() throws ConfigurationException;

   /**
    * 获取某列的显示宽度.
    *
    * @param column 第一列为1, 第二列为2, ...
    */
   int getColumnWidth(int column) throws ConfigurationException;

   /**
    * 获取某列的显示标题.
    *
    * @param column 第一列为1, 第二列为2, ...
    */
   String getColumnCaption(int column) throws ConfigurationException;

   /**
    * 获取某列的名称.
    *
    * @param column 第一列为1, 第二列为2, ...
    */
   String getColumnName(int column) throws ConfigurationException;

   /**
    * 获取用于读取该列的ResultReader对象.
    *
    * @param column 第一列为1, 第二列为2, ...
    */
   ResultReader getColumnReader(int column) throws ConfigurationException;

   /**
    * 查找用于读取某列的ResultReader对象.
    *
    * @param columnName 某列的名称
    */
   ResultReader findColumnReader(String columnName) throws ConfigurationException;

}
