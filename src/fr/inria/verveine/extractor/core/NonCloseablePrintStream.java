package fr.inria.verveine.extractor.core;

import java.io.OutputStream;
import java.io.PrintStream;

public class NonCloseablePrintStream extends PrintStream {
    public NonCloseablePrintStream(OutputStream var1) {
        super(var1);
    }

    public void close() {
    }
}
