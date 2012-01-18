
package self.micromagic.util;

import java.util.ArrayList;
import java.util.List;

class StringList
{
   private List strList = new ArrayList();

   public synchronized StringList append(char str[], int offset, int len)
   {
      this.strList.add(new String(str, offset, len));
      return this;
   }

   public synchronized String get(int index)
   {
      return (String) this.strList.get(index);
   }

   public synchronized String toString()
   {
      StringBuffer temp = new StringBuffer();

      return null;
   }

}
