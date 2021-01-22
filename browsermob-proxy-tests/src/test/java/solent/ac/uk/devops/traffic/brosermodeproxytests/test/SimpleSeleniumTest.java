package solent.ac.uk.devops.traffic.brosermodeproxytests.test;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.proxy.CaptureType;
import org.junit.Test;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

//import org.openqa.selenium.Proxy;
//import org.openqa.selenium.WebDriver;
//import org.openqa.selenium.firefox.FirefoxDriver;
//import org.openqa.selenium.remote.CapabilityType;
//import org.openqa.selenium.remote.DesiredCapabilities;
/**
 *
 * @author cgallen
 */
public class SimpleSeleniumTest {

    @Test
    public void test1() {

        FileOutputStream fos = null;
        BrowserMobProxy proxy = null;
        WebDriver driver = null;

        try {
            // get geko driver from https://github.com/mozilla/geckodriver/releases
            //if you didn't update the Path system variable to add the full directory path to the executable as above mentioned then doing this directly through code
            System.setProperty("webdriver.gecko.driver", "C:\\devel\\geckodriver.exe");

            File f = new File("target/test_har.har");
            f.delete();
            System.out.println("**************** har file: " + f.getAbsolutePath());

            // start the proxy
            proxy = new BrowserMobProxyServer();
            //CaptureType.getAllContentCaptureTypes()
            proxy.setHarCaptureTypes(CaptureType.REQUEST_HEADERS, CaptureType.RESPONSE_HEADERS);

            proxy.start(0);
            System.out.println("***************** BrowserMobProxyServer started: ");

            // get the Selenium proxy object
            Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);

            // configure it as a desired capability
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability(CapabilityType.PROXY, seleniumProxy);
            capabilities.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);

            // start the browser up
            driver = new FirefoxDriver(capabilities);

            // time out for script should not be longer than page load timout.
            driver.manage().timeouts().setScriptTimeout(20, TimeUnit.SECONDS);
            driver.manage().timeouts().pageLoadTimeout(90, TimeUnit.SECONDS);

            // enable more detailed HAR capture, if desired (see CaptureType for the complete list)
            proxy.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);

            // create a new HAR with the label "uk.yahoo.com"
            proxy.newHar("www.bbc.co.uk");

            System.out.println("***************** driver configured - getting site: ");

            // open yahoo.com
            driver.get("https://www.bbc.co.uk/");

            System.out.println("***************** driver get complete - writing har ");

            // get the HAR data
            Har har = proxy.getHar();
            proxy.endHar();

            fos = new FileOutputStream(f);
            har.writeTo(fos);

            System.out.println("***************** waiting 90 seconds");
            try {
                Thread.sleep(90000); // wait 30 secs
            } catch (InterruptedException e) {

            }
            System.out.println("***************** shutting down server");
            proxy.stop();
            System.out.println("***************** shutting down driver");
            driver.quit();

        } catch (Exception ex) {
            System.out.println("***************** ERROR INITIALISING");
            ex.printStackTrace();
        } finally {
            if ((proxy != null) && proxy.isStarted()) {
                try {
                    proxy.stop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (driver != null) {
                driver.quit();
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        System.out.println("***************** TEST COMPLETE ");
    }
}
