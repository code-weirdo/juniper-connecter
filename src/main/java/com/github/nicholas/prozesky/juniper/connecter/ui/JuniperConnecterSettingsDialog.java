package com.github.nicholas.prozesky.juniper.connecter.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.nicholas.prozesky.juniper.connecter.app.JuniperConnecterApplication;
import com.github.nicholas.prozesky.juniper.connecter.app.JuniperConnecterEvent;
import com.github.nicholas.prozesky.juniper.connecter.settings.JuniperConnecterSettings;

public class JuniperConnecterSettingsDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private JuniperConnecterApplication application;
	private JuniperConnecterSettings settings;
	private JTextField host;
	private JButton okayButton;
	private JButton cancelButton;
	
	public JuniperConnecterSettingsDialog() {
		setTitle("Juniper Connecter Settings");
		setLayout(null);
		setSize(300, 60);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
			}
		});

		JLabel hostLabel = new JLabel("Host");
		hostLabel.setBounds(5, 5, 50, 20);
		add(hostLabel);
		
		host = new JTextField();
		host.setBounds(50, 5, 245, 20);
		host.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
		host.getDocument().addDocumentListener(new InternalDocumentListener());
		add(host);
		
		okayButton = new JButton("OK");
		okayButton.setBounds(5, 30, 140, 25);
		okayButton.setEnabled(false);
		okayButton.addActionListener(this::saveAction);
		add(okayButton);
		
		cancelButton = new JButton("Cancel");
		cancelButton.setBounds(155, 30, 140, 25);
		cancelButton.addActionListener(this::cancelEvent);
		add(cancelButton);
		
		getRootPane().setDefaultButton(okayButton);

	}
	
	@Autowired
	public void setSettings(JuniperConnecterSettings settings) {
		this.settings = settings;
	}

	public void setApplication(JuniperConnecterApplication application) {
		this.application = application;
	}

	public void makeVisible() {
		SwingUtilities.invokeLater(() -> {
			host.setText(settings.get("host"));
			setVisible(true);
		});
	}
	private void saveAction(ActionEvent event) {
		okayButton.setEnabled(false);
		setVisible(false);
		String hostName = host.getText();
		settings.update("host", hostName);
		application.notifyEvent(JuniperConnecterEvent.EVENT_HOST_SETTINGS_UPDATED);
	}

	private void cancelEvent(ActionEvent event) {
		host.setText(settings.get("host"));
		setVisible(false);
		setSize(300, 60);
	}
	
	private class InternalDocumentListener implements DocumentListener {

		@Override
		public void changedUpdate(DocumentEvent event) {
			okayButton.setEnabled(!host.getText().equals(settings.get("host")));
		}

		@Override
		public void insertUpdate(DocumentEvent event) {
			okayButton.setEnabled(!host.getText().equals(settings.get("host")));
		}

		@Override
		public void removeUpdate(DocumentEvent event) {
			okayButton.setEnabled(!host.getText().equals(settings.get("host")));
		}
		
	}
}
