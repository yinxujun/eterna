
package self.micromagic.eterna.model.impl;

import java.util.Map;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.model.AppData;

/**
 * ���ݴ�����.
 */
public class DataHandler
{
   /**
    * ��ȡ���ݵ�����.
    */
   private String config;


   /**
    * ���ĸ�map�ж�ȡ/��������.
    */
   private int mapIndex = -1;

   /**
    * ��ȡ/����map�����ݵ�����.
    */
   private String mapDataName = null;

   /**
    * ���ĸ�cache�ж�ȡ/��������.
    */
   private int cacheIndex = -1;

   /**
    * �Ƿ�Ӷ�ջ�ж�ȡ����.
    */
   private boolean fromStack = false;

   /**
    * ��peek��ʽ��ȡ��ջ�е�ֵ.
    */
   private int peekIndex = -1;

   /**
    * ��ȡ�ĳ������ݵ�ֵ.
    */
   private String constValue = null;

   private boolean needMapDataName = true;
   private boolean readOnly = true;
   private String caption = "config";

   /**
    * @param caption           ��ʾ������Ϣ��ʹ�õı���
    * @param needMapDataName   ����ȡmap����ʱ, �Ƿ���Ҫ�������ݵ�����
    *                          ��readOnly��ֵΪfalseʱ, ��ֵ�ᱻǿ����Ϊtrue
    * @param readOnly          �Ƿ�Ϊֻ����ʽ
    */
   public DataHandler(String caption, boolean needMapDataName, boolean readOnly)
   {
      this.caption = caption;
      this.readOnly = readOnly;
      this.needMapDataName = !readOnly || needMapDataName;
   }

   /**
    * ������е�����
    */
   private void clearConfig()
   {
      this.config = null;
      this.mapIndex = -1;
      this.mapDataName = null;
      this.cacheIndex = -1;
      this.fromStack = false;
      this.peekIndex = -1;
   }

   /**
    * ��ȡ��������.
    */
   public String getConfig()
   {
      return this.config;
   }

   /**
    * ���ô�������.
    */
   public void setConfig(String config)
         throws ConfigurationException
   {
      this.clearConfig();
      this.config = config;
      int index = config.indexOf(':');
      String mainName = config;
      String subName = null;
      if (index != -1)
      {
         subName = config.substring(index + 1);
         mainName = config.substring(0, index);
      }

      for (int i = 0; i < AppData.MAP_NAMES.length; i++)
      {
         if (AppData.MAP_NAMES[i].equals(mainName))
         {
            this.mapIndex = i;
            break;
         }
      }
      if (this.mapIndex == -1)
      {
         for (int i = 0; i < AppData.MAP_SHORT_NAMES.length; i++)
         {
            if (AppData.MAP_SHORT_NAMES[i].equals(mainName))
            {
               this.mapIndex = i;
               break;
            }
         }
      }
      if (this.mapIndex != -1)
      {
         if (subName != null)
         {
            this.mapDataName = subName;
            return;
         }
         if (!this.needMapDataName)
         {
            // ������δ���ò��Ҳ��Ǳ���ʱ, ���˳������׳��쳣.
            return;
         }
      }
      else if ("cache".equals(mainName))
      {
         this.cacheIndex = 0;
         if (subName != null)
         {
            try
            {
               this.cacheIndex = Integer.parseInt(subName);
               return;
            }
            catch (NumberFormatException ex) {}
         }
         else
         {
            return;
         }
      }
      if (this.readOnly)
      {
         if ("stack".equals(mainName))
         {
            this.fromStack = true;
            if ("pop".equals(subName) || subName == null)
            {
               return;
            }
            if (subName != null && subName.startsWith("peek"))
            {
               this.peekIndex = 0;
               if (subName.length() > 4)
               {
                  if (subName.charAt(4) == '-')
                  {
                     try
                     {
                        this.peekIndex = Integer.parseInt(subName.substring(5));
                        return;
                     }
                     catch (NumberFormatException ex) {}
                  }
               }
               else
               {
                  return;
               }
            }
         }
         else if ("value".equals(mainName))
         {
            if (subName != null)
            {
               this.constValue = subName;
               return;
            }
         }
      }

      throw new ConfigurationException("Error " + this.caption + " [" + config + "].");
   }

   /**
    * ���ݴ������ö�ȡ����.
    *
    * @param data       AppData����, �ɴ��л�ȡ����
    * @param remove     ��ȡ���ݺ�, �Ƿ�Դͷ�������Ƴ�
    */
   public Object getData(AppData data, boolean remove)
         throws ConfigurationException
   {
      Object value = null;
      if (this.constValue != null)
      {
         value = this.constValue;
      }
      else if (this.mapIndex != -1)
      {
         Map tmpMap = data.maps[this.mapIndex];
         if (this.mapDataName != null)
         {
            value = tmpMap.get(this.mapDataName);
            if (remove)
            {
               tmpMap.remove(this.mapDataName);
            }
         }
         else
         {
            value = tmpMap;
         }
      }
      else if (this.cacheIndex != -1)
      {
         value = data.caches[this.cacheIndex];
         if (remove)
         {
            data.caches[this.cacheIndex] = null;
         }
      }
      else if (this.fromStack)
      {
         if (this.peekIndex != -1)
         {
            value = data.peek(this.peekIndex);
         }
         else
         {
            value = data.pop();
         }
      }
      return value;
   }

   /**
    * ���ݴ���������������
    */
   public void setData(AppData data, Object value)
         throws ConfigurationException
   {
      if (this.readOnly)
      {
         throw new ConfigurationException("The [" + this.caption + "] is read only, can't be setted.");
      }
      if (this.mapIndex != -1)
      {
         Map tmpMap = data.maps[this.mapIndex];
         if (value == null)
         {
            tmpMap.remove(this.mapDataName);
         }
         else
         {
            tmpMap.put(this.mapDataName, value);
         }
      }
      else if (this.cacheIndex != -1)
      {
         data.caches[this.cacheIndex] = value;
      }
   }

}
