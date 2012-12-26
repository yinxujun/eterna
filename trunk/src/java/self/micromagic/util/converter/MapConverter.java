
package self.micromagic.util.converter;

import java.util.Map;

import self.micromagic.util.StringTool;
import self.micromagic.util.ObjectRef;
import self.micromagic.cg.BeanTool;
import self.micromagic.cg.BeanMap;

public class MapConverter extends ObjectConverter
{
	private String itemDelimiter = ";\n";
	private char kvDelimiter = '=';

	/**
	 * ��ȡ�ַ�����ÿ��Ԫ�صķָ��.
	 */
	public String getItemDelimiter()
	{
		return this.itemDelimiter;
	}

	/**
	 * �����ַ�����ÿ��Ԫ�صķָ��.
	 */
	public void setItemDelimiter(String itemDelimiter)
	{
		this.itemDelimiter = itemDelimiter;
	}

	/**
	 * ��ȡ�ַ����м���ֵ�ķָ��.
	 */
	public char getKvDelimiter()
	{
		return this.kvDelimiter;
	}

	/**
	 * �����ַ����м���ֵ�ķָ��.
	 */
	public void setKvDelimiter(char kvDelimiter)
	{
		this.kvDelimiter = kvDelimiter;
	}


   public Map convertToMap(Object value)
   {
      if (value == null)
      {
         return null;
      }
      if (value instanceof Map)
      {
         return (Map) value;
      }
      if (value instanceof String)
      {
         return StringTool.string2Map((String) value, this.itemDelimiter, this.kvDelimiter,
					true, false, null, null);
      }
      if (value instanceof ObjectRef)
      {
         return this.convertToMap(((ObjectRef) value).getObject());
      }
		if (BeanTool.checkBean(value.getClass()))
		{
			BeanMap map = BeanTool.getBeanMap(value);
			map.setBean2Map(true);
			return map;
		}
      throw new ClassCastException(getCastErrorMessage(value, "Map"));
   }

   public Map convertToMap(String value)
   {
      if (value == null)
      {
         return null;
      }
		return StringTool.string2Map(value, this.itemDelimiter, this.kvDelimiter,
				true, false, null, null);
   }

   public Object convert(Object value)
   {
      if (value instanceof Map)
      {
         return (Map) value;
      }
      try
      {
         return this.convertToMap(value);
      }
      catch (Exception ex)
      {
         if (this.needThrow)
         {
            if (ex instanceof RuntimeException)
            {
               throw (RuntimeException) ex;
            }
            throw new ClassCastException(getCastErrorMessage(value, "Map"));
         }
         else
         {
            return null;
         }
      }
   }

   public Object convert(String value)
   {
      try
      {
         return this.convertToMap(value);
      }
      catch (Exception ex)
      {
         if (this.needThrow)
         {
            if (ex instanceof RuntimeException)
            {
               throw (RuntimeException) ex;
            }
            throw new ClassCastException(getCastErrorMessage(value, "Map"));
         }
         else
         {
            return null;
         }
      }
   }

}
