
package self.micromagic.cg;

/**
 * 判断给出的类名是否为bean的检查器.
 */
public interface BeanChecker
{
   /**
    * 检查结果，是.
    */
   public static final int CHECK_RESULT_YES = 1;

   /**
    * 检查结果，否.
    */
   public static final int CHECK_RESULT_NO = -1;

   /**
    * 检查结果，不明.
    */
   public static final int CHECK_RESULT_UNKNOW = 0;


   public int check(Class beanClass);

}
