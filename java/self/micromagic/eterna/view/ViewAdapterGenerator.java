
package self.micromagic.eterna.view;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.AdapterGenerator;

public interface ViewAdapterGenerator extends AdapterGenerator
{
   void setName(String name) throws ConfigurationException;

   void setDynamicViewRes(String res) throws ConfigurationException;

   void addComponent(Component com) throws ConfigurationException;

   void deleteComponent(Component com) throws ConfigurationException;

   void clearComponents() throws ConfigurationException;

   void setDebug(int debug) throws ConfigurationException;

   void setWidth(String width) throws ConfigurationException;

   void setHeight(String height) throws ConfigurationException;

   void setBeforeInit(String condition) throws ConfigurationException;

   void setInitScript(String body) throws ConfigurationException;

   ViewAdapter createViewAdapter() throws ConfigurationException;

   interface ModifiableViewRes extends ViewAdapter.ViewRes
   {
      /**
       * 添加一个方法.
       *
       * @return  添加的方法名称, 方法名称可能会根据当前环境有所变化.
       */
      public String addFunction(Function fn) throws ConfigurationException;

      public void addTypicalComponentNames(String name) throws ConfigurationException;

      public void addResourceNames(String name) throws ConfigurationException;

      public void addAll(ViewAdapter.ViewRes res) throws ConfigurationException;

   }

}
