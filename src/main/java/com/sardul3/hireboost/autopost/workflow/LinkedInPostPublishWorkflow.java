package com.sardul3.hireboost.autopost.workflow;

import com.sardul3.hireboost.autopost.activity.PostGenerateAndPublishActivity;
import com.sardul3.hireboost.autopost.model.TrendingTopic;
import io.temporal.activity.ActivityOptions;
import io.temporal.workflow.Workflow;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LinkedInPostPublishWorkflow implements PostGenerateAndPublishWorkflow {
    private final PostGenerateAndPublishActivity activities = Workflow.newActivityStub(
            PostGenerateAndPublishActivity.class,
            ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofMinutes(5))
                    .build()
    );

    @Override
    public String generateAndPost() {
        List<TrendingTopic> topicList = activities.getTrendingTopics();
        return activities.curatePost(topicList.get(0).title());
    }
}
