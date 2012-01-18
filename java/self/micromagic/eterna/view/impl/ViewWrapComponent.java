
package self.micromagic.eterna.view.impl;

import java.util.Iterator;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.view.Component;
import self.micromagic.eterna.view.ViewAdapter;
import self.micromagic.util.container.UnmodifiableIterator;

public class ViewWrapComponent extends ComponentImpl
      implements Component
{
   protected ViewAdapter view;

   public ViewWrapComponent(ViewAdapter view)
   {
      this.view = view;
   }

   public void initialize(EternaFactory factory, Component parent)
         throws ConfigurationException
   {
      if (this.initialized)
      {
         return;
      }
      this.eternaFactory = factory;
      this.initialized = true;
      this.stringCoder = factory.getStringCoder();
      Iterator subComponentItr = this.getSubComponents();
      while (subComponentItr.hasNext())
      {
         Component sub = (Component) subComponentItr.next();
         sub.initialize(factory, null);
      }
   }

   public String getName()
         throws ConfigurationException
   {
      return this.view.getName();
   }

   public String getType()
   {
      return "none";
   }

   public Component getParent()
   {
      return null;
   }

   public Iterator getSubComponents()
         throws ConfigurationException
   {
      return this.view.getComponents();
   }

   public Iterator getEvents()
   {
      return UnmodifiableIterator.EMPTY_ITERATOR;
   }

}
