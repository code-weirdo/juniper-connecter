package com.github.nicholas.prozesky.juniper.connecter.ncui;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class SystemStreamHandler extends Thread {
	InputStream inputStream;
	List<String> streamOutput = new LinkedList<>();

	public SystemStreamHandler(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	@Override
	public void run() {
		InputStreamReader streamReader = new InputStreamReader(inputStream);
		try (BufferedReader reader = new BufferedReader(streamReader)) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				streamOutput.add(line);
			}
		} catch (IOException ioException) {
		}
	}

	public List<String> getStreamOutput() {
		List<String> result = streamOutput.stream() //
				.map(line -> new String(line)) //
				.collect(Collectors.toList());
		return result;
	}

	public void closeStream() {
		try {
			inputStream.close();
		} catch (IOException exception) {
			System.out.println("IOException while closing stream");
		}
	}
}
