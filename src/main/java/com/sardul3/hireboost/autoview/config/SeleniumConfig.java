package com.sardul3.hireboost.autoview.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "selenium")
public class SeleniumConfig {
    private boolean headless;
    private String chromeDriverPath;
    private List<String> userAgents;
    private List<String> proxies;
}

