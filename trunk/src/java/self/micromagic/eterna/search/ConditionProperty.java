
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
    * ��ȡ���ConditionProperty��Ӧ�е���������.
    */
   int getColumnType() throws ConfigurationException;

   /**
    * ��ȡ���ConditionProperty��Ӧ�еĴ���������.
    */
   int getColumnPureType() throws ConfigurationException;

   /**
    * ��ȡ���ConditionProperty��Ӧ�е�������������.
    */
   String getColumnTypeName() throws ConfigurationException;

   /**
    * ͨ��String���͵����ݹ���һ��ValuePreparer.
    */
   ValuePreparer createValuePreparer(String value) throws ConfigurationException;

   /**
    * ͨ��Object���͵����ݹ���һ��ValuePreparer.
    */
   ValuePreparer createValuePreparer(Object value) throws ConfigurationException;

   /**
    * �Ƿ�ColumnType������ΪTYPE_IGNORE.
    * ���Ϊtrue���ʾ���Դ�Property, ���Խ���ɾ��, ����������Property
    * �̳�ʱȥ���������в���Ҫ��Property.
    */
   boolean isIgnore() throws ConfigurationException;

   /**
    * ��ConditionProperty�Ƿ�ɼ�.
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
