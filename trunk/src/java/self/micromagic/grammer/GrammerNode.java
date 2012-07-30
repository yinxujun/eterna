
package self.micromagic.grammer;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import self.micromagic.util.Utils;
import self.micromagic.util.StringAppender;
import self.micromagic.util.StringTool;

/**
 * 一个语法节点.
 */
public class GrammerNode extends AbstractElement
      implements GrammerElement
{
   /**
    * 返回true的检查器列表.
    */
   private List trueCheckers = null;

   /**
    * 返回false的的检查器列表.
    */
   private List falseCheckers = null;

   /**
    * 当为其他字符集时, 按什么状态处理.
    */
   private boolean otherCharType = false;

   /**
    * 当结束时, 按什么状态处理.
    */
   private boolean endType = false;


   public void initialize(Map elements) {}

   public boolean isTypeNone()
   {
      return this.getType() == TYPE_NONE;
   }

   public boolean doVerify(ParserData pd)
         throws GrammerException
   {
      if (pd.isEnd())
      {
         return this.endType;
      }
      char c = 0;
      try
      {
         c = pd.getNextChar();
      }
      catch (GrammerException ex)
      {
         if (pd.isEnd())
         {
            return this.endType;
         }
         throw ex;
      }

      Checker checker;

      checker = this.checkList(this.falseCheckers, c);
      if (checker != null)
      {
         return false;
      }

      checker = this.checkList(this.trueCheckers, c);
      if (checker != null)
      {
         return true;
      }
      return this.otherCharType;
   }

   public String toString()
   {
      StringAppender buf = StringTool.createStringAppender();
      buf.append("Node:").append(this.getName()).append(':')
            .append(this.otherCharType).append(',').append(this.endType)
            .append(':').append(GrammerManager.getGrammerElementTypeName(this.getType()));
      if (this.trueCheckers != null)
      {
         buf.append(":T").append(Utils.dealString2EditCode(this.trueCheckers.toString()));
      }
      if (this.falseCheckers != null)
      {
         buf.append(":F").append(Utils.dealString2EditCode(this.falseCheckers.toString()));
      }
      return buf.toString();
   }

   private Checker checkList(List list, char c)
   {
      if (list == null)
      {
         return null;
      }
      Iterator itr = list.iterator();
      while (itr.hasNext())
      {
         OneChecker checker = (OneChecker) itr.next();
         if (checker.verify(c))
         {
            return checker;
         }
      }
      return null;
   }

   public void addTrueChecker(OneChecker checker)
   {
      if (this.trueCheckers == null)
      {
         this.trueCheckers = new ArrayList();
      }
      this.trueCheckers.add(checker);
   }

   public void addFalseChecker(OneChecker checker)
   {
      if (this.falseCheckers == null)
      {
         this.falseCheckers = new ArrayList();
      }
      this.falseCheckers.add(checker);
   }

   public void setOtherCharType(boolean otherCharType)
   {
      this.otherCharType = otherCharType;
   }

   public void setEndType(boolean endType)
   {
      this.endType = endType;
   }

}
