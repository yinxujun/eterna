
package self.micromagic.eterna.digester;

import org.apache.commons.digester.Digester;
import org.xml.sax.Attributes;

/**
 * 设置对象属性的初始化规则.
 */
public class PropertySetRule extends MyRule
{
   protected PropertySetter[] setters;
   protected PropertySetter singleSetter;
   protected boolean pushStack = true;

   /**
    * @param setters     需要设置的多个属性
    * @param pushStack   是否需要将设置的值压入堆栈中, 默认为true
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
    * @param setter      需要设置的属性
    * @param pushStack   是否需要将设置的值压入堆栈中, 默认为true
    */
   public PropertySetRule(PropertySetter setter, boolean pushStack)
   {
      this(new PropertySetter[]{setter}, pushStack);
   }

   /**
    * @param setter      需要设置的属性
    */
   public PropertySetRule(PropertySetter setter)
   {
      this(new PropertySetter[]{setter}, true);
   }

   /**
    * @param attributeName   配置中获取值的属性
    * @param methodName      设置属性值的方法
    * @param mustExist       xml属性集中是否必须存在需要的值
    * @param pushStack       是否需要将设置的值压入堆栈中, 默认为true
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
