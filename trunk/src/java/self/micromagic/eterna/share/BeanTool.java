
package self.micromagic.eterna.share;

import java.util.Map;
import java.util.HashMap;
import java.util.Calendar;
import java.util.Iterator;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.lang.reflect.Field;
import java.beans.PropertyEditor;

import org.apache.commons.collections.ReferenceMap;
import self.micromagic.eterna.sql.converter.*;
import self.micromagic.eterna.sql.ResultRow;
import self.micromagic.util.MemoryStream;
import self.micromagic.util.MemoryChars;
import self.micromagic.util.StringAppender;
import self.micromagic.util.StringTool;
import self.micromagic.util.ResManager;
import self.micromagic.util.Utility;

/**
 * ��bean���в����Ĺ���.
 */
public class BeanTool
{
   /**
    * ͨ��map����bean������������.
    *
    * @param bean     ��Ҫ���������Ե�bean
    * @param values   ������Դ
    * @return     �������˵����Ը���
    */
   public static int setBeanValues(Object bean, Map values)
   {
      return setBeanValues(bean, values, "");
   }

   /**
    * ͨ��map����bean������������.
    *
    * @param bean     ��Ҫ���������Ե�bean
    * @param values   ������Դ
    * @param prefix   ��������ǰ׺
    * @return     �������˵����Ը���
    */
   public static int setBeanValues(Object bean, Map values, String prefix)
   {
      if (bean == null || values == null)
      {
         return 0;
      }
      MapToBean p = getMapToBean(bean.getClass());
      if (p == null)
      {
         return 0;
      }
      if (prefix == null)
      {
         prefix = "";
      }
      try
      {
         return p.setBeanValues(bean, values, prefix);
      }
      catch (Exception ex)
      {
         throw new RuntimeException(ex);
      }
   }

   /**
    * ���bean��map��ת������.
    *
    * @param bean     Ҫ��map����ת����bean
    */
   public static BeanMap getBeanMap(Object bean)
   {
      return getBeanMap(bean, "");
   }

   /**
    * ���bean��map��ת������.
    *
    * @param bean     Ҫ��map����ת����bean
    * @param prefix   ��������ǰ׺
    */
   public static BeanMap getBeanMap(Object bean, String prefix)
   {
      if (bean == null)
      {
         return null;
      }
      Class beanClass = bean.getClass();
      BeanDescriptor bd = getBeanDescriptor(beanClass);
      if (bd == null)
      {
         return null;
      }
      if (prefix == null)
      {
         prefix = "";
      }
      ConverterManager cm = (ConverterManager) beanConverterManagerCache.get(beanClass);
      if (cm == null)
      {
         cm = converterManager;
      }
      return new BeanMap(bean, prefix, bd, cm);
   }

   /**
    * ���bean��map��ת������.
    *
    * @param beanType  Ҫ��map����ת����bean������
    * @param prefix    ��������ǰ׺
    */
   public static BeanMap getBeanMap(Class beanType, String prefix)
   {
      if (beanType == null)
      {
         return null;
      }
      BeanDescriptor bd = getBeanDescriptor(beanType);
      if (bd == null)
      {
         return null;
      }
      if (prefix == null)
      {
         prefix = "";
      }
      ConverterManager cm = (ConverterManager) beanConverterManagerCache.get(beanType);
      if (cm == null)
      {
         cm = converterManager;
      }
      return new BeanMap(beanType, prefix, bd, cm);
   }

   /**
    * ���BeanMap�ж�bean���ԵĲ�����Ԫ.
    */
   public static BeanMap.CellDescriptor getBeanMapCell(Class type, String name)
   {
      BeanDescriptor db = (BeanDescriptor) beanDescriptorCache.get(type);
      if (db != null)
      {
         return (BeanMap.CellDescriptor) db.cells.get(name);
      }
      return null;
   }

   /**
    * ���BeanMap�ж�bean�Ĺ��쵥Ԫ.
    */
   public static BeanMap.CellDescriptor getBeanMapInitCell(Class type)
   {
      BeanDescriptor db = (BeanDescriptor) beanDescriptorCache.get(type);
      if (db != null)
      {
         return db.initCell;
      }
      return null;
   }

   /**
    * �Ƴ�BeanMap�Ľṹ��Ϣ, �Ƴ����ٴ�ʹ��ʱ���ܹ����¹���ṹ��Ϣ.
    */
   public static void removeBeanDescriptor(Class type)
   {
      beanDescriptorCache.remove(type);
      beanConverterManagerCache.remove(type);
   }

   private static Map beanDescriptorCache = new ReferenceMap(ReferenceMap.WEAK, ReferenceMap.HARD);
   /**
    * ��ö�bean���Ե�������Ϣ.
    */
   private static BeanDescriptor getBeanDescriptor(Class beanClass)
   {
      BeanDescriptor bd = (BeanDescriptor) beanDescriptorCache.get(beanClass);
      if (bd == null)
      {
         synchronized (beanDescriptorCache)
         {
            // �ٻ�ȡһ��, ��������߳��Ѵ�����, ����Ͳ�������
            bd = (BeanDescriptor) beanDescriptorCache.get(beanClass);
            if (bd == null)
            {
               Map psInfo = new HashMap();
               try
               {
                  String fnName = "public int setBeanValue(Object bean, Object value, String prefix, "
                        + "BeanMap beanMap, Object originObj, Object oldValue)";
                  String mh = StringTool.createStringAppender().append(fnName).appendln()
                        .append("      throws Exception").toString();
                  String beginCode = StringTool.createStringAppender()
                        .append("   int ").append(SETTED_COUNT_NAME).append(" = 0;").appendln()
                        .toString();
                  String endCode =  StringTool.createStringAppender()
                        .append("   return ").append(SETTED_COUNT_NAME).append(";").toString();
                  String[] imports = new String[]{
                     Tool.getPackageString(Map.class),
                     Tool.getPackageString(Tool.class),
                     Tool.getPackageString(ResultRow.class),
                     Tool.getPackageString(beanClass)
                  };
                  BeanPropertyWriteProcesser wp = new BeanPropertyWriteProcesser(
                        "value", "beanMap", "originObj", "oldValue");
                  Map tmp = JavassistTool.createPropertyProcessers(beanClass, BeanPropertyWriter.class, mh,
                        "bean", beginCode, endCode, wp, imports, Tool.BEAN_PROCESSER_TYPE_W);
                  Iterator tmpItr = tmp.entrySet().iterator();
                  while (tmpItr.hasNext())
                  {
                     Map.Entry entry = (Map.Entry) tmpItr.next();
                     BeanMap.CellDescriptor bmc = (BeanMap.CellDescriptor) psInfo.get(entry.getKey());
                     if (bmc == null)
                     {
                        bmc = new BeanMap.CellDescriptor();
                        psInfo.put(entry.getKey(), bmc);
                        bmc.setName((String) entry.getKey());
                     }
                     JavassistTool.ProcesserInfo pi = (JavassistTool.ProcesserInfo) entry.getValue();
                     bmc.setWriteProcesser((BeanPropertyWriter) pi.processer);
                     bmc.setCellType(pi.type);
                     if (Tool.isBean(pi.type))
                     {
                        bmc.setBeanType(true);
                     }
                  }

                  fnName = "public Object getBeanValue(Object bean, String prefix, BeanMap beanMap)";
                  mh = StringTool.createStringAppender().append(fnName).appendln()
                        .append("      throws Exception").toString();
                  beginCode = endCode = "";
                  BeanPropertyReadProcesser rp = new BeanPropertyReadProcesser();
                  tmp = JavassistTool.createPropertyProcessers(beanClass, BeanPropertyReader.class, mh,
                        "bean", beginCode, endCode, rp, imports, Tool.BEAN_PROCESSER_TYPE_R);
                  tmpItr = tmp.entrySet().iterator();
                  while (tmpItr.hasNext())
                  {
                     Map.Entry entry = (Map.Entry) tmpItr.next();
                     BeanMap.CellDescriptor bmc = (BeanMap.CellDescriptor) psInfo.get(entry.getKey());
                     if (bmc == null)
                     {
                        bmc = new BeanMap.CellDescriptor();
                        psInfo.put(entry.getKey(), bmc);
                        bmc.setName((String) entry.getKey());
                     }
                     JavassistTool.ProcesserInfo pi = (JavassistTool.ProcesserInfo) entry.getValue();
                     if (bmc.getCellType() != null && bmc.getCellType() != pi.type)
                     {
                        Tool.log.error("Error cell [" + beanClass.getName() + "#" + entry.getKey()
                              + "] type in create MapToBean, write:[" + bmc.getCellType()
                              + "], read:[" + pi.type + "]");
                        continue;
                     }
                     bmc.setReadProcesser((BeanPropertyReader) pi.processer);
                     if (bmc.getCellType() == null)
                     {
                        bmc.setCellType(pi.type);
                        if (Tool.isBean(pi.type))
                        {
                           bmc.setBeanType(true);
                        }
                     }
                  }

                  // �����fnName��ǰ�����ͬ, �Ͳ������¸�ֵ��
                  beginCode = StringTool.createStringAppender().append(fnName).appendln()
                        .append("      throws Exception").appendln().append("{").toString();
                  endCode = "}";
                  String bodyCode = "return new " + beanClass.getName() + "();";
                  BeanPropertyReader tmpBPR = (BeanPropertyReader) JavassistTool.createPropertyProcesser(
                        beanClass, BeanPropertyReader.class, beginCode, bodyCode, endCode, imports);
                  BeanMap.CellDescriptor tmpBMC = new BeanMap.CellDescriptor();
                  tmpBMC.setName("<init>");
                  tmpBMC.setReadProcesser(tmpBPR);
                  tmpBMC.setCellType(beanClass);
                  tmpBMC.setBeanType(true);

                  bd = new BeanDescriptor(psInfo, tmpBMC);
               }
               catch (Throwable ex)
               {
                  Tool.log.error("Error in create MapToBean.", ex);
               }
            }
            if (bd != null)
            {
               beanDescriptorCache.put(beanClass, bd);
            }
            else
            {
               throw new IllegalArgumentException("Can't create bean properties info for ["
                     + beanClass + "].");
            }
         }
      }
      return bd;
   }

   private static Map mapToBeanCache = new ReferenceMap(ReferenceMap.WEAK, ReferenceMap.HARD);
   /**
    * ��ý�map��ֵ���õ�bean�����еĴ�����.
    */
   private static MapToBean getMapToBean(Class beanClass)
   {
      Object obj = mapToBeanCache.get(beanClass);
      if (obj == null)
      {
         synchronized (mapToBeanCache)
         {
            // �ٻ�ȡһ��, ��������߳��Ѵ�����, ����Ͳ�������
            obj = mapToBeanCache.get(beanClass);
            if (obj == null)
            {
               try
               {
                  String mh = StringTool.createStringAppender()
                        .append("public int setBeanValues(Object bean, Map values, String prefix)").appendln()
                        .append("      throws Exception").toString();
                  String beginCode = StringTool.createStringAppender()
                        .append("   Object ").append(TMP_OBJ_NAME).append(";").appendln()
                        .append("   int ").append(SETTED_COUNT_NAME).append(" = 0;").appendln()
                        .append("   String ").append(TMP_STR_NAME).append(";").appendln()
                        .toString();
                  String endCode =  StringTool.createStringAppender()
                        .append("   return ").append(SETTED_COUNT_NAME).append(";").toString();
                  String[] imports = new String[]{
                     Tool.getPackageString(Map.class),
                     Tool.getPackageString(Tool.class),
                     Tool.getPackageString(beanClass)
                  };
                  MapToBeanProcesser p = new MapToBeanProcesser("values");
                  obj = JavassistTool.createBeanProcesser(beanClass, MapToBean.class, mh,
                        "bean", beginCode, endCode, p, imports, Tool.BEAN_PROCESSER_TYPE_W);
               }
               catch (Throwable ex)
               {
                  Tool.log.error("Error in create MapToBean.", ex);
               }
            }
            if (obj != null)
            {
               mapToBeanCache.put(beanClass, obj);
            }
         }
      }
      return (MapToBean) obj;
   }

   private static Map beanConverterManagerCache = new ReferenceMap(ReferenceMap.WEAK, ReferenceMap.HARD);

   /**
    * ���ĳ��bean, ע��һ������ת����.
    */
   public static synchronized void registerConverter(Class beanClass, Class type, ValueConverter converter)
   {
      ConverterManager cm = (ConverterManager) beanConverterManagerCache.get(beanClass);
      if (cm == null)
      {
         cm = (ConverterManager) converterManager.clone();
         beanConverterManagerCache.put(beanClass, cm);
      }
      cm.registerConverter(type, converter);
   }

   /**
    * ���ĳ��bean, ע��һ������ת��ʱʹ�õ�<code>PropertyEditor</code>.
    */
   public static void registerPropertyEditor(Class beanClass, Class type, PropertyEditor pe)
   {
      ConverterManager cm = (ConverterManager) beanConverterManagerCache.get(beanClass);
      if (cm == null)
      {
         cm = (ConverterManager) converterManager.clone();
         beanConverterManagerCache.put(beanClass, cm);
      }
      cm.registerPropertyEditor(type, pe);
   }

   private static final ConverterManager converterManager = new ConverterManager();

   /**
    * ע��һ������ת����.
    */
   public static void registerConverter(Class type, ValueConverter converter)
   {
      converterManager.registerConverter(type, converter);
   }

   /**
    * ע��һ������ת��ʱʹ�õ�<code>PropertyEditor</code>.
    */
   public static void registerPropertyEditor(Class type, PropertyEditor pe)
   {
      converterManager.registerPropertyEditor(type, pe);
   }

   /**
    * ����ת����������ֵ��ȡ��Ӧ��ת����.
    *
    * @param index  ת����������ֵ
    */
   public static ValueConverter getConverter(int index)
   {
      return converterManager.getConverter(index);
   }

   private static final String GET_FIRST_VALUE_RES = "getFirstValue";
   /**
    * ��ȡ�Ի����������õĴ���.
    */
   private static StringAppender getPrimitiveSetCode(String wrapName, Class type, String converterBase,
         String resName, Map paramCache, StringAppender sa)
   {
      int vcIndex = converterManager.getConverterIndex(type);
      // ����������ȫ��ע��, ���ﲻ����-1
      codeRes.printRes(GET_FIRST_VALUE_RES, paramCache, 1, sa).appendln();
      String typeName = type.getName();
      paramCache.put("declareType", typeName);
      paramCache.put("converterType", "self.micromagic.eterna.sql.converter."
            + wrapName + "Converter");
      paramCache.put("converterName", converterBase + ".getConverter(" + vcIndex + ")");
      paramCache.put("converterMethod",
            "convertTo" + Character.toUpperCase(typeName.charAt(0)) + typeName.substring(1));
      codeRes.printRes(resName, paramCache, 1, sa).appendln();
      return sa;
   }

   /**
    * ��������ת����
    */
   public static final BooleanConverter booleanConverter = new BooleanConverter();
   public static final ByteConverter byteConverter = new ByteConverter();
   public static final BytesConverter bytesConverter = new BytesConverter();
   public static final ShortConverter shortConverter = new ShortConverter();
   public static final IntegerConverter intConverter = new IntegerConverter();
   public static final LongConverter longConverter = new LongConverter();
   public static final FloatConverter floatConverter = new FloatConverter();
   public static final DoubleConverter doubleConverter = new DoubleConverter();
   public static final TimeConverter timeConverter = new TimeConverter();
   public static final DateConverter dateConverter = new DateConverter();
   public static final TimestampConverter timestampConverter = new TimestampConverter();
   public static final StringConverter stringConverter = new StringConverter();
   public static final StreamConverter streamConverter = new StreamConverter();
   public static final ReaderConverter readerConverter = new ReaderConverter();
   public static final BigIntegerConverter bigIntegerConverter = new BigIntegerConverter();
   public static final DecimalConverter decimalConverter = new DecimalConverter();
   public static final UtilDateConverter utilDateConverter = new UtilDateConverter();
   public static final CalendarConverter calendarConverter = new CalendarConverter();
   public static final CharacterConverter charConverter = new CharacterConverter();


   private static ResManager codeRes = new ResManager();
   /**
    * ��ʼ��������Դ���������Ͷ�Ӧ��ת����.
    */
   static
   {
      try
      {
         codeRes.load(BeanTool.class.getResourceAsStream("BeanTool.res"));
      }
      catch (Exception ex)
      {
         Tool.log.error("Error in get code res.", ex);
      }

      registerConverter(Boolean.class, booleanConverter);
      registerConverter(boolean.class, booleanConverter);
      registerConverter(Character.class, charConverter);
      registerConverter(char.class, charConverter);
      registerConverter(Byte.class, byteConverter);
      registerConverter(byte.class, byteConverter);
      registerConverter(Short.class, shortConverter);
      registerConverter(short.class, shortConverter);
      registerConverter(Integer.class, intConverter);
      registerConverter(int.class, intConverter);
      registerConverter(Long.class, longConverter);
      registerConverter(long.class, longConverter);
      registerConverter(Float.class, floatConverter);
      registerConverter(float.class, floatConverter);
      registerConverter(Double.class, doubleConverter);
      registerConverter(double.class, doubleConverter);
      registerConverter(String.class, stringConverter);
      registerConverter(byte[].class, bytesConverter);
      registerConverter(java.util.Date.class, utilDateConverter);
      registerConverter(java.sql.Time.class, timeConverter);
      registerConverter(java.sql.Date.class, dateConverter);
      registerConverter(java.sql.Timestamp.class, timestampConverter);
      registerConverter(Calendar.class, calendarConverter);
      registerConverter(InputStream.class, streamConverter);
      registerConverter(MemoryStream.class, streamConverter);
      registerConverter(Reader.class, readerConverter);
      registerConverter(MemoryChars.class, readerConverter);
      registerConverter(BigInteger.class, bigIntegerConverter);
      registerConverter(BigDecimal.class, decimalConverter);
   }

   /**
    * ���bean����ı�����.
    */
   static final String BEAN_NAME = "beanObj";

   /**
    * ��Ŷ�ȡ����ʱ����ı�����.
    */
   private static final String TMP_OBJ_NAME = "tmpObj";

   /**
    * ������õ����Ը����ı�����.
    */
   private static final String SETTED_COUNT_NAME = "settedCount";

   /**
    * �����Ҫ��ȡ������ǰ׺�ı�����.
    */
   private static final String PREFIX_NAME = "prefix";

   /**
    * �����Ҫ��ȡ������(��ƴ����ǰ׺�������)�ı�����.
    */
   private static final String TMP_STR_NAME = "tmpStr";

   /**
    * ��������ʱ��ת����������.
    */
   static class ConverterManager
         implements Cloneable
   {
      private ValueConverter[] converters = new ValueConverter[16];
      private int usedCount;
      private HashMap converterIndexMap = new HashMap();

      /**
       * ����ת����������ֵ��ȡ��Ӧ��ת����.
       *
       * @param index  ת����������ֵ
       */
      ValueConverter getConverter(int index)
      {
         return this.converters[index];
      }

      /**
       * ����ֵ�����ͻ��ת����������ֵ.
       *
       * @param type    ֵ������
       * @return   -1δ�ҵ���Ӧ��ת����, ������ڵ���0��Ϊת������Ӧ������ֵ
       * @see #getConverter(int)
       */
      int getConverterIndex(Class type)
      {
         Integer i = (Integer) this.converterIndexMap.get(type);
         if (i == null)
         {
            return -1;
         }
         return i.intValue();
      }

      /**
       * ��һ������ע��һ��ת����.
       */
      synchronized void registerConverter(Class type, ValueConverter converter)
      {
         if (converter == null)
         {
            return;
         }
         Integer i = (Integer) this.converterIndexMap.get(type);
         if (i == null)
         {
            if (this.converters.length <= this.usedCount + 1)
            {
               int newCapacity = this.usedCount + 16;
               ValueConverter[] newConverters = new ValueConverter[newCapacity];
               System.arraycopy(this.converters, 0, newConverters, 0, this.converters.length);
               this.converters = newConverters;
            }
            i = Utility.createInteger(++this.usedCount);
            this.converterIndexMap.put(type, i);
         }
         if (this.converters[i.intValue()] != null && type.isPrimitive())
         {
            if (this.converters[i.intValue()].getClass() != converter.getClass())
            {
               throw new IllegalArgumentException("For the primitive [" + type
                     + "], the ValueConverter class must same as the old.");
            }
         }
         this.converters[i.intValue()] = converter;
      }

      /**
       * ��һ������ע��һ��<code>PropertyEditor</code>, ת������ʹ����������ת��.
       */
      synchronized void registerPropertyEditor(Class type, PropertyEditor pe)
      {
         if (pe == null)
         {
            return;
         }
         if (type.isPrimitive())
         {
            int tmpI = this.getConverterIndex(type);
            ValueConverter vc = (ValueConverter) this.converters[tmpI].clone();
            vc.setPropertyEditor(pe);
            this.converters[tmpI] = vc;
         }
         else
         {
            ValueConverter vc = new ObjectConverter();
            vc.setPropertyEditor(pe);
            this.registerConverter(type, vc);
         }
      }

      /**
       * ��¡ת����������.
       */
      public Object clone()
      {
         ConverterManager result = null;
         try
         {
            result = (ConverterManager) super.clone();
         }
         catch (CloneNotSupportedException e)
         {
            // assert false;
         }
         result.converters = (ValueConverter[]) this.converters.clone();
         result.converterIndexMap = (HashMap) this.converterIndexMap.clone();
         return result;
      }

   }

   private static class MapToBeanProcesser
         implements JavassistTool.UnitProcesser
   {
      private static final String GET_MAP_VALUE_RES = "mapSet.getMapValue";

      protected Map paramCache = new HashMap();

      public MapToBeanProcesser(String mapName)
      {
         this.paramCache.put("beanName", BEAN_NAME);
         this.paramCache.put("mapName", mapName);
         this.paramCache.put("tmpObjName", TMP_OBJ_NAME);
         this.paramCache.put("settedCountName", SETTED_COUNT_NAME);
         this.paramCache.put("prefixName", PREFIX_NAME);
         this.paramCache.put("tmpStr", TMP_STR_NAME);
      }

      public String getFieldCode(Field f, Class type, String wrapName, int processerType)
      {
         this.paramCache.put("pName", f.getName());
         this.paramCache.put("fieldName", f.getName());
         String[] resNames = new String[] {
            "mapSet.primitiveFieldSet", "convertTypeFieldSet",
            "mapSet.beanTypeFieldSet", "otherTypeFieldSet"
         };
         return this.getProcesserCode(type, f.getName(), wrapName, resNames);
      }

      public String getMethodCode(Tool.BeanMethodInfo m, Class type, String wrapName, int processerType)
      {
         this.paramCache.put("pName", m.name);
         this.paramCache.put("methodName", m.method.getName());
         String[] resNames = new String[] {
            "mapSet.primitiveMethodSet", "convertTypeMethodSet",
            "mapSet.beanTypeMethodSet", "otherTypeMethodSet"
         };
         return this.getProcesserCode(type, m.name, wrapName, resNames);
      }

      protected String getProcesserCode(Class type, String pName, String wrapName, String[] resNames)
      {
         StringAppender sa = StringTool.createStringAppender(128);
         codeRes.printRes(GET_MAP_VALUE_RES, this.paramCache, 1, sa).appendln();
         if (wrapName != null)
         {
            sa = BeanTool.getPrimitiveSetCode(wrapName, type, "BeanTool", resNames[0],
                  this.paramCache, sa);
         }
         else
         {
            int vcIndex = converterManager.getConverterIndex(type);
            if (vcIndex != -1)
            {
               codeRes.printRes(GET_FIRST_VALUE_RES, this.paramCache, 1, sa).appendln();
               this.paramCache.put("converterName", "BeanTool.getConverter(" + vcIndex + ")");
               this.paramCache.put("className", type.getName());
               codeRes.printRes(resNames[1], this.paramCache, 1, sa).appendln();
            }
            else if (Tool.isBean(type))
            {
               this.paramCache.put("className", type.getName());
               codeRes.printRes(resNames[2], this.paramCache, 1, sa).appendln();
            }
            else
            {
               this.paramCache.put("className", type.getName());
               codeRes.printRes(resNames[3], this.paramCache, 1, sa).appendln();
            }
         }
         sa.appendln();
         return sa.toString();
      }

   }

   private static class BeanPropertyWriteProcesser
         implements JavassistTool.UnitProcesser
   {
      protected Map paramCache = new HashMap();
      protected String beanMapName;

      public BeanPropertyWriteProcesser(String valueName, String beanMapName, String originName,
            String oldValueName)
      {
         this.paramCache.put("beanName", BEAN_NAME);
         this.paramCache.put("originObjName", originName);
         this.paramCache.put("tmpObjName", valueName);
         this.paramCache.put("settedCountName", SETTED_COUNT_NAME);
         this.paramCache.put("prefixName", PREFIX_NAME);
         this.paramCache.put("oldValueName", oldValueName);
         this.beanMapName = beanMapName;
      }

      public String getFieldCode(Field f, Class type, String wrapName, int processerType)
      {
         this.paramCache.put("pName", f.getName());
         this.paramCache.put("fieldName", f.getName());
         String[] resNames = new String[] {
            "beanMap.primitiveFieldSet", "convertTypeFieldSet",
            "beanMap.beanTypeFieldSet", "otherTypeFieldSet"
         };
         return this.getProcesserCode(type, f.getName(), wrapName, resNames);
      }

      public String getMethodCode(Tool.BeanMethodInfo m, Class type, String wrapName, int processerType)
      {
         this.paramCache.put("pName", m.name);
         this.paramCache.put("methodName", m.method.getName());
         String[] resNames = new String[] {
            "beanMap.primitiveMethodSet", "convertTypeMethodSet",
            "beanMap.beanTypeMethodSet", "otherTypeMethodSet"
         };
         return this.getProcesserCode(type, m.name, wrapName, resNames);
      }

      protected String getProcesserCode(Class type, String pName, String wrapName, String[] resNames)
      {
         StringAppender sa = StringTool.createStringAppender(128);
         if (wrapName != null)
         {
            sa = BeanTool.getPrimitiveSetCode(wrapName, type, this.beanMapName, resNames[0],
                  this.paramCache, sa);
         }
         else
         {
            int vcIndex = converterManager.getConverterIndex(type);
            if (vcIndex != -1)
            {
               codeRes.printRes(GET_FIRST_VALUE_RES, this.paramCache, 1, sa).appendln();
               this.paramCache.put("converterName", this.beanMapName + ".getConverter(" + vcIndex + ")");
               this.paramCache.put("className", type.getName());
               codeRes.printRes(resNames[1], this.paramCache, 1, sa).appendln();
            }
            else if (Tool.isBean(type))
            {
               this.paramCache.put("className", type.getName());
               codeRes.printRes(resNames[2], this.paramCache, 1, sa).appendln();
            }
            else
            {
               this.paramCache.put("className", type.getName());
               codeRes.printRes(resNames[3], this.paramCache, 1, sa).appendln();
            }
         }
         return sa.toString();
      }

   }

   private static class BeanPropertyReadProcesser
         implements JavassistTool.UnitProcesser
   {
      protected Map paramCache = new HashMap();

      public BeanPropertyReadProcesser()
      {
         this.paramCache.put("beanName", BEAN_NAME);
      }

      public String getFieldCode(Field f, Class type, String wrapName, int processerType)
      {
         this.paramCache.put("fieldName", f.getName());
         String[] resNames = new String[] {
            "primitiveFieldGet", "otherTypeFieldGet"
         };
         return this.getProcesserCode(type, f.getName(), wrapName, resNames);
      }

      public String getMethodCode(Tool.BeanMethodInfo m, Class type, String wrapName, int processerType)
      {
         this.paramCache.put("methodName", m.method.getName());
         String[] resNames = new String[] {
            "primitiveMethodGet", "otherTypeMethodGet"
         };
         return this.getProcesserCode(type, m.name, wrapName, resNames);
      }

      protected String getProcesserCode(Class type, String pName, String wrapName, String[] resNames)
      {
         StringAppender sa = StringTool.createStringAppender(128);
         if (wrapName != null)
         {
            this.paramCache.put("wrapName", wrapName);
            codeRes.printRes(resNames[0], this.paramCache, 1, sa).appendln();
         }
         else
         {
            codeRes.printRes(resNames[1], this.paramCache, 1, sa).appendln();
         }
         return sa.toString();
      }

   }

   /**
    * ��map�е��������õ�bean�еĹ���.
    */
   public interface MapToBean
   {
      public int setBeanValues(Object bean, Map values, String prefix) throws Exception;

   }

   /**
    * ��ȡһ��bean���ԵĹ���.
    */
   public interface BeanPropertyReader
   {
      public Object getBeanValue(Object bean, String prefix, BeanMap beanMap) throws Exception;

   }

   /**
    * ����һ��bean���ԵĹ���.
    */
   public interface BeanPropertyWriter
   {
      public int setBeanValue(Object bean, Object value, String prefix, BeanMap beanMap,
            Object originObj, Object oldValue)
            throws Exception;

   }

   static class BeanDescriptor
   {
      public final Map cells;
      public final BeanMap.CellDescriptor initCell;

      public BeanDescriptor(Map cells, BeanMap.CellDescriptor initCell)
      {
         this.cells = cells;
         this.initCell = initCell;
      }

   }

}
