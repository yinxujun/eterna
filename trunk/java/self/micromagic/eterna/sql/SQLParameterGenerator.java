
package self.micromagic.eterna.sql;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.Generator;
import self.micromagic.util.IntegerRef;

public interface SQLParameterGenerator extends Generator
{
   /**
    * ����Ҫ�����SQLParameter������.
    */
   void setName(String name) throws ConfigurationException;

   /**
    * ���ö�Ӧ������.
    */
   void setColumnName(String name) throws ConfigurationException;

   /**
    * ����Ҫ�����SQLParameter������.
    */
   void setParamType(String type) throws ConfigurationException;

   /**
    * ���ö�Ӧ������׼��������.
    */
   void setParamVPC(String vpcName) throws ConfigurationException;

   /**
    * ����һ��SQLParameter.
    */
   SQLParameter createParameter(int paramIndex) throws ConfigurationException;

}
