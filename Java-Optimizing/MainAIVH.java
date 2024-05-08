package optjava.ch15;

/**
 *
 * @author ben
 */
public class MainAIVH {

    public static void main(String[] args) {
        AtomicIntegerWithVarHandles a = new AtomicIntegerWithVarHandles();
        a.set(42);
        int original = a.getAndSet(17);
        System.out.println(original);
        System.out.println(a.intValue());
    }
    
}
