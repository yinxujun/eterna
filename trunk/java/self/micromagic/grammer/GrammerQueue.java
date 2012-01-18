
package self.micromagic.grammer;

import java.util.Map;

import self.micromagic.util.Utils;

public class GrammerQueue extends AbstractElement
      implements GrammerElement
{
   private String queue = "";

   public void initialize(Map elements) {}

   public boolean isTypeNone()
   {
      return this.getType() == TYPE_NONE;
   }

   public void setQueue(String queue)
   {
      this.queue = queue;
   }

   public boolean doVerify(ParserData pd)
         throws GrammerException
   {
      for (int i = 0; i < this.queue.length(); i++)
      {
         if (pd.isEnd())
         {
            return false;
         }
         char c = 0;
         try
         {
            c = pd.getNextChar();
         }
         catch (GrammerException ex)
         {
            if (pd.isEnd())
            {
               return false;
            }
            throw ex;
         }
         if (c != this.queue.charAt(i))
         {
            return false;
         }
      }
      return true;
   }

   public String toString()
   {
      return "Queue:" + this.getName() + ":" + GrammerManager.getGrammerElementTypeName(this.getType())
            + ":Q[" + Utils.dealString2EditCode(this.queue) + "]";
   }


}
