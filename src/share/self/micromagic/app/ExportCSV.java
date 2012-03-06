
package self.micromagic.app;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.model.ModelExport;
import self.micromagic.eterna.sql.ResultIterator;
import self.micromagic.eterna.sql.ResultMetaData;
import self.micromagic.eterna.sql.ResultReader;
import self.micromagic.eterna.sql.ResultRow;
import self.micromagic.util.MemoryStream;
import self.micromagic.util.Utility;
import self.micromagic.util.Utils;

/**
 * 将ResultIterator导出成csv格式
 */
public class ExportCSV extends AbstractExportExecute
{
   public String getExecuteType()
         throws ConfigurationException
   {
      return "exportCSV";
   }

   public ModelExport execute(AppData data, Connection conn)
         throws ConfigurationException, SQLException, IOException
   {
      ResultIterator ritr = null;
      Writer out = null;
      try
      {
         ritr = this.getResultIterator(data, conn);
         HttpServletResponse response = data.getHttpServletResponse();
         if (!saveExport && response != null)
         {
            response.setContentType("application/excel");
            response.setHeader("Content-disposition", "attachment; filename="
                  + Utils.dealString2URL(this.getFileName(data) + ".csv", this.fileNameEncode));
            out = new OutputStreamWriter(data.getOutputStream(), this.encodeName);
         }
         else
         {
            MemoryStream ms = new MemoryStream(1, 1024 * 4);
            out = new OutputStreamWriter(data.getOutputStream(), this.encodeName);
            Map raMap = data.getRequestAttributeMap();
            raMap.put(DOWNLOAD_CONTENTTYPE, "application/excel");
            raMap.put(DOWNLOAD_FILENAME, Utils.dealString2URL(this.getFileName(data) + ".csv", this.fileNameEncode));
            raMap.put(DOWNLOAD_STREAM, ms.getInputStream());
         }
         this.dealExportCSV(out, ritr, data, conn);
      }
      catch (Exception ex)
      {
         log.error("Write excel error.", ex);
      }
      finally
      {
         if (ritr != null)
         {
            // 这里可能是需要接管数据库链接, 所以使用完后需要自行释放
            ritr.close();
         }
         if (out != null)
         {
            out.close();
         }
      }
      return null;
   }

   protected void dealExportCSV(Writer out, ResultIterator ritr, AppData data, Connection conn)
         throws Exception
   {
      ResultMetaData meta = ritr.getMetaData();
      int count = meta.getColumnCount();
      boolean[] notPrint = new boolean[count];

      int skipColumnCount = 0;
      for (int i = 0; i < count; i++)
      {
         ResultReader reader = meta.getColumnReader(i + 1);
         notPrint[i] = "true".equalsIgnoreCase((String) reader.getAttribute("print.notPrint"));
         if (notPrint[i])
         {
            skipColumnCount++;
            continue;
         }
         if (i - skipColumnCount > 0)
         {
            out.write(',');
         }
         this.writeString(out, meta.getColumnCaption(i + 1));
      }
      while (ritr.hasMoreRow())
      {
         out.write(Utility.LINE_SEPARATOR);
         ResultRow row = ritr.nextRow();
         skipColumnCount = 0;
         for (int i = 0; i < count; i++)
         {
            if (notPrint[i])
            {
               skipColumnCount++;
               continue;
            }
            if (i - skipColumnCount > 0)
            {
               out.write(',');
            }
            this.writeString(out, row.getFormated(i + 1));
         }
      }
   }

   protected void writeString(Writer out, String str)
         throws IOException
   {
      if (str == null)
      {
         return;
      }
      int modifyCount = 0;
      for (int i = 0; i < str.length(); i++)
      {
         char c = str.charAt(i);
         String appendStr = null;
         if (c == ',' || c < ' ')
         {
            modifyCount++;
         }
         else if (c == '"')
         {
            appendStr = "\"\"";
            modifyCount++;
         }
         if (modifyCount == 1)
         {
            out.write('"');
            out.write(str.substring(0, i));
            //这里将modifyCount的个数增加, 防止下一次调用使他继续进入这个初始化
            modifyCount++;
         }
         if (modifyCount > 0)
         {
            if (appendStr == null)
            {
               out.write(c);
            }
            else
            {
               out.write(appendStr);
            }
         }
      }
      if (modifyCount > 0)
      {
         out.write('"');
      }
      else
      {
         out.write(str);
      }
   }

}
