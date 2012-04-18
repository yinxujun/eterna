
package self.micromagic.dc;

/**
 * ��̬������ʱ�ĳ�����Ϣ.
 */
public class CodeErrorInfo
{
   /**
    * ��Ҫ��̬����Ĵ���.
    */
   public final String code;

   /**
    * �������ڵ�λ����Ϣ.
    */
   public final String position;

   /**
    * ������쳣��Ϣ.
    */
   public final Exception error;

   public CodeErrorInfo(String code, String position, Exception error)
   {
      this.code = code;
      this.position = position;
      this.error = error;
   }

   private String message;

   public String toString()
   {
      if (this.message == null)
      {
         StringBuffer buf = new StringBuffer(256);
         buf.append("CodeErrorInfo:[\n   position:").append(this.position).append("\n")
               .append("   error:").append(this.error.getMessage()).append("\n")
               .append("   code:\n").append(this.code).append("\n")
               .append(" ]");
         this.message = buf.toString();
      }
      return this.message;
   }

}
