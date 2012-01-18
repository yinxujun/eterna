
package self.micromagic.eterna.sql;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.Generator;

public interface ResultFormatGenerator extends Generator
{
   /**
    * �������format������.
    */
   void setName(String name) throws ConfigurationException;

   /**
    * ��ȡ���format������.
    */
   String getName() throws ConfigurationException;

   /**
    * �������formatҪ��ʽ���Ķ��������.
    */
   void setType(String type) throws ConfigurationException;

   /**
    * ���ø�ʽ�������ģ��.
    * ����������pattern������, Ҳ����������pattern�ӽڵ��body��.
    * �������������, ��ȡpattern�����е�����.
    */
   void setPattern(String pattern) throws ConfigurationException;

   /**
    * ����һ��<code>ResultFormat</code>��ʵ��. <p>
    *
    * @return <code>ResultFormat</code>��ʵ��.
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   ResultFormat createFormat() throws ConfigurationException;

}
