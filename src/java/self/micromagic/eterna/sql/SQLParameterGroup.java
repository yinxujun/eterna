
package self.micromagic.eterna.sql;

import java.util.Iterator;

import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.digester.ConfigurationException;

/**
 * ������, ������������
 */
public interface SQLParameterGroup
{
   /**
    * ��reader��arrtibute������VPCʹ�õ�����.
    */
   public static final String READER_VPC_FLAG = "vpcName";

   /**
    * ��reader��arrtibute������columnNameʹ�õ�����.
    */
   public static final String READER_COLNAME_FLAG = "columnName";

   /**
    * ignoreList�б���, ���ϴ˱������ʾ���Բ�������ͬ���Ĳ���
    */
   public static final String IGNORE_SAME_NAME = "$ignoreSame";

   /**
    * ��ʼ����SQLParameterGroup����, ϵͳ���ڳ�ʼ��ʱ���ô˷���. <p>
    * �÷�������Ҫ�����ǳ�ʼ��ÿ��SQLParameter����, �����ݸ�����������Լ�
    * �Լ���reader�б�.
    *
    * @param factory  EternaFactory��ʵ��, ���Դ��л�ø�����
    */
   void initialize(EternaFactory factory) throws ConfigurationException;

   /**
    * ���ñ�SQLParameterGroup������.
    */
   void setName(String name) throws ConfigurationException;

   /**
    * ��ȡ��SQLParameterGroup������.
    */
   String getName() throws ConfigurationException;

   /**
    * ��ȡSQLParameterGenerator�б�ĵ�����.
    */
   Iterator getParameterGeneratorIterator() throws ConfigurationException;

   /**
    * ���һ������. <p>
    *
    * @param paramGenerator     ����������.
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   void addParameter(SQLParameterGenerator paramGenerator) throws ConfigurationException;

   /**
    * ���һ��������. <p>
    *
    * @param groupName     ����������.
    * @param ignoreList    ���ԵĲ����б�.
    * @throws ConfigurationException     ��������ó���ʱ.
    */
   void addParameterRef(String groupName, String ignoreList) throws ConfigurationException;

}
