package org.opennms.harmapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.schibsted.spt.data.jslt.Expression;
import com.schibsted.spt.data.jslt.Parser;

public class HarTransformMapper {

	private final String DEFAULT_JSTL_HAR_TRANSFORM_FILE = "hartransform-0-1.jslt";

	private final ObjectMapper mapper = new ObjectMapper();

	private final Expression jslt;

	public HarTransformMapper() {
		String jsltTransform;
		try {
			jsltTransform = getResourceFileAsString(DEFAULT_JSTL_HAR_TRANSFORM_FILE);
			jslt = Parser.compileString(jsltTransform);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public HarTransformMapper(String jsltTransform) {
		jslt = Parser.compileString(jsltTransform);
	};

	public HarTransformMapper(File jstlTransformFile) {
		jslt = Parser.compile(jstlTransformFile);
	};

	public JsonNode transform(String inputString, Object metaData)
			throws JsonMappingException, JsonProcessingException {
		JsonNode input = mapper.readTree(inputString);
		return transform(input, metaData);
	}

	public JsonNode transform(File inputFile, Object metaData) throws IOException {
		JsonNode input = mapper.readTree(inputFile);
		return transform(input, metaData);
	}

	public ArrayNode transform(JsonNode input, Object pollMetaData) throws JsonMappingException, JsonProcessingException {
		ArrayNode  output;

		// injecting values
		if (pollMetaData != null) {
			JsonNode jsonMetaData = mapper.convertValue(pollMetaData, JsonNode.class);
			Map<String, JsonNode> injectedValues = new LinkedHashMap<>();
			injectedValues.put("pollMetaData", jsonMetaData);
			output = (ArrayNode) jslt.apply(injectedValues, input);
		} else {
			output = (ArrayNode) jslt.apply(input);
		}
		return output;
	}

	private static String getResourceFileAsString(String fileName) throws IOException {
		ClassLoader classLoader = ClassLoader.getSystemClassLoader();
		try (InputStream is = classLoader.getResourceAsStream(fileName)) {
			if (is == null)
				return null;
			try (InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
					BufferedReader reader = new BufferedReader(isr)) {
				return reader.lines().collect(Collectors.joining(System.lineSeparator()));
			}
		}
	}

}
