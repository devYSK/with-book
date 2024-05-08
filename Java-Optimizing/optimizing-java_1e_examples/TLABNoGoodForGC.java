package optjava;

/**
 *
 * @author ben
 */
public class TLABNoGoodForGC {

    // tag::TLAB_ESCAPE[]
    public static void main(String[] args) {
        int[] anInt = new int[1];
        anInt[0] = 42;
        Runnable r = () -> { 
            anInt[0]++; 
            System.out.println("Changed: "+ anInt[0]);
        };
        new Thread(r).start();
    }
    // end::TLAB_ESCAPE[]
}
