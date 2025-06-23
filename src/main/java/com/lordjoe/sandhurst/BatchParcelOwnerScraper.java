package com.lordjoe.sandhurst;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.List;

public class BatchParcelOwnerScraper {

    public static void main(String[] args) throws Exception {
        System.setProperty("webdriver.chrome.driver", "/home/Steve/programs/chromedriver-linux64/chromedriver"); // <--- Change this path

        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        List<House> houses = Neighborhood.Instance.getHouses();

        try (PrintWriter writer = new PrintWriter(new FileWriter("owners_output.tsv"))) {
            writer.println("Address\tOwner");

            for (House house : houses) {
                String address = house.getAddress();
                System.out.println("Looking up: " + address);

                try {
                    driver.get("https://blue.kingcounty.gov/Assessor/eRealProperty/");

                    WebElement addressField = wait.until(
                            ExpectedConditions.presenceOfElementLocated(By.id("MainContent_txtAddress")));
                    addressField.clear();
                    addressField.sendKeys(address);

                    WebElement searchButton = driver.findElement(By.id("MainContent_btnSearch"));
                    searchButton.click();

                    // Check for multiple results and click first
                    try {
                        WebElement firstResult = wait.until(
                                ExpectedConditions.elementToBeClickable(
                                        By.cssSelector("#MainContent_gvResults tr:nth-child(2) td a")));
                        firstResult.click();
                    } catch (Exception ignored) {
                        // no multiple match - already on detail page
                    }

                    WebElement ownerField = wait.until(
                            ExpectedConditions.presenceOfElementLocated(By.id("MainContent_lblOwner")));
                    String ownerName = ownerField.getText();

                    writer.printf("%s\t%s%n", address, ownerName);
                } catch (Exception e) {
                    System.out.println("Failed to process address: " + address);
                    writer.printf("%s\t%s%n", address, "ERROR or Not Found");
                    e.printStackTrace();
                }
            }
        } finally {
            driver.quit();
        }

        System.out.println("Done. Results saved to owners_output.tsv");
    }
}

