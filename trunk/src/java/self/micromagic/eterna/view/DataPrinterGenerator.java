
package self.micromagic.eterna.view;

import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.share.Generator;
import self.micromagic.eterna.digester.ConfigurationException;

/**
 * ���ݼ�������Ĺ�����.
 */
public interface DataPrinterGenerator extends Generator
{
   /**
    * ��ʼ���˹�����.
    */
   void initialize(EternaFactory factory) throws ConfigurationException;

   /**
    * ����һ�����ݼ������.
    *
    * @return    ���ݼ������
    */
   DataPrinter createDataPrinter() throws ConfigurationException;

}
