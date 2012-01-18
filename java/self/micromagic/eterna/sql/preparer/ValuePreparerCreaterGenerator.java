
package self.micromagic.eterna.sql.preparer;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.Generator;
import self.micromagic.eterna.share.EternaFactory;

public interface ValuePreparerCreaterGenerator extends Generator
{

   /**
    * ��ʼ��ValuePreparer.
    */
   void initialize(EternaFactory factory) throws ConfigurationException;

   /**
    * ����type������ص�VPGenerator, ����������ValuePreparer.
    */
   ValuePreparerCreater createValuePreparerCreater(int pureType) throws ConfigurationException;

}
