
package self.micromagic.cg;

/**
 * 绫荤殑canonicalName鐨勮闂櫒.
 *
 * @author micromagic.sina.com
 */
class ClassGenerator$ClassCanonicalNameAccessor
		implements ClassGenerator.NameAccessor
{
	public String getName(Class c)
	{
		return c.getCanonicalName();
	}

}