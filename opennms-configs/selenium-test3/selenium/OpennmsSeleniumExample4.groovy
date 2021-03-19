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
// DEMONSTRATES USING BMP TO CAPTURE HAR and transfer to elastic search
package selenium;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.firefox.FirefoxDriverLogLevel;
import org.opennms.core.logging.Logging;
import org.opennms.elastic.client.ElasticClient;
import org.opennms.elastic.httpclient.ApacheElasticClient;
import org.opennms.harmapper.HarTransformMapper;
import org.opennms.harmapper.OnmsHarPollMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import org.openqa.selenium.Proxy;

import net.lightbody.bmp.BrowserMobProxy;
import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.client.ClientUtil;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.proxy.CaptureType;
import net.lightbody.bmp.mitm.TrustSource;

class OpennmsSeleniumExample {
	private Logger LOG = LoggerFactory.getLogger("selenium");

	private String baseUrl = "http://www.papajohns.co.uk/";
	private int timeout = 10;
	private StringBuffer verificationErrors = new StringBuffer();

	private WebDriver driver = null;
	private BrowserMobProxy proxy = null;
	private HarTransformMapper harTransformMapper = null;
	private ApacheElasticClient elasticClient = null;

	private String harMapperJsltFileName = null;
	private String seleniumElasticUrl = null;
	private String seleniumElasticPassword = null;
	private String seleniumElasticUsername = null;
	private String opennmsHome = null;
	private String seleniumElasticIndexName = null;
	private String seleniumElasticIndexType = null;

	public OpennmsSeleniumExample(String url, int timeoutInSeconds) {
		baseUrl = url;
		timeout = timeoutInSeconds;
		
		//TODO these need replaced in opennms.properties
		System.setProperty("webdriver.gecko.driver", "/opt/geckodriver/geckodriver");
        System.setProperty("harMapperJsltFileName", "hartransform-0-1.jslt");
        System.setProperty("seleniumElasticUrl", "http://localhost:9200");
        //System.setProperty("seleniumElasticUsername", null);
        //System.setProperty("seleniumElasticPassword", null);
        System.setProperty("seleniumElasticIndexName", "onmshardata");
        System.setProperty("seleniumElasticIndexType", "onmshartype");
		
	}

	@Before
	public void setUp() throws Exception {
		Logging.withPrefix("selenium_groovey", new Runnable() {

			public void run() {
				try {
					LOG.debug("setUp() starting selenium script baseUrl=" + baseUrl + " timeout=" + timeout);

					harMapperJsltFileName = System.getProperty("harMapperJsltFileName", "hartransform-0-1.jslt");
					seleniumElasticUrl = System.getProperty("seleniumElasticUrl");
					seleniumElasticUsername = System.getProperty("seleniumElasticUsername");
					seleniumElasticPassword = System.getProperty("seleniumElasticPassword");
					seleniumElasticIndexName = System.getProperty("seleniumElasticIndexName");
					seleniumElasticIndexType = System.getProperty("seleniumElasticIndexType");

					opennmsHome = System.getProperty("opennms.home");

					LOG.debug("setUp() system settings harMapperJsltFileName =" + harMapperJsltFileName
							+ " seleniumElasticUrl=" + seleniumElasticUrl + " seleniumElasticUsername="
							+ seleniumElasticUsername + " seleniumElasticPassword =" + seleniumElasticPassword
							+ " opennmsHome=" + opennmsHome);

					File mappingFile = new File(opennmsHome+"/etc/selenium/"+ harMapperJsltFileName);
					LOG.debug("testSelenium mappingfile location:"+mappingFile.getAbsolutePath());
					harTransformMapper = new HarTransformMapper(mappingFile);

					if (seleniumElasticUrl == null) {
						LOG.debug("testSelenium elastic not configured so not setting up elasticClient");
					} else {
						LOG.debug("testSelenium writing to elastic");
						elasticClient = new ApacheElasticClient(seleniumElasticUrl, seleniumElasticIndexName,
								seleniumElasticIndexType, seleniumElasticUsername, seleniumElasticPassword);
					}

				} catch (Throwable ex) {
					LOG.error("setUp() selenium script exception ", ex);
				}
			}
		});
	}

	@Test
	public void testSelenium() throws Exception {

		Logging.withPrefix("selenium_groovey", new Runnable() {

			public void run() {
				try {
					LOG.debug("testSelenium() running selenium test baseUrl=" + baseUrl);

					LOG.debug("setting up browsermob proxy");
					// start the proxy
					proxy = new BrowserMobProxyServer();
					// CaptureType.getAllContentCaptureTypes()
					proxy.setHarCaptureTypes(CaptureType.REQUEST_HEADERS, CaptureType.RESPONSE_HEADERS);

					proxy.start(0);
					LOG.debug("***************** BrowserMobProxyServer started: ");

					// get the Selenium proxy object
					Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);

					FirefoxBinary firefoxBinary = new FirefoxBinary();
					LOG.debug("setUp() HEADLESS");
					firefoxBinary.addCommandLineOptions("--headless");

					LOG.debug("setUp() BMP PROXY");

					FirefoxOptions firefoxOptions = new FirefoxOptions();
					// set up proxy
					firefoxOptions.setCapability(CapabilityType.PROXY, seleniumProxy);
					firefoxOptions.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);

					LOG.debug("setUp() FIREFOX BINARY LOG LEVEL TRACE");
					firefoxOptions.setBinary(firefoxBinary);
					firefoxOptions.setLogLevel(FirefoxDriverLogLevel.TRACE);
					LOG.debug("setUp() 4");

					driver = new FirefoxDriver(firefoxOptions);
					LOG.debug("setUp() 5");
					// driver = new FirefoxDriver();
					// driver.manage().timeouts().implicitlyWait(timeout, TimeUnit.SECONDS);
					LOG.debug("setUp() 6");

					driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

					LOG.debug("setUp() driver started");

					LOG.debug("testSelenium() CREATE NEW HAR NAME=" + baseUrl);
					proxy.newHar(baseUrl);

					// open | / |
					LOG.debug("testSelenium() driver=" + driver);

					driver.get(baseUrl);

					LOG.debug("testSelenium() finished getting base url");

					// click | link=Our Story |
					// driver.findElement(By.linkText("Latest Offers")).click();

					// assertText | link=Contact Us | Contact Us
					// assertEquals("Contact Us", driver.findElement(By.linkText("Contact
					// Us")).getText());
					// assertEquals("Contact Us", driver.findElement(By.linkText("Contact
					// Us")).getText())
					// fail("selenium test failed script");

					LOG.debug("testSelenium() Get the HAR data");
					Har har = proxy.getHar();
					LOG.debug("har:" + har);
					proxy.endHar();

					StringWriter writer = new StringWriter();
					try {
						har.writeTo(writer);
					} catch (IOException e) {
						LOG.error("testSelenium() HAR WRITING EXCEPTION ", e);
					}
					String harString = writer.toString();
					LOG.debug("testSelenium() HAR data: " + harString);

					ObjectMapper mapper = new ObjectMapper();

					String json = null;
					JsonNode harJsonNode = null;

					OnmsHarPollMetaData pollMetaData = new OnmsHarPollMetaData();

					ArrayNode jsonArrayData = null;
					try {
						harJsonNode = mapper.readTree(harString);
						jsonArrayData = harTransformMapper.transform(harJsonNode, pollMetaData);
						LOG.debug("testSelenium transformed har into array of " + jsonArrayData.size() + " objects :");
						json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(harJsonNode);
						LOG.debug(json);
					} catch (JsonProcessingException e) {
						LOG.error("testSelenium problem parsing har file", e);
					}

					if (elasticClient == null) {
						LOG.debug("testSelenium elastic not configured not writing to elastic search");
					} else {
						LOG.debug("testSelenium writing to elastic");
						elasticClient.sendBulkJsonArray(jsonArrayData);
					}

				} catch (java.lang.AssertionError ae) {
					LOG.debug("testSelenium() selenium poller assertion error: ", ae);
					throw ae;
				} catch (Throwable ex) {
					LOG.error("testSelenium() selenium script exception ", ex);
				}
			}
		});
	}

	@After
	public void tearDown() throws Exception {

		if (driver != null)
			driver.quit();

		if ((proxy != null) && proxy.isStarted()) {
			try {
				proxy.stop();
			} catch (Exception e) {
				LOG.error("testSelenium() PROXY STOPPING EXCEPTION ", e);
			}
		}

		String verificationErrorString = verificationErrors.toString();
		if (!"".equals(verificationErrorString)) {
			fail(verificationErrorString);
		}
	}
}
