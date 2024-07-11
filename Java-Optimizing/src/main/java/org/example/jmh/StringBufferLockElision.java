package org.example.jmh;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;

@State(Scope.Thread)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
public class StringBufferLockElision {

    private static final String[] pieces = new String[]{"a", "b", "c", "d", "e"};

    @Benchmark
    public String concatWithStringBuffer() {
        final StringBuffer buffer = new StringBuffer();

        for (String piece : pieces) {
            buffer.append(piece);
        }

        return buffer.toString();
    }

    @Benchmark
    public String concatWithStringBuilder() {
        StringBuilder builder = new StringBuilder();

        for (String piece : pieces) {
            builder.append(piece);
        }

        return builder.toString();
    }
}
