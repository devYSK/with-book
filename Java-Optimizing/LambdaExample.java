package optjava;

/**
 *
 * @author ben
 */
// tag::LAMBDA_SOURCE[]
public class LambdaExample {
    private static final String HELLO = "Hello";

    public static void main(String[] args) throws Exception {
        Runnable r = () -> System.out.println(HELLO);
        Thread t = new Thread(r);
        t.start();
        t.join();
    }
}
// end::LAMBDA_SOURCE[]
