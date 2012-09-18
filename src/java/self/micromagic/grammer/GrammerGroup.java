
package self.micromagic.grammer;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class GrammerGroup extends AbstractElement
      implements GrammerElement
{
   private List groupElements = new ArrayList();
   private boolean initialized = false;
   private boolean initOver = false;
   private boolean allSubNone = false;

   public void initialize(Map elements)
         throws GrammerException
   {
      if (!this.initialized)
      {
         this.initialized = true;
         int hasType = 0;
         Iterator itr = this.groupElements.iterator();
         while (itr.hasNext())
         {
            GrammerGroupCell cell = (GrammerGroupCell) itr.next();
            cell.initialize(elements);
            int nowType;
            try
            {
               nowType = cell.grammerElement.isTypeNone() ? -1 : 1;
            }
            catch (GrammerException ex)
            {
               // ��������쳣, �Ǳ�ʾ��Ԫ�ر�ѭ��������, Ĭ�ϱ�ѭ�����õ�Ԫ��������
               nowType = 1;
            }
            if (hasType == 0)
            {
               hasType = nowType;
            }
            else
            {
               if (hasType != nowType)
               {
                  throw new GrammerException("In a group[" + this.getName()
                        + "], all cell type must TYPE_NONE or not TYPE_NONE." + this.groupElements);
               }
            }
         }
         this.allSubNone = hasType == 1 ? false : true;
         this.initOver = true;
      }
   }

   public boolean isTypeNone()
         throws GrammerException
   {
      if (!this.initOver)
      {
         throw new GrammerException("In a group[" + this.getName()
               + "], hasn't initialized.");
      }
      return this.getType() == TYPE_NONE ? this.allSubNone : false;
   }

   public void addElement(String name)
   {
      this.groupElements.add(new GrammerGroupCell(name));
   }

   public void addElement(GrammerElement element)
   {
      this.groupElements.add(new GrammerGroupCell(element.getName(), element));
   }

   public boolean doVerify(ParserData pd)
         throws GrammerException
   {
      Iterator itr = this.groupElements.iterator();
      int preIndex = -1;
      GrammerGroupCell trueCell = null;
      while (itr.hasNext())
      {
         pd.addResetPoint();
         GrammerGroupCell cell = (GrammerGroupCell) itr.next();
         if (cell.grammerElement.verify(pd))
         {
            if (preIndex == -1 || preIndex < pd.getCurrentIndex())
            {
               preIndex = pd.getCurrentIndex();
               trueCell = cell;
            }
         }
         pd.reset();
      }
      if (trueCell == null)
      {
         return false;
      }
      else
      {
         trueCell.grammerElement.verify(pd);
         return true;
      }
   }

   public String toString()
   {
      return "Group:" + this.getName() + ":" + GrammerManager.getGrammerElementTypeName(this.getType());
   }

   private static class GrammerGroupCell
   {
      public final String name;
      private GrammerElement grammerElement = null;

      public GrammerGroupCell(String name)
      {
         this.name = name;
      }

      public GrammerGroupCell(String name, GrammerElement grammerElement)
      {
         this.name = name;
         this.grammerElement = grammerElement;
      }

      public void initialize(Map elements)
            throws GrammerException
      {
         if (this.grammerElement == null)
         {
            GrammerElement e = (GrammerElement) elements.get(this.name);
            if (e == null)
            {
               throw new GrammerException("Not found the GrammerElement:" + this.name + ".");
            }
            e.initialize(elements);
            this.grammerElement = e;
         }
         else
         {
            this.grammerElement.initialize(elements);
         }
      }

      public String toString()
      {
         if (this.grammerElement == null)
         {
            return this.name;
         }
         return this.grammerElement.toString();
      }

   }

}