
package self.micromagic.eterna.sql.preparer;

/**
 * Created by IntelliJ IDEA.
 * User: micromagic
 * Date: 2009-3-3
 * Time: 11:06:07
 * To change this template use Options | File Templates.
 */
public abstract class ValuePreparerCreater
{
   public abstract ValuePreparer createPreparer(Object value);

   public abstract ValuePreparer createPreparer(String value);

}
