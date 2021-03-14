/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2011-2014 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2014 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/
// DEMONSTRATES BASIC HEADLESS POLLING WITH SELENIUM FIREFOX
package selenium;

import static org.junit.Assert.*

import java.util.concurrent.TimeUnit

import org.junit.After
import org.junit.Before
import org.junit.Test

import org.openqa.selenium.WebDriver
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.opennms.core.logging.Logging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



class OpennmsSeleniumExample  {
    
    private WebDriver driver=null;
    private String baseUrl="http://www.papajohns.co.uk/";
    private int timeout = 10;
    private StringBuffer verificationErrors = new StringBuffer();
    private  Logger LOG = LoggerFactory.getLogger("selenium");

    
    public OpennmsSeleniumExample(String url, int timeoutInSeconds) {
        baseUrl = url;
        timeout = timeoutInSeconds;
    }
    
    @Before
    public void setUp() throws Exception {
        Logging.withPrefix("selenium_groovey", new Runnable() {

	    public void run() {
                try {
                   LOG.debug("setUp() starting selenium script baseUrl="+baseUrl+" timeout="+timeout);
                   System.setProperty("webdriver.gecko.driver", "/opt/geckodriver/geckodriver");
                   FirefoxBinary firefoxBinary = new FirefoxBinary();
                   LOG.debug("setUp() 1"); 
                   firefoxBinary.addCommandLineOptions("--headless");
            LOG.debug("setUp() 2"); 
                   
                   FirefoxOptions firefoxOptions = new FirefoxOptions();
            LOG.debug("setUp() 3");
                   firefoxOptions.setBinary(firefoxBinary);
                   firefoxOptions.setLogLevel(FirefoxDriverLogLevel.TRACE);
            LOG.debug("setUp() 4");                    

            driver = new FirefoxDriver(firefoxOptions);
            LOG.debug("setUp() 5"); 
//                   driver = new FirefoxDriver(); 
                   //driver.manage().timeouts().implicitlyWait(timeout, TimeUnit.SECONDS);
            LOG.debug("setUp() 6");

                   driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

                   LOG.debug("setUp() driver started");

                } catch (Throwable ex){
                  LOG.error("setUp() selenium script exception ",ex);
                } 

            }

	});

    }

    @Test
    public void testSelenium() throws Exception {

        Logging.withPrefix("selenium_groovey", new Runnable() {

            public void run() {
                try {
                    LOG.debug("testSelenium() running selenium test baseUrl="+baseUrl);

                    // open | / |
                    LOG.debug("testSelenium() driver="+driver);

                    driver.get(baseUrl);

                    LOG.debug("testSelenium() finished getting base url");

                    // click | link=Our Story |
                    // driver.findElement(By.linkText("Latest Offers")).click();

                    // assertText | link=Contact Us | Contact Us
                    //assertEquals("Contact Us", driver.findElement(By.linkText("Contact Us")).getText());
                    //assertEquals("Contact Us", driver.findElement(By.linkText("Contact Us")).getText())
                    //fail("selenium test failed script");  
                } catch (java.lang.AssertionError ae ) {
                    LOG.debug("testSelenium() selenium poller assertion error: ",ae);
                    throw ae;
                } catch (Throwable ex){
                  LOG.error("testSelenium() selenium script exception ",ex);
                }
               
            }
            
        });


    }

    @After
    public void tearDown() throws Exception {
        if(driver !=null) driver.quit();
        String verificationErrorString = verificationErrors.toString();
        if (!"".equals(verificationErrorString)) {
            fail(verificationErrorString);
        }
    }
    
    
}
