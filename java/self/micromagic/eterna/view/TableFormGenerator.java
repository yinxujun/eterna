
package self.micromagic.eterna.view;

import self.micromagic.eterna.digester.ConfigurationException;

public interface TableFormGenerator extends ComponentGenerator
{
   void setAutoArrange(boolean autoArrange) throws ConfigurationException;

   void setPercentWidth(boolean percentWidth) throws ConfigurationException;

   void setCaculateWidth(boolean caculateWidth) throws ConfigurationException;

   void setCaculateWidthFix(int caculateWidthFix) throws ConfigurationException;

   void setColumns(String columns) throws ConfigurationException;

   void setTR(Component tr) throws ConfigurationException;

   void setCellOrder(String order) throws ConfigurationException;

   /**
    * @param name  可以是一个query       名称以[query:]开始或直接query的名称
    *              可以是一个readManager 名称以[reader:]开始，后面接readerManager的名称
    *              可以是一个search      名称以[search:]开始，后面接search的名称
    */
   void setBaseName(String name) throws ConfigurationException;

   void setDataName(String dataName) throws ConfigurationException;

   TableForm createTableForm() throws ConfigurationException;

   void addCell(TableForm.Cell cell) throws ConfigurationException;

   void deleteCell(TableForm.Cell cell) throws ConfigurationException;

   void clearCells() throws ConfigurationException;

   interface CellGenerator extends ComponentGenerator
   {
      void setTitleSize(int size) throws ConfigurationException;

      void setTitleParam(String param) throws ConfigurationException;

      void setContainerSize(int size) throws ConfigurationException;

      void setIgnoreGlobalTitleParam(boolean ignore) throws ConfigurationException;

      void setRowSpan(int rowSpan) throws ConfigurationException;

      void setCaption(String caption) throws ConfigurationException;

      void setDefaultValue(String value) throws ConfigurationException;

      void setIgnore(boolean ignore) throws ConfigurationException;

      void setNewRow(boolean newRow) throws ConfigurationException;

      void setSrcName(String srcName) throws ConfigurationException;

      void setRequired(boolean required) throws ConfigurationException;

      void setNeedIndex(boolean needIndex) throws ConfigurationException;

      void setTypicalComponentName(String name) throws ConfigurationException;

      void setInitParam(String param) throws ConfigurationException;

      TableForm.Cell createCell() throws ConfigurationException;

   }

}
