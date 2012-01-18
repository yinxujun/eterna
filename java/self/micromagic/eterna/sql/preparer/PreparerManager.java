
package self.micromagic.eterna.sql.preparer;

import java.sql.SQLException;
import java.util.Arrays;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.sql.PreparedStatementWrap;
import self.micromagic.eterna.sql.SQLParameter;

public class PreparerManager
{
   public static final ValuePreparer IGNORE_PREPARER = new AbstractValuePreparer(){
      public void setValueToStatement(int index, PreparedStatementWrap stmtWrap) {}
   };

   private ValuePreparer[] preparers;
   private SQLParameter[] parameterArray = null;
   private PreparerManagerList[] insertedPM = null;

   /**
    * ����һ��PreparerManager, ��Ҫָ��preparers�ĸ���
    */
   public PreparerManager(int count)
   {
      this.preparers = new ValuePreparer[count];
   }

   /**
    * ͨ��ָ��SQLParameter����������һ��PreparerManager
    */
   public PreparerManager(SQLParameter[] parameterArray)
   {
      this(parameterArray.length);
      this.parameterArray = parameterArray;
   }

   /**
    * ��ȡpreparers�ĸ���
    */
   public int getCount()
   {
      return this.preparers.length;
   }

   /**
    * ��ȡδ���ó�"����"�Ĳ�������
    */
   public int getParamCount()
   {
      int count = 0;
      for (int i = 0; i < this.preparers.length; i++)
      {
         if (this.preparers[i] != IGNORE_PREPARER)
         {
            count++;
         }
      }
      if (this.insertedPM != null)
      {
         PreparerManagerList tpml;
         for (int i = 0; i < this.insertedPM.length; i++)
         {
            if (this.insertedPM[i] != null)
            {
               tpml = this.insertedPM[i];
               do
               {
                  count += tpml.preparerManager.getParamCount();
                  tpml = tpml.next;
               } while (tpml != null);
            }
         }
      }
      return count;
   }

   /**
    * �ж��Ƿ���δ"����"�Ĳ���
    */
   public boolean hasActiveParam()
   {
      for (int i = 0; i < this.preparers.length; i++)
      {
         if (this.preparers[i] != IGNORE_PREPARER)
         {
            return true;
         }
      }
      if (this.insertedPM != null)
      {
         PreparerManagerList tpml;
         for (int i = 0; i < this.insertedPM.length; i++)
         {
            if (this.insertedPM[i] != null)
            {
               tpml = this.insertedPM[i];
               do
               {
                  if (tpml.preparerManager.hasActiveParam())
                  {
                     return true;
                  }
                  tpml = tpml.next;
               } while (tpml != null);
            }
         }
      }
      return false;
   }

   /**
    * ����һ��preparer
    */
   public void setValuePreparer(ValuePreparer preparer)
         throws ConfigurationException
   {
      try
      {
         this.preparers[preparer.getRelativeIndex() - 1] = preparer;
      }
      catch (Exception ex)
      {
         throw new ConfigurationException(
               "Invalid parameter index:" + (preparer.getRelativeIndex()) + ".");
      }
   }

   /**
    * ��ĳ��preparer����Ϊ���ԵĲ���
    */
   public void setIgnore(int index)
         throws ConfigurationException
   {
      try
      {
         this.preparers[index - 1] = IGNORE_PREPARER;
      }
      catch (Exception ex)
      {
         throw new ConfigurationException(
               "Invalid parameter index:" + (index) + ".");
      }
   }

   /**
    * ���м����һ��PreparerManager
    *
    * @param pm             Ҫ�����PreparerManager
    * @param index          ����Ĳ���λ��
    * @param subPartIndex   �������sqlλ��
    */
   public void inserPreparerManager(PreparerManager pm, int index, int subPartIndex)
   {
      if (pm == null && this.insertedPM == null)
      {
         // �����õ�PreparerManagerΪnull, ��insertedPMδ��ʼ��ʱ,
         // �����κβ���
         return;
      }
      if (index < 0 || index > this.preparers.length)
      {
         StringBuffer buf = new StringBuffer(18);
         buf.append("[index:").append(index).append(" size:")
               .append(this.preparers.length + 1).append("]");
         throw new IndexOutOfBoundsException(buf.toString());
      }
      if (this.insertedPM == null)
      {
         this.insertedPM = new PreparerManagerList[this.preparers.length + 1];
      }
      this.insertedPM[index] = this.modifyPreparerManagerList(
            this.insertedPM[index], pm, subPartIndex);
   }

   private PreparerManagerList modifyPreparerManagerList(PreparerManagerList pml,
         PreparerManager pm, int subPartIndex)
   {
      if (pml == null)
      {
         // ���pmlΪ��, pm��Ϊ��, ���½�һ��pml
         if (pm != null)
         {
            return new PreparerManagerList(subPartIndex, pm);
         }
      }
      else
      {
         PreparerManagerList prepml = pml;
         PreparerManagerList nowpml = pml;
         boolean found = false;
         do
         {
            if (nowpml.subPartIndex == subPartIndex)
            {
               found = true;
               break;
            }
            else if (nowpml.subPartIndex > subPartIndex)
            {
               break;
            }
            prepml = nowpml;
            nowpml = nowpml.next;
         } while (nowpml != null);

         if (found)
         {
            // ����ҵ���pml
            if (pm == null)
            {
               // ��pmΪ��, ��Ҫɾ�����pml
               if (prepml == nowpml)
               {
                  // prepml == nowpml ��˵����ͷһ��
                  return nowpml.next;
               }
               else
               {
                  prepml.next = nowpml.next;
               }
            }
            else
            {
               nowpml.preparerManager = pm;
            }
         }
         else if (pm != null)
         {
            // �����δ����pml, ��pm��Ϊ��
            if (prepml == nowpml)
            {
               // prepml == nowpml ��˵����ͷһ��
               prepml = new PreparerManagerList(subPartIndex, pm);
               prepml.next = nowpml;
               return prepml;
            }
            else
            {
               prepml.next = new PreparerManagerList(subPartIndex, pm);
               prepml.next.next = nowpml;
            }
         }
      }
      return pml;
   }

   /**
    * �����е�preparer�������õ�PreparedStatement��
    */
   public void prepareValues(PreparedStatementWrap stmtWrap)
         throws ConfigurationException, SQLException
   {
      this.prepareValues(stmtWrap, 1, null);
   }

   /**
    * �����е�preparer�������õ�PreparedStatement��
    * ���һὫ�����õĲ������õ�����ֵ�ŵ�paramIndexs��
    */
   public void prepareValues(PreparedStatementWrap stmtWrap, int[] paramIndexs)
         throws ConfigurationException, SQLException
   {
      if (paramIndexs != null)
      {
         if (paramIndexs.length < this.preparers.length)
         {
            paramIndexs = null;
         }
         else
         {
            Arrays.fill(paramIndexs, -1);
         }
      }
      this.prepareValues(stmtWrap, 1, paramIndexs);
   }

   /**
    * �����е�preparer�������õ�PreparedStatement��
    *
    * @return ʵ�����õĲ�������
    */
   private int prepareValues(PreparedStatementWrap stmtWrap, int startIndex, int[] paramIndexs)
         throws ConfigurationException, SQLException
   {
      int realIndex = startIndex;
      int settedCount = 0;
      PreparerManagerList tpml;
      for (int i = 0; i < this.preparers.length; i++)
      {
         if (this.insertedPM != null)
         {
            if (this.insertedPM[i] != null)
            {
               tpml = this.insertedPM[i];
               do
               {
                  int count = tpml.preparerManager.prepareValues(stmtWrap, realIndex, null);
                  realIndex += count;
                  settedCount += count;
                  tpml = tpml.next;
               } while (tpml != null);
            }
         }
         if (this.preparers[i] == null)
         {
            StringBuffer buf = new StringBuffer(52);
            buf.append("The parameter");
            if (this.parameterArray != null)
            {
               buf.append(" [").append(this.parameterArray[i].getName()).append("]");
            }
            buf.append(" not setted. real:").append(realIndex);
            buf.append(" relative:").append(i + 1).append(".");
            throw new ConfigurationException(buf.toString());
         }
         else
         {
            if (this.preparers[i] != IGNORE_PREPARER)
            {
               this.preparers[i].setValueToStatement(realIndex, stmtWrap);
               if (paramIndexs != null)
               {
                  paramIndexs[i] = realIndex;
               }
               realIndex++;
               settedCount++;
            }
         }
      }
      if (this.insertedPM != null)
      {
         if (this.insertedPM[this.preparers.length] != null)
         {
            tpml = this.insertedPM[this.preparers.length];
            do
            {
               int count = tpml.preparerManager.prepareValues(stmtWrap, realIndex, null);
               realIndex += count;
               settedCount += count;
               tpml = tpml.next;
            } while (tpml != null);
         }
      }
      return settedCount;
   }

   private static class PreparerManagerList
   {
      public int subPartIndex;
      public PreparerManager preparerManager;
      public PreparerManagerList next;

      public PreparerManagerList(int subPartIndex, PreparerManager preparerManager)
      {
         this.subPartIndex = subPartIndex;
         this.preparerManager = preparerManager;
         this.next = null;
      }

   }

}
