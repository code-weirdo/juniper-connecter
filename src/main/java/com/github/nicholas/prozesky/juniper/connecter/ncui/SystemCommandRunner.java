package com.github.nicholas.prozesky.juniper.connecter.ncui;

import java.io.PrintWriter;
import java.util.List;

import com.github.nicholas.prozesky.juniper.connecter.utils.ThreadUtils;

public class SystemCommandRunner {

	private Process process;
	private SystemStreamHandler inputStreamHandler;
	private SystemStreamHandler errorStreamHandler;
	private PrintWriter prompt;
	private int exitCode = -1;

	public void execute(List<String> commands) {
		ProcessBuilder processBuilder = new ProcessBuilder().command(commands);
		try {
			process = processBuilder.start();
			inputStreamHandler = new SystemStreamHandler(process.getInputStream());
			errorStreamHandler = new SystemStreamHandler(process.getErrorStream());
			prompt = new PrintWriter(process.getOutputStream());
			startStreamHandlers();
			new Thread(() -> {
				try {
					exitCode = process.waitFor();
				} catch (InterruptedException exception) {
					throw new RuntimeException("Process thread was interrupted!");
				} finally {
					closeStreamHandler(inputStreamHandler);
					closeStreamHandler(errorStreamHandler);
					prompt.close();
				}
			}).start();
		} catch (Exception exception) {
			throw new RuntimeException();
		}
	}

	public void waitFor() {
		while (isRunning()) {
			ThreadUtils.sleep(100);
		}
	}

	public List<String> getErrorStreamOutput() {
		return errorStreamHandler.getStreamOutput();
	}

	public List<String> getInputStreamOutput() {
		return inputStreamHandler.getStreamOutput();
	}

	public void terminate() {
		if (isRunning()) {
			process.destroyForcibly();
		}
	}

	public void writeln(String text) {
		prompt.write(text + System.getProperty("line.separator"));
		prompt.flush();
	}

	public boolean isRunning() {
		return (process != null) && process.isAlive();
	}

	public int getExitCode() {
		return exitCode;
	}

	private void startStreamHandlers() {
		inputStreamHandler.start();
		errorStreamHandler.start();
	}

	private void closeStreamHandler(SystemStreamHandler streamHandler) {
		try {
			streamHandler.interrupt();
			streamHandler.join();
			streamHandler.closeStream();
		} catch (InterruptedException exception) {
			throw new RuntimeException("InterruptedException when trying to close SystemStreamHandler");
		}
	}
}
