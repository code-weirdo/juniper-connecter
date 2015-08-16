package com.github.nicholas.prozesky.juniper.connecter.ncui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class JuniperConnecterNcsvcRunner {

	private String adminPassword = null;

	public void startNcsvcIfNotRunning(String adminPassword) {
		this.adminPassword = adminPassword;
		if (!isNcsvcRunning()) {
			SystemCommandRunner commandRunner = new SystemCommandRunner();
			commandRunner.execute(createNcsvcCommand());
			commandRunner.writeln(this.adminPassword);
		}
	}

	public void terminateIfRunning() {
		if ((adminPassword == null) || (!isNcsvcRunning())) {
			return;
		}
		SystemCommandRunner commandRunner = new SystemCommandRunner();
		commandRunner.execute(createPsCommand());
		commandRunner.waitFor();
		List<String> output = commandRunner.getInputStreamOutput();
		List<String> process = output.stream() //
				.filter(line -> {
					return !(line.contains("grep") || line.contains("sudo"));
				}) //
				.collect(Collectors.toList());
		if (process.size() != 0) {
			String[] parts = process.get(0).split("\\s+");
			String processId = parts[1];
			commandRunner = new SystemCommandRunner();
			commandRunner.execute(createKillCommand(processId));
			commandRunner.writeln(adminPassword);
			commandRunner.waitFor();
		}
	}

	private boolean isNcsvcRunning() {
		SystemCommandRunner commandRunner = new SystemCommandRunner();
		commandRunner.execute(createPsCommand());
		commandRunner.waitFor();
		List<String> output = commandRunner.getInputStreamOutput();
		List<String> ncsvcProcesses = output.stream() //
				.filter(line -> !line.contains("grep")) //
				.collect(Collectors.toList());
		return ncsvcProcesses.size() > 0;
	}

	private List<String> createPsCommand() {
		List<String> commands = new ArrayList<>();
		commands.add("/bin/bash");
		commands.add("-c");
		commands.add("ps aux | grep ncsvc");
		return commands;
	}

	private List<String> createNcsvcCommand() {
		String home = System.getProperty("user.home");
		String executionPath = home + File.separator + ".juniper_networks" + File.separator + "network_connect";
		List<String> commands = new ArrayList<>();
		commands.add("sudo");
		commands.add("-S");
		commands.add(executionPath + File.separator + "ncsvc");
		return commands;
	}

	private List<String> createKillCommand(String processId) {
		List<String> commands = new ArrayList<>();
		commands.add("sudo");
		commands.add("-S");
		commands.add("kill");
		commands.add(processId);
		return commands;
	}

}
