package com.github.nicholas.prozesky.juniper.connecter;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.github.nicholas.prozesky.juniper.connecter.app.JuniperConnecterApplication;
import com.github.nicholas.prozesky.juniper.connecter.comms.JuniperConnecterCommunicater;
import com.github.nicholas.prozesky.juniper.connecter.ncui.JuniperConnecterNcsvcRunner;
import com.github.nicholas.prozesky.juniper.connecter.ncui.JuniperConnecterNcuiRunner;
import com.github.nicholas.prozesky.juniper.connecter.settings.JuniperConnecterSettings;
import com.github.nicholas.prozesky.juniper.connecter.ui.JuniperConnecterAdminDialog;
import com.github.nicholas.prozesky.juniper.connecter.ui.JuniperConnecterConnectDialog;
import com.github.nicholas.prozesky.juniper.connecter.ui.JuniperConnecterSettingsDialog;

@Configuration
public class JuniperConnecterConfig {

	@Bean
	@Scope("singleton")
	public JuniperConnecterApplication juniperConnecter() {
		return new JuniperConnecterApplication();
	}

	@Bean
	@Scope("singleton")
	public JuniperConnecterSettings settings() {
		return new JuniperConnecterSettings();
	}

	@Bean
	@Scope("singleton")
	public JuniperConnecterSettingsDialog settingsDialog() {
		return new JuniperConnecterSettingsDialog();
	}

	@Bean
	@Scope("singleton")
	public JuniperConnecterConnectDialog connectDialog() {
		return new JuniperConnecterConnectDialog();
	}

	@Bean
	@Scope("singleton")
	public JuniperConnecterAdminDialog adminDialog() {
		return new JuniperConnecterAdminDialog();
	}

	@Bean
	@Scope("singleton")
	public JuniperConnecterCommunicater communicater() {
		return new JuniperConnecterCommunicater();
	}

	@Bean
	@Scope("singleton")
	public JuniperConnecterNcuiRunner ncuiRunner() {
		return new JuniperConnecterNcuiRunner();
	}

	@Bean
	@Scope("singleton")
	public JuniperConnecterNcsvcRunner ncsvcRunner() {
		return new JuniperConnecterNcsvcRunner();
	}

	@Bean
	@Scope("prototype")
	public WebDriver webDriver() {
		return new HtmlUnitDriver();
	}
}
