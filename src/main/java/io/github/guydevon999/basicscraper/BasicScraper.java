package io.github.guydevon999.basicscraper;

/*
Basic web scraper class uses selenium to control a headless Chrome browser.
 */

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.time.Duration;
import java.util.Set;

public class BasicScraper implements AutoCloseable {
    private WebDriver driver;
    private WebDriverWait wait;

    // No-arg constructor for scraper with headless browser
    public BasicScraper() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        this.driver = new ChromeDriver(options);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // scrapeProducts method requires a target url and page limit. Uses pagination
    public List<Product> scrapeProducts(String url, int maxPages) {
        List<Product> products = new ArrayList<>();
        int currentPage = 1;
        String currentUrl = url;

        driver.get(currentUrl);

        while (currentPage <= maxPages) {
            System.out.println("Scraping page: " + currentPage + " (" + driver.getCurrentUrl() + ")");

            // Check if we're actually on a product page
            if (!isValidProductPage()) {
                System.out.println("No products found - stopping at page " + currentPage);
                break;
            }

            // Scrape current page
            List<Product> pageProducts = scrapeCurrentPage();
            products.addAll(pageProducts);
            System.out.println("Found " + pageProducts.size() + " products on this page");

            // Check for next page link before incrementing
            WebElement nextPageLink = findNextPageLink();
            if (nextPageLink == null || currentPage >= maxPages) {
                System.out.println("Reached last page at " + currentPage);
                break;
            }

            // Navigate to next page
            String nextPageUrl = nextPageLink.getAttribute("href");
            driver.get(nextPageUrl);

            // Small delay to ensure page loads
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Wait for page load
            wait.until(ExpectedConditions.urlToBe(nextPageUrl));
            currentPage++;
        }

        return products;
    }

    // Helper method to determine if there are relevant products on the page
    private boolean isValidProductPage() {
        try {
            return !driver.findElements(By.cssSelector("article.product_pod")).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    // Helper method to find url for next page
    private WebElement findNextPageLink() {
        try {
            return driver.findElement(By.cssSelector("li.next > a"));
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    // Method to scrape pages for products on https://books.toscrape.com/
    private List<Product> scrapeCurrentPage() {
        List<Product> products = new ArrayList<>();
        Set<String> seenUrls = new HashSet<>();         // HashSet to avoid duplicate product urls

        List<WebElement> productElements = driver.findElements(By.cssSelector("article.product_pod"));

        for (WebElement productElement : productElements) {
            try {
                String productUrl = productElement.findElement(By.cssSelector("h3 > a")).getAttribute("href");

                // Check for duplicates
                if (seenUrls.contains(productUrl)) {
                    continue;
                }
                seenUrls.add(productUrl);

                // Set the product url, image url, name, and price
                Product product = new Product();
                product.setUrl(productUrl);
                product.setImage(productElement.findElement(By.cssSelector("img")).getAttribute("src"));
                product.setName(productElement.findElement(By.cssSelector("h3 > a")).getAttribute("title"));
                product.setPrice(productElement.findElement(By.cssSelector("p.price_color")).getText());

                // Set the star rating (0-5)
                String ratingClass = productElement.findElement(By.cssSelector("p.star-rating")).getAttribute("class");
                product.setRating(ratingClass.replace("star-rating ", ""));

                // Set availability
                product.setAvailability(productElement.findElement(By.cssSelector("p.availability")).getText().trim());

                products.add(product);
            } catch (NoSuchElementException e) {
                System.err.println("Failed to extract some product data: " + e.getMessage());
            }
        }
        return products;
    }

    @Override
    public void close() {
        if (driver != null) {
            driver.quit();
        }
    }
}
