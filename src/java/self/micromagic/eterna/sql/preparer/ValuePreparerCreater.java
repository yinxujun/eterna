
package self.micromagic.eterna.sql.preparer;

/**
 * ֵ׼�����Ĵ�����.
 */
public abstract class ValuePreparerCreater
{
   /**
    * ����һ��Object���͵�ֵ����һ��ֵ׼����.
    *
    * @param value    ֵ
    * @return      ֵ׼����
    */
   public abstract ValuePreparer createPreparer(Object value);

   /**
    * ����һ��String���͵�ֵ����һ��ֵ׼����.
    *
    * @param value    ֵ
    * @return      ֵ׼����
    */
   public abstract ValuePreparer createPreparer(String value);

}
