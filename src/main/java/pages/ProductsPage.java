package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page Object for the TechCart demo products page (bundled with this
 * project, served locally by utils.LocalServer).
 *
 * Add-to-cart clicks are dispatched via JavaScript rather than a
 * simulated native click. Native clicks on headless Chrome in CI
 * environments can occasionally fail to register due to layout/paint
 * timing quirks that don't occur on a local machine; a JS-dispatched
 * click fires the DOM click event directly and deterministically,
 * which removes that class of flakiness entirely.
 */
public class ProductsPage {

    private final WebDriver driver;
    private final WebDriverWait wait;
    private final JavascriptExecutor jsExecutor;

    private final By pageTitle = By.className("title");
    private final By inventoryItems = By.className("inventory_item");
    private final By addToCartButtons = By.className("add-to-cart");
    private final By cartBadge = By.className("shopping_cart_badge");

    public ProductsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        this.jsExecutor = (JavascriptExecutor) driver;
    }

    public boolean isLoaded() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(pageTitle))
                .getText().equalsIgnoreCase("products");
    }

    public int getProductCount() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(inventoryItems));
        return driver.findElements(inventoryItems).size();
    }

    private void jsClick(WebElement element) {
        jsExecutor.executeScript("arguments[0].click();", element);
    }

    public void addFirstProductToCart() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(addToCartButtons));
        List<WebElement> buttons = driver.findElements(addToCartButtons);
        if (!buttons.isEmpty()) {
            jsClick(buttons.get(0));
            wait.until(ExpectedConditions.textToBePresentInElementLocated(cartBadge, "1"));
        }
    }

    public void addProductsToCart(int count) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(addToCartButtons));
        List<WebElement> buttons = driver.findElements(addToCartButtons);
        for (int i = 0; i < count && i < buttons.size(); i++) {
            int expectedCount = i + 1;
            jsClick(buttons.get(i));
            wait.until(ExpectedConditions.textToBePresentInElementLocated(cartBadge, String.valueOf(expectedCount)));
        }
    }

    public String getCartCount() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(cartBadge)).getText();
    }
}
