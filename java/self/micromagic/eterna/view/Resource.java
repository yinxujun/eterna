
package self.micromagic.eterna.view;

import java.util.Iterator;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.EternaFactory;

/**
 * �ı���Դ
 */
public interface Resource
{
   /**
    * ��ʼ�����ı���Դ.
    */
   void initialize(EternaFactory factory) throws ConfigurationException;

   /**
    * ��ȡ���ı���Դ������. <p>
    */
   String getName() throws ConfigurationException;

   /**
    * ��ý��������Դ�����б�.
    */
   Iterator getParsedRessource() throws ConfigurationException;

   /**
    * ��ȡû�в������ı���Դ��ֵ. <p>
    * ������ı���Դ�в���, ��Ĭ��Ϊ���ַ���.
    */
   String getValue() throws ConfigurationException;

   /**
    * ��ȡ���в������ı���Դ��ֵ. <p>
    * ������ı���Դ�Ĳ������������õĲ���, ��
    * ����Ĳ���Ĭ��Ϊ���ַ���.
    */
   String getValue(Object[] params) throws ConfigurationException;

}
