package com.github.nicholas.prozesky.juniper.connecter.app;

public enum JuniperConnecterEvent {
	
	// Events generated by the system tray component
	EVENT_TRAY_SETTINGS,
	EVENT_TRAY_CONNECT,
	EVENT_TRAY_DISCONNECT,
	EVENT_EXIT,
	
	// Events generated by the settings dialog component
	EVENT_HOST_SETTINGS_UPDATED,
	
	// Events generated by the connect dialog component
	EVENT_CONNECT_OKAY,
	EVENT_CONNECT_CANCELED,
	EVENT_CONNECT_ONE_TIME_PIN_OKAY,
	EVENT_CONNECT_ONE_TIME_PIN_CANCELED,
	
	// Events generated by the admin dialog component
	EVENT_ADMIN_OKAY,
	EVENT_ADMIN_CANCELED,
	
	EVENT_COMMUNICATOR_TIMEOUT,
	EVENT_COMMUNICATOR_INVALID_URL,
	EVENT_COMMUNICATOR_LOGIN,
	EVENT_COMMUNICATOR_ONE_TIME_PIN,
	EVENT_COMMUNICATOR_CONFIRM,
	EVENT_COMMUNICATOR_LOGIN_SUCCESSFUL,
	
	EVENT_NCUI_STARTED,
	EVENT_NCUI_STOPPED
}
