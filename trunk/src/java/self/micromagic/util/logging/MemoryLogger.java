
package self.micromagic.util.logging;

import java.util.Iterator;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;
import java.io.Writer;
import java.io.IOException;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.DocumentHelper;
import org.dom4j.io.XMLWriter;
import self.micromagic.util.FormatTool;
import self.micromagic.cg.ClassGenerator;
import self.micromagic.eterna.model.AppData;

/**
 * ����־��XML����ʽ��¼���ڴ���.
 */
public class MemoryLogger
{
	/**
	 * ��ȡһ��ȫ�ֵ��ڴ���־ʵ��.
	 */
	public static MemoryLogger getInstance()
	{
		return instance;
	}
	private static MemoryLogger instance = new MemoryLogger();

	/**
	 * ��ȡһ��ֵ�����Ƶ��ڴ���־ʵ��.
	 */
	public static MemoryLogger getInstance(String name)
	{
		if (name == null)
		{
			return instance;
		}
		MemoryLogger ml = (MemoryLogger) instanceCache.get(name);
		if (ml == null)
		{
			synchronized (instanceCache)
			{
				ml = (MemoryLogger) instanceCache.get(name);
				if (ml == null)
				{
					ml = new MemoryLogger();
					instanceCache.put(name, ml);
				}
			}
		}
		return ml;
	}
	private static Map instanceCache = new HashMap();

	/**
	 * �����־��Ϣ��dom�ڵ�.
	 */
	private Document logDocument = null;
	private Element logNodes = null;

	private MemoryLogger()
	{
	}

	/**
	 * ���õ�ǰ�ڴ���־�Ƿ���Ч.
	 */
	public void setLogValid(boolean valid)
	{
		this.logValid = valid;
	}
	private boolean logValid;

	/**
	 * ��鲢��ʼ����־��Ϣ��dom�ڵ�.
	 */
	private void checkNodeInit()
	{
		if (this.logDocument == null)
		{
			this.logDocument = DocumentHelper.createDocument();
			Element root = this.logDocument.addElement("eterna");
			this.logNodes = root.addElement("logs");
		}

		if (this.logNodes.elements().size() > 2048)
		{
			// ���ڵ����ʱ, ���������ӵļ����ڵ�
			Iterator itr = this.logNodes.elementIterator();
			try
			{
				for (int i = 0; i < 1536; i++)
				{
					itr.next();
					itr.remove();
				}
			}
			catch (Exception ex)
			{
				// ��ȥ���ڵ����ʱ, �������־
				this.logDocument = null;
				this.checkNodeInit();
			}
		}
	}

	/**
	 * ���һ����¼��־��Ϣ�Ľڵ�.
	 */
	private synchronized void addLogNode(Element logNode)
	{
		this.checkNodeInit();
      this.logNodes.add(logNode);
	}

	/**
	 * ����һ����¼��־��Ϣ�Ľڵ�.
	 *
	 * @param nodeName  �ڵ������
	 */
	private Element createLogNode(String nodeName)
	{
      return DocumentHelper.createElement(nodeName);
	}

	/**
	 * ���һ����־��Ϣ.
	 *
	 * @param msg         ��־���ı���Ϣ
	 * @param ex          �쳣��Ϣ
	 * @param isCause     �Ƿ�Ϊ��¼�쳣�Ĳ�����, ����������쳣���쳣
	 * @param level       ��־�ĵȼ�
	 * @param threadName  ��¼��־�������߳�
	 * @param className   ��¼��־��������
	 * @param methodName  ��¼��־�����ڷ���
	 * @param fileName    ��¼��־�������ļ�
	 * @param lineNumber  ��¼��־��������
	 * @param logNode     ��־��Ϣ����ӵĽڵ�
	 */
	private void addLog(String msg, Throwable ex, boolean isCause, String level, String threadName,
			String className, String methodName, String fileName, String lineNumber, Element logNode)
	{
		if (!isCause)
		{
			logNode.addAttribute("level", level);
			logNode.addAttribute("thread", threadName);
			logNode.addAttribute("class", className);
			logNode.addAttribute("method", methodName);
			if (fileName != null)
			{
				logNode.addAttribute("fileName", fileName);
			}
			if (lineNumber != null)
			{
				logNode.addAttribute("lineNumber", lineNumber);
			}
			logNode.addAttribute("message", msg);
		}
		logNode.addAttribute("time", FormatTool.dateFullFormat.format(new Date(System.currentTimeMillis())));
		if (ex != null)
		{
			logNode.addAttribute("exClass", ClassGenerator.getClassName(ex.getClass()));
			logNode.addAttribute("exMessage", ex.getMessage());
			Element stacks = logNode.addElement("stacks");
			StackTraceElement[] trace = ex.getStackTrace();
			for (int i = 0; i < trace.length; i++)
			{
				Element stack = stacks.addElement("stack");
				stack.setText(trace[i].toString());
			}
		}
	}

	/**
	 * ���һ����־��Ϣ.
	 *
	 * @param msg         ��־���ı���Ϣ
	 * @param ex          �쳣��Ϣ
	 * @param level       ��־�ĵȼ�
	 * @param threadName  ��¼��־�������߳�
	 * @param className   ��¼��־��������
	 * @param methodName  ��¼��־�����ڷ���
	 * @param fileName    ��¼��־�������ļ�
	 * @param lineNumber  ��¼��־��������
	 */
	public void addLog(String msg, Throwable ex, String level, String threadName, String className,
			String methodName, String fileName, String lineNumber)
	{
		if (!this.logValid)
		{
			return;
		}
		Element logNode = this.createLogNode(ex == null ? "message" : "exception");
		this.addLog(msg, ex, false, level, threadName, className, methodName, fileName, lineNumber, logNode);
		if (ex != null)
		{
			Throwable cause = ex;
			while ((cause = cause.getCause()) != null)
			{
				Element causeNode = logNode.addElement("cause_by");
				this.addLog(null, cause, true, null, null, null, null, null, null, causeNode);
			}
		}
		if (AppData.getAppLogType() > 0)
		{
			Element nowNode = AppData.getCurrentData().getCurrentNode();
			if (nowNode != null)
			{
				nowNode.add(logNode.createCopy());
			}
		}
		this.addLogNode(logNode);
	}

	/**
	 * ���һ����־��Ϣ.
	 *
	 * @param msg         ��־���ı���Ϣ
	 * @param level       ��־�ĵȼ�
	 * @param threadName  ��¼��־�������߳�
	 * @param className   ��¼��־��������
	 * @param methodName  ��¼��־�����ڷ���
	 * @param fileName    ��¼��־�������ļ�
	 * @param lineNumber  ��¼��־��������
	 */
	public void addLog(String msg, String level, String threadName, String className, String methodName,
			String fileName, String lineNumber)
	{
		this.addLog(msg, null, level, threadName, className, methodName, fileName, lineNumber);
	}

	/**
	 * ��ӡ��־��Ϣ.
	 *
	 * @param out    ��ӡ���ӵ������
	 * @param clear  �Ƿ���Ҫ������е���־
	 */
	public void printLog(Writer out, boolean clear)
			throws IOException
	{
		if (this.logDocument == null)
		{
			return;
		}
		synchronized (this)
		{
			XMLWriter writer = new XMLWriter(out);
			writer.write(this.logDocument);
			writer.flush();
			if (clear)
			{
				logDocument = null;
				logNodes = null;
			}
		}
	}

}
