
package self.micromagic.eterna.digester;

import java.util.Map;
import java.util.HashMap;

import org.xml.sax.Attributes;

/**
 * ��鸸�����еĶ����Ƿ�������������ͬ�ĳ�ʼ������.
 * �����ͬ, �򲻴���˶���, ͨ������MyRule.dealRule������.
 */
public class SameCheckRule extends MyRule
{
   private static final Object FILL_OBJ = new Object();
   private static Map dealedObjMap;

   private String objName;
   private String attrName;
   private boolean ignoreObj = false;

   /**
    * @param objName      �������������
    * @param attrName     ������ָ���������Ƶ�����
    */
   public SameCheckRule(String objName, String attrName)
   {
      this.objName = objName;
      this.attrName = attrName;
   }

   public void begin(String namespace, String name, Attributes attributes)
         throws Exception
   {
      if (!dealRule)
      {
         return;
      }
      String objFlag = this.objName;
      if (this.attrName != null)
      {
         objFlag = this.objName + ":" + attributes.getValue(this.attrName);
      }
      boolean hasObj = dealedObjMap.put(objFlag, FILL_OBJ) != null;
      if (hasObj && FactoryManager.getSuperInitLevel() > 0)
      {
         dealRule = false;
         this.ignoreObj = true;
         if (FactoryManager.log.isDebugEnabled())
         {
            FactoryManager.log.debug(objFlag + " has bean overwrited.");
         }
      }
   }

   public void end(String namespace, String name)
         throws Exception
   {
      if (this.ignoreObj)
      {
         this.ignoreObj = false;
         dealRule = true;
      }
   }

   /**
    * ��ʼ���Ѵ������Ļ���.
    */
   static void initDealedObjMap()
   {
      dealedObjMap = new HashMap(512);
   }

   /**
    * ����Ѵ������Ļ���.
    */
   static void clearDealedObjMap()
   {
      dealedObjMap = null;
   }

}
