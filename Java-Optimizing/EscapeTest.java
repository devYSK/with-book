package optjava;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
public class EscapeTest {

    private java.util.Random random = new java.util.Random();

    private class Wrapper {
        private int value;

        public Wrapper(int value) {
            this.value = value;
        }

        public boolean equals(Wrapper other) {
            return this.value == other.value;
        }
    }

    @Benchmark
    public boolean run() {
        Wrapper w1 = new Wrapper(random.nextInt());
        Wrapper w2 = new Wrapper(random.nextInt());

        return w1.equals(w2);
    }
}
