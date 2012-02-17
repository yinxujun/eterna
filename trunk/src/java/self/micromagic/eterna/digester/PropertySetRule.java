
package self.micromagic.eterna.digester;

import org.apache.commons.digester.Digester;
import org.xml.sax.Attributes;

/**
 * ���ö������Եĳ�ʼ������.
 */
public class PropertySetRule extends MyRule
{
   protected PropertySetter[] setters;
   protected PropertySetter singleSetter;
   protected boolean pushStack = true;

   /**
    * @param setters     ��Ҫ���õĶ������
    * @param pushStack   �Ƿ���Ҫ�����õ�ֵѹ���ջ��, Ĭ��Ϊtrue
    */
   public PropertySetRule(PropertySetter[] setters, boolean pushStack)
   {
      this.setters = setters;
      this.pushStack = pushStack;
      if (this.setters.length == 1)
      {
         this.singleSetter = this.setters[0];
      }
   }

   /**
    * @param setter      ��Ҫ���õ�����
    * @param pushStack   �Ƿ���Ҫ�����õ�ֵѹ���ջ��, Ĭ��Ϊtrue
    */
   public PropertySetRule(PropertySetter setter, boolean pushStack)
   {
      this(new PropertySetter[]{setter}, pushStack);
   }

   /**
    * @param setter      ��Ҫ���õ�����
    */
   public PropertySetRule(PropertySetter setter)
   {
      this(new PropertySetter[]{setter}, true);
   }

   /**
    * @param attributeName   �����л�ȡֵ������
    * @param methodName      ��������ֵ�ķ���
    * @param mustExist       xml���Լ����Ƿ���������Ҫ��ֵ
    * @param pushStack       �Ƿ���Ҫ�����õ�ֵѹ���ջ��, Ĭ��Ϊtrue
    */
   public PropertySetRule(String attributeName, String methodName,
         boolean mustExist, boolean pushStack)
   {
      this(new StringPropertySetter(attributeName, methodName, mustExist),
            pushStack);
   }

   public void setDigester(Digester digester)
   {
      super.setDigester(digester);
      for (int i = 0; i < setters.length; i++)
      {
         this.setters[i].setDigester(digester);
      }
      if (this.singleSetter != null)
      {
         this.useBodyText = this.singleSetter.requireBodyValue();
      }
   }

   public void myBegin(String namespace, String name, Attributes attributes)
         throws Exception
   {
      Object obj;
      if (this.singleSetter != null)
      {
         obj = this.singleSetter.prepareProperty(namespace, name, attributes);
      }
      else
      {
         Object[] array = new Object[this.setters.length];
         for (int i = 0; i < setters.length; i++)
         {
            array[i] = this.setters[i].prepareProperty(namespace, name, attributes);
         }
         obj = array;
      }
      if (this.pushStack)
      {
         this.digester.push(obj);
      }
   }

   public void myBody(String namespace, String name, BodyText text)
         throws Exception
   {
      if (this.singleSetter != null)
      {
         Object obj = this.singleSetter.prepareProperty(namespace, name, text);
         if (this.pushStack)
         {
            this.digester.pop();
            this.digester.push(obj);
         }
      }
   }

   public void myEnd(String namespace, String name)
         throws Exception
   {
      if (this.pushStack)
      {
         Object top = this.digester.pop();
         this.digester.getLogger().debug("Pop " + top.getClass().getName());
      }
      for (int i = 0; i < setters.length; i++)
      {
         this.setters[i].setProperty();
      }
   }

}
