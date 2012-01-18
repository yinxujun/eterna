
package self.micromagic.eterna.view;

import self.micromagic.eterna.digester.ConfigurationException;

public interface TableListGenerator extends ComponentGenerator
{
   void setAutoArrange(boolean autoArrange) throws ConfigurationException;

   void setPercentWidth(boolean percentWidth) throws ConfigurationException;

   void setCaculateWidth(boolean caculateWidth) throws ConfigurationException;

   void setCaculateWidthFix(int caculateWidthFix) throws ConfigurationException;

   void setTR(Component tr) throws ConfigurationException;

   void setColumnOrder(String order) throws ConfigurationException;

   /**
    * @param name  ������һ��query       ������[query:]��ʼ��ֱ��query������
    *              ������һ��readManager ������[reader:]��ʼ�������readerManager������
    *              ������һ��search      ������[search:]��ʼ�������search������
    */
   void setBaseName(String name) throws ConfigurationException;

   void setDataName(String dataName) throws ConfigurationException;

   TableList createTableList() throws ConfigurationException;

   void addColumn(TableList.Column column) throws ConfigurationException;

   void deleteColumn(TableList.Column column) throws ConfigurationException;

   void clearColumns() throws ConfigurationException;

   interface ColumnGenerator extends ComponentGenerator
   {
      void setWidth(int width) throws ConfigurationException;

      void setTitleParam(String param) throws ConfigurationException;

      void setIgnoreGlobalTitleParam(boolean ignore) throws ConfigurationException;

      void setCaption(String caption) throws ConfigurationException;

      void setDefaultValue(String value) throws ConfigurationException;

      void setIgnore(boolean ignore) throws ConfigurationException;

      void setSrcName(String srcName) throws ConfigurationException;

      void setTypicalComponentName(String name) throws ConfigurationException;

      void setInitParam(String param) throws ConfigurationException;

      TableList.Column createColumn() throws ConfigurationException;

   }

}
