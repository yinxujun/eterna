
package self.micromagic.eterna.digester;

import java.lang.reflect.Constructor;
import java.util.Map;

import org.xml.sax.Attributes;
import self.micromagic.util.Utility;

public class ObjectCreateRule extends MyRule
{
   public static final Class[] defaultConstructorParamClass = new Class[0];
   public static final Object[] defaultConstructorParam = new Object[0];

   protected String attributeName = null;
   protected String className = null;
   protected Class classType = null;

   public ObjectCreateRule(String className, String attributeName, Class classType)
   {
      this.className = className;
      this.attributeName = attributeName;
      this.classType = classType;
   }

   public static String getClassName(String attributeName, String className, Attributes attributes)
   {
      String realClassName = className;
      if (attributeName != null)
      {
         String value = attributes.getValue(attributeName);
         if (value != null)
         {
            realClassName = value;
         }
      }
      return realClassName;
   }

   public static Object createObject(String className)
         throws ClassNotFoundException, InstantiationException, IllegalAccessException
   {
      return createObject(className, true);
   }

   public static Object createObject(String className, boolean mustCreate)
         throws ClassNotFoundException, InstantiationException, IllegalAccessException
   {
      try
      {
         Class theClass;
         if (className.startsWith("c|"))
         {
            Map cache = FactoryManager.getInitCache();
            theClass = (Class) cache.get(className);
         }
         else
         {
            theClass = Class.forName(className);
         }
         Object instance;
         try
         {
            Constructor constructor = theClass.getDeclaredConstructor(
                  defaultConstructorParamClass);
            constructor.setAccessible(true);
            instance = constructor.newInstance(defaultConstructorParam);
         }
         catch (Exception ex)
         {
            instance = theClass.newInstance();
         }
         return instance;
      }
      catch (ClassNotFoundException ex)
      {
         return ObjectCreateRule.createObject(
               className, Utility.getContextClassLoader(), mustCreate);
      }
   }

   public static Object createObject(String className, ClassLoader loader, boolean mustCreate)
         throws ClassNotFoundException, InstantiationException, IllegalAccessException
   {
      try
      {
         Class theClass = Class.forName(className, true, loader);
         Object instance;
         try
         {
            Constructor constructor = theClass.getDeclaredConstructor(
                  defaultConstructorParamClass);
            constructor.setAccessible(true);
            instance = constructor.newInstance(defaultConstructorParam);
         }
         catch (Exception ex)
         {
            instance = theClass.newInstance();
         }
         return instance;
      }
      catch (ClassNotFoundException ex)
      {
         if (mustCreate)
         {
            throw ex;
         }
         return null;
      }
   }

   public static void checkType(Class classType, Object instance)
         throws InvalidAttributesException
   {
      if (classType != null && !classType.isInstance(instance))
      {
         throw new InvalidAttributesException("The class '" + instance.getClass().getName()
               + "' is not instance of " + classType.getName());
      }
   }

   public void myBegin(String namespace, String name, Attributes attributes)
         throws Exception
   {
      String realClassName = ObjectCreateRule.getClassName(
            this.attributeName, this.className, attributes);
      this.digester.getLogger().debug("New " + realClassName);
      Object instance = ObjectCreateRule.createObject(realClassName);
      ObjectCreateRule.checkType(this.classType, instance);
      this.digester.push(instance);
   }

   public void myEnd(String namespace, String name)
         throws Exception
   {
      Object top = this.digester.pop();
      this.digester.getLogger().debug("Pop " + top.getClass().getName());
   }

}
