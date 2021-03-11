package org.opennms.elastic.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public interface ElasticClient {

	void sendBulkJsonArray(ArrayNode jsonArrayData);

	void deleteIndex();

	void stop();

	void putTypeMapping(JsonNode jsonTypeMapping);

}