package org.opennms.elastic.httpclient.test;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.opennms.elastic.httpclient.ApacheElasticClient;
import org.opennms.experimental.elastic.ElasticClient;
import org.opennms.experimental.elastic.HarTransformMapper;
import org.opennms.experimental.elastic.OnmsHarPollMetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.gson.JsonArray;

public class AsyncMappingToElasticTest {
	static final Logger LOG = LoggerFactory.getLogger(AsyncMappingToElasticTest.class);

	String elasticUrl = "http://localhost:9200";
	String indexName = "onmshardata";
	String indexType = "onmshartype";
	ElasticClient elasticClient;
	private String username = null;
	private String password = null;

	@Before
	public void before() {
		elasticClient = new ApacheElasticClient(elasticUrl, indexName, indexType, username, password);
	}

	//@Test
	public void testDelete() {
		elasticClient.deleteIndex();
	}

	@Test
	public void testMapping() throws JsonProcessingException, IOException {
		File inputFile = new File("./src/main/resources/onmshartypemapping-0-1.json");
		LOG.debug("reading type inputFile from :" + inputFile.getAbsolutePath());

		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonTypeMapping = mapper.readTree(inputFile);
		
		elasticClient.putTypeMapping(jsonTypeMapping);

		LOG.debug("Waiting for responses");
		/* Pause for 10 seconds */
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			LOG.debug("sleep interrupted");
		}

	}

	@After
	public void after() {
		elasticClient.stop();
	}
}
