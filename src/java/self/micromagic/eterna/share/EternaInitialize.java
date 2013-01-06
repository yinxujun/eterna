
package self.micromagic.eterna.share;

import self.micromagic.eterna.digester.FactoryManager;

/**
 * Eterna的初始化配置. <p>
 * 假如你想在Eterna初始化完后, 初始化你的类, 请实现此接口, 并在你的类中加上
 * 下面这些方法.
 * 然后在配置文件中的self.micromagic.eterna.digester.initClasses
 * 属性中加入你的类名(包括类路径), 类之间用";"分割.
 * 如果你使用的是base class来初始化, 则只需base class实现此接口, 而不需要在
 * 配置文件中添加定义.
 *
 * 需要定义的方法如下:
 * private static void afterEternaInitialize(FactoryManager.Instance factoryManager)
 *
 * 如果初始化完成的通知需要发送到类的实例, 则不要将此方法定义成静态的, 如:
 * private void afterEternaInitialize(FactoryManager.Instance factoryManager)
 *
 * @see FactoryManager.Instance#createClassFactoryManager(Class)
 * @see FactoryManager.Instance#addInitializedListener(Object)
 *
 *
 * 如果你使用的是base class来初始化, 并需要自动重载的功能.
 *
 * 需要定义的方法如下:
 * private static long autoReloadTime()
 * 返回值为检查重载的间隔毫秒数, 至少要大于200.
 */
public interface EternaInitialize
{

}