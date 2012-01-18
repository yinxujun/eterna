
package self.micromagic.eterna.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.CallableStatement;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.security.PermissionSet;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.share.TypeManager;

/**
 * 一个空的ResultReader, 主要用于替换没有权限的ResultReader.
 */
public class NullResultReader
      implements ResultReader
{
   protected String name;

   public NullResultReader(String name)
   {
      this.name = name;
   }

   public void initialize(EternaFactory factory)
   {
   }

   public int getType()
   {
      return TypeManager.TYPE_IGNORE;
   }

   public boolean isIgnore()
   {
      return true;
   }

   public ResultFormat getFormat()
   {
      return null;
   }

   public String getFormatName()
   {
      return null;
   }

   public String getName()
   {
      return this.name;
   }

   public String getOrderName()
   {
      return this.name;
   }

   public String getColumnName()
   {
      return this.name;
   }

   public boolean isUseColumnName()
   {
      return true;
   }

   public int getColumnIndex()
   {
      return -1;
   }

   public boolean needHtmlFilter() throws ConfigurationException
   {
      return false;
   }

   public boolean isValid() throws ConfigurationException
   {
      return false;
   }

   public boolean isUseColumnIndex()
   {
      return false;
   }

   public PermissionSet getPermissionSet()
   {
      return null;
   }

   public String getCaption() throws ConfigurationException
   {
      return null;
   }

   public String getFilledCaption() throws ConfigurationException
   {
      return null;
   }

   public int getWidth() throws ConfigurationException
   {
      return 0;
   }

   public boolean isVisible() throws ConfigurationException
   {
      return false;
   }

   public Object getAttribute(String name) throws ConfigurationException
   {
      return null;
   }

   public String[] getAttributeNames() throws ConfigurationException
   {
      return null;
   }

   public Object readResult(ResultSet rs)
         throws SQLException
   {
      return null;
   }

   public Object readCall(CallableStatement call, int index)
   {
      return null;
   }

   public Object readObject(Object obj) throws ConfigurationException
   {
      return null;
   }

}
