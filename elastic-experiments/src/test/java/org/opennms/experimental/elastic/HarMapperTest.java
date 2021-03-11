package org.opennms.experimental.elastic;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.opennms.harmapper.HarTransformMapper;
import org.opennms.harmapper.OnmsHarPollMetaData;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class HarMapperTest {
	
	@Test
	public void testHarMapping() throws IOException {
		
		System.out.println("****************Test Har Mapping");

		File inputFile = new File("./src/test/resources/testfiles/test_har_1.json");
		System.out.println("reading inputFile from :" + inputFile.getAbsolutePath());
		
		ObjectMapper mapper = new ObjectMapper();
		
		HarTransformMapper harMapper = new HarTransformMapper();
		
		OnmsHarPollMetaData metaData = new OnmsHarPollMetaData();

		JsonNode input = mapper.readTree(inputFile);
		
		ArrayNode output =  harMapper.transform(input, metaData);
		
		System.out.println("child nodes"+ output.size());
		
		String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(output);
		System.out.println(json);
		
	}

}
