
package self.micromagic.util.converter;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

/**
 * ValueConverter对象的查找器, 可通过给出的Class找寻对应的
 * ValueConverter对象.
 * 取出的所有ValueConverter对象的isNeedThrow值默认为true
 *
 * @see ValueConverter#isNeedThrow
 */
public class ConverterFinder
{
	/**
	 * 查找一个对应的ValueConverter对象.
	 *
	 * @param c       对应的Class
	 * @return 查到的ValueConverter对象, 如果未查到则返回null
	 */
	public static ValueConverter findConverter(Class c)
	{
		return findConverter(c, true);
	}

	/**
	 * 查找一个对应的ValueConverter对象.
	 * 此ValueConverter对象的isNeedThrow值默认为false
	 *
	 * @param c       对应的Class
	 * @return 查到的ValueConverter对象, 如果未查到则返回null
	 */
	public static ValueConverter findConverterWithoutThrow(Class c)
	{
		return findConverter(c, true);
	}

	/**
	 * 查找一个对应的ValueConverter对象.
	 *
	 * @param c       对应的Class
	 * @param copy    是否需要复制ValueConverter对象
	 * @return 查到的ValueConverter对象, 如果未查到则返回null
	 */
	public static ValueConverter findConverter(Class c, boolean copy)
	{
		ValueConverter vc = (ValueConverter) converterCache.get(c);
		if (vc == null)
		{
			return null;
		}
		return copy ? vc.copy() : vc;
	}

	/**
	 * 默认的ValueConverter对象缓存.
	 */
	private static Map converterCache = new HashMap();

	/**
	 * isNeedThrow值为false的ValueConverter对象缓存.
	 */
	private static Map withoutThrowCache = new HashMap();

	static
	{
		ValueConverter converter;

		converter = new BooleanConverter();
		converter.setNeedThrow(true);
		converterCache.put(boolean.class, converter);
		converterCache.put(Boolean.class, converter);
		converter = new ByteConverter();
		converter.setNeedThrow(true);
		converterCache.put(byte.class, converter);
		converterCache.put(Byte.class, converter);
		converter = new CharacterConverter();
		converter.setNeedThrow(true);
		converterCache.put(char.class, converter);
		converterCache.put(Character.class, converter);
		converter = new ShortConverter();
		converter.setNeedThrow(true);
		converterCache.put(short.class, converter);
		converterCache.put(Short.class, converter);
		converter = new IntegerConverter();
		converter.setNeedThrow(true);
		converterCache.put(int.class, converter);
		converterCache.put(Integer.class, converter);
		converter = new LongConverter();
		converter.setNeedThrow(true);
		converterCache.put(long.class, converter);
		converterCache.put(Long.class, converter);
		converter = new FloatConverter();
		converter.setNeedThrow(true);
		converterCache.put(float.class, converter);
		converterCache.put(Float.class, converter);
		converter = new DoubleConverter();
		converter.setNeedThrow(true);
		converterCache.put(double.class, converter);
		converterCache.put(Double.class, converter);

		converter = new ObjectConverter();
		converter.setNeedThrow(true);
		converterCache.put(Object.class, converter);
		converter = new StringConverter();
		converter.setNeedThrow(true);
		converterCache.put(String.class, converter);
		converter = new BigIntegerConverter();
		converter.setNeedThrow(true);
		converterCache.put(BigInteger.class, converter);
		converter = new DecimalConverter();
		converter.setNeedThrow(true);
		converterCache.put(BigDecimal.class, converter);
		converter = new BytesConverter();
		converter.setNeedThrow(true);
		converterCache.put(byte[].class, converter);
		converter = new TimeConverter();
		converter.setNeedThrow(true);
		converterCache.put(java.sql.Time.class, converter);
		converter = new DateConverter();
		converter.setNeedThrow(true);
		converterCache.put(java.sql.Date.class, converter);
		converter = new TimestampConverter();
		converter.setNeedThrow(true);
		converterCache.put(java.sql.Timestamp.class, converter);
		converter = new UtilDateConverter();
		converter.setNeedThrow(true);
		converterCache.put(java.util.Date.class, converter);
		converter = new CalendarConverter();
		converter.setNeedThrow(true);
		converterCache.put(java.util.Calendar.class, converter);
		converter = new StreamConverter();
		converter.setNeedThrow(true);
		converterCache.put(InputStream.class, converter);
		converter = new ReaderConverter();
		converter.setNeedThrow(true);
		converterCache.put(Reader.class, converter);

		Iterator itr = converterCache.entrySet().iterator();
		while (itr.hasNext())
		{
			Map.Entry entry = (Map.Entry) itr.next();
			ValueConverter vc = (ValueConverter) entry.getValue();
			vc = vc.copy();
			vc.setNeedThrow(false);
			withoutThrowCache.put(entry.getKey(), vc);
		}
	}

}
