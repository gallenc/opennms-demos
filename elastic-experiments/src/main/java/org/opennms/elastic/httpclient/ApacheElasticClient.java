package org.opennms.elastic.httpclient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import io.searchbox.core.Index;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.opennms.experimental.elastic.ElasticClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ApacheElasticClient implements ElasticClient {
	static final Logger LOG = LoggerFactory.getLogger(ApacheElasticClient.class);

	private String elasticUrl = "http://localhost:9200";
	private String indexType = null;
	private String indexName = null;
	private String username = null;
	private String password = null;

	private ApacheHttpAsyncClient asyncClient = null;

	public ApacheElasticClient(String elasticUrl, String indexName, String indexType, String username,
			String password) {
		this.elasticUrl = elasticUrl;
		this.indexType = indexType;
		this.indexName = indexName;
		this.username = username;
		this.password = password;

		asyncClient = new ApacheHttpAsyncClient();
		asyncClient.startListener();
		asyncClient.startClient();

	}

	/**
	 * curl -X POST "localhost:9200/_bulk?pretty" -H 'Content-Type:
	 * application/json' -d' { "index" : { "_index" : "test", "_id" : "1" } } {
	 * "field1" : "value1" } { "delete" : { "_index" : "test", "_id" : "2" } } {
	 * "create" : { "_index" : "test", "_id" : "3" } } { "field1" : "value3" } {
	 * "update" : {"_id" : "1", "_index" : "test"} } { "doc" : {"field2" : "value2"}
	 * } '
	 * 
	 */

	@Override
	public void sendBulkJsonArray(ArrayNode jsonArrayData) {
		StringBuilder sb = new StringBuilder();

		// see https://queirozf.com/entries/elasticsearch-bulk-inserting-examples
		
		// String commandStr = "{\"index\":{\"_index\":\"" + indexName + "\",\"_type\":\"" + indexType + "\"}}\n";
		String commandStr = "{\"index\":{}\n";
		for (JsonNode data : jsonArrayData) {
			sb.append(commandStr + data.toString() + "\n");
		}
		// must have extra \n at the end of the last document
		sb.append("\n");

		String jsonMessage = sb.toString();
		LOG.debug("*** sending data:\n" + jsonMessage);

		// using default indexName and defaultIndexType
		asyncClient.postRequest(elasticUrl + "/"+indexName+"/"+indexType+"/_bulk", jsonMessage, username, password, "application/x-ndjson",
				"application/json");

		// see
		// https://github.com/searchbox-io/Jest/blob/master/jest-common/src/test/java/io/searchbox/core/BulkTest.java
		// Index.Builder(source).index("twitter").type("tweet").id("1").build()
		// {\"index\":{\"_id\":\"1\",\"_index\":\"twitter\",\"_type\":\"tweet\"}}\n
		// "{\"field\":\"value\"}\n" +
	}

	@Override
	public void deleteIndex() {
		asyncClient.deleteRequest(elasticUrl + "/" + indexName, username, password);
	}
	
	@Override
	public void putTypeMapping(JsonNode jsonTypeMapping) {
		String typeMapping = jsonTypeMapping.toString();
		LOG.debug("*** sending type mapping data:\n" + typeMapping );
		asyncClient.putRequest(elasticUrl + "/" + indexName+"",typeMapping  , username, password);

	}

	@Override
	public synchronized void stop() {
		if (asyncClient != null) {
			asyncClient.stopClient();
			asyncClient.stopListener();
			asyncClient = null;
		}

	}

}
