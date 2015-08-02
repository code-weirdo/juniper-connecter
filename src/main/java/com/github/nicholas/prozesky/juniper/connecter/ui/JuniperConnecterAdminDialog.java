package com.github.nicholas.prozesky.juniper.connecter.ui;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.SwingUtilities;

import com.github.nicholas.prozesky.juniper.connecter.app.JuniperConnecterApplication;
import com.github.nicholas.prozesky.juniper.connecter.app.JuniperConnecterEvent;

public class JuniperConnecterAdminDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JuniperConnecterApplication application;
	private JPasswordField password;
	private JButton okayButton;
	private JButton cancelButton;

	public JuniperConnecterAdminDialog() {
		setTitle("Administrator Password");
		setLayout(null);
		setSize(300, 60);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
			}
		});

		JLabel passwordLabel = new JLabel("Password");
		passwordLabel.setBounds(5, 5, 75, 20);
		add(passwordLabel);

		password = new JPasswordField();
		password.setBounds(80, 5, 215, 20);
		password.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
		add(password);

		okayButton = new JButton("OK");
		okayButton.setBounds(5, 30, 140, 25);
		okayButton.addActionListener(e -> fireEvent(JuniperConnecterEvent.EVENT_ADMIN_OKAY));
		add(okayButton);

		cancelButton = new JButton("Cancel");
		cancelButton.setBounds(155, 30, 140, 25);
		cancelButton.addActionListener(e -> {
			SwingUtilities.invokeLater(() -> setVisible(false));
			fireEvent(JuniperConnecterEvent.EVENT_ADMIN_CANCELED);
		});
		add(cancelButton);

		getRootPane().setDefaultButton(okayButton);
	}

	public void setApplication(JuniperConnecterApplication application) {
		this.application = application;
	}

	public void makeVisible() {
		SwingUtilities.invokeLater(() -> {
			password.setText("");
			setVisible(true);
		});
	}

	public String getPassword() {
		return new String(password.getPassword());
	}

	private void fireEvent(JuniperConnecterEvent event) {
		if (application != null) {
			application.notifyEvent(event);
			setVisible(false);
		}
	}
}
