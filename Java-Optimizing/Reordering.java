package optjava;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class Reordering {

    class ReorderingThread extends Thread {
        
    }
    
    public static void main(String[] args) {
        CyclicBarrier end = new CyclicBarrier(2);
        CountDownLatch start1 = new CountDownLatch(1);
        CountDownLatch start2 = new CountDownLatch(1);
    }

}
