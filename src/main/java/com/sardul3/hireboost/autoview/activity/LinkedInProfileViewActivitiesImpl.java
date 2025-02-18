package com.sardul3.hireboost.autoview.activity;

import com.sardul3.hireboost.autoview.model.PersonProfile;
import com.sardul3.hireboost.autoview.service.LinkedInCookieService;
import com.sardul3.hireboost.autoview.service.ProfileRankService;
import com.sardul3.hireboost.autoview.service.SeleniumSessionManager;
import com.sardul3.hireboost.autoview.util.WebDriverUtils;
import com.sardul3.hireboost.autoview.config.LinkedInConfig;
import com.sardul3.hireboost.autoview.service.SeleniumService;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
public class LinkedInProfileViewActivitiesImpl implements LinkedInProfileViewActivities {

    private final LinkedInConfig linkedInConfig;
    private final LinkedInCookieService linkedInCookieService;
    private final SeleniumSessionManager seleniumSessionManager;
    private final ProfileRankService profileRankService;

    private final Random random = new Random();
    private String currentSearchUrl;  // Stores the search URL for easy navigation


    @Value("${profile-visit.recruiter-only}")
    private boolean browseRecruitersOnly;

    public LinkedInProfileViewActivitiesImpl(SeleniumService seleniumService, LinkedInConfig linkedInConfig, LinkedInCookieService linkedInCookieService, SeleniumSessionManager seleniumSessionManager, ProfileRankService profileRankService) {
        this.linkedInConfig = linkedInConfig;
        this.linkedInCookieService = linkedInCookieService;
        this.seleniumSessionManager = seleniumSessionManager;
        this.profileRankService = profileRankService;
    }

    /** 🔹 STAGE 1: LOGIN OPERATION **/
    @Override
    public boolean loginToLinkedIn(String username, String password) {
        WebDriver driver = seleniumSessionManager.createWebDriverForUser(username);
        try {
            driver.get(linkedInConfig.getBaseUrl());
            log.info("Logging into LinkedIn for user '{}'", username);

            // 1️⃣ Check if session is already active
            if (isAlreadyLoggedIn(username)) {
                log.info("✅ User '{}' is already logged in. Skipping login.", username);
                return true;
            }

            // 2️⃣ Try restoring session from saved cookies
            if (linkedInCookieService.loadSessionCookies(driver, username)) {
                driver.get("https://www.linkedin.com/feed/");
                if (isAlreadyLoggedIn(username)) {
                    log.info("✅ Session restored for '{}'. Skipping login.", username);
                    return true;
                }
            }
            driver.get(linkedInConfig.getLoginUrl());

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(linkedInConfig.getWaitTimeSeconds()));

            WebDriverUtils.sleep(5);
            WebElement usernameField = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("username")));
            WebElement passwordField = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("password")));

            if (usernameField.isDisplayed()) {
                slowType(usernameField, username);
            }

            if (passwordField.isDisplayed()) {
                slowType(passwordField, password);
            }

            driver.findElement(By.xpath("//button[@type='submit']")).click();
            wait.until(ExpectedConditions.urlContains("feed"));

            linkedInCookieService.saveSessionCookies(driver, username);
            log.info("✅ Successfully logged into LinkedIn as '{}'", username);
            return true;
        } catch (Exception e) {
            log.error("❌ Failed to log in to LinkedIn", e);
            return false;
        }
    }

    /** 🔹 STAGE 2: SEARCH OPERATION **/
    @Override
    public boolean performProfileSearch(String username) {
        WebDriver driver = seleniumSessionManager.createWebDriverForUser(username);
        try {
            log.info("🚀 Performing LinkedIn profile search...");
            if(browseRecruitersOnly) {
                int randomPage = random.nextInt(40) + 1; // Page range: 1 - 40
                currentSearchUrl = linkedInConfig.getRecruiterUrl() + "&page=" + randomPage;
            } else {
                currentSearchUrl = getRandomSearchUrl();
            }
            driver.get(currentSearchUrl);

            WebDriverUtils.scrollPage(driver, linkedInConfig.getScrollCount());
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error("❌ Error while searching trending profiles", e);
            return Boolean.FALSE;
        }
    }

    /** 🔹 STAGE 3: DATA RETRIEVAL STAGE - Extract Profile Details **/
    @Override
    public List<PersonProfile> extractProfileDetails(String username) {
        List<PersonProfile> profiles = new ArrayList<>();
        WebDriver driver = seleniumSessionManager.createWebDriverForUser(username);

        try {
            List<WebElement> profileElements = driver.findElements(By.cssSelector("a[data-test-app-aware-link]"));
            int minIndex = linkedInConfig.getMinProfileIndex();
            int maxIndex = Math.min(profileElements.size(), linkedInConfig.getMaxProfileIndex());

            for (int i = minIndex; i < maxIndex; i++) {
                WebElement profile = profileElements.get(i);
                new Actions(driver).moveToElement(profile).perform();
                WebDriverUtils.sleep(2);

                String name = profile.getText();
                String profileUrl = profile.getAttribute("href");

                profiles.add(new PersonProfile(name, profileUrl));
                log.info("✅ Found: {} - {}", name, profileUrl);
            }
        } catch (Exception e) {
            log.error("❌ Error extracting profile details", e);
        }
        return profiles;
    }

    /** 🔹 STAGE 4: NAVIGATE TO PROFILE **/
    @Override
    public PersonProfile navigateToProfile(String profileUrl, String username) {
        WebDriver driver = seleniumSessionManager.createWebDriverForUser(username);

        try {
            log.info("🌐 Navigating to profile: {}", profileUrl);
            driver.get(profileUrl);
            WebDriverUtils.sleep(5);

            // Extract key details
            String profilePicture = getAttribute(driver, By.cssSelector("img.pv-top-card-profile-picture__image--show"), "src");
            String name = getText(driver, By.cssSelector("h1.inline.t-24.v-align-middle.break-words"));
            String jobTitle = getText(driver, By.cssSelector(".text-body-medium.break-words"));
            String company = getText(driver, By.cssSelector("button[aria-label^='Current company:'] div"));
            String education = getText(driver, By.cssSelector("button[aria-label^='Education:'] div"));
            String location = getText(driver, By.cssSelector(".text-body-small.inline.t-black--light.break-words"));
            String connectionsText = getText(driver, By.cssSelector(".text-body-small span.t-bold"));
            int connections = parseNumber(connectionsText);
            String website = getAttribute(driver, By.cssSelector("section.pv-top-card--website a"), "href");
            boolean hasContactInfo = isElementPresent(driver, By.cssSelector("a#top-card-text-details-contact-info"));
            String mutualConnectionsText = getText(driver, By.cssSelector("a[data-test-app-aware-link] span.t-normal.t-black--light"));
            int mutualConnections = parseNumber(mutualConnectionsText);

            // Extract Activity Data
            String followersText = getText(driver, By.cssSelector(".pvs-header__optional-link.text-body-small"));
            int followers = parseNumber(followersText);
            boolean hasRecentPosts = !isElementPresent(driver, By.cssSelector("span.text-body-medium-bold"));
            boolean isConnected = !isElementWithTextPresent(driver, By.cssSelector("button[aria-label]"), "invite");

            // Rank Profile
            double rankScore = profileRankService.rankProfile(followers, connections, mutualConnections, hasRecentPosts, website, hasContactInfo, jobTitle, company, education);

            log.info("""
                📊 Extracted Profile Data for '{}':
                🖼 Profile Picture: {}
                🏷 Name: {}
                💼 Job Title: {}
                🏢 Company: {}
                🎓 Education: {}
                📍 Location: {}
                🤝 Connections: {}
                🔗 Website: {}
                📞 Contact Info Available: {}
                🔄 Mutual Connections: {}
                
                📢 Activity Data:
                👥 Followers: {}
                📝 Recent Posts Available: {}
                🔗 Rank Score: {}
                🛜 Connected Already: {}
                """,
                    username, profilePicture, name, jobTitle, company, education, location, connections, website,
                    hasContactInfo, mutualConnections, followers, hasRecentPosts, rankScore, isConnected
            );

            // Store the profile data for ranking
            return new PersonProfile(name, profileUrl, jobTitle, company, education, location, connections, followers, mutualConnections, website, hasRecentPosts, isConnected, rankScore);

        } catch (Exception e) {
            log.error("❌ Failed to navigate to profile: {}", profileUrl, e);
            return null;
        }
    }

    /** 🔹 STAGE 5: NAVIGATE BACK TO SEARCH **/
    @Override
    public boolean navigateBackToSearch(String username) {
        WebDriver driver = seleniumSessionManager.createWebDriverForUser(username);

        try {
            log.info("🔙 Navigating back to search results...");
            driver.get(currentSearchUrl);
            WebDriverUtils.sleep(3);

            return true;

        } catch (Exception e) {
            log.error("❌ Failed to navigate back to search", e);
            return false;
        }
    }

    @Override
    public void cleanupSession(String username) {
        seleniumSessionManager.closeWebDriverForUser(username);
    }

    /** 🔹 Helper: Select a random LinkedIn search URL **/
    private String getRandomSearchUrl() {
        List<String> searchUrls = linkedInConfig.getSearchUrls();
        return searchUrls.get(random.nextInt(searchUrls.size()));
    }

    /** 🔹 CHECK IF USER IS ALREADY LOGGED IN **/
    private boolean isAlreadyLoggedIn(String username) {
        WebDriver driver = seleniumSessionManager.createWebDriverForUser(username);
        try {
            driver.get(linkedInConfig.getBaseUrl());
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("a[href*='feed']")));
            log.info("✅ User '{}' is already logged in.", username);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /** 🔹 Extracts text safely, returns "NA" if not found **/
    private String getText(WebDriver driver, By selector) {
        try {
            return driver.findElement(selector).getText().trim();
        } catch (NoSuchElementException e) {
            return "NA";
        }
    }

    /** 🔹 Extracts attribute value safely, returns "NA" if not found **/
    private String getAttribute(WebDriver driver, By selector, String attribute) {
        try {
            return driver.findElement(selector).getAttribute(attribute);
        } catch (NoSuchElementException e) {
            return "NA";
        }
    }

    /** 🔹 Checks if an element exists **/
    private boolean isElementPresent(WebDriver driver, By selector) {
        try {
            driver.findElement(selector);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }



    /** 🔹 Parses numerical values from text **/
    private int parseNumber(String text) {
        try {
            return Integer.parseInt(text.replaceAll("[^0-9]", "").trim());
        } catch (Exception e) {
            return 0;
        }
    }

    /** ✅ Checks if an element with a given text (case-insensitive) exists */
    private boolean isElementWithTextPresent(WebDriver driver, By selector, String text) {
        try {
            List<WebElement> elements = driver.findElements(selector);
            for (WebElement element : elements) {
                String ariaLabel = element.getAttribute("aria-label");
                if (ariaLabel != null && ariaLabel.toLowerCase().contains(text.toLowerCase())) {
                    return true;
                }
            }
        } catch (NoSuchElementException e) {
            return false;
        }
        return false;
    }

    private void slowType(WebElement element, String text) {
        for (char c : text.toCharArray()) {
            element.sendKeys(String.valueOf(c));
            WebDriverUtils.sleep(random.nextInt(5) + 1);
        }
    }

    private void smoothScroll(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        for (int i = 0; i < linkedInConfig.getScrollCount(); i++) {
            js.executeScript("window.scrollBy(0, " + (random.nextInt(300) + 200) + ")");
            WebDriverUtils.sleep(randomDelay());
        }
    }

    private int randomDelay() {
        return random.nextInt(2000) + 1000;
    }
}
