package com.sardul3.hireboost.autoview.workflow;

import com.sardul3.hireboost.autoview.model.PersonProfile;
import com.sardul3.hireboost.autoview.activity.LinkedInProfileViewActivities;
import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
public class LinkedInProfileViewWorkflowImpl implements LinkedInProfileViewWorkflow {

    private final LinkedInProfileViewActivities activities = Workflow.newActivityStub(
            LinkedInProfileViewActivities.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofMinutes(5))
                    .build()
    );

    @Override
    public List<PersonProfile> visitProfiles(String username, String password) {
        log.info("🚀 Starting LinkedIn Profile Visiting Workflow for user: {}", username);

        // ✅ Step 1: Login with session/cookies
        boolean loginSuccess = activities.loginToLinkedIn(username, password);
        if (!loginSuccess) {
            log.error("❌ LinkedIn login failed for user: {}. Stopping workflow.", username);
            return Collections.emptyList();
        }

        // ✅ Step 2: Perform profile search
        boolean searchSuccessful = activities.performProfileSearch(username);
        if (!searchSuccessful) {
            log.error("❌ Profile search failed for user: {}. Stopping workflow.", username);
            return Collections.emptyList();
        }

        // ✅ Step 3: Extract profile details
        List<PersonProfile> profiles = activities.extractProfileDetails(username);

        if (profiles.isEmpty()) {
            log.warn("⚠ No profiles found for user: {}", username);
            return Collections.emptyList();
        }

        log.info("✅ Found {} profiles for user: {}", profiles.size(), username);

        // ✅ Step 4: Visit and rank each profile
        List<PersonProfile> rankedAndDataRichProfiles = new ArrayList<>();
        for (PersonProfile profile : profiles) {
            log.info("🔍 Visiting profile: {}", profile.getProfileUrl());

            // Navigate to profile and extract data
            Optional<PersonProfile> visitedProfileOpt = Optional.ofNullable(
                    activities.navigateToProfile(profile.getProfileUrl(), username));

            if (visitedProfileOpt.isPresent()) {
                PersonProfile visitedProfile = visitedProfileOpt.get();
                rankedAndDataRichProfiles.add(visitedProfile);
                log.info("✅ Extracted profile: {} | Rank Score: {}", visitedProfile.getName(), visitedProfile.getRankScore());

                // Navigate back to the search results page
                activities.navigateBackToSearch(username);
            } else {
                log.error("❌ Failed to navigate to profile: {}", profile.getProfileUrl());
            }
        }

        activities.cleanupSession(username);
        log.info("🔚 Workflow completed for user: {}", username);

        return rankedAndDataRichProfiles;
    }
}
