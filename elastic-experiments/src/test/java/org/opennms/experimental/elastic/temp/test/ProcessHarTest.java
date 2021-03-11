package org.opennms.experimental.elastic.temp.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;
import org.opennms.harmapper.OnmsHarPollMetaData;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.IntNode;
import com.schibsted.spt.data.jslt.Parser;
import com.schibsted.spt.data.jslt.Expression;

public class ProcessHarTest {

	@Test
	public void test1() throws JsonProcessingException, IOException {
		System.out.println("****************Test ONE");

		File inputFile = new File("./src/test/resources/testfiles/test_har_1.json");
		System.out.println("reading inputFile from :" + inputFile.getAbsolutePath());

		File transformFile = new File("./src/test/resources/testfiles/transform1.jslt");
		System.out.println("reading transform file from :" + transformFile.getAbsolutePath());

		ObjectMapper mapper = new ObjectMapper();
		JsonNode input = mapper.readTree(inputFile);

		Expression jslt = Parser.compile(transformFile);
		JsonNode output = jslt.apply(input);

		String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(output);
		System.out.println(json);

	}

	@Test
	public void test2() throws JsonProcessingException, IOException {

		System.out.println("****************Test TWO");

		File inputFile = new File("./src/test/resources/testfiles/test_har_1.json");
		System.out.println("reading inputFile from :" + inputFile.getAbsolutePath());

		File transformFile = new File("./src/test/resources/testfiles/transform2.jslt");
		System.out.println("reading transform file from :" + transformFile.getAbsolutePath());

		ObjectMapper mapper = new ObjectMapper();
		JsonNode input = mapper.readTree(inputFile);

		Expression jslt = Parser.compile(transformFile);

		// injecting values
		OnmsHarPollMetaData onmsHarPollMetaData = new OnmsHarPollMetaData();
		JsonNode pollMetadata = mapper.convertValue(onmsHarPollMetaData, JsonNode.class);

		Map<String, JsonNode> injectedValues = new LinkedHashMap<>();

		injectedValues.put("pollMetadata", pollMetadata);

		JsonNode output = jslt.apply(injectedValues, input);

		String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(output);
		System.out.println(json);

	}

	@Test
	public void test3() throws JsonProcessingException, IOException {

		System.out.println("****************Test THREE");

		File inputFile = new File("./src/test/resources/testfiles/test_har_1-small.json");
		System.out.println("reading inputFile from :" + inputFile.getAbsolutePath());

		File transformFile = new File("./src/test/resources/testfiles/transform3.jslt");
		System.out.println("reading transform file from :" + transformFile.getAbsolutePath());

		ObjectMapper mapper = new ObjectMapper();
		JsonNode input = mapper.readTree(inputFile);

		Expression jslt = Parser.compile(transformFile);

		// injecting values
		OnmsHarPollMetaData onmsHarPollMetaData = new OnmsHarPollMetaData();
		JsonNode pollMetadata = mapper.convertValue(onmsHarPollMetaData, JsonNode.class);

		Map<String, JsonNode> injectedValues = new LinkedHashMap<>();

		injectedValues.put("pollMetadata", pollMetadata);

		JsonNode output = jslt.apply(injectedValues, input);

		String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(output);
		System.out.println(json);

		File outputFile = new File("./target/test3.json");
		BufferedWriter writer = null;
		
		try {
			outputFile.delete();
			writer = new BufferedWriter(new FileWriter(outputFile));
			writer.write(json);
		} catch (Exception ex) {
			throw new RuntimeException("cannot write to file: " + outputFile.getAbsolutePath(), ex);
		} finally {
			if (writer != null)
				writer.close();
		}

	}

}
