
package self.micromagic.eterna.share;


/**
 * Eterna的初始化配置.
 * 假如你想在Eterna初始化完后, 初始化你的类, 请在你的类中加上
 * 下面这些方法, 并且必须定义为静态的, 并且要继承此接口.
 * 然后在配置文件中的self.micromagic.eterna.digester.initClasses
 * 属性中加入你的类名(包括类路径), 类之间用";"分割.
 * 如果你使用的是base class来初始化, 则只需base class实现此接口,
 * 而不需要在配置文件中添加定义.
 *
 * 需要定义的方法如下:
 * private static void afterEternaInitialize(FactoryManager.Instance factoryManager)
 *
 * @see self.micromagic.eterna.digester.FactoryManager.Instance#createClassFactoryManager(Class)
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
