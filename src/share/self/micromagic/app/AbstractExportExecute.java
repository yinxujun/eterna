
package self.micromagic.app;

import java.sql.Connection;
import java.sql.SQLException;

import self.micromagic.eterna.model.ModelAdapter;
import self.micromagic.eterna.model.Execute;
import self.micromagic.eterna.model.AppData;
import self.micromagic.eterna.model.impl.AbstractExecute;
import self.micromagic.eterna.digester.ConfigurationException;
import self.micromagic.eterna.share.Generator;
import self.micromagic.eterna.sql.ResultIterator;
import self.micromagic.eterna.sql.QueryAdapter;
import self.micromagic.eterna.sql.ResultReaderManager;
import self.micromagic.eterna.search.SearchAdapter;
import self.micromagic.util.Utils;

public abstract class AbstractExportExecute extends AbstractExecute
      implements Execute, Generator
{
   /**
    * 标识是否是使用数据集中的指定值来作为文件名.
    */
   public static final String DATA_FILE_NAME_PREFIX = "$data.";

   public static final String DOWNLOAD_CONTENTTYPE = "download.contentType";
   public static final String DOWNLOAD_FILENAME = "download.fileName";
   public static final String DOWNLOAD_STREAM = "download.stream";

   public static final int PRINT_TYPE_NUMBER = 1;
   public static final int PRINT_TYPE_DATE = 2;

   protected boolean holdConnection = true;
   protected int queryCacheIndex = 5;
   protected ResultReaderManager otherReaderManager = null;
   protected String fileName = "export";
   protected String encodeName = "UTF-8";
   protected String fileNameEncode = "UTF-8";
   protected boolean saveExport = false;

   public void initialize(ModelAdapter model)
         throws ConfigurationException
   {
      if (this.initialized)
      {
         return;
      }
      super.initialize(model);

      String temp = (String) this.getAttribute("fileName");
      if (temp != null)
      {
         this.fileName = temp;
      }
      temp = (String) this.getAttribute("encodeName");
      if (temp != null)
      {
         this.encodeName = temp;
      }
      temp = (String) this.getAttribute("fileNameEncode");
      if (temp != null)
      {
         this.fileNameEncode = temp;
      }
      temp = (String) this.getAttribute("saveExport");
      if (temp != null)
      {
         this.saveExport = "true".equalsIgnoreCase(temp);
      }
      temp = (String) this.getAttribute("queryCacheIndex");
      if (temp != null)
      {
         this.queryCacheIndex = Utils.parseInt(temp, 5);
      }
      temp = (String) this.getAttribute("otherReaderManager");
      if (temp != null)
      {
         this.otherReaderManager = model.getFactory().getReaderManager(temp);
         if (this.otherReaderManager == null)
         {
            log.error("Not found reader manager [" + temp + "].");
         }
      }
      this.holdConnection = model.getTransactionType() ==  ModelAdapter.T_HOLD;
   }

   protected String getFileName(AppData data)
   {
      if (this.fileName.startsWith(DATA_FILE_NAME_PREFIX))
      {
         return (String) data.dataMap.get(this.fileName.substring(DATA_FILE_NAME_PREFIX.length()));
      }
      return this.fileName;
   }

   protected ResultIterator getResultIterator(AppData data, Connection conn)
         throws ConfigurationException, SQLException
   {
      Object obj = data.caches[this.queryCacheIndex];
      if (obj == null)
      {
         throw new ConfigurationException("There is no value in cache:" + this.queryCacheIndex + ".");
      }
      if (obj instanceof QueryAdapter)
      {
         QueryAdapter query = (QueryAdapter) obj;
         if (this.otherReaderManager != null)
         {
            query.setReaderManager(this.otherReaderManager);
         }
         return this.holdConnection ? query.executeQueryHoldConnection(conn) : query.executeQuery(conn);
      }
      if (obj instanceof ResultIterator)
      {
         return (ResultIterator) obj;
      }
      if (obj instanceof SearchAdapter.Result)
      {
         return ((SearchAdapter.Result) obj).queryResult;
      }
      throw new ConfigurationException("Error value type [" + obj.getClass() + "] in cache:"
            + this.queryCacheIndex + ".");
   }

}
