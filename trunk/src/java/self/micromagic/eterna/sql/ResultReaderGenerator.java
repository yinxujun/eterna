
package self.micromagic.eterna.sql;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.Generator;

public interface ResultReaderGenerator extends Generator
{
   /**
    * ����reader������.
    */
   void setName(String name) throws ConfigurationException;

   /**
    * ����reader��ȡ����ʱʹ�õ�����. <p>
    * �÷�����{@link #setColumnIndex(int)}����ֻ��һ����Ч, ��������ʹ��
    * ��������Ͳ���������ʹ������ֵ.
    */
   void setColumnName(String columnName) throws ConfigurationException;

   /**
    * ����reader��ȡ����ʱʹ�õ�����ֵ. <p>
    * �÷�����{@link #setColumnName(String)}����ֻ��һ����Ч, ��������ʹ��
    * ������ֵ��Ͳ���������ʹ������.
    */
   void setColumnIndex(int columnIndex) throws ConfigurationException;

   /**
    * ������Ϊ������, ��������"ORDER BY"֮�������.
    * �ڶ����ʱ, Ҳ������"[����].[����]"����ʽ.
    */
   void setOrderName(String orderName) throws ConfigurationException;

   /**
    * ����format������.
    */
   void setFormatName(String name) throws ConfigurationException;

   /**
    * ����reader������.
    *
    * @param type   ���͵�����
    * @see self.micromagic.eterna.share.TypeManager
    */
   void setType(String type) throws ConfigurationException;

   /**
    * ���ÿɶ�ȡ���е�Ȩ�޼���, ֻ��ӵ�м����е�����1��Ȩ�޾Ϳ���
    * ��ȡ����.
    *
    * @param permissions  Ȩ�޵�����, �������֮����","�ָ�
    */
   void setPermissions(String permissions) throws ConfigurationException;

   /**
    * ����reader�ı���.
    */
   void setCaption(String caption) throws ConfigurationException;

   /**
    * ������ʾʱ�Ŀ��.
    */
   void setWidth(int width) throws ConfigurationException;

   /**
    * ������htmlҳ�����ʱ�Ƿ�Ҫ���������ǩ.
    */
   void setHtmlFilter(boolean filter) throws ConfigurationException;

   /**
    * �����Ƿ�ɼ�.
    */
   void setVisible(boolean visible) throws ConfigurationException;

   /**
    * �������е�����, ����һ��reader����.
    */
   ResultReader createReader() throws ConfigurationException;

}
