package optjava;

import sun.misc.Unsafe;

//tag::ATOMIC[]
public class AtomicIntegerExample extends Number {

    private volatile int value;

    // setup to use Unsafe.compareAndSwapInt for updates
    private static final Unsafe unsafe = Unsafe.getUnsafe();
    private static final long valueOffset;

    static {
        try {
            valueOffset = unsafe.objectFieldOffset(
                 AtomicIntegerExample.class.getDeclaredField("value"));
        } catch (Exception ex) {
            throw new Error(ex);
        }
    }

    public final int get() {
        return value;
    }

    public final void set(int newValue) {
        value = newValue;
    }

    public final int getAndSet(int newValue) {
        return unsafe.getAndSetInt(this, valueOffset, newValue);
    }
   // ...
    
// end::ATOMIC[]

//tag::UNSAFE[]    
    public final int getAndSetInt(Object o, long offset, int newValue) {
        int v;
        do {
            v = getIntVolatile(o, offset);
        } while (!compareAndSwapInt(o, offset, v, newValue));

        return v;
    }
 
    public native int getIntVolatile(Object o, long offset);

    public final native boolean compareAndSwapInt(Object o, long offset,
                                    int expected, int x);
//end::UNSAFE[]
    
    @Override
    public int intValue() {
        return value;
    }

    @Override
    public long longValue() {
        return value;
    }

    @Override
    public float floatValue() {
        return value;
    }

    @Override
    public double doubleValue() {
        return value;
    }

}
