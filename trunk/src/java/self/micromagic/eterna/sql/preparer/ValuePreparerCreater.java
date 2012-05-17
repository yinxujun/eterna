
package self.micromagic.eterna.sql.preparer;

import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.digester.ConfigurationException;

/**
 * ֵ׼����������.
 */
public interface ValuePreparerCreater
{
   /**
    * ����Ĭ��ʹ�õ�ValuePreparerCreater.
    * ����һ��factory��arrtibute, ����ֵ���������vpc������.
    * ���û��ָ��, factory���Զ�����һ��Ĭ�ϵ�.
    */
   public static final String DEFAULT_VPC_ATTRIBUTE = "default.vpc.name";

   /**
    * ��vpc��factory������������, �Ƿ�Ҫ�����ַ�����Ϊnull, Ĭ��ֵΪtrue.
    * ���vpc��������ֵ, �����factory�е�����.
    */
   public static final String EMPTY_STRING_TO_NULL = "sql.emptyStringToNull";

   /**
    * ��ȡ����ֵ׼���������ߵĹ���.
    */
   EternaFactory getFactory() throws ConfigurationException;

   /**
    * ���һ�����õ�����.
    *
    * @param name    ���Ե�����
    * @return        ���Ե�ֵ
    */
   Object getAttribute(String name) throws ConfigurationException;

   /**
    * �Ƿ�Ҫ�����ַ�����Ϊnull.
    */
   boolean isEmptyStringToNull();

   /**
    * ����һ��Object���͵�ֵ����һ��ֵ׼����.
    *
    * @param value    ֵ
    * @return      ֵ׼����
    */
   ValuePreparer createPreparer(Object value) throws ConfigurationException;

   /**
    * ����һ��String���͵�ֵ����һ��ֵ׼����.
    *
    * @param value    ֵ
    * @return      ֵ׼����
    */
   ValuePreparer createPreparer(String value) throws ConfigurationException;

}
