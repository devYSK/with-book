package optjava.ch11;

import java.lang.invoke.*;
import java.lang.reflect.*;

/**
 *
 * @author ben
 */
public class MainMH {

    public static void main(String[] args) throws Throwable {
        // tag::HANDLE[]
        try {
            MethodType mt = MethodType.methodType(int.class);
            MethodHandles.Lookup l = MethodHandles.lookup();
            MethodHandle mh = l.findVirtual(String.class, "hashCode", mt);

            String receiver = "b";
            int ret = (int) mh.invoke(receiver);
            System.out.println(ret);
        } catch (IllegalArgumentException | NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        } catch (IllegalAccessException x) {
            x.printStackTrace();
        }
        // end::HANDLE[]
    }

}
