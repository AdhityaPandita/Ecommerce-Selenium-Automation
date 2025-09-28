package My.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class Ecommerce_basic {

    public static void main(String[] args) throws InterruptedException {

    	ChromeOptions options = new ChromeOptions();
    	options.addArguments("--guest"); // runs Chrome in Guest mode
    	WebDriver driver = new ChromeDriver(options);

       // WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        WebDriverWait w = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // ✅ 1. Open URL and login
            driver.get("https://www.saucedemo.com/");
            if (driver.getTitle().equals("Swag Labs")) {
                System.out.println("✅ Site is opened");
            } else {
                System.out.println("❌ Site title mismatched: " + driver.getTitle());
            }

            driver.findElement(By.id("user-name")).sendKeys("standard_user");
            driver.findElement(By.id("password")).sendKeys("secret_sauce");
            driver.findElement(By.id("login-button")).click();
            w.until(ExpectedConditions.urlContains("inventory"));
            System.out.println("✅ Login Successful");

            // ✅ 2. Add first product and verify button text changes
            WebDriverWait w1 = new WebDriverWait(driver, Duration.ofSeconds(10));

         // Wait until button is clickable and click
         WebElement firstItemBtn = w1.until(ExpectedConditions.elementToBeClickable(
                 By.id("add-to-cart-sauce-labs-backpack")
         ));
         firstItemBtn.click();

         // Wait until cart badge updates to 1
         w1.until(ExpectedConditions.textToBePresentInElementLocated(
                 By.className("shopping_cart_badge"), "1"
         ));

         System.out.println("✅ First item added to cart");
            // ✅ 3. Add second product and verify
         WebElement secondItemBtn = w.until(ExpectedConditions.elementToBeClickable(
        	        By.id("add-to-cart-sauce-labs-bike-light")
        	));
        	secondItemBtn.click();

        	// Wait until cart badge updates to 2
        	w.until(ExpectedConditions.textToBePresentInElementLocated(
        	        By.className("shopping_cart_badge"), "2"
        	));

        	System.out.println("✅ Second item added to cart");

            // ✅ 4. Verify cart badge count
            WebElement badge = driver.findElement(By.className("shopping_cart_badge"));
            String badgeText = badge.getText();
            if (badgeText.equals("2")) {
                System.out.println("✅ Badge count is working fine: " + badgeText);
            } else {
                System.out.println("❌ Badge count mismatch: " + badgeText);
            }

            // ✅ 5. Go to cart
            driver.findElement(By.className("shopping_cart_link")).click();
            w1.until(ExpectedConditions.visibilityOfElementLocated(By.className("cart_item")));

            // ✅ 6. Verify both products in the cart
            String product1 = driver.findElements(By.className("inventory_item_name")).get(0).getText();
            String product2 = driver.findElements(By.className("inventory_item_name")).get(1).getText();
            if (product1.equals("Sauce Labs Backpack")) {
                System.out.println("✅ First item verified in the cart");
            } else {
                System.out.println("❌ First item verifi	cation failed in the cart");
            }
            if (product2.equals("Sauce Labs Bike Light")) {
                System.out.println("✅ Second item verified in the cart");
            } else {
                System.out.println("❌ Second item verification failed in the cart");
            }

            int itemCount = driver.findElements(By.className("inventory_item_name")).size();
            if (itemCount == 2) {
                System.out.println("✅ Cart has exactly 2 items");
            } else {
                System.out.println("❌ Cart items mismatched: " + itemCount);
            }

            // ✅ 7. Click checkout and verify fields visible
            driver.findElement(By.id("checkout")).click();
            w1.until(ExpectedConditions.visibilityOfElementLocated(By.id("first-name")));
            System.out.println("✅ Checkout page loaded");

            if (driver.findElement(By.id("first-name")).isDisplayed() &&
                driver.findElement(By.id("last-name")).isDisplayed() &&
                driver.findElement(By.id("postal-code")).isDisplayed()) {
                System.out.println("✅ All checkout fields are visible");
            }

            if (driver.findElement(By.id("first-name")).isEnabled() &&
                driver.findElement(By.id("last-name")).isEnabled() &&
                driver.findElement(By.id("postal-code")).isEnabled()) {
                System.out.println("✅ All checkout fields are enabled");
            }

            // ✅ 8. Click continue with nothing filled → expect first name error
            driver.findElement(By.id("continue")).click();
            validateError(driver, "Error: First Name is required");

            // ✅ 9. Fill only first name → expect last name error
            driver.findElement(By.id("first-name")).sendKeys("John");
            driver.findElement(By.id("continue")).click();
            validateError(driver, "Error: Last Name is required");

            // ✅ 10. Fill last name too → expect postal code error
            driver.findElement(By.id("last-name")).sendKeys("Doe");
            driver.findElement(By.id("continue")).click();
            validateError(driver, "Error: Postal Code is required");

            // ✅ 11. Fill all fields correctly
            driver.findElement(By.id("postal-code")).sendKeys("12345");
            driver.findElement(By.id("continue")).click();

            // ✅ 12. Finish order
            w1.until(ExpectedConditions.visibilityOfElementLocated(By.id("finish")));
            driver.findElement(By.id("finish")).click();
            w1.until(ExpectedConditions.visibilityOfElementLocated(By.className("complete-header")));
            String completeMsg = driver.findElement(By.className("complete-header")).getText();
            if (completeMsg.equals("Thank you for your order!")) {
                System.out.println("✅ Order completed successfully");
            } else {
                System.out.println("❌ Order message mismatch: " + completeMsg);
            }

        } catch (Exception e) {
            System.out.println("❌ Test failed: " + e.getMessage());
        } finally {
            Thread.sleep(2000);
            driver.quit();
            System.out.println("Browser closed");
        }
    }

    // 🔹 Helper method to validate error message
    static void validateError(WebDriver driver, String expectedMsg) {
        WebElement errorElement = driver.findElement(By.cssSelector("h3[data-test='error']"));
        String errorMsg = errorElement.getText();
        if (errorMsg.equals(expectedMsg)) {
            System.out.println("✅ Validation message displayed:--- " + errorMsg);
        } else {
            System.out.println("❌ Validation mismatch. Expected:--- " + expectedMsg + " | Found: " + errorMsg);
        }
    }
}
