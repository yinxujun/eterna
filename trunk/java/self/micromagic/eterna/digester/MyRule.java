
package self.micromagic.eterna.digester;

import org.apache.commons.digester.Rule;
import org.xml.sax.Attributes;

public abstract class MyRule extends Rule
{
   static boolean dealRule = true;

   protected boolean useBodyText = false;

   public MyRule()
   {
   }

   public void body(String namespace, String name, String text)
         throws Exception
   {
      if (dealRule && this.useBodyText)
      {
         BodyText temp = new BodyText();
         temp.append(text.toCharArray(), 0, text.length());
         this.myBody(namespace, name, temp);
      }
   }

   public void myBody(String namespace, String name, BodyText text)
         throws Exception
   {
   }

   public void begin(String namespace, String name, Attributes attributes)
         throws Exception
   {
      if (dealRule)
      {
         this.myBegin(namespace, name, attributes);
      }
   }

   public void myBegin(String namespace, String name, Attributes attributes)
         throws Exception
   {
   }

   public void end(String namespace, String name)
         throws Exception
   {
      if (dealRule)
      {
         this.myEnd(namespace, name);
      }
   }

   public void myEnd(String namespace, String name)
         throws Exception
   {
   }

}
