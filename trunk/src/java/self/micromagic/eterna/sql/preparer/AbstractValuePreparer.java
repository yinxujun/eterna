
package self.micromagic.eterna.sql.preparer;

import org.apache.commons.logging.Log;
import self.micromagic.util.Utility;

public abstract class AbstractValuePreparer
      implements ValuePreparer
{
   protected static final Log log = Utility.createLog("eterna");

   protected int index;
   protected String name;

   public void setName(String name)
   {
      this.name = name;
   }

   public String getName()
   {
      return this.name;
   }

   public void setRelativeIndex(int index)
   {
      this.index = index;
   }

   public int getRelativeIndex()
   {
      return this.index;
   }

}
