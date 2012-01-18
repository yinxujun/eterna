
package self.micromagic.grammer;

/**
 * 字符检查器接口.
 */
public interface Checker
{
   /**
    * 对一个字符进行检查.
    *
    * @param pd  解析器数据.
    * @return    如果检查结果为合法, 则返回true..
    * @throws GrammerException     检查中发生语法错误.
    */
   boolean verify(ParserData pd) throws GrammerException;

}
