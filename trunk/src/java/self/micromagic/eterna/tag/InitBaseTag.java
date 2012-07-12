
package self.micromagic.eterna.tag;

import java.util.Map;
import java.util.HashMap;

import javax.servlet.jsp.tagext.TagSupport;

import self.micromagic.eterna.view.ViewAdapter;
import self.micromagic.eterna.digester.ConfigurationException;

public class InitBaseTag extends TagSupport
{
   /**
    * 在_eterna.cache中存放区分多个同名控件时使用的后缀的名称.
    */
   public static final String SUFFIX_ID_FLAG = "eSuffixId";

   /**
    * 在_eterna.cache中存放根控件的名称.
    */
   public static final String ROOT_OBJ_ID_FLAG = "eRootObjId";

   /**
    * 在_eterna.cache中存放模板根控件标记的名称.
    */
   public static final String SCATTER_FLAG = "scatterFlag";


   private String parentElement;
   private String suffixId;
   private boolean useAJAX;
   private String scatterFlag;

   protected Map getCacheMap(ViewAdapter view)
         throws ConfigurationException
   {
      Map cache = new HashMap();
      if (this.suffixId != null)
      {
         cache.put(SUFFIX_ID_FLAG, this.suffixId);
      }
      if (this.parentElement != null)
      {
         cache.put(ROOT_OBJ_ID_FLAG, this.parentElement);
      }
      if (this.scatterFlag != null)
      {
         cache.put(SCATTER_FLAG, this.scatterFlag);
      }
      String width = view.getWidth();
      String height = view.getHeight();
      if (width != null)
      {
         cache.put(ROOT_OBJ_ID_FLAG + ".width", width);
      }
      if (height != null)
      {
         cache.put(ROOT_OBJ_ID_FLAG + ".height", height);
      }
      return cache.size() > 0 ? cache : null;
   }

   public void release()
   {
      this.parentElement = null;
      this.suffixId = null;
      this.useAJAX = false;
      this.scatterFlag = null;
      super.release();
   }

   public String getParentElement()
   {
      return this.parentElement;
   }

   public void setParentElement(String parentElement)
   {
      this.parentElement = parentElement;
   }

   public String getSuffixId()
   {
      return this.suffixId;
   }

   public void setSuffixId(String suffixId)
   {
      this.suffixId = suffixId;
   }

   public boolean isUseAJAX()
   {
      return this.useAJAX;
   }

   public void setUseAJAX(boolean useAJAX)
   {
      this.useAJAX = useAJAX;
   }

   public String getScatterFlag()
   {
      return this.scatterFlag;
   }

   public void setScatterFlag(String scatterFlag)
   {
      this.scatterFlag = scatterFlag;
   }

}
