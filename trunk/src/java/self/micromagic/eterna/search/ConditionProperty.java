
package self.micromagic.eterna.search;

import java.util.List;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.security.PermissionSet;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.sql.preparer.ValuePreparer;

/**
 * @author micromagic@sina.com
 */
public interface ConditionProperty
{
   void initialize(EternaFactory factory) throws ConfigurationException;

   String getName() throws ConfigurationException;

   String getColumnName() throws ConfigurationException;

   String getColumnCaption() throws ConfigurationException;

   /**
    * 获取这个ConditionProperty对应列的数据类型.
    */
   int getColumnType() throws ConfigurationException;

   /**
    * 获取这个ConditionProperty对应列的纯数据类型.
    */
   int getColumnPureType() throws ConfigurationException;

   /**
    * 获取这个ConditionProperty对应列的数据类型名称.
    */
   String getColumnTypeName() throws ConfigurationException;

   /**
    * 通过String类型的数据构成一个ValuePreparer.
    */
   ValuePreparer createValuePreparer(String value) throws ConfigurationException;

   /**
    * 通过Object类型的数据构成一个ValuePreparer.
    */
   ValuePreparer createValuePreparer(Object value) throws ConfigurationException;

   /**
    * 是否ColumnType的类型为TYPE_IGNORE.
    * 如果为true则表示忽略此Property, 可以将其删除, 这样可以在Property
    * 继承时去掉父对象中不需要的Property.
    */
   boolean isIgnore() throws ConfigurationException;

   /**
    * 该ConditionProperty是否可见.
    */
   boolean isVisible() throws ConfigurationException;

   String getConditionInputType() throws ConfigurationException;

   String getDefaultValue() throws ConfigurationException;

   String getAttribute(String name) throws ConfigurationException;

   String[] getAttributeNames() throws ConfigurationException;

   PermissionSet getPermissionSet() throws ConfigurationException;

   String getConditionBuilderListName() throws ConfigurationException;

   List getConditionBuilderList() throws ConfigurationException;

   boolean isUseDefaultConditionBuilder() throws ConfigurationException;

   ConditionBuilder getDefaultConditionBuilder() throws ConfigurationException;

}
