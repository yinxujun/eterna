
package self.micromagic.app;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.Element;
import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.model.AppDataLogExecute;
import self.micromagic.eterna.model.Execute;
import self.micromagic.eterna.model.ModelAdapter;
import self.micromagic.eterna.model.ModelExport;
import self.micromagic.eterna.model.impl.AbstractExecute;
import self.micromagic.eterna.share.Generator;
import self.micromagic.util.StringTool;

public class DataPrepareExecute extends AbstractExecute
      implements Execute, Generator
{
   protected Map prepares;
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
         this.prepares = StringTool.string2Map(tmp, ";", '=');
      }
      else
      {
         this.prepares = new HashMap();
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
