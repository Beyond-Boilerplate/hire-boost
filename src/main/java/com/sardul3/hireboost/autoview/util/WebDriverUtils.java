package com.sardul3.hireboost.autoview.util;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class WebDriverUtils {

    private WebDriverUtils() {
        throw new UnsupportedOperationException("Utility class should not be instantiated");
    }
    public static void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void sendKeysWithPause(WebElement element, String text) {
        for (char ch : text.toCharArray()) {
            element.sendKeys(String.valueOf(ch));
            sleep(1);
        }
    }

    public static void scrollPage(WebDriver driver, int times) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        for (int i = 0; i < times; i++) {
            js.executeScript("window.scrollBy(0, 1000);");
            sleep(2);
        }
    }
}

