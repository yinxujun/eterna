
package self.micromagic.eterna.view;

import java.io.Writer;
import java.io.IOException;
import java.util.Map;
import java.util.Iterator;
import java.sql.SQLException;
import java.text.DateFormat;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.sql.ResultIterator;
import self.micromagic.eterna.sql.ResultRow;

/**
 * ���ݼ������, ���ڽ����ݼ�����һ���ĸ�ʽ�������.
 * ע: ���ݼ��в����������õ�����.
 */
public interface DataPrinter
{
   /**
    * ��ʼ�������ݼ������.
    */
   public void initialize(EternaFactory factory) throws ConfigurationException;

   /**
    * ��ȡ�����ݼ������������.
    */
   public String getName() throws ConfigurationException;

   /**
    * ������ݼ�.
    *
    * @param out          �����
    * @param data         ���ݼ�
    * @param hasPreData   ��������ݼ�ǰ�Ƿ�����������
    */
   void printData(Writer out, Map data, boolean hasPreData) throws IOException, ConfigurationException;

   /**
    * �����������ֵ.
    *
    * @param out         �����
    * @param b           ����ֵ
    */
   void print(Writer out, boolean b) throws IOException, ConfigurationException;

   /**
    * ����ַ�����ֵ.
    *
    * @param out         �����
    * @param c           �ַ�ֵ
    */
   void print(Writer out, char c) throws IOException, ConfigurationException;

   /**
    * �������ֵ.
    *
    * @param out         �����
    * @param i           ����ֵ
    */
   void print(Writer out, int i) throws IOException, ConfigurationException;

   /**
    * ���������ֵ.
    *
    * @param out         �����
    * @param l           ������ֵ
    */
   void print(Writer out, long l) throws IOException, ConfigurationException;

   /**
    * ���������ֵ.
    *
    * @param out         �����
    * @param f           ������ֵ
    */
   void print(Writer out, float f) throws IOException, ConfigurationException;

   /**
    * ���˫���ȸ�����ֵ.
    *
    * @param out         �����
    * @param d           ˫���ȸ�����ֵ
    */
   void print(Writer out, double d) throws IOException, ConfigurationException;

   /**
    * ����ַ�������ֵ.
    *
    * @param out         �����
    * @param s           �ַ�������ֵ
    */
   void print(Writer out, String s) throws IOException, ConfigurationException;

   /**
    * ���һ��Object����.
    *
    * @param out          �����
    * @param value        Ҫ�����Object����
    */
   void print(Writer out, Object value) throws IOException, ConfigurationException;

   /**
    * ���һ��Object��������.
    *
    * @param out          �����
    * @param values       Ҫ�����Object��������
    */
   void print(Writer out, Object[] values) throws IOException, ConfigurationException;

   /**
    * ���һ��Map����.
    *
    * @param out          �����
    * @param map          Ҫ�����Map����
    */
   void printMap(Writer out, Map map) throws IOException, ConfigurationException;

   /**
    * ��������������������.
    *
    * @param out          �����
    * @param ritr         �����������
    */
   void printResultIterator(Writer out, ResultIterator ritr)
         throws IOException, ConfigurationException, SQLException;

   /**
    * ����н����������.
    *
    * @param out          �����
    * @param row          �н����
    */
   void printResultRow(Writer out, ResultRow row)
         throws IOException, ConfigurationException, SQLException;

   /**
    * ���������������.
    *
    * @param out          �����
    * @param itr          ������
    */
   void printIterator(Writer out, Iterator itr) throws IOException, ConfigurationException;

	/**
	 * ����������ʼ��.
    *
    * @param out          �����
	 */
	void printObjectBegin(Writer out) throws IOException, ConfigurationException;

	/**
	 * �������Ľ�����.
    *
    * @param out          �����
	 */
	void printObjectEnd(Writer out) throws IOException, ConfigurationException;

	/**
	 * ���һ��key-value��.
	 *
    * @param out          �����
    * @param key          ����
    * @param value        ����ֵ
    * @param first        �Ƿ�Ϊ�����еĵ�һ��ֵ
	 */
	void printPair(Writer out, String key, boolean value, boolean first)
			throws IOException, ConfigurationException;

	/**
	 * ���һ��key-value��.
	 *
    * @param out          �����
    * @param key          ����
    * @param value        �ַ�ֵ
    * @param first        �Ƿ�Ϊ�����еĵ�һ��ֵ
	 */
	void printPair(Writer out, String key, char value, boolean first)
			throws IOException, ConfigurationException;

	/**
	 * ���һ��key-value��.
	 *
    * @param out          �����
    * @param key          ����
    * @param value        ����ֵ
    * @param first        �Ƿ�Ϊ�����еĵ�һ��ֵ
	 */
	void printPair(Writer out, String key, int value, boolean first)
			throws IOException, ConfigurationException;

	/**
	 * ���һ��key-value��, ��������key�Ƿ�Ϊnull, �Ƿ���������ַ�.
	 *
    * @param out          �����
    * @param key          ����
    * @param value        ����ֵ
    * @param first        �Ƿ�Ϊ�����еĵ�һ��ֵ
	 */
	void printPairWithoutCheck(Writer out, String key, int value, boolean first)
			throws IOException, ConfigurationException;

	/**
	 * ���һ��key-value��.
	 *
    * @param out          �����
    * @param key          ����
    * @param value        ������ֵ
    * @param first        �Ƿ�Ϊ�����еĵ�һ��ֵ
	 */
	void printPair(Writer out, String key, long value, boolean first)
			throws IOException, ConfigurationException;

	/**
	 * ���һ��key-value��, ��������key�Ƿ�Ϊnull, �Ƿ���������ַ�.
	 *
    * @param out          �����
    * @param key          ����
    * @param value        ������ֵ
    * @param first        �Ƿ�Ϊ�����еĵ�һ��ֵ
	 */
	void printPairWithoutCheck(Writer out, String key, long value, boolean first)
			throws IOException, ConfigurationException;

	/**
	 * ���һ��key-value��.
	 *
    * @param out          �����
    * @param key          ����
    * @param value        ������ֵ
    * @param first        �Ƿ�Ϊ�����еĵ�һ��ֵ
	 */
	void printPair(Writer out, String key, float value, boolean first)
			throws IOException, ConfigurationException;

	/**
	 * ���һ��key-value��.
	 *
    * @param out          �����
    * @param key          ����
    * @param value        ˫���ȸ�����ֵ
    * @param first        �Ƿ�Ϊ�����еĵ�һ��ֵ
	 */
	void printPair(Writer out, String key, double value, boolean first)
			throws IOException, ConfigurationException;

	/**
	 * ���һ��key-value��, ��������key�Ƿ�Ϊnull, �Ƿ���������ַ�.
	 *
    * @param out          �����
    * @param key          ����
    * @param value        ˫���ȸ�����ֵ
    * @param first        �Ƿ�Ϊ�����еĵ�һ��ֵ
	 */
	void printPairWithoutCheck(Writer out, String key, double value, boolean first)
			throws IOException, ConfigurationException;

	/**
	 * ���һ��key-value��.
	 *
    * @param out          �����
    * @param key          ����
    * @param value        �ַ���ֵ
    * @param first        �Ƿ�Ϊ�����еĵ�һ��ֵ
	 */
	void printPair(Writer out, String key, String value, boolean first)
			throws IOException, ConfigurationException;

	/**
	 * ���һ��key-value��, ��������key�Ƿ�Ϊnull, �Ƿ���������ַ�.
	 *
    * @param out          �����
    * @param key          ����
    * @param value        �ַ���ֵ
    * @param first        �Ƿ�Ϊ�����еĵ�һ��ֵ
	 */
	void printPairWithoutCheck(Writer out, String key, String value, boolean first)
			throws IOException, ConfigurationException;

	/**
	 * ���һ��key-value��.
	 *
    * @param out          �����
    * @param key          ����
    * @param value        ����ֵ
    * @param first        �Ƿ�Ϊ�����еĵ�һ��ֵ
	 */
	void printPair(Writer out, String key, Object value, boolean first)
			throws IOException, ConfigurationException;

	/**
	 * ���һ��key-value��, ��������key�Ƿ�Ϊnull, �Ƿ���������ַ�.
	 *
    * @param out          �����
    * @param key          ����
    * @param value        ����ֵ
    * @param first        �Ƿ�Ϊ�����еĵ�һ��ֵ
	 */
	void printPairWithoutCheck(Writer out, String key, Object value, boolean first)
			throws IOException, ConfigurationException;

	/**
	 * �����������͵�����ʹ�õĸ�ʽ����ʽ.
	 */
	void setDateFormat(DateFormat format);

   /**
    * ��ȡһ��beanʵ���������.
    *
    * @param beanClass     bean��Class
    * @return      beanʵ���������
    */
   BeanPrinter getBeanPrinter(Class beanClass) throws ConfigurationException;

   /**
    * beanʵ���������.
    */
   interface BeanPrinter
   {
      /**
       * ���bean�е�����.
       *
       * @param p         ���ݼ������
       * @param out       �����
       * @param bean      bean��ʵ��
       */
      void print(DataPrinter p, Writer out, Object bean) throws IOException, ConfigurationException;

   }

}