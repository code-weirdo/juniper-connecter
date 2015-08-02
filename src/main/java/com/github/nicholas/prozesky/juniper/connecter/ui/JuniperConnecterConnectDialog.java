package com.github.nicholas.prozesky.juniper.connecter.ui;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.nicholas.prozesky.juniper.connecter.app.JuniperConnecterApplication;
import com.github.nicholas.prozesky.juniper.connecter.app.JuniperConnecterEvent;
import com.github.nicholas.prozesky.juniper.connecter.settings.JuniperConnecterSettings;

@Component
public class JuniperConnecterConnectDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private JuniperConnecterSettings settings;
	private JuniperConnecterApplication application;

	// Connecting View
	private JPanel connectingPanel;
	private JLabel urlLabel;
	private JTextField url;

	// Login View
	private JPanel loginPanel;
	private JTextField username;
	private JPasswordField password;
	private JComboBox<String> realm;
	private JButton okayButton;
	private JButton cancelButton;

	// One Time Pin View
	private JPanel oneTimePinPanel;
	private JPasswordField oneTimePin;
	private JButton oneTimePinOkayButton;
	private JButton oneTimePinCanelButton;

	public enum View {
		CONNECTING, LOGIN, ONE_TIME_PIN
	}

	public JuniperConnecterConnectDialog() {
		setTitle("Juniper Connecter");
		setLayout(null);
		setSize(300, 115);
		connectingPanel = createConnectingView();
		add(connectingPanel);
		loginPanel = createLoginView();
		add(loginPanel);
		oneTimePinPanel = createOneTimePinView();
		add(oneTimePinPanel);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
			}
		});
	}

	@Autowired
	public void setSettings(JuniperConnecterSettings settings) {
		this.settings = settings;
	}

	public void setApplication(JuniperConnecterApplication application) {
		this.application = application;
	}

	public void makeVisible(View view) {
		SwingUtilities.invokeLater(() -> {
			connectingPanel.setVisible(view == View.CONNECTING);
			loginPanel.setVisible(view == View.LOGIN);
			oneTimePinPanel.setVisible(view == View.ONE_TIME_PIN);
			switch (view) {
			case CONNECTING:
				setSize(300, 55);
				url.setText("https://" + settings.get("host"));
				getRootPane().setDefaultButton(null);
				break;
			case LOGIN:
				password.setText("");
				setSize(300, 115);
				getRootPane().setDefaultButton(okayButton);
				break;
			case ONE_TIME_PIN:
				oneTimePin.setText("");
				setSize(300, 60);
				getRootPane().setDefaultButton(oneTimePinOkayButton);
				break;
			}
			setVisible(true);
		});
	}

	public void setRealms(List<String> realms) {
		SwingUtilities.invokeLater(() -> {
			realm.setModel(new DefaultComboBoxModel<String>(realms.toArray(new String[realms.size()])));
		});
	}

	public String getUsername() {
		return username.getText();
	}

	public String getPassword() {
		return new String(password.getPassword());
	}

	public String getRealm() {
		return realm.getSelectedItem().toString();
	}

	public String getOneTimePin() {
		return new String(oneTimePin.getPassword());
	}

	private JPanel createConnectingView() {
		JPanel connectingView = new JPanel();
		connectingView.setLayout(null);
		connectingView.setBounds(0, 0, 300, 115);

		urlLabel = new JLabel("Attempting to connect to:");
		urlLabel.setBounds(5, 5, 290, 20);
		connectingView.add(urlLabel);

		url = new JTextField();
		url.setBounds(5, 25, 290, 20);
		url.setEditable(false);
		connectingView.add(url);
		return connectingView;
	}

	private JPanel createLoginView() {
		JPanel loginView = new JPanel();
		loginView.setLayout(null);
		loginView.setBounds(0, 0, 300, 115);

		JLabel usernameLabel = new JLabel("Username");
		usernameLabel.setBounds(5, 5, 75, 20);
		loginView.add(usernameLabel);
		username = new JTextField();
		username.setBounds(80, 5, 215, 20);
		username.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
		loginView.add(username);

		JLabel passwordLabel = new JLabel("Password");
		passwordLabel.setBounds(5, 30, 75, 20);
		loginView.add(passwordLabel);
		password = new JPasswordField();
		password.setBounds(80, 30, 215, 20);
		password.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
		loginView.add(password);

		realm = new JComboBox<>();
		realm.setBounds(5, 55, 290, 20);
		loginView.add(realm);

		okayButton = new JButton("Connect");
		okayButton.setBounds(5, 80, 140, 25);
		okayButton.addActionListener(e -> fireEvent(JuniperConnecterEvent.EVENT_CONNECT_OKAY));
		loginView.add(okayButton);

		cancelButton = new JButton("Cancel");
		cancelButton.setBounds(155, 80, 140, 25);
		cancelButton.addActionListener(e -> {
			SwingUtilities.invokeLater(() -> setVisible(false));
			fireEvent(JuniperConnecterEvent.EVENT_CONNECT_CANCELED);
		});
		
		loginView.add(cancelButton);
		return loginView;
	}

	private JPanel createOneTimePinView() {
		JPanel oneTimePinView = new JPanel();
		oneTimePinView.setLayout(null);
		oneTimePinView.setBounds(0, 0, 300, 115);

		JLabel oneTimePinLabel = new JLabel("OTP");
		oneTimePinLabel.setBounds(5, 5, 75, 20);
		oneTimePinView.add(oneTimePinLabel);

		oneTimePin = new JPasswordField();
		oneTimePin.setBounds(80, 5, 215, 20);
		oneTimePin.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1, true));
		oneTimePinView.add(oneTimePin);

		oneTimePinOkayButton = new JButton("OK");
		oneTimePinOkayButton.setBounds(5, 30, 140, 25);
		oneTimePinOkayButton.addActionListener(e -> fireEvent(JuniperConnecterEvent.EVENT_CONNECT_ONE_TIME_PIN_OKAY));
		oneTimePinView.add(oneTimePinOkayButton);

		oneTimePinCanelButton = new JButton("Cancel");
		oneTimePinCanelButton.setBounds(155, 30, 140, 25);
		oneTimePinCanelButton.addActionListener(e -> {
			SwingUtilities.invokeLater(() -> setVisible(false));
			fireEvent(JuniperConnecterEvent.EVENT_CONNECT_ONE_TIME_PIN_CANCELED);
		});
		oneTimePinView.add(oneTimePinCanelButton);
		return oneTimePinView;
	}

	private void fireEvent(JuniperConnecterEvent event) {
		if (application != null) {
			application.notifyEvent(event);
			setVisible(false);
		}
	}

}
