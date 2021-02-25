package org.opennms.experimental.elastict.test;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Index;
import io.searchbox.core.Update;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;

public class ElasticTest1 {

	@Test
	public void test1() {

		JestClient jestClient = jestClient();

		try {
			System.out.println("deleting index webrequests");
			jestClient.execute(new DeleteIndex.Builder("webrequests").build());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			System.out.println("creating index webrequests");
			jestClient.execute(new CreateIndex.Builder("webrequests").build());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

//    	String protocol = "http";
//    	String host = "example.com";
//    	int port = 4567;
//    	String path = "/foldername/1234";
//    	String auth = null;
//    	String fragment = null;
//    	String query;
//		URI uri = new URI(protocol, auth, host, port, path, query, fragment);
//    	URL url = uri.toURL();

		String pollId = UUID.randomUUID().toString();
		for (int i = 1; i < 3; i++) {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.createObjectNode().put("pollid", pollId).put("timestamp", new Date().getTime())
					.put("protocol", "http").put("domainName", "example.com").put("ipaddress", "192.168.0.1")
					.put("port", 80).put("path", "/example").put("query", i);

			try {
				System.out.println("sending to index :" + jsonNode.toString());
				jestClient.execute(new Index.Builder(jsonNode.toString()).index("webrequests").type("test").build());
				//jestClient.execute(new Update.Builder(jsonNode.toString()).index("webrequests").build());

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		try {
			jestClient.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static JestClient jestClient() {
		JestClientFactory factory = new JestClientFactory();
		factory.setHttpClientConfig(new HttpClientConfig.Builder("http://localhost:9200").multiThreaded(true)
				.defaultMaxTotalConnectionPerRoute(2).maxTotalConnection(20).build());
		return factory.getObject();
	}
}
