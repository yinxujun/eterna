
package self.micromagic.eterna.view;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.share.EternaFactory;

public interface Component
{
   void initialize(EternaFactory factory, Component parent) throws ConfigurationException;

   String getName() throws ConfigurationException;

   String getType() throws ConfigurationException;

   Component getParent() throws ConfigurationException;

   Iterator getSubComponents() throws ConfigurationException;

   Iterator getEvents() throws ConfigurationException;

   boolean isIgnoreGlobalParam() throws ConfigurationException;

   String getComponentParam() throws ConfigurationException;

   String getBeforeInit() throws ConfigurationException;

   String getInitScript() throws ConfigurationException;

   /**
    * 获取本Component某个设置的属性.
    */
   Object getAttribute(String name) throws ConfigurationException;

   /**
    * 获取本Component设置的所有属性的名称.
    */
   String[] getAttributeNames() throws ConfigurationException;

   EternaFactory getFactory() throws ConfigurationException;

   ViewAdapter.ViewRes getViewRes() throws ConfigurationException;

   void print(Writer out, AppData data, ViewAdapter view) throws IOException, ConfigurationException;

   void printBody(Writer out, AppData data, ViewAdapter view) throws IOException, ConfigurationException;

   void printSpecialBody(Writer out, AppData data, ViewAdapter view) throws IOException, ConfigurationException;

   interface Event
   {
      void initialize(Component component) throws ConfigurationException;

      String getName() throws ConfigurationException;

      String getScriptParam() throws ConfigurationException;

      String getScriptBody() throws ConfigurationException;

      Component getComponent() throws ConfigurationException;

      ViewAdapter.ViewRes getViewRes() throws ConfigurationException;

   }

}
