
package self.micromagic.eterna.sql.impl;

import java.text.DateFormat;
import java.text.Format;
import java.text.NumberFormat;
import java.util.Locale;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.security.Permission;
import self.micromagic.eterna.share.AbstractGenerator;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.sql.ResultFormat;
import self.micromagic.eterna.sql.ResultFormatGenerator;
import self.micromagic.eterna.sql.ResultRow;
import self.micromagic.util.BooleanRef;

public class ResultFormatGeneratorImpl extends AbstractGenerator
      implements ResultFormatGenerator
{
   private String formatType;
   private String formatPattern;

   public void setType(String type)
   {
      this.formatType = type;
   }

   public void setPattern(String pattern)
   {
      this.formatPattern = pattern;
   }

   public Object create()
         throws ConfigurationException
   {
      return this.createFormat();
   }

   public ResultFormat createFormat()
         throws ConfigurationException
   {
      Format format;
      if (this.formatType == null)
      {
         // 当没有指定类型时, 无法生成需要的格式化对象
         throw new ConfigurationException(
               "The format's attribute [type] not give.");
      }
      if ("Number".equals(this.formatType))
      {
         if (this.formatPattern == null)
         {
            format = NumberFormat.getInstance();
         }
         else
         {
            format = new java.text.DecimalFormat(this.formatPattern);
         }
      }
      else if ("Date".equals(this.formatType))
      {
         if (this.formatPattern == null)
         {
            format = DateFormat.getInstance();
         }
         else if (this.formatPattern.startsWith("locale:"))
         {
            int index = this.formatPattern.indexOf(',');
            if (index == -1)
            {
               // 如果有地区设置，地区与日期模式之间必须用“,”分隔
               throw new ConfigurationException(
                     "Error format pattern:[" + this.formatPattern + "].");
            }
            String pattern = this.formatPattern.substring(index + 1);
            String localeStr = this.formatPattern.substring(7, index);
            index = localeStr.indexOf('_');
            Locale locale = index == -1 ? new Locale(localeStr)
                  : new Locale(localeStr.substring(0, index), localeStr.substring(index + 1));
            format = new java.text.SimpleDateFormat(pattern, locale);
         }
         else
         {
            format = new java.text.SimpleDateFormat(this.formatPattern);
         }
      }
      else if ("boolean".equals(this.formatType))
      {
         return new BooleanFormat(this.formatPattern, this.name);
      }
      else
      {
         // 类型不明, 无法生成需要的格式化对象
         throw new ConfigurationException(
               "Error format type [" + this.formatType + "].");
      }
      return new MyResultFormat(format, this.name);
   }

   private static class MyResultFormat
         implements ResultFormat
   {
      private Format format;
      private String name;

      public MyResultFormat(Format format, String name)
      {
         this.format = format;
         this.name = name;
      }

      public void initialize(EternaFactory factory) {}

      public String getName()
      {
         return this.name;
      }

      public String format(Object obj, Permission permission)
      {
         return this.format.format(obj);
      }

      public String format(Object obj, ResultRow row, Permission permission)
      {
         return this.format.format(obj);
      }

   }

   private static class BooleanFormat
         implements ResultFormat
   {
      private String trueValue = "Yes";
      private String falseValue = "No";
      private String name;

      public BooleanFormat(String formatPattern, String name)
      {
         this.name = name;
         if (formatPattern == null)
         {
            return;
         }
         int index = formatPattern.indexOf(':');
         if (index != -1)
         {
            this.trueValue = formatPattern.substring(0, index);
            this.falseValue = formatPattern.substring(index + 1);
         }
      }

      public void initialize(EternaFactory factory) {}

      public String getName()
      {
         return this.name;
      }

      public String format(Object obj, Permission permission)
      {
         boolean v = BooleanRef.getBooleanValue(obj);
         return v ? this.trueValue : this.falseValue;
      }

      public String format(Object obj, ResultRow row, Permission permission)
      {
         boolean v = BooleanRef.getBooleanValue(obj);
         return v ? this.trueValue : this.falseValue;
      }

   }

}
