
package self.micromagic.coder;

public abstract class AbstractCoder
		implements Coder
{
	public byte[] encode(byte[] buf)
	{
		return this.encode(buf, true);
	}

	public byte[] decode(byte[] buf)
	{
		return this.decode(buf, true);
	}

}