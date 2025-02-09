package com.sardul3.hireboost.autopost.temporal.activities;

import com.sardul3.hireboost.autopost.service.AIContentService;
import com.sardul3.hireboost.autopost.service.LinkedInService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class LinkedInPostPublishActivity implements PostGenerateAndPublishActivity{

    private final AIContentService aiContentService;
    private final LinkedInService linkedInService;

    @Override
    public String curatePost() {
        return this.aiContentService.generateLinkedInPost();
    }

    @Override
    public void publishPostToLinkedIn(String post) {
        this.linkedInService.postToLinkedIn(post);
    }
}
