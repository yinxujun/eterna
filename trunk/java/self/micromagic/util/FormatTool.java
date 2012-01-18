
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
    * ��ʽ�������ǰ������-ʱ��
    */
   public static String getCurrentDatetimeString()
   {
      return datetimeFormat.format(new Date());
   }

   /**
    * ��һ��double�����ҵĸ�ʽ���(����2λС��)
    */
   public static String formatCurrency(double number)
   {
      return currencyFormat.format(number);
   }

   /**
    * ��һ��double�����ҵĸ�ʽ���(����2λС��, ������ǧ��λ)
    */
   public static String formatCurrency2(double number)
   {
      return currency2Format.format(number);
   }

   /**
    * ��ʽ�����ĳ������-ʱ��
    */
   public static String formatDatetime(Object datetime)
   {
      return datetimeFormat.format(datetime);
   }

   /**
    * ��ʽ�����ĳ������
    */
   public static String formatDate(Object datetime)
   {
      return dateFormat.format(datetime);
   }

   /**
    * ��ʽ�����ĳ��ʱ��
    */
   public static String formatTime(Object time)
   {
      return timeFormat.format(time);
   }

   /**
    * ��ĳ���ַ���������-ʱ��ĸ�ʽ������Date
    */
   public static Date parseDatetime(String str)
         throws ParseException
   {
      return datetimeFormat.parse(str);
   }

   /**
    * ��ĳ���ַ��������ڵĸ�ʽ������Date
    */
   public static Date parseDate(String str)
         throws ParseException
   {
      return dateFormat.parse(str);
   }

   /**
    * ��ĳ���ַ�����ʱ��ĸ�ʽ������Date
    */
   public static Date parseTime(String str)
         throws ParseException
   {
      return timeFormat.parse(str);
   }

}
