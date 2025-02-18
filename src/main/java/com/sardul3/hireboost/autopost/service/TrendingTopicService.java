package com.sardul3.hireboost.autopost.service;

import com.sardul3.hireboost.autopost.model.TrendingTopic;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public sealed interface TrendingTopicService permits BaseExtractor {
    CompletableFuture<List<TrendingTopic>> fetchTrendingTopics();
}


