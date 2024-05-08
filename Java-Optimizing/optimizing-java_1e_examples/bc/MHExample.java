package optjava.bc;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 *
 * @author ben
 */
public class MHExample {

    // tag::MH_EXAMPLE[]

    public MethodHandle getToStringMH() throws NoSuchMethodException, 
        IllegalAccessException {
        MethodType mt = MethodType.methodType(String.class);
        MethodHandles.Lookup lk = MethodHandles.lookup();
        MethodHandle mh = lk.findVirtual(getClass(), "toString", mt);

        return mh;
    }

    public void callMH() {
        try {
            MethodHandle mh = getToStringMH();
            Object o = mh.invoke(this, null);
            System.out.println(o);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
    // end::MH_EXAMPLE[]

    public static void main(String[] args) {
        MHExample mhe = new MHExample();
        mhe.callMH();
    }
}
