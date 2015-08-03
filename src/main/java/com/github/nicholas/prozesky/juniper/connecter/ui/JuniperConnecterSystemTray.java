package com.github.nicholas.prozesky.juniper.connecter.ui;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

import org.springframework.stereotype.Service;

import com.github.nicholas.prozesky.juniper.connecter.app.JuniperConnecterApplication;
import com.github.nicholas.prozesky.juniper.connecter.app.JuniperConnecterEvent;

@Service
public class JuniperConnecterSystemTray {

	JuniperConnecterApplication application;
	final Image CONNECTED_ICON = createIcon("vpn-connected.png");
	final Image DISCONNECTED_ICON = createIcon("vpn-disconnected.png");

	final SystemTray tray;
	final TrayIcon trayIcon;
	final PopupMenu popupMenu;
	MenuItem connectItem;
	MenuItem disconnectItem;
	MenuItem settingsItem;
	MenuItem exitItem;

	public JuniperConnecterSystemTray() {
		connectItem = new MenuItem("Connect");
		connectItem.addActionListener(e -> fireEvent(JuniperConnecterEvent.EVENT_TRAY_CONNECT));
		disconnectItem = new MenuItem("Disconnect");
		disconnectItem.addActionListener(e -> fireEvent(JuniperConnecterEvent.EVENT_TRAY_DISCONNECT));
		disconnectItem.setEnabled(false);
		settingsItem = new MenuItem("Settings");
		settingsItem.addActionListener(e -> fireEvent(JuniperConnecterEvent.EVENT_TRAY_SETTINGS));
		exitItem = new MenuItem("Exit");
		exitItem.addActionListener(e -> fireEvent(JuniperConnecterEvent.EVENT_EXIT));

		this.popupMenu = new PopupMenu();
		popupMenu.add(connectItem);
		popupMenu.add(disconnectItem);
		popupMenu.add(settingsItem);
		popupMenu.addSeparator();
		popupMenu.add(exitItem);

		this.trayIcon = new TrayIcon(DISCONNECTED_ICON);
		this.trayIcon.setImageAutoSize(false);
		this.trayIcon.setPopupMenu(this.popupMenu);
		this.tray = SystemTray.getSystemTray();
		try {
			this.tray.add(trayIcon);
		} catch (AWTException exception) {
			throw new RuntimeException("Juniper Connecter is not supported on this machine, unfortunately.");
		}
	}

	public void setApplication(JuniperConnecterApplication application) {
		this.application = application;
	}

	public void setConnected(boolean connected) {
		SwingUtilities.invokeLater(() -> {
			connectItem.setEnabled(!connected);
			disconnectItem.setEnabled(connected);
			trayIcon.setImage(connected ? CONNECTED_ICON : DISCONNECTED_ICON);
		});
	}

	public void showMessage(String message) {
		trayIcon.displayMessage("Info Message", message, TrayIcon.MessageType.INFO);
	}

	public void hide() {
		tray.remove(trayIcon);
	}

	public static boolean isSupported() {
		return SystemTray.isSupported();
	}

	private Image createIcon(String name) {
		try {
			return ImageIO.read(ClassLoader.getSystemResource(name));
		} catch (IOException exception) {
			throw new RuntimeException("Missing image! " + name);
		}
	}

	private void fireEvent(JuniperConnecterEvent event) {
		if (application != null) {
			application.notifyEvent(event);
		}
	}

}
