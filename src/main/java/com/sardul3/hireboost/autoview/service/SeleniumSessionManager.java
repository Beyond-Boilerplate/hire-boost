package com.sardul3.hireboost.autoview.service;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.NoSuchDriverException;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class SeleniumSessionManager {

    private final Map<String, WebDriver> sessionMap = new ConcurrentHashMap<>();

    public WebDriver createWebDriverForUser(String username) {
        if (sessionMap.containsKey(username)) {
            WebDriver existingDriver = sessionMap.get(username);

            // ✅ Check if the existing WebDriver is still active
            if (isWebDriverAlive(existingDriver)) {
                log.info("✅ Returning existing WebDriver for user: {}", username);
                return existingDriver;
            } else {
                log.warn("⚠️ Stale WebDriver detected for '{}'. Restarting session.", username);
                closeWebDriverForUser(username);
            }
        }

        log.info("🚀 Creating a new WebDriver session for user: {}", username);
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled", "--disable-infobars");
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--disable-gpu");

        WebDriver driver = new ChromeDriver(options);

        // ✅ Set timeouts to prevent WebDriver from hanging indefinitely
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30)); // Max 30s for page load
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10)); // 10s implicit wait

        sessionMap.put(username, driver);
        return driver;
    }

    public void closeWebDriverForUser(String username) {
        WebDriver driver = sessionMap.remove(username);
        if (driver != null) {
            log.info("🔴 Closing WebDriver session for user: {}", username);
            try {
                driver.quit();
            } catch (UnreachableBrowserException e) {
                log.warn("⚠️ WebDriver already closed or unreachable: {}", e.getMessage());
            }
        }
    }

    /** ✅ Check if WebDriver is alive before reusing it */
    private boolean isWebDriverAlive(WebDriver driver) {
        try {
            driver.getTitle();  // Try fetching page title, if fails, session is dead
            return true;
        } catch (NoSuchDriverException | UnreachableBrowserException e) {
            return false;
        }
    }
}
