
package self.micromagic.eterna.share;

import self.micromagic.eterna.digester.FactoryManager;

/**
 * Eterna�ĳ�ʼ������. <p>
 * ����������Eterna��ʼ�����, ��ʼ�������, ��ʵ�ִ˽ӿ�, ����������м���
 * ������Щ����.
 * Ȼ���������ļ��е�self.micromagic.eterna.digester.initClasses
 * �����м����������(������·��), ��֮����";"�ָ�.
 * �����ʹ�õ���base class����ʼ��, ��ֻ��base classʵ�ִ˽ӿ�, ������Ҫ��
 * �����ļ�����Ӷ���.
 *
 * ��Ҫ����ķ�������:
 * private static void afterEternaInitialize(FactoryManager.Instance factoryManager)
 *
 * �����ʼ����ɵ�֪ͨ��Ҫ���͵����ʵ��, ��Ҫ���˷�������ɾ�̬��, ��:
 * private void afterEternaInitialize(FactoryManager.Instance factoryManager)
 *
 * @see FactoryManager.Instance#createClassFactoryManager(Class)
 * @see FactoryManager.Instance#addInitializedListener(Object)
 *
 *
 * �����ʹ�õ���base class����ʼ��, ����Ҫ�Զ����صĹ���.
 *
 * ��Ҫ����ķ�������:
 * private static long autoReloadTime()
 * ����ֵΪ������صļ��������, ����Ҫ����200.
 */
public interface EternaInitialize
{

}
