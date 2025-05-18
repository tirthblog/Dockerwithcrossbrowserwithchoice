package base;

import java.net.URL;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseClass {

    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    protected WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    @BeforeMethod(alwaysRun = true)
    public void setup() throws Exception {
        String environment = System.getProperty("environment", "local");
        String browser = System.getProperty("browser", "edge");  // default to edge now
        String remoteUrl = System.getProperty("remoteUrl", "");

        WebDriver driver;

        if ("local".equalsIgnoreCase(environment)) {
            driver = createLocalDriver(browser);
        } else if ("remote".equalsIgnoreCase(environment)) {
            if (remoteUrl == null || remoteUrl.isEmpty()) {
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
        switch (browser.toLowerCase()) {
            case "chrome":
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--remote-allow-origins=*");
                return new ChromeDriver(chromeOptions);

            case "firefox":
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                return new FirefoxDriver(firefoxOptions);

            case "edge":
                EdgeOptions edgeOptions = new EdgeOptions();
                return new EdgeDriver(edgeOptions);

            default:
                throw new IllegalArgumentException("Unsupported browser: " + browser);
        }
    }

    private WebDriver createRemoteDriver(String browser, String remoteUrl) throws Exception {
        URL url = new URL(remoteUrl);
        switch (browser.toLowerCase()) {
            case "chrome":
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--remote-allow-origins=*");
                return new RemoteWebDriver(url, chromeOptions);

            case "firefox":
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                return new RemoteWebDriver(url, firefoxOptions);

            case "edge":
                EdgeOptions edgeOptions = new EdgeOptions();
                return new RemoteWebDriver(url, edgeOptions);

            default:
                throw new IllegalArgumentException("Unsupported browser: " + browser);
        }
    }

    @AfterMethod(alwaysRun = true, description = "Tearing down test setup")
    public void tearDown() {
        WebDriver driver = getDriver();
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                System.err.println("Error quitting driver: " + e.getMessage());
            }
        }
    }

    @AfterClass(alwaysRun = true)
    public void terminate() {
        driverThreadLocal.remove();
    }
}