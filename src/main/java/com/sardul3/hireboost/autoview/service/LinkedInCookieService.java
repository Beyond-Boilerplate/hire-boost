package com.sardul3.hireboost.autoview.service;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

@Slf4j
@Service
public class LinkedInCookieService {

    private static final String COOKIE_DIR = "linkedin_cookies/";

    public boolean loadSessionCookies(WebDriver driver, String username) {
        Path cookieFile = Path.of(COOKIE_DIR, "linkedin_cookies_" + username + ".json");
        if (!Files.exists(cookieFile)) return false;

        try (FileReader reader = new FileReader(cookieFile.toFile())) {
            JSONArray cookiesArray = new JSONArray(new JSONTokener(reader));
            driver.get("https://www.linkedin.com"); // Ensure domain is correct

            for (Object obj : cookiesArray) {
                JSONObject json = (JSONObject) obj;
                Cookie cookie = new Cookie(json.getString("name"), json.getString("value"));
                driver.manage().addCookie(cookie);
            }

            driver.navigate().refresh();
            return true;
        } catch (IOException e) {
            log.error("❌ Error loading session cookies for '{}'", username, e);
            return false;
        }
    }

    public void saveSessionCookies(WebDriver driver, String username) {
        Path cookieFile = Path.of(COOKIE_DIR, "linkedin_cookies_" + username + ".json");
        createCookieDirIfNeeded();

        try (FileWriter writer = new FileWriter(cookieFile.toFile())) {
            JSONArray cookiesArray = new JSONArray();
            Set<Cookie> cookies = driver.manage().getCookies();
            for (Cookie cookie : cookies) {
                JSONObject cookieJson = new JSONObject();
                cookieJson.put("name", cookie.getName());
                cookieJson.put("value", cookie.getValue());
                cookieJson.put("domain", cookie.getDomain());
                cookiesArray.put(cookieJson);
            }
            writer.write(cookiesArray.toString());
            log.info("✅ Session cookies saved for user '{}'.", username);
        } catch (IOException e) {
            log.error("❌ Error saving session cookies for '{}'", username, e);
        }
    }

    private void createCookieDirIfNeeded() {
        Path cookieDir = Path.of(COOKIE_DIR);
        if (!Files.exists(cookieDir)) {
            try {
                Files.createDirectories(cookieDir);
            } catch (IOException e) {
                log.error("❌ Could not create cookie directory", e);
            }
        }
    }
}

