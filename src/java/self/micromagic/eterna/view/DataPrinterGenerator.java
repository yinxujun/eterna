
package self.micromagic.eterna.view;

import self.micromagic.eterna.share.EternaFactory;
import self.micromagic.eterna.share.Generator;
import self.micromagic.eterna.digester.ConfigurationException;

/**
 * 数据集输出器的构造者.
 */
public interface DataPrinterGenerator extends Generator
{
	/**
	 * 初始化此构造者.
	 */
	void initialize(EternaFactory factory) throws ConfigurationException;

	/**
	 * 创建一个数据集输出器.
	 *
	 * @return    数据集输出器
	 */
	DataPrinter createDataPrinter() throws ConfigurationException;

}