package com.sardul3.hireboost.autoview.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "linkedin")
public class LinkedInConfig {
    private String baseUrl;
    private String loginUrl;
    private String searchUrl;
    private int waitTimeSeconds;
    private int scrollCount;
    private int minProfileIndex;
    private int maxProfileIndex;
    private String recruiterUrl;
    private List<String> searchUrls;
}

