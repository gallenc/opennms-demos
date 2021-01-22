package org.opennms.experiemental.jmetertests.javadsl;

import static org.junit.Assert.*;

import org.junit.Test;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.save.SaveService;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;

import java.io.File;
import java.io.IOException;
import java.time.Instant;

// https://www.blazemeter.com/blog/5-ways-launch-jmeter-test-without-using-jmeter-gui
// https://stackoverflow.com/questions/53806643/how-to-programmatically-run-a-jmeter-benchmark-from-code
public class SimpleJmeterTest {

	@Test
	public void test() throws IOException {
	

        // JMeter Engine
        StandardJMeterEngine jmeter = new StandardJMeterEngine();


        // Initialize Properties, logging, locale, etc.
        JMeterUtils.loadJMeterProperties("C:\\devel\\jmeter\\apache-jmeter-5.4\\bin\\jmeter.properties"); // /path/to/your/jmeter/bin/jmeter.properties
        JMeterUtils.setJMeterHome("C:\\devel\\jmeter\\apache-jmeter-5.4"); // /path/to/your/jmeter
        JMeterUtils.initLocale();

        // Initialize JMeter SaveService
        SaveService.loadProperties();

        // Load existing .jmx Test Plan
        //HashTree testPlanTree = SaveService.loadTree(new File("./src/test/resources/firstTestCraigGallen.jmx")); // /path/to/your/jmeter/extras/Test.jmx
        HashTree testPlanTree = SaveService.loadTree(new File("./src/test/resources/simpleTest.jmx")); // /path/to/your/jmeter/extras/Test.jmx

        Summariser summer = null;
        String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
        if (summariserName.length() > 0) {
            summer = new Summariser(summariserName);
        }


        // Store execution results into a .jtl file
        String logFile = "./target/standardTest" + Instant.now().toString().replace(":", "-") + ".jtl\""; // /path/to/results/file.jtl
        ResultCollector logger = new ResultCollector(summer);
        logger.setFilename(logFile);
        testPlanTree.add(testPlanTree.getArray()[0], logger);

        // Run JMeter Test
        jmeter.configure(testPlanTree);
        jmeter.run();

    }
}


