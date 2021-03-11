package org.opennms.experimental.elastic;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.gson.JsonArray;


public class HarToElasticTest {
	
	String elasticUrl = "http://localhost:9200"; 
	String indexName = "onmshardata";
	String indexType = "onmshartype";
	ElasticClient elasticClient;
	
	@Before
	public void before() {
		elasticClient = new JestElasticClient(elasticUrl,indexName, indexType);
	}
	
	@Test
	public void test() throws JsonProcessingException, IOException {
		File inputFile = new File("./src/test/resources/testfiles/test_har_1.json");
		System.out.println("reading inputFile from :" + inputFile.getAbsolutePath());
		
		ObjectMapper mapper = new ObjectMapper();
		
		HarTransformMapper harMapper = new HarTransformMapper();
		
		OnmsHarPollMetaData metaData = new OnmsHarPollMetaData();

		JsonNode input = mapper.readTree(inputFile);
		
		ArrayNode jsonArrayData = harMapper.transform(input, metaData);
		
		System.out.println("transformed har into array of "+ jsonArrayData.size() + " objects :");
		
		elasticClient.sendBulkJsonArray(jsonArrayData);
		
	}
}
