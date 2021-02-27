package org.opennms.experimental.elastic;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import io.searchbox.indices.DeleteIndex;

public class SeleniumExecutorToElasticTest {
	
	private static final String elasticUrl = "http://localhost:9200";
	private static final String indexName = "onmshardata";
	private static final String indexType = "onmshartype";

	SeleniumExecutor seleniumExecutor = null;

	HarTransformMapper harTransformMapper = null;

	ElasticClient elasticClient;
	
//	@BeforeClass
//	public static void beforeClass() {
//		System.out.println("removing index before test");
//		ElasticClient elasticClient = new ElasticClient(elasticUrl, indexName, indexType);
//		elasticClient.deleteIndex();
//		elasticClient.stop();
//		elasticClient=null;
//	}

	@Before
	public void before() {
		seleniumExecutor = new SeleniumExecutor();
		harTransformMapper = new HarTransformMapper();
		elasticClient = new ElasticClient(elasticUrl, indexName, indexType);
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

		OnmsHarPollMetaData metaData = new OnmsHarPollMetaData();

		// execute selenium test and get har file
		String har = seleniumExecutor.executeSelenium(url);
		System.out.println("har file: " + har);

		ObjectMapper mapper = new ObjectMapper();

		String json = null;
		JsonNode harJsonNode = null;

		HarTransformMapper harMapper = new HarTransformMapper();
		ArrayNode jsonArrayData = null;
		try {
			harJsonNode = mapper.readTree(har);
			json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(harJsonNode);
			System.out.println(json);

			jsonArrayData = harMapper.transform(harJsonNode, metaData);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("transformed har into array of " + jsonArrayData.size() + " objects :");

		elasticClient.sendBulkJsonArray(jsonArrayData);

	}

	@After
	public void after() {
		if (seleniumExecutor != null)
			seleniumExecutor.stop();
		if (elasticClient != null)
			elasticClient.stop();
	}

}
