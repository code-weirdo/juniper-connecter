package com.github.nicholas.prozesky.juniper.connecter.app;

import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.nicholas.prozesky.juniper.connecter.comms.JuniperConnecterCommunicater;
import com.github.nicholas.prozesky.juniper.connecter.ncui.JuniperConnecterNcuiRunner;
import com.github.nicholas.prozesky.juniper.connecter.settings.JuniperConnecterSettings;
import com.github.nicholas.prozesky.juniper.connecter.ui.JuniperConnecterAdminDialog;
import com.github.nicholas.prozesky.juniper.connecter.ui.JuniperConnecterConnectDialog;
import com.github.nicholas.prozesky.juniper.connecter.ui.JuniperConnecterSettingsDialog;
import com.github.nicholas.prozesky.juniper.connecter.ui.JuniperConnecterSystemTray;
import com.github.nicholas.prozesky.juniper.connecter.ui.JuniperConnecterConnectDialog.View;

@Component
public class JuniperConnecterApplication {

	private JuniperConnecterSettings settings;
	private JuniperConnecterSystemTray systemTray;
	private JuniperConnecterSettingsDialog settingsDialog;
	private JuniperConnecterConnectDialog connectDialog;
	private JuniperConnecterAdminDialog adminDialog;
	private JuniperConnecterCommunicater communicater;
	private JuniperConnecterNcuiRunner ncuiRunner;

	@Autowired
	public void setSettings(JuniperConnecterSettings settings) {
		this.settings = settings;
		this.settings.load();
	}

	@Autowired
	public void setSystemTray(JuniperConnecterSystemTray systemTray) {
		this.systemTray = systemTray;
		this.systemTray.setApplication(this);
	}

	@Autowired
	public void setSettingsDialog(JuniperConnecterSettingsDialog settingsDialog) {
		this.settingsDialog = settingsDialog;
		this.settingsDialog.setApplication(this);
	}

	@Autowired
	public void setConnectDialog(JuniperConnecterConnectDialog connectDialog) {
		this.connectDialog = connectDialog;
		this.connectDialog.setApplication(this);
	}

	@Autowired
	public void setAdminDialog(JuniperConnecterAdminDialog adminDialog) {
		this.adminDialog = adminDialog;
		this.adminDialog.setApplication(this);
	}

	@Autowired
	public void setCommunicater(JuniperConnecterCommunicater communicater) {
		this.communicater = communicater;
		this.communicater.setApplication(this);
	}

	@Autowired
	public void setNcuiRunner(JuniperConnecterNcuiRunner ncuiRunner) {
		this.ncuiRunner = ncuiRunner;
		this.ncuiRunner.setApplication(this);
	}

	public JuniperConnecterSettings getSettings() {
		return settings;
	}

	public void notifyEvent(JuniperConnecterEvent event) {
		switch (event) {
		// SYSTEM TRAY
		case EVENT_EXIT:
			System.exit(0);
			break;
		case EVENT_TRAY_SETTINGS:
			settingsDialog.makeVisible();
			break;
		case EVENT_TRAY_CONNECT:
			showConnectionDialog();
			break;
		case EVENT_TRAY_DISCONNECT:
			ncuiRunner.terminate();
			break;
		// SETTINGS DIALOG
		case EVENT_HOST_SETTINGS_UPDATED:
			settings.save();
			break;
		// EVENT DIALOG
		case EVENT_CONNECT_OKAY:
			loginInBackground();
			break;
		case EVENT_CONNECT_CANCELED:
			communicater.disconnect();
			break;
		case EVENT_CONNECT_ONE_TIME_PIN_OKAY:
			String oneTimePin = connectDialog.getOneTimePin();
			sendOneTimePinInBackground(oneTimePin);
			break;
		case EVENT_CONNECT_ONE_TIME_PIN_CANCELED:
			communicater.disconnect();
			break;
		// COMMUNICATER
		case EVENT_COMMUNICATOR_TIMEOUT:
			break;
		case EVENT_COMMUNICATOR_INVALID_URL:
			systemTray.showMessage("Could not connect...");
			SwingUtilities.invokeLater(() -> connectDialog.setVisible(false));
			break;
		case EVENT_COMMUNICATOR_LOGIN:
			showConnectionDialog();
			break;
		case EVENT_COMMUNICATOR_ONE_TIME_PIN:
			SwingUtilities.invokeLater(this::showConnectionDialog);
			break;
		case EVENT_COMMUNICATOR_CONFIRM:
			SwingUtilities.invokeLater(communicater::sumbitConfirm);
			break;
		case EVENT_COMMUNICATOR_LOGIN_SUCCESSFUL:
			String dsid = communicater.getDSID();
			ncuiRunner.setDsid(dsid);
			adminDialog.makeVisible();
			break;
		// ADMIN
		case EVENT_ADMIN_OKAY:
			String adminPassword = adminDialog.getPassword();
			ncuiRunner.startNcui(adminPassword);
			break;
		case EVENT_ADMIN_CANCELED:
			communicater.disconnect();
			break;
		// RUNNER
		case EVENT_NCUI_STARTED:
			systemTray.setConnected(true);
			break;
		case EVENT_NCUI_STOPPED:
			communicater.disconnect();
			systemTray.setConnected(false);
			break;
		}
	}

	private void showConnectionDialog() {
		switch (communicater.getCurrentPage()) {
		case NONE:
			connectDialog.makeVisible(View.CONNECTING);
			connectInBackground();
			break;
		case LOGIN_PAGE:
			connectDialog.makeVisible(View.LOGIN);
			updateRealmsInBackground();
			break;
		case ONE_TIME_PIN_PAGE:
			connectDialog.makeVisible(View.ONE_TIME_PIN);
			break;
		case CONFIRM_CONTINUE_PAGE:
			break;
		case LOGIN_COMPLETE_PAGE:
			break;
		}
	}

	public void connectInBackground() {
		new SwingWorker<Void, Void>() {
			protected Void doInBackground() throws Exception {
				communicater.connect();
				return null;
			}
		}.execute();
	}

	private void updateRealmsInBackground() {
		SwingWorker<List<String>, Void> worker = new SwingWorker<List<String>, Void>() {
			@Override
			protected List<String> doInBackground() throws Exception {
				List<String> realms = communicater.getRealms();
				return realms;
			}

			@Override
			public void done() {
				try {
					List<String> realms = get();
					connectDialog.setRealms(realms);
				} catch (Exception exception) {
					System.out.println("Couldn't fetch the realm information.");
				}
			}
		};
		worker.execute();
	}

	public void loginInBackground() {
		new SwingWorker<Void, Void>() {
			protected Void doInBackground() throws Exception {
				String username = connectDialog.getUsername();
				String password = connectDialog.getPassword();
				String realm = connectDialog.getRealm();
				communicater.login(username, password, realm);
				return null;
			}
		}.execute();
	}

	public void sendOneTimePinInBackground(String oneTimePin) {
		new SwingWorker<Void, Void>() {
			protected Void doInBackground() throws Exception {
				communicater.submitOneTimePin(oneTimePin);
				return null;
			}
		}.execute();
	}

}
