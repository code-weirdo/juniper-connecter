package com.github.nicholas.prozesky.juniper.connecter.comms;

import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.github.nicholas.prozesky.juniper.connecter.app.JuniperConnecterApplication;
import com.github.nicholas.prozesky.juniper.connecter.app.JuniperConnecterEvent;
import com.github.nicholas.prozesky.juniper.connecter.settings.JuniperConnecterSettings;
import com.github.nicholas.prozesky.juniper.connecter.utils.ThreadUtils;

/**
 * The communicator is responsible for talking to the VPN web site, logging the
 * user in and getting the DSID that is then provided as a cookie. The DSID is
 * then used by the network connect process to connect to the VPN.
 */
@Component
public class JuniperConnecterCommunicater {

	private ApplicationContext applicationContext;
	private WebDriver webDriver;
	private JuniperConnecterSettings settings;
	private JuniperConnecterApplication application;
	private JuniperLoginPage currentPage = JuniperLoginPage.NONE;

	private static final String LOGIN_FORM = "frmLogin";
	private static final String OTP_FORM = "frmDefender";
	private static final String CONFIRM_FORM = "frmConfirmation";
	private static final String GRAB_FORM = "frmGrab";
	private static final String INVALID_USERNAME_PASSWORD = "Invalid username or password";
	private static final String TOKEN = "TOKEN";

	private enum LoginPageType {
		LOGIN_ENTRY, LOGIN_INVALID_USERNAME_PASSWORD
	}

	private enum OtpPageType {
		OTP_ENTRY, OTP_INVALID_USERNAME_PASSWORD
	}

	@Autowired
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Autowired
	public void setWebDriver(WebDriver driver) {
		this.webDriver = driver;
	}

	@Autowired
	public void setSettings(JuniperConnecterSettings settings) {
		this.settings = settings;
	}

	public void setApplication(JuniperConnecterApplication application) {
		this.application = application;
	}

	public JuniperLoginPage getCurrentPage() {
		return currentPage;
	}

	public void connect() {
		Thread connectThread = new Thread(() -> {
			webDriver.get("https://" + settings.get("host"));
			notifyApplicationOfEvent();
		});
		connectThread.start();
		boolean requestOkay = ThreadUtils.join(connectThread, 5000);
		if (!requestOkay) {
			application.notifyEvent(JuniperConnecterEvent.EVENT_COMMUNICATOR_TIMEOUT);
		}
	}

	public List<String> getRealms() {
		List<WebElement> elements = webDriver.findElements(By.xpath("//*[contains(@id, 'option_LoginPage')]"));
		List<String> realms = elements.stream(). //
				map(elemen -> elemen.getAttribute("value")). //
				collect(Collectors.toList());
		return realms;
	}

	public void login(String username, String password, String realm) {
		selectRealm(realm);
		setUsername(username);
		setPassword(password);
		submit();
		notifyApplicationOfEvent();
	}

	public void submitOneTimePin(String oneTimePin) {
		setPassword(oneTimePin);
		submit();
		notifyApplicationOfEvent();
	}

	public void sumbitConfirm() {
		WebElement element = webDriver
				.findElement(By.xpath("//*[contains(@type, 'submit') and contains(@id, 'btnContinue')]"));
		element.click();
		notifyApplicationOfEvent();
	}

	public String getDSID() {
		return webDriver.manage().getCookieNamed("DSID").getValue();
	}

	public void disconnect() {
		currentPage = JuniperLoginPage.NONE;
		webDriver = (WebDriver) applicationContext.getBean("webDriver");
	}

	private void selectRealm(String realm) {
		Select select = new Select(webDriver.findElement(By.xpath("//*/select")));
		select.selectByVisibleText(realm);
	}

	private void setUsername(String username) {
		WebElement element = webDriver.findElement(By.name("username"));
		element.sendKeys(username);
	}

	private void setPassword(String password) {
		WebElement element = webDriver.findElement(By.name("password"));
		element.sendKeys(password);
	}

	private void submit() {
		WebElement element = webDriver.findElement(By.xpath("//*[contains(@type, 'submit')]"));
		element.click();
	}

	private void notifyApplicationOfEvent() {
		currentPage = queryDriverForCurrentPage();
		switch (currentPage) {
		case LOGIN_PAGE:
			LoginPageType loginPageType = getLoginPageType();
			if (loginPageType == LoginPageType.LOGIN_ENTRY) {
				application.notifyEvent(JuniperConnecterEvent.EVENT_COMMUNICATOR_LOGIN);
			} else if (loginPageType == LoginPageType.LOGIN_INVALID_USERNAME_PASSWORD) {
				application.notifyEvent(JuniperConnecterEvent.EVENT_COMMINICATOR_INVALID_USERNAME_PASSWORD);
			}
			break;
		case CONFIRM_CONTINUE_PAGE:
			application.notifyEvent(JuniperConnecterEvent.EVENT_COMMUNICATOR_CONFIRM);
			break;
		case ONE_TIME_PIN_PAGE:
			OtpPageType otpPageType = getOneTimePinPageType();
			if (otpPageType == OtpPageType.OTP_ENTRY) {
				application.notifyEvent(JuniperConnecterEvent.EVENT_COMMUNICATOR_ONE_TIME_PIN);
			} else if (otpPageType == OtpPageType.OTP_INVALID_USERNAME_PASSWORD) {
				application.notifyEvent(JuniperConnecterEvent.EVENT_COMMINICATOR_INVALID_USERNAME_PASSWORD);
			}
			break;
		case LOGIN_COMPLETE_PAGE:
			application.notifyEvent(JuniperConnecterEvent.EVENT_COMMUNICATOR_LOGIN_SUCCESSFUL);
			break;
		case NONE:
			application.notifyEvent(JuniperConnecterEvent.EVENT_COMMUNICATOR_INVALID_URL);
			break;
		}
	}

	private JuniperLoginPage queryDriverForCurrentPage() {
		try {
			WebElement formElement = webDriver.findElement(By.xpath("//*/form"));
			String form = formElement.getAttribute("name");
			if (LOGIN_FORM.equals(form)) {
				return JuniperLoginPage.LOGIN_PAGE;
			} else if (OTP_FORM.equals(form)) {
				return JuniperLoginPage.ONE_TIME_PIN_PAGE;
			} else if (CONFIRM_FORM.equals(form)) {
				return JuniperLoginPage.CONFIRM_CONTINUE_PAGE;
			} else if (GRAB_FORM.equals(form)) {
				return JuniperLoginPage.LOGIN_COMPLETE_PAGE;
			} else {
				return JuniperLoginPage.NONE;
			}
		} catch (Exception exception) {
			return JuniperLoginPage.NONE;
		}
	}

	private LoginPageType getLoginPageType() {
		try {
			String challenge = webDriver.findElement(By.xpath("//*[contains(@id, 'table_LoginPage_5')]/tbody/tr/td"))
					.getText();
			if (challenge.contains(INVALID_USERNAME_PASSWORD)) {
				return LoginPageType.LOGIN_INVALID_USERNAME_PASSWORD;
			}
		} catch (Exception exception) {
			return LoginPageType.LOGIN_ENTRY;
		}
		System.out.println("DON'T KNOW THIS ONE YET...\n" + webDriver.getPageSource());
		return LoginPageType.LOGIN_ENTRY;
	}

	private OtpPageType getOneTimePinPageType() {
		String challenge = webDriver.findElement(By.xpath("//*[contains(@id, 'table_Defender_6')]/tbody/tr/td"))
				.getText();
		if (challenge.contains(TOKEN)) {
			return OtpPageType.OTP_ENTRY;
		} else if (challenge.contains(INVALID_USERNAME_PASSWORD)) {
			return OtpPageType.OTP_INVALID_USERNAME_PASSWORD;
		}
		System.out.println("DON'T KNOW THIS ONE YET...\n" + webDriver.getPageSource());
		return OtpPageType.OTP_ENTRY;

	}

}
