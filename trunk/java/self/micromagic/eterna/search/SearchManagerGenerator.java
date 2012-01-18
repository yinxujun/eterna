
package self.micromagic.eterna.search;

import self.micromagic.eterna.share.Generator;
import self.micromagic.eterna.digester.ConfigurationException;

public interface SearchManagerGenerator extends Generator
{
   SearchManager createSearchManager() throws ConfigurationException;

}
