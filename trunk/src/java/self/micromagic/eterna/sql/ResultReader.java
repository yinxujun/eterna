
package self.micromagic.eterna.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.CallableStatement;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.security.PermissionSet;
import self.micromagic.eterna.share.EternaFactory;

/**
 * @author micromagic@sina.com
 */
public interface ResultReader
{
   /**
    * ��arrtibute���������������ʹ�õ�����.
    */
   public static final String INPUT_TYPE_FLAG = "inputType";

   /**
    * ��ʼ����reader����, ϵͳ���ڳ�ʼ��ʱ���ô˷���. <p>
    * �÷�������Ҫ�����Ǹ������õ�format����������ʼ��format����.
    *
    * @param factory  EternaFactory��ʵ��, ���Դ��л��format����
    */
   void initialize(EternaFactory factory) throws ConfigurationException;

   /**
    * ��ȡ��reader������.
    *
    * @return  һ�������reader���͵�����
    * @see self.micromagic.eterna.share.TypeManager
    */
   int getType() throws ConfigurationException;

   /**
    * �Ƿ�Type������ΪTYPE_IGNORE.
    * ���Ϊtrue���ʾ���Դ�ResultReader, ���Խ���ɾ��, ����������ResultReaderManager
    * �̳�ʱȥ���������в���Ҫ��ResultReader.
    */
   boolean isIgnore() throws ConfigurationException;

   /**
    * ��ȡformat������.
    */
   String getFormatName() throws ConfigurationException;

   /**
    * ��ȡformat����.
    */
   ResultFormat getFormat() throws ConfigurationException;

   /**
    * ��ȡ��reader������.
    */
   String getName() throws ConfigurationException;

   /**
    * �����Ϊ������, ��������"ORDER BY"֮�������.
    * �ڶ����ʱ, Ҳ������"[����].[����]"����ʽ.
    */
   String getOrderName() throws ConfigurationException;

   /**
    * ��ȡ����ʱ, ��ȡ������. <p>
    * �÷�����{@link #getColumnIndex}����ֻ��һ����Ч, ������ֵ��Ч
    * ʱ�����ص�����Ϊnull.
    */
   String getColumnName() throws ConfigurationException;

   /**
    * ��ȡ����ʱ, ��ȡ��������. <p>
    * �÷�����{@link #getColumnName}����ֻ��һ����Ч, ��������Чʱ,
    * ���ص�����ֵΪ-1.
    */
   int getColumnIndex() throws ConfigurationException;

   /**
    * ����htmlҳ�����ʱ, �Ƿ���Ҫ���������ǩ�Ĺ���.
    */
   boolean needHtmlFilter() throws ConfigurationException;

   /**
    * ��ResultReader�Ƿ�ɼ�.
    */
   boolean isVisible() throws ConfigurationException;

   /**
    * ��ResultReader�Ƿ���Ч.
    * ���������Ȩ���ʵ���, �ͻ�����һ���յ�ResultReader��ռλ,
    * ����յ�ResultReader��validֵ��Ϊfalse.
    *
    * ע: ��������ÿյ�ResultReaderռλ�Ļ�, ��index����ʱ�ͻ����.
    */
   boolean isValid() throws ConfigurationException;

   /**
    * �ж϶�ȡ����ʱ, �Ƿ���ͨ����������ȡ.
    */
   boolean isUseColumnName() throws ConfigurationException;

   /**
    * �ж϶�ȡ����ʱ, �Ƿ���ͨ������ֵ����ȡ.
    */
   boolean isUseColumnIndex() throws ConfigurationException;

   /**
    * ���һ��attribute.
    *
    * @param name    Ҫ��õ�attribute������
    */
   Object getAttribute(String name) throws ConfigurationException;

   /**
    * �������attribute������.
    */
   String[] getAttributeNames() throws ConfigurationException;

   /**
    * ��ȡ�ɶ�ȡ���е�Ȩ�޼���, ֻ��ӵ�м����е�����1��Ȩ�޾Ϳ���
    * ��ȡ����. <p>
    * ���û������Ȩ�޼���, �򷵻�null, ��ʾ��ȡ���в���ҪȨ��.
    */
   PermissionSet getPermissionSet() throws ConfigurationException;

   /**
    * ��ȡ���еı���.
    */
   String getCaption() throws ConfigurationException;

   /**
    * ��ȡ�����ı���. <p>
    * �������Ϊ��, ���ʹ������������.
    */
   String getFilledCaption() throws ConfigurationException;

   /**
    * ��ȡ������ʾʱ�Ŀ��.
    */
   int getWidth() throws ConfigurationException;

   /**
    * ��<code>ResultSet</code>�����ж�ȡ����, ������Ӧ�Ķ��󷵻�.
    */
   Object readResult(ResultSet rs) throws SQLException;

   /**
    * ��<code>CallableStatement</code>�����ж�ȡ����, ������Ӧ�Ķ��󷵻�.
    */
   Object readCall(CallableStatement call, int index) throws SQLException;

   /**
    * ��<code>Object</code>�����ж�ȡ����, ������Ӧ�Ķ��󷵻�.
    */
   Object readObject(Object obj) throws ConfigurationException;

}
