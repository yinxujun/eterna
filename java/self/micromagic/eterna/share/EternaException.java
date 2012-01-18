
package self.micromagic.eterna.share;

public class EternaException extends Exception
{
   /**
    * 构造一个<code>EternaException</code>.
    */
   public EternaException()
   {
      super();
   }

   /**
    * 通过参数<code>message</code>来构造一个<code>EternaException</code>.
    *
    * @param message   出错信息
    */
   public EternaException(String message)
   {
      super(message);
   }

   /**
    * 通过一个抛出的对象来构造一个<code>EternaException</code>.
    *
    * @param origin    异常或错误
    */
   public EternaException(Throwable origin)
   {
      super(origin);
   }

   /**
    * 通过参数<code>message</code>和一个抛出的对象来构造一个<code>EternaException</code>.
    *
    * @param message   出错信息
    * @param origin    异常或错误
    */
   public EternaException(String message, Throwable origin)
   {
      super(message, origin);
   }

   public String getMessage()
   {
      String msg = super.getMessage();
      if (msg == null && this.getCause() != null)
      {
         msg = this.getCause().getMessage();
      }
      return msg;
   }

}
