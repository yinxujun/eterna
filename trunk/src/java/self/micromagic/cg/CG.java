
package self.micromagic.cg;

/**
 * 类的编译接口, 用于实现不同的编译方式.
 */
public interface CG
{
   Class createClass(ClassGenerator cg) throws Exception;

}
