
package self.micromagic.coder;

import java.util.Arrays;

public class Gost extends AbstractCoder
      implements Coder
{
   private static final int GROUP_SIZE = 8;
   private static final int ENCODE_BYTES_SIZE = 8;
   private static final int DECODE_BYTES_SIZE = 9;

   private int[] enKey = new int[8];

   private byte[] encodeLeftByte = new byte[ENCODE_BYTES_SIZE];
   private int encodeLeftCount = 0;
   private byte[] decodeLeftByte = new byte[DECODE_BYTES_SIZE];
   private int decodeLeftCount = 0;

   public Gost(byte[] key)
   {
      if (key.length > 0)
      {
         byte[] temp = new byte[32];
         int count = 32;
         int copyCount = Math.min(key.length, count);
         System.arraycopy(key, 0, temp, 0, copyCount);
         int[] tempBuf = new int[2];
         for (int i = 0; i < 4; i++)
         {
            dataToBuf(temp, i * 8, tempBuf);
            this.enKey[i * 2] = tempBuf[0];
            this.enKey[i * 2 + 1] = tempBuf[1];
         }
      }
   }

   private Gost(Gost src)
   {
      this.enKey = src.enKey;
   }

   public Coder createNew()
   {
      return new Gost(this);
   }

   public void clear()
   {
      this.encodeLeftCount = 0;
      this.decodeLeftCount = 0;
   }

   public byte[] decode(byte[] des)
   {
      if (des.length % GROUP_SIZE != 1)
      {
         throw new IllegalArgumentException("The byte array must be a (length MOD 8 = 1).");
      }
      return this.decode(des, true);
   }

   public byte[] encode(byte[] buf, boolean over)
   {
      int aLen = buf.length + this.encodeLeftCount;
      int numFullGroups = aLen / GROUP_SIZE;
      int numBytesLeft = aLen - GROUP_SIZE * numFullGroups;
      // �����Ҫ�����Ļ���Ҫ����һ���һ���ֽ�, һ���ǽ����õ�, һ���ֽ��Ƕ�ʣ���ֽ�����
      int resultLen = over ?
            GROUP_SIZE * (numFullGroups + (numBytesLeft > 0 ? 1 : 0)) + 1 :
            GROUP_SIZE * numFullGroups;
      byte[] result = null;
      if (resultLen == 0)
      {
         if (buf.length > 0)
         {
            System.arraycopy(buf, 0, this.encodeLeftByte,
                  this.encodeLeftCount, buf.length);
            this.encodeLeftCount += buf.length;
         }
         result = EMPTY_BYTE_ARRAY;
      }
      else
      {
         result = new byte[resultLen];
         int[] tempBuf = new int[2];
         int srcOffset = 0;
         int desOffset = 0;

         // ����ǰ����������һЩ�ֽ�
         if (this.encodeLeftCount > 0 && numFullGroups >= 1)
         {
            srcOffset = ENCODE_BYTES_SIZE - this.encodeLeftCount;
            System.arraycopy(buf, 0, this.encodeLeftByte,
                  this.encodeLeftCount, srcOffset);
            encry_data(this.encodeLeftByte, result, 0, 0, this.enKey, tempBuf);
            numFullGroups--;
            desOffset += GROUP_SIZE;
            this.encodeLeftCount = 0;
         }

         for (int i = 0; i < numFullGroups; i++)
         {
            encry_data(buf, result, srcOffset, desOffset, this.enKey, tempBuf);
            srcOffset += GROUP_SIZE;
            desOffset += GROUP_SIZE;
         }
         if (numBytesLeft != 0)
         {
            System.arraycopy(buf, srcOffset, this.encodeLeftByte,
                  this.encodeLeftCount, numBytesLeft - this.encodeLeftCount);
            this.encodeLeftCount = numBytesLeft;
            if (over)
            {
               Arrays.fill(this.encodeLeftByte,
                     this.encodeLeftCount, ENCODE_BYTES_SIZE, (byte) 0);
               encry_data(this.encodeLeftByte, result, 0, desOffset,
                     this.enKey, tempBuf);
               desOffset += GROUP_SIZE;
               result[desOffset++] = (byte) this.encodeLeftCount;
               this.encodeLeftCount = 0;
            }
         }
         else
         {
            if (over)
            {
               result[desOffset++] = (byte) GROUP_SIZE;
            }
            this.encodeLeftCount = 0;
         }
      }
      if (over)
      {
         this.encodeLeftCount = 0;
      }
      return result;
   }

   public byte[] decode(byte[] buf, boolean over)
   {
      int aLen = buf.length + this.decodeLeftCount;
      byte[] result = null;
      if (aLen < DECODE_BYTES_SIZE)
      {
         if (buf.length != 0)
         {
            System.arraycopy(buf, 0, this.decodeLeftByte,
                  this.decodeLeftCount, buf.length);
            this.decodeLeftCount += buf.length;
         }
         result = EMPTY_BYTE_ARRAY;
      }
      else
      {
         int resultLen;
         int numFullGroups;
         int lastGroupSize = 0;
         int[] tempBuf = new int[2];
         byte[] tempBytes = null;
         if (over)
         {
            tempBytes = new byte[GROUP_SIZE];
            if (aLen % GROUP_SIZE != 1)
            {
               numFullGroups = aLen / GROUP_SIZE;
               resultLen = (numFullGroups) * GROUP_SIZE;
               lastGroupSize = GROUP_SIZE;
            }
            else
            {
               if (aLen > 8)
               {
                  numFullGroups = aLen / GROUP_SIZE - 1;
                  lastGroupSize = buf.length > 0 ? buf[buf.length - 1] :
                        this.decodeLeftByte[this.decodeLeftCount - 1];
                  resultLen = (numFullGroups) * GROUP_SIZE
                        + (aLen > 0 ? lastGroupSize : 0);
               }
               else
               {
                  numFullGroups = 0;
                  lastGroupSize = 0;
                  resultLen = 0;
               }
            }
         }
         else
         {
            numFullGroups = (aLen - 1) / GROUP_SIZE;
            resultLen = numFullGroups * GROUP_SIZE;
         }
         result = new byte[resultLen];
         int numBytesLeft = aLen - GROUP_SIZE * numFullGroups;
         int desOffset = 0;
         int srcOffset = 0;

         // ����ǰ����������һЩ�ֽ�
         if (this.decodeLeftCount > 0 && numFullGroups >= 1)
         {
            if (this.decodeLeftCount > GROUP_SIZE)
            {
               decry_data(this.decodeLeftByte, result, 0, 0, this.enKey, tempBuf);
               srcOffset += GROUP_SIZE;
               numFullGroups--;
               this.decodeLeftByte[0] = this.decodeLeftByte[GROUP_SIZE + 1];
               this.decodeLeftCount = 1;
            }
            if (numFullGroups >= 1)
            {
               desOffset = GROUP_SIZE - this.decodeLeftCount;
               System.arraycopy(buf, 0, this.decodeLeftByte,
                     this.decodeLeftCount, desOffset);
               decry_data(this.decodeLeftByte, result, 0, srcOffset, this.enKey, tempBuf);
               numFullGroups--;
               srcOffset += GROUP_SIZE;
               this.decodeLeftCount = 0;
            }
         }

         for (int i = 0; i < numFullGroups; i++)
         {
            decry_data(buf, result, desOffset, srcOffset, this.enKey, tempBuf);
            desOffset += GROUP_SIZE;
            srcOffset += GROUP_SIZE;
         }
         if (this.decodeLeftCount < numBytesLeft)
         {
            System.arraycopy(buf, desOffset, this.decodeLeftByte,
                  this.decodeLeftCount, numBytesLeft - this.decodeLeftCount);
            this.decodeLeftCount = numBytesLeft;
         }
         if (over && this.decodeLeftCount == DECODE_BYTES_SIZE)
         {
            decry_data(this.decodeLeftByte, tempBytes, 0, 0, this.enKey, tempBuf);
            System.arraycopy(tempBytes, 0, result, srcOffset, lastGroupSize);
            this.decodeLeftCount = 0;
         }
      }
      if (over)
      {
         this.decodeLeftCount = 0;
      }
      return result;
   }

   /**
    * 32�ּ��ܲ���. <p>
    * ���ĵ��ֽ����ͼ��ܺ���ֽ����ĸ�����Ϊ8.
    *
    * @param src         �����ֽ���
    * @param des         ���ܺ�������ֽ���
    * @param srcOffset   ���ν��ܵ������ֽ�����ʼλ��
    * @param desOffset   ���ν��ܵ������ֽ�����ʼλ��
    * @param key         ��Կ-����Ϊ8
    * @param lrBuf       ���ڴ洢�м�����Ļ���-����Ϊ2
    */
   private static void encry_data(byte[] src, byte[] des, int srcOffset,
         int desOffset, int[] key, int[] lrBuf)
   {
      dataToBuf(src, srcOffset, lrBuf);
      for (int i = 0; i < 32; i++)
      {
         lrBuf[1] ^= f_32_11(lrBuf[0] + key[WZ_SPKEY[i]]);
         gost_swap(lrBuf);   // ����ֵ����
      }
      gost_swap(lrBuf);   // ����ֵ����
      bufToData(lrBuf, des, desOffset);
   }

   /**
    * 32�ֽ��ܲ���. <p>
    * ���ĵ��ֽ����ͽ��ܺ���ֽ����ĸ�����Ϊ8.
    *
    * @param des         �����ֽ���
    * @param src         ���ܺ�������ֽ���
    * @param desOffset   ���ν��ܵ������ֽ�����ʼλ��
    * @param srcOffset   ���ν��ܵ������ֽ�����ʼλ��
    * @param key         ��Կ-����Ϊ8
    * @param lrBuf       ���ڴ洢�м�����Ļ���-����Ϊ2
    */
   private static void decry_data(byte[] des, byte[] src, int desOffset,
         int srcOffset, int[] key, int[] lrBuf)
   {
      dataToBuf(des, desOffset, lrBuf);
      for (int i = 0; i < 32; i++)
      {
         lrBuf[1] ^= f_32_11(lrBuf[0] + key[WZ_SPKEY[31 - i]]);
         gost_swap(lrBuf);   // ����ֵ����
      }
      gost_swap(lrBuf);   // ����ֵ����
      bufToData(lrBuf, src, srcOffset);
   }

   /**
    * s-���滻��ѭ������11λ����
    */
   private static int f_32_11(int x)
   {
      x = (WZ_SP[7][(x >>> 28) & 0xf] << 28) | (WZ_SP[6][(x >>> 24) & 0xf] << 24)
            | (WZ_SP[5][(x >>> 20) & 0xf] << 20) | (WZ_SP[4][(x >>> 16) & 0xf] << 16)
            | (WZ_SP[3][(x >>> 12) & 0xf] << 12) | (WZ_SP[2][(x >>> 8) & 0xf] << 8)
            | (WZ_SP[1][(x >>> 4) & 0xf] << 4) | WZ_SP[0][x & 0xf];
      return x << 11 | x >>> 21;
   }

   /**
    * ����ֵ����. <p>
    * �������һ��ֵ�͵ڶ���ֵ����.
    */
   private static void gost_swap(int[] buf)
   {
      int tempbuf;
      tempbuf = buf[1];
      buf[1] = buf[0];
      buf[0] = tempbuf;
   }

   /**
    * ����offset��ʼ��8��data��ֵת����2��buf��.
    */
   private static void dataToBuf(byte[] data, int offset, int[] buf)
   {
      buf[0] = (data[offset] & 0xff) | ((data[offset + 1] & 0xff) << 8)
            | ((data[offset + 2] & 0xff) << 16) | ((data[offset + 3] & 0xff) << 24);
      buf[1] = (data[offset + 4] & 0xff) | ((data[offset + 5] & 0xff) << 8)
            | ((data[offset + 6] & 0xff) << 16) | ((data[offset + 7] & 0xff) << 24);
   }

   /**
    * ��2��buf�е�ֵת����offset��ʼ��8��data��.
    */
   private static void bufToData(int[] buf, byte[] data, int offset)
   {
      data[offset] = (byte) (buf[0] & 0xff);
      data[offset + 1] = (byte) ((buf[0] >>> 8) & 0xff);
      data[offset + 2] = (byte) ((buf[0] >>> 16) & 0xff);
      data[offset + 3] = (byte) ((buf[0] >>> 24) & 0xff);
      data[offset + 4] = (byte) (buf[1] & 0xff);
      data[offset + 5] = (byte) ((buf[1] >>> 8) & 0xff);
      data[offset + 6] = (byte) ((buf[1] >>> 16) & 0xff);
      data[offset + 7] = (byte) ((buf[1] >>> 24) & 0xff);
   }

   // Gost��s-��: [8][16]
   private static final byte WZ_SP[][] = {
      {0x4, 0xa, 0x9, 0x2, 0xd, 0x8, 0x0, 0xe, 0x6, 0xb, 0x1, 0xc, 0x7, 0xf, 0x5, 0x3},
      {0xe, 0xb, 0x4, 0xc, 0x6, 0xd, 0xf, 0xa, 0x2, 0x3, 0x8, 0x1, 0x0, 0x7, 0x5, 0x9},
      {0x5, 0x8, 0x1, 0xd, 0xa, 0x3, 0x4, 0x2, 0xe, 0xf, 0xc, 0x7, 0x6, 0x0, 0x9, 0xb},
      {0x7, 0xd, 0xa, 0x1, 0x0, 0x8, 0x9, 0xf, 0xe, 0x4, 0x6, 0xc, 0xb, 0x2, 0x5, 0x3},
      {0x6, 0xc, 0x7, 0x1, 0x5, 0xf, 0xd, 0x8, 0x4, 0xa, 0x9, 0xe, 0x0, 0x3, 0xb, 0x2},
      {0x4, 0xb, 0xa, 0x0, 0x7, 0x2, 0x1, 0xd, 0x3, 0x6, 0x8, 0x5, 0x9, 0xc, 0xf, 0xe},
      {0xd, 0xb, 0x4, 0x1, 0x3, 0x6, 0x5, 0x9, 0x0, 0xa, 0xe, 0x7, 0xf, 0x8, 0x2, 0xc},
      {0x1, 0xf, 0xd, 0x0, 0x5, 0x7, 0xa, 0x4, 0x9, 0x2, 0x3, 0xe, 0x6, 0xb, 0x8, 0xc}
   };

   // ������Կʹ��˳���
   private static final int WZ_SPKEY[] = {
      0, 1, 2, 3, 4, 5, 6, 7,
      0, 2, 6, 1, 3, 7, 5, 6,
      2, 3, 7, 4, 0, 1, 5, 4,
      7, 6, 5, 4, 3, 2, 1, 0
   };

}