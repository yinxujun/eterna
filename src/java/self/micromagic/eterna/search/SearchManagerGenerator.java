
package self.micromagic.eterna.search;

import self.micromagic.eterna.share.Generator;
import self.micromagic.eterna.digester.ConfigurationException;

/**
 * @author micromagic@sina.com
 */
public interface SearchManagerGenerator extends Generator
{
   SearchManager createSearchManager() throws ConfigurationException;

}
