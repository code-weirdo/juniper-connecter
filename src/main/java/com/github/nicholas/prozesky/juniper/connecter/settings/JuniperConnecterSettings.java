package com.github.nicholas.prozesky.juniper.connecter.settings;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Responsible for saving and loading of a settings file that gets written
 * the the user's home folder: ${user.home}/.juniper_networs/juniper-connector.conf
 */
public class JuniperConnecterSettings {

	private Map<String, String> settings = new HashMap<>();

	public void update(String key, String setting) {
		settings.put(key.toLowerCase(), setting);
	}

	public String get(String key) {
		key = key.toLowerCase();
		if (settings.containsKey(key)) {
			return settings.get(key.toLowerCase());
		}
		return "";
	}

	public void save() {
		File settingsFolder = getSettingsFolder();
		if (!settingsFolder.exists()) {
			settingsFolder.mkdirs();
		}
		File settingsFile = getSettingsFile(settingsFolder);
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(settingsFile))) {
			for (String key : settings.keySet()) {
				writer.write(key + "=" + settings.get(key));
				writer.newLine();
			}
		} catch (IOException exception) {
			System.out.println("Couldn't write settings file");
		}
	}

	public void load() {
		File settingsFolder = getSettingsFolder();
		File settingsFile = getSettingsFile(settingsFolder);
		if (!settingsFile.exists()) {
			return;
		}
		settings.clear();
		try (BufferedReader reader = new BufferedReader(new FileReader(settingsFile))) {
			String line;
			while ((line = reader.readLine()) != null) {
				if ((!"".equals(line)) && (line.contains("="))) {
					String[] parts = line.split("=");
					settings.put(parts[0].toLowerCase(), parts[1]);
				}
			}
		} catch (IOException exception) {
			System.out.println("Couldn't write settings file");
		}

	}

	private File getSettingsFolder() {
		String settingsPath = System.getProperty("user.home") + File.separator + ".juniper_networks";
		File settingsFolder = new File(settingsPath);
		return settingsFolder;
	}

	private File getSettingsFile(File settingsFolder) {
		return new File(settingsFolder, "juniper-connecter.config");
	}

}
