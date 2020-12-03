package fr.inria.verveine.extractor.java.utils;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * A non-closeable output stream
 * Exists solely to redefine an empty close()
 */
public class NonCloseablePrintStream extends PrintStream {

	
	public NonCloseablePrintStream(OutputStream underlying) {
		super(underlying);
	}

	public void close() {
		// Aah aaaah! NOT closing :-)
	}
	
}
