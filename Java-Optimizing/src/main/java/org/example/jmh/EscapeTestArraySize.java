package org.example.jmh;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
public class EscapeTestArraySize {

    private java.util.Random random = new java.util.Random();

    @Benchmark
    public long arraySize63() {
        int[] a = new int[63];

        a[0] = random.nextInt();
        a[1] = random.nextInt();

        return a[0] + a[1];
    }

    @Benchmark
    public long arraySize64() {
        int[] a = new int[64];

        a[0] = random.nextInt();
        a[1] = random.nextInt();

        return a[0] + a[1];
    }

    @Benchmark
    public long arraySize65() {
        int[] a = new int[65];

        a[0] = random.nextInt();
        a[1] = random.nextInt();

        return a[0] + a[1];
    }
}
