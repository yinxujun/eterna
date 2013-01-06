
package self.micromagic.coder;

public class Random
{
	private static final long multiplier = 0x5DEECE66DL;
	private static final long addend = 0xBL;
	private static final long mask = (1L << 48) - 1;

	private static final int BITS_PER_BYTE = 8;
	private static final int BYTES_PER_INT = 4;

	private long seed;

	public Random()
	{
		this(System.currentTimeMillis());
	}

	public Random(long seed)
	{
		this.seed = 0L;
		this.setSeed(seed);
	}

	public void setSeed(long seed)
	{
		this.seed = (seed ^ multiplier) & mask;
	}

	protected int next(int bits)
	{
		this.seed  = this.seed  * multiplier + addend & mask;
		return (int) (this.seed  >>> 48 - bits);
	}

	public void nextBytes(byte bytes[])
	{
		int numRequested = bytes.length;
		int numGot = 0;
		int rnd = 0;

		while (true)
		{
			for (int i = 0; i < BYTES_PER_INT; i++)
			{
				if (numGot == numRequested)
					return;

				rnd = (i == 0 ? this.next(BITS_PER_BYTE * BYTES_PER_INT)
						: rnd >> BITS_PER_BYTE);
				bytes[numGot++] = (byte) rnd;
			}
		}
	}

	public int nextInt()
	{
		return this.next(32);
	}

	public int nextInt(int n)
	{
		if (n <= 0)
		{
			throw new IllegalArgumentException("n must be positive");
		}
		if ((n & -n) == n)  // i.e., n is a power of 2
		{
			return (int) ((long) n * (long) this.next(31) >> 31);
		}
		int bits;
		int val;
		do
		{
			bits = this.next(31);
			val = bits % n;
		} while ((bits - val) + (n - 1) < 0);
		return val;
	}

	public long nextLong()
	{
		return ((long) this.next(32) << 32) + (long) this.next(32);
	}

}