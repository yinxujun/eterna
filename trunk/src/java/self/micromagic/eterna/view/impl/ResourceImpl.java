
package self.micromagic.eterna.view.impl;

import java.util.Iterator;
import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;

import self.micromagic.eterna.view.ResourceGenerator;
import self.micromagic.eterna.view.Resource;
import self.micromagic.eterna.view.BaseManager;
import self.micromagic.eterna.share.AbstractGenerator;
import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.util.container.UnmodifiableIterator;
import self.micromagic.util.Utility;
import self.micromagic.util.StringAppender;
import self.micromagic.util.StringTool;
import self.micromagic.grammer.ParserData;

public class ResourceImpl extends AbstractGenerator
      implements Resource, ResourceGenerator
{
   private static final String[] EMPTY_RESOURCE = {""};

   protected String text;
   protected boolean trimLine = false;
   protected Object[] resArray;
   protected int estimateResSize = 128;

   public void initialize(EternaFactory factory)
         throws ConfigurationException
   {
      if (this.text == null)
      {
         this.resArray = EMPTY_RESOURCE;
         return;
      }
      this.estimateResSize = this.text.length() + 32;
      List resCelllist = ViewTool.parseResourceText(this.trimText(this.text));
      List rList = new LinkedList();
      this.parseResCell(resCelllist, rList);
      this.resArray = rList.toArray();
   }

   /**
    * 根据trimLine的设置去除文本两边的空格.
    */
   private String trimText(String text)
   {
	   return this.trimLine ? text.trim() : text;
   }

   protected void parseResCell(List resCelllist, List rList)
   {
      Iterator itr = resCelllist.iterator();
      while (itr.hasNext())
      {
         ParserData.GrammerCell cell = (ParserData.GrammerCell) itr.next();
         if ("resource".equals(cell.grammerElement.getName()))
         {
            // resource的结构 开始标记"{"  结束标记"}" 中间1或2位的数字表示参数的索引值
            // 整个结构中间不能有空格, 否则将视为普通文本
            Integer index = Utility.createInteger(
                  Integer.parseInt(((ParserData.GrammerCell) cell.subCells.get(1)).textBuf));
            rList.add(index);
            continue;
         }
         if (cell.subCells != null)
         {
            this.parseResCell(resCelllist, rList);
         }
         else if (cell.textBuf.length() > 0)
         {
            rList.add(cell.textBuf);
         }
      }
   }

   public Iterator getParsedRessource()
   {
      return new UnmodifiableIterator(Arrays.asList(this.resArray).iterator());
   }

   public String getValue()
   {
      return this.getValue(null);
   }

   public String getValue(Object[] params)
   {
      if (this.resArray.length == 1)
      {
         Object res = this.resArray[0];
         if (res instanceof String)
         {
            return (String) res;
         }
         else
         {
            if (params == null)
            {
               return "";
            }
            int index = ((Integer) res).intValue();
            if (index >= params.length)
            {
               return "";
            }
            return params[index] == null ? "" : String.valueOf(params[index]);
         }
      }
      StringAppender buf = StringTool.createStringAppender(this.estimateResSize);
      for (int i = 0; i < this.resArray.length; i++)
      {
         Object res = this.resArray[i];
         if (res instanceof String)
         {
            buf.append(res);
         }
         else if (params != null)
         {
            int index = ((Integer) res).intValue();
            if (index < params.length && params[index] != null)
            {
               buf.append(String.valueOf(params[index]));
            }
         }
      }
      return buf.toString();
   }

   public void setResourceText(String text)
   {
      this.text = text;
   }

   public void setTrimLine(boolean trimLine)
   {
      this.trimLine = trimLine;
   }

   public Resource createResource()
         throws ConfigurationException
   {
      return this;
   }

   public Object create()
         throws ConfigurationException
   {
      return this.createResource();
   }

}
