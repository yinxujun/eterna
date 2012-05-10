
package self.micromagic.eterna.digester;

import org.xml.sax.Attributes;

/**
 * ���г�ʼ�������¼�ĳ�ʼ������.
 * ��Ҫ��¼��ǰ�������ɵ���ʲô����, �����ڷ����쳣ʱ����׼ȷ�ر���
 * �ĸ����������.
 */
public class ObjectLogRule extends MyRule
{
   private String attributeName;
   private String objType;

   /**
    * @param attributeName   ������ָ���������Ƶ�����
    * @param objType         ��������
    */
   public ObjectLogRule(String attributeName, String objType)
   {
      this.attributeName = attributeName;
      this.objType = objType;
   }

   public void myBegin(String namespace, String name, Attributes attributes)
         throws Exception
   {
      String theName = attributes.getValue(this.attributeName);
      if (theName == null)
      {
         theName = "null";
      }
      StringBuffer temp = new StringBuffer(
            this.objType.length() + theName.length() + 2);
      temp.append(this.objType).append("[").append(theName).append("]");
      ConfigurationException.objName = temp.toString();
   }

   /**
    * ���õ�ǰ���ڳ�ʼ��������.
    */
   public static void setConfigName(String name)
   {
      ConfigurationException.config = name;
   }

   /**
    * ���õ�ǰ���ڳ�ʼ���Ķ�������.
    */
   public static void setObjName(String name)
   {
      ConfigurationException.objName = name;
   }

   /**
    * ���õ�ǰ���ڳ�ʼ���Ķ�������ͼ�����.
    */
   public static void setObjName(String type, String name)
   {
      if (name == null)
      {
         setObjName(type);
      }
      StringBuffer temp = new StringBuffer(type.length() + name.length() + 2);
      temp.append(type).append("[").append(name).append("]");
      ConfigurationException.objName = temp.toString();
   }

}