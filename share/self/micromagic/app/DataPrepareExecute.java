
package self.micromagic.app;

import java.sql.Connection;
import java.sql.SQLException;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import self.micromagic.eterna.share.Generator;
import self.micromagic.eterna.model.Execute;
import self.micromagic.eterna.model.ModelExport;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.model.ModelAdapter;
import self.micromagic.eterna.model.AppDataLogExecute;
import self.micromagic.eterna.model.impl.AbstractExecute;
import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.util.StringTool;
import self.micromagic.util.Utility;
import org.dom4j.Element;

public class DataPrepareExecute extends AbstractExecute
      implements Execute, Generator
{
   protected Map prepares = new HashMap();
   protected boolean needPrepare = true;
   protected boolean pushPrepare = false;

   public void initialize(ModelAdapter model)
         throws ConfigurationException
   {
      if (this.initialized)
      {
         return;
      }
      String tmp;

      tmp = (String) this.getAttribute("needPrepare");
      if (tmp != null)
      {
         this.needPrepare = "true".equalsIgnoreCase(tmp);
      }
      tmp = (String) this.getAttribute("pushPrepare");
      if (tmp != null)
      {
         this.pushPrepare = "true".equalsIgnoreCase(tmp);
      }

      tmp = (String) this.getAttribute("prepares");
      if (tmp != null)
      {
         String[] tmps = StringTool.separateString(Utility.resolveDynamicPropnames(tmp), ";", true);
         for (int i = 0; i < tmps.length; i++)
         {
            int index = tmps[i].indexOf('=');
            if (index != -1)
            {
               this.prepares.put(tmps[i].substring(0, index).trim(),
                     tmps[i].substring(index + 1).trim());
            }
         }
      }
   }

   public String getExecuteType()
   {
      return "dataPrepare";
   }

   public ModelExport execute(AppData data, Connection conn)
         throws ConfigurationException, SQLException, IOException
   {
      if (AppData.getAppLogType() != 0)
      {
         Element nowNode = data.getCurrentNode();
         if (nowNode != null)
         {
            Element vNode = nowNode.addElement("prepares");
            AppDataLogExecute.printObject(vNode, this.prepares);
         }
      }
      if (this.needPrepare)
      {
         data.dataMap.putAll(this.prepares);
      }
      if (this.pushPrepare)
      {
         data.push(this.prepares);
      }
      return null;
   }

}
