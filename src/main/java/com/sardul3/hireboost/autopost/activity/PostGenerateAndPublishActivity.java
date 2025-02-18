package com.sardul3.hireboost.autopost.activity;

import com.sardul3.hireboost.autopost.model.TrendingTopic;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@ActivityInterface
public interface PostGenerateAndPublishActivity {

    @ActivityMethod
    List<TrendingTopic> getTrendingTopics();

    @ActivityMethod
    String curatePost(String topic);

    @ActivityMethod
    void publishPostToLinkedIn(String post);
}
