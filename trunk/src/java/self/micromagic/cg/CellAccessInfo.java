
package self.micromagic.cg;

/**
 * ���Եķ�����Ϣ.
 */
public class CellAccessInfo
{
   /**
    * �����Ե�Ԫ���ڵ�BeanMap.
    */
   public final BeanMap beanMap;

   /**
    * ���Ե�Ԫ��������.
    */
   public final CellDescriptor cellDescriptor;

   /**
    * �����鵥Ԫ���ʵ�����ֵ�б�.
    */
   public final int[] indexs;

   /**
    * ����һ�����Եķ�����Ϣ��.
    */
   public CellAccessInfo(BeanMap beanMap, CellDescriptor cellDescriptor, int[] indexs)
   {
      this.beanMap = beanMap;
      this.cellDescriptor = cellDescriptor;
      this.indexs = indexs;
   }

   /**
    * ��ȡ��Ӧ������Ϣ��ֵ.
    * ����÷�����Ϣ�����Ե�Ԫû�ж�����, ���ܻ�ȡ.
    */
   public Object getValue()
   {
      if (this.cellDescriptor.readProcesser != null)
      {
         try
         {
            Object beanObj = this.beanMap.getBean();
            String prefix = this.beanMap.getPrefix();
            if (beanObj != null)
            {
               return this.cellDescriptor.readProcesser.getBeanValue(
                     this.cellDescriptor, this.indexs, beanObj, prefix, this.beanMap);
            }
         }
         catch (Exception ex)
         {
            if (ClassGenerator.COMPILE_LOG_TYPE > 0)
            {
               ClassGenerator.log.info("Read bean value error.", ex);
            }
         }
      }
      return null;
   }

   /**
    * ���ö�Ӧ������Ϣ��ֵ.
    * ����÷�����Ϣ�����Ե�Ԫû��д����, ��������.
    *
    * @param value   Ҫ���õ�ֵ
    * @return   �÷�����Ϣ��ԭ����ֵ, ����÷�����Ϣ�����Ե�Ԫû�ж�����,
    *           ����Զ����<code>null</code>.
    */
   public Object setValue(Object value)
   {
      if (this.cellDescriptor.writeProcesser != null)
      {
         try
         {
            Object oldValue = null;
            Object beanObj = this.beanMap.getBean();
            String prefix = this.beanMap.getPrefix();
            if (beanObj != null)
            {
               if (this.cellDescriptor.readProcesser != null)
               {
                  oldValue = this.cellDescriptor.readProcesser.getBeanValue(
                        this.cellDescriptor, this.indexs, beanObj, prefix, this.beanMap);
               }
            }
            else
            {
               beanObj = this.beanMap.createBean();
            }
            this.cellDescriptor.writeProcesser.setBeanValue(this.cellDescriptor, this.indexs,
                  beanObj, value, prefix, this.beanMap, null, oldValue);
            return oldValue;
         }
         catch (Exception ex)
         {
            if (ClassGenerator.COMPILE_LOG_TYPE > 0)
            {
               ClassGenerator.log.info("Write bean value error.", ex);
            }
         }
      }
      return null;
   }

}
