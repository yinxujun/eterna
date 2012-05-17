
package self.micromagic.eterna.sql.preparer;

import java.io.InputStream;
import java.io.Reader;

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
    * 获取生成此构造者的工厂.
    */
   EternaFactory getFactory() throws ConfigurationException;

   /**
    * 是否要将空字符串变为null.
    */
   boolean isEmptyStringToNull();

   /**
    * 根据type类型生成相关的值准备器的创建者.
    *
    * @return   值准备器的创建者
    */
   ValuePreparerCreater createValuePreparerCreater(int pureType) throws ConfigurationException;

   /**
    * 生成一个null类型的值准备器.
    *
    * @param index   对应参数的相对索引值
    * @param type    在<code>java.sql.Types</code>中定义的SQL型
    */
   ValuePreparer createNullPreparer(int index, int type) throws ConfigurationException;

   /**
    * 生成一个boolean类型的值准备器.
    *
    * @param index   对应参数的相对索引值
    * @param v       值
    */
   ValuePreparer createBooleanPreparer(int index, boolean v) throws ConfigurationException;

   /**
    * 生成一个byte类型的值准备器.
    *
    * @param index   对应参数的相对索引值
    * @param v       值
    */
   ValuePreparer createBytePreparer(int index, byte v) throws ConfigurationException;

   /**
    * 生成一个byte数组类型的值准备器.
    *
    * @param index   对应参数的相对索引值
    * @param v       值
    */
   ValuePreparer createBytesPreparer(int index, byte[] v) throws ConfigurationException;

   /**
    * 生成一个short类型的值准备器.
    *
    * @param index   对应参数的相对索引值
    * @param v       值
    */
   ValuePreparer createShortPreparer(int index, short v) throws ConfigurationException;

   /**
    * 生成一个int类型的值准备器.
    *
    * @param index   对应参数的相对索引值
    * @param v       值
    */
   ValuePreparer createIntPreparer(int index, int v) throws ConfigurationException;

   /**
    * 生成一个long类型的值准备器.
    *
    * @param index   对应参数的相对索引值
    * @param v       值
    */
   ValuePreparer createLongPreparer(int index, long v) throws ConfigurationException;

   /**
    * 生成一个float类型的值准备器.
    *
    * @param index   对应参数的相对索引值
    * @param v       值
    */
   ValuePreparer createFloatPreparer(int index, float v) throws ConfigurationException;

   /**
    * 生成一个double类型的值准备器.
    *
    * @param index   对应参数的相对索引值
    * @param v       值
    */
   ValuePreparer createDoublePreparer(int index, double v) throws ConfigurationException;

   /**
    * 生成一个String类型的值准备器.
    *
    * @param index   对应参数的相对索引值
    * @param v       值
    */
   ValuePreparer createStringPreparer(int index, String v) throws ConfigurationException;

   /**
    * 生成一个Stream类型的值准备器.
    *
    * @param index   对应参数的相对索引值
    * @param v       值
    */
   ValuePreparer createStreamPreparer(int index, InputStream v, int length) throws ConfigurationException;

   /**
    * 生成一个Reader类型的值准备器.
    *
    * @param index   对应参数的相对索引值
    * @param v       值
    */
   ValuePreparer createReaderPreparer(int index, Reader v, int length) throws ConfigurationException;

   /**
    * 生成一个Date类型的值准备器.
    *
    * @param index   对应参数的相对索引值
    * @param v       值
    */
   ValuePreparer createDatePreparer(int index, java.sql.Date v) throws ConfigurationException;

   /**
    * 生成一个Time类型的值准备器.
    *
    * @param index   对应参数的相对索引值
    * @param v       值
    */
   ValuePreparer createTimePreparer(int index, java.sql.Time v) throws ConfigurationException;

   /**
    * 生成一个Timestamp类型的值准备器.
    *
    * @param index   对应参数的相对索引值
    * @param v       值
    */
   ValuePreparer createTimestampPreparer(int index, java.sql.Timestamp v) throws ConfigurationException;

   /**
    * 生成一个Object类型的值准备器.
    *
    * @param index   对应参数的相对索引值
    * @param v       值
    */
   ValuePreparer createObjectPreparer(int index, Object v) throws ConfigurationException;

}
