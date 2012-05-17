
package self.micromagic.eterna.sql.preparer;

import java.io.InputStream;
import java.io.Reader;

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
    * ��ȡ���ɴ˹����ߵĹ���.
    */
   EternaFactory getFactory() throws ConfigurationException;

   /**
    * �Ƿ�Ҫ�����ַ�����Ϊnull.
    */
   boolean isEmptyStringToNull();

   /**
    * ����type����������ص�ֵ׼�����Ĵ�����.
    *
    * @return   ֵ׼�����Ĵ�����
    */
   ValuePreparerCreater createValuePreparerCreater(int pureType) throws ConfigurationException;

   /**
    * ����һ��null���͵�ֵ׼����.
    *
    * @param index   ��Ӧ�������������ֵ
    * @param type    ��<code>java.sql.Types</code>�ж����SQL��
    */
   ValuePreparer createNullPreparer(int index, int type) throws ConfigurationException;

   /**
    * ����һ��boolean���͵�ֵ׼����.
    *
    * @param index   ��Ӧ�������������ֵ
    * @param v       ֵ
    */
   ValuePreparer createBooleanPreparer(int index, boolean v) throws ConfigurationException;

   /**
    * ����һ��byte���͵�ֵ׼����.
    *
    * @param index   ��Ӧ�������������ֵ
    * @param v       ֵ
    */
   ValuePreparer createBytePreparer(int index, byte v) throws ConfigurationException;

   /**
    * ����һ��byte�������͵�ֵ׼����.
    *
    * @param index   ��Ӧ�������������ֵ
    * @param v       ֵ
    */
   ValuePreparer createBytesPreparer(int index, byte[] v) throws ConfigurationException;

   /**
    * ����һ��short���͵�ֵ׼����.
    *
    * @param index   ��Ӧ�������������ֵ
    * @param v       ֵ
    */
   ValuePreparer createShortPreparer(int index, short v) throws ConfigurationException;

   /**
    * ����һ��int���͵�ֵ׼����.
    *
    * @param index   ��Ӧ�������������ֵ
    * @param v       ֵ
    */
   ValuePreparer createIntPreparer(int index, int v) throws ConfigurationException;

   /**
    * ����һ��long���͵�ֵ׼����.
    *
    * @param index   ��Ӧ�������������ֵ
    * @param v       ֵ
    */
   ValuePreparer createLongPreparer(int index, long v) throws ConfigurationException;

   /**
    * ����һ��float���͵�ֵ׼����.
    *
    * @param index   ��Ӧ�������������ֵ
    * @param v       ֵ
    */
   ValuePreparer createFloatPreparer(int index, float v) throws ConfigurationException;

   /**
    * ����һ��double���͵�ֵ׼����.
    *
    * @param index   ��Ӧ�������������ֵ
    * @param v       ֵ
    */
   ValuePreparer createDoublePreparer(int index, double v) throws ConfigurationException;

   /**
    * ����һ��String���͵�ֵ׼����.
    *
    * @param index   ��Ӧ�������������ֵ
    * @param v       ֵ
    */
   ValuePreparer createStringPreparer(int index, String v) throws ConfigurationException;

   /**
    * ����һ��Stream���͵�ֵ׼����.
    *
    * @param index   ��Ӧ�������������ֵ
    * @param v       ֵ
    */
   ValuePreparer createStreamPreparer(int index, InputStream v, int length) throws ConfigurationException;

   /**
    * ����һ��Reader���͵�ֵ׼����.
    *
    * @param index   ��Ӧ�������������ֵ
    * @param v       ֵ
    */
   ValuePreparer createReaderPreparer(int index, Reader v, int length) throws ConfigurationException;

   /**
    * ����һ��Date���͵�ֵ׼����.
    *
    * @param index   ��Ӧ�������������ֵ
    * @param v       ֵ
    */
   ValuePreparer createDatePreparer(int index, java.sql.Date v) throws ConfigurationException;

   /**
    * ����һ��Time���͵�ֵ׼����.
    *
    * @param index   ��Ӧ�������������ֵ
    * @param v       ֵ
    */
   ValuePreparer createTimePreparer(int index, java.sql.Time v) throws ConfigurationException;

   /**
    * ����һ��Timestamp���͵�ֵ׼����.
    *
    * @param index   ��Ӧ�������������ֵ
    * @param v       ֵ
    */
   ValuePreparer createTimestampPreparer(int index, java.sql.Timestamp v) throws ConfigurationException;

   /**
    * ����һ��Object���͵�ֵ׼����.
    *
    * @param index   ��Ӧ�������������ֵ
    * @param v       ֵ
    */
   ValuePreparer createObjectPreparer(int index, Object v) throws ConfigurationException;

}
