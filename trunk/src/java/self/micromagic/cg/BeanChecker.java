
package self.micromagic.cg;

/**
 * �жϸ����������Ƿ�Ϊbean�ļ����.
 */
public interface BeanChecker
{
   /**
    * ���������.
    */
   public static final int CHECK_RESULT_YES = 1;

   /**
    * ���������.
    */
   public static final int CHECK_RESULT_NO = -1;

   /**
    * �����������.
    */
   public static final int CHECK_RESULT_UNKNOW = 0;


   public int check(Class beanClass);

}
