
package org.opennms.elastic.httpclient.test;

import org.apache.http.config.SocketConfig;
import org.slf4j.Logger;

import org.slf4j.LoggerFactory;

import org.apache.http.ConnectionClosedException;
import org.apache.http.ExceptionLogger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opennms.elastic.httpclient.ApacheHttpAsyncClient;

import java.io.IOException;
import java.net.SocketTimeoutException;

public class AsyncClientTest {
	static final Logger LOG = LoggerFactory.getLogger(AsyncClientTest.class);

	private int port = 8681;
	// String baseUrl;

	private HttpServer m_server;

	private String[] m_allowedTargets = null;

	@Before
	public void before() {

		LOG.debug("create test http server");
		try {
			SocketConfig socketConfig = SocketConfig.custom().setSoTimeout(15000).setTcpNoDelay(true).build();

			m_server = ServerBootstrap.bootstrap().setListenerPort(port).setServerInfo("Test/1.1")
					.setSocketConfig(socketConfig).setExceptionLogger(new ExceptionLogger() {

						public void log(Exception ex) {
							if (ex instanceof SocketTimeoutException) {
								LOG.error("Connection timed out http server: " + ex.toString());
							} else if (ex instanceof ConnectionClosedException) {
								LOG.error("Connection closed in http server: " + ex.toString());
							} else {
								LOG.error("Exception in http server: " + ex.toString());
							}
						}

					}).registerHandler("*", new HttpRequestHandler() {

						@Override
						public void handle(HttpRequest request, HttpResponse response, HttpContext context)
								throws HttpException, IOException {

							LOG.debug("received request: " + request.toString());

							response.setStatusCode(HttpStatus.SC_OK);

							// reply with a empty string
							String jsonString = "{}";
							StringEntity entity = new StringEntity(jsonString,
									ContentType.create("application/json", "UTF-8"));
							response.setEntity(entity);
							return;

						}

					}).create();

			m_server.start();
			LOG.debug("server started");

		} catch (Exception ex) {
			LOG.error("problem starting server", ex);
		}
	}

	@After
	public void after() {
		LOG.debug("stopping server");
		if (m_server != null)
			m_server.stop();

	}

	@Test
	public void test1() {
        LOG.debug("Start of test1");
		ApacheHttpAsyncClient asyncClient = null;
		try {

			asyncClient = new ApacheHttpAsyncClient();
			asyncClient.startListener();
			asyncClient.startClient();
			// http://localhost:9200/onmshardata/onmshartype/_bulk
			String url = "http://localhost:" + port + "/test";
			String jsonMessage = "";
			String username = null;
			String password = null;
			asyncClient.postRequest(url, jsonMessage, username, password);
			
	        LOG.debug("Waiting for responses");
	        /* Pause for 10 seconds */
	        try {
	            Thread.sleep(10000);
	        } catch (InterruptedException e) {
	            LOG.debug("sleep interrupted");
	        }
	
			
		} catch (Exception ex) {
			throw ex;
		} finally {
			if (asyncClient != null) {
				asyncClient.stopClient();
				asyncClient.stopListener();
				asyncClient = null;
			}
		}
		
        LOG.debug("End of test1");

	}

}
