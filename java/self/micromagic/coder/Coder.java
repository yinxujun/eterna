
package self.micromagic.coder;

public interface Coder
{
   /**
    * 长度为0的字节数组, 在编码解码时需要的话可以使用.
    */
   public final static byte[] EMPTY_BYTE_ARRAY = new byte[0];

   /**
    * 以自身为副本, 生成一个新的<code>Coder</code>. <p>
    * 该副本的状态为原<code>Coder</code>初始化后的状态.
    */
   public Coder createNew();

   /**
    * 用于清除多次编码或解码时, 产生的中间数据, 使其恢复到初始
    * 状态.
    */
   public void clear();

   /**
    * 对一组字节进行编码. <p>
    *
    * @param buf   需要编码的字节
    * @return      编码后的字节
    */
   public byte[] encode(byte[] buf);

   /**
    * 对一组字节进行解码. <p>
    *
    * @param buf   需要解码的字节
    * @return      解码后的字节
    */
   public byte[] decode(byte[] buf);

   /**
    * 对一组字节进行编码. <p>
    * 可以将字节分为多次进行编码, 前几次编码时将参数<code>over</code>
    * 设为<code>false</code>，最后一次时将参数<code>over</code>设为
    * <code>true</code>.
    *
    * @param buf   需要编码的字节
    * @param over  是否是最后一批字节
    * @return      编码后的字节
    */
   public byte[] encode(byte[] buf, boolean over);

   /**
    * 对一组字节进行解码. <p>
    * 可以将字节分为多次进行解码, 前几次解码时将参数<code>over</code>
    * 设为<code>false</code>，最后一次时将参数<code>over</code>设为
    * <code>true</code>.
    *
    * @param buf   需要解码的字节
    * @param over  是否是最后一批字节
    * @return      解码后的字节
    */
   public byte[] decode(byte[] buf, boolean over);

}
