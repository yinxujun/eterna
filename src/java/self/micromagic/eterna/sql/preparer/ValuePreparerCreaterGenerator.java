
package self.micromagic.eterna.sql.preparer;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.Generator;
import self.micromagic.eterna.share.EternaFactory;

/**
 * ֵ׼�����Ĵ����ߵĹ�����.
 * ��������һ��ֵ׼�����Ĵ�����, ��ֵ׼�����Ĵ����߿ɲ���һ��ֵ׼����.
 */
public interface ValuePreparerCreaterGenerator extends Generator
{
   /**
    * ��ʼ���˹�����.
    */
   void initialize(EternaFactory factory) throws ConfigurationException;

   /**
    * ����type����������ص�ֵ׼�����Ĵ�����.
    *
    * @return   ֵ׼�����Ĵ�����
    */
   ValuePreparerCreater createValuePreparerCreater(int pureType) throws ConfigurationException;

}
