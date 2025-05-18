package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class HomePage {

    private WebDriver driver;
    private By searchBox = By.name("q");

    public HomePage(WebDriver driver) {
        this.driver = driver;
    }

    public void enterSearchText(String text) {
        WebElement box = driver.findElement(searchBox);
        box.sendKeys(text);
    }

    public void submitSearch() {
        driver.findElement(searchBox).submit();
    }
}
