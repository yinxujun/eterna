
package self.micromagic.cg;

import java.beans.PropertyEditor;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import self.micromagic.eterna.sql.ResultRow;
import self.micromagic.util.converter.*;
import self.micromagic.util.MemoryChars;
import self.micromagic.util.MemoryStream;
import self.micromagic.util.ResManager;
import self.micromagic.util.StringAppender;
import self.micromagic.util.StringTool;
import self.micromagic.util.Utility;
import self.micromagic.util.container.SynHashMap;

/**
 * ��bean���в����Ĺ���.
 */
public class BeanTool
{
   /**
    * �����Ƿ�Ҫʹ��Ĭ�ϵ�bean�����.
    */
   public static final String CG_USE_DBC_PROPERTY = "self.micromagic.cg.use.defaultBeanChecker";

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
      return new BeanMap(bean, prefix, bd);
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
      return new BeanMap(beanType, prefix, bd);
   }

   /**
    * ���BeanMap�ж�bean���ԵĲ�����Ԫ.
    */
   public static CellDescriptor getBeanMapCell(Class type, String name)
   {
      BeanDescriptor db = getBeanDescriptor(type);
      if (db != null)
      {
         return db.getCell(name);
      }
      return null;
   }

   /**
    * ���BeanMap�ж�bean�Ĺ��쵥Ԫ.
    */
   public static CellDescriptor getBeanMapInitCell(Class type)
   {
      BeanDescriptor db = getBeanDescriptor(type);
      if (db != null)
      {
         return db.getInitCell();
      }
      return null;
   }

   /**
    * �Ƴ�BeanMap�Ľṹ��Ϣ, �Ƴ����ٴ�ʹ��ʱ���ܹ����¹���ṹ��Ϣ.
    */
   public static synchronized void removeBeanDescriptor(Class type)
   {
      beanDescriptorCache.removeProperty(type);
      //beanDescriptorCache.remove(type);
   }

   private static ClassKeyCache beanDescriptorCache = ClassKeyCache.getInstance();
   //private static java.util.WeakHashMap beanDescriptorCache = new java.util.WeakHashMap();
   /**
    * ��ö�bean���������Ϣ.
    */
   public static BeanDescriptor getBeanDescriptor(Class beanClass)
   {
      if (beanClass == null)
      {
         return null;
      }
      BeanDescriptor bd = (BeanDescriptor) beanDescriptorCache.getProperty(beanClass);
      //BeanDescriptor bd = (BeanDescriptor) beanDescriptorCache.get(beanClass);
      if (bd == null)
      {
         bd = getBeanDescriptor0(beanClass);
      }
      return bd;
   }
   private static synchronized BeanDescriptor getBeanDescriptor0(Class beanClass)
   {
      BeanDescriptor bd = (BeanDescriptor) beanDescriptorCache.getProperty(beanClass);
      //BeanDescriptor bd = (BeanDescriptor) beanDescriptorCache.get(beanClass);
      if (bd == null)
      {
         Map psInfo = new HashMap();
         Map tmp;
         Iterator tmpItr;
         try
         {
            String fnName = "public int setBeanValue(CellDescriptor cd, int[] indexs, Object bean, "
                  + "Object value, String prefix, BeanMap beanMap, Object originObj, Object oldValue)";
            String mh = StringTool.createStringAppender().append(fnName).appendln()
                  .append("      throws Exception").toString();
            String beginCode = StringTool.createStringAppender()
                  .append("   int ").append(SETTED_COUNT_NAME).append(" = 0;").appendln()
                  .toString();
            String endCode =  StringTool.createStringAppender()
                  .append("   return ").append(SETTED_COUNT_NAME).append(";").toString();
            String[] imports = {
               ClassGenerator.getPackageString(Map.class),
               ClassGenerator.getPackageString(BeanTool.class),
               ClassGenerator.getPackageString(ResultRow.class),
               ClassGenerator.getPackageString(beanClass)
            };
            BeanPropertyWriteProcesser wp = new BeanPropertyWriteProcesser(
                  "value", "beanMap", "originObj", "oldValue");
            tmp = createPropertyProcessers(beanClass, BeanPropertyWriter.class,
                  mh, "bean", beginCode, endCode, wp, imports, BEAN_PROCESSER_TYPE_W);
            tmpItr = tmp.entrySet().iterator();
            while (tmpItr.hasNext())
            {
               Map.Entry entry = (Map.Entry) tmpItr.next();
               CellDescriptor bmc = (CellDescriptor) psInfo.get(entry.getKey());
               if (bmc == null)
               {
                  bmc = new CellDescriptor();
                  psInfo.put(entry.getKey(), bmc);
                  bmc.setName((String) entry.getKey());
               }
               ProcesserInfo pi = (ProcesserInfo) entry.getValue();
               bmc.setWriteProcesser((BeanPropertyWriter) pi.processer);
               bmc.setCellType(pi.type);
               if (pi.type.isArray())
               {
                  bmc.setArrayType(true);
               }
               else if (Collection.class.isAssignableFrom(pi.type))
               {
                  bmc.setReadOldValue(true);
               }
               else if (checkBean(pi.type))
               {
                  bmc.setBeanType(true);
               }
            }

            fnName = "public Object getBeanValue(CellDescriptor cd, int[] indexs, Object bean, "
                  + "String prefix, BeanMap beanMap)";
            mh = StringTool.createStringAppender().append(fnName).appendln()
                  .append("      throws Exception").toString();
            beginCode = endCode = "";
            BeanPropertyReadProcesser rp = new BeanPropertyReadProcesser(beanClass);
            tmp = createPropertyProcessers(beanClass, BeanPropertyReader.class,
                  mh, "bean", beginCode, endCode, rp, imports, BEAN_PROCESSER_TYPE_R);
            tmpItr = tmp.entrySet().iterator();
            while (tmpItr.hasNext())
            {
               Map.Entry entry = (Map.Entry) tmpItr.next();
               CellDescriptor bmc = (CellDescriptor) psInfo.get(entry.getKey());
               if (bmc == null)
               {
                  bmc = new CellDescriptor();
                  psInfo.put(entry.getKey(), bmc);
                  bmc.setName((String) entry.getKey());
               }
               ProcesserInfo pi = (ProcesserInfo) entry.getValue();
               if (bmc.getCellType() != null && bmc.getCellType() != pi.type)
               {
                  CG.log.error("Error cell [" + ClassGenerator.getClassName(beanClass)
                        + "#" + entry.getKey() + "] type in create MapToBean, write:["
                        + bmc.getCellType() + "], read:[" + pi.type + "]");
                  continue;
               }
               bmc.setReadProcesser((BeanPropertyReader) pi.processer);
               if (bmc.getCellType() == null)
               {
                  bmc.setCellType(pi.type);
                  if (pi.type.isArray())
                  {
                     bmc.setArrayType(true);
                  }
                  else if (Collection.class.isAssignableFrom(pi.type))
                  {
                     bmc.setReadOldValue(true);
                  }
                  else if (checkBean(pi.type))
                  {
                     bmc.setBeanType(true);
                  }
               }
            }

            // �����fnName��ǰ�����ͬ, �Ͳ������¸�ֵ��
            beginCode = StringTool.createStringAppender().append(fnName).appendln()
                  .append("      throws Exception").appendln().append("{").toString();
            endCode = "}";
            String bodyCode = "return new " + ClassGenerator.getClassName(beanClass) + "();";
            BeanPropertyReader tmpBPR;
            tmpBPR = (BeanPropertyReader) createPropertyProcesser("P_init",
                  beanClass, BeanPropertyReader.class, beginCode, bodyCode, endCode, imports);
            CellDescriptor tmpBMC = new CellDescriptor();
            tmpBMC.setName("<init>");
            tmpBMC.setReadProcesser(tmpBPR);
            tmpBMC.setCellType(beanClass);
            tmpBMC.setBeanType(true);

            bd = new BeanDescriptor(beanClass, psInfo, tmpBMC);
         }
         catch (Throwable ex)
         {
            CG.log.error("Error in create MapToBean.", ex);
         }
      }
      if (bd != null)
      {
         beanDescriptorCache.setProperty(beanClass, bd);
         //beanDescriptorCache.put(beanClass, bd);
      }
      else
      {
         throw new IllegalArgumentException("Can't create bean properties info for ["
               + beanClass + "].");
      }
      return bd;
   }

   private static ClassKeyCache mapToBeanCache = ClassKeyCache.getInstance();
   /**
    * ��ý�map��ֵ���õ�bean�����еĴ�����.
    */
   private static MapToBean getMapToBean(Class beanClass)
   {
      Object obj = mapToBeanCache.getProperty(beanClass);
      if (obj == null)
      {
         obj = getMapToBean0(beanClass);
      }
      return (MapToBean) obj;
   }
   private static synchronized MapToBean getMapToBean0(Class beanClass)
   {
      Object obj = mapToBeanCache.getProperty(beanClass);
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
            String[] imports = {
               ClassGenerator.getPackageString(Map.class),
               ClassGenerator.getPackageString(BeanTool.class),
               ClassGenerator.getPackageString(beanClass)
            };
            MapToBeanProcesser p = new MapToBeanProcesser("values");
            obj = createBeanProcesser(beanClass, MapToBean.class, mh,
                  "bean", beginCode, endCode, p, imports, BEAN_PROCESSER_TYPE_W);
         }
         catch (Throwable ex)
         {
            CG.log.error("Error in create MapToBean.", ex);
         }
      }
      if (obj != null)
      {
         mapToBeanCache.setProperty(beanClass, obj);
      }
      return (MapToBean) obj;
   }


   /**
    * ���bean���е����й��зǾ�̬������
    */
   public static Field[] getBeanFields(Class c)
   {
      List result = new ArrayList();
      Field[] fs = c.getFields();
      for (int i = 0; i < fs.length; i++)
      {
         Field f = fs[i];
         if (!Modifier.isStatic(f.getModifiers()))
         {
            result.add(f);
         }
      }
      return (Field[]) result.toArray(new Field[result.size()]);
   }

   /**
    * ���bean���е����й�����get����
    */
   public static BeanMethodInfo[] getBeanReadMethods(Class c)
   {
      List result = new ArrayList();
      BeanMethodInfo[] infos = BeanMethodInfo.getBeanMethods(c);
      for (int i = 0; i < infos.length; i++)
      {
         BeanMethodInfo info = infos[i];
         if (info.doGet)
         {
            result.add(info);
         }
      }
      return (BeanMethodInfo[]) result.toArray(new BeanMethodInfo[result.size()]);
   }

   /**
    * ���bean���е����й�����set����
    */
   public static BeanMethodInfo[] getBeanWriteMethods(Class c)
   {
      List result = new ArrayList();
      BeanMethodInfo[] infos = BeanMethodInfo.getBeanMethods(c);
      for (int i = 0; i < infos.length; i++)
      {
         BeanMethodInfo info = infos[i];
         if (!info.doGet)
         {
            result.add(info);
         }
      }
      return (BeanMethodInfo[]) result.toArray(new BeanMethodInfo[result.size()]);
   }

   /**
    * �����ȡ�Ĺ���.
    */
   public static int BEAN_PROCESSER_TYPE_R = 0;

   /**
    * ����д��Ĺ���.
    */
   public static int BEAN_PROCESSER_TYPE_W = 1;

   /**
    * ����һ��bean�Ĵ�����.
    *
    * @param suffix              ���������ĺ�׺
    * @param beanClass           bean��
    * @param interfaceClass      ����ӿ�
    * @param methodHead          ����ͷ��
    * @param beanParamName       bean����������
    * @param unitTemplate        ��Ԫ����ģ��
    * @param primitiveTemplate   �������͵�Ԫ����ģ��
    * @param linkTemplate        �������͵�Ԫ֮�������ģ��
    * @param imports             Ҫ����İ�
    * @param processerType       ����������õĹ��̻��Ƕ�ȡ�Ĺ���
    * @return                    ������Ӧ�Ĵ�����
    */
   public static Object createBeanProcesser(String suffix, Class beanClass, Class interfaceClass, String methodHead,
         String beanParamName, String unitTemplate, String primitiveTemplate, String linkTemplate,
         String[] imports, int processerType)
   {
      ClassGenerator cg = ClassGenerator.createClassGenerator(suffix, beanClass, interfaceClass, imports);
      StringAppender function = StringTool.createStringAppender(256);
      function.append(methodHead).appendln().append("{").appendln();
      function.append("   ").append(ClassGenerator.getClassName(beanClass)).append(" ").append(BeanTool.BEAN_NAME)
            .append(" = (").append(ClassGenerator.getClassName(beanClass)).append(") ").append(beanParamName)
            .append(";").appendln();
      boolean first = true;

      Map dataMap = new HashMap();

      Field[] fields = BeanTool.getBeanFields(beanClass);
      for (int i = 0; i < fields.length; i++)
      {
         if (processerType == BEAN_PROCESSER_TYPE_W && Modifier.isFinal(fields[i].getModifiers()))
         {
            continue;
         }
         if (!first)
         {
            function.append(Utility.resolveDynamicPropnames(linkTemplate, dataMap, true)).appendln();
         }
         first = false;
         dataMap.clear();
         Field f = fields[i];
         dataMap.put("name", f.getName());
         dataMap.put("type", "field");
         if (f.getType().isPrimitive())
         {
            String pType = ClassGenerator.getClassName(f.getType());
            dataMap.put("primitive", pType);
            dataMap.put("value", "String.valueOf(" + BeanTool.BEAN_NAME + "." + f.getName() + ")");
            dataMap.put("o_value", BeanTool.BEAN_NAME + "." + f.getName());
            dataMap.put("wrapName", BeanTool.getPrimitiveWrapClassName(pType));
            function.append(Utility.resolveDynamicPropnames(primitiveTemplate, dataMap, true))
                  .appendln();
         }
         else
         {
            dataMap.put("value", BeanTool.BEAN_NAME + "." + f.getName());
            function.append(Utility.resolveDynamicPropnames(unitTemplate, dataMap, true)).appendln();
         }
      }
      BeanMethodInfo[] methods = processerType == BEAN_PROCESSER_TYPE_W ?
            BeanTool.getBeanWriteMethods(beanClass) : BeanTool.getBeanReadMethods(beanClass);
      for (int i = 0; i < methods.length; i++)
      {
         BeanMethodInfo m = methods[i];
         if (m.method != null)
         {
            if (!first)
            {
               function.append(Utility.resolveDynamicPropnames(linkTemplate, dataMap, true))
                     .appendln();
            }
            first = false;
            dataMap.put("name", m.name);
            dataMap.put("type", "method");
            if (m.type.isPrimitive())
            {
               String pType = ClassGenerator.getClassName(m.type);
               dataMap.put("primitive", pType);
               dataMap.put("value",
                     "String.valueOf(" + BeanTool.BEAN_NAME + "." + m.method.getName() + "())");
               dataMap.put("o_value", BeanTool.BEAN_NAME + "." + m.method.getName() + "()");
               dataMap.put("wrapName", BeanTool.getPrimitiveWrapClassName(pType));
               function.append(Utility.resolveDynamicPropnames(primitiveTemplate, dataMap, true))
                     .appendln();
            }
            else
            {
               dataMap.put("value", BeanTool.BEAN_NAME + "." + m.method.getName() + "()");
               function.append(Utility.resolveDynamicPropnames(unitTemplate, dataMap, true))
                     .appendln();
            }
         }
      }
      function.append("}");

      cg.addMethod(function.toString());
      cg.setClassLoader(beanClass.getClassLoader());
      try
      {
         return cg.createClass().newInstance();
      }
      catch (Throwable ex)
      {
         if (ClassGenerator.COMPILE_LOG_TYPE > CG.COMPILE_LOG_TYPE_ERROR)
         {
            CG.log.error("Error in create bean processer.", ex);
         }
         return null;
      }
   }

   /**
    * ����һ��bean�Ĵ�����.
    *
    * @param beanClass           bean��
    * @param interfaceClass      ����ӿ�
    * @param methodHead          ����ͷ��
    * @param beanParamName       bean����������
    * @param beginCode           ������ʼ���ֵĴ���
    * @param endCode             �����������ֵĴ���
    * @param unitProcesser       ���Ե�Ԫ�Ĵ�����
    * @param imports             Ҫ����İ�
    * @param processerType       ����������õĹ��̻��Ƕ�ȡ�Ĺ���
    * @return                    ������Ӧ�Ĵ�����
    */
   static Object createBeanProcesser(Class beanClass, Class interfaceClass, String methodHead, String beanParamName,
         String beginCode, String endCode, UnitProcesser unitProcesser, String[] imports, int processerType)
   {
      ClassGenerator cg = ClassGenerator.createClassGenerator("Processer", beanClass, interfaceClass, imports);
      StringAppender function = StringTool.createStringAppender(256);
      function.append(methodHead).appendln().append("{").appendln();
      function.append("   ").append(ClassGenerator.getClassName(beanClass)).append(" ").append(BeanTool.BEAN_NAME)
            .append(" = (").append(ClassGenerator.getClassName(beanClass)).append(") ").append(beanParamName)
            .append(";").appendln();
      function.append(beginCode).appendln();

      Field[] fields = getBeanFields(beanClass);
      for (int i = 0; i < fields.length; i++)
      {
         if (processerType == BEAN_PROCESSER_TYPE_W && Modifier.isFinal(fields[i].getModifiers()))
         {
            continue;
         }
         String code;
         Field f = fields[i];
         if (f.getType().isPrimitive())
         {
            String wrapName = (String) BeanTool.primitiveWrapClass.get(
                  ClassGenerator.getClassName(f.getType()));
            code = unitProcesser.getFieldCode(f, f.getType(), wrapName, processerType, cg);
         }
         else
         {
            code = unitProcesser.getFieldCode(f, f.getType(), null, processerType, cg);
         }
         function.append(code).appendln();
      }
      BeanMethodInfo[] methods = processerType == BEAN_PROCESSER_TYPE_W ?
            getBeanWriteMethods(beanClass) : getBeanReadMethods(beanClass);
      for (int i = 0; i < methods.length; i++)
      {
         String code;
         BeanMethodInfo m = methods[i];
         if (m.type.isPrimitive())
         {
            String wrapName = (String) BeanTool.primitiveWrapClass.get(ClassGenerator.getClassName(m.type));
            code = unitProcesser.getMethodCode(m, m.type, wrapName, processerType, cg);
         }
         else
         {
            code = unitProcesser.getMethodCode(m, m.type, null, processerType, cg);
         }
         if (!StringTool.isEmpty(code))
         {
            function.append(code).appendln();
         }
      }
      function.append(endCode).appendln().append("}");

      cg.addMethod(function.toString());
      cg.setClassLoader(beanClass.getClassLoader());
      try
      {
         return cg.createClass().newInstance();
      }
      catch (Throwable ex)
      {
         if (ClassGenerator.COMPILE_LOG_TYPE > CG.COMPILE_LOG_TYPE_ERROR)
         {
            CG.log.error("Error in create bean processer.", ex);
         }
         return null;
      }
   }

   /**
    * ��һ��bean����һ�����ԵĴ�����.
    *
    * @param beanClass           bean��
    * @param interfaceClass      ����ӿ�
    * @param methodHead          ����ͷ��
    * @param beanParamName       bean����������
    * @param beginCode           ������ʼ���ֵĴ���
    * @param endCode             �����������ֵĴ���
    * @param unitProcesser       ���Ե�Ԫ�Ĵ�����
    * @param imports             Ҫ����İ�
    * @param processerType       ����������õĹ��̻��Ƕ�ȡ�Ĺ���
    * @return                    ������Ӧ�Ĵ�����
    */
   static Map createPropertyProcessers(Class beanClass, Class interfaceClass, String methodHead, String beanParamName,
         String beginCode, String endCode, UnitProcesser unitProcesser, String[] imports, int processerType)
   {
      Map result = new HashMap();
      String beginCode0 = StringTool.createStringAppender(256)
            .append(methodHead).appendln().append("{").appendln()
            .append("   ").append(ClassGenerator.getClassName(beanClass)).append(" ").append(BeanTool.BEAN_NAME)
            .append(" = (").append(ClassGenerator.getClassName(beanClass)).append(") ").append(beanParamName)
            .append(";").appendln().append(beginCode).appendln().toString();
      String endCode0 = StringTool.isEmpty(endCode) ? "}"
            : StringTool.createStringAppender(endCode, 5, false).appendln().append("}").toString();

      Field[] fields = getBeanFields(beanClass);
      for (int i = 0; i < fields.length; i++)
      {
         if (processerType == BEAN_PROCESSER_TYPE_W && Modifier.isFinal(fields[i].getModifiers()))
         {
            continue;
         }
         String code;
         Field f = fields[i];
         String wrapName = null;
         if (f.getType().isPrimitive())
         {
            wrapName = (String) BeanTool.primitiveWrapClass.get(ClassGenerator.getClassName(f.getType()));
         }
         ClassGenerator cg = ClassGenerator.createClassGenerator("P_" + f.getName(),
               beanClass, interfaceClass, imports);
         code = unitProcesser.getFieldCode(f, f.getType(), wrapName, processerType, cg);
         Object p = createPropertyProcesser(cg, beanClass, beginCode0, code, endCode0);
         result.put(f.getName(), new ProcesserInfo(f.getName(), f.getType(), p));
      }
      BeanMethodInfo[] methods = processerType == BEAN_PROCESSER_TYPE_W ?
            getBeanWriteMethods(beanClass) : getBeanReadMethods(beanClass);
      for (int i = 0; i < methods.length; i++)
      {
         String code;
         BeanMethodInfo m = methods[i];
         String wrapName = null;
         if (m.type.isPrimitive())
         {
            wrapName = (String) BeanTool.primitiveWrapClass.get(ClassGenerator.getClassName(m.type));
         }
         ClassGenerator cg = ClassGenerator.createClassGenerator("P_" + m.name,
               beanClass, interfaceClass, imports);
         code = unitProcesser.getMethodCode(m, m.type, wrapName, processerType, cg);
         if (!StringTool.isEmpty(code))
         {
            Object p = createPropertyProcesser(cg, beanClass, beginCode0, code, endCode0);
            result.put(m.name, new ProcesserInfo(m.name, m.type, p));
         }
      }

      return result;
   }

   /**
    * ����һ�����ԵĴ�����.
    *
    * @param cg               ���ɴ�����Ĵ���������
    * @param beanClass           bean��
    * @param beginCode        ������ʼ���ֵĴ���
    * @param bodyCode         �������ⲿ�ֵĴ���
    * @param endCode          �����������ֵĴ���
    * @return                 ������Ӧ�Ĵ�����
    */
   static Object createPropertyProcesser(ClassGenerator cg, Class beanClass, String beginCode,
         String bodyCode, String endCode)
   {
      StringAppender function = StringTool.createStringAppender(256);
      function.append(beginCode).appendln()
            .append(bodyCode).appendln()
            .append(endCode).appendln();
      cg.addMethod(function.toString());
      cg.setClassLoader(beanClass.getClassLoader());
      try
      {
         return cg.createClass().newInstance();
      }
      catch (Throwable ex)
      {
         if (ClassGenerator.COMPILE_LOG_TYPE > CG.COMPILE_LOG_TYPE_ERROR)
         {
            CG.log.error("Error in create bean processer.", ex);
         }
         return null;
      }
   }

   /**
    * ����һ�����ԵĴ�����.
    *
    * @param suffix              ���������ĺ�׺
    * @param beanClass           bean��
    * @param interfaceClass      ����ӿ�
    * @param beginCode           ������ʼ���ֵĴ���
    * @param bodyCode            �������ⲿ�ֵĴ���
    * @param endCode             �����������ֵĴ���
    * @param imports             Ҫ����İ�
    * @return                    ������Ӧ�Ĵ�����
    */
   static Object createPropertyProcesser(String suffix, Class beanClass, Class interfaceClass,
         String beginCode, String bodyCode, String endCode, String[] imports)
   {
      ClassGenerator cg = ClassGenerator.createClassGenerator(suffix, beanClass, interfaceClass, imports);
      StringAppender function = StringTool.createStringAppender(256);
      function.append(beginCode).appendln()
            .append(bodyCode).appendln()
            .append(endCode).appendln();
      cg.addMethod(function.toString());
      cg.setClassLoader(beanClass.getClassLoader());
      try
      {
         return cg.createClass().newInstance();
      }
      catch (Throwable ex)
      {
         if (ClassGenerator.COMPILE_LOG_TYPE > CG.COMPILE_LOG_TYPE_ERROR)
         {
            CG.log.error("Error in create bean processer.", ex);
         }
         return null;
      }
   }

   /**
    * ���ĳ��bean, ע��һ������ת����.
    */
   public static synchronized void registerConverter(Class beanClass, Class type, ValueConverter converter)
   {
      BeanDescriptor bd = getBeanDescriptor(beanClass);
      if (bd.getConverterManager() == converterManager)
      {
         bd.setConverterManager((ConverterManager) converterManager.clone());
      }
      bd.getConverterManager().registerConverter(type, converter);
   }

   /**
    * ���ĳ��bean, ע��һ������ת��ʱʹ�õ�<code>PropertyEditor</code>.
    */
   public static synchronized void registerPropertyEditor(Class beanClass, Class type, PropertyEditor pe)
   {
      BeanDescriptor bd = getBeanDescriptor(beanClass);
      if (bd.getConverterManager() == converterManager)
      {
         bd.setConverterManager((ConverterManager) converterManager.clone());
      }
      bd.getConverterManager().registerPropertyEditor(type, pe);
   }

   static final ConverterManager converterManager = new ConverterManager();

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

   /**
    * ��ȡ�Ի����������õĴ���.
    */
   static StringAppender getPrimitiveSetCode(String wrapName, Class type, String converterBase,
         String resName, Map paramCache, StringAppender sa)
   {
      int vcIndex = converterManager.getConverterIndex(type);
      // ����������ȫ��ע��, ���ﲻ����-1
      codeRes.printRes(GET_FIRST_VALUE_RES, paramCache, 1, sa).appendln();
      String typeName = ClassGenerator.getClassName(type);
      paramCache.put("declareType", typeName);
      paramCache.put("converterType", "self.micromagic.util.converter."
            + wrapName + "Converter");
      paramCache.put("converterName", converterBase + ".getConverter(" + vcIndex + ")");
      paramCache.put("converterMethod",
            "convertTo" + Character.toUpperCase(typeName.charAt(0)) + typeName.substring(1));
      codeRes.printRes(resName, paramCache, 1, sa).appendln();
      return sa;
   }

   /**
    * �������Ͷ�Ӧ���⸲��.
    */
   static Map primitiveWrapClass = new HashMap();

   /**
    * ���������������һ����������, ����������⸲�������.
    * ���򷵻�<code>null</code>.
    */
   public static String getPrimitiveWrapClassName(String className)
   {
      return (String) primitiveWrapClass.get(className);
   }

   /**
    * �����ж�һ�������Ƿ�Ϊbean��map.
    */
   static Map beanClassNameCheckMap = new SynHashMap();

   /**
    * ���bean������ģ��ļ���.
    */
   static Set beanClassNamePatternSet = new HashSet();

   /**
    * ע����Ϊbean����, �������֮����","��";"����.
    */
   public static void registerBean(String classNames)
   {
      String[] names = StringTool.separateString(
            Utility.resolveDynamicPropnames(classNames), ",;", true);
      if (names == null)
      {
         return;
      }
      for (int i = 0; i < names.length; i++)
      {
         if (names[i].indexOf('*') != -1)
         {
            // ����"*"��, ��ʾ��һ������ģ��
            beanClassNamePatternSet.add(names[i]);
         }
         else
         {
            beanClassNameCheckMap.put(names[i], Boolean.TRUE);
         }
      }
   }

   private static boolean CG_USE_DEFAULT_BEAN_CHECKER = true;
   /**
    * �ж��������������Ƿ���bean.
    */
   public static boolean checkBean(Class type)
   {
      String className = type.getName();
      Boolean beanType = (Boolean) beanClassNameCheckMap.get(className);
      if (beanType != null)
      {
         return beanType.booleanValue();
      }
      if (beanCheckers.size() > 0)
      {
         Iterator itr = beanCheckers.iterator();
         while (itr.hasNext())
         {
            BeanChecker bc = (BeanChecker) itr.next();
            if (bc.check(type) == BeanChecker.CHECK_RESULT_YES)
            {
               beanClassNameCheckMap.put(className, Boolean.TRUE);
               return true;
            }
            else if (bc.check(type) == BeanChecker.CHECK_RESULT_NO)
            {
               return false;
            }
         }
      }
      if (CG_USE_DEFAULT_BEAN_CHECKER)
      {
         if (defaultBeanChecker.check(type) == BeanChecker.CHECK_RESULT_YES)
         {
            beanClassNameCheckMap.put(className, Boolean.TRUE);
            return true;
         }
      }
      return false;
   }

   /**
    * һ�����ڼ������������Ƿ���һ��bean�ļ�����б�.
    */
   private static List beanCheckers = new LinkedList();

   private static BeanChecker defaultBeanChecker = new DefaultBeanChecker();

   /**
    * ע��һ��bean�ļ����.
    */
   public static synchronized void registerBeanChecker(BeanChecker bc)
   {
      if (!beanCheckers.contains(bc))
      {
         beanCheckers.add(bc);
      }
   }

   /**
    * ȥ��һ��bean�ļ����.
    */
   public static synchronized void removeBeanChecker(BeanChecker bc)
   {
      beanCheckers.remove(bc);
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

   /**
    * �������Դ.
    */
   static ResManager codeRes = new ResManager();

   /**
    * ��ʼ��������Դ���������Ͷ�Ӧ��ת����.
    */
   static
   {
      try
      {
         codeRes.load(BeanTool.class.getResourceAsStream("BeanTool.res"));
         Utility.addFieldPropertyManager(CG_USE_DBC_PROPERTY, BeanTool.class,
               "CG_USE_DEFAULT_BEAN_CHECKER", "true");
      }
      catch (Exception ex)
      {
         CG.log.error("Error in get code res.", ex);
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

      primitiveWrapClass.put("boolean", "Boolean");
      primitiveWrapClass.put("char", "Character");
      primitiveWrapClass.put("byte", "Byte");
      primitiveWrapClass.put("short", "Short");
      primitiveWrapClass.put("int", "Integer");
      primitiveWrapClass.put("long", "Long");
      primitiveWrapClass.put("float", "Float");
      primitiveWrapClass.put("double", "Double");
   }

   /**
    * ���bean����ı�����.
    */
   public static final String BEAN_NAME = "beanObj";

   /**
    * ��ȡ�ַ��������һ��Ԫ�صĴ�����Դ����
    */
   static final String GET_FIRST_VALUE_RES = "getFirstValue";

   /**
    * ��Ŷ�ȡ����ʱ����ı�����.
    */
   static final String TMP_OBJ_NAME = "tmpObj";

   /**
    * ����������ı�����.
    */
   static final String CELL_DESCRIPTOR_NAME = "cd";

   /**
    * ��ǰ��BeanMap����ı�����.
    */
   static final String BEAN_MAP_NAME = "beanMap";

   /**
    * ������õ����Ը����ı�����.
    */
   static final String SETTED_COUNT_NAME = "settedCount";

   /**
    * �����Ҫ��ȡ������ǰ׺�ı�����.
    */
   static final String PREFIX_NAME = "prefix";

   /**
    * �����Ҫ��ȡ������(��ƴ����ǰ׺�������)�ı�����.
    */
   static final String TMP_STR_NAME = "tmpStr";

   /**
    * �����Collectionʹ�õ�����ֵ�б�.
    */
   static final String INDEXS_NAME = "indexs";

   /**
    * ��������������.
    */
   static final String DEF_ARRAY_NAME = "tmpArr";

   /**
    * ���������������.
    */
   static final String PROCESSER_ARRAY_NAME = "processerArr";

}
