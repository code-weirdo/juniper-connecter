package com.github.nicholas.prozesky.juniper.connecter.ncui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SystemStreamHandler extends Thread {
	InputStream inputStream;
	String type;

	public SystemStreamHandler(InputStream inputStream, String type) {
		this.inputStream = inputStream;
		this.type = type;
	}

	@Override
	public void run() {
		InputStreamReader streamReader = new InputStreamReader(inputStream);
		try (BufferedReader reader = new BufferedReader(streamReader)) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				System.out.println(type + "> " + line);
			}
		} catch (IOException ioException) {
		}
	}

	public void closeStream() {
		try {
			inputStream.close();
		} catch (IOException exception) {
			System.out.println("IOException while closing stream");
		}
	}
}
