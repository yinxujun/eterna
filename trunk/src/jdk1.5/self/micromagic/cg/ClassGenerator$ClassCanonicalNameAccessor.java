
package self.micromagic.cg;

/**
 * 类的canonicalName的访问器.
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
