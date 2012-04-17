
package self.micromagic.eterna.model;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.Generator;

public interface QueryExecuteGenerator extends Generator
{
   void setCache(int cacheIndex) throws ConfigurationException;

   void setDoExecute(boolean execute) throws ConfigurationException;

   void setPushResult(boolean push) throws ConfigurationException;

   void setStart(int start) throws ConfigurationException;

   void setCount(int count) throws ConfigurationException;

   /**
    * ���ü����ܼ�¼���ķ�ʽ. <p>
    * �ֱ�Ϊauto, count, none. Ĭ��ֵΪ: none.
    */
   void setCountType(String countType) throws ConfigurationException;

   void addParamBind(ParamBind bind) throws ConfigurationException;

   Execute createExecute() throws ConfigurationException;
}