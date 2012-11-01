
package self.micromagic.eterna.search;

import self.micromagic.eterna.share.Generator;
import self.micromagic.eterna.digester.ConfigurationException;

/**
 * @author micromagic@sina.com
 */
public interface ConditionPropertyGenerator extends Generator
{
   void setName(String name) throws ConfigurationException;

   void setColumnName(String name) throws ConfigurationException;

   void setColumnCaption(String caption) throws ConfigurationException;

   void setColumnType(String type) throws ConfigurationException;

   /**
    * ���ö�Ӧ�е�����׼��������.
    */
   void setColumnVPC(String vpcName) throws ConfigurationException;

   /**
    * �����Ƿ�ɼ�.
    */
   void setVisible(boolean visible) throws ConfigurationException;

   void setConditionInputType(String type) throws ConfigurationException;

   void setDefaultValue(String value) throws ConfigurationException;

   void setPermissions(String permissions) throws ConfigurationException;

   void setUseDefaultConditionBuilder(boolean use) throws ConfigurationException;

   void setDefaultConditionBuilderName(String name) throws ConfigurationException;

   void setConditionBuilderListName(String name) throws ConfigurationException;

   ConditionProperty createConditionProperty() throws ConfigurationException;

}
