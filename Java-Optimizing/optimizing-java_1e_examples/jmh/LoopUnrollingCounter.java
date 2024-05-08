package optjava.jmh;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
public class LoopUnrollingCounter
{
	private static final int MAX = 1_000_000;

	private long[] data = new long[MAX];

	@Setup
	public void createData()
	{
		java.util.Random random = new java.util.Random();

		for (int i = 0; i < MAX; i++)
		{
			data[i] = random.nextLong();
		}
	}

	@Benchmark
	public long intStride1()
	{
		long sum = 0;
		for (int i = 0; i < MAX; i++)
		{
			sum += data[i];
		}
		return sum;
	}

	@Benchmark
	public long longStride1()
	{
		long sum = 0;
		for (long l = 0; l < MAX; l++)
		{
			sum += data[(int) l];
		}
		return sum;
	}
}
