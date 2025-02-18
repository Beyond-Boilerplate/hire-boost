package com.sardul3.hireboost.autopost.activity;

import com.sardul3.hireboost.autopost.model.TrendingTopic;
import com.sardul3.hireboost.autopost.service.LinkedInPostGenerator;
import com.sardul3.hireboost.autopost.service.LinkedInService;
import com.sardul3.hireboost.autopost.service.TrendingTopicsAggregator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class LinkedInPostPublishActivity implements PostGenerateAndPublishActivity {

    private final LinkedInService linkedInService;
    private final TrendingTopicsAggregator trendingTopicsAggregator;
    private final LinkedInPostGenerator linkedInPostGenerator;
    @Override
    public List<TrendingTopic> getTrendingTopics() {
        return trendingTopicsAggregator.getAllTrendingTopics();
    }

    @Override
    public String curatePost(String topic) {
        return this.linkedInPostGenerator.generateLinkedInPost(topic);
    }

    @Override
    public void publishPostToLinkedIn(String post) {
        this.linkedInService.postToLinkedIn(post);
    }
}
