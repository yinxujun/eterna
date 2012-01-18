
package self.micromagic.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.NumberFormat;
import java.util.Date;

/**
 * @deprecated
 * @see FormatTool
 */
public class Formater
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
      return Formater.datetimeFormat.format(new Date());
   }

   /**
    * ��һ��double�����ҵĸ�ʽ���(����2λС��)
    */
   public static String formatCurrency(double number)
   {
      return Formater.currencyFormat.format(number);
   }

   /**
    * ��һ��double�����ҵĸ�ʽ���(����2λС��, ������ǧ��λ)
    */
   public static String formatCurrency2(double number)
   {
      return Formater.currency2Format.format(number);
   }

   /**
    * ��ʽ�����ĳ������-ʱ��
    */
   public static String formatDatetime(Object datetime)
   {
      return Formater.datetimeFormat.format(datetime);
   }

   /**
    * ��ʽ�����ĳ������
    */
   public static String formatDate(Object datetime)
   {
      return Formater.dateFormat.format(datetime);
   }

   /**
    * ��ʽ�����ĳ��ʱ��
    */
   public static String formatTime(Object time)
   {
      return Formater.timeFormat.format(time);
   }

   /**
    * ��ĳ���ַ���������-ʱ��ĸ�ʽ������Date
    */
   public static Date parseDatetime(String str)
         throws ParseException
   {
      return Formater.datetimeFormat.parse(str);
   }

   /**
    * ��ĳ���ַ��������ڵĸ�ʽ������Date
    */
   public static Date parseDate(String str)
         throws ParseException
   {
      return Formater.dateFormat.parse(str);
   }

   /**
    * ��ĳ���ַ�����ʱ��ĸ�ʽ������Date
    */
   public static Date parseTime(String str)
         throws ParseException
   {
      return Formater.timeFormat.parse(str);
   }

   /**
    * @deprecated As of eterna 1.0.0,
    * replaced by <code>parseDatetime</code>.
    */
   public static Date parserDatetime(String str)
         throws ParseException
   {
      return Formater.datetimeFormat.parse(str);
   }

   /**
    * @deprecated As of eterna 1.0.0,
    * replaced by <code>parseDate</code>.
    */
   public static Date parserDate(String str)
         throws ParseException
   {
      return Formater.dateFormat.parse(str);
   }

   /**
    * @deprecated As of eterna 1.0.0,
    * replaced by <code>parseTime</code>.
    */
   public static Date parserTime(String str)
         throws ParseException
   {
      return Formater.timeFormat.parse(str);
   }

}
