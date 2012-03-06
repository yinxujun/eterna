
package self.micromagic.eterna.sql.preparer;

import org.apache.commons.logging.Log;
import self.micromagic.eterna.share.Tool;

public abstract class AbstractValuePreparer
      implements ValuePreparer
{
   protected static final Log log = Tool.log;

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
