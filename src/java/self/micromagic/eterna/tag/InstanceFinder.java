
package self.micromagic.eterna.tag;

import self.micromagic.eterna.digester.FactoryManager;

/**
 * ����ʵ���Ĳ�ѯ��.
 */
public interface InstanceFinder
{
   /**
    * ���ݸ��������Ʋ���һ��������ʵ��.
    *
    * @param name   ��ͨ�������Ʋ��ҹ���ʵ��
    * @return  �鵽�Ĺ���ʵ��, ��<code>null</code>û�в鵽
    */
   FactoryManager.Instance findInstance(String name);

}
