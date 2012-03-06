
package self.micromagic.eterna.sql.preparer;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.Generator;
import self.micromagic.eterna.share.EternaFactory;

/**
 * 值准备器的创建者的构造者.
 * 用于生成一个值准备器的创建者, 而值准备器的创建者可产生一个值准备器.
 */
public interface ValuePreparerCreaterGenerator extends Generator
{
   /**
    * 初始化此构造者.
    */
   void initialize(EternaFactory factory) throws ConfigurationException;

   /**
    * 根据type类型生成相关的值准备器的创建者.
    *
    * @return   值准备器的创建者
    */
   ValuePreparerCreater createValuePreparerCreater(int pureType) throws ConfigurationException;

}
