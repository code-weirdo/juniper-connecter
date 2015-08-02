package com.github.nicholas.prozesky.juniper.connecter.utils;

public class ThreadUtils {

	public static void sleep(long milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException exception) {
		}
	}
	
	public static boolean join(Thread thread, long milliseconds) {
		try {
			thread.join(milliseconds);
		} catch (InterruptedException exception) {
			return false;
		}
		if (thread.isAlive()) {
			thread.interrupt();
			return false;
		}
		return true;
	}

}
