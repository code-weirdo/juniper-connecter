package com.github.nicholas.prozesky.juniper.connecter.ui;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.github.nicholas.prozesky.juniper.connecter.app.JuniperConnecterApplication;
import com.github.nicholas.prozesky.juniper.connecter.app.JuniperConnecterEvent;

public class JuniperConnecterSessionDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JuniperConnecterApplication application;
	private JTextField dsid;
	private JButton closeButton;

	public JuniperConnecterSessionDialog() {
		setTitle("Session Info");
		setLayout(null);
		setSize(400, 60);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
			}
		});

		JLabel dsidLabel = new JLabel("DSID");
		dsidLabel.setBounds(5, 5, 75, 20);
		add(dsidLabel);

		dsid = new JTextField();
		dsid.setBounds(80, 5, 315, 20);
		dsid.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
		dsid.setEditable(false);
		add(dsid);

		closeButton = new JButton("Close");
		closeButton.setBounds(5, 30, 390, 25);
		closeButton.addActionListener(e -> fireEvent(JuniperConnecterEvent.EVENT_SESSION_CLOSE));
		add(closeButton);

		getRootPane().setDefaultButton(closeButton);
	}

	public void setApplication(JuniperConnecterApplication application) {
		this.application = application;
	}

	public void makeVisible() {
		setSize(400, 60);
		setVisible(true);
	}

	public void setDsid(String dsid) {
		this.dsid.setText(dsid);
	}

	private void fireEvent(JuniperConnecterEvent event) {
		if (application != null) {
			application.notifyEvent(event);
			setVisible(false);
		}
	}
}
