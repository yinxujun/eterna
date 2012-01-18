
package self.micromagic.grammer;

/**
 * �﷨����г����쳣.
 */
public class GrammerException extends Exception
{
   private Checker checker = null;

   /**
    * @param message   ������Ϣ
    */
   public GrammerException(String message)
   {
      super(message);
   }

   public GrammerException(Checker checker)
   {
      this.checker = checker;
   }

   public GrammerException(Exception cause)
   {
      super(cause);
   }

   public Checker getChecker()
   {
      return this.checker;
   }

}
