
package self.micromagic.eterna.share;

public class EternaException extends Exception
{
   /**
    * ����һ��<code>EternaException</code>.
    */
   public EternaException()
   {
      super();
   }

   /**
    * ͨ������<code>message</code>������һ��<code>EternaException</code>.
    *
    * @param message   ������Ϣ
    */
   public EternaException(String message)
   {
      super(message);
   }

   /**
    * ͨ��һ���׳��Ķ���������һ��<code>EternaException</code>.
    *
    * @param origin    �쳣�����
    */
   public EternaException(Throwable origin)
   {
      super(origin);
   }

   /**
    * ͨ������<code>message</code>��һ���׳��Ķ���������һ��<code>EternaException</code>.
    *
    * @param message   ������Ϣ
    * @param origin    �쳣�����
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
