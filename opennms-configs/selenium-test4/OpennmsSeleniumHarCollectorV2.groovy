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
// DEMONSTRATES USING BMP TO CAPTURE HAR and transform for elastic search v2
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
import org.opennms.elastic.httpclient.ApacheElasticClient;
import org.opennms.harmapper.HarTransformMapper;
import org.opennms.harmapper.OnmsHarPollMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.opennms.netmgt.poller.MonitoredService;
import java.util.Map;

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

class OpennmsSeleniumHarCollector {
	private Logger LOG = LoggerFactory.getLogger("selenium");

	private String baseUrl = "baseUrl-undefined";
	private int timeout = 10;
	private StringBuffer verificationErrors = new StringBuffer();

	private WebDriver driver = null;
	private BrowserMobProxy proxy = null;
	private HarTransformMapper harTransformMapper = null;
	private ApacheElasticClient elasticClient = null;

	private String geckoDriver = null;
	private String harMapperJsltFileName = null;
	private String seleniumElasticUrl = null;
	private String seleniumElasticPassword = null;
	private String seleniumElasticUsername = null;
	private String opennmsHome = null;
	private String seleniumElasticIndexName = null;
	private String seleniumElasticIndexType = null;

	public OpennmsSeleniumHarCollector(String url, int timeoutInSeconds, MonitoredService svc, Map<String, Object> parameters) {
		baseUrl = url;
		timeout = timeoutInSeconds;
	}

    @Before
    public void setUp() throws Exception {

        try {
            LOG.debug("setUp() starting selenium script baseUrl=" + baseUrl + " timeout=" + timeout);

            
            if(System.getProperty("webdriver.gecko.driver")==null){
                System.setProperty("webdriver.gecko.driver", "/opt/geckodriver/geckodriver");
            }
            
            geckoDriver = System.getProperty("webdriver.gecko.driver"); 
            
            harMapperJsltFileName = System.getProperty("harMapperJsltFileName", "hartransform-0-1.jslt");
            seleniumElasticUrl = System.getProperty("seleniumElasticUrl", "http://localhost:9200");
            seleniumElasticUsername = System.getProperty("seleniumElasticUsername", "");
            seleniumElasticPassword = System.getProperty("seleniumElasticPassword", "");
            seleniumElasticIndexName = System.getProperty("seleniumElasticIndexName", "onmshardata");
            seleniumElasticIndexType = System.getProperty("seleniumElasticIndexType", "onmshartype");
            opennmsHome = System.getProperty("opennms.home");

            LOG.debug("setUp() System settings location of webdriver.gecko.driver ="+geckoDriver
                    + " harMapperJsltFileName =" + harMapperJsltFileName + " seleniumElasticUrl="
                    + seleniumElasticUrl + " seleniumElasticUsername=" + seleniumElasticUsername
                    + " seleniumElasticPassword =" + seleniumElasticPassword + " opennmsHome=" + opennmsHome);

            File mappingFile = new File(opennmsHome+"/etc/selenium/"+ harMapperJsltFileName);
            LOG.debug("setUp() mappingfile location:" + mappingFile.getAbsolutePath());

            harTransformMapper = new HarTransformMapper(mappingFile);

            if (seleniumElasticUrl == null | "".equals(seleniumElasticUrl) ) {
                LOG.debug("setUp()  seleniumElasticUrl not configured so not setting up elasticClient");
            } else {
                LOG.debug("setUp() elastic configured");
                elasticClient = new ApacheElasticClient(seleniumElasticUrl, seleniumElasticIndexName,
                        seleniumElasticIndexType, seleniumElasticUsername, seleniumElasticPassword);
            }

            LOG.debug("setUp() setting up browsermob proxy");
            // start the proxy
            proxy = new BrowserMobProxyServer();
            proxy.setHarCaptureTypes(CaptureType.REQUEST_HEADERS, 
                    CaptureType.RESPONSE_HEADERS, 
                    CaptureType.RESPONSE_BINARY_CONTENT,  
                    CaptureType.RESPONSE_CONTENT);
            proxy.setTrustAllServers(true); //note might be better to use cert authorities
            
            proxy.start(0);
            LOG.debug("setUp() BrowserMobProxyServer started: ");

        } catch (Throwable ex) {
            LOG.error("setUp() selenium script exception ", ex);
            
        } 
    }



	@Test
	public void testSelenium() throws Exception {


		try {
			LOG.debug("testSelenium() running selenium test baseUrl=" + baseUrl);

			// get the Selenium proxy object
			Proxy seleniumProxy = ClientUtil.createSeleniumProxy(proxy);

			FirefoxBinary firefoxBinary = new FirefoxBinary();
			firefoxBinary.addCommandLineOptions("--headless");

			FirefoxOptions firefoxOptions = new FirefoxOptions();
			// set up proxy
			firefoxOptions.setCapability(CapabilityType.PROXY, seleniumProxy);
			firefoxOptions.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
			firefoxOptions.setBinary(firefoxBinary);
			firefoxOptions.setLogLevel(FirefoxDriverLogLevel.INFO);

			int implicitlyWait = timeout - 10000; 
			LOG.debug("testSelenium()  create driver: pageloadtimeout ms = "+ timeout+ " implicitlyWait ms= "+implicitlyWait);
			driver = new FirefoxDriver(firefoxOptions);
			driver.manage().deleteAllCookies();
			driver.manage().timeouts().pageLoadTimeout(timeout, TimeUnit.MILLISECONDS);
			driver.manage().timeouts().implicitlyWait(implicitlyWait, TimeUnit.MILLISECONDS);

			LOG.debug("setUp() driver started");

			LOG.debug("testSelenium() create har name=" + baseUrl);
			proxy.newHar(baseUrl);

			LOG.debug("testSelenium() driver get base url=" + baseUrl);

			driver.get(baseUrl);

			LOG.debug("testSelenium() finished getting base url");

			// insert your own tests here if they fail, the test will end and capture will
			// complete

		} catch (java.lang.AssertionError ae) {
			LOG.debug("testSelenium() selenium poller assertion error: ", ae);
			throw ae;

		} catch (Throwable ex) {
			LOG.error("testSelenium() selenium script exception ", ex);

		} finally {

			// at end of tests gather har and complete
			try {

				LOG.debug("testSelenium() Get the HAR data");
				Har har = proxy.getHar();
				LOG.trace("har:" + har);
				proxy.endHar();

				StringWriter writer = new StringWriter();
				try {
					har.writeTo(writer);
				} catch (IOException e) {
					LOG.error("testSelenium() HAR writing exception ", e);
				}

				String harString = writer.toString();

				LOG.trace("testSelenium() HAR data: " + harString);

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
					LOG.trace(json);
				} catch (JsonProcessingException e) {
					LOG.error("testSelenium problem parsing har file", e);
				}

				if (elasticClient == null) {
					LOG.debug("testSelenium elastic not configured not writing to elastic search");
				} else {
					LOG.debug("testSelenium writing to elastic");
					try {
						elasticClient.sendBulkJsonArray(jsonArrayData);
					} catch (Throwable ex) {
						LOG.error("testSelenium() error sending bulk data to elastic ", ex);
					}
				}
			} catch (Throwable th) {
				LOG.error("testSelenium() Error in finalising har processing ", th);
			}
		}
	}


	@After
	public void tearDown() throws Exception {

		if (driver != null) {
			try {
				driver.quit();
			} catch (Exception e) {
				LOG.error("tearDown() driver quit exception ", e);
			}
		}

		if ((proxy != null)) {
			try {
				proxy.stop();
			} catch (Exception e) {
				LOG.error("tearDown() proxy stopping exception ", e);
			}
		}

		String verificationErrorString = verificationErrors.toString();
		if (!"".equals(verificationErrorString)) {
			fail(verificationErrorString);
		}
	}
}
