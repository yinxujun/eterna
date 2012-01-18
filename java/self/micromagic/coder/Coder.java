
package self.micromagic.coder;

public interface Coder
{
   /**
    * ����Ϊ0���ֽ�����, �ڱ������ʱ��Ҫ�Ļ�����ʹ��.
    */
   public final static byte[] EMPTY_BYTE_ARRAY = new byte[0];

   /**
    * ������Ϊ����, ����һ���µ�<code>Coder</code>. <p>
    * �ø�����״̬Ϊԭ<code>Coder</code>��ʼ�����״̬.
    */
   public Coder createNew();

   /**
    * ���������α�������ʱ, �������м�����, ʹ��ָ�����ʼ
    * ״̬.
    */
   public void clear();

   /**
    * ��һ���ֽڽ��б���. <p>
    *
    * @param buf   ��Ҫ������ֽ�
    * @return      �������ֽ�
    */
   public byte[] encode(byte[] buf);

   /**
    * ��һ���ֽڽ��н���. <p>
    *
    * @param buf   ��Ҫ������ֽ�
    * @return      �������ֽ�
    */
   public byte[] decode(byte[] buf);

   /**
    * ��һ���ֽڽ��б���. <p>
    * ���Խ��ֽڷ�Ϊ��ν��б���, ǰ���α���ʱ������<code>over</code>
    * ��Ϊ<code>false</code>�����һ��ʱ������<code>over</code>��Ϊ
    * <code>true</code>.
    *
    * @param buf   ��Ҫ������ֽ�
    * @param over  �Ƿ������һ���ֽ�
    * @return      �������ֽ�
    */
   public byte[] encode(byte[] buf, boolean over);

   /**
    * ��һ���ֽڽ��н���. <p>
    * ���Խ��ֽڷ�Ϊ��ν��н���, ǰ���ν���ʱ������<code>over</code>
    * ��Ϊ<code>false</code>�����һ��ʱ������<code>over</code>��Ϊ
    * <code>true</code>.
    *
    * @param buf   ��Ҫ������ֽ�
    * @param over  �Ƿ������һ���ֽ�
    * @return      �������ֽ�
    */
   public byte[] decode(byte[] buf, boolean over);

}
