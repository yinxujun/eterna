
package self.micromagic.eterna.digester;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BodyText
{
   private class CharElement
   {
      public final int length;

      private CharElement preElement;
      private char[] ch;
      private int leftTrimCount = 0;
      private int rightTrimCount = 0;
      private boolean endNewLine = true;

      public CharElement(char[] ch, int start, int length, CharElement preElement)
      {
         this.preElement = preElement;
         this.length = length;
         this.parse(ch, start, length);
         if (this.leftTrimCount + this.rightTrimCount == length)
         {
            this.ch = null;
            // 如果是以新行结束, 则需要检查前一个, 添加rightTrim数
            if (this.endNewLine && this.preElement != null)
            {
               this.rightTrimCount = length;
               this.leftTrimCount = 0;
               this.preElement.addRightTrimCount();
            }
         }
         else
         {
            this.ch = new char[length];
            System.arraycopy(ch, start, this.ch, 0, length);
         }
      }

      private void parse(char[] ch, int start, int length)
      {
         if (this.preElement == null || this.preElement.endNewLine || this.preElement.ch == null)
         {
            // 没有前一个元素, 前一个元素是以新行结束, 前一个元素全为空格
            // 才处理leftTrim数
            int leftTrimCount = 0;
            for (; leftTrimCount < length - 1 && ch[leftTrimCount + start] <= ' '; leftTrimCount++);
            if (leftTrimCount == length - 1)
            {
               char c = ch[start + length - 1];
               if (c != BodyText.NEW_LINE && c <= ' ')
               {
                  leftTrimCount++;
               }
            }
            this.leftTrimCount = leftTrimCount;
         }
         if (ch[start + length - 1] == BodyText.NEW_LINE)
         {
            int rightTrimCount = 0;
            int count = length - this.leftTrimCount;
            for (; rightTrimCount < count && ch[start + length - rightTrimCount - 1] <= ' '; rightTrimCount++);
            this.rightTrimCount = rightTrimCount;
         }
         else
         {
            this.endNewLine = false;
         }
      }

      private void addRightTrimCount()
      {
         if (this.endNewLine)
         {
            // 如果是以新行结束, 就不需要处理rightTrim数了
            return;
         }
         if (this.ch == null || this.allBlank())
         {
            this.ch = null;
            this.rightTrimCount = this.leftTrimCount;
            this.leftTrimCount = 0;
            if (this.preElement != null)
            {
               this.preElement.addRightTrimCount();
            }
            return;
         }
         int rightTrimCount = 0;
         int count = this.length - this.leftTrimCount;
         for (; rightTrimCount < count && this.ch[this.length - rightTrimCount - 1] <= ' '; rightTrimCount++);
         this.rightTrimCount = rightTrimCount;
      }

      private boolean allBlank()
      {
         for (int i = 0; i < this.ch.length; i++)
         {
            if (this.ch[i] > ' ') return false;
         }
         return true;
      }

      public void appendTo(StringBuffer sb)
      {
         if (this.ch != null)
         {
            sb.append(this.ch);
            return;
         }
         for (int i = 0; i < this.length - 1; i++)
         {
            sb.append(' ');
         }
         sb.append(this.endNewLine ? BodyText.NEW_LINE : ' ');
      }

      public void trimSpaceAppendTo(StringBuffer sb, boolean noLine)
      {
         if (this.ch == null)
         {
            if (this.endNewLine)
            {
               sb.append(noLine ? ' ' : BodyText.NEW_LINE);
            }
            return;
         }
         sb.append(this.ch, this.leftTrimCount,
                   this.ch.length - this.leftTrimCount - this.rightTrimCount);
         if (this.endNewLine)
         {
            sb.append(noLine ? ' ' : BodyText.NEW_LINE);
         }
      }

      public int trimSpacelength()
      {
         return this.length - this.leftTrimCount - this.rightTrimCount
               + (this.endNewLine ? 1 : 0);
      }

   }

   private static final char NEW_LINE = '\n';

   private List elements = new ArrayList();

   private int count = 0;

   private CharElement preElement = null;

   private String cacheStr = null;

   private String cacheTrim = null;

   public BodyText append(char[] ch, int start, int length)
   {
      this.append(ch, start, length, true);
      return this;
   }

   public BodyText append(char[] ch, int start, int length, boolean unbindLine)
   {
      this.cacheStr = null;
      this.cacheTrim = null;
      CharElement temp;
      if (unbindLine)
      {
         int preStart = start;
         int end = start + length;
         for (int i = start; i < end; i++)
         {
            if (ch[i] == BodyText.NEW_LINE)
            {
               temp = new CharElement(ch, preStart, i - preStart + 1, this.preElement);
               this.elements.add(temp);
               this.preElement = temp;
               preStart = i + 1;
            }
         }
         if (preStart < end)
         {
            temp = new CharElement(ch, preStart, end - preStart, this.preElement);
            this.elements.add(temp);
            this.preElement = temp;
         }
      }
      else
      {
         this.elements.add(new CharElement(ch, start, length, null));
         this.preElement = null;
      }
      this.count += length;
      return this;
   }

   public String toString()
   {
      if (this.cacheStr == null)
      {
         StringBuffer sb = new StringBuffer(this.count);
         int size = this.elements.size();
         Iterator itr = this.elements.iterator();
         for (int i = 0; i < size; i++)
         {
            ((CharElement) itr.next()).appendTo(sb);
         }
         this.cacheStr = sb.toString();
      }
      return this.cacheStr;
   }

   public String trimEveryLineSpace(boolean noLine)
   {
      if (this.cacheTrim == null || noLine)
      {
         int size = this.elements.size();
         Iterator itr = this.elements.iterator();
         int tempCount = 0;
         for (int i = 0; i < size; i++)
         {
            tempCount += ((CharElement) itr.next()).trimSpacelength();
         }
         StringBuffer sb = new StringBuffer(tempCount);
         itr = this.elements.iterator();
         for (int i = 0; i < size; i++)
         {
            ((CharElement) itr.next()).trimSpaceAppendTo(sb, noLine);
         }
         if (noLine)
         {
            return sb.toString();
         }
         else
         {
            this.cacheTrim = sb.toString();
         }
      }
      return this.cacheTrim;
   }

}
