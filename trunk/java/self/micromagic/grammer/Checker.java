
package self.micromagic.grammer;

/**
 * �ַ�������ӿ�.
 */
public interface Checker
{
   /**
    * ��һ���ַ����м��.
    *
    * @param pd  ����������.
    * @return    ��������Ϊ�Ϸ�, �򷵻�true..
    * @throws GrammerException     ����з����﷨����.
    */
   boolean verify(ParserData pd) throws GrammerException;

}
