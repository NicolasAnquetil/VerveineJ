package eu.synectique.licence;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LicenceChecker {
	private static final String VALIDATE_LICENCE = "http://37.139.2.203/validator/validate:";

	public static final int OK = 100;
	
	// IO errors
	private static final int MALFORMED_URL = 101;
	private static final int IO_EXCEPT_ON_OPEN_CONNECTION = 102;
	private static final int IO_EXCEPT_ON_GET_RESPONSE_CODE = 103;
	private static final int IO_EXCEPT_ON_GET_INPUT_STREAM = 104;
	private static final int IO_EXCEPT_ON_READLINE = 105;
	private static final int IO_EXCEPT_ON_CLOSE = 106;

	// our errors
	private static final int POS_FILE_NOT_FOUND = 11;
	private static final int WRONG_POS_FORMAT = 12;
	private static final int OPENING_KEY_FILE = 13;
	private static final int READING_KEY_FILE = 14;
	private static final int READING_OLD_KEY = 15;
	private static final int WRITING_KEY_FILE = 16;
	private static final int CLOSING_KEY_FILE = 17;
	
	public static final int WRONG_LICENCE = 107;

	private static final String KEY_FILE = "famix.jar";
	private static final String POS_FILE = "key";

	private String line = null;
	private String keyDir;  // where the key file is
	private long keyPos;  // where the key is
	private String key;  // the key
	private RandomAccessFile keyFile;  // the key file 

	public int checkLicence() {
		int ret;
		ret = setKeyDir();
		if (ret != OK) { return(ret); }
			
		ret = setKeyPos();
		if (ret != OK) { return(ret); }
		
		ret = openKeyFile();
		if (ret != OK) { return(ret); }
		
		ret = getOldKey();
		if (ret != OK) { return(ret); }
		
		ret = accessServer();
		if (ret != OK) { return(ret); }
		
		ret = updateKey();
		if (ret != OK) { return(ret); }

		return OK;
	}

	private int setKeyDir() {
		String keyPath = null;
		int i;

		// find where the key is
		String classPath = System.getProperty("java.class.path");
		
		for (String libpath : classPath.split(System.getProperty("path.separator"))) {
			if (libpath.endsWith("akuhn-util-r28011.jar")) {
				keyPath = libpath;
				break;
			}
		}
		
		// reconstruct key dir
		i = keyPath.lastIndexOf(System.getProperty("file.separator"));
		keyDir = keyPath.substring(0, i+1);
		return OK;
	}

	private int setKeyPos() {
		int ret;
		try {
			ret = readLineInStream( new FileInputStream(keyDir + POS_FILE) );
		} catch (FileNotFoundException e) {
			return POS_FILE_NOT_FOUND;
		}
		
		if (ret == OK) {
			try {
				keyPos = Long.parseLong(line);
			}
			catch (NumberFormatException e) {
				return WRONG_POS_FORMAT;
			}
		}

		return OK;
	}

	private int openKeyFile() {
		try {
			keyFile = new RandomAccessFile(keyDir + KEY_FILE, "rw");
		} catch (FileNotFoundException e) {
			return(OPENING_KEY_FILE);
		}
		return OK;
	}

	private int getOldKey() {
		int ret;
		byte[] buffer = new byte[20];

		try {
			keyFile.seek(keyPos);
			ret = keyFile.read(buffer);
		} catch (IOException e1) {
			return(READING_KEY_FILE);
		}

		if (ret <= 0) {
			return READING_OLD_KEY;  
		}

		key = new String(buffer, 0, ret);
		
		return OK;
	}

	private int accessServer() {
		URL url;
		HttpURLConnection conn;
		int ret;

		try {
			url = new URL(VALIDATE_LICENCE + key);
		} catch (MalformedURLException e) {
			return MALFORMED_URL;
		}
		try {
			conn = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			return IO_EXCEPT_ON_OPEN_CONNECTION;
		}

		try {
			if (conn.getResponseCode() != 200) {
				if  (conn.getResponseCode() == 401) {
					conn.disconnect();	
					line = "Unauthorized";
					return WRONG_LICENCE;
				}
				else {
					return conn.getResponseCode();
				}
			}
		} catch (IOException e) {
			return IO_EXCEPT_ON_GET_RESPONSE_CODE;
		}

		try {
			ret = readLineInStream(conn.getInputStream());
		} catch (IOException e) {
			return IO_EXCEPT_ON_GET_INPUT_STREAM;
		}

		conn.disconnect();

		if (ret != OK) {
			return ret;  
		}
		
		key = line;  // new key
		
		return OK;
	}

	private int updateKey() {
		try {
			keyFile.setLength(keyPos);  // truncates and does a seek
			keyFile.write(key.getBytes());
		} catch (IOException e1) {
			return(WRITING_KEY_FILE);
		}

		try {
			keyFile.close();
		} catch (IOException e) {
			return(CLOSING_KEY_FILE);
		}
		return OK;
	}

	private int readLineInStream(InputStream stream) {
		BufferedReader rd;

		rd = new BufferedReader(new InputStreamReader(stream));

		try {
			line = rd.readLine();
		} catch (IOException e) {
			return IO_EXCEPT_ON_READLINE;
		}

		try {
			rd.close();
		} catch (IOException e) {
			return IO_EXCEPT_ON_CLOSE;
		}
		
		return OK;
	}

	public String getLineRead() {
		return line;
	}

}
