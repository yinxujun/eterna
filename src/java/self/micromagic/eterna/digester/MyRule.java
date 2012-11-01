
package self.micromagic.eterna.digester;

import org.apache.commons.digester.Rule;
import org.xml.sax.Attributes;

/**
 * ����ĳ�ʼ������, �ṩ�˹��õķ������Ƿ�ɳ�ʼ���Ŀ���.
 *
 * @author micromagic@sina.com
 */
public abstract class MyRule extends Rule
{
   /**
    * �Ƿ��ִ�г�ʼ��.
    * ���Ǹ���̬��, ���ڳ�ʼ��ʱֻ����һ���߳��н���, �������ﲻ���ж��̵߳�����.
    */
   static boolean dealRule = true;

   /**
    * �Ƿ�ʹ��body�ı�������.
    */
   protected boolean useBodyText = false;

   public MyRule()
   {
   }

   /**
    * �̳и���Ĵ���body�ı�.
    * ���ж��Ƿ��ִ�м��Ƿ�ʹ��body�ı�, ͨ���Ļ��ŵ���myBody.
    */
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

   /**
    * ʵ�ʴ���body�ı��ķ���, �̳�������д�˷���ʵ�������Ĵ���.
    */
   public void myBody(String namespace, String name, BodyText text)
         throws Exception
   {
   }

   /**
    * �̳и���Ĵ���ڵ㿪ʼ.
    * ���ж��Ƿ��ִ��, ͨ���Ļ��ŵ���myBegin.
    */
   public void begin(String namespace, String name, Attributes attributes)
         throws Exception
   {
      if (dealRule)
      {
         this.myBegin(namespace, name, attributes);
      }
   }

   /**
    * ʵ�ʴ���ڵ㿪ʼ�ķ���, �̳�������д�˷���ʵ�������Ĵ���.
    */
   public void myBegin(String namespace, String name, Attributes attributes)
         throws Exception
   {
   }

   /**
    * �̳и���Ĵ���ڵ����.
    * ���ж��Ƿ��ִ��, ͨ���Ļ��ŵ���myEnd.
    */
   public void end(String namespace, String name)
         throws Exception
   {
      if (dealRule)
      {
         this.myEnd(namespace, name);
      }
   }

   /**
    * ʵ�ʴ���ڵ�����ķ���, �̳�������д�˷���ʵ�������Ĵ���.
    */
   public void myEnd(String namespace, String name)
         throws Exception
   {
   }

}
