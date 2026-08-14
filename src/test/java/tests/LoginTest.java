package tests;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import pages.LoginPage;
import pages.ProductsPage;
import utils.DriverFactory;
import utils.LocalServer;

/**
 * Functional + negative test coverage for the TechCart demo app login
 * and cart flow. The app under test is bundled with this project
 * (src/test/resources/webapp) and served locally by LocalServer —
 * there is no dependency on any external website.
 *
 * Demonstrates: Page Object Model, data-driven testing via TestNG
 * DataProvider, and clean setup/teardown per test and per suite.
 */
public class LoginTest {

    private static String baseUrl;

    @BeforeClass
    public void startServer() throws Exception {
        baseUrl = LocalServer.start();
    }

    @AfterClass
    public void stopServer() {
        LocalServer.stop();
    }

    @BeforeMethod
    public void setUp() {
        DriverFactory.initDriver(true); // headless = true for CI
    }

    @AfterMethod
    public void tearDown() {
        DriverFactory.quitDriver();
    }

    @Test(description = "Valid login should land the user on the Products page")
    public void validLoginShowsProductsPage() {
        LoginPage loginPage = new LoginPage(DriverFactory.getDriver());
        loginPage.open(baseUrl);

        ProductsPage productsPage = loginPage
                .enterUsername("ranjeet.pd")
                .enterPassword("ranjeet1")
                .clickLogin();

        Assert.assertTrue(productsPage.isLoaded(), "Products page did not load after valid login");
        Assert.assertTrue(productsPage.getProductCount() > 0, "Expected products to be listed");
    }

    @Test(description = "Locked account should see an error message and stay on login page")
    public void lockedAccountSeesError() {
        LoginPage loginPage = new LoginPage(DriverFactory.getDriver());
        loginPage.open(baseUrl);
        loginPage.login("locked_user", "ranjeet1");

        String error = loginPage.getErrorMessage();
        Assert.assertTrue(error.contains("locked"),
                "Expected a locked-account error message, got: " + error);
    }

    @Test(dataProvider = "invalidCredentials",
          description = "Invalid credential combinations should all be rejected")
    public void invalidLoginIsRejected(String username, String password) {
        LoginPage loginPage = new LoginPage(DriverFactory.getDriver());
        loginPage.open(baseUrl);
        loginPage.login(username, password);

        String error = loginPage.getErrorMessage();
        Assert.assertTrue(error.length() > 0, "Expected an error message for invalid login");
    }

    @Test(description = "Adding a product should update the cart badge count")
    public void addingProductUpdatesCartBadge() {
        LoginPage loginPage = new LoginPage(DriverFactory.getDriver());
        loginPage.open(baseUrl);
        ProductsPage productsPage = loginPage
                .enterUsername("ranjeet.pd")
                .enterPassword("ranjeet1")
                .clickLogin();

        productsPage.isLoaded();
        productsPage.addFirstProductToCart();

        Assert.assertEquals(productsPage.getCartCount(), "1",
                "Cart badge should show 1 item after adding a product");
    }

    @Test(description = "Adding multiple products should increment the cart badge correctly")
    public void addingMultipleProductsUpdatesCartBadge() {
        LoginPage loginPage = new LoginPage(DriverFactory.getDriver());
        loginPage.open(baseUrl);
        ProductsPage productsPage = loginPage
                .enterUsername("ranjeet.pd")
                .enterPassword("ranjeet1")
                .clickLogin();

        productsPage.isLoaded();
        productsPage.addProductsToCart(3);

        Assert.assertEquals(productsPage.getCartCount(), "3",
                "Cart badge should show 3 items after adding three products");
    }

    @DataProvider(name = "invalidCredentials")
    public Object[][] invalidCredentials() {
        return new Object[][]{
                {"wrong_user", "ranjeet1"},
                {"ranjeet.pd", "wrong_password"},
                {"", ""}
        };
    }
}
