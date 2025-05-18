package tests;

import org.testng.annotations.Test;
import base.BaseClass;
import pages.HomePage;

public class ExampleTest extends BaseClass {

    @Test
    public void googleSearchTest() {
        // Use getDriver() to get the thread-safe driver instance
        HomePage google = new HomePage(getDriver());

        getDriver().get("https://www.google.com");
        google.enterSearchText("abc");
        System.out.println(getDriver().getTitle());
        
    }
}
