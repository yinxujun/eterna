
package self.micromagic.eterna.sql;

import self.micromagic.eterna.digester.ConfigurationException;


public interface UpdateAdapterGenerator extends SQLAdapterGenerator
{
   /**
    * 获得一个<code>QueryAdapter</code>的实例. <p>
    *
    * @return <code>QueryAdapter</code>的实例.
    * @throws ConfigurationException     当相关配置出错时.
    */
   UpdateAdapter createUpdateAdapter() throws ConfigurationException;

}
