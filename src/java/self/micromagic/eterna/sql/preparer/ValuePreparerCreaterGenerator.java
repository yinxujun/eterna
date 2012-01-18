
package self.micromagic.eterna.sql.preparer;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.Generator;
import self.micromagic.eterna.share.EternaFactory;

public interface ValuePreparerCreaterGenerator extends Generator
{

   /**
    * 初始化ValuePreparer.
    */
   void initialize(EternaFactory factory) throws ConfigurationException;

   /**
    * 根据type生成相关的VPGenerator, 来继续生成ValuePreparer.
    */
   ValuePreparerCreater createValuePreparerCreater(int pureType) throws ConfigurationException;

}
