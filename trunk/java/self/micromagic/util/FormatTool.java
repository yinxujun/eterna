
package self.micromagic.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Date;

public class FormatTool
{
   private static DateFormat datetimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
   private static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
   private static DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
   private static NumberFormat currencyFormat = new DecimalFormat("#0.00");
   private static NumberFormat currency2Format = new DecimalFormat("#,##0.00");

   /**
    * 格式化输出当前的日期-时间
    */
   public static String getCurrentDatetimeString()
   {
      return datetimeFormat.format(new Date());
   }

   /**
    * 将一个double按货币的格式输出(保留2位小数)
    */
   public static String formatCurrency(double number)
   {
      return currencyFormat.format(number);
   }

   /**
    * 将一个double按货币的格式输出(保留2位小数, 并加上千分位)
    */
   public static String formatCurrency2(double number)
   {
      return currency2Format.format(number);
   }

   /**
    * 格式化输出某个日期-时间
    */
   public static String formatDatetime(Object datetime)
   {
      return datetimeFormat.format(datetime);
   }

   /**
    * 格式化输出某个日期
    */
   public static String formatDate(Object datetime)
   {
      return dateFormat.format(datetime);
   }

   /**
    * 格式化输出某个时间
    */
   public static String formatTime(Object time)
   {
      return timeFormat.format(time);
   }

   /**
    * 将某个字符串按日期-时间的格式解析成Date
    */
   public static Date parseDatetime(String str)
         throws ParseException
   {
      return datetimeFormat.parse(str);
   }

   /**
    * 将某个字符串按日期的格式解析成Date
    */
   public static Date parseDate(String str)
         throws ParseException
   {
      return dateFormat.parse(str);
   }

   /**
    * 将某个字符串按时间的格式解析成Date
    */
   public static Date parseTime(String str)
         throws ParseException
   {
      return timeFormat.parse(str);
   }

}
