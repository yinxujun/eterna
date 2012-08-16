
package self.micromagic.eterna.search.impl;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.search.ConditionProperty;
import self.micromagic.eterna.search.ConditionPropertyGenerator;
import self.micromagic.eterna.share.AbstractGenerator;
import self.micromagic.eterna.share.TypeManager;

public class ConditionPropertyGeneratorImpl extends AbstractGenerator
      implements ConditionPropertyGenerator
{
	private ConditionPropertyImpl conditionProperty = new ConditionPropertyImpl();

   private String columnName;
   private String columnType;
   private String inputType;

   public void setColumnName(String name)
   {
      this.columnName = name;
   }

   public void setColumnCaption(String caption)
   {
      this.conditionProperty.columnCaption = caption;
   }

   public void setColumnType(String type)
   {
      this.columnType = type;
   }

   public void setColumnVPC(String vpcName)
   {
      this.conditionProperty.vpcName = vpcName;
   }

   public void setConditionInputType(String type)
   {
      this.inputType = type;
   }

   public void setDefaultValue(String value)
   {
      this.conditionProperty.defaultValue = value;
   }

   public void setVisible(boolean visible)
   {
      this.conditionProperty.visible = visible;
   }

   public void setPermissions(String permissions)
   {
      this.conditionProperty.permissions = permissions;
   }

   public void setUseDefaultConditionBuilder(boolean use)
   {
      this.conditionProperty.useDefaultConditionBuilder = use;
   }

   public void setDefaultConditionBuilderName(String name)
   {
      this.conditionProperty.defaultBuilderName = name;
   }

   public void setConditionBuilderListName(String name)
   {
      this.conditionProperty.listName = name;
   }

   public Object create()
         throws ConfigurationException
   {
      return this.createConditionProperty();
   }

   public ConditionProperty createConditionProperty()
         throws ConfigurationException
   {
		this.conditionProperty.name = this.name;
      this.conditionProperty.columnName = this.columnName == null ? this.name : this.columnName;
      this.conditionProperty.columnType = TypeManager.getTypeId(this.columnType);
      if (this.conditionProperty.columnType == TypeManager.TYPE_IGNORE)
      {
         this.conditionProperty.ignore = true;
      }
      this.conditionProperty.inputType = this.inputType == null ? "text" : this.inputType;
      if (this.conditionProperty.listName == null)
      {
         if (this.conditionProperty.inputType != null
					&& this.conditionProperty.inputType.toLowerCase().startsWith("select"))
         {
            this.conditionProperty.listName = "cbl_List";
         }
         else
         {
            if (TypeManager.isTypeString(this.conditionProperty.columnType))
            {
               this.conditionProperty.listName = "cbl_String";
            }
            else
            {
               this.conditionProperty.listName = "cbl_Other";
            }
         }
      }
      this.conditionProperty.attributes = this.attributes;
      return this.conditionProperty;
   }

}
