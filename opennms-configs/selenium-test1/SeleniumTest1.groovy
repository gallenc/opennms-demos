package selenium;

import static org.junit.Assert.*

import java.util.concurrent.TimeUnit

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.openqa.selenium.By
import org.openqa.selenium.WebDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxOptions;

import org.opennms.netmgt.poller.monitors.SeleniumMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class SeleniumTest1  {
    Logger LOG = LoggerFactory.getLogger(SeleniumMonitor.class);

    private WebDriver driver;
    private String baseUrl="";
    private int timeout = 30;
    private StringBuffer verificationErrors = new StringBuffer();
    
    public SeleniumTest1(String url, int timeoutInSeconds, MonitoredService svc, Map<String, Object> parameters) {
        baseUrl = url;
        timeout = timeoutInSeconds;
    }
    
    @Before
    public void setUp() throws Exception {
       LOG.debug("setting up Firefox Driverxx");
       System.out.println("setting up Firefox Driverxx");
//       FirefoxBinary firefoxBinary = new FirefoxBinary();
  //     firefoxBinary.addCommandLineOptions("--headless");
    //   FirefoxOptions firefoxOptions = new FirefoxOptions();
    //   firefoxOptions.setBinary(firefoxBinary);

      //  driver = new FirefoxDriver(firefoxOptions);
      //  driver.manage().timeouts().implicitlyWait(timeout, TimeUnit.SECONDS);
    }

    @Test
    public void testSelenium() throws Exception {
        // open | / |
       LOG.error("xxselinium test timout="+timeout+" baseurl="+baseurl);
       System.out.println("xxselinium test timout="+timeout+" baseurl="+baseurl);
       //driver.get(baseUrl);
       // driver.findElement(By.linkText("Service Problems")).click();
	
    }

    @After
    public void tearDown() throws Exception {
        //driver.quit();
       // String verificationErrorString = verificationErrors.toString();
       // if (!"".equals(verificationErrorString)) {
       //     fail(verificationErrorString);
       // }
   LOG.error("end selinium test timout="+timeout+" baseurl="+baseurl);
       System.out.println("end xxselinium test timout="+timeout+" baseurl="+baseurl);

    }
    
    
}
