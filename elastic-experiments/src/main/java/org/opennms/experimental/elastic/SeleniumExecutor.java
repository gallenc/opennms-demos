package org.opennms.experimental.elastic;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.proxy.CaptureType;

public class SeleniumExecutor {
	
	private BrowserMobProxy proxy = null;
	private WebDriver driver;
	private Map<String, Object> vars;
	private JavascriptExecutor js;


	public SeleniumExecutor() {
		
		// setting up browsermob proxy
        // start the proxy
        proxy = new BrowserMobProxyServer();
        //CaptureType.getAllContentCaptureTypes()
        proxy.setHarCaptureTypes(CaptureType.REQUEST_HEADERS, CaptureType.RESPONSE_HEADERS);

        proxy.start(0);
        System.out.println("***************** BrowserMobProxyServer started: ");
        
        // get the Selenium proxy object
        Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);

        // configure it as a desired capability
        //DesiredCapabilities capabilities = new DesiredCapabilities();
       // capabilities.setCapability(CapabilityType.PROXY, seleniumProxy);
       // capabilities.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
		
		
		// setting up firefox headless operation
		FirefoxBinary firefoxBinary = new FirefoxBinary();
		firefoxBinary.addCommandLineOptions("--headless");
		FirefoxOptions firefoxOptions = new FirefoxOptions();
		firefoxOptions.setBinary(firefoxBinary);
		System.setProperty("webdriver.gecko.driver", "C:\\devel\\geckodriver.exe");
		
		// set up proxy
		firefoxOptions.setCapability(CapabilityType.PROXY, seleniumProxy);
		firefoxOptions.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
		
		// run headless see https://developer.mozilla.org/en-US/docs/Mozilla/Firefox/Headless_mode
		driver = new FirefoxDriver(firefoxOptions);
		//driver = new FirefoxDriver();
		js = (JavascriptExecutor) driver;
		vars = new HashMap<String, Object>();
		
	}
	
	public String executeSelenium(String url) {
        // "http://tmf656-test1.centralus.cloudapp.azure.com:8080/tmf656-simulator-war/"
		
		proxy.newHar(url); 
		driver.get(url);
		
        // get the HAR data
        Har har = proxy.getHar();
        proxy.endHar();
        
        StringWriter writer = new StringWriter();
        try {
			har.writeTo(writer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return writer.toString();


	}
	
	public void stop() {
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
            
	}

}
