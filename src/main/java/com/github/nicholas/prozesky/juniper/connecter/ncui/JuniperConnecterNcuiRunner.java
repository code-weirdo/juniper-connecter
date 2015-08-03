package com.github.nicholas.prozesky.juniper.connecter.ncui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.nicholas.prozesky.juniper.connecter.app.JuniperConnecterApplication;
import com.github.nicholas.prozesky.juniper.connecter.app.JuniperConnecterEvent;
import com.github.nicholas.prozesky.juniper.connecter.settings.JuniperConnecterSettings;
import com.github.nicholas.prozesky.juniper.connecter.utils.ThreadUtils;

/**
 * The runner is the component launches and monitors the network connect
 * process. Network Connect is the client application that Juniper provides. To
 * make this work, we need a DSID that we've fetched by logging into the VPN web
 * site.
 */
@Component
public class JuniperConnecterNcuiRunner {

	private JuniperConnecterApplication application;
	private JuniperConnecterSettings settings;
	private SystemCommandRunner commandRunner;
	private String dsid = null;

	@Autowired
	public void setSettings(JuniperConnecterSettings settings) {
		this.settings = settings;
	}

	public void setApplication(JuniperConnecterApplication application) {
		this.application = application;
	}

	public void setDsid(String dsid) {
		this.dsid = dsid;
	}

	public void startNcui(String adminPassword) {
		commandRunner = new SystemCommandRunner();
		commandRunner.execute(createCommand());
		commandRunner.writeln(adminPassword); // Feed to ncui
		application.notifyEvent(JuniperConnecterEvent.EVENT_NCUI_STARTED);
		createMonitoringThread().start();
	}

	public void terminate() {
		if (commandRunner != null) {
			commandRunner.terminate();
		}
	}

	private List<String> createCommand() {
		String home = System.getProperty("user.home");
		String executionPath = home + File.separator + ".juniper_networks" + File.separator + "network_connect";
		List<String> commands = new ArrayList<>();
		commands.add(executionPath + File.separator + "ncui");
		commands.add("-h");
		commands.add(settings.get("host"));
		commands.add("-c");
		commands.add("DSID=" + dsid);
		commands.add("-f");
		commands.add(executionPath + File.separator + "ssl.crt");
		return commands;
	}

	private Thread createMonitoringThread() {
		return new Thread(() -> {
			while (commandRunner.isRunning()) {
				ThreadUtils.sleep(500);
			}
			ThreadUtils.sleep(100);
			application.notifyEvent(JuniperConnecterEvent.EVENT_NCUI_STOPPED);
		});
	}

}
