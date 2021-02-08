package com.mozilla.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.openqa.selenium.firefox.FirefoxOptions;

import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeadlessFirefoxSeleniumExample {
	public static Logger LOG = LoggerFactory.getLogger(HeadlessFirefoxSeleniumExample.class);

	public static void main(String[] args) {
		System.setProperty("webdriver.gecko.driver", "/opt/geckodriver/geckodriver");
		FirefoxBinary firefoxBinary = new FirefoxBinary();
		firefoxBinary.addCommandLineOptions("--headless");

		FirefoxOptions firefoxOptions = new FirefoxOptions();
		firefoxOptions.setBinary(firefoxBinary); 
		firefoxOptions.setLogLevel(FirefoxDriverLogLevel.TRACE);

		FirefoxDriver driver = new FirefoxDriver(firefoxOptions);
		try {
			// driver.get("http://www.google.com");
			driver.get("http://localhost/");
			driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
//      WebElement queryBox = driver.findElement(By.name("q"));
//      queryBox.sendKeys("headless firefox");
//      WebElement searchBtn = driver.findElement(By.name("btnK"));
//      searchBtn.click();
//      WebElement iresDiv = driver.findElement(By.id("ires"));
//      iresDiv.findElements(By.tagName("a")).get(0).click();
			System.out.println(driver.getPageSource());

			// see
			// https://stackoverflow.com/questions/25431380/capturing-browser-logs-with-selenium-webdriver-using-java

			// should work but problems in firefox https://github.com/mozilla/geckodriver/issues/284
   //   LogEntries logs = driver.manage().logs().get(LogType.BROWSER);
   //   for (LogEntry entry : logs) {
   // 	  LOG.debug("browserlog: "+new Date(entry.getTimestamp()) + " " + entry.getLevel() + " " + entry.getMessage());
    //  }

			System.out.println("END OF TEST");
		} finally {
			driver.quit();
		}
	}
}