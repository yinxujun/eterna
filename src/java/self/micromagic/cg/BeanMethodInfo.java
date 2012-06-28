
package self.micromagic.cg;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collection;
import java.beans.Introspector;

import org.apache.commons.collections.ReferenceMap;

/**
 * ����bean��һ����������Ϣ��.
 */
public class BeanMethodInfo
{
   /**
    * ������Ӧ���Ե�����.
    */
   public final String name;

   /**
    * ���Ե�����.
    */
   public final Class type;

   /**
    * �����Բ����ķ���.
    */
   public final Method method;

   /**
    * �Ƿ�Ϊ��ȡ�ķ���.
    * <code>true</code>Ϊ��ȡ, <code>false</code>Ϊ����.
    */
   public final boolean doGet;

   /**
    * ��������ֵ������.
    */
   public final Class indexedType;

   /**
    * ��������ֵ�����Բ����ķ���.
    */
   public final Method indexedMethod;

   /**
    * һ��Ĺ��캯��.
    */
   private BeanMethodInfo(String name, Method method, Class type, boolean doGet,
         Method indexedMethod, Class indexedType)
   {
      this.name = name;
      this.method = method;
      this.type = type;
      this.doGet = doGet;
      this.indexedMethod = indexedMethod;
      this.indexedType = indexedType;
   }

   /**
    * ������<code>BeanMethodInfo</code>�ϲ��Ĺ��캯��.
    */
   private BeanMethodInfo(BeanMethodInfo info1, BeanMethodInfo info2)
   {

      this.name = info1.name;
      this.type = info1.type == null ? info2.type : info1.type;
      this.doGet = info1.doGet;
      if (this.type == boolean.class && this.doGet)
      {
         if (info1.method == null)
         {
            this.method = info2.method;
         }
         else if (info2.method == null)
         {
            this.method = info1.method;
         }
         // �����������������, ��ȡis��ͷ�ķ���
         else if (info1.method.getName().startsWith(IS_PREFIX))
         {
            this.method = info1.method;
         }
         else
         {
            this.method = info2.method;
         }
      }
      else
      {
         this.method = info1.method == null ? info2.method : info1.method;
      }
      // ��������ֵ�ķ���������is��ͷ��
      this.indexedType = info1.indexedType == null ? info2.indexedType : info1.indexedType;
      this.indexedMethod = info1.indexedMethod == null ? info2.indexedMethod : info1.indexedMethod;
   }

   /**
    * bean��صķ����б�Ļ���.
    */
   private static ReferenceMap beanMethodsCache = new ReferenceMap(ReferenceMap.WEAK, ReferenceMap.SOFT);

   /**
    * ��ȡ�����bean��صķ���.
    *
    * @param beanClass   bean��
    * @return      bean��صķ����б�
    */
   public static BeanMethodInfo[] getBeanMethods(Class beanClass)
   {
      BeanMethodInfo[] result = (BeanMethodInfo[]) beanMethodsCache.get(beanClass);
      if (result != null)
      {
         return result;
      }

      Method[] methodList = beanClass.getMethods();
      Map tmpMethodsCache = new HashMap();
      for (int i = 0; i < methodList.length; i++)
      {
         Method method = methodList[i];
         if (Modifier.isStatic(method.getModifiers()))
         {
            // ���Ծ�̬����
            continue;
         }

         String name = method.getName();
         Class argTypes[] = method.getParameterTypes();
         Class resultType = method.getReturnType();
         int argCount = argTypes.length;
         if (argCount == 0)
         {
            if (name.startsWith(GET_PREFIX) && !name.equals(GET_CLASS))
            {
               // һ���get����
               addBeanMethod(tmpMethodsCache, Introspector.decapitalize(name.substring(3)),
                     method, resultType, true, false);
            }
            else if (resultType == boolean.class && name.startsWith(IS_PREFIX))
            {
               // boolean���͵�get����
               addBeanMethod(tmpMethodsCache, Introspector.decapitalize(name.substring(2)),
                     method, resultType, true, false);
            }
         }
         else if (argCount == 1)
         {
            if (argTypes[0] == int.class && name.startsWith(GET_PREFIX))
            {
               // ������ֵ��get����
               addBeanMethod(tmpMethodsCache, Introspector.decapitalize(name.substring(3)),
                     method, resultType, true, true);
            }
            else if (resultType == void.class && name.startsWith(SET_PREFIX))
            {
               // һ���set����
               addBeanMethod(tmpMethodsCache, Introspector.decapitalize(name.substring(3)),
                     method, argTypes[0], false, false);
            }
         }
         else if (argCount == 2)
         {
            if (argTypes[0] == int.class && name.startsWith(SET_PREFIX))
            {
               // ������ֵ��set����
               addBeanMethod(tmpMethodsCache, Introspector.decapitalize(name.substring(3)),
                     method, argTypes[1], false, true);
            }
         }
      }

      result = arrangeMethods(tmpMethodsCache);
      beanMethodsCache.put(beanClass, result);
      return result;
   }

   /**
    * �����ȡ��bean����.
    */
   private static BeanMethodInfo[] arrangeMethods(Map methodsCache)
   {
      List result = new ArrayList();
      List list;
      Iterator itr = methodsCache.values().iterator();
      while (itr.hasNext())
      {
         list = (List) itr.next();

         // ÿ��bean�������������5������, is get set getI setI, ����
         // ������5�����������Ͷ���һ��, ûһ�鷽���ϲ��������������.
         // is/get �� set
         BeanMethodInfo[][] infos = new BeanMethodInfo[5][2];

         // �����еķ��������ͷ���, ���������������
         for (int i = 0; i < list.size(); i++)
         {
            BeanMethodInfo info = (BeanMethodInfo) list.get(i);
            for (int x = 0; x < infos.length; x++)
            {
               if (infos[x][0] != null)
               {
                  BeanMethodInfo tmp = infos[x][0];
                  if (checkType(tmp, info))
                  {
                     if (tmp.doGet == info.doGet)
                     {
                        infos[x][0] = new BeanMethodInfo(tmp, info);
                     }
                     else if (infos[x][1] == null)
                     {
                        infos[x][1] = info;
                     }
                     else
                     {
                        infos[x][1] = new BeanMethodInfo(infos[x][1], info);;
                     }
                     break;
                  }
               }
               else
               {
                  infos[x][0] = info;
                  break;
               }
            }
         }

         int rWeight = 0;
         BeanMethodInfo[] r = null;

         // ����ÿһ�����������Ȩ��, ȡ��ߵ�
         // Ȩ�ع���Ϊ:
         // ��get                   +1
         // ��set��get              +1
         // �л�������              +1
         for (int i = 0; i < infos.length; i++)
         {
            if (infos[i][0] == null)
            {
               // û�з�����Ϣ�����˳�
               break;
            }
            int weitht = 0;
            if (infos[i][1] != null)
            {
               // ��set��get
               weitht++;
               // ��get  (�������������ڱض���get)
               weitht++;
               if (infos[i][1].method != null)
               {
                  // �л�������
                  weitht++;
               }
            }
            else if (infos[i][0].doGet)
            {
               // ��get
               weitht++;
            }
            if (infos[i][0].method != null)
            {
               // �л�������
               weitht++;
            }

            if (weitht > rWeight)
            {
               // Ȩ�ر�ԭ���ĸ����滻ԭ����
               r = infos[i];
               rWeight = weitht;
            }
         }

         if (r != null)
         {
            for (int i = 0; i < r.length; i++)
            {
               if (r[i] != null)
               {
                  result.add(r[i]);
               }
            }
         }
      }
      return (BeanMethodInfo[]) result.toArray(new BeanMethodInfo[result.size()]);
   }

   /**
    * �������������Ϣ�������Ƿ�һ��.
    */
   private static boolean checkType(BeanMethodInfo info1, BeanMethodInfo info2)
   {
      if (info1.type != null && info2.type != null)
      {
         // ����������Ͷ���Ϊnull, �������ͬ
         return info1.type == info2.type;
      }
      if (info1.indexedType != null && info2.indexedType != null)
      {
         // ��������������Ͷ���Ϊnull, �������ͬ
         return info1.indexedType == info2.indexedType;
      }
      return checkIndexedType(info1.type, info2.indexedType)
            || checkIndexedType(info2.type, info1.indexedType);
   }

   /**
    * �������������ͺͻ��������Ƿ���һ�µ�.
    */
   private static boolean checkIndexedType(Class type, Class indexedType)
   {
      if (type == null || indexedType == null)
      {
         // �κ�һ��Ϊnull, �򲻿���һ��
         return false;
      }
      if (type == indexedType)
      {
         // �������������ͬ��һ��
         return true;
      }
      if (type.isArray() && type.getComponentType() == indexedType)
      {
         // �����������Ϊ����, ������Ԫ�����ͺʹ�������������ͬ��һ��
         return true;
      }
      if (Collection.class.isAssignableFrom(type))
      {
         // �����������ʵ����Collection, ����Ϊ��һ�µ�
         return true;
      }
      // ʣ�������Ϊ��һ��
      return false;
   }

   /**
    * ����ʱ�������������һ������.
    */
   private static void addBeanMethod(Map methodsCache, String name, Method method, Class type,
         boolean doGet, boolean withIndex)
   {
      BeanMethodInfo bmi;
      if (withIndex)
      {
         bmi = new BeanMethodInfo(name, null, null, doGet, method, type);
      }
      else
      {
         bmi = new BeanMethodInfo(name, method, type, doGet, null, null);
      }
      List l = (List) methodsCache.get(name);
      if (l == null)
      {
         l = new ArrayList();
         methodsCache.put(name, l);
      }
      l.add(bmi);
   }

   private static final String GET_CLASS = "getClass";
   private static final String GET_PREFIX = "get";
   private static final String SET_PREFIX = "set";
   private static final String IS_PREFIX = "is";

}
