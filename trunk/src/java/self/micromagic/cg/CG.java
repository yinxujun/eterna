
package self.micromagic.cg;

import org.apache.commons.logging.Log;
import self.micromagic.util.Utility;

/**
 * ��ı���ӿ�, ����ʵ�ֲ�ͬ�ı��뷽ʽ.
 */
public interface CG
{
   /**
    * ������Ʊ�����. �ڴ����ڲ�, ��Ҫ�����౾���д���캯��ʱ, ��Ҫ�õ�������. <p>
    * �磺
    * ���캯��   public ${thisName}()
    * ���屾��   ${thisName} value = new ${thisName}();
    */
   public static final String THIS_NAME = "thisName";

   /**
    * ���öԴ�����������.
    */
   public static final String COMPILE_TYPE_PROPERTY = "self.micromagic.compile.type";

   /**
    * ��ant��Ϊ��������ʱʹ�õ�����.
    */
   public static final String COMPILE_TYPE_ANT = AntCG.COMPILE_TYPE;

   /**
    * ��javassist��Ϊ��������ʱʹ�õ�����.
    */
   public static final String COMPILE_TYPE_JAVASSIST = JavassistCG.COMPILE_TYPE;

   /**
    * �����Ƿ�Ҫ������붯̬������ص���־��Ϣ.
    * �����õ�ֵ����:
    * 1. ֻ��¼������Ϣ
    * 2. ��¼��������е�һЩ��Ϣ
    * 3. ��¼���ɵĴ�����Ϣ
    */
   public static final String COMPILE_LOG_PROPERTY = "self.micromagic.compile.log";

   /**
    * 1 (> 0). ֻ��¼������Ϣ
    */
   public static final int COMPILE_LOG_TYPE_ERROR = 0;

   /**
    * 2 (> 1). ��¼��������е�һЩ��Ϣ
    */
   public static final int COMPILE_LOG_TYPE_INFO = 1;

   /**
    * 3 (> 2). ��¼���ɵĴ�����Ϣ
    */
   public static final int COMPILE_LOG_TYPE_DEBUG = 2;

   /**
    * ���ڼ�¼��־.
    */
   static final Log log = Utility.createLog("cg");

   /**
    * ����һ����.
    *
    * @param cg   ������ʱ��Ҫʹ�õ����๹����.
    */
   Class createClass(ClassGenerator cg) throws Exception;

}
