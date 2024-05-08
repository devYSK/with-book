package optjava;

public class SchedulerOverhead {

    public static void main(String[] args) throws InterruptedException {
        SchedulerOverhead so = new SchedulerOverhead();
        so.run();
    }

    private void run() throws InterruptedException {
        // tag::SCHEDULER_OVERHEAD[]
        long start = System.currentTimeMillis();
        for (int i = 0; i < 2_000; i++) {
            Thread.sleep(2);
        }
        long end = System.currentTimeMillis();
        System.out.println("Millis elapsed: " + (end - start) / 4000.0);
        // end::SCHEDULER_OVERHEAD[]
    }
}
