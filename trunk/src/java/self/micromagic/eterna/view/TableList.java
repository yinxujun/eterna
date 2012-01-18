
package self.micromagic.eterna.view;

import java.util.Iterator;

import self.micromagic.eterna.digester.ConfigurationException;

public interface TableList extends Component
{
   public static final String TR_NAME_PERFIX = "tableList_TR";

   boolean isAutoArrange() throws ConfigurationException;

   boolean isPercentWidth() throws ConfigurationException;

   boolean isCaculateWidth() throws ConfigurationException;

   int getCaculateWidthFix() throws ConfigurationException;

   Component getTR() throws ConfigurationException;

   String getBaseName() throws ConfigurationException;

   String getDataName() throws ConfigurationException;

   Iterator getColumns() throws ConfigurationException;

   interface Column extends Component
   {
      int getWidth() throws ConfigurationException;

      String getTitleParam() throws ConfigurationException;

      String getContainerParam() throws ConfigurationException;

      boolean isIgnoreGlobalTitleParam() throws ConfigurationException;

      boolean isIgnoreGlobalContainerParam() throws ConfigurationException;

      String getCaption() throws ConfigurationException;

      String getDefaultValue() throws ConfigurationException;

      boolean isIgnore() throws ConfigurationException;

      String getSrcName() throws ConfigurationException;

      String getDataName() throws ConfigurationException;

      boolean isOtherData() throws ConfigurationException;

      Component getTypicalComponent() throws ConfigurationException;

      String getInitParam() throws ConfigurationException;

   }
}
