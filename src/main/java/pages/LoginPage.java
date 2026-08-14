package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Page Object for the TechCart demo login page (bundled with this
 * project, served locally by utils.LocalServer — see
 * src/test/resources/webapp).
 */
public class LoginPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final JavascriptExecutor jsExecutor;

    private final By usernameField = By.id("user-name");
    private final By passwordField = By.id("password");
    private final By loginButton = By.id("login-button");
    private final By errorMessage = By.cssSelector("[data-test='error-message']");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        this.jsExecutor = (JavascriptExecutor) driver;
    }

    private void jsClick(WebElement element) {
        jsExecutor.executeScript("arguments[0].click();", element);
    }

    public void open(String baseUrl) {
        driver.get(baseUrl);
        wait.until(ExpectedConditions.visibilityOfElementLocated(usernameField));
    }

    public LoginPage enterUsername(String username) {
        wait.until(ExpectedConditions.elementToBeClickable(usernameField)).sendKeys(username);
        return this;
    }

    public LoginPage enterPassword(String password) {
        wait.until(ExpectedConditions.elementToBeClickable(passwordField)).sendKeys(password);
        return this;
    }

    public ProductsPage clickLogin() {
        WebElement button = wait.until(ExpectedConditions.presenceOfElementLocated(loginButton));
        jsClick(button);
        return new ProductsPage(driver);
    }

    public LoginPage login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        WebElement button = wait.until(ExpectedConditions.presenceOfElementLocated(loginButton));
        jsClick(button);
        return this;
    }

    public String getErrorMessage() {
        WebElement error = wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage));
        return error.getText();
    }
}
