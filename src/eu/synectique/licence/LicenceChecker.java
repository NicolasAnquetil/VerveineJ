package eu.synectique.licence;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LicenceChecker {
	private static final String LICENCE_KEY = "http://37.139.2.203/client/licence-worldline.txt";

	public static final int OK = 100;
	private static final int MALFORMED_URL = 101;
	private static final int IO_EXCEPT_ON_OPEN_CONNECTION = 102;
	private static final int IO_EXCEPT_ON_GET_RESPONSE_CODE = 103;
	private static final int IO_EXCEPT_ON_GET_INPUT_STREAM = 104;
	private static final int IO_EXCEPT_ON_READLINE = 105;
	private static final int IO_EXCEPT_ON_CLOSE = 106;

	public static final int WRONG_LICENCE = 107;
	private static final String VALID_LICENCE = "OK";

	
	private String line = null;

	public String getLineRead() {
		return line;
	}

	public int checkLicence() {
		URL url;
		HttpURLConnection conn;

		try {
			url = new URL(LICENCE_KEY);
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
				return conn.getResponseCode();
			}
		} catch (IOException e) {
			return IO_EXCEPT_ON_GET_RESPONSE_CODE;
		}

		// Buffer the result into a string
		BufferedReader rd;
		try {
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		} catch (IOException e) {
			return IO_EXCEPT_ON_GET_INPUT_STREAM;
		}
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

		conn.disconnect();

		if (line.equals(VALID_LICENCE)) {
			return OK;
		}
		else {
			return WRONG_LICENCE;
		}
	}

}
