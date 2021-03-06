package org.opennms.experimental.selenium;

//Generated by Selenium IDE
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Alert;
import org.openqa.selenium.Keys;
import java.util.*;
import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class Test1Test {
	private WebDriver driver;
	private Map<String, Object> vars;
	JavascriptExecutor js;

	@Before
	public void setUp() {
		FirefoxBinary firefoxBinary = new FirefoxBinary();
		firefoxBinary.addCommandLineOptions("--headless");
		FirefoxOptions firefoxOptions = new FirefoxOptions();
		firefoxOptions.setBinary(firefoxBinary);


		System.setProperty("webdriver.gecko.driver", "C:\\devel\\geckodriver.exe");
		
		// run headless see https://developer.mozilla.org/en-US/docs/Mozilla/Firefox/Headless_mode
		driver = new FirefoxDriver(firefoxOptions);
		//driver = new FirefoxDriver();
		js = (JavascriptExecutor) driver;
		vars = new HashMap<String, Object>();
	}

	@After
	public void tearDown() {
		driver.quit();
	}

	@Test
	public void test1() {
		driver.get("http://tmf656-test1.centralus.cloudapp.azure.com:8080/tmf656-simulator-war/");
		driver.manage().window().setSize(new Dimension(1091, 691));
		driver.findElement(By.linkText("Service Problems")).click();
		driver.findElement(By.linkText("Home")).click();
		System.out.println("***************** waiting 20 seconds");
		try {
			Thread.sleep(20000); // wait 20 secs
		} catch (InterruptedException e) {

		}
		System.out.println("***************** shutting downtest");
	}
}
