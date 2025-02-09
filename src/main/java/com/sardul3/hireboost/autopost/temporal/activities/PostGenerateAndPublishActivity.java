package com.sardul3.hireboost.autopost.temporal.activities;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface PostGenerateAndPublishActivity {
    @ActivityMethod
    String curatePost();

    @ActivityMethod
    void publishPostToLinkedIn(String post);
}
