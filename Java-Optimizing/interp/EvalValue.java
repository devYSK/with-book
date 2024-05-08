package optjava.interp;

/**
 * Holds the type of the evaluation stack entry, and a bit pattern that
 * corresponds to the actual value. For objects, the bit pattern is an ID into
 * into the heap structure.
 * 
 * @author ben
 */
public class EvalValue {
    public final JVMType type;
    public final long value;

    EvalValue(JVMType t, long bits) {
        this.type = t;
        this.value = bits;
    }
}
