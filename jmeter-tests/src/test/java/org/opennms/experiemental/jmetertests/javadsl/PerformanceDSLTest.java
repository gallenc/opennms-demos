package org.opennms.experiemental.jmetertests.javadsl;

import static org.junit.Assert.*;
import static us.abstracta.jmeter.javadsl.JmeterDsl.*;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import org.junit.Test;

import org.eclipse.jetty.http.MimeTypes.Type;
import us.abstracta.jmeter.javadsl.core.TestPlanStats;

/**
 *  see java dsl https://github.com/abstracta/jmeter-java-dsl Simple Java API to run performance tests, 
* using JMeter as engine, in an VCS (versioning control system) and programmers friendly way.
*/
public class PerformanceDSLTest {

	@Test
	public void testPerformance() throws IOException {
		TestPlanStats stats = testPlan(
				threadGroup(2, 1, httpSampler("http://tmf656-test1.centralus.cloudapp.azure.com:8080"
						+ "/tmf656-simulator-war/tmf-api/serviceProblemManagement/v3/generic-hub/allSubscriptionStatistics")),

				// httpSampler("http://my.service").post("{\"name\": \"test\"}",
				// Type.APPLICATION_JSON)),
				// this is just to log details of each request stats
				jtlWriter("./target/test" + Instant.now().toString().replace(":", "-") + ".jtl")).run();

		assertTrue(stats.overall().elapsedTimePercentile99().compareTo(Duration.ofSeconds(5)) < 0);
	}

}