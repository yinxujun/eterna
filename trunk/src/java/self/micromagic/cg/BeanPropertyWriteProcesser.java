
package self.micromagic.cg;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import self.micromagic.eterna.share.Tool;
import self.micromagic.util.StringAppender;
import self.micromagic.util.StringTool;

/**
 * 生成对bean属性设置的属性单元处理者.
 */
class BeanPropertyWriteProcesser
      implements UnitProcesser
{
   protected Map paramCache = new HashMap();
   protected String beanMapName;

   public BeanPropertyWriteProcesser(String valueName, String beanMapName, String originName,
         String oldValueName)
   {
      this.paramCache.put("beanName", BeanTool.BEAN_NAME);
      this.paramCache.put("originObjName", originName);
      this.paramCache.put("tmpObjName", valueName);
      this.paramCache.put("settedCountName", BeanTool.SETTED_COUNT_NAME);
      this.paramCache.put("prefixName", BeanTool.PREFIX_NAME);
      this.paramCache.put("oldValueName", oldValueName);
      this.beanMapName = beanMapName;
   }

   public String getFieldCode(Field f, Class type, String wrapName, int processerType, ClassGenerator cg)
   {
      this.paramCache.put("pName", f.getName());
      this.paramCache.put("fieldName", f.getName());
      String[] resNames = new String[] {
         "beanMap.primitiveFieldSet", "convertTypeFieldSet",
         "beanMap.beanTypeFieldSet", "otherTypeFieldSet"
      };
      return this.getProcesserCode(type, f.getName(), wrapName, resNames);
   }

   public String getMethodCode(BeanMethodInfo m, Class type, String wrapName, int processerType,
         ClassGenerator cg)
   {
      if (m.method == null)
      {
         return null;
      }
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
         int vcIndex = BeanTool.converterManager.getConverterIndex(type);
         if (vcIndex != -1)
         {
            BeanTool.codeRes.printRes(BeanTool.GET_FIRST_VALUE_RES, this.paramCache, 1, sa).appendln();
            this.paramCache.put("converterName", this.beanMapName + ".getConverter(" + vcIndex + ")");
            this.paramCache.put("className", ClassGenerator.getClassName(type));
            BeanTool.codeRes.printRes(resNames[1], this.paramCache, 1, sa).appendln();
         }
         else if (Tool.isBean(type))
         {
            this.paramCache.put("className", ClassGenerator.getClassName(type));
            BeanTool.codeRes.printRes(resNames[2], this.paramCache, 1, sa).appendln();
         }
         else
         {
            this.paramCache.put("className", ClassGenerator.getClassName(type));
            BeanTool.codeRes.printRes(resNames[3], this.paramCache, 1, sa).appendln();
         }
      }
      return sa.toString();
   }

}
