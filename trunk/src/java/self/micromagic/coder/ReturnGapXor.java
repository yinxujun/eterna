
package self.micromagic.coder;

public class ReturnGapXor extends AbstractCoder
      implements Coder
{
   private byte[] enKey;
   private int enGapCount;

   private Environment encodeEnv = new Environment();
   private Environment decodeEnv = new Environment();

   /**
    * ע: ��Ҫ���޸Ĳ���<code>key</code>�е�ֵ, �����Ӱ��
    * ����������ȷ��.
    */
   public ReturnGapXor(byte[] key, int gapCount)
   {
      this.enKey = key;
      this.enGapCount = gapCount;
   }

   public Coder createNew()
   {
      return new ReturnGapXor(this.enKey, this.enGapCount);
   }

   public void clear()
   {
      this.encodeEnv.clear();
      this.decodeEnv.clear();
   }

   public byte[] encode(byte[] buf, boolean over)
   {
      byte[] result = encode(buf, this.enKey, this.enGapCount, this.encodeEnv);
      if (over)
      {
         this.encodeEnv.clear();
      }
      return result;
   }

   public byte[] decode(byte[] buf, boolean over)
   {
      byte[] result = decode(buf, this.enKey, this.enGapCount, this.decodeEnv);
      if (over)
      {
         this.decodeEnv.clear();
      }
      return result;
   }

   public static byte[] encode(byte[] src, byte[] key, int gapCount)
   {
      return encode(src, key, gapCount, null);
   }

   private static byte[] encode(byte[] src, byte[] key, int gapCount, Environment env)
   {
      int keyCount = key.length;
      byte[] des = new byte[src.length]; //���ܵ�����
      byte preByte = env == null ? 0 : env.preByte;
      int count = src.length;
      int nowGap = env == null ? 0 : env.nowGap;
      int preCount = env == null ? 0 : env.preCount;
      for (int i = 0; i < count; i++)
      {
         if (nowGap == gapCount)
         {
            // ���1λ������
            nowGap = 0;
            des[i] = (byte) (src[i] ^ (preCount + i) ^ preByte);
         }
         else
         {
            des[i]= (byte) ((src[i] ^ key[(preCount + i) % keyCount] ^ preByte) & 0xff);
            preByte = des[i];
            nowGap++;
         }
      }
      if (env != null)
      {
         env.preByte = preByte;
         env.nowGap = nowGap;
         env.preCount += count;
      }
      return des;
   }

   public static byte[] decode(byte[] des, byte[] key, int gapCount)
   {
      return decode(des, key, gapCount, null);
   }

   private static byte[] decode(byte[] des, byte[] key, int gapCount, Environment env)
   {
      int keyCount = key.length;
      byte[] src = new byte[des.length]; //���ܵ�����
      byte preByte = env == null ? 0 : env.preByte;
      int count = src.length;
      int nowGap = env == null ? 0 : env.nowGap;
      int preCount = env == null ? 0 : env.preCount;
      for (int i = 0; i < count; i++)
      {
         if (nowGap == gapCount)
         {
            // ���1λ������
            nowGap = 0;
            src[i] = (byte) (des[i] ^ (preCount + i) ^ preByte);
         }
         else
         {
            src[i] = (byte) ((des[i] ^ key[(preCount + i) % keyCount] ^ preByte) & 0xff);
            preByte = des[i];
            nowGap++;
         }
      }
      if (env != null)
      {
         env.preByte = preByte;
         env.nowGap = nowGap;
         env.preCount += count;
      }
      return src;
   }

   private class Environment
   {
      public byte preByte = 0;
      public int nowGap = 0;
      public int preCount = 0;

      public void clear()
      {
         this.preByte = 0;
         this.nowGap = 0;
         this.preCount = 0;
      }

   }

}
