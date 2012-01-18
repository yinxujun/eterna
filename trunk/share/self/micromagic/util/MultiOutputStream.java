
package self.micromagic.util;

import java.io.IOException;
import java.io.OutputStream;

public class MultiOutputStream extends OutputStream
{
   private OutputStream out1;
   private OutputStream out2;

   public MultiOutputStream(OutputStream out1, OutputStream out2)
   {
      this.out1 = out1;
      this.out2 = out2;
   }

   public void write(int b)
         throws IOException
   {
      this.out1.write(b);
      this.out2.write(b);
   }

   public void write(byte b[])
         throws IOException
   {
      this.out1.write(b);
      this.out2.write(b);
   }

   public void write(byte b[], int off, int len)
         throws IOException
   {
      this.out1.write(b, off, len);
      this.out2.write(b, off, len);
   }

   public void flush()
         throws IOException
   {
      this.out1.flush();
      this.out2.flush();
   }

   public void close()
         throws IOException
   {
      this.out1.close();
      this.out2.close();
   }

}
