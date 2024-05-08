package optjava;

public class LoopUnrolling
{
    public LoopUnrolling()
    {
        createData();

        long total = 0;

        total += intStride1();
        total += intStride2();
        total += intStride4();
        total += intStride8();
        total += intStride16();
        total += intStrideVariable(1);
        total += longStride1();
        total += intStride1With1Exit();
        total += intStride1With2Exits();
        total += intStride1With4Exits();

        System.out.println("Total: " + total);
    }

    // tag::setup[]
    private static final int MAX = 1_000_000;

    private long[] data = new long[MAX];

    private void createData()
    {
        java.util.Random random = new java.util.Random();

        for (int i = 0; i < MAX; i++)
        {
            data[i] = random.nextLong();
        }
    }
    // end::setup[]

    // tag::intStride1[]
    private long intStride1()
    {
        long sum = 0;
        for (int i = 0; i < MAX; i += 1)
        {
            sum += data[i];
        }
        return sum;
    }
    // end::intStride1[]

    // tag::intStride2[]
    private long intStride2()
    {
        long sum = 0;
        for (int i = 0; i < MAX; i += 2)
        {
            sum += data[i];
        }
        return sum;
    }
    // end::intStride2[]

    // tag::intStride4[]
    private long intStride4()
    {
        long sum = 0;
        for (int i = 0; i < MAX; i += 4)
        {
            sum += data[i];
        }
        return sum;
    }
    // end::intStride4[]

    // tag::intStride8[]
    private long intStride8()
    {
        long sum = 0;
        for (int i = 0; i < MAX; i += 8)
        {
            sum += data[i];
        }
        return sum;
    }
    // end::intStride8[]

    // tag::intStride16[]
    private long intStride16()
    {
        long sum = 0;
        for (int i = 0; i < MAX; i += 16)
        {
            sum += data[i];
        }
        return sum;
    }
    // end::intStride16[]

    // tag::intStrideVariable[]
    private long intStrideVariable(int stride)
    {
        long sum = 0;
        for (int i = 0; i < MAX; i += stride)
        {
            sum += data[i];
        }
        return sum;
    }
    // end::intStrideVariable[]

    // tag::longStride1[]
    private long longStride1()
    {
        long sum = 0;
        for (long l = 0; l < MAX; l++)
        {
            sum += data[(int) l];
        }
        return sum;
    }
    // end::longStride1[]

    // tag::intStride1With1Exit[]
    private long intStride1With1Exit()
    {
        long sum = 0;
        for (int i = 0; i < MAX; i += 1)
        {
            if (data[i] == 0x1234)
            {
                break;
            }
            else
            {
                sum += data[i];
            }
        }

        return sum;
    }
    // end::intStride1With1Exit[]

    // tag::intStride1With2Exits[]
    private long intStride1With2Exits()
    {
        long sum = 0;

        for (int i = 0; i < MAX; i += 1)
        {
            if (data[i] == 0x1234)
            {
                break;
            }
            else if (data[i] == 0x5678)
            {
                break;
            }
            else
            {
                sum += data[i];
            }
        }

        return sum;
    }
    // end::intStride1With2Exits[]

    // tag::intStride1With4Exits[]
    private long intStride1With4Exits()
    {
        long sum = 0;

        for (int i = 0; i < MAX; i += 1)
        {
            if (data[i] == 0x1234)
            {
                break;
            }
            else if (data[i] == 0x5678)
            {
                break;
            }
            else if (data[i] == 0x9ABC)
            {
                break;
            }
            else if (data[i] == 0xDEF0)
            {
                break;
            }
            else
            {
                sum += data[i];
            }
        }

        return sum;
    }
    // end::intStride1With4Exits

    public static void main(String[] args)
    {
        new LoopUnrolling();
    }
}
