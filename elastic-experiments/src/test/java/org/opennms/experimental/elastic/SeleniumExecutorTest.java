package org.opennms.experimental.elastic;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class SeleniumExecutorTest {

	SeleniumExecutor seleniumExecutor = null;

	@Before
	public void before() {
		seleniumExecutor = new SeleniumExecutor();

	}
	
	@Test
	public void test1() {
		test("http://tmf656-test1.centralus.cloudapp.azure.com:8080/tmf656-simulator-war/");
	}
	
	@Test
	public void test2() {
		test("https://www.bbc.co.uk/");
	}

	public void test(String url) {
		String har = seleniumExecutor
				.executeSelenium(url);
		System.out.println("har file: " + har);
		
		ObjectMapper mapper = new ObjectMapper();

		String json;
		try {
			JsonNode harJsonNode = mapper.readTree(har);
			json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(harJsonNode);
			System.out.println(json);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@After
	public void after() {
		if (seleniumExecutor != null)
			seleniumExecutor.stop();
	}

}
