
package self.micromagic.eterna.sql;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.Generator;
import self.micromagic.util.IntegerRef;

public interface SQLParameterGenerator extends Generator
{
   /**
    * 设置要构造的SQLParameter的名称.
    */
   void setName(String name) throws ConfigurationException;

   /**
    * 设置对应的列名.
    */
   void setColumnName(String name) throws ConfigurationException;

   /**
    * 设置要构造的SQLParameter的类型.
    */
   void setParamType(String type) throws ConfigurationException;

   /**
    * 设置对应的数据准备生成器.
    */
   void setParamVPC(String vpcName) throws ConfigurationException;

   /**
    * 构造一个SQLParameter.
    */
   SQLParameter createParameter(int paramIndex) throws ConfigurationException;

}
