package fr.inria.verveine.extractor.java.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * A utility to read the content of files.
 * To make it faster, we try to keep the file opened so that successive reads in the same file
 * do not have to re-open.
 * Also to make it easier to use, everything is <code>static</code> (not proud of it)
 * @author anquetil
 */
public class FileContentExtractor {

	static protected String filename = null;
	static protected RandomAccessFile openedFile = null;

	public static String getFileContent( String inputfile, int start, int end) {
		if (filename != inputfile) {
			closeCurrentFile();
			openFile(inputfile);
		}
		return getFileContent(start, end);
	}

	protected static void openFile(String filename) {
		try {
			openedFile = new RandomAccessFile( filename, "r");
		} catch (FileNotFoundException e) {
			System.err.println("Error opening "+filename+" for reading");
		}
	}

	protected static void closeCurrentFile() {
		if (openedFile!= null) {
			try {
				openedFile.close();
			} catch (IOException e) {
				// nothing
			}
		}
	}

	protected static String getFileContent( int start, int end) {
		byte buffer[] = new byte[ end-start+1];
		try {
			openedFile.seek(start);
			int ret = openedFile.read(buffer);
			if (ret < end-start+1) {
				System.err.println("missing bytes, read "+ret+" instead of "+(end-start+1));
				return "";
			}
			return buffer.toString();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

}
