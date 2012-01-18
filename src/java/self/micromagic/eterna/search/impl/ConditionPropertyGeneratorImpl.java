
package self.micromagic.eterna.search.impl;

import java.util.List;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.search.ConditionBuilder;
import self.micromagic.eterna.search.ConditionProperty;
import self.micromagic.eterna.search.ConditionPropertyGenerator;
import self.micromagic.eterna.security.PermissionSet;
import self.micromagic.eterna.share.AbstractGenerator;
import self.micromagic.eterna.share.AttributeManager;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.share.Tool;
import self.micromagic.eterna.share.TypeManager;
import self.micromagic.eterna.sql.preparer.ValuePreparer;
import self.micromagic.eterna.sql.preparer.ValuePreparerCreater;
import self.micromagic.util.StringTool;

public class ConditionPropertyGeneratorImpl extends AbstractGenerator
      implements ConditionPropertyGenerator
{
   private String columnName;
   private String columnCaption = null;
   private String columnType;
   private String vpcName;
   private String inputType;
   private String defaultValue;
   private boolean visible = true;
   private String listName;
   private boolean useDefaultConditionBuilder = false;
   private String defaultBuilderName;
   private String permissions = null;

   public void setColumnName(String name)
   {
      this.columnName = name;
   }

   public void setColumnCaption(String caption)
   {
      this.columnCaption = caption;
   }

   public void setColumnType(String type)
   {
      this.columnType = type;
   }

   public void setColumnVPC(String vpcName)
   {
      this.vpcName = vpcName;
   }

   public void setConditionInputType(String type)
   {
      this.inputType = type;
   }

   public void setDefaultValue(String value)
   {
      this.defaultValue = value;
   }

   public void setVisible(boolean visible)
   {
      this.visible = visible;
   }

   public void setPermissions(String permissions)
   {
      this.permissions = permissions;
   }

   public void setUseDefaultConditionBuilder(boolean use)
   {
      this.useDefaultConditionBuilder = use;
   }

   public void setDefaultConditionBuilderName(String name)
   {
      this.defaultBuilderName = name;
   }

   public void setConditionBuilderListName(String name)
   {
      this.listName = name;
   }

   public Object create()
         throws ConfigurationException
   {
      return this.createConditionProperty();
   }

   public ConditionProperty createConditionProperty()
         throws ConfigurationException
   {
      ConditionPropertyImpl cp = new ConditionPropertyImpl();
      cp.name = this.name;
      cp.columnName = this.columnName == null ? this.name : this.columnName;
      cp.columnCaption = this.columnCaption;
      cp.permissions = this.permissions;
      cp.visible = this.visible;
      cp.columnType = TypeManager.getTypeId(this.columnType);
      cp.vpcName = this.vpcName;
      if (cp.columnType == TypeManager.TYPE_IGNORE)
      {
         cp.ignore = true;
      }
      cp.inputType = this.inputType == null ? "text" : this.inputType;
      cp.defaultValue = this.defaultValue;
      cp.listName = this.listName;
      if (this.listName == null)
      {
         if (cp.inputType != null && cp.inputType.toLowerCase().startsWith("select"))
         {
            cp.listName = "cbl_List";
         }
         else
         {
            if (TypeManager.isTypeString(cp.columnType))
            {
               cp.listName = "cbl_String";
            }
            else
            {
               cp.listName = "cbl_Other";
            }
         }
      }
      cp.useDefaultConditionBuilder = this.useDefaultConditionBuilder;
      cp.defaultBuilderName = this.defaultBuilderName;
      cp.attributes = this.attributes;
      return cp;
   }

   private static class ConditionPropertyImpl
         implements ConditionProperty
   {
      private String name;
      private String columnName;
      private String columnCaption = null;
      private boolean ignore = false;
      private int columnType;
      private String vpcName;
      private ValuePreparerCreater vpCreater;
      private boolean visible;
      private String inputType;
      private String defaultValue;
      private String listName;
      private String defaultBuilderName;
      private String permissions;
      private PermissionSet permissionSet = null;
      private List conditionBuilderList;
      private boolean useDefaultConditionBuilder = false;
      private ConditionBuilder defaultBuilder;
      private AttributeManager attributes;

      public void initialize(EternaFactory factory)
            throws ConfigurationException
      {
         if (this.listName != null)
         {
            this.conditionBuilderList = factory.getConditionBuilderList(this.listName);
            if (this.conditionBuilderList == null)
            {
               log.warn("The ConditionBuilder list [" + this.listName + "] not found.");
            }
         }

         if (this.defaultBuilderName != null)
         {
            this.defaultBuilder = factory.getConditionBuilder(this.defaultBuilderName);
            if (this.defaultBuilder == null)
            {
               log.warn("The ConditionBuilder [" + this.defaultBuilderName + "] not found.");
            }
         }
         else if (this.conditionBuilderList != null)
         {
            if (this.conditionBuilderList.size() > 0)
            {
               this.defaultBuilder = (ConditionBuilder) this.conditionBuilderList.get(0);
            }
         }

         if (this.permissions != null && this.permissions.trim().length() > 0)
         {
            this.permissionSet = new PermissionSet(
                  StringTool.separateString(this.permissions, ",", true));
            this.permissionSet.initialize(factory);
         }

         this.vpCreater = factory.createValuePreparerCreater(this.vpcName, this.getColumnPureType());
         if (this.vpCreater == null)
         {
            log.warn("The value preparer generator [" + this.vpcName + "] not found.");
            this.vpCreater = factory.createValuePreparerCreater(this.getColumnPureType());
         }

         if (this.columnCaption == null)
         {
            this.columnCaption = Tool.translateCaption(factory, this.getName());
         }
      }

      public String getName()
      {
         return this.name;
      }

      public String getColumnName()
      {
         return this.columnName;
      }

      public String getColumnCaption()
      {
         return this.columnCaption;
      }

      public String getColumnTypeName()
      {
         return TypeManager.getTypeName(this.columnType);
      }

      public int getColumnPureType()
      {
         return TypeManager.getPureType(this.columnType);
      }

      public int getColumnType()
      {
         return this.columnType;
      }

      public ValuePreparer createValuePreparer(String value)
      {
         return this.vpCreater.createPreparer(value);
      }

      public ValuePreparer createValuePreparer(Object value)
      {
         return this.vpCreater.createPreparer(value);
      }

      public boolean isIgnore()
      {
         return this.ignore;
      }

      public boolean isVisible()
      {
         return this.visible;
      }

      public String getConditionInputType()
      {
         return this.inputType;
      }

      public String getDefaultValue()
      {
         return this.defaultValue;
      }

      public String getAttribute(String name)
      {
         return (String) this.attributes.getAttribute(name);
      }

      public String[] getAttributeNames()
      {
         return this.attributes.getAttributeNames();
      }

      public PermissionSet getPermissionSet()
      {
         return this.permissionSet;
      }

      public String getConditionBuilderListName()
      {
         return this.listName;
      }

      public boolean isUseDefaultConditionBuilder()
      {
         return this.useDefaultConditionBuilder;
      }

      public ConditionBuilder getDefaultConditionBuilder()
      {
         return this.defaultBuilder;
      }

      public List getConditionBuilderList()
      {
         return this.conditionBuilderList;
      }

   }

}
