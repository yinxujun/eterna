
package self.micromagic.eterna.sql.preparer;

/**
 * 值准备器的创建者.
 */
public abstract class ValuePreparerCreater
{
   /**
    * 根据一个Object类型的值创建一个值准备器.
    *
    * @param value    值
    * @return      值准备器
    */
   public abstract ValuePreparer createPreparer(Object value);

   /**
    * 根据一个String类型的值创建一个值准备器.
    *
    * @param value    值
    * @return      值准备器
    */
   public abstract ValuePreparer createPreparer(String value);

}
