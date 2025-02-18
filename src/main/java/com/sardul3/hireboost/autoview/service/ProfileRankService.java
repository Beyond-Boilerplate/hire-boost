package com.sardul3.hireboost.autoview.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class ProfileRankService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileRankService.class);

    // Adjusted Weights for Better Differentiation
    private static final double FOLLOWER_WEIGHT = 0.35;
    private static final double CONNECTION_WEIGHT = 0.25;
    private static final double MUTUAL_CONNECTION_WEIGHT = 0.40;
    private static final double RECENT_POST_BONUS = 12.0;
    private static final double WEBSITE_BONUS = 8.0;
    private static final double CONTACT_INFO_BONUS = 5.0;
    private static final double ENGAGEMENT_MULTIPLIER = 1.5;
    private static final double DECAY_FACTOR = 0.94; // Stronger decay for inactivity
    private static final double INCOMPLETE_PROFILE_PENALTY = -5.0; // Deduction for missing details
    private static final double MAX_BASE_SCORE = 150.0; // Ensures normalization

    /**
     * Ranks a profile based on meaningful factors while ensuring better score differentiation.
     *
     * @param followers         Number of followers
     * @param connections       Number of connections
     * @param mutualConnections Number of mutual connections
     * @param hasRecentPosts    Whether the user has recent posts
     * @param website           Website presence
     * @param hasContactInfo    Whether contact info is available
     * @param jobTitle          Job title (affects seniority ranking)
     * @param company           Company presence (missing company info reduces ranking)
     * @param education         Education presence
     * @return A **better differentiated** rank score (0 to 100). Returns 0 on error.
     */
    public double rankProfile(Integer followers, Integer connections, Integer mutualConnections,
                              Boolean hasRecentPosts, String website, Boolean hasContactInfo,
                              String jobTitle, String company, String education) {
        try {
            // Ensure values are sanitized
            int safeFollowers = sanitizeInput(followers);
            int safeConnections = sanitizeInput(connections);
            int safeMutualConnections = sanitizeInput(mutualConnections);
            boolean safeHasRecentPosts = Objects.requireNonNullElse(hasRecentPosts, false);
            boolean safeHasContactInfo = Objects.requireNonNullElse(hasContactInfo, false);
            boolean hasWebsite = Objects.nonNull(website) && !website.isBlank();

            double score = 0.0;

            // Influence Metrics (with adjusted scaling)
            score += Math.min(safeFollowers * FOLLOWER_WEIGHT, 50);
            score += Math.min(safeConnections * CONNECTION_WEIGHT, 35);
            score += Math.min(safeMutualConnections * MUTUAL_CONNECTION_WEIGHT, 40);

            // Activity Bonus (Recency Matters)
            if (safeHasRecentPosts) {
                score += RECENT_POST_BONUS;
            }

            // Professionalism Bonuses
            if (hasWebsite) {
                score += WEBSITE_BONUS;
            }
            if (safeHasContactInfo) {
                score += CONTACT_INFO_BONUS;
            }

            // Seniority Bonus (Ranking higher-level professionals slightly better)
            if (jobTitle != null && !jobTitle.equalsIgnoreCase("NA")) {
                if (jobTitle.toLowerCase().contains("cto") || jobTitle.toLowerCase().contains("vp") ||
                        jobTitle.toLowerCase().contains("chief") || jobTitle.toLowerCase().contains("director")) {
                    score += 7.0; // Senior roles get an extra boost
                } else if (jobTitle.toLowerCase().contains("manager") || jobTitle.toLowerCase().contains("lead")) {
                    score += 4.0; // Mid-level roles get a slight boost
                }
            }

            // Penalize incomplete profiles (Missing company, education, location)
            int missingDataPoints = 0;
            if (company == null || company.equalsIgnoreCase("NA")) missingDataPoints++;
            if (education == null || education.equalsIgnoreCase("NA")) missingDataPoints++;

            if (missingDataPoints > 0) {
                score += INCOMPLETE_PROFILE_PENALTY * missingDataPoints;
            }

            // Engagement Boost (for highly followed users)
            if (safeFollowers > 5000 || safeMutualConnections > 100) {
                score *= ENGAGEMENT_MULTIPLIER;
            }

            // Normalize the score to 100
            return normalizeScore(score);

        } catch (Exception e) {
            LOGGER.error("Error ranking profile: {}", e.getMessage(), e);
            return 0.0; // Fail-safe return
        }
    }

    /**
     * Ensures an integer input is valid, replacing null and negative values with 0.
     */
    private int sanitizeInput(Integer value) {
        return (value == null || value < 0) ? 0 : value;
    }

    /**
     * Normalizes the raw score to fit within the range of 0 to 100.
     */
    private double normalizeScore(double rawScore) {
        return Math.max(0, Math.min((rawScore / MAX_BASE_SCORE) * 100, 100));
    }
}


