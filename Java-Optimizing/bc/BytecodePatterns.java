package optjava.bc;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 *
 * @author ben
 */
public class BytecodePatterns {

    // tag::BC_PATTERNS_S[]
    public static final String HELLO = "Hello World";
    public static final double PI = 3.142;
    
    public void showConstsAndLdc() {
        Object o = null;
        int i = -1;
        i = 0;
        i = 1;
        o = HELLO;
        double d = 0.0;
        d = PI;
    }
    // end::BC_PATTERNS_S[]

    public final void showFinal() {
        System.out.println(HELLO);
    }

    // tag::BC_PATTERNS_MH[]
    public void mh() throws Exception {
        MethodType mt = MethodType.methodType(void.class);
        MethodHandle mh = MethodHandles.lookup().findVirtual
            (BytecodePatterns.class, "mh", mt);
    }
    // end::BC_PATTERNS_MH[]

    public void for_loop() {
        for (int i = 0; i < 2; i++) {
            System.out.println(HELLO);
        }
    }

    public void lambda() {
        Runnable r = () -> {
            System.out.println(HELLO);
        };
        r.run();
    }

    public static void main(String[] args) {
        BytecodePatterns bcp = new BytecodePatterns();
        bcp.showFinal();
    }
}
