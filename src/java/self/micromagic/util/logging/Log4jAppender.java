
package self.micromagic.util.logging;

import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.ThrowableInformation;
import org.apache.log4j.spi.LocationInfo;
import org.apache.log4j.spi.OptionHandler;

/**
 * 在log4j中调用MemoryLogger进行日志记录.
 */
public class Log4jAppender
		implements Appender, OptionHandler
{
	private String name;
	private Filter filter;
	private ErrorHandler errorHandler;
	private Layout layout;

	private MemoryLogger log;

	public Log4jAppender()
	{
		this.log = MemoryLogger.getInstance();
	}

	public Log4jAppender(String name)
	{
		this.log = MemoryLogger.getInstance(name);
	}

	/**
	 * 设置使用的MemoryLogger的名称.
	 */
	public void setMemoryLogger(String name)
	{
      this.log = MemoryLogger.getInstance(name);
	}

	public void doAppend(LoggingEvent event)
	{
		Throwable ex = null;
		ThrowableInformation ti = event.getThrowableInformation();
		if (ti != null)
		{
			ex = ti.getThrowable();
		}
		LocationInfo li = event.getLocationInformation();
		this.log.addLog(String.valueOf(event.getMessage()), ex, String.valueOf(event.getLevel()),
				event.getThreadName(), li.getClassName(), li.getMethodName(), li.getFileName(), li.getLineNumber());
	}

	public void setName(String initConfig)
	{
		this.name = initConfig;
	}

	public String getName()
	{
		return this.name;
	}

	public void addFilter(Filter filter)
	{
		if (this.filter != null)
		{
			this.filter = filter;
		}
	}

	public void clearFilters()
	{
		this.filter = null;
	}

	public Filter getFilter()
	{
		return this.filter;
	}

	public void setErrorHandler(ErrorHandler errorHandler)
	{
		this.errorHandler = errorHandler;
	}

	public ErrorHandler getErrorHandler()
	{
		return this.errorHandler;
	}

	public void setLayout(Layout layout)
	{
		this.layout = layout;
	}

	public Layout getLayout()
	{
		return this.layout;
	}

	public boolean requiresLayout()
	{
		return false;
	}

	public void activateOptions()
	{
	}

	public void close()
	{
	}

}
