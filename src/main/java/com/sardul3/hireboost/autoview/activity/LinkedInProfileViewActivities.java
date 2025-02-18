package com.sardul3.hireboost.autoview.activity;

import com.sardul3.hireboost.autoview.model.PersonProfile;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;
import java.util.List;

@ActivityInterface
public interface LinkedInProfileViewActivities {

    @ActivityMethod
    boolean loginToLinkedIn(String username, String password);  // Returns success status

    @ActivityMethod
    boolean performProfileSearch(String username);  // Returns the search URL used

    @ActivityMethod
    List<PersonProfile> extractProfileDetails(String username);  // Returns extracted profiles

    @ActivityMethod
    PersonProfile navigateToProfile(String profileUrl, String username);  // Returns success/failure status

    @ActivityMethod
    boolean navigateBackToSearch(String username);  // Returns success/failure status

    @ActivityMethod
    void cleanupSession(String username);
}

