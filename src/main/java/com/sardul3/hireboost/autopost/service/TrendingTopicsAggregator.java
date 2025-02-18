package com.sardul3.hireboost.autopost.service;

import com.sardul3.hireboost.autopost.model.TrendingTopic;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class TrendingTopicsAggregator {
    private final List<TrendingTopicService> services;

    public TrendingTopicsAggregator(List<TrendingTopicService> services) {
        this.services = services;
    }

    public List<TrendingTopic> getAllTrendingTopics() {
        List<CompletableFuture<List<TrendingTopic>>> futures = services.stream()
                .map(TrendingTopicService::fetchTrendingTopics)
                .toList();

        return futures.stream()
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .toList();
    }
}
