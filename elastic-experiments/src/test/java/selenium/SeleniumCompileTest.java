package selenium;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.codehaus.groovy.control.CompilationFailedException;
import org.junit.Test;

import groovy.lang.GroovyClassLoader;

import java.io.File;
import java.io.IOException;

public class SeleniumCompileTest {

    @Test
    public void test() {

        try {
            createGroovyClass("OpennmsSeleniumHarCollectorV3.groovy");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Class<?> createGroovyClass(String filename) throws CompilationFailedException, IOException {
        GroovyClassLoader gcl = new GroovyClassLoader();

        String file = "src/test/resources/groovy/" + filename;
        File f = new File(file);
        System.out.println(f.getAbsolutePath());
        return gcl.parseClass(f);
    }

}
