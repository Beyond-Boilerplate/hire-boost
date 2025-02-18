package com.sardul3.hireboost.autoview.service;

import org.openqa.selenium.WebDriver;

public interface SeleniumService {
    WebDriver getWebDriver();
    void tearDown();
}
