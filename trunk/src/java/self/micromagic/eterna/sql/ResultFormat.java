
package self.micromagic.eterna.sql;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.security.Permission;
import self.micromagic.eterna.share.EternaFactory;

public interface ResultFormat
{
   /**
    * ��ʼ��format.
    */
   void initialize(EternaFactory factory) throws ConfigurationException;

   /**
    * ��ȡ���format������.
    */
   String getName() throws ConfigurationException;

   /**
    * ��һ��������и�ʽ�����. <p>
    * ע: ��ʽ���Ľ�����ܷ���<code>null</code>, ����޷���ʽ��, ���׳��쳣.
    *
    * @param obj         Ҫ���и�ʽ������Ķ���
    * @param permission  ��ص�Ȩ����Ϣ
    * @return   ��ʽ������ַ���
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   String format(Object obj, Permission permission) throws ConfigurationException;

   /**
    * ��һ��������и�ʽ�����. <p>
    * ע: ��ʽ���Ľ�����ܷ���<code>null</code>, ����޷���ʽ��, ���׳��쳣.
    *
    * @param obj         Ҫ���и�ʽ������Ķ���
    * @param row         ��ǰ��ʽ���������ڵ��ж���
    * @param permission  ��ص�Ȩ����Ϣ
    * @return   ��ʽ������ַ���
    * @throws ConfigurationException     ��������ó�����޷���ʽ��ʱ.
    */
   String format(Object obj, ResultRow row, Permission permission) throws ConfigurationException;

}
