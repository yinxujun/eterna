
package self.micromagic.cg;

import java.lang.reflect.Field;

import self.micromagic.util.StringRef;

/**
 * ��һ�����Ե�Ԫ�Ĵ���������.
 */
public interface UnitProcesser
{
   /**
    * ��ö����ԵĴ������.
    *
    * @param f              ���Զ���
    * @param type           ���Ե�����
    * @param wrapName       ����ǻ������͵Ļ�, �⸲�������
    * @param processerType  ��������, д���
    * @param cg             ���ɴ�����Ĵ���������
    * @return   ��������ԵĴ������
    */
   public String getFieldCode(Field f, Class type, String wrapName, int processerType, ClassGenerator cg);

   /**
    * ��öԷ����Ĵ������.
    *
    * @param m              ������Ϣ
    * @param type           ���Ե�����
    * @param wrapName       ����ǻ������͵Ļ�, �⸲�������
    * @param processerType  ��������, д���
    * @param cg             ���ɴ�����Ĵ���������
    * @return   ����������Ĵ������
    */
   public String getMethodCode(BeanMethodInfo m, Class type, String wrapName, int processerType,
         ClassGenerator cg);

}
