
package self.micromagic.dc;

import self.micromagic.util.StringAppender;
import self.micromagic.util.StringTool;

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
         StringAppender buf = StringTool.createStringAppender(256);
         buf.append("CodeErrorInfo:[\n   position:").append(this.position).append("\n")
               .append("   error:").append(this.error.getMessage()).append("\n")
               .append("   code:\n").append(this.code).append("\n")
               .append(" ]");
         this.message = buf.toString();
      }
      return this.message;
   }

}
