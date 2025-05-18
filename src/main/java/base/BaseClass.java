package base;

import java.net.URL;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseClass {

    // ThreadLocal WebDriver for thread-safe driver instances in parallel tests
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    // Method to get the current thread's driver
    protected WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    // Setup method using System properties for flexibility
    @BeforeMethod(alwaysRun = true)
    public void setup() throws Exception {
        String environment = System.getProperty("environment", "local");
        String browser = System.getProperty("browser", "chrome");
        String remoteUrl = System.getProperty("remoteUrl", "");

        WebDriver driver;

        if ("local".equalsIgnoreCase(environment)) {
            driver = createLocalDriver(browser);
        } else if ("remote".equalsIgnoreCase(environment)) {
            if (remoteUrl.isEmpty()) {
                throw new IllegalArgumentException("Remote URL required for remote environment");
            }
            driver = createRemoteDriver(browser, remoteUrl);
            System.out.println("Using remoteUrl: " + remoteUrl);
        } else {
            throw new IllegalArgumentException("Unknown environment: " + environment);
        }

        driverThreadLocal.set(driver);
    }

    private WebDriver createLocalDriver(String browser) {
        if ("chrome".equalsIgnoreCase(browser)) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--remote-allow-origins=*");
            return new ChromeDriver(options);
        } else if ("firefox".equalsIgnoreCase(browser)) {
            FirefoxOptions options = new FirefoxOptions();
            return new FirefoxDriver(options);
        } else {
            throw new IllegalArgumentException("Unsupported browser: " + browser);
        }
    }

    private WebDriver createRemoteDriver(String browser, String remoteUrl) throws Exception {
        if ("chrome".equalsIgnoreCase(browser)) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--remote-allow-origins=*");
            return new RemoteWebDriver(new URL(remoteUrl), options);
        } else if ("firefox".equalsIgnoreCase(browser)) {
            FirefoxOptions options = new FirefoxOptions();
            return new RemoteWebDriver(new URL(remoteUrl), options);
        } else {
            throw new IllegalArgumentException("Unsupported browser: " + browser);
        }
    }

    /**
     * Tear down the WebDriver after the test method is completed
     */
    @AfterMethod(alwaysRun = true, description = "Tearing down test setup")
    public void tearDown() {
        WebDriver driver = getDriver();
        if (driver != null) {
            driver.quit();
        }
    }

    @AfterClass(alwaysRun = true)
    public void terminate() {
        driverThreadLocal.remove();
    }
}
