package com.github.nicholas.prozesky.juniper.connecter;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import com.github.nicholas.prozesky.juniper.connecter.ui.JuniperConnecterSystemTray;

/**
 * Serves at the entry point into the application.
 */
@Component
public class JuniperConnecter {

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		if (!JuniperConnecter.isSupported()) {
			System.out.println("Juniper Connecter is not supported on this machine, unfortunately.");
			return;
		}
		ApplicationContext applicationContext = new AnnotationConfigApplicationContext("com.github.nicholas.prozesky.juniper.connecter");
		applicationContext.getBean("juniperConnecter");
	}

	protected static boolean isSupported() {
		return JuniperConnecterSystemTray.isSupported();
	}
	
}
