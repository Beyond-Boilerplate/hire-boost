package com.sardul3.hireboost.autoview.service;

import com.sardul3.hireboost.autoview.config.SeleniumConfig;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class SeleniumServiceImpl implements SeleniumService {

    private final WebDriver driver;
    private final Random random = new Random();

    @Value("${selenium.headless:true}")
    private boolean headlessMode;

    public SeleniumServiceImpl(SeleniumConfig seleniumConfig) {
        if (seleniumConfig.getChromeDriverPath() == null) {
            throw new IllegalStateException("ChromeDriver path is missing! Please check your application.yml.");
        }

        System.setProperty("webdriver.chrome.driver", seleniumConfig.getChromeDriverPath());

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-blink-features=AutomationControlled");  // Prevent bot detection
        options.addArguments("--disable-infobars");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        if(headlessMode) {
            options.addArguments("--headless");
        }

        String userAgent = getRandomElement(seleniumConfig.getUserAgents());
        options.addArguments("user-agent=" + userAgent);

        String proxyAddress = getRandomElement(seleniumConfig.getProxies());
        Proxy proxy = new Proxy();
        proxy.setHttpProxy(proxyAddress).setSslProxy(proxyAddress);

        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability(CapabilityType.PROXY, proxy);
        options.merge(capabilities);

        this.driver = new ChromeDriver(options);
        log.info("✅ WebDriver initialized with proxy: {} and user-agent: {}", proxyAddress, userAgent);
    }

    @Override
    public WebDriver getWebDriver() {
        return driver;
    }

    @PreDestroy
    @Override
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private <T> T getRandomElement(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }
}

